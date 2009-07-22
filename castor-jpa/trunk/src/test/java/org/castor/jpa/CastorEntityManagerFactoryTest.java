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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CastorEntityManagerFactoryTest {

    /**
     * Verifies correct creation of an {@link EntityManager} instance.
     * {@link EntityManager#isOpen()} must return true.
     */
    @Test
    public void createEntityManager() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();
        assertNotNull(entityManager);
        assertTrue(entityManager.isOpen());
    }

    /**
     * Tries to obtain an {@link EntityManager} from a closed
     * {@link EntityManagerFactory}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    public void createEntityManagerOnClosedFactory() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");

        entityManagerFactory.close();

        entityManagerFactory.createEntityManager();
    }

    /**
     * Verifies correct creation of an {@link EntityManager} instance.
     * {@link EntityManager#isOpen()} must return true.
     */
    @Test
    @Ignore
    public void createEntityManagerWithProperties() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("some.test.property", "unit-test-value");

        EntityManager entityManager =
                entityManagerFactory.createEntityManager(properties);
        assertNotNull(entityManager);
        assertTrue(entityManager.isOpen());
    }

    /**
     * Tries to obtain an {@link EntityManager} with properties from a closed
     * {@link EntityManagerFactory}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    public void createEntityManagerWithPropertiesOnClosedFactory() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");

        entityManagerFactory.close();

        Map<String, String> properties = new HashMap<String, String>();
        entityManagerFactory.createEntityManager(properties);
    }

    /**
     * Verifies correct behavior of {@link EntityManagerFactory#close()}.
     */
    @Test
    public void close() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");
        entityManagerFactory.close();

        assertFalse(entityManagerFactory.isOpen());
    }

    /**
     * Tries to invoke {@link EntityManagerFactory#close()} twice. Must result
     * in an {@link IllegalStateException}.
     */
    @Test(expected = IllegalStateException.class)
    public void closeTwice() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");
        entityManagerFactory.close();

        entityManagerFactory.close();
    }

    /**
     * Obtains a new {@link EntityManagerFactory} and verifies that
     * {@link EntityManagerFactory#isOpen()} returns <code>true</code>.
     */
    @Test
    public void isOpen() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("castor");
        assertTrue(entityManagerFactory.isOpen());
    }
}