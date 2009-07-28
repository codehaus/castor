/*
 * Copyright 2009 Lukas Lang
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
 */
package org.castor.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceContextType;

/**
 * A {@link HashSet} based transaction-scoped implementation of the
 * {@link PersistenceContext} interface.<br/>
 * <br/>
 * <b>NOTE: This implementation is neither thread-safe nor transactional!</b>.
 * 
 * @author lukas.lang
 * 
 */
class TransactionScopedPersistenceContext implements PersistenceContext {

    /**
     * The persistence context container.
     */
    private Set<Object> container;

    public TransactionScopedPersistenceContext() {
        this.container = new HashSet<Object>();
    }

    public boolean contains(Object entity) {
        verifyNotNull(entity);
        return this.container.contains(entity);
    }

    public PersistenceContextType getScope() {
        return PersistenceContextType.TRANSACTION;
    }

    public void manage(Object entity) {
        verifyNotNull(entity);
        this.container.add(entity);
    }

    public void remove(Object entity) {
        verifyNotNull(entity);
        this.container.remove(entity);
    }

    public void replace(Object oldEntity, Object newEntity) {
        verifyNotNull(oldEntity);
        verifyNotNull(newEntity);
        if (!contains(oldEntity)) {
            throw new IllegalArgumentException("Can not replace an entity of type >"
                    + oldEntity.getClass() + "< which is not managed!");
        }
        this.remove(oldEntity);
        this.manage(newEntity);
    }

    /**
     * Verifies whether a given entity is valid. Throws an
     * {@link IllegalArgumentException} in case entity is <code>null</code>.
     * 
     * @param entity
     *            the entity.
     */
    private void verifyNotNull(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("An entity must not be null!");
        }
    }

}
