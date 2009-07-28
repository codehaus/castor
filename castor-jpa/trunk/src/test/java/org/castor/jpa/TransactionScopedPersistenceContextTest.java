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

import javax.persistence.PersistenceContextType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Runs test on a transaction-scoped {@link PersistenceContext} implementation.
 * 
 * @author lukas.lang
 * 
 */
public class TransactionScopedPersistenceContextTest {

    @Test
    public void testGetScope() {
        PersistenceContext context = new TransactionScopedPersistenceContext();
        assertEquals(PersistenceContextType.TRANSACTION, context.getScope());
    }

    @Test
    public void testRemove() {
        PersistenceContext context = new TransactionScopedPersistenceContext();
        Object entity = new Object();

        context.manage(entity);
        assertTrue(context.contains(entity));

        context.remove(entity);
        assertFalse(context.contains(entity));

        assertTrue(context.removed(entity));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        PersistenceContext context = new TransactionScopedPersistenceContext();

        context.remove(null);
    }

    @Test
    public void testRemoveNonExisting() {
        PersistenceContext context = new TransactionScopedPersistenceContext();

        context.remove(new Object());
    }

    @Test
    public void testReplace() {
        PersistenceContext context = new TransactionScopedPersistenceContext();
        Object oldEntity = new Object();
        Object newEntity = new Object();

        context.manage(oldEntity);
        assertTrue(context.contains(oldEntity));

        context.replace(oldEntity, newEntity);
        assertTrue(context.contains(newEntity));
        assertFalse(context.contains(oldEntity));
        assertFalse(context.removed(oldEntity));
    }

    @Test
    public void testReplaceSame() {
        PersistenceContext context = new TransactionScopedPersistenceContext();
        Object entity = new Object();

        context.manage(entity);
        assertTrue(context.contains(entity));

        context.replace(entity, entity);
        assertTrue(context.contains(entity));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplaceNonExisting() {
        PersistenceContext context = new TransactionScopedPersistenceContext();

        context.replace(new Object(), new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplaceNull() {
        PersistenceContext context = new TransactionScopedPersistenceContext();

        context.replace(null, null);
    }

    @Test
    public void testContains() {
        PersistenceContext context = new TransactionScopedPersistenceContext();
        Object entity = new Object();

        context.manage(entity);
        assertTrue(context.contains(entity));
    }

    @Test
    public void testContainsNonExisting() {
        PersistenceContext context = new TransactionScopedPersistenceContext();
        Object entity = new Object();

        assertFalse(context.contains(entity));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsNull() {
        PersistenceContext context = new TransactionScopedPersistenceContext();

        context.contains(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemovedNull() {
        PersistenceContext context = new TransactionScopedPersistenceContext();

        context.removed(null);
    }
}
