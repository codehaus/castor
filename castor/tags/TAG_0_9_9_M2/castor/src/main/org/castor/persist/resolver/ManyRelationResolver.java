/*
 * Copyright 2005 Werner Guttmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.castor.persist.resolver;

import java.util.ArrayList;
import java.util.Iterator;

import org.castor.persist.ProposedObject;
import org.castor.persist.TransactionContext;
import org.castor.persist.UpdateAndRemovedFlags;
import org.castor.persist.UpdateFlags;
import org.castor.persist.proxy.CollectionProxy;
import org.castor.persist.proxy.RelationCollection;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.ClassMolder;
import org.exolab.castor.persist.ClassMolderHelper;
import org.exolab.castor.persist.FieldMolder;
import org.exolab.castor.persist.Lazy;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.OID;

/**
 * Implementation of {@link org.castor.persist.resolver.ResolverStrategy} for many relations. This class carries
 * behaviour common to 1:M and M:N relations.
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @since 0.9.9
 */
public abstract class ManyRelationResolver implements ResolverStrategy {

    /**
     * Associated {@link ClassMolder}.
     */
    protected ClassMolder _classMolder;
    
    /**
     * Associated {@link FieldMolder}.
     */
    protected FieldMolder _fieldMolder;
    
    /**
     * ???
     */
    protected boolean _debug;
    
    /** 
     * Creates an instance of ManyRelationResolver
     * @param classMolder Associated {@link ClassMolder}
     * @param fieldMolder Associated {@link FieldMolder}
     * @param debug ???
     */
    public ManyRelationResolver(final ClassMolder classMolder,
            final FieldMolder fieldMolder, final boolean debug) {
        this._classMolder = classMolder;
        this._fieldMolder = fieldMolder;
        this._debug = debug;
    }
    
