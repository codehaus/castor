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
 */


package org.exolab.castor.persist;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import org.exolab.castor.jdo.Persistent;
import org.exolab.castor.jdo.TimeStampable;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.engine.SQLEngine;
import org.exolab.castor.jdo.engine.JDOCallback;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.loader.MappingLoader;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.mapping.loader.RelationDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.spi.CallbackInterceptor;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.spi.LogInterceptor;
import org.exolab.castor.mapping.loader.ClassDescriptorImpl;
import org.exolab.castor.jdo.engine.JDOClassDescriptor;
import org.exolab.castor.jdo.engine.JDOFieldDescriptor;
import org.exolab.castor.util.Messages;
import java.sql.Connection;
import java.util.Vector;
import java.util.ArrayList;


public class ClassMolder {

    Class _base;

    String _name;

    FieldMolder[] _ids;

    FieldMolder[] _fhs;
    
    ClassMolder _extends;

    ClassMolder _depends;

    Vector _dependent;

    Vector _extendent;

    AccessMode _accessMode;

    Persistence _persistence;

    LockEngine _engine;

    CallbackInterceptor _callback;

    int _cachetype;

    int _cacheparam;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( _name );
        return sb.toString();
    }

    ClassMolder( DatingService ds, MappingLoader loader, LockEngine lock, ClassDescriptor clsDesc, Persistence persist ) 
            throws ClassNotFoundException, MappingException {
        ClassMapping clsMap = ((ClassDescriptorImpl) clsDesc).getMapping();

        _engine = lock;

        _persistence = persist;

        _name = clsMap.getName();

        _base = clsDesc.getJavaClass();

        _accessMode = AccessMode.getAccessMode( clsMap.getAccess() );

        ds.register( _name, this );

        ClassMapping dep = (ClassMapping) clsMap.getDepends();
        ClassMapping ext = (ClassMapping) clsMap.getExtends();

        if ( dep != null && ext != null ) 
            throw new MappingException("A JDO cannot both extends and depends on other objects");

        if ( dep != null ) {            
            ds.pairDepends( this, dep.getName() );
        }

        if ( ext != null ) {
            ds.pairExtends( this, ext.getName() );
        }
        
        FieldMapping[] fmId = getIdFields( clsMap );
        _ids = new FieldMolder[fmId.length];
        for ( int i=0; i<_ids.length; i++ ) {
            _ids[i] = new FieldMolder( ds, this, fmId[i] );
        }

        FieldMapping[] fmFields = getFullFields( clsMap );
        _fhs = new FieldMolder[fmFields.length];
        for ( int i=0; i<_fhs.length; i++ ) {
            if ( fmFields[i].getSql() != null && fmFields[i].getSql().getManyTable() != null ) {
                String manyTable = null; 
                String[] idSQL = null;
                int[] idType = null;
                String[] relatedIdSQL = null;
                int[] relatedIdType = null;

                manyTable = fmFields[i].getSql().getManyTable();

                idSQL = new String[fmId.length];
                idType = new int[fmId.length];
                FieldDescriptor[] fd = ((ClassDescriptorImpl)clsDesc).getIdentities();
                for ( int j=0; j < fmId.length; j++ ) {
                    idSQL[j] = fmId[j].getSql().getName();
                    
                    if ( fd[j] instanceof JDOFieldDescriptor ) {
                        idType[j] = ((JDOFieldDescriptor)fd[j]).getSQLType();
                    } else {
                        throw new MappingException("Identity type must contains sql information: "+ _name );
                    }
                }

                relatedIdSQL = null;
                String relatedType = fmFields[i].getType();
                ClassDescriptor relDesc = loader.getDescriptor( ds.resolve( relatedType ) );
                if ( relDesc instanceof JDOClassDescriptor ) {
                    FieldDescriptor[] relatedIds = ((JDOClassDescriptor)relDesc).getIdentities();
                    relatedIdSQL = new String[relatedIds.length];
                    relatedIdType = new int[relatedIds.length];
                    for ( int j=0; j < relatedIdSQL.length; j++ ) {
                        if ( relatedIds[j] instanceof JDOFieldDescriptor ) {
                            relatedIdSQL[j] = ((JDOFieldDescriptor)relatedIds[j]).getSQLName();
                            relatedIdType[j] = ((JDOFieldDescriptor)relatedIds[j]).getSQLType();
                        } else {
                            throw new MappingException("Field type is not persistence-capable: "+ relatedIds[j].getFieldName() );
                        }
                    }
                }

                // if many-key exist, idSQL is overridden
                String manyKey = fmFields[i].getSql().getManyKey();
                String[] keys = breakApart( manyKey, ' ' );
                if ( keys != null && keys.length != 0 ) {
                    if ( keys.length != idSQL.length ) 
                        throw new MappingException("The number of many-keys doesn't match referred object: "+clsDesc.getJavaClass().getName());
                    idSQL = keys;
                }

                // if name="" exist, relatedIdSQL is overridden
                String manyName = fmFields[i].getSql().getName();
                String[] names = breakApart( manyName, ' ' );
                if ( names != null && names.length != 0 ) {
                    if ( names.length != relatedIdSQL.length ) 
                        throw new MappingException("The number of many-keys doesn't match referred object: "+relDesc.getJavaClass().getName());
                    relatedIdSQL = names;
                }

                _fhs[i] = new FieldMolder( ds, this, fmFields[i], manyTable, idSQL, idType, relatedIdSQL, relatedIdType ); 
            } else {
                _fhs[i] = new FieldMolder( ds, this, fmFields[i] ); 
            }
        }

        if ( Persistent.class.isAssignableFrom( _base ) )
            _callback = new JDOCallback();
    }
    /**
     * Break a string into array of substring which serparated 
     * by a delimitator
     */
    private String[] breakApart( String strings, char delimit ) {
        if ( strings == null ) 
            return new String[0];
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
    /* 
     * Get the all the id fields of a class
     * If the class, C, is a dependent class, then
     * the depended class', D, id fields will be 
     * appended at the back and returned.
     * If the class is an extended class, the id
     * fields of the extended class will be returned.
     */
    private FieldMapping[] getIdFields( ClassMapping clsMap ) 
            throws MappingException {
        ClassMapping base;
        FieldMapping[] fmDepended;
        FieldMapping[] fmResult;
        FieldMapping[] fmBase;
        FieldMapping[] fmIds;
        String[] identities;
        boolean idfield;

        // start with the extended class
        base = clsMap;
        while ( base.getExtends() != null ) {
            base = (ClassMapping) base.getExtends();
        }
        fmDepended = null; 
        /*
        if ( base.getDepends() != null ) {
            fmDepended = getIdFields( (ClassMapping) base.getDepends() );
        }*/

        identities = breakApart( base.getIdentity(), ' ' );
       
        if ( identities == null || identities.length == 0 )
            throw new MappingException("Identity is null!");


        fmIds = new FieldMapping[identities.length];
        fmBase = base.getFieldMapping();
        for ( int i=0,j=0; i<fmBase.length; i++ ) {
            idfield = false;
            IDSEARCH:
            for ( int k=0; k<identities.length; k++ ) {
                if ( fmBase[i].getName().equals( identities[k] ) ) {
                    idfield = true;
                    break IDSEARCH;
                }
            }
            if ( idfield ) {
                fmIds[j] = fmBase[i];
                j++;
            }
        }
        if ( fmDepended == null ) 
            return fmIds;

        // join depend ids and class id
        fmResult = new FieldMapping[fmDepended.length + identities.length];
        System.arraycopy( fmIds, 0, fmResult, 0, fmIds.length );
        System.arraycopy( fmDepended, 0 , fmResult, fmIds.length, fmDepended.length );
        return fmIds;
    }
    /*
     * Get all the field mapping, including all the field
     * in extended class, but id fields
     */
    private FieldMapping[] getFullFields( ClassMapping clsMap ) 
            throws MappingException {
        FieldMapping[] extendFields;
        FieldMapping[] thisFields;
        FieldMapping[] fields = null;
        String[] identities;
        boolean idfield;
        ClassMapping extend = (ClassMapping) clsMap.getExtends();


        if ( extend != null ) {
            extendFields = getFullFields( extend );
            thisFields = clsMap.getFieldMapping();

            fields = new FieldMapping[extendFields.length+thisFields.length];
            System.arraycopy( extendFields, 0, fields, 0, extendFields.length );
            System.arraycopy( thisFields, 0, fields, extendFields.length, thisFields.length );
        } else {
            identities = breakApart( clsMap.getIdentity(), ' ' );
            if ( identities == null || identities.length == 0 )
                throw new MappingException("Identity is null!");
            
            // return array of fieldmapping without the id field
            thisFields = clsMap.getFieldMapping();
            fields = new FieldMapping[thisFields.length-identities.length];

            for ( int i=0,j=0; i<thisFields.length; i++ ) {
                idfield = false;
                IDSEARCH:
                for ( int k=0; k<identities.length; k++ ) {
                    if ( thisFields[i].getName().equals( identities[k] ) ) {
                        idfield = true;
                        break IDSEARCH;
                    }
                }
                if ( !idfield ) {
                    fields[j] = thisFields[i];
                    j++;
                }
            }

        }        
        return fields;
    }

    public static Vector resolve( MappingLoader loader, LockEngine lock, 
            PersistenceFactory factory, LogInterceptor logInterceptor )
            throws MappingException, ClassNotFoundException {

        Vector result = new Vector();
        Enumeration enum;
        ClassMolder mold;
        ClassMapping map;
        Persistence persist;
        ClassDescriptor desc;

        DatingService ds = new DatingService( ClassLoader.getSystemClassLoader() );
        enum = loader.listRelations();
        while ( enum.hasMoreElements() ) {
            ds.registerRelation( (RelationDescriptor) enum.nextElement() );
        }

        enum = loader.listJavaClasses();
        while ( enum.hasMoreElements() ) {
            desc = (ClassDescriptor) loader.getDescriptor((Class)enum.nextElement());
            persist = factory.getPersistence( desc, logInterceptor );
            mold = new ClassMolder( ds, loader, lock, desc, persist );
            result.add( mold );
        }
        ds.close();
        return result;
    }

    //
    // Methods of LockEngine, which called by TransactionContext
    //
    public OID load( TransactionContext tx, OID oid, DepositBox locker, Object object, AccessMode accessMode )
            throws ObjectNotFoundException, PersistenceException {       
        System.out.println("ClassMolder.load(): Oid: "+oid);

        Connection conn;
        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        PersistenceInfo info;
        Object[] fields;
        Object[] ids;
        AccessMode am;
        Object value;
        Object stamp;        
        Object[] temp;
        int fieldType;

        if ( oid.isIdsNull() ) 
            throw new PersistenceException("The identities of the object to be loaded is null");
            
        fields = (Object[]) locker.getObject( tx );
        if ( fields == null ) {
            fields = new Object[_fhs.length];
            conn = (Connection)tx.getConnection(oid.getLockEngine());
            stamp = _persistence.load( conn, fields, oid.getIdentities(), accessMode );
            oid.setDbLock( accessMode == AccessMode.DbLocked );
            //try {
                locker.setObject( tx, fields );
            //} catch ( LockNotGrantedException e ) {
                // the chance it happens is one in zillions:
                // it happens only if two transaction both try to load
                // an object which isn't in cache, and also the one of 
                // the transaction "luckly" preempted between acquire 
                // and setObject, and also the second transaction
                // already acquire and read lock.
                // transaction finish.
            //  try {
            //      fields = locker.getObject( tx );
            //  } catch ( LockNotGrantedException e ) {
            //        
            //  }
            //}
        }

        ids = oid.getIdentities();
        for ( int i=0; i<_ids.length; i++ ) {
            System.out.println("Ids.length: "+ids.length);
            if ( ids[i] == null )
                throw new PersistenceException( "One of the identities is null!" );
            _ids[i].setValue( object, ids[i] );
        }

        for ( int i=0; i<_fhs.length; i++ ) {
            System.out.print("<"+i+":"+(fields[i] instanceof Object[]?OID.flatten((Object[])fields[i]):fields[i])+" of type: "+_fhs[i].getJavaClass()+">  ");
        }
        System.out.println("  is loaded!");
    
        for ( int i = 0; i < _fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                temp = (Object[]) fields[i];
                if ( temp[0] != null ) 
                    _fhs[i].setValue( object, temp[0] );
                else
                    _fhs[i].setValue( object, null );
                break;

            case FieldMolder.PERSISTANCECAPABLE:
                if ( _fhs[i].isLazy() )
                    System.err.println( "Warning: Lazy loading of object is not yet support!" );

                // depedent class won't have persistenceInfo in LockEngine
                // must look at fieldMolder for it
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();

                if ( !OID.isIdsNull( (Object[])fields[i] ) ) {
                    // convert 
                    value = tx.load( fieldEngine, fieldClassMolder, (Object[])fields[i], null );
                    _fhs[i].setValue( object, value );
                } else {
                    _fhs[i].setValue( object, null );
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                if ( !_fhs[i].isLazy() ) {
                    ArrayList col = new ArrayList();
                    //(ArrayList)_fhs[i].getCollectionType().newInstance();
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();

                    ArrayVector v = (ArrayVector)fields[i];
                    if ( v != null ) {
                        for ( int j=0,l=v.size(); j<l; j++ ) {
                            //System.out.println("LockEninge: "+oid.getLockEngine()+" Object: "+v.elementAt(j));
                            col.add( tx.load( oid.getLockEngine(), fieldClassMolder, (Object[])v.get(j), null ) );
                        }
                        _fhs[i].setValue( object, col );
                    } else {
                        _fhs[i].setValue( object, null );
                    }
                } else {
                    ArrayVector list = (ArrayVector) fields[i];
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();
                    RelationCollection relcol = new RelationCollection( tx, oid, fieldEngine, fieldClassMolder, null, list );
                    _fhs[i].setValue( object, relcol );
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                if ( !_fhs[i].isLazy() ) {
                    ArrayList col = new ArrayList();
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();

                    ArrayVector v = (ArrayVector)fields[i];
                    if ( v != null ) {                        
                        for ( int j=0,l=v.size(); j<l; j++ ) {
                            col.add( tx.load( oid.getLockEngine(), fieldClassMolder, (Object[])v.get(j), null ) );
                        }
                        _fhs[i].setValue( object, col );
                    }
                } else {
                    ArrayVector list = (ArrayVector) fields[i];
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();
                    RelationCollection relcol = new RelationCollection( tx, oid, fieldEngine, fieldClassMolder, null, list );
                    _fhs[i].setValue( object, relcol );
                }
                break;
            default:
                throw new PersistenceException("Unexpected field type!");
            }
        }

        return oid;
    }

    public ObjectBin fetch( TransactionContext tx, PersistenceQuery pq, OID oid, AccessMode accessMode )
            throws ObjectNotFoundException, PersistenceException {
        // seem like we don't need fetch anymore, queryresult now call 
        // transactionContext.load instead of calling classmolder itself
        // same as load, exception call pq.fetch, in place of load
        throw new RuntimeException("ObjectBin.fetch() is not implemented!");        
    }

    public Object[] create( TransactionContext tx, OID oid, DepositBox locker, Object object )
            throws DuplicateIdentityException, PersistenceException {

        System.out.println("ClassMolder.create(): Oid: "+oid);

        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Iterator itor;
        Object fetchedObject;
        ArrayVector fids;
        Object key;
        Object[] fields;
        Object[] fid;
        Object[] createdId;
        Object[] ids;
        Object[] oids;
        Object[] newids;
        Object[] temp;
        Object o;

        int fieldType;

        if ( _persistence == null )
            throw new PersistenceException("non persistence capable: "+oid.getJavaClass());        

        fields = new Object[_fhs.length];
        ids = oid.getIdentities();
        oids = new Object[ids.length];
        System.arraycopy( ids, 0, oids, 0, ids.length );

        if ( !OID.isEquals( ids, getIdentities(object) ) )
            throw new PersistenceException("Object identity change is not allowed! original id: "+OID.flatten(ids)+" changed id: "+OID.flatten(getIdentities(object)));

        // copy the object to cache should make a new field now,
        for ( int i=0; i<_fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();

            switch (fieldType) {
            case FieldMolder.PRIMITIVE: // primitive includes int, float, Date, Time etc
                fields[i] = new Object[] { _fhs[i].getValue( object ) };
                break;

            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fid = fieldClassMolder.getIdentities( o );
                    if ( fid != null ) {
                        fields[i] = fid;
                    }
                }
                break;

            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fids = getIds( fieldClassMolder, o );
                    fields[i] = fids;
                }
                break;

            case FieldMolder.MANY_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fids = getIds( fieldClassMolder, o );
                    fields[i] = fids;
                }
                break;

            default:
                throw new IllegalArgumentException("Field type invalid!");
            }
        }

        createdId = _persistence.create( (Connection)tx.getConnection(oid.getLockEngine()), 
                fields, ids );
        locker.setObject( tx, fields );
        oid.setDbLock( true );

        for ( int i=0; i<_ids.length; i++ ) {
            _ids[i].setValue( object, createdId[i] );        
        }
        for ( int i=0; i<_fhs.length; i++ ) {
            System.out.print("<"+i+":"+(fields[i] instanceof Object[]?OID.flatten((Object[])fields[i]):fields[i])+" of type: "+_fhs[i].getJavaClass()+">  ");
        }
        System.out.println(" will be created");

        if ( createdId == null || createdId[0] == null )
            throw new PersistenceException("Identity can't be created!");

        // ================================================
        for ( int i=0; i<_fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    // need multi-pk
                    if ( _fhs[i].isDependent() ) {
                        if ( !tx.isPersistent( o ) ) 
                            tx.create( fieldEngine, fieldClassMolder, o, oid );
                        else 
                            // fail-fast principle: if the object depend on another object,
                            // throw exception
                            if ( !tx.isDepended( oid, o ) )
                                throw new PersistenceException("Dependent object may not change its master. Object: "+o+" new master: "+oid);
                    } else {
                        //if ( !tx.isPersistent( o ) ) 
                        //    tx.create( fieldEngine, fieldClassMolder, o, null );
                    }
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                itor = getIterator( o );
                if ( o != null ) {
                    itor = getIterator( o );
                    while (itor.hasNext()) {
                        Object oo = itor.next();
                        if ( _fhs[i].isDependent() ) {
                            if ( !tx.isPersistent( oo ) ) {
                                tx.create( fieldEngine, fieldClassMolder, oo, oid );
                            } else 
                                // fail-fast principle: if the object depend on another object,
                                // throw exception
                                if ( !tx.isDepended( oid, oo ) )
                                    throw new PersistenceException("Dependent object may not change its master");
                        } else {
                            //if ( !tx.isPersistent( oo ) ) 
                            //    tx.create( fieldEngine, fieldClassMolder, oo, null );
                        }
                    }
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    itor = getIterator( o );
                    // many-to-many relation is never dependent relation
                    while (itor.hasNext()) {
                        Object oo = itor.next();
                        if ( !tx.isPersistent( oo ) ) {
                            System.out.println("Object Does not Exist!");
                            tx.create( fieldEngine, fieldClassMolder, oo, null );
                        } else {
                            System.out.println("Object already created and will not be recreated!");
                        }
                        // create the relation in relation table too
                        _fhs[i].getRelationLoader().createRelation( 
                        (Connection)tx.getConnection(oid.getLockEngine()), 
                        oid.getIdentities(), fieldClassMolder.getIdentities(oo) );
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Field type invalid!");
            }
        }

        if ( createdId != ids ) 
            return createdId;
        else 
            return null;
    }

    public boolean store( TransactionContext tx, OID oid, DepositBox locker, Object object )
            throws DuplicateIdentityException, PersistenceException,
            ObjectModifiedException, ObjectDeletedException {

        // if cached.id != object.id, rise exception
        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Object[] newfields;
        Object[] dependIds;
        Object[] fields;
        Iterator itor;
        ArrayVector list;
        ArrayVector orgFields;
        Object newobj;
        Object oldobj;
        Object fetched;
        Object[] fids;
        Object[] newids;
        Object[] ids;
        Object[] ffid;
        Object o;
        int fieldType;
        boolean modified;
        boolean lockrequired;

        if ( oid.isIdsNull() ) 
            throw new PersistenceException("The identities of the object to be stored is null");

        if ( !OID.isEquals( oid.getIdentities(), getIdentities( object ) ) ) 
            throw new PersistenceException("Identities changes is not allowed!");

        fields = (Object[]) locker.getObject( tx );

        if ( fields == null ) 
            throw new PersistenceException("Object, "+oid+",  isn't loaded in the persistence storage!");


        newfields = new Object[_fhs.length];

        if ( getCallback() != null ) {
            // Preliminary check for modifications - needed for jdoStore()
            // To make it faster we will check only PRIMITIVE and PERSISTANCECAPABLE
            modified = false;
            for ( int i=0; i<newfields.length; i++ ) {
                fieldType = _fhs[i].getFieldType();
                if ( fieldType == FieldMolder.PRIMITIVE ||
                     fieldType == FieldMolder.PERSISTANCECAPABLE ) {
                    if ( fieldType == FieldMolder.PRIMITIVE ) {
                        Object[] temp = new Object[1];
                        temp[0] = _fhs[i].getValue( object );
                        newfields[i] = temp;
                    } else {
                        o = _fhs[i].getValue( object );
                        if ( o != null ) 
                            newfields[i] = _fhs[i].getFieldClassMolder().getIdentities( o );
                    }
                    if ( !OID.isEquals( (Object[])fields[i], (Object[])newfields[i] ) ) {
                        modified = true;
                        break;
                    }
                }
            }
            try {
                getCallback().storing( object, modified );
            } catch ( Exception except ) {
                throw new PersistenceException( except.getMessage(), except );
            }
        }
        
        ids = oid.getIdentities();

        newfields = new Object[_fhs.length];
        modified = false;
        lockrequired = false;

        for ( int i=0; i<newfields.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                Object[] temp = new Object[] {_fhs[i].getValue( object )};
                newfields[i] = temp;

                if ( !OID.isEquals( (Object[])fields[i], temp ) ) {
                    if ( _fhs[i].isCheckDirty() ) {
                        modified = true;
                        lockrequired = true;
                    } else {
                        modified = true;
                    }
                }
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) 
                    newfields[i] = fieldClassMolder.getIdentities( o );

                if ( !OID.isEquals( (Object[])fields[i], (Object[])newfields[i] ) ) {
                    if ( _fhs[i].isStored() ) {
                        if ( _fhs[i].isCheckDirty() ) {
                            modified = true;
                            lockrequired = true;
                        } else {
                            modified = true;
                        }
                    }

                    if ( o != null ) {
                        if ( _fhs[i].isDependent() ) {
                            if ( !OID.isEquals( fields[i], newfields[i] ) ) {
                                if ( fields[i] != null ) { 
                                    Object reldel = tx.fetch( fieldEngine, fieldClassMolder, (Object[])fields[i], null );
                                    if ( reldel != null )
                                        tx.delete( reldel );
                                    else {
                                        // should i notify user that the object does not exist?
                                        // user can't delete dependent object himself. So, must
                                        // error.                                   
                                    }
                                        
                                }
                                if ( newfields[i] != null ) {
                                    if ( !tx.isPersistent( o ) ) {
                                        // should be created if transaction have no record of the object
                                        tx.create( fieldEngine, fieldClassMolder, o, oid );
                                    } else {
                                        // should i notify user that the object does not exist?
                                        // user can't create dependent object himself. So, must
                                        // error.                                   
                                    }
                                }
                            }
                        } else {
                            // need to do anything for non-dependent object?
                            // it seem not. should list all the case and make sure
                        }
                    }
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                orgFields = (ArrayVector)fields[i];
                if ( ! (o instanceof Lazy) ) {
                    itor = getIterator( o );
                    ArrayList v = (ArrayList) o;
                    list = new ArrayVector( v.size() );
                    for ( int j=0; j<v.size(); j++ ) {
                        list.add( (fieldClassMolder.getIdentities( v.get(j) )) );
                    }
                    if ( !OID.isEquals( (ArrayVector)fields[i], list ) ) {
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }

                        if ( _fhs[i].isDependent() ) {
                            if ( orgFields != null && list != null ) {
                                for ( int j=0; j<orgFields.size(); j++ ) {
                                    if ( !list.contains( orgFields.get(j) ) ) {
                                        Object reldel = tx.fetch( fieldEngine, fieldClassMolder, (Object[])orgFields.get(j), null );
                                        if ( reldel != null ) {
                                            tx.delete( reldel );
                                        } else {
                                            // should i notify user that the object does not exist?
                                            // user can't delete dependent object himself. So, must
                                            // error.
                                        }
                                    }
                                }
                                // add relation which added after it's created or loaded
                                for ( int j=0; j<list.size(); j++ ) {
                                    if ( !orgFields.contains( list.get(j) ) ) {
                                        if ( !tx.isPersistent( v.get(j) ) ) 
                                            tx.create( fieldEngine, fieldClassMolder, v.get(j), oid );
                                        else {
                                            // should i notify user that the object does not exist?
                                            // user can't create dependent object himself. So, must
                                            // error.                                   
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    RelationCollection lazy = (RelationCollection) o;
                    ArrayList deleted = lazy.getDeleted();
                    if ( deleted != null ) {
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }

                        if ( _fhs[i].isDependent() ) {
                            itor = deleted.iterator();
                            while ( itor.hasNext() ) {
                                Object toBeDeleted = lazy.find( (Object[])itor.next() );
                                if ( toBeDeleted != null && tx.isPersistent( toBeDeleted ) ) {
                                    tx.delete( toBeDeleted );
                                } else { 
                                    System.out.println("Object to be delete is not found!");
                                    // what to do if it happens?
                                }
                            }
                        }
                    }

                    ArrayList added = lazy.getAdded();
                    if ( added != null ) {
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }

                        if ( _fhs[i].isDependent() ) {
                            itor = added.iterator();
                            while ( itor.hasNext() ) {
                                Object toBeAdded = lazy.find( (Object[])itor.next() );
                                if ( toBeAdded != null ) {
                                    tx.create( fieldEngine, fieldClassMolder, toBeAdded, oid );
                                } else {
                                    // what to do if it happens?
                                    System.out.println("Object to be added is not found!");
                                }
                            }
                        }
                    }

                }
                break;
            case FieldMolder.MANY_TO_MANY:

                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( ! (o instanceof Lazy) ) {
                    itor = getIterator( o );
                    ArrayList v = (ArrayList) o;
                    list = getIds( fieldClassMolder, o );
                    orgFields = (ArrayVector)fields[i];
                    if ( !OID.isEquals( orgFields, list ) ) {

                        itor = getIterator( o );
                        while ( o != null && itor.hasNext() ) {
                            newobj = itor.next();
                            fids = fieldClassMolder.getIdentities( newobj );

                            if ( !tx.isPersistent( newobj ) ) {
                                // should be created if transaction have no record of the object
                                // (will not be dependent, so don't create it)
                                //if ( _fhs[i].isDependent() ) {
                                //    tx.create( fieldEngine, fieldClassMolder, newobj, oid );
                                //}
                                // create the relation in relation table too
                                _fhs[i].getRelationLoader().createRelation( 
                                (Connection)tx.getConnection(oid.getLockEngine()), 
                                oid.getIdentities(), fieldClassMolder.getIdentities(newobj) );
                            }
                        }

                        // need to add support for add and delete relation
                        // delete relation which no long exist
                        if ( orgFields != null && list != null ) {
                            for ( int j=0; j<orgFields.size(); j++ ) {
                                if ( !list.contains( orgFields.get(j) ) ) {
                                    Object reldel = tx.load( fieldEngine, fieldClassMolder, (Object[])orgFields.get(j), null );
                                    if ( reldel != null ) {
                                        tx.writeLock( reldel, tx.getLockTimeout() );
                                     
                                        _fhs[i].getRelationLoader().deleteRelation( 
                                        (Connection)tx.getConnection(oid.getLockEngine()), 
                                        oid.getIdentities(), (Object[])orgFields.get(j) );
                                    } else {
                                        // the object not there, and we try to delete the rubbish relation,
                                        // if there is
                                        _fhs[i].getRelationLoader().deleteRelation( 
                                        (Connection)tx.getConnection(oid.getLockEngine()), 
                                        oid.getIdentities(), (Object[])orgFields.get(j) );
                                    }
                                }
                            }
                            // add relation which added after it's created or loaded
                            for ( int j=0; j<list.size(); j++ ) {
                                if ( !orgFields.contains( list.get(j) ) ) {
                                    Object reladd = tx.load( fieldEngine, fieldClassMolder, (Object[])list.get(j), null );
                                    if ( reladd != null ) {
                                        tx.writeLock( reladd, tx.getLockTimeout() );
                                     
                                        _fhs[i].getRelationLoader().createRelation( 
                                        (Connection)tx.getConnection(oid.getLockEngine()), 
                                        oid.getIdentities(), (Object[])orgFields.get(j) );
                                    } else {
                                        // ignored if object not found, if later in transaction 
                                        // the other side of object is added. then, the relation 
                                        // will be added if the other side of object is just 
                                        // deleted in this transaction, then it seem to be an 
                                        // non-critical error ignore it seem to better than annoy 
                                        // user
                                    }
                                }
                            }
                        }
                    }
                } else {
                    RelationCollection lazy = (RelationCollection) o;
                    ArrayList deleted = lazy.getDeleted();
                    if ( deleted != null ) {
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }

                        itor = deleted.iterator();
                        while ( itor.hasNext() ) {
                            Object[] deletedId = (Object[])itor.next();
                            Object toBeDeleted = lazy.find( deletedId );
                            if ( toBeDeleted != null && !tx.isPersistent(toBeDeleted) ) {
                                tx.writeLock( toBeDeleted, 0 );

                                _fhs[i].getRelationLoader().deleteRelation( 
                                (Connection)tx.getConnection(oid.getLockEngine()), 
                                oid.getIdentities(), deletedId );
                            } else { 
                                // what to do if it happens?
                            }
                        }
                    }

                    ArrayList added = lazy.getAdded();
                    if ( added != null ) {
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }

                        itor = added.iterator();
                        while ( itor.hasNext() ) {
                            Object[] addedId = (Object[])itor.next();
                            Object toBeAdded = lazy.find( addedId );
                            if ( toBeAdded != null ) {
                                _fhs[i].getRelationLoader().createRelation( 
                                (Connection)tx.getConnection(oid.getLockEngine()), 
                                oid.getIdentities(), addedId );
                            } else {
                                // what to do if it happens?
                            }
                        }
                    }
                }
                break;
            default:
            }
        }
 

        if ( lockrequired ) {
            tx.writeLock( object, tx.getTransactionTimeout() );
        }

        if ( modified ) {
            System.out.println("object is modifed, now storing it");
            for ( int i=0; newfields!=null && i<newfields.length; i++ ) {
                System.out.print("<"+i+":"+(newfields[i] instanceof Object[]?OID.flatten((Object[])newfields[i]):newfields[i])+" of "+_fhs[i].getJavaClass()+">  ");
            }
            System.out.println("     new field in object");

            for ( int i=0; fields!=null && i<fields.length; i++ ) {
                    System.out.print("<"+i+":"+(fields[i] instanceof Object[]?OID.flatten((Object[])fields[i]):fields[i])+">  ");
            }
            System.out.println("       in cache");
            
            Object stamp = _persistence.store( tx.getConnection(oid.getLockEngine()),
                newfields, oid.getIdentities(), fields, oid.getStamp() );
            oid.setStamp( stamp );
            return true;
        } else {
            System.out.println("object not modifed!");
            return false;
        }
        
        // checkValidity
        // call store of each fieldMolder
        // update oid and setStamp
    }

    public void update( TransactionContext tx, OID oid, DepositBox locker, Object object, AccessMode accessMode )
        throws PersistenceException, ObjectModifiedException {

        System.out.println("ClassMolder.update(): Oid: "+oid);

        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        PersistenceInfo info;
        ArrayVector list;
        ArrayVector orgFields;
        Iterator itor;
        Object[] fields;
        Object[] ids;
        AccessMode am;
        Object value;
        Object stamp;        
        Object[] temp;
        int fieldType;
        long timestamp;
        Object o;


        fields = (Object[]) locker.getObject( tx );

        if ( fields == null ) 
            throw new ObjectModifiedException("Object is cleared from cache.");

        timestamp = locker.getTimeStamp();

        ids = oid.getIdentities();

        // If the object implements TimeStampable interface, verify
        // the object's timestamp against the cache
        if ( object instanceof TimeStampable ) {
            TimeStampable ts = (TimeStampable) object;
            if ( ts.jdoGetTimeStamp() != timestamp ) {
                throw new ObjectModifiedException( Messages.format( "persist.objectModified", object.getClass(), 
                                                    OID.flatten( ids ) ) );
            }
        }

        for ( int i=0; i<_fhs.length; i++ ) {
            System.out.print("<"+i+":"+(fields[i] instanceof Object[]?OID.flatten((Object[])fields[i]):fields[i])+" of type: "+(_fhs[i]==null?null:_fhs[i].getJavaClass())+">  ");
        }
        System.out.println("  is updated!");
        
        for ( int i=0; i<_fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    // need multi-pk
                    if ( _fhs[i].isDependent() ) {
                        if ( !tx.isPersistent( o ) ) 
                            tx.update( fieldEngine, fieldClassMolder, o);
                        else 
                            // fail-fast principle: if the object depend on another object,
                            // throw exception
                            if ( !tx.isDepended( oid, o ) )
                                throw new PersistenceException("Dependent object may not change its master. Object: "+o+" new master: "+oid);
                    } else {
                        //if ( !tx.isPersistent( o ) ) 
                        //    tx.create( fieldEngine, fieldClassMolder, o, null );
                    }
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                orgFields = (ArrayVector)fields[i];
                if ( ! (o instanceof Lazy) ) {
                    itor = getIterator( o );
                    ArrayList v = (ArrayList) o;
                    list = new ArrayVector( v.size() );
                    for ( int j=0; j<v.size(); j++ ) {
                        list.add( (fieldClassMolder.getIdentities( v.get(j) )) );
                    }
                    if ( !OID.isEquals( (ArrayVector)fields[i], list ) ) {
                        if ( _fhs[i].isDependent() ) {
                            if ( orgFields != null && list != null ) {
                                // make sure that all dependent objects are included in the transaction,
                                // otherwise objects that should be deleted won't be deleted
                                for ( int j=0; j<orgFields.size(); j++ ) {
                                    if ( !list.contains( orgFields.get(j) ) ) {
                                        tx.load( fieldEngine, fieldClassMolder, (Object[])orgFields.get(j), null );
                                    }
                                }
                                // update all dependent objects
                                for ( int j=0; j<list.size(); j++ ) {
                                    tx.update( fieldEngine, fieldClassMolder, v.get(j));
                                }
                            }
                        }
                    }
                } else {
                    // I guess that lazy loading cannot be used together with 
                    // long transactions
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                break;
            default:
                throw new IllegalArgumentException("Field type invalid!");
            }
        }
    }

    public void updateCache( TransactionContext tx, OID oid, DepositBox locker, Object object )
                        throws DuplicateIdentityException, PersistenceException {
        // similar to store, no recusrion is needed
        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Iterator itor;
        Object newid;
        ArrayList fids;
        Object key;
        Object[] fid;
        Object[] fields;
        Object id;
        Object o;
        
        int fieldType;

        fields = (Object[])locker.getObject( tx );

        if ( oid.isIdsNull() ) 
            throw new PersistenceException("The identities of the cache to be updated is null");

        for ( int i=0; i < _fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                // should give some attemp to reduce new object array
                Object[] temp = new Object[] {_fhs[i].getValue( object )};
                if ( temp[0] != null ) {
                    fields[i] = temp;
                } else {
                    fields[i] = null;
                }
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fid = fieldClassMolder.getIdentities( o );
                    fields[i] = fid[0];
                } else {
                    fields[i] = null;
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    if ( !( o instanceof Lazy) ) {
                        ArrayList list = (ArrayList) o;
                        ArrayVector fidlist = new ArrayVector( list.size() );
                        for ( int j=0; j<list.size(); j++ ) {
                            fidlist.add( fieldClassMolder.getIdentities( list.get(j) ) );
                        }
                        fields[i] = fidlist;
                    } else {
                        RelationCollection lazy = (RelationCollection) o;
                        fids = (ArrayList)lazy.getIdentitiesList().clone();
                        fields[i] = fids;
                    }
                } else {
                    fields[i] = null;
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    if ( !( o instanceof Lazy ) ) {
                        ArrayList list = (ArrayList) o;
                        ArrayVector fidlist = new ArrayVector( list.size() );
                        for ( int j=0; j<list.size(); j++ ) {
                            fidlist.add( fieldClassMolder.getIdentities( list.get(j) ) );
                        }
                        fields[i] = fidlist;
                    } else {
                        RelationCollection lazy = (RelationCollection) o;
                        fids = (ArrayList)lazy.getIdentitiesList().clone();
                        fields[i] = fids;
                    }
                } else {
                    fields[i] = null;
                }
                break;
            default:
                throw new IllegalArgumentException("Field type invalid!");
            }
        }
    }

    private static void deleteExtend( TransactionContext tx, ClassMolder extend, Object[] identities ) 
            throws ObjectNotFoundException, PersistenceException {
        // if the extend field contains dependent of field type,
        // fields must be loaded, so that we get the foreign key
        // of the depedent table. We may get no result. And, it 
        // is good and we don't have to process further.
        // it will be cheaper if we go directly to SQL and load
        // only the foreign field needed. But, it require extra
        // method in Persistent SPI. So, maybe we rather stay 
        // with the expensive appoarch.

        // if there is any depedent field of reference type, the simplest way is
        // to load the dependent objects and delete it using tansaction. 
        // (Will ObjectNotFound throws in the loading??)        

        // if the extend field has many-to-many field, we must delete the 
        // relation from he relation table.
        Object[] persistFields = null;
        for ( int i=0; i < extend._fhs.length; i++ ) {
            if ( extend._fhs[i].isDependent() ) {
                try {
                    if ( persistFields == null ) {

                        persistFields = new Object[extend._fhs.length];
                        extend._persistence.load( (Connection)tx.getConnection(extend._engine), 
                        persistFields, identities, AccessMode.ReadOnly );
                    }

                    if ( extend._fhs[i].isMulti() ) {
                        ArrayVector listOfIds = (ArrayVector)persistFields[i];
                        for ( int j=0; i < listOfIds.size(); j++ ) {
                            extend._persistence.delete( tx.getConnection(extend._fhs[i].getFieldLockEngine()),
                            (Object[])listOfIds.get(j) );
                        }
                    }
                } catch ( ObjectNotFoundException e ) {
                }
            } else if ( extend._fhs[i].isManyToMany() ) {
                extend._fhs[i].getRelationLoader().deleteRelation( 
                (Connection)tx.getConnection(extend._fhs[i].getFieldLockEngine()), 
                identities );
            }
        }
    }

    public void delete( TransactionContext tx, OID oid ) 
            throws PersistenceException {

        Object[] ids = oid.getIdentities();
        _persistence.delete( (Connection)tx.getConnection(oid.getLockEngine()), ids );

        // All field along the extend path will be deleted by transaction
        // However, everything off the path must be deleted by ClassMolder.

        Vector extendPath = new Vector();
        ClassMolder base = this;
        while ( base != null ) {
            extendPath.add( base );
            base = base._extends;
        }

        base = _depends;
        while ( base != null ) {
            if ( base._extendent != null ) 
                for ( int i=0; i < base._extendent.size(); i++ ) 
                    if ( !extendPath.contains( base._extendent.get(i) ) ) {
                        //deleteExtend( tx, (ClassMolder)base._extendent.get(i), ids );
                    } else {
                    }

            base = base._extends;
        }

        if ( _extendent != null ) {
            for ( int i=0; i < _extendent.size(); i++ ) {
                if ( !extendPath.contains( _extendent.get(i) ) ) {
                    //deleteExtend( tx, (ClassMolder)_extendent.get(i), ids );
                }
            }
        }
    }

    public void markDelete( TransactionContext tx, OID oid, DepositBox locker, Object object )
            throws ObjectNotFoundException, PersistenceException {

        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Object[] fields;

        fields = (Object[])locker.getObject( tx );


        for ( int i=0; i < _fhs.length; i++ ) {
            int fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                if ( _fhs[i].isDependent() ) {
                    Object[] fid = (Object[]) fields[i];
                    Object fetched = null;
                    if ( fid != null ) {
                        fetched = tx.fetch( fieldEngine, fieldClassMolder, fid, null );
                        if ( fetched != null ) 
                            tx.delete( fetched );
                    }

                    Object fobject = _fhs[i].getValue( object );
                    if ( fobject != null && tx.isPersistent( fobject ) ) {
                        tx.delete( fobject );
                    }
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                if ( _fhs[i].isDependent() ) {
                    ArrayList alist = (ArrayList) fields[i];
                    for ( int j=0; j<alist.size(); j++ ) {
                        Object[] fid = (Object[]) alist.get(j);
                        Object fetched = null;
                        if ( fid != null ) {    
                            fetched = tx.fetch( fieldEngine, fieldClassMolder, fid, null );
                            if ( fetched != null ) 
                                tx.delete( fetched );
                        }
                    }

                    ArrayList blist = (ArrayList) _fhs[i].getValue( object );
                    for ( int j=0; j<blist.size(); j++ ) {
                        Object fobject = blist.get(j);
                        if ( fobject != null && tx.isPersistent( fobject ) ) {
                            tx.delete( fobject );
                        }
                    }
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                // delete the relation in relation table too
                _fhs[i].getRelationLoader().deleteRelation( 
                (Connection)tx.getConnection(oid.getLockEngine()), 
                oid.getIdentities() );

                break;
            default:
                throw new PersistenceException("Invalid field type!");
            }
        }
    }

    public void revertObject( TransactionContext tx, OID oid, DepositBox locker, Object object ) 
            throws PersistenceException {

        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        PersistenceInfo info;
        Object[] fields;
        AccessMode am;
        Object value;
        Object[] ids;
        
        int fieldType;

        if ( oid.isIdsNull() ) 
            throw new PersistenceException("The identities of the object to be revert is null");

        fields = (Object[]) locker.getObject( tx );

        ids = oid.getIdentities();
        if ( ids != null ) {
            for ( int i=0; i<ids.length; i++ ) {
                _ids[i].setValue( object, ids[i] );
            }
        }

        for ( int i=0; i < _fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                _fhs[i].setValue( object, fields[i] );
                break;
            case FieldMolder.PERSISTANCECAPABLE:

                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();

                if ( fields[i] != null ) {
                    // will load() work as we wanted if the the 
                    // persistenceCapable field was deleted in
                    // the transaction?
                    value = tx.load( fieldEngine, fieldClassMolder, (Object[]) fields[i], null );
                    _fhs[i].setValue( object, value );
                } else {
                    _fhs[i].setValue( object, null );
                }
                break;
            case FieldMolder.ONE_TO_MANY:
            case FieldMolder.MANY_TO_MANY:
                Object o = fields[i];
                if ( o == null ) {
                    _fhs[i].setValue( object, null );
                } else if ( !(o instanceof Lazy) ) {
                    ArrayList col = new ArrayList();
                    //(ArrayList)_fhs[i].getCollectionType().newInstance();
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();

                    ArrayVector v = (ArrayVector)fields[i];
                    for ( int j=0,l=v.size(); j<l; j++ ) {
                        col.add( tx.load( fieldEngine, fieldClassMolder, (Object[])v.get(j), null ) );
                    }
                    _fhs[i].setValue( object, col );
                } else {
                    ArrayVector list = (ArrayVector) fields[i];
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();
                    RelationCollection relcol = new RelationCollection( tx, oid, fieldEngine, fieldClassMolder, null, list );
                    _fhs[i].setValue( object, relcol );
                }
                break;
            default:
            }
        }
    }

    public void writeLock( TransactionContext tx, OID oid, DepositBox locker, Object object )
            throws PersistenceException {
        // call SQLEngine to lock an record
    }

    // ***********************
    // Methods of ClassHandler
    public Object newInstance() {
        try {
            return _base.newInstance();
        } catch ( IllegalAccessException e ) {
        } catch ( InstantiationException e ) {
        } catch ( ExceptionInInitializerError e ) {
        } catch ( SecurityException e ) {
        }
        return null;
    }

    public AccessMode getAccessMode( AccessMode txMode ) {

        if ( txMode == null )
            return _accessMode;

        if ( _accessMode == AccessMode.ReadOnly || txMode == AccessMode.ReadOnly )
            return AccessMode.ReadOnly;
        if ( _accessMode == AccessMode.DbLocked || txMode == AccessMode.DbLocked )
            return AccessMode.DbLocked;
        if ( _accessMode == AccessMode.Exclusive || txMode == AccessMode.Exclusive )
            return AccessMode.Exclusive;
        return txMode;

    }

    public CallbackInterceptor getCallback() {
        return _callback;
    }

    /**
     * @param o -- cannot be null
     */
    public Object[] getIdentities( Object o ) {
        Object[] osIds = new Object[_ids.length];
        for ( int i=0; i<osIds.length; i++ ) {
            osIds[i] = _ids[i].getValue( o );
        }
        return osIds;
    }

    public void setFieldsNull(Object o) {
    }

    public Persistence getPersistence() {
        return _persistence;
    }

    public void setPersistence( Persistence persist ) {
        _persistence = persist;
    }

    public int getSQLType() {
        return 0;
    }

    // Methods of ClassInfo
    public Class getJavaClass() {
        return _base;
    }

    public String getName() {
        return _name;
    }

    public FieldMolder[] getFields() {
        return _fhs;
    }

    public FieldMolder[] getIds() {
        return _ids;
    }

    public ClassMolder getExtends() {
        return _extends;
    }

    public ClassMolder getDepends() {
        return _depends;
    }
    
    public LockEngine getLockEngine() {
        return _engine;
    }
    public int getCacheType() {
        return _cachetype;
    }
    public int getCacheParam() {
        return _cacheparam;
    }
    public boolean isDependent() {
        return _depends != null;
    }

    void addExtendent( ClassMolder ext ) {
        if ( _extendent == null )
            _extendent = new Vector();
        _extendent.add( ext );
    }

    void addDependent( ClassMolder dep ) {
        if ( _dependent == null )
            _dependent = new Vector();
        _dependent.add( dep );
    }
    /*
     * Mutator method
     */
    void setExtends( ClassMolder ext ) {
        _extends = ext;
        ext.addExtendent( this );
    }

    /*
     * Mutator method
     */
    void setDepends( ClassMolder dep ) {
        _depends = dep;
        dep.addDependent( this );
    }
    private Iterator getIterator( Object o ) {
        if ( o instanceof Collection ) {
            return ((Collection) o).iterator();
        } else if ( o instanceof Vector ) {
            System.out.println("expect colleciton");
            return null;            
        } else {
            return null;
        }
    }
    /**
     *  Vector of Object[]
     */
    private ArrayVector getIds( ClassMolder molder, Object o ) {
        ArrayVector v;
        Vector vo;
        Iterator i;
        Collection c;
        Enumeration e;
        if ( o instanceof Collection ) {
            c = (Collection) o ;
            i = c.iterator();
            v = new ArrayVector( c.size() );
            while ( i.hasNext() ) {
                v.add( molder.getIdentities( i.next() ) );
            }
        } else if ( o instanceof Vector ) {
            vo = (Vector) o;
            e = vo.elements();
            v = new ArrayVector( vo.size() );
            while ( e.hasMoreElements() ) {
                v.add( molder.getIdentities( e.nextElement() ) );
            }
        } else {
            v = null;
            System.out.println("expecting collection");
        }
        return v;
    }
}


