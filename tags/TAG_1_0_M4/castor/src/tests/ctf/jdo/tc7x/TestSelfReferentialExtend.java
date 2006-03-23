/*
 * Copyright 2005 Werner Guttmann
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
 *
 * $Id$
 *
 */

package ctf.jdo.tc7x;

import java.util.Iterator;
import java.util.List;

import jdo.JDOCategory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import harness.CastorTestCase;
import harness.TestHarness;

/**
 * Tests that modification to read only objects are not persist in the 
 * database.
 */
public final class TestSelfReferentialExtend extends CastorTestCase {

    private static final Log LOG = LogFactory.getLog(TestSelfReferentialExtend.class);

    private JDOCategory _category;

    /**
     * Constructor
     * @param category the test suite that this test case belongs
     */
    public TestSelfReferentialExtend(final TestHarness category) {
        super(category, "TC75", "self-referential relation test with extend hierarchies");
        _category = (JDOCategory) category;
    }

    /**
     * Tests that modification to read only objects are not persist in the 
     * database.
     */
    public void runTest() throws Exception {
        testQueryParent();
        testLoadParent();
        testLoadChild();
    }

    
    /**
     * Test method.
     * @throws Exception For any exception thrown.
     */
    public void testQueryParent() throws Exception {
        Database db = _category.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT entity FROM "
                + SelfReferentialParent.class.getName() + " entity WHERE id = $1");
        query.bind(new Integer(1));
        QueryResults results = query.execute();
        
        SelfReferentialParent entity = (SelfReferentialParent) results.next();

        assertNotNull(entity);
        assertEquals(new Integer(1), entity.getId());
        
        List children = entity.getEntities();
        assertNotNull(children);
        assertEquals(2, children.size());
        
        Iterator childrenIterator = children.iterator();
        
        SelfReferentialParent child = (SelfReferentialParent) childrenIterator.next();
        assertNotNull(child);
        assertEquals(2, child.getId().intValue());
        assertNotNull(child.getEntities());
        assertEquals(0,child.getEntities().size());

        child = (SelfReferentialParent) childrenIterator.next();
        assertNotNull(child);
        assertEquals(3, child.getId().intValue());
        assertNotNull(child.getEntities());
        assertEquals(0,child.getEntities().size());

        db.commit();
        db.close();
    }

    /**
     * Test method.
     * @throws Exception For any exception thrown.
     */
    public void testLoadParent() throws Exception {
        Database db = _category.getDatabase();
        db.begin();
        
        SelfReferentialParent entity = (SelfReferentialParent) db.load(SelfReferentialParent.class, new Integer(1));

        assertNotNull(entity);
        assertEquals(new Integer(1), entity.getId());
        
        List children = entity.getEntities();
        assertNotNull(children);
        assertEquals(2, children.size());
        
        Iterator childrenIterator = children.iterator();
        
        SelfReferentialParent child = (SelfReferentialParent) childrenIterator.next();
        assertNotNull(child);
        assertEquals(2, child.getId().intValue());
        assertNotNull(child.getEntities());
        assertEquals(0, child.getEntities().size());

        child = (SelfReferentialParent) childrenIterator.next();
        assertNotNull(child);
        assertEquals(3, child.getId().intValue());
        assertNotNull(child.getEntities());
        assertEquals(0, child.getEntities().size());

        db.commit();
        db.close();
    }

    /**
     * Test method.
     * @throws Exception For any exception thrown.
     */
    public void testLoadChild() throws Exception {
        Database db = _category.getDatabase();
        db.begin();
        
        SelfReferentialChild child = (SelfReferentialChild) db.load(SelfReferentialChild.class, new Integer(1));

        assertNotNull(child);
        assertEquals(new Integer(1), child.getId());
        
        List children = child.getEntities();
        assertNotNull(children);
        assertEquals(2, children.size());
        
        Iterator childrenIterator = children.iterator();
        
        SelfReferentialParent entity = (SelfReferentialParent) childrenIterator.next();
        assertNotNull(entity);
        assertEquals(2, entity.getId().intValue());
        assertNotNull(entity.getEntities());
        assertEquals(0, entity.getEntities().size());

        entity = (SelfReferentialParent) childrenIterator.next();
        assertNotNull(entity);
        assertEquals(3, entity.getId().intValue());
        assertNotNull(entity.getEntities());
        assertEquals(0, entity.getEntities().size());

        db.commit();
        db.close();
    }
    
}
