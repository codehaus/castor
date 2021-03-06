/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999-2001 (C) Intalio, Inc. All Rights Reserved.
 */
package ctf.jdo.tc1x;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import harness.CastorTestCase;
import harness.TestHarness;

import jdo.JDOCategory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.persist.spi.Identity;

/**
 * Test for multiple columns primary Keys. These tests create data objects
 * model of different types that make uses of primary keys, modify the objects
 * and the relationship between objects and verify if changes made is persisted
 * properly.
 */
public final class TestMultiPrimKeysOnly extends CastorTestCase {
    /**
     * The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     * Commons Logging</a> instance used for all logging.
     */
    private static final Log LOG = LogFactory.getLog(TestMultiPrimKeysOnly.class);
    
    private JDOCategory    _category;

    private Database       _db;

    private Connection     _conn;

    /**
     * Constructor
     *
     * @param category The test suite of these test cases
     */
    public TestMultiPrimKeysOnly(final TestHarness category) {
        super(category, "TC15a", "Multiple columns primary keys only tests");
        _category = (JDOCategory) category;
    }

    /**
     * Get a JDO database and direct JDBC connection. Clean up old values in
     * tables using JDBC conneciton and create different types of data object
     * that make use of multiple columns primary keys.
     */
    public void setUp() throws PersistenceException, SQLException {
        _db = _category.getDatabase();
        _conn = _category.getJDBCConnection();
        _conn.setAutoCommit(false);

        // delete everything directly
        LOG.debug("Delete everything");
        Statement stmt = _conn.createStatement();
        stmt.executeUpdate("DELETE FROM tc1x_pks_only");
        
        _conn.commit();
        _conn.close();
    }
    
    public void createAndLoadOnly() throws PersistenceException {
	_db.begin();
	PrimaryKeysOnly only = new PrimaryKeysOnly();
	only.setFirstName("werner");
	only.setLastName("guttmann");
	_db.create(only);
	_db.commit();
	
	_db.begin();
	Identity identity = new Identity(new Object[] {"werner", "guttmann"});
	PrimaryKeysOnly searched = (PrimaryKeysOnly) _db.load(PrimaryKeysOnly.class, identity);
	assertNotNull(searched);
	assertEquals("werner", searched.getFirstName());
	assertEquals("guttmann", searched.getLastName());
	_db.commit();
	
	_db.begin();
	PrimaryKeysOnly toDelete = (PrimaryKeysOnly) _db.load(PrimaryKeysOnly.class, identity);
	assertNotNull(toDelete);
	_db.remove(toDelete);
	_db.commit();
    }

    /**
     * Release the JDO Database
     */
    public void tearDown() throws PersistenceException {
        if (_db.isActive()) { _db.rollback(); }
        _db.close();
    }

    protected void runTest() throws Throwable {
	createAndLoadOnly();
    }
    
    
}
