package org.castor.jpa.annotations;

import static org.junit.Assert.assertEquals;

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
public class RoleManyToOneTest {

    public static final Log LOG = LogFactory
            .getLog(PersonCallbackHookTest.class);
    private static final EntityManager EM = Persistence
            .createEntityManagerFactory("castor").createEntityManager();

    @AfterClass
    public static void closeEntityManager() throws Exception {
        EM.close();
    }

    private void persistPerson(Person p) throws Exception {
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

    private void removePersistedPerson(long id) throws Exception {
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
    public void dummy() throws Exception {
        assertEquals(1, 1);
    }
}
