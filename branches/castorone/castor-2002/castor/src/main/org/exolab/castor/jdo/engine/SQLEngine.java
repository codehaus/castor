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


    private FieldInfo[]         _ids;


    private SQLEngine           _extends;


    private QueryExpression     _sqlFinder;


    private PersistenceFactory  _factory;


    private String              _stampField;


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

        if ( _clsDesc.getExtends() != null ) {
            //_extends = new SQLEngine( (JDOClassDescriptor) _clsDesc.getExtends(), null,
            //          _factory, _stampField );
        } else {
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

        // we always put the original id info in front
        FieldDescriptor[] idDescriptors = clsDesc.getIdentities();
        for ( int i=0; i<idDescriptors.length; i++ ) {
            KeyGeneratorDescriptor keyGenDesc = clsDesc.getKeyGeneratorDescriptor();
            if ( keyGenDesc != null ) {
                if ( idDescriptors.length != 1 )
                    throw new MappingException("Confussed KeyGenerator!");
                _keyGen = keyGenDesc.getKeyGeneratorRegistry().getKeyGenerator(
                        _factory, keyGenDesc, ((JDOFieldDescriptor)idDescriptors[i]).getSQLType(),
                        _logInterceptor );
                if ( idDescriptors[i] instanceof JDOFieldDescriptor )
                    idsInfo.add( new FieldInfo( (JDOFieldDescriptor)idDescriptors[i], true, _keyGen ) );
                else 
                    throw new MappingException("Except JDOFieldDescriptor");
            } else {
                if ( idDescriptors[i] instanceof JDOFieldDescriptor )
                    idsInfo.add( new FieldInfo( (JDOFieldDescriptor)idDescriptors[i], true ) );
                else 
                    throw new MappingException("Except JDOFieldDescriptor");
            }
        }

        // then, we put depended class id in the back
        ClassDescriptor base = clsDesc;
        // walk until the base class which this class extends found
        while ( base.getExtends() != null ) {
            base = (ClassDescriptor) base.getExtends();
        }
        JDOClassDescriptor jdoBase = (JDOClassDescriptor) base;
        idDescriptors = null;
        if ( jdoBase.getDepends()!= null ) {
            idDescriptors = ((ClassDescriptorImpl)jdoBase.getDepends()).getIdentities();
            for ( int i=0; i<idDescriptors.length; i++ ) {
                if ( idDescriptors[i] instanceof JDOFieldDescriptor )
                    idsInfo.add( new FieldInfo( (JDOFieldDescriptor)idDescriptors[i], true ) );
                else 
                    throw new MappingException("Except JDOFieldDescriptor");
            }
        }

        // then do the fields
        FieldDescriptor[] fieldDescriptors = clsDesc.getFields();
        for ( int i=0; i<fieldDescriptors.length; i++ ) {
            if ( fieldDescriptors[i] instanceof JDOFieldDescriptor )
                fieldsInfo.add( new FieldInfo( (JDOFieldDescriptor)fieldDescriptors[i], true  ) );
            else 
                fieldsInfo.add( new FieldInfo( fieldDescriptors[i], true ) );
        }

        _ids = new FieldInfo[idsInfo.size()];        
        idsInfo.copyInto( _ids );

        _fields = new FieldInfo[fieldsInfo.size()];        
        fieldsInfo.copyInto( _fields );

        try {
            buildSql( _clsDesc, _logInterceptor );
            buildFinder( _clsDesc, _logInterceptor );
        } catch ( QueryException except ) {
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

    private Object[] toSql( Object[] objects, boolean ids ) 
            throws PersistenceException {
        Object[] result = null;

        if ( objects == null ) return null;

        /*
        System.out.print("toSql. object: ");
        if ( objects == null ) {
            System.out.println(" null ");
        } else {
            System.out.println(" not null");
            for ( int i=0; i<objects.length; i++ ) {
                if ( i > 0 ) System.out.print(",");
                System.out.print( (objects[i]==null?null:objects[i].getClass()) + "/" + objects[i] + "    ");
            }
            System.out.println();
        }*/
        try {
            if ( ids == ID_TYPE ) {
                // using id convert
                result = new Object[objects.length];
                for ( int i=0; i<_ids.length; i++ ) {
                    if ( _ids[i].convertFrom != null && objects[i] != null ) {
                        result[i] = _ids[i].convertFrom.convert( objects[i], _ids[i].convertParam );
                    } else {
                        result[i] = objects[i];
                    }
                }
                /*
                System.out.println("result:");
                for ( int i=0; i<objects.length; i++ ) {
                    if ( i > 0 ) System.out.print(",");
                    System.out.print( (result[i]==null?null:result[i].getClass()) + "/" + result[i] + "    ");
                }*/
                return result;
            } else {
                // using field convert
                result = new Object[objects.length];
                for ( int i=0; i<_fields.length; i++ ) {
                    if ( _fields[i].store && _fields[i].convertFrom != null && objects[i] != null ) {
                        result[i] = _fields[i].convertFrom.convert( objects[i], _fields[i].convertParam );
                    } else {
                        result[i] = objects[i];
                    }
                }
                /*
                System.out.println("result:");
                for ( int i=0; i<objects.length; i++ ) {
                    if ( i > 0 ) System.out.print(",");
                    System.out.print( (objects[i]==null?null:objects[i].getClass()) + "/" + objects[i] + "    ");
                }*/
                return result;
            }
        } catch ( ClassCastException e ) {
            throw new PersistenceException( Messages.format( "mapping.wrongConvertor",  "" ) );
        }
    }

    private Object[] toJava( Object[] objects, boolean ids ) 
            throws PersistenceException {

        /*
        System.out.print("toJava. ");
        if ( objects == null ) {
            System.out.println("object null ");
        } else {
            if ( ids == ID_TYPE ) {
                for ( int i=0; (i<objects.length)&&(i<_ids.length); i++ ) {
                    if ( i > 0 ) System.out.print("     ,");
                    System.out.print( (objects[i]==null?null:objects[i].getClass()) + "/" + objects[i] + "|");
                    System.out.println(_ids[i].convertTo);
                }
            } else {
                for ( int i=0; (i<objects.length)&&(i<_fields.length); i++ ) {
                    if ( i > 0 ) System.out.print("     ,");
                    System.out.print( (objects[i]==null?null:objects[i].getClass()) + "/" + objects[i] + "|");
                    System.out.println(_fields[i].convertTo);
                }
            }

            System.out.println();
        }*/

        if ( objects == null ) return null;

        Object[] result = null;

        try {
            if ( ids == ID_TYPE ) {
                // using id convert
                result = new Object[objects.length];
                for ( int i=0; i<_ids.length; i++ ) {
                    if ( _ids[i].convertTo != null && objects[i] != null ) {
                        result[i] = _ids[i].convertTo.convert( objects[i], _ids[i].convertParam );
                        //System.out.println("..."+result[i].getClass()+"...");
                    } else {
                        result[i] = objects[i];
                    }
                }
                /*
                System.out.println("result:");
                for ( int i=0; i<result.length; i++ ) {
                    if ( i > 0 ) System.out.print(",");
                    System.out.print( result[i].getClass() + "/" + result[i] + "    ");
                    System.out.print( _ids[i].convertTo );
                }
                System.out.println();
                */
                return result;
            } else {
                // using field convert
                result = new Object[objects.length];
                for ( int i=0; i<_fields.length; i++ ) {
                    if ( _fields[i].load && _fields[i].convertTo != null && objects[i] != null ) {
                        if ( _fields[i].multi ) {
                            Vector v = new Vector();
                            Vector loaded = (Vector) objects[i];
                            for ( int j=0; j<loaded.size(); j++ ) {
                                v.addElement( _fields[i].convertTo.convert( loaded.elementAt(j), 
                                    _fields[i].convertParam ) );
                            }
                            result[i] = v;
                        } else {
                            result[i] = _fields[i].convertTo.convert( objects[i], 
                                    _fields[i].convertParam );
                        }
                    } else {
                        result[i] = objects[i];
                    }
                }
                /*
                System.out.println("result:");
                for ( int i=0; (i<objects.length)&&(i<_fields.length); i++ ) {
                    if ( i > 0 ) System.out.print(",");
                    System.out.print( (objects[i]==null?null:objects[i].getClass()) + "/" + objects[i] + "    ");
                    System.out.print(_fields[i].convertTo);
                }
                System.out.println();
                */
                return result;
            }
        } catch ( ClassCastException e ) {
            throw new PersistenceException( Messages.format( "mapping.wrongConvertor",  "" ) );
        }
    }

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
        Object[]          convertedFields;
        Object[]          sqlIdentities;
        Object            tempId;        

        convertedFields = toSql( fields, FIELD_TYPE );

        stmt = null;
        try {
            // Must create record in parent table first.
            // All other dependents are created afterwards.
            if ( _extends != null )
                identities = _extends.create( conn, fields, identities );

            if ( _keyGen == null && identities == null )
                throw new PersistenceExceptionImpl( "persist.noIdentity" );

            // Generate key before INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                identities[0] = generateKey( conn );   // genKey return identity in JDO type
            }

            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.DURING_INSERT ) 
                stmt = ( (Connection) conn ).prepareCall( _sqlCreate );
            else
                stmt = ( (Connection) conn ).prepareStatement( _sqlCreate );

            System.out.println( _sqlCreate );
            // Must remember that SQL column index is base one
            count = 1;
            if ( _keyGen == null || _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                sqlIdentities = toSql( identities, ID_TYPE );
                for ( int i=0; i<_ids.length; i++ ) {
                    System.out.print("<<<<id:"+count+":"+sqlIdentities[i]+":"+_ids[i].name+">>>>");
                    stmt.setObject( count, sqlIdentities[i] );
                    ++count;
                }
            }


            for ( int i = 0 ; i < _fields.length ; ++i ) 
                if ( _fields[ i ].store ) {
                    if ( convertedFields[ i ] == null ) {
                        System.out.print("<<<<"+count+":null:"+_fields[i].name+" type: "+_fields[ i ].sqlType+">>>>");
                        stmt.setNull( count, _fields[ i ].sqlType );
                    } else {
                        System.out.print("<<<<"+count+":"+convertedFields[i]+":"+_fields[i].name+" type: "+_fields[ i ].sqlType+">>>>");
                        stmt.setObject( count, convertedFields[ i ], _fields[ i ].sqlType );
                    }

                    ++count;
                }
            System.out.println();
            //System.out.println();
            // Generate key during INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.DURING_INSERT ) {
                CallableStatement cstmt = (CallableStatement) stmt;
                int sqlType;

                sqlType = ( (JDOFieldDescriptor) _clsDesc.getIdentity() ).getSQLType();
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

                if ( _ids[0].convertTo != null && tempId != null ) {
                    identities[0] = _ids[0].convertTo.convert( tempId, _ids[0].convertParam );
                } else {
                    identities[0] = tempId;
                }
            } else 
                stmt.executeUpdate();
            
            stmt.close();

            // Generate key after INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.AFTER_INSERT ) {
                tempId = generateKey( conn );
                if ( _ids[0].convertTo != null && tempId != null ) {
                    identities[0] = _ids[0].convertTo.convert( tempId, _ids[0].convertParam );
                } else {
                    identities[0] = tempId;
                }
            }

            return identities;

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
        throws ObjectModifiedException, ObjectDeletedException, PersistenceException
    {
        PreparedStatement stmt = null;
        int               count;

        Object[]          convertedFields;
        Object[]          convertedOriginal = null;
        Object[]          sqlIdentities;

        convertedFields = toSql( fields, FIELD_TYPE );

        if ( original != null ) {
            convertedOriginal = toSql( original, FIELD_TYPE );
        } else {
            convertedOriginal = null;
        }


        sqlIdentities = toSql( identities, ID_TYPE );

        try {
            // Must store record in parent table first.
            // All other dependents are stored independently.
            if ( _extends != null )
                _extends.store( conn, fields, identities, original, stamp );
            
            stmt = ( (Connection) conn ).prepareStatement( original == null ? _sqlStore : _sqlStoreDirty );
            System.out.println(original == null ? _sqlStore : _sqlStoreDirty);

            count = 1;
            for ( int i = 0 ; i < _fields.length ; ++i )
                if ( _fields[ i ].store ) {
                    if ( convertedFields[ i ] == null ) {
                        System.out.print("<<"+_fields[i].name+":"+count+":NULL:type "+_fields[ i ].sqlType+">>");
                        if ( _fields[ i ].sqlType == 12 ) {
                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            stmt.setObject( count, "", _fields[ i ].sqlType );
                        } else {
                            stmt.setNull( count, _fields[ i ].sqlType );
                        }
                    } else {
                        System.out.print("<<"+_fields[i].name+":"+count+":"+convertedFields[i]+":type "+_fields[ i ].sqlType+">>");
                        stmt.setObject( count, convertedFields[ i ], _fields[ i ].sqlType );
                    }
                    ++count;
                }

            for ( int i=0; i< sqlIdentities.length; i++ ) {
                System.out.print("<<id#"+_ids[i].name+":"+count+":"+sqlIdentities[i]+":type "+_ids[ i ].sqlType+">>");
                stmt.setObject( count, sqlIdentities[i], _ids[i].sqlType );
                ++count;
            }

            if ( original != null ) {
                for ( int i = 0 ; i < _fields.length ; ++i ) {
                    if ( _fields[ i ].dirtyCheck && _fields[i].store ) {
                        if ( convertedOriginal[ i ] == null ) {
                            System.out.print("<<"+_fields[i].name+":"+count+":"+"NULL"+":type "+_fields[ i ].sqlType+">>");
                            if ( _fields[ i ].sqlType == 12 ) {
                                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                                stmt.setObject( count, "", _fields[ i ].sqlType );
                            } else 
                                stmt.setNull( count, _fields[ i ].sqlType );

                        } else {
                            System.out.print("<<"+_fields[i].name+":"+count+":"+convertedOriginal[ i ]+":type "+_fields[ i ].sqlType+">>");
                            stmt.setObject( count, convertedOriginal[ i ], _fields[ i ].sqlType );
                        }
                        ++count;
                    }
                }
            } 
            System.out.println();
            
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

        Object[] sqlIdentities = toSql( identities, ID_TYPE );

        try {
            stmt = ( (Connection) conn ).prepareStatement( _sqlRemove );
            for ( int i=0; i<sqlIdentities.length; i++ ) {
                stmt.setObject( 1+i, sqlIdentities[i] );
            }

            System.out.println("Object will be deleted: "+OID.flatten( identities ));
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

        Object[] sqlIdentities = toSql( identities, ID_TYPE );

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
        throws ObjectNotFoundException, PersistenceException
    {
        PreparedStatement stmt;
        ResultSet         rs;
        Object            stamp = null;
        Object[]          sqlIdentities;
        Object[]          sqlFields;
        Object[]          tempFields;
        int               count;

        try {
            sqlIdentities = toSql( identities, ID_TYPE );
            stmt = ( (Connection) conn ).prepareStatement( ( accessMode == AccessMode.DbLocked ) ? _sqlLoadLock : _sqlLoad );
            System.out.println(( accessMode == AccessMode.DbLocked ) ? _sqlLoadLock : _sqlLoad);

            for ( int i=0; i<sqlIdentities.length; i++ ) {
                System.out.println("sqlId: " + (sqlIdentities[i]==null?null:(sqlIdentities[i]+"/"+sqlIdentities[i].getClass())));
                stmt.setObject( i+1, sqlIdentities[i], _ids[i].sqlType );
            }

            sqlFields = new Object[fields.length];

            rs = stmt.executeQuery();
            if ( ! rs.next() )
                throw new ObjectNotFoundExceptionImpl( _clsDesc.getJavaClass(), OID.flatten( identities ) );

            count = 1;
            // Load all the fields of the object including one-one relations
            for ( int i = 0 ; i < _fields.length ; ++i  ) {
                Object value;

                fields[ i ] =  null;
                if ( ! _fields[ i ].load )
                    continue;

                if ( _fields[ i ].multi ) {
                    sqlFields[ i ] = new Vector();
                    System.out.println("is multi................");
                    value = SQLTypes.getObject( rs, count, _fields[ i ].sqlType );
                    if ( ! rs.wasNull() )
                        ( (Vector) sqlFields[ i ] ).addElement( value );
                } else {
                    System.out.println("not multi................");
                    value = SQLTypes.getObject( rs, count, _fields[ i ].sqlType );
                    sqlFields[ i ] =  rs.wasNull() ? null : value;
                }
                ++count;
            }

            while ( rs.next() ) {
                count = 1;
                for ( int i = 0; i < _fields.length ; ++i  ) {
                    if ( ! _fields[ i ].load )
                        continue;

                    if ( _fields[ i ].multi ) {
                        Object value;
                        System.out.println("while loop.........is multi");
                        value = SQLTypes.getObject( rs, count, _fields[ i ].sqlType );
                        if ( ! rs.wasNull() && ! ( (Vector) sqlFields[ i ] ).contains( value ) ) {
                            ( (Vector) sqlFields[ i ] ).addElement( value );
                        }
                    
                    }
                    count ++;
                }
            }
            rs.close();
            stmt.close();
            
        } catch ( SQLException except ) {
            throw new PersistenceExceptionImpl( except );
        }

        tempFields = toJava( sqlFields, FIELD_TYPE );
        System.arraycopy( tempFields, 0, fields, 0, tempFields.length );
        return stamp;
    }


    private void buildSql( JDOClassDescriptor clsDesc, LogInterceptor logInterceptor )
            throws QueryException
    {
        StringBuffer         sql;
        JDOFieldDescriptor[] jdoFields;
        FieldDescriptor[]    fields;
        int                  count;
        QueryExpression      query;
        String               wherePK;
        String               primKeyName;
        String               tableName;
        boolean              keyGened = false;
      

        tableName = clsDesc.getTableName();
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
            if ( _ids[i].keyGen == null || _ids[i].keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                if ( count > 0 ) sql.append( ',' );
                keyGened = true;
                sql.append( _factory.quoteName( _ids[i].name ) );
                ++count;
            }
        }
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[i].store ) {
                if ( count > 0 ) {
                    sql.append( ',' );
                    sql.append( _factory.quoteName( _fields[i].name ) );                    
                }
                ++count;
            }
        }
        sql.append( ") VALUES (" );
        for ( int i = 0 ; i < count ; ++i ) {
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
        if ( logInterceptor != null )
            logInterceptor.storeStatement( "SQL for creating " + clsDesc.getJavaClass().getName() +
                                           ": " + _sqlCreate );


        sql = new StringBuffer( "DELETE FROM " ).append( _factory.quoteName( tableName ) );
        sql.append( wherePK );
        _sqlRemove = sql.toString();
        if ( logInterceptor != null )
            logInterceptor.storeStatement( "SQL for deleting " + clsDesc.getJavaClass().getName() +
                                           ": " + _sqlRemove );


        sql = new StringBuffer( "UPDATE " );
        sql.append( _factory.quoteName( clsDesc.getTableName() ) ).append( " SET " );
        count = 0;
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ].store ) {
                if ( count > 0 )
                    sql.append( ',' );
                sql.append( _factory.quoteName( _fields[ i ].name ) );
                sql.append( "=?" );
                ++count;
            }
        }
        sql.append( wherePK );
        _sqlStore = sql.toString();

        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ] != null && _fields[i].store ) {
                if ( _fields[i].dirtyCheck ) {
                    sql.append( " AND " );
                    sql.append( _factory.quoteName( _fields[i].name ) );
                    sql.append( "=?" );
                }
            }
        }
        _sqlStoreDirty = sql.toString();
        if ( logInterceptor != null )
            logInterceptor.storeStatement( "SQL for updating " + clsDesc.getJavaClass().getName() +
                                           ": " + _sqlStoreDirty );
    }


    private void buildFinder( JDOClassDescriptor clsDesc, LogInterceptor logInterceptor )
        throws MappingException, QueryException
    {
        Vector          fields;
        QueryExpression expr;

        fields = new Vector();
        expr = _factory.getQueryExpression();
        addLoadSql( clsDesc, expr, fields, false, true, true );

        _sqlLoad = expr.getStatement( false );
        _sqlLoadLock = expr.getStatement( true );
        _fields = new FieldInfo[ fields.size() ];
        fields.copyInto( _fields );
        if ( logInterceptor != null )
            logInterceptor.storeStatement( "SQL for loading " + clsDesc.getJavaClass().getName() +
                                           ":  " + _sqlLoad );

        _sqlFinder = _factory.getQueryExpression();
        addLoadSql( clsDesc, _sqlFinder, fields, true, false, true );
    }


    private void addLoadSql( JDOClassDescriptor clsDesc, QueryExpression expr, Vector allFields,
                             boolean loadPk, boolean queryPk, boolean store )
        throws MappingException
    {
        FieldDescriptor[]    fields;
        JDOClassDescriptor   extend;
        FieldDescriptor      identity;
        String               identitySQL;

        identity = clsDesc.getIdentity();
        identitySQL = ( (JDOFieldDescriptor) identity ).getSQLName();

        // need depend......

        // If this class extends another class, create a join with the parent table and
        // add the load fields of the parent class (but not the store fields)
        if ( clsDesc.getExtends() != null ) {
            expr.addInnerJoin( clsDesc.getTableName(), identitySQL,
                               ( (JDOClassDescriptor) clsDesc.getExtends() ).getTableName(), identitySQL );
            addLoadSql( (JDOClassDescriptor) clsDesc.getExtends(), expr, allFields,
            true, queryPk, false );
            loadPk = false;
            queryPk = false;
        }

        if ( loadPk )
            expr.addColumn( clsDesc.getTableName(), identitySQL );
        if ( queryPk )
            expr.addParameter( clsDesc.getTableName(), identitySQL, QueryExpression.OpEquals );

        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor ) {
                ClassDescriptor relDesc = fields[i].getClassDescriptor();

                if ( ((JDOFieldDescriptor)fields[i]).getManyTable() == null ) {
                    // Field is primitive.
                    expr.addColumn( clsDesc.getTableName(),
                                    ( (JDOFieldDescriptor) fields[ i ] ).getSQLName() );

                    if ( relDesc == null )
                        allFields.addElement( new FieldInfo( (JDOFieldDescriptor)fields[i], store ) );
                    else
                        allFields.addElement( new FieldInfo( (JDOFieldDescriptor)fields[i], store,
                            ( (JDOFieldDescriptor) relDesc.getIdentity() ).getSQLType() ) );
                    
                } else {
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
        }
    }


    public String toString()
    {
        return _clsDesc.toString();
    }


    static final class FieldInfo {

        final boolean load;

        final boolean store;

        boolean manyToMany;

        final String  name;

        final boolean multi;

        final boolean dirtyCheck;

        int           sqlType;

        KeyGenerator  keyGen;

        final TypeConvertor convertTo;

        final TypeConvertor convertFrom;

        final String convertParam;

        

        FieldInfo( FieldDescriptor fieldDesc, boolean store ) 
                throws MappingException{
            this.name = fieldDesc.getFieldName();
            this.dirtyCheck = false;

            ClassDescriptor related = fieldDesc.getClassDescriptor();
            if ( related == null ) 
                throw new MappingException("No related object!");

            if ( related instanceof JDOClassDescriptor ) {
                FieldDescriptor[] ids = ((JDOClassDescriptor)related).getIdentities();
                if ( ids.length != 1 ) 
                    throw new MappingException("Many-to-Many relation must have single column pk");

                FieldHandlerImpl fh = (FieldHandlerImpl) ids[0].getHandler();
                convertTo = fh.getConvertTo();
                convertFrom = fh.getConvertFrom();
                convertParam = fh.getConvertParam();
                this.sqlType = ((JDOFieldDescriptor)ids[0]).getSQLType();
                this.manyToMany = true;
                this.multi = true;
                //System.out.println("new FieldInfo. name: "+this.name+" sqltype: "+this.sqlType+" one-to-many");
            } else {
                FieldDescriptor id = related.getIdentity();
                if ( id == null ) 
                    throw new MappingException("One-to-Many relation must have single column pk");

                FieldHandlerImpl fh = (FieldHandlerImpl) id.getHandler();
                convertTo = fh.getConvertTo();
                convertFrom = fh.getConvertFrom();
                convertParam = fh.getConvertParam();
                if ( id instanceof JDOFieldDescriptor ) {
                    this.sqlType = ((JDOFieldDescriptor)id).getSQLType();
                } else 
                    throw new MappingException("Related object's id field is not an SQL field!");

                //System.out.println("new FieldInfo. name: "+this.name+" sqltype: "+this.sqlType+" one-to-many");
                this.manyToMany = false;
                this.multi = true;
            }                

            this.store = false;
            this.load = true;

        }
        FieldInfo( JDOFieldDescriptor fieldDesc, boolean store ) 
                throws MappingException {

            this.name = fieldDesc.getSQLName();
            this.manyToMany = fieldDesc.getManyTable() != null;
            this.multi = fieldDesc.isMultivalued() || this.manyToMany;
            this.dirtyCheck = fieldDesc.isDirtyCheck();
            
            if ( ! this.multi ) {
                // field is primitive
                FieldHandlerImpl fh = (FieldHandlerImpl) fieldDesc.getHandler();
                this.convertTo = fh.getConvertTo();
                this.convertFrom = fh.getConvertFrom();
                this.convertParam = fh.getConvertParam();

                this.store = true;
                this.load = true;
                this.sqlType = fieldDesc.getSQLType();
                //System.out.println("new FieldInfo. name: "+this.name+" sqltype: "+this.sqlType+" prim");
            } else if ( ! this.manyToMany ) {
                // field is one-to-many
                ClassDescriptor related = fieldDesc.getClassDescriptor();
                if ( related instanceof JDOClassDescriptor ) {
                    FieldDescriptor[] ids = ((JDOClassDescriptor)related).getIdentities();
                    if ( ids.length != 1 ) 
                        throw new MappingException("Many-to-Many relation must have single column pk");

                    FieldHandlerImpl fh = (FieldHandlerImpl) ids[0].getHandler();
                    convertTo = fh.getConvertTo();
                    convertFrom = fh.getConvertFrom();
                    convertParam = fh.getConvertParam();
                    this.sqlType = ((JDOFieldDescriptor)ids[0]).getSQLType();
                    //System.out.println("new FieldInfo. name: "+this.name+" sqltype: "+this.sqlType);
                } else 
                    throw new MappingException("Except JDOClassDescriptor");

                this.store = false;
                this.load = true;

            } else {
                // actually exactly the same as many-to-many now....
                ClassDescriptor related = fieldDesc.getClassDescriptor();
                if ( related instanceof JDOClassDescriptor ) {
                    FieldDescriptor[] ids = ((JDOClassDescriptor)related).getIdentities();
                    if ( ids.length != 1 ) 
                        throw new MappingException("Many-to-Many relation must have single column pk");

                    FieldHandlerImpl fh = (FieldHandlerImpl) ids[0].getHandler();
                    convertTo = fh.getConvertTo();
                    convertFrom = fh.getConvertFrom();
                    convertParam = fh.getConvertParam();
                    this.sqlType = ((JDOFieldDescriptor)ids[0]).getSQLType();

                    //System.out.println("new FieldInfo. name: "+this.name+" sqltype: "+this.sqlType+" M:N");
                    //System.out.println(" VARCHAR: "+java.sql.Types.VARCHAR+" "
                    //                    +" INT: "+java.sql.Types.INTEGER);

                } else 
                    throw new MappingException("Except JDOClassDescriptor");
                this.store = false;
                this.load = true;
            } 
        }
        FieldInfo( FieldDescriptor fieldDesc, boolean store, KeyGenerator keyGen )
                throws MappingException {
            this( fieldDesc, store );
            this.keyGen = keyGen;
        }
        FieldInfo( FieldDescriptor fieldDesc, boolean store, int sqlType )
                throws MappingException {
            this( fieldDesc, store );
            this.sqlType = sqlType;
        }

        public String toString()
        {
            return name;
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
            //System.out.println("SQLQuery executed!");
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
                    //System.out.println("return from next Id");
                    if ( empty ) {
                        System.out.println("result set done!");
                        return null;
                    } else {
                        return _engine.toJava(_lastIdentities,_engine.ID_TYPE);
                    }
                }

                sqlIdentities = _engine.toSql( identities, _engine.ID_TYPE );
                /*
                System.out.println("id: "+OID.flatten( identities ) + "  " + identities[0].getClass() );
                System.out.println("sql-ed id: "+OID.flatten( sqlIdentities ) + "  " + sqlIdentities[0].getClass() );
                System.out.println("last_id: "+ OID.flatten( _lastIdentities ) + "  " + _lastIdentities[0].getClass() );
                System.out.println("sql-ed id==last_id? "+OID.isEquals( sqlIdentities, _lastIdentities) );
                */
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
                return _engine.toJava( _lastIdentities, _engine.ID_TYPE );
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
            Object[] sqlFields = new Object[fields.length];


            try {
                count = _engine._ids.length + 1; 

                sqlIdentities = _engine.toSql( identities, _engine.ID_TYPE );

                // Load all the fields of the object including one-one relations
                for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                    Object value;
                    
                    value = SQLTypes.getObject( _rs, i + count, _engine._fields[ i ].sqlType );
                    if ( _engine._fields[ i ].multi ) {
                        sqlFields[ i ] = new Vector();
                        if ( ! _rs.wasNull() )
                            ( (Vector) sqlFields[ i ] ).addElement( value );
                    } else
                        sqlFields[ i ] =  _rs.wasNull() ? null : value;
                }

                if ( _rs.next() ) {
                    if ( _lastIdentities == null )
                        _lastIdentities = new Object[_engine._ids.length];
                    for ( int i=0; i<_lastIdentities.length; i++ ) {
                        _lastIdentities[i] = SQLTypes.getObject( _rs, i+1, _identSqlType );
                    }
                    while ( OID.isEquals( sqlIdentities, _lastIdentities ) ) {
                        for ( int i = 0; i < _engine._fields.length ; ++i  )
                            if ( _engine._fields[ i ].multi ) {
                                Object value;

                                value = SQLTypes.getObject( _rs, i + count, _engine._fields[ i ].sqlType );
                                if ( ! _rs.wasNull() && ! ( (Vector) sqlFields[ i ] ).contains( value ) )
                                    ( (Vector) sqlFields[ i ] ).addElement( value );
                            }
                        if ( _rs.next() )
                            for ( int i=0; i<_engine._ids.length; i++ ) {
                                _lastIdentities[i] = SQLTypes.getObject( _rs, 1, _identSqlType );
                            }
                        else
                            _lastIdentities = null;
                    }
                } else
                    _lastIdentities = null;
            } catch ( SQLException except ) {
                throw new PersistenceExceptionImpl( except );
            }

            Object[] tempFields = _engine.toJava( sqlFields, FIELD_TYPE );
            System.arraycopy( tempFields, 0, fields, 0, fields.length );
            return stamp;
        }
    }
}

