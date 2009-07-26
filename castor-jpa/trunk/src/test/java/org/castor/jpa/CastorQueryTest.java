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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CastorQueryTest {

    private EntityManagerFactory factory;

    /**
     * Invoked before every single test method.
     */
    @Before
    public void before() {
        factory = Persistence.createEntityManagerFactory("castor");
    }

    /**
     * Creates a named {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void namedQueryGetResultListOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query namedQuery = em.createNamedQuery("unit-test-named-query");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        namedQuery.getResultList();
    }

    /**
     * Creates a named {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void namedQueryGetSingleResultOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query namedQuery = em.createNamedQuery("unit-test-named-select-query");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        namedQuery.getSingleResult();
    }

    /**
     * Creates a named {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void namedQueryUpdateOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query namedQuery = em.createNamedQuery("unit-test-named-update-query");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        namedQuery.executeUpdate();
    }

    /**
     * Creates a JPA {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void queryGetResultListOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT e FROM Entity e");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getResultList();
    }

    /**
     * Creates a JPA {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void queryGetSingleResultOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT e FROM Entity e");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getSingleResult();
    }

    /**
     * Creates a JPA {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void queryUpdateOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT e FROM Entity e");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.executeUpdate();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryResultListOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getResultList();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryGetSingleResultOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getSingleResult();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryUpdateOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.executeUpdate();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryWithTypeResultListOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity", Entity.class);
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getResultList();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryWithTypeGetSingleResultOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity", Entity.class);
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getSingleResult();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryWithTypeUpdateOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity", Entity.class);
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.executeUpdate();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryWithMappingResultListOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity", "unit-test-mapping");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getResultList();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryWithMappingGetSingleResultOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT * FROM entity", "unit-test-mapping");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.getSingleResult();
    }

    /**
     * Creates a native {@link Query} and then closes the {@link EntityManager}.
     * The query must be invalid then.
     */
    @Test(expected = PersistenceException.class)
    public void nativeQueryWithMappingUpdateOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("DELETE FROM entity", "unit-test-mapping");
        // Now close the EntityManager.
        em.close();

        // Query must fail due to its invalidity.
        query.executeUpdate();
    }

}
