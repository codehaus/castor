package org.castor.jpa.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.castor.jpa.annotations.model.Person;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "JPAIntegrationTests-context.xml" })
public class PersonCrudTest {

    private static final EntityManager EM = Persistence
            .createEntityManagerFactory("castor").createEntityManager();

    public static final long ID = 1L;
    public static final String NAME = "Bob";

    @AfterClass
    public static void cleanup() throws Exception {
        EM.close();
    }

    @Test
    public void persistCreation() throws Exception {
        final Person personToPersist = new Person();
        personToPersist.setId(ID);
        personToPersist.setName(NAME);
        EM.getTransaction().begin();
        EM.persist(personToPersist);
        EM.getTransaction().commit();

        EM.getTransaction().begin();
        final Person loadedPerson = EM.find(Person.class, ID);
        EM.getTransaction().commit();
        assertNotNull(loadedPerson);
        assertEquals(ID, loadedPerson.getId());
        assertEquals(NAME, loadedPerson.getName());
    }

}
