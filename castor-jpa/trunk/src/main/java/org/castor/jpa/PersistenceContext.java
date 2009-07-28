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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContextType;

/**
 * The persistence context represents the set of entities, managed by an
 * {@link EntityManager}.<br/>
 * <br/>
 * A Persistence context can be either:<br/>
 * <ul>
 * <li>a transaction-scoped (i.e. {@link PersistenceContextType#EXTENDED}) or</li>
 * <li>an extended persistence context (i.e.
 * {@link PersistenceContextType#EXTENDED}).</li>
 * </ul>
 * 
 * @author lukas.lang
 * 
 */
interface PersistenceContext {

    /**
     * @return the type of the persistence context.
     */
    PersistenceContextType getScope();

    /**
     * Adds an entity to the set of managed entities.
     * 
     * @param entity
     *            the entity.
     * 
     * @throws IllegalArgumentException
     *             in case the entity is <code>null</code>.
     */
    void manage(Object entity);

    /**
     * Removes an entity from the set of managed entities.
     * 
     * @param entity
     *            the entity.
     * 
     * @throws IllegalArgumentException
     *             in case the entity is <code>null</code>.
     */
    void remove(Object entity);

    /**
     * Replaces an old entity with a new one.
     * 
     * @param oldEntity
     *            the old entity.
     * @param newEntity
     *            the new entity.
     * @throws IllegalArgumentException
     *             in case the old entity is not managed or one of the arguments
     *             is <code>null</code>.
     */
    void replace(Object oldEntity, Object newEntity) throws IllegalArgumentException;

    /**
     * @param entity
     *            an entity.
     * @return <code>true</code> if the given entity is managed by this
     *         {@link PersistenceContext}.
     * @throws IllegalArgumentException
     *             in case the entity is <code>null</code>.
     */
    boolean contains(Object entity);
}