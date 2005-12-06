package jdo.bug1240;

import java.util.Iterator;
import java.util.SortedSet;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

public final class TestSortedContainer extends TestCase {
    private static final String JDO_CONF_FILE = "jdo-conf.xml";
    private static final String DATABASE_NAME = "bugTemplate";
    
    private static Log LOG = null;
    
    private JDOManager _jdo = null;

    public static void main(final String[] args) throws Exception {
        TestSortedContainer test = new TestSortedContainer();
        test.setUp();
        test.testQueryEntityOne();
        test.tearDown();
    }
    
    public TestSortedContainer() {
        super();
    }
    
    public TestSortedContainer(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        LOG = LogFactory.getLog(TestSortedContainer.class);

        String config = getClass().getResource(JDO_CONF_FILE).toString();
        JDOManager.loadConfiguration(config, getClass().getClassLoader());
        _jdo = JDOManager.createInstance(DATABASE_NAME);
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Test method.
     * @throws Exception For any exception thrown.
     */
    public void testQueryEntityOne() throws Exception {
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT entity FROM "
                + SortedContainer.class.getName() + " entity WHERE id = $1");
        query.bind(new Integer(1));
        QueryResults results = query.execute();
        
        SortedContainer entity = null;
        
        entity = (SortedContainer) results.next();
        assertNotNull(entity);
        assertEquals(new Integer(1), entity.getId());
        assertNotNull (entity.getTwos());
        assertEquals(2, entity.getTwos().size());
 
        SortedSet twos = entity.getTwos();
        Iterator iterator = twos.iterator();
        
        SortedContainerItem two = null;
        int i = 1;
        while (iterator.hasNext()) {
            two = (SortedContainerItem) iterator.next();
            LOG.error(two);
            assertNotNull(two);
            assertEquals(new Integer(i), two.getId());
            i++;
        }

        db.commit();
        db.close();
    }
}
