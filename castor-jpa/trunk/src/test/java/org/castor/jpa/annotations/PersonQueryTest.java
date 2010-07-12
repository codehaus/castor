package org.castor.jpa.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jpa.CastorQuery;
import org.castor.jpa.annotations.model.Person;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class contains some integration tests for castor's JPA using the
 * {@link javax.persistence.EntityManager EntityManager}.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "JPAIntegrationTests-context.xml" })
public class PersonQueryTest {

    private static final Log LOG = LogFactory
            .getLog(PersonCallbackHookTest.class);
    private static final EntityManager EM = Persistence
            .createEntityManagerFactory("castor").createEntityManager();

    public static String NAME1 = "Ronny James Dio";

    @AfterClass
    public static void closeEntityManager() throws Exception {
        EM.close();
    }

    private void persistPerson(Person p) {
        EntityTransaction tx = EM.getTransaction();
        try {
            tx.begin();
            EM.persist(p);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            LOG
                    .debug("Something went wrong while trying to persist an entity: "
                            + e.getMessage());
        }
    }

    private void removePersistedPerson(long id) {
        EntityTransaction tx = EM.getTransaction();
        try {
            tx.begin();
            EM.remove(EM.find(Person.class, id));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            LOG
                    .debug("Something went wrong while trying to remove an entity from the DB: "
                            + e.getMessage());
        }
    }

    @Test
    public void personIsCreatedAndPersistedCorrectly() {
        persistPerson(new Person(Person.QUERY_TEST_ID, NAME1));

        EM.getTransaction().begin();
        final Person loadedPerson = EM.find(Person.class, Person.QUERY_TEST_ID);
        EM.getTransaction().commit();
        assertNotNull(loadedPerson);
        assertEquals(Person.QUERY_TEST_ID, loadedPerson.getId());
        assertEquals(NAME1, loadedPerson.getName());

        removePersistedPerson(loadedPerson.getId());
    }

    @Test
    public void findPersonViaNamedNativeQuery() {
        persistPerson(new Person(Person.QUERY_TEST_ID, NAME1));

        EM.getTransaction().begin();
        CastorQuery q = (CastorQuery) EM.createNamedQuery("findRonnyJames");
        List resultList = q.getResultList();
        final Person queriedPerson = (Person) resultList.get(0);
        assertNotNull(queriedPerson);
        EM.getTransaction().commit();
        assertEquals(Person.QUERY_TEST_ID, queriedPerson.getId());
        assertEquals(NAME1, queriedPerson.getName());

        removePersistedPerson(queriedPerson.getId());
    }

    @Test
    public void findPersonViaNamedQueryUsingParameter() {
        persistPerson(new Person(Person.QUERY_TEST_ID, NAME1));

        EM.getTransaction().begin();
        CastorQuery q = (CastorQuery) EM.createNamedQuery("findPersonByName");
        q.setParameter("$1", NAME1); // FIXME: Since OQL doesn't support
        // individual parameter names, the name is
        // completely irrelevant, as long as the
        // paras in in the correct order.
        List resultList = q.getResultList();
        final Person queriedPerson = (Person) resultList.get(0);
        assertNotNull(queriedPerson);
        EM.getTransaction().commit();
        assertEquals(Person.QUERY_TEST_ID, queriedPerson.getId());
        assertEquals(NAME1, queriedPerson.getName());

        removePersistedPerson(queriedPerson.getId());
    }
}
