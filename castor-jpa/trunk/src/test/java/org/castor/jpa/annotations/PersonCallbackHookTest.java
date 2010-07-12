package org.castor.jpa.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class PersonCallbackHookTest {

    private static final Log LOG = LogFactory
            .getLog(PersonCallbackHookTest.class);
    private static final EntityManager EM = Persistence
            .createEntityManagerFactory("castor").createEntityManager();

    public static long ID1 = 1000L;
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
        persistPerson(new Person(ID1, NAME1));

        EM.getTransaction().begin();
        final Person loadedPerson = EM.find(Person.class, ID1);
        EM.getTransaction().commit();
        assertNotNull(loadedPerson);
        assertEquals(ID1, loadedPerson.getId());
        assertEquals(NAME1, loadedPerson.getName());

        removePersistedPerson(loadedPerson.getId());
    }

    @Test
    public void prePersistCallbackMethodGetsExecutedAsExpected() {
        try {
            EM.getTransaction().begin();
            EM.persist(new Person(Person.CALLBACK_TEST_ID,
                    Person.PREPERSIST_TEST_NAME));
            fail("Should have throwen an exceptions for person with this name "
                    + "(see: @PrePersist Person.validateCreation)");
        } catch (Exception e) {
            LOG.debug("Exception thrown as expected: " + e.getMessage());
        } finally {
            EM.getTransaction().rollback(); // clean up
        }
    }

    // @Test
    public void anotherPrePersistCallbackMethodGetsExexutedAsExpected() {
        persistPerson(new Person(ID1, "Clark Kent"));

        EM.getTransaction().begin();
        final Person loadedPerson = EM.find(Person.class, ID1);
        EM.getTransaction().commit();

        assertNotNull(loadedPerson);
        assertEquals(ID1, loadedPerson.getId());
        assertEquals("Superman", loadedPerson.getName());

        removePersistedPerson(ID1);
    }

    /*
     * This test is one of more unit tests for @postPersist. But it shows
     * another failure behavior (rollback is not working correctly)
     */
    @Test
    public void postPersistCallbackMethodGetsExecutedAndThrowsExceptionAsExpected() {
        try {
            EM.getTransaction().begin();
            EM.persist(new Person(Person.CALLBACK_TEST_ID,
                    Person.POSTPERSIST_TEST_NAME));
            fail("Should have throwen an exceptions for person with this name "
                    + "(see: @PostPersist Person.validatePersistence)");
            EM.getTransaction().commit();
        } catch (Exception e) {
            // EM.getTransaction().rollback(); //FIXME: For whatever reason
            // rollback only isn't working
            // Workaround:
            EM.getTransaction().commit();
            removePersistedPerson(Person.CALLBACK_TEST_ID);
        }
    }

    /*
     * 
     */
    // @Test
    public void anotherPostPersistCallbackMethodTest() {
        Person personToPersist = new Person(007L, "Bruce Wayne");
        persistPerson(new Person(007L, "Bruce Wayne"));
        assertEquals("Batman", personToPersist.getName());

        EM.getTransaction().begin();
        final Person loadedPerson = EM.find(Person.class, 007L);
        EM.getTransaction().commit();

        // After a new load we should have the original value again
        assertNotNull(loadedPerson);
        assertEquals(007L, loadedPerson.getId());
        assertEquals("Bruce Wayne", loadedPerson.getName()); // FIXME: shouldn't
                                                             // that be Bruce
                                                             // Wayne
                                                             // (POSTpersist).

        removePersistedPerson(007L);

    }

    // FIMXE: Using the same ID as in test cases before completely messes
    // everything up
    /*
     * This test case is almost equivalent to the one above. The only difference
     * is that we use an ID, that we have already used before. This shouldn't be
     * a problem, because at the beginning of the test case, the DB is empty.
     * Nevertheless this test case shows an errourness behavior.
     */
    // @Test
    public void oneMorePostPersistCallbackMethodTest() {
        Person personToPersist = new Person(ID1, "Bruce Wayne");
        persistPerson(new Person(ID1, "Bruce Wayne"));
        assertEquals("Batman", personToPersist.getName());

        EM.getTransaction().begin();
        final Person loadedPerson = EM.find(Person.class, ID1);
        EM.getTransaction().commit();

        assertNotNull(loadedPerson);
        assertEquals(ID1, loadedPerson.getId());
        assertEquals("Bruce Wayne", loadedPerson.getName()); // FIXME: shouldn't
                                                             // that be Bruce
                                                             // Wayne
                                                             // (POSTpersist).

        removePersistedPerson(007L);

    }
}
