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


import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.engine.SQLEngine;
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
import java.sql.Connection;
import java.util.Vector;
import java.util.ArrayList;

// New ToDo (Jul 26)
// -- DONE --put Identity back to make TC01 work
// -- DONE -- understand the way dependent object was fetch in
//      Query and make sure new architecture work for it
// -- DONE -- put AccessMode back
// -- fix fieldmolder's constructor. it need field type.


// DONE -- see if clsMap provides enough info
// DONE -- see how getHandler work and where the handler from in ClassDescription
// DONE -- write class Molder
// DONE -- think how RelationMapping to be done
//    i mean when a ClassMolder make a new sub fieldMolder, how
//    does it determine which type of field it is. we can use
//    data service to do it. but, beware, we may need to introduce
//    one more phase of ninePM() to resolve the complexities
//    -- note, it is now a boolean, if it is set, we know
//       the collection object will a relation collection, it is fine to treat as it
//       normal collection, just in creation time, we make a relationCollection using
//       EnclosingClassMolder.getClass() and FieldTypeClassMolder as parameter of
//       constructor, instead of normal collection
// DONE -- write constructor of field Molder
// DONE -- write dateservice
// go to cache engine, when iterate ClassMaping and
//         RelationMapping, put both to ........
// copy code from FieldHandler to FieldMolder

public class ClassMolder implements CacheHolder {

    class CacheItem {
        Object[] fields;
        boolean isHold;
        boolean isReleased;
    }

    Class _base;

    String _name;

    FieldMolder[] _ids;

    FieldMolder[] _fhs;
    
    ClassMolder _extends;

    ClassMolder _depends;

    AccessMode _accessMode;

    Persistence _persistence;

    LockEngine _engine;

    Hashtable _cache = new Hashtable();

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
                String idSQL = null;
                String relatedIdSQL = null;

                manyTable = fmFields[i].getSql().getManyTable();
                idSQL = null;

                if ( fmId.length == 1 ) { 
                    idSQL = fmId[0].getSql().getName();
                } else {
                    throw new MappingException("Many-to-Many relation only support single column pk");
                }

                relatedIdSQL = null;
                String relatedType = fmFields[i].getType();
                ClassDescriptor relDesc = loader.getDescriptor( ds.resolve( relatedType ) );
                if ( relDesc instanceof JDOClassDescriptor ) {
                    FieldDescriptor[] relatedIds = ((JDOClassDescriptor)relDesc).getIdentities();
                    if ( relatedIds != null && relatedIds.length == 1 
                            && (relatedIds[0] instanceof JDOFieldDescriptor) ) {
                        relatedIdSQL = ((JDOFieldDescriptor)relatedIds[0]).getSQLName();
                    } else {
                        throw new MappingException("Many-to-Many relation only support single column pk");
                    }
                }

