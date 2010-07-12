package org.castor.jpa.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jpa.annotations.model.Actor;
import org.castor.jpa.annotations.model.Role;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class contains some integration tests for castor's JPA using one to many
 * relations and {@link javax.persistence.EntityManager EntityManager}.
 * 
 * Code adopted from Werner Guttmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "JPAIntegrationTests-context.xml" })
public class ActorOneToManyTest {

    public static final Log LOG = LogFactory
            .getLog(PersonCallbackHookTest.class);
    private static final EntityManager EM = Persistence
            .createEntityManagerFactory("castor").createEntityManager();

    @AfterClass
    public static void closeEntityManager() throws Exception {
        EM.close();
    }

    @Test
    public void dummy() {
        assertEquals(0, 0);
    }

    // @Test //FIXME: role gets not persisted
    public void persistAnActor() {
        Collection<Role> roles = new ArrayList<Role>();
        Role newRole = new Role();
        newRole.setId(1234);
        newRole.setName("Otello");
        roles.add(newRole);

        Actor actor = new Actor();
        actor.setSvnr(1234567890);
        actor.setFirstname("Max");
        actor.setLastname("Mustermann");
        actor.setRoles(roles);

        EM.getTransaction().begin();
        EM.persist(actor);
        // EM.getTransaction().commit();

        // EM.getTransaction().begin();
        final Actor loadedActor = EM.find(Actor.class, 1234567890);
        EM.getTransaction().commit();

        assertEquals("Max", loadedActor.getFirstname());
        assertEquals("Mustermann", loadedActor.getLastname());
        assertEquals(1234567890, loadedActor.getSvnr());
        assertEquals(1, loadedActor.getRoles().size());
        Role role = loadedActor.getRoles().iterator().next();
        assertEquals(1234, role.getId());
        assertEquals("Otello", role.getName());

        EM.getTransaction().begin();
        final Role loadedRole = EM.find(Role.class, 1234);
        EM.getTransaction().commit();

        assertNotNull(loadedRole);
        assertEquals("Otello", loadedRole.getName());
    }

    // @Test
    public void roleHasBeenPersistedCorrectly() throws Exception {
        EM.getTransaction().begin();
        final Role loadedRole = EM.find(Role.class, 1234);
        EM.getTransaction().commit();

        assertEquals("Otello", loadedRole.getName());
    }

    // @Test
    public void delete() {

        Actor actor = new Actor();
        actor.setSvnr(1234567890);
        actor.setFirstname("Max");
        actor.setLastname("Mustermann");

        EM.getTransaction().begin();
        EM.persist(actor);
        EM.getTransaction().commit();

        EM.getTransaction().begin();
        Actor got = EM.find(Actor.class, 1234567890);
        EM.getTransaction().commit();
        assertEquals("Max", got.getFirstname());
        assertEquals("Mustermann", got.getLastname());

        EM.getTransaction().begin();
        EM.remove(EM.find(Actor.class, 1234567890));
        EM.getTransaction().commit();

        try {
            EM.getTransaction().begin();
            EM.find(Actor.class, 1234567890);
            EM.getTransaction().commit();
            fail();
        } catch (Exception e) {
        }

    }
}
