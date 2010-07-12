package org.castor.jpa.annotations.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceException;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jdo.jpa.annotations.Cache;
import org.castor.jdo.jpa.annotations.CacheProperty;

/**
 * This class is part of the functional test suite for the castor-jpa project
 * and assists in testing JPA annotation support.
 * 
 */

@Entity
@Table(name = "person")
@NamedNativeQuery(name = "findRonnyJames", query = "SELECT * FROM person WHERE id=1000")
@NamedQuery(name = "findPersonByName", query = "SELECT p FROM org.castor.jpa.annotations.model.Person p WHERE p.name = $1")
@Cache( { @CacheProperty(key = "type", value = "none") })
public class Person {

    private static final Log LOG = LogFactory.getLog(Person.class);

    public static final long QUERY_TEST_ID = 1000L;
    public static final long CALLBACK_TEST_ID = 666L;
    public static final String PREPERSIST_TEST_NAME = "Minime";
    public static final String POSTPERSIST_TEST_NAME = "Austin Powers";
    public static final String PREREMOVE_TEST_NAME = "Dr. Evil";

    private long id;
    private String name;

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person() {
    }

    @Id
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // methods for callback tests:

    @PrePersist
    protected void validateCreation() {
        if (this.name.equals(Person.PREPERSIST_TEST_NAME)) {
            throw new PersistenceException(String.format(
                    "Person mustn't be called %s.", this.name));
        }
    }

    @PrePersist
    protected void clarkKentTransformsToSuperman() {
        if (this.name.equals("Clark Kent"))
            this.name = "Superman";
    }

    @PostPersist
    protected void validatePersistence() {
        if (this.name.equals(Person.POSTPERSIST_TEST_NAME)) {
            throw new PersistenceException(String.format(
                    "Person mustn't be called %s.", this.name));
        }
    }

    @PostPersist
    protected void BruceWayneTransformsToBatman() {
        if (this.name.equals("Bruce Wayne")) {
            this.name = "Batman";
        }
    }

    @PreRemove
    protected void validateRemoval() {
        if (this.name.equals(Person.PREREMOVE_TEST_NAME)) {
            throw new PersistenceException(String.format(
                    "Person mustn't be called %s.", this.name));
        }
    }

}