    /**
     * @see org.castor.persist.resolver.ResolverStrategy
     *      #create(org.castor.persist.TransactionContext,
     *      java.lang.Object)
     */
    public Object create(final TransactionContext tx, final Object object) {
        Object field = null;
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        Object o = _fieldMolder.getValue(object, tx.getClassLoader());
        if (o != null) {
            ArrayList fids = 
                ClassMolderHelper.extractIdentityList(tx, fieldClassMolder, o);
            field = fids;
        }
        return field;
    }

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#markCreate(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, java.lang.Object)
     */
    public abstract boolean markCreate(final TransactionContext tx, final OID oid,
            final Object object) 
    throws DuplicateIdentityException, PersistenceException;

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#preStore(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, java.lang.Object, int, java.lang.Object)
     */
    public abstract UpdateFlags preStore(final TransactionContext tx,
            final OID oid, final Object object, final int timeout,
            final Object field) 
    throws PersistenceException;

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#store(org.castor.persist.TransactionContext, java.lang.Object, java.lang.Object)
     */
    public Object store(final TransactionContext tx, final Object object,
            final Object field) {
        // nothing to do ....
        return null;
    }

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#update(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, java.lang.Object, org.exolab.castor.mapping.AccessMode, java.lang.Object)
     */
    public abstract void update(final TransactionContext tx, final OID oid,
            final Object object, final AccessMode suggestedAccessMode,
            final Object field) throws PersistenceException,
            ObjectModifiedException;

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#updateCache(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, java.lang.Object)
     */
    public Object updateCache(final TransactionContext tx, final OID oid,
            final Object object) {
        Object field = null;
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        Object value = _fieldMolder.getValue(object, tx.getClassLoader());
        if (value != null) {
            ArrayList fids;
            if (!(value instanceof Lazy)) {
                fids = ClassMolderHelper.extractIdentityList(tx,
                        fieldClassMolder, value);
            } else {
                RelationCollection lazy = (RelationCollection) value;
                fids = (ArrayList) lazy.getIdentitiesList().clone();
            }
            field = fids;
        }
        return field;
    }

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#markDelete(org.castor.persist.TransactionContext, java.lang.Object, java.lang.Object)
     */
    public abstract void markDelete(final TransactionContext tx,
            final Object object, final Object field)
    throws ObjectNotFoundException, PersistenceException;

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#revertObject(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, java.lang.Object, java.lang.Object)
     */
    public void revertObject(final TransactionContext tx, final OID oid,
            final Object object, final Object field)
    throws PersistenceException {
        Object o = field;
        if (o == null) {
            _fieldMolder.setValue(object, null, tx.getClassLoader());
        } else if (!_fieldMolder.isLazy()) { // <-- fix for bug #1046
            ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
            Class collectionType = _fieldMolder.getCollectionType();

            ArrayList v = (ArrayList) field;
            if (v != null) {
                if (collectionType.isArray()) {
                    Object[] arrayValue = (Object[]) java.lang.reflect.Array
                            .newInstance(collectionType.getComponentType(), v
                                    .size());
                    for (int j = 0, l = v.size(); j < l; j++) {
                        arrayValue[j] = tx.fetch(oid.getLockEngine(),
                                fieldClassMolder, v.get(j), null);
                    }
                    _fieldMolder.setValue(object, arrayValue, tx
                            .getClassLoader());
                } else {
                    CollectionProxy cp = CollectionProxy.create(_fieldMolder,
                            object, tx.getClassLoader());
                    // clear collection
                    _fieldMolder.setValue(object, cp.getCollection(), tx
                            .getClassLoader());

                    for (int j = 0, l = v.size(); j < l; j++) {
                        Object obj = tx.fetch(oid.getLockEngine(),
                                fieldClassMolder, v.get(j), null);
                        if (obj != null) {
                            cp.add(v.get(j), obj);
                        }
                    }
                    cp.close();
                    // fieldMolder.setValue( object, cp.getCollection() );
                }
            } else {
                _fieldMolder.setValue(object, null, tx.getClassLoader());
            }
        } else {
            ArrayList list = (ArrayList) field;
            ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
            LockEngine fieldEngine = _fieldMolder.getFieldLockEngine();

            RelationCollection relcol = new RelationCollection(tx, oid,
                    fieldEngine, fieldClassMolder, null, list);
            _fieldMolder.setValue(object, relcol, tx.getClassLoader());
        }
    }

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#expireCache(org.castor.persist.TransactionContext, java.lang.Object)
     */
    public void expireCache(final TransactionContext tx, final Object field)
    throws PersistenceException {
        // field is one-to-many and many-to-many type. All the related
        // objects will be expired

        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        LockEngine fieldEngine = _fieldMolder.getFieldLockEngine();

        ArrayList v = (ArrayList) field;
        if (v != null) {
            for (int j = 0, l = v.size(); j < l; j++) {
                tx.expireCache(fieldEngine, fieldClassMolder, v.get(j));
            }
        }
    }

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#load(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, org.castor.persist.ProposedObject, org.exolab.castor.mapping.AccessMode, java.lang.Object)
     */
    public void load(final TransactionContext tx, final OID oid,
            final ProposedObject proposedObject,
            final AccessMode suggestedAccessMode, final Object field)
    throws ObjectNotFoundException, PersistenceException {
        // field is one-to-many and many-to-many type. All the related
        // object will be loaded and put in a Collection. And, the
        // collection will be set as the field.
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        LockEngine fieldEngine = _fieldMolder.getFieldLockEngine();

        if (!_fieldMolder.isLazy()) {
            // lazy loading is not specified, load all objects into
            // the collection and set the Collection as the data object
            // field.
            ArrayList v = (ArrayList) field;
            if (v != null) {
                // simple array type support
                Class collectionType = _fieldMolder.getCollectionType();
                if (collectionType.isArray()) {
                    Object[] value = (Object[]) java.lang.reflect.Array
                            .newInstance(collectionType.getComponentType(), v
                                    .size());
                    for (int j = 0, l = v.size(); j < l; j++) {
                        ProposedObject proposedValue = new ProposedObject();
                        value[j] = tx.load(oid.getLockEngine(),
                                fieldClassMolder, v.get(j), proposedValue,
                                suggestedAccessMode);
                    }
                    _fieldMolder.setValue(proposedObject.getObject(), value, tx
                            .getClassLoader());
                } else {
                    CollectionProxy cp = CollectionProxy.create(_fieldMolder,
                            proposedObject.getObject(), tx.getClassLoader());
                    for (int j = 0, l = v.size(); j < l; j++) {
                        ProposedObject proposedValue = new ProposedObject();
                        cp.add(v.get(j), tx.load(oid.getLockEngine(),
                                fieldClassMolder, v.get(j), proposedValue,
                                suggestedAccessMode));
                    }
                    cp.close();
                }
            } else {
                _fieldMolder.setValue(proposedObject.getObject(), null, tx
                        .getClassLoader());
            }
        } else {
            // lazy loading is specified. Related object will not be loaded.
            // A lazy collection with all the identity of the related object
            // will constructed and set as the data object's field.
            ArrayList list = (ArrayList) field;
            RelationCollection relcol = new RelationCollection(tx, oid,
                    fieldEngine, fieldClassMolder, suggestedAccessMode, list);
            _fieldMolder.setValue(proposedObject.getObject(), relcol, tx
                    .getClassLoader());
        }
    }

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#postCreate(org.castor.persist.TransactionContext, org.exolab.castor.persist.OID, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public abstract Object postCreate(final TransactionContext tx,
            final OID oid, final Object object, final Object field,
            final Object createdId)
    throws DuplicateIdentityException, PersistenceException;

    /* (non-Javadoc)
     * @see org.castor.persist.resolver.ResolverStrategy#removeRelation(org.castor.persist.TransactionContext, java.lang.Object, org.exolab.castor.persist.ClassMolder, java.lang.Object)
     */
    public UpdateAndRemovedFlags removeRelation(final TransactionContext tx,
            final Object object, final ClassMolder relatedMolder,
            final Object relatedObject) {
        UpdateAndRemovedFlags flags = new UpdateAndRemovedFlags();
        // remove the object from the collection
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        ClassMolder relatedBaseMolder = relatedMolder;
        while (fieldClassMolder != relatedBaseMolder
                && relatedBaseMolder != null) {
            relatedBaseMolder = relatedBaseMolder.getExtends();
        }
        if (fieldClassMolder == relatedBaseMolder) {
            boolean changed = false;
            Object related = _fieldMolder.getValue(object, tx.getClassLoader());

            if (related instanceof RelationCollection) {
                RelationCollection lazy = (RelationCollection) related;
                changed = lazy.remove(relatedObject);
            } else {
                Iterator itor = ClassMolderHelper.getIterator(related);
                while (itor.hasNext()) {
                    Object o = itor.next();
                    if (o == relatedObject) {
                        changed = true;
                        itor.remove();
                    }
                }
            }
            if (changed) {
                flags.setUpdateCache(true);
                flags.setUpdatePersist(false);
                flags.setRemoved(true);
            }
        }
        return flags;
    }

}