                _fhs[i] = new FieldMolder( ds, this, fmFields[i], manyTable, idSQL, relatedIdSQL ); 
            } else {
                _fhs[i] = new FieldMolder( ds, this, fmFields[i] ); 
            }
        }
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
                    //System.out.println("--Field["+j+"] "+thisFields[i].getName());
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
    // Methods and fields of interface Cache
    //
    public void hold( OID oid ) {
        CacheItem ci = (CacheItem) _cache.get( oid );
        if ( ci == null )  {
            ci = new CacheItem();
            _cache.put( oid, ci );
        }
        ci.isHold = true;
        ci.isReleased = false;
    }

    public void release( OID oid ) {
        CacheItem ci = (CacheItem) _cache.remove( oid );
        if ( ci == null ) 
            System.out.println("=================Cache to be release doesn't exist!");
            //throw new IllegalStateException("Cache to be released doesn't exist!");
        ci.isHold = false;
        ci.isReleased = true;
    }


    public void destory( OID oid ) {
        CacheItem ci = (CacheItem) _cache.remove( oid );
        if ( ci == null ) 
            System.out.println("=================Cache to be destory doesn't exist!");
            //throw new IllegalStateException("Cache to be destory doesn't exist!");
        else
            ci.isHold = false;
    }

    //
    // Methods of LockEngine, which called by TransactionContext
    //
    public OID load( TransactionContext tx, OID oid, Object object, AccessMode accessMode )
            throws ObjectNotFoundException, PersistenceException {       
        System.out.println("ClassMolder.load(): Oid: "+oid);

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

        // assumption, only one transaction with the same oid will attemp load at
        // any given time.
        // if cache is not in hold state, rise exception
        CacheItem ci = (CacheItem) _cache.get( oid );
        if ( !ci.isHold )
            throw new PersistenceException( "Illegal Cache State!" );

        //oid.getMolder().setIdentity( object, oid.getIdentity() );
        // if the database is locked, return cache's copy
        //synchronized( ci ) {
            // wrong?? need to consider what if the fields changed in
            // persistence storage............
        if ( ci.fields == null || oid.isDbLock() == false 
                || accessMode == AccessMode.Exclusive || accessMode == AccessMode.DbLocked ) {
            fields = new Object[_fhs.length];
            //System.out.println("object loaded "+(accessMode == AccessMode.DbLocked? "with":"without")+ " database lock!");
            stamp = _persistence.load( (Connection)tx.getConnection(oid.getLockEngine()), 
                    fields, oid.getIdentities(), accessMode );
            oid.setDbLock( accessMode == AccessMode.DbLocked );
            ci.fields = fields;
        } else {
            fields = ci.fields;
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
            //System.out.println("!!!!!!!!!ClassMolder.load(): for: "+oid);
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                temp = (Object[]) fields[i];
                _fhs[i].setValue( object, temp[0] );
                break;

            case FieldMolder.PERSISTANCECAPABLE:
                if ( _fhs[i].isLazy() )
                    System.err.println( "Warning: Lazy loading of object is not yet support!" );

                //if ( info == null )
                //should make sure that not happen in elsewhere, but not throw here
                //    throw NonPersistanceCapableException("Field c");

                //info = tx.getPersistenceInfoGroup().getPersistenceInfo
                //        (_fhs[i].getFieldClassMolder().getJavaClass() );
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
                if ( _fhs[i].isLazy() ) {
                    // currently, many-to-many relation only allow one column pk
                    ArrayVector list = (ArrayVector) fields[i];
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();
                    RelationCollection relcol = new RelationCollection( tx, oid, fieldEngine, fieldClassMolder, null, list );
                    _fhs[i].setValue( object, relcol );
                } else {
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
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                if ( _fhs[i].isLazy() ) {
                    // currently, many-to-many relation only allow one column pk
                    ArrayVector list = (ArrayVector) fields[i];
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();
                    RelationCollection relcol = new RelationCollection( tx, oid, fieldEngine, fieldClassMolder, null, list );
                    _fhs[i].setValue( object, relcol );
                } else {
                    ArrayList col = new ArrayList();
                    //(ArrayList)_fhs[i].getCollectionType().newInstance();
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();

                    ArrayVector v = (ArrayVector)fields[i];
                    if ( v != null ) {                        
                        for ( int j=0,l=v.size(); j<l; j++ ) {
                            col.add( tx.load( oid.getLockEngine(), fieldClassMolder, (Object[])v.get(j), null ) );
                        }
                        _fhs[i].setValue( object, col );
                    }
                }
                break;
            default:
                throw new PersistenceException("Unexpect field type!");
            }
        }

        return oid;

        // for each field
        //   if field is a lazy collection
        //     if the field is many-to-many
        //       load with RelationCollectionClassMolder....
        //     else
        //       load
        //    make an new object
        //    call extended SQLEngine to load some fields first
        //    load all field which this molder handle
        //    update oid.setStamp
        //    if object not found rise exception
        //    call FieldMolder for each of the 1:N fields
        //    return object
        //  if lazy ....not yet implement, but leave space for the addition

        //  fieldMolder
        //    call FieldMolder for each of the 1:N fields
        //      if the field is lazy
        //        if the field is many-to-many
        //          make a ManyToManyrelationCollection of SQLEngine, table, Transaction
        //            (in relationCollection, leave space to take care the after
        //             transaction call to a lazy collection)
        //        if not many-to-many
        //          make a RelationCollection of SQLEngine, table, Transaction
        //      if the field is not lazy
        //        if one-to-many
        //          call typeMolder to molde the id

    }

    public ObjectBin fetch( TransactionContext tx, PersistenceQuery pq, OID oid, AccessMode accessMode )
            throws ObjectNotFoundException, PersistenceException {
        // same as load, exception call pq.fetch, in place of load
        throw new RuntimeException("ObjectBin.fetch() is not implemented!");        
    }

    public OID create( TransactionContext tx, OID oid, Object object )
            throws DuplicateIdentityException, PersistenceException {
        // if cache exist for object, rise exception
        System.out.println("ClassMolder.create(): Oid: "+oid);

        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Iterator itor;
        CacheItem ci;
        Object fetchedObject;
        ArrayVector fids;
        Object key;
        Object[] fid;
        Object[] createdId;
        Object[] ids;
		Object[] oids;
        Object[] newids;
        Object[] temp;
        Object o;

        int fieldType;

        ci = (CacheItem) _cache.get( oid );
        if ( !ci.isHold )
            throw new PersistenceException("Illegal Cache State!");

        if ( oid.isDbLock() && ci.fields != null )
            throw new DuplicateIdentityException("Object exist in the persistence storage!");

        if ( _persistence == null )
            throw new PersistenceException("non persistence capable");        

        ci.fields = new Object[_fhs.length];
        ids = oid.getIdentities();
		oids = new Object[ids.length];
		System.arraycopy( ids, 0, oids, 0, ids.length );

        if ( !OID.isEquals( ids, getIdentities(object) ) )
            throw new PersistenceException("Object identity change is not allowed! original id: "+OID.flatten(ids)+" changed id: "+OID.flatten(getIdentities(object)));

        // copy the object to cache should make a new field now,
        for ( int i=0; i<_fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                temp = new Object[1];
                temp[0] = _fhs[i].getValue( object );
                ci.fields[i] = temp;
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    // need multi-pk
                    fid = fieldClassMolder.getIdentities( o );
                    // support only singular object
                    if ( fid != null ) {
                        ci.fields[i] = fid;
                    }
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fids = getIds( fieldClassMolder, o );
                    ci.fields[i] = fids;
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fids = getIds( fieldClassMolder, o );
                    System.out.println("fids: "+ fids);
                    ci.fields[i] = fids;
                }
                break;
            default:
                throw new IllegalArgumentException("Field type invalid!");
            }
        }

        createdId = _persistence.create( (Connection)tx.getConnection(oid.getLockEngine()), 
                ci.fields, ids );

        for ( int i=0; i<_fhs.length; i++ ) {
            System.out.print("<"+i+":"+(ci.fields[i] instanceof Object[]?OID.flatten((Object[])ci.fields[i]):ci.fields[i])+" of type: "+_fhs[i].getJavaClass()+">  ");
        }
        System.out.println(" will be created");

        if ( createdId == null )
            throw new PersistenceException("Identity can't be created!");

        if ( !OID.isEquals( createdId, ids ) ) {
			_cache.remove( oid );
            for ( int i=0; i<_ids.length; i++ ) {
                _ids[i].setValue( object, createdId[i] );
            }
            //_cache.remove( oid );
            oid = new OID( oid.getLockEngine(), this, createdId );
            oid.setDbLock( false );
            _cache.put( oid, ci );
            // set id in cache.......
        } 


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
                            if ( !tx.isPersistent( oo ) )
                                tx.create( fieldEngine, fieldClassMolder, oo, oid );
							else 
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

        // and replace the cache at the end if and only if it's succeed)
        // generate id if not yet have id
        // checkValidity
        // call store of each fieldMolder
        // update oid and setStamp

        System.out.println("ClassMolder.create() done: returning, oid=" +oid+" id="+OID.flatten( oid.getIdentities() ));
		System.out.println("return oid from LockEngine.create() "+ oid);
        return oid;
    }

    public void store( TransactionContext tx, OID oid, Object object )
            throws DuplicateIdentityException, PersistenceException,
            ObjectModifiedException, ObjectDeletedException {
        System.out.println("ClassMolder.store(): Oid: "+oid);

        // if cached.id != object.id, rise exception
        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Object[] newfields;
        Object[] dependIds;
        Iterator itor;
        CacheItem ci;
        ArrayVector list;
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
        

        ci = (CacheItem) _cache.get( oid );
        if ( ! ci.isHold )
            throw new PersistenceException("Illegal Cache State!");

        if ( oid.getIdentities() == null || ci.fields == null )
            throw new PersistenceException("Object, "+oid+",  isn't loaded in the persistence storage!");
        
        if ( !OID.isEquals( oid.getIdentities(), getIdentities( object ) ) ) 
            throw new PersistenceException("Identities changes is not allowed!");

        ids = oid.getIdentities();

        newfields = new Object[_fhs.length];
        modified = false;
        lockrequired = false;

        for ( int i=0; i<newfields.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                Object[] temp = new Object[1];
                temp[0] = _fhs[i].getValue( object );
                newfields[i] = temp;

                if ( !OID.isEquals( (Object[])ci.fields[i], (Object[])newfields[i] ) ) {
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

                if ( !OID.isEquals( (Object[])ci.fields[i], (Object[])newfields[i] ) ) {
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
                            if ( !tx.isPersistent( o ) ) 
                                // should be created if transaction have no record of the object
                                tx.create( fieldEngine, fieldClassMolder, o, oid );
                        } else {
                            //if ( !tx.isPersistent( o ) ) 
                                // should be created if transaction have no record of the object
                            //    tx.create( fieldEngine, fieldClassMolder, o, null );
                        }
                    }
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                o = _fhs[i].getValue( object );
                if ( ! (o instanceof Lazy) ) {
                    itor = getIterator( o );
                    ArrayList v = (ArrayList) o;
                    list = new ArrayVector( v.size() );
                    for ( int j=0; j<v.size(); j++ ) {
                        list.add( (fieldClassMolder.getIdentities( v.get(j) ))[0] );
                    }
                    if ( !OID.isEquals( (ArrayVector)ci.fields[i], list ) ) {
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }

                        // need to add support for dependent object
                        itor = getIterator( o );
                        while ( itor.hasNext() ) {
                            newobj = itor.next();
                            fids = fieldClassMolder.getIdentities( newobj );

                            if ( _fhs[i].isDependent() ) {
                                if ( !tx.isPersistent( newobj ) ) 
                                    // should be created if transaction have no record of the object
                                    tx.create( fieldEngine, fieldClassMolder, newobj, oid );
                            } else {
                                //if ( !tx.isPersistent( newobj ) ) 
                                    // should be created if transaction have no record of the object
                                //   tx.create( fieldEngine, fieldClassMolder, newobj, null );
                            }
                        }
                    }
                } else {
                    /*
                    Lazy lazy = (Lazy) o;
                    list = lazy.getIdentitiesList();
                    if ( !OID.isEquals( list, (Vector)ci.fields[i] ) ) {
                        System.out.println("Store.m-to-n(lazy) modified");
                        if ( _fhs[i].isStored() ) {
                            if ( _fhs[i].isCheckDirty() ) {
                                modified = true;
                                lockrequired = true;
                            } else {
                                modified = true;
                            }
                        }
                        // should do something for lazy collection
                        throw new RuntimeException("One-to-many lazy loading is not implemented!");
                    }*/
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
                    ArrayVector orgFields = (ArrayVector)ci.fields[i];
                    if ( !OID.isEquals( orgFields, list ) ) {

                        itor = getIterator( o );
                        while ( o != null && itor.hasNext() ) {
                            newobj = itor.next();
                            fids = fieldClassMolder.getIdentities( newobj );

                            if ( !tx.isPersistent( newobj ) ) {
                                // should be created if transaction have no record of the object
								if ( _fhs[i].isDependent() ) {
	                                tx.create( fieldEngine, fieldClassMolder, newobj, oid );
								}
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
                    /*
                    Lazy lazy = (Lazy) o;
                    list = lazy.getIdentitiesList();
                    if ( !OID.isEquals( list, (Vector)ci.fields[i] ) ) {
                        System.out.println("Store.m-to-n(lazy) modified");
                        if ( _fhs[i].isCheckDirty() ) {
                            modified = true;
                            lockrequired = true;
                        } else {
                            modified = true;
                        }
                        // should do something for lazy collection
                    }*/
                }
                break;
            default:
            }
        }

        if ( lockrequired ) {
            tx.writeLock( object, 0 );
        }

        if ( modified ) {
            System.out.println("object is modifed, now storing it");
            for ( int i=0; newfields!=null && i<newfields.length; i++ ) {
                System.out.print("<"+i+":"+(newfields[i] instanceof Object[]?OID.flatten((Object[])newfields[i]):newfields[i])+" of "+_fhs[i].getJavaClass()+">  ");
            }
            System.out.println("     new field in object");

            for ( int i=0; ci.fields!=null && i<ci.fields.length; i++ ) {
                    System.out.print("<"+i+":"+(ci.fields[i] instanceof Object[]?OID.flatten((Object[])ci.fields[i]):ci.fields[i])+">  ");
            }
            System.out.println("       in cache");
            
            Object stamp = _persistence.store( tx.getConnection(oid.getLockEngine()),
                newfields, oid.getIdentities(), ci.fields, oid.getStamp() );
            oid.setStamp( stamp );
        } else {
            System.out.println("object not modifed!");
        }
        
        // checkValidity
        // call store of each fieldMolder
        // update oid and setStamp
    }
    public void updateCache( TransactionContext tx, OID oid, Object object )
                        throws DuplicateIdentityException, PersistenceException {
        // similar to store, no recusrion is needed
        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        Iterator itor;
        CacheItem ci;
        Object newid;
        Vector fids;
        Object key;
        Object[] fid;
        Object id;
        Object o;
        
        int fieldType;

        if ( oid.isIdsNull() ) 
            throw new PersistenceException("The identities of the cache to be updated is null");

        // assumption, only one transaction with the same oid will attempt load at
        // any given time.
        // if cache is not in hold state, rise exception
        ci = (CacheItem) _cache.get( oid );
        if ( !ci.isHold )
            throw new PersistenceException( "Illegal Cache State!" );

        for ( int i=0; i < _fhs.length; i++ ) {
            fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                // should give some attemp to reduce new object array
                Object[] temp = new Object[1];
                temp[0] = _fhs[i].getValue( object );
                if ( temp[0] != null ) {
                    ci.fields[i] = temp;
                } else {
                    ci.fields[i] = null;
                }
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    fid = fieldClassMolder.getIdentities( o );
                    ci.fields[i] = fid[0];
                } else {
                    ci.fields[i] = null;
                }
                break;
            case FieldMolder.ONE_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    if ( !( o instanceof Lazy) ) {
                        ArrayList list = (ArrayList) o;
                        Vector fidlist = new Vector( list.size() );
                        for ( int j=0; j<list.size(); j++ ) {
                            fidlist.add( fieldClassMolder.getIdentities( list.get(j) ) );
                        }
                        ci.fields[i] = fidlist;
                    } else {
                        Lazy lazy = (Lazy) o;
                        fids = (Vector) lazy.getIdentitiesList();
                        ci.fields[i] = fids;
                    }
                } else {
                    ci.fields[i] = null;
                }
                break;
            case FieldMolder.MANY_TO_MANY:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                o = _fhs[i].getValue( object );
                if ( o != null ) {
                    if ( !( o instanceof Lazy ) ) {
                        ArrayList list = (ArrayList) o;
                        Vector fidlist = new Vector( list.size() );
                        for ( int j=0; j<list.size(); j++ ) {
                            fidlist.add( fieldClassMolder.getIdentities( list.get(j) ) );
                        }
                        ci.fields[i] = fidlist;
                    } else {
                        Lazy lazy = (Lazy) o;
                        fids = (Vector) lazy.getIdentitiesList();
                        ci.fields[i] = fids;
                    }
                } else {
                    ci.fields[i] = null;
                }
                break;
            default:
                throw new IllegalArgumentException("Field type invalid!");
            }
        }
    }

    public void delete( TransactionContext tx, OID oid ) 
            throws PersistenceException {
        
        _persistence.delete( (Connection)tx.getConnection(oid.getLockEngine()), oid.getIdentities() );
    }

    public void markDelete( TransactionContext tx, OID oid, Object object )
            throws ObjectNotFoundException, PersistenceException {
        System.out.println("ClassMolder.markDelete() is called for "+oid+" object: "+ object );

        ClassMolder fieldClassMolder;
        LockEngine fieldEngine;
        CacheItem ci;

        ci = (CacheItem) _cache.get( oid );
        if ( !ci.isHold )
            throw new PersistenceException( "Illegal Cache State!" );

        for ( int i=0; i < _fhs.length; i++ ) {
            int fieldType = _fhs[i].getFieldType();
            switch (fieldType) {
            case FieldMolder.PRIMITIVE:
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();
                if ( _fhs[i].isDependent() ) {
                    Object[] fid = (Object[]) ci.fields[i];
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
                    ArrayList alist = (ArrayList) ci.fields[i];
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

    public void revertObject( TransactionContext tx, OID oid, Object object ) 
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

        CacheItem ci = (CacheItem) _cache.get( oid );
        if ( !ci.isHold )
            throw new PersistenceException( "Illegal Cache State!" );

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
                _fhs[i].setValue( object, ci.fields[i] );
                break;
            case FieldMolder.PERSISTANCECAPABLE:
                if ( _fhs[i].isLazy() )
                    System.err.println( "Warning: Lazy loading of object is not yet supported!" );

                fieldClassMolder = _fhs[i].getFieldClassMolder();
                fieldEngine = _fhs[i].getFieldLockEngine();

                if ( ci.fields[i] != null ) {
                    value = tx.load( fieldEngine, fieldClassMolder, (Object[]) ci.fields[i], null );
                    _fhs[i].setValue( object, value );
                } else {
                    _fhs[i].setValue( object, null );
                }
                break;
            case FieldMolder.ONE_TO_MANY:
            case FieldMolder.MANY_TO_MANY:
                if ( ci.fields[i] == null ) {
                    _fhs[i].setValue( object, null );
                } else if ( _fhs[i].isLazy() ) {
                    ArrayVector list = (ArrayVector) ci.fields[i];
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();
                    RelationCollection relcol = new RelationCollection( tx, oid, fieldEngine, fieldClassMolder, null, list );
                    _fhs[i].setValue( object, relcol );
                } else {
                    ArrayList col = new ArrayList();
                    //(ArrayList)_fhs[i].getCollectionType().newInstance();
                    fieldClassMolder = _fhs[i].getFieldClassMolder();
                    fieldEngine = _fhs[i].getFieldLockEngine();

                    ArrayVector v = (ArrayVector)ci.fields[i];
                    for ( int j=0,l=v.size(); j<l; j++ ) {
                        col.add( tx.load( fieldEngine, fieldClassMolder, (Object[])v.get(j), null ) );
                    }
                    _fhs[i].setValue( object, col );
                }
                break;
            default:
            }
        }
    }

    public void writeLock( TransactionContext tx, OID oid, Object object )
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
        return null;
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

    public boolean isDependent() {
        return _depends != null;
    }

    /*
     * Mutator method
     */
    void setExtends( ClassMolder ext ) {
        _extends = ext;
    }

    /*
     * Mutator method
     */
    void setDepends( ClassMolder dep ) {
		System.out.println("++++++++++++++++++++++ setDepends "+dep);
        _depends = dep;
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


