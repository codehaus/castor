/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.jdo.engine;


import java.util.Vector;
import java.util.Stack;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.persist.DuplicateIdentityExceptionImpl;
import org.exolab.castor.persist.PersistenceExceptionImpl;
import org.exolab.castor.persist.ObjectNotFoundExceptionImpl;
import org.exolab.castor.persist.ObjectDeletedExceptionImpl;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.TypeConvertor;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.loader.FieldHandlerImpl;
import org.exolab.castor.mapping.loader.FieldDescriptorImpl;
import org.exolab.castor.persist.ClassMolder;
import org.exolab.castor.persist.ArrayVector;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.mapping.loader.ClassDescriptorImpl;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.spi.LogInterceptor;
import org.exolab.castor.util.Logger;
import org.exolab.castor.util.Messages;
import org.exolab.castor.mapping.loader.RelationDescriptor;
import org.exolab.castor.persist.OID;


// ToDo (written on Aug 6, 2000)
// -- JDODescription / MappingLoader for getIdenties
// -- so better id, id.length
// -- verify wherePK under addLoadSql etc...
// -- query, store, delete ALL need new PK
// -- QueryResult (blindly changed getIdentity to getIdentities)
// -- create dependent object, tx need an special method for that


/**
 * The SQL engine performs persistence of one object type against one
 * SQL database. It can only persist simple objects and extended
 * relationships. An SQL engine is created for each object type
 * represented by a database. When persisting, it requires a physical
 * connection that maps to the SQL database and the transaction
 * running on that database, through the {@link ConnectionProvider}
 * interface.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class SQLEngine implements Persistence {

    private final static boolean ID_TYPE = true;


    private final static boolean FIELD_TYPE = false;


    private String              _pkLookup;


    private String              _sqlCreate;


    private String              _sqlRemove;


    private String              _sqlStore;


    private String              _sqlStoreDirty;


    private String              _sqlLoad;


    private String              _sqlLoadLock;


    private FieldInfo[]         _fields;


    private ColumnInfo[]         _ids;


    private ColumnInfo[]        _dependedIds;


    private SQLEngine           _extends;


    private QueryExpression     _sqlFinder;


    private PersistenceFactory  _factory;


    private String              _stampField;


    private String              _type;


    private String              _mapTo;


    private String              _extTable;


    private LogInterceptor       _logInterceptor;


    private JDOClassDescriptor   _clsDesc;


    private KeyGenerator         _keyGen;


    private ClassMolder          _mold;




    SQLEngine( JDOClassDescriptor clsDesc,
               LogInterceptor logInterceptor, PersistenceFactory factory, String stampField )
        throws MappingException {

        _clsDesc = clsDesc;
        _stampField = stampField;
        _factory = factory;
        _logInterceptor = logInterceptor;
        _keyGen = null;
        _type = clsDesc.getJavaClass().getName();
        _mapTo = clsDesc.getTableName();

        if ( _clsDesc.getExtends() == null ) {
            KeyGeneratorDescriptor keyGenDesc = clsDesc.getKeyGeneratorDescriptor();
            if ( keyGenDesc != null ) {
                _keyGen = keyGenDesc.getKeyGeneratorRegistry().getKeyGenerator(
                        _factory, keyGenDesc,
                        ( (JDOFieldDescriptor) _clsDesc.getIdentity() ).getSQLType(),
                        _logInterceptor );
            }
        }

        // construct field and id info
        Vector idsInfo = new Vector();
        Vector fieldsInfo = new Vector();

        /*
         * Implementation Note:
         * Extends and Depends has some special mutual exclusive
         * properties, which implementator should aware of.
         *
         * A Depended class may depends on another depended class
         * A class should either extends or depends on other class
         * A class should not depend on extending class.
         *  because, it is the same as depends on the base class
         * A class may be depended by zero or more classes
         * A class may be extended by zero or more classes
         * A class may extends only zero or one class
         * A class may depends only zero or one class
         * A class may depend on extended class
         * A class may extend a dependent class.
         * A class may extend a depended class.
         * No loop or circle should exist
         *
         * In other word,
         *
         */
        // then, we put depended class ids in the back
        JDOClassDescriptor base = clsDesc;

        // make sure there is no forbidded cases
        while ( base.getDepends() != null ) {
            if ( base.getExtends() != null )
                throw new MappingException("Class should not both depends on and extended other classes");

            base = (JDOClassDescriptor)base.getDepends();
            if ( base.getExtends() != null )
                throw new MappingException("Class should not depends on an extended class");
            // do we need to add loop detection?
        }

        // walk until the base class which this class extends
        base = clsDesc;
        Stack stack = new Stack();
        stack.push( base );
        while ( base.getExtends() != null ) {
            if ( base.getDepends() != null )
                throw new MappingException("Class should not both depends on and extended other classes");
            base = (JDOClassDescriptor) base.getExtends();
            stack.push( base );
            // do we need to add loop detection?
        }
        if ( base != clsDesc ) {
            _extTable = base.getTableName();
        }

        // now base is either the base of extended class, or
        // clsDesc
        // we always put the original id info in front
        JDOClassDescriptor jdoBase = (JDOClassDescriptor) base;
        FieldDescriptor[] idDescriptors = base.getIdentities();

        for ( int i=0; i<idDescriptors.length; i++ ) {
            if ( idDescriptors[i] instanceof JDOFieldDescriptor ) {
                String name = ((JDOFieldDescriptor)idDescriptors[i]).getSQLName();
                int type = ((JDOFieldDescriptor)idDescriptors[i]).getSQLType();
                FieldHandlerImpl fh = (FieldHandlerImpl)idDescriptors[i].getHandler();
                idsInfo.add( new ColumnInfo( name, type, fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam() ) );
            } else
                throw new MappingException("Except JDOFieldDescriptor");
        }

        // if class or base class depend on other class,
        // depended class ids will be part of this ids and
        // will be added in the back. We don't need to take
        // care depended class which is depends on other class.
        // ClassMolder will take care of it.
        /*
        idDescriptors = null;
        if ( clsDesc.getDepends()!= null ) {
            idDescriptors = ((ClassDescriptorImpl)jdoBase.getDepends()).getIdentities();
            for ( int i=0; i<idDescriptors.length; i++ ) {
                if ( idDescriptors[i] instanceof JDOFieldDescriptor ) {
                    String name = ((JDOFieldDescriptor)idDescriptors[i]).getSQLName();
                    int type = ((JDOFieldDescriptor)idDescriptors[i]).getSQLType();
                    FieldHandlerImpl fh = (FieldHandlerImpl)idDescriptors[i].getHandler();
                    idsInfo.add( new ColumnInfo( name, type, fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam() ) );
                } else
                    throw new MappingException("Except JDOFieldDescriptor");
            }
        } */

        // then do the fields
        boolean extendField = true;
        while ( !stack.empty() ) {
            base = (JDOClassDescriptor)stack.pop();
            FieldDescriptor[] fieldDescriptors = base.getFields();
            for ( int i=0; i<fieldDescriptors.length; i++ ) {
                if ( stack.empty() ) {
                    fieldsInfo.add( new FieldInfo( clsDesc, fieldDescriptors[i], clsDesc.getTableName(), !extendField ) );
                } else {
                    fieldsInfo.add( new FieldInfo( clsDesc, fieldDescriptors[i], base.getTableName(), extendField ) );
                }
            }
        }

        _ids = new ColumnInfo[idsInfo.size()];
        idsInfo.copyInto( _ids );

        _fields = new FieldInfo[fieldsInfo.size()];
        fieldsInfo.copyInto( _fields );

        try {
            buildSql();
            buildFinder( clsDesc );
        } catch ( QueryException except ) {
            except.printStackTrace();
            throw new MappingException( except );
        }
    }


    /**
     * Mutator method for setting extends SQLEngine
     */
    public void setExtends( SQLEngine engine ) {
        _extends = engine;
    }

    /**
     * Used by {@link OQLQuery} to retrieve the class descriptor.
     */
    public JDOClassDescriptor getDescriptor()
    {
        return _clsDesc;
    }


    /**
     * Used by ParseTreeWalker to quote names in WHERE clause
     */
    public String quoteName( String name )
    {
        return _factory.quoteName( name );
    }


    public PersistenceQuery createQuery( QueryExpression query, Class[] types )
        throws QueryException
    {
        String sql;

        sql = query.getStatement( _clsDesc.getAccessMode() == AccessMode.DbLocked);
        if ( _logInterceptor != null )
            _logInterceptor.queryStatement( sql );
        return new SQLQuery( this, sql, types );
    }


    public PersistenceQuery createCall( String spCall, Class[] types )
    {
        FieldDescriptor[] fields;   
        String[] jdoFields0;
        String[] jdoFields;
        int[] sqlTypes0;
        int[] sqlTypes;
        int count; 

        if ( _logInterceptor != null )
            _logInterceptor.queryStatement( spCall );

        fields = _clsDesc.getFields();
        jdoFields0 = new String[ fields.length + 1 ];
        sqlTypes0 = new int[ fields.length + 1 ];
        // the first field is the identity
        count = 1;
        jdoFields0[ 0 ] = _clsDesc.getIdentity().getFieldName();
        sqlTypes0[ 0 ] = ( (JDOFieldDescriptor) _clsDesc.getIdentity() ).getSQLType();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor ) {
                jdoFields0[ count ] = ((JDOFieldDescriptor) fields[ i ]).getSQLName();
                sqlTypes0[ count ] = ((JDOFieldDescriptor) fields[ i ]).getSQLType();
                ++count;
            }
        }
        jdoFields = new String[ count ];
        sqlTypes = new int[ count ];
        System.arraycopy( jdoFields0, 0, jdoFields, 0, count );
        System.arraycopy( sqlTypes0, 0, sqlTypes, 0, count );
        return ((BaseFactory) _factory).getCallQuery( spCall, types, _clsDesc.getJavaClass(), jdoFields, sqlTypes );
    }



    public QueryExpression getQueryExpression() {
        return _factory.getQueryExpression();
    }

    public QueryExpression getFinder()
    {
        return (QueryExpression) _sqlFinder.clone();
    }
    private Object[] idToSql( Object[] objects )
            throws PersistenceException {

        if ( objects == null ) return null;

        Object[] result = new Object[objects.length];
        for ( int i=0; i<_ids.length; i++ ) {
			try {
                if ( _ids[i].convertFrom != null && objects[i] != null ) {
                    result[i] = _ids[i].convertFrom.convert( objects[i], _ids[i].convertParam );
                } else {
                    result[i] = objects[i];
                }
	        } catch ( ClassCastException e ) {
		        throw new PersistenceException( "Wrong convertor: "+_ids[i].convertFrom
					+" for object type: "+(objects[i]==null?null:objects[i].getClass() ));
			}
         }             
		 return result;
    }
	private Object idToSQL( int index, Object object ) 
			throws PersistenceException {
		if ( object == null || _ids[index].convertFrom == null )
			return object;
		return _ids[index].convertFrom.convert( object, _ids[index].convertParam );
	}

    private Object toSQL( int field, int column, Object object ) 
            throws PersistenceException {
        ColumnInfo col = _fields[field].columns[column];
        if ( object == null || col.convertFrom == null )
            return object;
        return col.convertFrom.convert( object, col.convertParam );
    }
    /*
    private Object[] toSql( Object[] objects )
            throws PersistenceException {

        Object[] result = null;

        if ( objects == null ) return null;


            System.out.print("Before toSQL. object: ");
            if ( objects == null ) {
                System.out.println(" null ");
            } else {
                System.out.println(" not null");
                for ( int i=0; i<objects.length; i++ ) {
                    if ( i > 0 ) System.out.print(";  ");
                    System.out.print("[");
                    if ( objects[i] == null ) 
                        System.out.print("null:");
                    else {
                        if ( _fields[i].multi ) {
                            System.out.print("multi");
                        } else {

                            Object[] temp = (Object[]) objects[i];
                            for ( int j=0; j<_fields[i].columns.length; j++ ) {
                                if ( j > 0 ) System.out.print(",  ");
                                System.out.print( 
                                ((temp==null||temp[j]==null)?null:temp[j].getClass().getName()) + "/" +
                                 (temp==null?null:temp[j]));
                            }
                        }
                        
                    }
                    System.out.print("]");
                }
                System.out.println();
            }
        try {
            // using field convert
            result = new Object[objects.length];
            for ( int i=0; i<_fields.length; i++ ) {
                if ( objects[i] != null && _fields[i].store ) {
                    Object[] inner = (Object[]) objects[i];
                    Object[] temp = new Object[inner.length];
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                        if ( _fields[i].columns[j].convertFrom != null && inner[j] != null ) {
                            temp[j] = _fields[i].columns[j].convertFrom.convert(
                                    inner[j], _fields[i].columns[j].convertParam );
                        } else {
                            temp[j] = inner[j];
                        }
                    }
                    result[i] = temp;
                }
            }
            /*
            System.out.println("result:");
            for ( int i=0; i<objects.length; i++ ) {
                if ( i > 0 ) System.out.print(",");
                System.out.print( (objects[i]==null?null:objects[i].getClass()) + "/" + objects[i] + "    ");
            }*//*
            return result;
        } catch ( ClassCastException e ) {
            throw new PersistenceException( Messages.format( "mapping.wrongConvertor",  "" ) );
        }*/
    //}*/
    private Object[] idToJava( Object[] objects )
            throws PersistenceException {

        if ( objects == null ) return null;

        try {
            // using id convert
            Object[] result = new Object[objects.length];
            for ( int i=0; i<_ids.length; i++ ) {
                if ( _ids[i].convertTo != null && objects[i] != null ) {
                    result[i] = _ids[i].convertTo.convert( objects[i], _ids[i].convertParam );
                } else {
                    result[i] = objects[i];
                }
            }
            return result;
        } catch ( ClassCastException e ) {
            throw new PersistenceException( Messages.format( "mapping.wrongConvertor",  "" ) );
        }

    }
	private Object idToJava( int index, Object object ) 
			throws PersistenceException {
		if ( object == null || _ids[index].convertTo == null )
			return object;
		return _ids[index].convertTo.convert( object, _ids[index].convertParam );
	}

    private Object toJava( int field, int column, Object object ) 
            throws PersistenceException {
        ColumnInfo col = _fields[field].columns[column];
        if ( object == null || col.convertTo == null )
            return object;
        return col.convertTo.convert( object, col.convertParam );
    }
    /*
    private Object[] toJava( Object[] objects )
            throws PersistenceException {

        if ( objects == null ) return null;

        Object[] result = null;



            System.out.print("Before toJava. object: ");
            if ( objects == null ) {
                System.out.println(" null ");
            } else {
                System.out.println(" not null");
                for ( int i=0; i<objects.length; i++ ) {
                    if ( i > 0 ) System.out.print(";  ");
                    System.out.print("[");
                    if ( objects[i] == null ) 
                        System.out.print("null:");
                    else {
                        if ( _fields[i].multi ) {
                            System.out.print("multi");
                        } else {

                            Object[] temp = (Object[]) objects[i];
                            for ( int j=0; j<_fields[i].columns.length; j++ ) {
                                if ( j > 0 ) System.out.print(",  ");
                                System.out.print( 
                                ((temp==null||temp[j]==null)?null:temp[j].getClass().getName()) + "/" +
                                 (temp==null?null:temp[j]));
                            }
                        }
                        
                    }
                    System.out.print("]");
                }
                System.out.println();
            }


        try {
            // using field convert
            result = new Object[objects.length];
            for ( int i=0; i<_fields.length; i++ ) {
                if ( objects[i] != null && _fields[i].load  ) {
                    if ( !_fields[i].multi ) {
                        Object[] inner = (Object[]) objects[i];
                        Object[] temp =  new Object[inner.length];

                        for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            if ( inner[j] != null && _fields[i].columns[j].convertTo != null ) {
                                
                                temp[j] = _fields[i].columns[j].convertTo.convert( inner[j],
                                        _fields[i].columns[j].convertParam );
                            } else {
                                
                                temp[j] = inner[j];
                            }
                        }
                        result[i] = temp;
                    } else {
                        Vector vec = (Vector) objects[i];
                        Vector res = new Vector( vec.size() );
                        for ( int k=0; k<vec.size(); k++ ) {
                            Object[] inner = (Object[]) vec.elementAt(k);
                            Object[] temp = new Object[inner.length];
                            for ( int j=0; j<_fields[i].columns.length; j++ ) {
                                if ( inner[j] != null && _fields[i].columns[j].convertTo != null ) {
                                    temp[j] = _fields[i].columns[j].convertTo.convert( inner[j],
                                            _fields[i].columns[j].convertParam );
                                } else {
                                    temp[j] = inner[j];
                                }
                            }
                            res.addElement(temp);
                        }
                        result[i] = res;
                    }
                }
            }


            System.out.print("Result of toJava. object: ");
            if ( result == null ) {
                System.out.println(" null ");
            } else {
                System.out.println(" not null");
                for ( int i=0; i<result.length; i++ ) {
                    if ( i > 0 ) System.out.print(";  ");
                    System.out.print("[");
                    if ( result[i] == null ) 
                        System.out.print("null:");
                    else {
                        if ( _fields[i].multi ) {
                            System.out.print("multi");
                        } else {

                            Object[] temp = (Object[]) objects[i];
                            for ( int j=0; j<_fields[i].columns.length; j++ ) {
                                if ( j > 0 ) System.out.print(",  ");
                                System.out.print( 
                                ((temp==null||temp[j]==null)?null:temp[j].getClass().getName()) + "/" +
                                 (temp==null?null:temp[j]));
                            }
                        }                        
                    }
                    System.out.print("]");
                }
                System.out.println();
            }

            return result;
        } catch ( ClassCastException e ) {
            throw new PersistenceException( Messages.format( "mapping.wrongConvertor",  "" ) );
        }
    }*/

    private Object generateKey( Object conn ) throws PersistenceException
    {
        Object identity;

        identity = _keyGen.generateKey( (Connection) conn, _clsDesc.getTableName(),
                _ids[0].name, null );

        if ( identity == null )
            throw new PersistenceExceptionImpl( "persist.noIdentity" );

        if ( _ids[0].convertTo != null && _ids[0].convertTo != null ) {
            identity = _ids[0].convertTo.convert( identity, _ids[0].convertParam );
        }

        return identity;
    }


    public Object[] create( Object conn, Object[] fields, Object[] identities )
        throws DuplicateIdentityException, PersistenceException
    {
        PreparedStatement stmt;
        int               count;
        //Object[]          convertedFields;
        Object[]          resultIds = null;
        Object            tempId;

        //convertedFields = toSql( fields );

        stmt = null;
        try {
            // Must create record in parent table first.
            // All other dependents are created afterwards.
            if ( _extends != null )
                identities = _extends.create( conn, fields, identities );

            if ( _keyGen == null && identities == null )
                throw new PersistenceExceptionImpl( "persist.noIdentity" );

			if ( identities != null ) {
				resultIds = new Object[identities.length];
				System.arraycopy( identities, 0, resultIds, 0, identities.length );
			}

            // Generate key before INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                resultIds[0] = generateKey( conn );   // genKey return identity in JDO type
            }

            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.DURING_INSERT )
                stmt = ( (Connection) conn ).prepareCall( _sqlCreate );
            else
                stmt = ( (Connection) conn ).prepareStatement( _sqlCreate );

            System.out.println( _sqlCreate );
            // Must remember that SQL column index is base one
            count = 1;
            if ( _keyGen == null || _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                for ( int i=0; i<_ids.length; i++ ) {
                    System.out.print("[id"+count+":"+resultIds[i]+"    "+_ids[i].name+" ]  ");
                    stmt.setObject( count, idToSQL( i, resultIds[i] ) );
                    ++count;
                }
            }

            for ( int i = 0 ; i < _fields.length ; ++i ) {
                if ( _fields[ i ].store ) {
                    Object[] inner = (Object[])fields[i];
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                        if ( inner == null || inner[j] == null ) {
                            System.out.print("["+count+":null:"+_fields[i].columns[j].name+" type: "
                            +_fields[i].columns[j].sqlType+"    "+_fields[i].jdoName+"]  ");
                            stmt.setNull( count, _fields[i].columns[j].sqlType );
                        } else {
                            System.out.print("["+count+":"+inner[j]+":"
                                +_fields[i].columns[j].name+" type: "+_fields[i].columns[j].sqlType+"    "+_fields[i].jdoName+"]  ");
                            stmt.setObject( count, toSQL( i, j, inner[j]), _fields[i].columns[j].sqlType );
                        }
                        ++count;
                    }
                }
            }
            System.out.println();

            // Generate key during INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.DURING_INSERT ) {
                CallableStatement cstmt = (CallableStatement) stmt;
                int sqlType;

                sqlType = _ids[0].sqlType;
                cstmt.registerOutParameter( count, sqlType );
                cstmt.execute();

                // First skip all results "for maximum portability"
                // as proposed in CallableStatement javadocs.
                while ( cstmt.getMoreResults() || cstmt.getUpdateCount() != -1 );

                // Identity is returned in the last parameter
                // Workaround: for INTEGER type in Oracle getObject returns BigDecimal
                if ( sqlType == java.sql.Types.INTEGER )
                    tempId = new Integer( cstmt.getInt( count ) );
                else
                    tempId = cstmt.getObject( count );

				resultIds[0] = idToJava( 0, tempId );
            } else
                stmt.executeUpdate();

            stmt.close();

            // Generate key after INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.AFTER_INSERT ) {
                tempId = generateKey( conn );
                resultIds[0] = tempId;
            }

            return resultIds;

        } catch ( SQLException except ) {
            // [oleg] Check for duplicate key based on X/Open error code
            // Bad way: all validation exceptions are reported as DuplicateKey
            //if ( except.getSQLState() != null &&
            //     except.getSQLState().startsWith( "23" ) )
            //    throw new DuplicateIdentityExceptionImpl( _clsDesc.getJavaClass(), identity );

            // Good way: let PersistenceFactory try to determine
            Boolean isDupKey;

            isDupKey = _factory.isDuplicateKeyException( except );
            if ( Boolean.TRUE.equals( isDupKey ) ) {
                throw new DuplicateIdentityExceptionImpl( _clsDesc.getJavaClass(), OID.flatten(identities) );
            } else if ( Boolean.FALSE.equals( isDupKey ) ) {
                throw new PersistenceExceptionImpl( except );
            }
            // else unknown, let's check directly.

            // [oleg] Check for duplicate key the old fashioned way,
            //        after the INSERT failed to prevent race conditions
            //        and optimize INSERT times
            try {
                // Close the insert statement
                if ( stmt != null )
                    stmt.close();

                stmt = ( (Connection) conn ).prepareStatement( _pkLookup );
                for ( int i=0; i<identities.length; i++ ) {
                    stmt.setObject( 1, identities[i] );
                }
                if ( stmt.executeQuery().next() ) {
                    stmt.close();
                    throw new DuplicateIdentityExceptionImpl( _clsDesc.getJavaClass(), OID.flatten(identities) );
                }
            } catch ( SQLException except2 ) {
                // Error at the stage indicates it wasn't a duplicate
                // primary key problem. But best if the INSERT error is
                // reported, not the SELECT error.
            }

            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceExceptionImpl( except );
        }
    }


    public Object store( Object conn, Object[] fields, Object[] identities,
                         Object[] original, Object stamp )
        throws ObjectModifiedException, ObjectDeletedException, PersistenceException {
        PreparedStatement stmt = null;
        int               count;

        //Object[]        convertedFields;
        //Object[]        convertedOriginal = null;
        Object[]          sqlIdentities;

        //convertedFields = toSql( fields );

        //convertedOriginal = toSql( original );

        sqlIdentities = idToSql( identities );

        try {
            // Must store record in parent table first.
            // All other dependents are stored independently.
            if ( _extends != null )
                _extends.store( conn, fields, identities, original, stamp );

            stmt = ( (Connection) conn ).prepareStatement( original == null ? _sqlStore : _sqlStoreDirty );
            System.out.println(original == null ? _sqlStore : _sqlStoreDirty);

            count = 1;
            for ( int i = 0 ; i < _fields.length ; ++i ) {
                if ( _fields[ i ].store ) {
                    Object[] inner = (Object[])fields[i];
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                        if ( inner == null || inner[j] == null ) {
                            System.out.print("["+count+":null:"+_fields[i].columns[j].name+" type: "
                            +_fields[i].columns[j].sqlType+"]");
                            stmt.setNull( count, _fields[i].columns[j].sqlType );
                        } else {
                            System.out.print("["+count+":"+inner[j]+":"
                                +_fields[i].columns[j].name+" type: "+_fields[i].columns[j].sqlType+"]");
                            stmt.setObject( count, toSQL( i, j, inner[j]), _fields[i].columns[j].sqlType );
                        }
                        ++count;
                    }
                }
            }
            System.out.println("    new field to update (SQLEngine)");

            for ( int i=0; i< sqlIdentities.length; i++ ) {
                System.out.print("<<id#"+_ids[i].name+":"+count+":"+sqlIdentities[i]+":type "+_ids[ i ].sqlType+">>");
                stmt.setObject( count, sqlIdentities[i], _ids[i].sqlType );
                count++;
            }

            if ( original != null ) {
                for ( int i = 0 ; i < _fields.length ; ++i ) {
                    if ( _fields[ i ].store && _fields[i].dirtyCheck ) {
                        Object[] inner = (Object[])original[i];
                        for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            if ( inner == null || inner[j] == null ) {
                                System.out.print("["+count+":null:"+_fields[i].columns[j].name+" type: "
                                +_fields[i].columns[j].sqlType+"]");
                                stmt.setNull( count, _fields[i].columns[j].sqlType );
                            } else {
                                System.out.print("["+count+":"+inner[j]+":"
                                    +_fields[i].columns[j].name+" type: "+_fields[i].columns[j].sqlType+"]");
                                stmt.setObject( count, toSQL( i, j, inner[j]), _fields[i].columns[j].sqlType );
                            }
                            ++count;
                        }
                    }
                }
            }
            System.out.println("    old field to compare (SQLEngine)");

            if ( stmt.executeUpdate() == 0 ) {
                // If no update was performed, the object has been previously
                // removed from persistent storage or has been modified if
                // dirty checking. Determine which is which.
                stmt.close();
                if ( original != null ) {
                    stmt = ( (Connection) conn ).prepareStatement( /*_pkLookup*/_sqlLoad );
                    for ( int i=0; i<sqlIdentities.length; i++ ) {
                        stmt.setObject( 1+i, sqlIdentities[i] );
                    }
                    ResultSet res = stmt.executeQuery();
                    int c = res.getMetaData().getColumnCount();
                    if ( res.next() ) {
                        System.out.println("A row with same id found! column count: "+c);

                        for ( int i=0; i<c; i++ ) {
                            if ( i > 0 ) System.out.print(", ");
                            System.out.print(res.getString(i+1));
                        }
                        stmt.close();
                        System.out.println("");
                        throw new ObjectModifiedException( Messages.format( "persist.objectModified", _clsDesc.getJavaClass().getName(), OID.flatten(identities) ) );
                    }
                    stmt.close();
                }

                throw new ObjectDeletedExceptionImpl( _clsDesc.getJavaClass(), OID.flatten( identities ) );
            }
            stmt.close();
            return null;
        } catch ( SQLException except ) {
            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceExceptionImpl( except );
        }
    }


    public void delete( Object conn, Object[] identities )
        throws PersistenceException
    {
        PreparedStatement stmt = null;

        Object[] sqlIdentities = idToSql( identities );

        try {
            stmt = ( (Connection) conn ).prepareStatement( _sqlRemove );
            for ( int i=0; i<sqlIdentities.length; i++ ) {
                stmt.setObject( 1+i, sqlIdentities[i] );
            }

            System.out.println("Row is being deleted from table of: "+_mapTo+" id: "+OID.flatten( identities ));
            int result = stmt.executeUpdate();
            if ( result < 1 )
                throw new PersistenceException("Object to be deleted does not exist! "+OID.flatten( identities ));

            stmt.close();

            // Must delete record in parent table last.
            // All other dependents have been deleted before.
            if ( _extends != null )
                _extends.delete( conn, identities );
        } catch ( SQLException except ) {
            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceExceptionImpl( except );
        }
    }


    public void writeLock( Object conn, Object[] identities )
        throws ObjectDeletedException, PersistenceException
    {
        PreparedStatement stmt = null;

        Object[] sqlIdentities = idToSql( identities );

        try {
            // Must obtain lock on record in parent table first.
            if ( _extends != null )
                _extends.writeLock( conn, identities );

            stmt = ( (Connection) conn ).prepareStatement( _pkLookup );
            for ( int i=0; i<identities.length; i++ ) {
                stmt.setObject( 1+i, identities[i] );
            }
            // If no query was performed, the object has been previously
            // removed from persistent storage. Complain about this.
            if ( ! stmt.executeQuery().next() )
                throw new ObjectDeletedExceptionImpl( _clsDesc.getJavaClass(), OID.flatten( identities ) );
            stmt.close();
        } catch ( SQLException except ) {
            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceExceptionImpl( except );
        }
    }


    public Object load( Object conn, Object[] fields, Object[] identities, AccessMode accessMode )
            throws ObjectNotFoundException, PersistenceException {

        PreparedStatement stmt;
        ResultSet         rs;
        Object            stamp = null;
        Object[]          sqlIdentities;
        //Object[]          sqlFields;
        //Object[]          tempFields;
        int               count;
        boolean           notNull;

        try {
            sqlIdentities = idToSql( identities );
            stmt = ( (Connection) conn ).prepareStatement( ( accessMode == AccessMode.DbLocked ) ? _sqlLoadLock : _sqlLoad );
            System.out.println(( accessMode == AccessMode.DbLocked ) ? _sqlLoadLock : _sqlLoad);

            for ( int i=0; i<_ids.length; i++ ) {
                System.out.println("set id");
                stmt.setObject( i+1, sqlIdentities[i], _ids[i].sqlType );
            }

            //sqlFields = new Object[fields.length];

            rs = stmt.executeQuery();
            if ( ! rs.next() )
                throw new ObjectNotFoundExceptionImpl( _clsDesc.getJavaClass(), OID.flatten( identities ) );
            // Load all the fields of the object including one-one relations

            count = 1;
            for ( int i = 0 ; i < _fields.length ; ++i  ) {
                if ( !_fields[i].load )
                    continue;

                if ( !_fields[i].multi ) {
                    notNull = false;
                    Object[] inner = (Object[]) fields[i];
                    Object[] temp = new Object[_fields[i].columns.length];
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                        Object value = toJava( i, j, SQLTypes.getObject( rs, count, _fields[i].columns[j].sqlType ) );
                        if ( !rs.wasNull() ) {
                            temp[j] = value;
                            notNull = true;
                        } 
                        count++;
                    }
                    fields[i] = temp;
                } else {
                    ArrayVector res = new ArrayVector();
                    Object[] temp = new Object[_fields[i].columns.length];
                    notNull = false;
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                        Object value = toJava( i, j, SQLTypes.getObject( rs, count, _fields[i].columns[j].sqlType ) );
                        if ( !rs.wasNull() ) {
                            temp[j] = value;
                            notNull = true;
                        }
                        count++;
                    }
                    if ( notNull ) res.add(temp);
                    fields[i] = res;
                }
            }

            while ( rs.next() ) {
                count = 1;
                for ( int i = 0; i < _fields.length ; ++i  ) {
                    if ( !_fields[i].load )
                        continue;

                    if ( _fields[i].multi ) {
                        ArrayVector res = (ArrayVector)fields[i];
                        Object[] temp = new Object[_fields[i].columns.length];
                        notNull = false;
                        for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            Object value = toJava( i, j, SQLTypes.getObject( rs, count, _fields[i].columns[j].sqlType ) );
                            if ( !rs.wasNull() ) {
                                temp[j] = value;
                                notNull = true;
                            }
                            count++;
                        }
                        if ( notNull || !res.contains( temp ) ) res.add(temp);
                    } else {
                        count += _fields[i].columns.length;
                    }
                }
            }
            rs.close();
            stmt.close();

        } catch ( SQLException except ) {
            throw new PersistenceExceptionImpl( except );
        }
        return stamp;
    }

    private static String[] breakApart( String strings, char delimit ) {
        if ( strings == null )
            return null;

        Vector v = new Vector();
        int start = 0;
        int count = 0;
        while ( count < strings.length() ) {
            if ( strings.charAt( count ) == delimit ) {
                if ( start < (count - 1) ) {
                    v.add( strings.substring( start, count ) );
                    count++;
                    start = count;
                    continue;
                }
            }
            count++;
        }
        if ( start < (count - 1) ) {
            v.add( strings.substring( start, count ) );
        }

        String[] result = new String[v.size()];
        v.copyInto( result );
        return result;
    }

    private void buildSql() throws QueryException {

        StringBuffer         sql;
        JDOFieldDescriptor[] jdoFields;
        FieldDescriptor[]    fields;
        int                  count;
        QueryExpression      query;
        String               wherePK;
        String               primKeyName;
        String               tableName;
        boolean              keyGened = false;


        tableName = _mapTo;
        query = _factory.getQueryExpression();

        // initalize lookup query
        for ( int i=0; i<_ids.length; i++ ) {
            query.addParameter( tableName, _ids[i].name, QueryExpression.OpEquals );
        }
        _pkLookup = query.getStatement( true );

        // create sql statements
        StringBuffer sb = new StringBuffer();
        sb.append( JDBCSyntax.Where );
        for ( int i=0; i<_ids.length; i++ ) {
            if ( i > 0 ) sb.append( " AND " );
            sb.append( _factory.quoteName( _ids[i].name ) );
            sb.append( QueryExpression.OpEquals );
            sb.append( JDBCSyntax.Parameter );
        }
        wherePK = sb.toString();

        // Create statement to insert a new row into the table
        // using the specified primary key if one is required
        sql = new StringBuffer( "INSERT INTO " );
        sql.append( _factory.quoteName( tableName ) ).append( " (" );
        count = 0;
        for ( int i=0; i<_ids.length; i++ ) {
            if ( _keyGen == null || _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                if ( count > 0 ) sql.append( ',' );
                keyGened = true;
                sql.append( _factory.quoteName( _ids[i].name ) );
                ++count;
            }
        }
        for ( int i = 0 ; i < _fields.length; ++i ) {
            if ( _fields[i].store ) {
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    if ( count > 0 )
                        sql.append( ',' );
                    sql.append( _factory.quoteName( _fields[i].columns[j].name ) );
                    ++count;
                }
            }
        }
        sql.append( ") VALUES (" );
        for ( int i = 0 ; i < count; ++i ) {
            if ( i > 0 )
                sql.append( ',' );
            sql.append( '?' );
        }
        sql.append( ')' );
        _sqlCreate = sql.toString();

        if ( keyGened ) {
            //try {
            //    _sqlCreate = _keyGen.patchSQL( _sqlCreate, _ids[0].name /*primKeyName*/ );
            //} catch ( MappingException except )  {
            //    Logger.getSystemLogger().println( except.toString() );

                // proceed without this stupid key generator
            //    _keyGen = null;
            //    buildSql( clsDesc, logInterceptor );
            //    return;
            //}
            //if ( _keyGen.getStyle() == KeyGenerator.DURING_INSERT )
            //    _sqlCreate = "{call " + _sqlCreate + "}";
        }
        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for creating " + _type + ": " + _sqlCreate );


        sql = new StringBuffer( "DELETE FROM " ).append( _factory.quoteName( tableName ) );
        sql.append( wherePK );
        _sqlRemove = sql.toString();
        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for deleting " + _type + ": " + _sqlRemove );

        sql = new StringBuffer( "UPDATE " );
        sql.append( _factory.quoteName( _mapTo ) );
        sql.append( " SET " );
        count = 0;
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ].store ) {
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    if ( count > 0 )
                        sql.append( ',' );
                    sql.append( _factory.quoteName( _fields[i].columns[j].name ) );
                    sql.append( "=?" );
                    ++count;
                }
            }
        }
        sql.append( wherePK );
        _sqlStore = sql.toString();

        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[i].store && _fields[i].dirtyCheck ) {
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    sql.append( " AND " );
                    sql.append( _factory.quoteName( _fields[i].columns[j].name ) );
                    sql.append( "=?" );
                }
            }
        }
        _sqlStoreDirty = sql.toString();
        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for updating " + _type + ": " + _sqlStoreDirty );
    }


    private void buildFinder( JDOClassDescriptor clsDesc ) throws MappingException, QueryException {
        Vector          fields;
        QueryExpression expr;
        QueryExpression find;

        fields = new Vector();
        expr = _factory.getQueryExpression();
        find = _factory.getQueryExpression();
        //addLoadSql( expr, fields, false, true, true );

        //_fields = new FieldInfo[ fields.size() ];
        //fields.copyInto( _fields );

        // get id columns' names
        String[] idnames = new String[_ids.length];
        for ( int i=0; i<_ids.length; i++ ) {
            idnames[i] = _ids[i].name;
            expr.addParameter( _mapTo, _ids[i].name, QueryExpression.OpEquals );
        }

        // join all the extended table
        JDOClassDescriptor base = clsDesc;
        while ( base.getExtends() != null ) {
            base = (JDOClassDescriptor)base.getExtends();
            expr.addInnerJoin( _mapTo, idnames, base.getTableName(), idnames );
            find.addInnerJoin( _mapTo, idnames, base.getTableName(), idnames );
        }
        for ( int i=0; i<_ids.length; i++ ) {
            find.addColumn( _mapTo, idnames[i] );
        }

        // join all the related/depended table
        Vector joinTables = new Vector();
        for ( int i=0; i<_fields.length; i++ ) {
            if ( _fields[i].load ) {
                if ( _fields[i].joined /*&& !joinTables.contains( _fields[i].tableName )*/ ) {
                    System.out.println("join field: "+_fields[i].jdoName);
                    System.out.println("current table: "+_mapTo+" other table: "+_fields[i].tableName);
                    int offset = 0;
                    //clsDesc.getDepends()!=null? clsDesc.getIdentities().length : 0;
                    String[] rightCol = _fields[i].joinFields;
                    String[] leftCol = new String[_ids.length-offset];
                    for ( int j=0; j<leftCol.length; j++ ) {
                        leftCol[j] = _ids[j+offset].name;
                        System.out.println("this id: "+leftCol);
                        System.out.println("other table id: "+rightCol[j]);
                    }
                    expr.addOuterJoin( _mapTo, leftCol, _fields[i].tableName, rightCol );
                    find.addOuterJoin( _mapTo, leftCol, _fields[i].tableName, rightCol );
                    joinTables.add( _fields[i].tableName );
                }
    
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    expr.addColumn( _fields[i].tableName, _fields[i].columns[j].name );
                    find.addColumn( _fields[i].tableName, _fields[i].columns[j].name );
                }
            }
        } 
        _sqlLoad = expr.getStatement( false );
        _sqlLoadLock = expr.getStatement( true );

        _sqlFinder = find;

        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for loading " + _type + ":  " + _sqlLoad );

    }


    private void addLoadSql( QueryExpression expr, Vector allFields,
                             boolean loadPk, boolean queryPk, boolean store )
        throws MappingException
    {
        FieldDescriptor[]    fields;
        JDOClassDescriptor   extend;
        FieldDescriptor      identity;
        String               identitySQL;

        //identity = clsDesc.getIdentity();
        //identitySQL = ( (JDOFieldDescriptor) identity ).getSQLName();

        // need depend......

        // If this class extends another class, create a join with the parent table and
        // add the load fields of the parent class (but not the store fields)
        /*
        if ( clsDesc.getExtends() != null ) {
            expr.addInnerJoin( clsDesc.getTableName(), identitySQL,
                    ((JDOClassDescriptor)clsDesc.getExtends()).getTableName(), identitySQL );
            addLoadSql( (JDOClassDescriptor) clsDesc.getExtends(), expr, allFields,
            true, queryPk, false );
            loadPk = false;
            queryPk = false;
        }*/

        /*
        if ( loadPk )
            expr.addColumn( clsDesc.getTableName(), identitySQL );
        if ( queryPk )
            expr.addParameter( clsDesc.getTableName(), identitySQL, QueryExpression.OpEquals );
   

        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor ) {
                // Basically, meaning that FieldMapping have <sql> as sub-element
                ClassDescriptor relDesc = fields[i].getClassDescriptor();

                if ( ((JDOFieldDescriptor)fields[i]).getManyTable() == null ) {
                    // Field is primitive (or one-to-many.
                    expr.addColumn( clsDesc.getTableName(),
                                    ( (JDOFieldDescriptor) fields[ i ] ).getSQLName() );

                    if ( relDesc == null )
                        allFields.addElement( new FieldInfo( (JDOFieldDescriptor)fields[i], store ) );
                    else
                        allFields.addElement( new FieldInfo( (JDOFieldDescriptor)fields[i], store,
                            ( (JDOFieldDescriptor) relDesc.getIdentity() ).getSQLType() ) );

                } else {
                    // Field is many-to-many
                    String manyTable = ((JDOFieldDescriptor)fields[i]).getManyTable();
                    String idSQL = null;
                    if ( clsDesc instanceof JDOClassDescriptor ) {
                        FieldDescriptor[] idFields = ((JDOClassDescriptor)clsDesc).getIdentities();
                        if ( idFields != null && idFields.length == 1
                                && (idFields[0] instanceof JDOFieldDescriptor) ) {
                            idSQL = ((JDOFieldDescriptor)idFields[0]).getSQLName();
                        } else {
                            throw new MappingException("Many-to-Many relation only support single column pk");
                        }
                    }
                    String relatedIdSQL = null;
                    if ( relDesc instanceof JDOClassDescriptor ) {
                        FieldDescriptor[] relatedIds = ((JDOClassDescriptor)relDesc).getIdentities();
                        if ( relatedIds != null && relatedIds.length == 1
                                && (relatedIds[0] instanceof JDOFieldDescriptor) ) {
                            relatedIdSQL = ((JDOFieldDescriptor)relatedIds[0]).getSQLName();
                        } else {
                            throw new MappingException("Many-to-Many relation only support single column pk");
                        }
                    }
                    expr.addColumn( manyTable, relatedIdSQL );
                    //System.out.println("idSQL: "+idSQL+" relatedIdSQL: "+relatedIdSQL);
                    expr.addOuterJoin( clsDesc.getTableName(), idSQL, manyTable, idSQL );
                    allFields.addElement( new FieldInfo( (JDOFieldDescriptor)fields[i], store ) );

                }
            } else {
                JDOClassDescriptor relDesc;

                relDesc = (JDOClassDescriptor) fields[ i ].getClassDescriptor();
                if ( relDesc == null )
                    if ( fields[i] instanceof JDOClassDescriptor ) {
                        allFields.addElement( new FieldInfo( (JDOFieldDescriptor)fields[ i ], false ) );
                    } else {
                        allFields.addElement( new FieldInfo( fields[ i ], false ) );
                    }
                else {
                    FieldDescriptor[] relFields;

                    String            foreKey = null;

                    relFields = relDesc.getFields();
                    for ( int j = 0 ; j < relFields.length ; ++j ) {
                        if ( relFields[ j ] instanceof JDOFieldDescriptor &&
                             relFields[ j ].getClassDescriptor() == clsDesc ) {
                                foreKey = ( (JDOFieldDescriptor) relFields[ j ] ).getSQLName();
                                break;
                             }
                    }
                    if ( foreKey == null )
                        throw new MappingException( "mapping.noRelation", relDesc.getTableName(), fields[ i ] );
                    else {
                        expr.addColumn( relDesc.getTableName(), ( (JDOFieldDescriptor) relDesc.getIdentity() ).getSQLName() );
                        expr.addOuterJoin( clsDesc.getTableName(), ( (JDOFieldDescriptor) identity ).getSQLName(),
                                           relDesc.getTableName(), foreKey );
                        allFields.addElement( new FieldInfo( fields[ i ], false ) );
                    }
                }
            }
        } */
    }


    public String toString() {
        return _clsDesc.toString();
    }

    static final class FieldInfo {

        final String  tableName;

        final String  jdoName;

        final boolean load;

        final boolean store;

        final boolean multi;

        final boolean joined;

        final boolean dirtyCheck;

        final String[] joinFields;
        //!TY fix this. joinFields should be in FieldInfo, not ColumnInfo

        //final boolean foreign;

        ColumnInfo[] columns;

        FieldInfo( JDOClassDescriptor clsDesc, FieldDescriptor fieldDesc, String classTable, boolean ext )
                throws MappingException{

            // for readability
            final int FIELD_TYPE = 0;

            final int REF_TYPE = 1;

            final int REL_TABLE_TYPE = 2;

            int type;

            FieldDescriptor[] classids = clsDesc.getIdentities();
            ClassDescriptor related = fieldDesc.getClassDescriptor();
            if ( related != null && !( related instanceof JDOClassDescriptor ) )
                    throw new MappingException("Related class is not JDOClassDescriptor");

            if ( fieldDesc.getClassDescriptor() != null ) {
                // !(fieldDesc instanceof JDOFieldDescriptor) ) {
                // no <sql> tag, treated as foreign key field of
                // PersistenceCapable

                // determine the type of field 
                if ( !( fieldDesc instanceof JDOFieldDescriptor ) )
                    type = REF_TYPE;
                else if ( ((JDOFieldDescriptor)fieldDesc).getManyTable() != null )
                    type = REL_TABLE_TYPE;
                else if ( ((JDOFieldDescriptor)fieldDesc).getSQLName() != null )
                    type = FIELD_TYPE;
                else 
                    type = REF_TYPE;
                        
                // initalize the column names
                FieldDescriptor[] relids = ((JDOClassDescriptor)related).getIdentities();
                String[] names = null;
                if ( fieldDesc instanceof JDOFieldDescriptor )
                    names = breakApart( ((JDOFieldDescriptor)fieldDesc).getSQLName(), ' ' );
                String[] relnames = new String[relids.length];
                for ( int i=0; i<relids.length; i++ ) {
                    relnames[i] = ((JDOFieldDescriptor)relids[i]).getSQLName();
                    if ( relnames[i] == null )
                        throw new MappingException("Related class identities field does not contains sql information!");
                }
                String[] joins = null;
                if ( fieldDesc instanceof JDOFieldDescriptor ) 
                    joins = breakApart( ((JDOFieldDescriptor)fieldDesc).getManyKey(), ' ' );
                String[] classnames = new String[classids.length];
                for ( int i=0; i<classids.length; i++ ) {
                    classnames[i] = ((JDOFieldDescriptor)classids[i]).getSQLName();
                    if ( classnames[i] == null )
                        throw new MappingException("Related class identities field does not contains sql information!");
                }

                // basic check of column names
                if ( names != null && names.length != relids.length )
                    throw new MappingException("The number of column of foreign keys doesn't not match with what specified in manyKey");                
                if ( joins != null && joins.length != classids.length )
                    throw new MappingException("The number of column of foreign keys doesn't not match with what specified in manyKey");

                // initalize the class
                switch (type) {
                case FIELD_TYPE:
                    this.tableName = ((JDOClassDescriptor)clsDesc).getTableName();;
                    this.jdoName = fieldDesc.getFieldName();
                    this.load = true;
                    this.store = !ext;
                    this.multi = false;
                    this.joined = false;
                    this.dirtyCheck = ((JDOFieldDescriptor)fieldDesc).isDirtyCheck();
                    names = (names!=null?names:relnames);
                    this.joinFields = classnames;
                    break;
                case REF_TYPE:
                    this.tableName = ((JDOClassDescriptor)related).getTableName();
                    this.jdoName = fieldDesc.getFieldName();
                    this.load = true;
                    this.store = false;
                    this.multi = fieldDesc.isMultivalued();
                    this.joined = true;
                    this.dirtyCheck = (fieldDesc instanceof JDOFieldDescriptor)?((JDOFieldDescriptor)fieldDesc).isDirtyCheck():true;
                    names = (names!=null?names:relnames);
                    this.joinFields = (joins!=null?joins:classnames);
                    break;
                case REL_TABLE_TYPE:
                    this.tableName = ((JDOFieldDescriptor)fieldDesc).getManyTable();
                    this.jdoName = fieldDesc.getFieldName();
                    this.load = true;
                    this.store = false;
                    this.multi = fieldDesc.isMultivalued();
                    this.joined = true;
                    this.dirtyCheck = ((JDOFieldDescriptor)fieldDesc).isDirtyCheck();
                    names = (names!=null?names:relnames);
                    this.joinFields = (joins!=null?joins:classnames);
                    break;
                default:
                    throw new MappingException("Never happen! But, it won't compile without the exception");
                }
                
                this.columns = new ColumnInfo[relids.length];
                for ( int i=0; i<relids.length; i++ ) {
                    if ( !(relids[i] instanceof JDOFieldDescriptor) )
                        throw new MappingException("Related class identities field does not contains sql information!");

                    JDOFieldDescriptor relId = (JDOFieldDescriptor)relids[i];
                    FieldHandlerImpl fh = (FieldHandlerImpl) relId.getHandler();
                    columns[i] = new ColumnInfo( names[i], relId.getSQLType(), 
                            fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam() ); 
                }
            } else {
                // primitive field
                this.tableName = classTable;
                this.jdoName = fieldDesc.getFieldName();
                this.load = true;
                this.store = !ext;
                this.multi = false;
                this.joined = false;
                this.joinFields = null;
                this.dirtyCheck = ((JDOFieldDescriptor)fieldDesc).isDirtyCheck();

                FieldHandlerImpl fh = (FieldHandlerImpl) fieldDesc.getHandler();
                this.columns = new ColumnInfo[1];
                this.columns[0] = new ColumnInfo( ((JDOFieldDescriptor)fieldDesc).getSQLName(), 
                        ((JDOFieldDescriptor)fieldDesc).getSQLType(), fh.getConvertTo(),
                        fh.getConvertFrom(), fh.getConvertParam() );
            }
        }
        public String toString() {
            return tableName;
        }
    }

    static final class ColumnInfo {

        final String  name;

        final int sqlType;

        final TypeConvertor convertTo;

        final TypeConvertor convertFrom;

        final String convertParam;

        ColumnInfo( String name, int type, TypeConvertor convertTo,
                TypeConvertor convertFrom, String convertParam ) {
            this.name = name;
            this.sqlType = type;
            this.convertTo = convertTo;
            this.convertFrom = convertFrom;
            this.convertParam = convertParam;
        }
    }
    static final class SQLQuery
        implements PersistenceQuery
    {


        private PreparedStatement _stmt;


        private ResultSet         _rs;


        private final SQLEngine _engine;


        private final Class[]   _types;


        private final Object[]  _values;


        private final String    _sql;


        private Object[]        _lastIdentities;


        private int             _identSqlType;


        SQLQuery( SQLEngine engine, String sql, Class[] types )
        {
            _engine = engine;
            _types = types;
            _values = new Object[ _types.length ];
            _sql = sql;
            _identSqlType = ( (JDOFieldDescriptor) _engine._clsDesc.getIdentity() ).getSQLType();
        }


        public int getParameterCount()
        {
            return _types.length;
        }


        public Class getParameterType( int index )
            throws ArrayIndexOutOfBoundsException
        {

            return _types[ index ];
        }


        public void setParameter( int index, Object value )
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
        {
            _values[ index ] = value;
        }


        public Class getResultType()
        {
            return _engine._clsDesc.getJavaClass();
        }


        public void execute( Object conn, AccessMode accessMode )
            throws QueryException, PersistenceException
        {
            _lastIdentities = null;
            try {
                _stmt = ( (Connection) conn ).prepareStatement( _sql );
                for ( int i = 0 ; i < _values.length ; ++i ) {
                    _stmt.setObject( i + 1, _values[ i ] );
                    _values[ i ] = null;
                }
                _rs = _stmt.executeQuery();
            } catch ( SQLException except ) {
                if ( _stmt != null ) {
                    try {
                        _stmt.close();
                    } catch ( SQLException e2 ) { }
                }
                throw new PersistenceExceptionImpl( except );
            }
        }


        public Object[] nextIdentities( Object[] identities )
            throws PersistenceException
        {
            Object[] sqlIdentities;
            Object[] returnId;
            boolean empty;

            try {
                if ( _lastIdentities == null ) {

                    if ( ! _rs.next() ) {
                        System.out.println("result set done!");
                        return null;
                    }

                    _lastIdentities = new Object[_engine._ids.length];
                    empty = true;
                    for ( int i=0; i<_engine._ids.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType );
                        if ( _lastIdentities[i] != null )
                            empty = false;
                    }
                    if ( empty ) {
                        return null;
                    } else {
                        return _engine.idToJava( _lastIdentities );
                    }
                }

                sqlIdentities = _engine.idToSql( identities );
                // skip to next id
                while ( _lastIdentities != null && OID.isEquals( _lastIdentities, sqlIdentities ) ) {
                    //System.out.println("inside loop");
                    if ( ! _rs.next() ) {
                        _lastIdentities = null;
                        return null;
                    }

                    _lastIdentities = new Object[_engine._ids.length];
                    empty = true;
                    for ( int i=0; i<_engine._ids.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType );
                        if ( _lastIdentities[i] != null )
                            empty = false;
                    }
                    if ( empty )
                        _lastIdentities = null;
                }
                return _engine.idToJava( _lastIdentities );
            } catch ( SQLException except ) {
                _lastIdentities = null;
                throw new PersistenceExceptionImpl( except );
            }
        }


        public void close()
        {
            if ( _rs != null ) {
                try {
                    _rs.close();
                } catch ( SQLException except ) { }
                _rs = null;
            }
            if ( _stmt != null ) {
                try {
                    _stmt.close();
                } catch ( SQLException except ) { }
                _stmt = null;
            }
        }


        public Object fetch( Object[] fields, Object[] identities )
            throws ObjectNotFoundException, PersistenceException
        {
            //System.out.println("SQLQuery fetched!");
            int    count;
            Object stamp = null;
            Object[] sqlIdentities;
            //Object[] sqlFields = new Object[fields.length];


            try {
                sqlIdentities = _engine.idToSql( identities );

                // Load all the fields of the object including one-one relations
                count = _engine._ids.length;
                for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                    if ( _engine._fields[i].multi ) {
                        ArrayVector res = new ArrayVector();
                        Object[] temp = new Object[_engine._fields[i].columns.length];
                        for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                            Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                            if ( ! _rs.wasNull() )
                                temp[j] = value ;
                            count++;
                        }
                        res.add( temp );
                        fields[i] = res;
                    } else {
                        Object[] temp = new Object[_engine._fields[i].columns.length];
                        for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                            Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                            temp[j] =  _rs.wasNull() ? null : value;
                            count++;
                        }
                        fields[i] = temp;
                    }
                }

                if ( _rs.next() ) {
                    count = 1;
                    if ( _lastIdentities == null )
                        _lastIdentities = new Object[_engine._ids.length];
                    for ( int i=0; i<_lastIdentities.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, count, _identSqlType );
                        count++;
                    }
                    while ( OID.isEquals( sqlIdentities, _lastIdentities ) ) {
                        for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                            if ( _engine._fields[i].multi ) {
                                ArrayVector res = (ArrayVector) fields[i];
                                Object[] temp = new Object[_engine._fields[i].columns.length];
                                for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                                    Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                                    if ( ! _rs.wasNull() )
                                        temp[j] = value ;
                                }
                                res.add( temp );
                                fields[i] = res;
                                count++;
                            } else {
                                count += _engine._fields[i].columns.length;
                            }
                        }

                        if ( _rs.next() )
                            for ( int i=0; i<_engine._ids.length; i++ ) {
                                _lastIdentities[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType );
                            }
                        else
                            _lastIdentities = null;
                    }
                } else
                    _lastIdentities = null;
            } catch ( SQLException except ) {
                throw new PersistenceExceptionImpl( except );
            }

            return stamp;
        }
    }



    /**
     * PersistenceQuery implementation for use with stored procedures.
     * It can deals with multiple ResultSets.
     *
     * @author <a href="on@ibis.odessa.ua">Oleg Nitz</a>
     * @version $Revision$ $Date$
     */
    static final class StoredProcedure implements PersistenceQuery
    {


        private CallableStatement _stmt;


        private ResultSet         _rs;


        private final SQLEngine   _engine;


        private final Class[]     _types;


        private final Object[]    _values;


        private final String      _sql;


        private Object[]          _lastIdentities;


        private int               _identSqlType;


        StoredProcedure( SQLEngine engine, String sql, Class[] types )
        {
            _engine = engine;
            _types = types;
            _values = new Object[ _types.length ];
            _sql = sql;
            _identSqlType = ( (JDOFieldDescriptor) _engine._clsDesc.getIdentity() ).getSQLType();
        }


        public int getParameterCount()
        {
            return _types.length;
        }


        public Class getParameterType( int index )
            throws ArrayIndexOutOfBoundsException
        {
            return _types[ index ];
        }


        public void setParameter( int index, Object value )
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
        {
            _values[ index ] = value;
        }


        public Class getResultType()
        {
            return _engine.getDescriptor().getJavaClass();
        }


        public void execute( Object conn, AccessMode accessMode )
            throws QueryException, PersistenceException
        {
            //System.out.println("SQLQuery executed!");
            _lastIdentities = null;
            try {
                _stmt = ( (Connection) conn ).prepareCall( _sql );
                for ( int i = 0 ; i < _values.length ; ++i ) {
                    _stmt.setObject( i + 1, _values[ i ] );
                    _values[ i ] = null;
                }
                _stmt.execute();
                _rs = _stmt.getResultSet();
            } catch ( SQLException except ) {
                throw new PersistenceExceptionImpl( except );
            }
        }


        private boolean nextRow() throws SQLException {
            while ( true ) {
                if ( _rs != null && _rs.next() ) {
                    return true;
                }
                if ( !_stmt.getMoreResults() && _stmt.getUpdateCount() == -1 ) {
                    _rs = null;
                    return false;
                }
                _rs = _stmt.getResultSet();
            }
        }


        public Object[] nextIdentities( Object[] identities )
            throws PersistenceException
        {
            try {
                if ( _lastIdentities == null ) {
                    if ( !nextRow() )
                        return null;
                    // need for loop
                    _lastIdentities = new Object[_engine._ids.length];
                    for ( int i=0; i<_engine._ids.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType );
                    }
                    return _engine.idToJava( _lastIdentities );
                }

                while ( _lastIdentities.equals( identities ) ) {
                    if ( ! nextRow() ) {
                        _lastIdentities = null;
                        return null;
                    }
                    for ( int i=0; i<_engine._ids.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType );
                    }
                }
                return _engine.idToJava( _lastIdentities );
            } catch ( SQLException except ) {
                _lastIdentities = null;
                throw new PersistenceExceptionImpl( except );
            }
        }


        public void close()
        {
            if ( _rs != null ) {
                try {
                    _rs.close();
                } catch ( SQLException except ) { }
                _rs = null;
            }
            if ( _stmt != null ) {
                try {
                    _stmt.close();
                } catch ( SQLException except ) { }
                _stmt = null;
            }
        }


        public Object fetch( Object[] fields, Object[] identities )
            throws ObjectNotFoundException, PersistenceException
        {
            int    count;
            Object stamp = null;
            Object[] sqlIdentities = null;
            //Object[] sqlFields = new Object[fields.length];


            try {
                sqlIdentities = _engine.idToSql( identities );

                // Load all the fields of the object including one-one relations
                count = _engine._ids.length;
                for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                    if ( _engine._fields[i].multi ) {
                        ArrayVector res = new ArrayVector();
                        Object[] temp = new Object[_engine._fields[i].columns.length];
                        for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                            Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                            if ( ! _rs.wasNull() )
                                temp[j] = value ;
                            count++;
                        }
                        res.add( temp );
                        fields[i] = res;
                    } else {
                        Object[] temp = new Object[_engine._fields[i].columns.length];
                        for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                            Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                            temp[j] =  _rs.wasNull() ? null : value;
                            count++;
                        }
                        fields[i] = temp;
                    }
                }

                if ( _rs.next() ) {
                    count = 1;
                    if ( _lastIdentities == null )
                        _lastIdentities = new Object[_engine._ids.length];
                    for ( int i=0; i<_lastIdentities.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, count, _identSqlType );
                        count++;
                    }
                    while ( OID.isEquals( sqlIdentities, _lastIdentities ) ) {
                        count = 1;
                        for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                            if ( _engine._fields[i].multi ) {
                                ArrayVector res = (ArrayVector) fields[i];
                                Object[] temp = new Object[_engine._fields[i].columns.length];
                                for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                                    Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                                    if ( ! _rs.wasNull() )
                                        temp[j] = value ;
                                }
                                res.add( temp );
                                fields[i] = res;
                            } else {
                                count += _engine._fields[i].columns.length;
                            }
                        }

                        if ( _rs.next() )
                            for ( int i=0; i<_engine._ids.length; i++ ) {
                                _lastIdentities[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType );
                            }
                        else
                            _lastIdentities = null;
                    }
                } else
                    _lastIdentities = null;
            } catch ( SQLException except ) {
                throw new PersistenceExceptionImpl( except );
            }
            return stamp;
        }

    }
}
