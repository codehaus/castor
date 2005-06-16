package ptf.jdo.rel1toN;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

public final class TestRemove extends TestCase {
    private static final String JDO_CONF_FILE = "uni-jdo-conf.xml";
    private static final String DATABASE_NAME = "rel1toN_uni";
    
    private static final Log LOG = LogFactory.getLog(TestRemove.class);
    
    private JDOManager _jdo = null;

    public static Test suite() throws Exception {
        String config = TestRemove.class.getResource(JDO_CONF_FILE).toString();
        JDOManager.loadConfiguration(config, TestRemove.class.getClassLoader());

        TestSuite suite = new TestSuite("Remove ptf.jdo.rel1toN test objects");

        suite.addTest(new TestRemove("testRemoveService"));
        suite.addTest(new TestRemove("testRemoveEquipment"));
        suite.addTest(new TestRemove("testRemoveType"));
        suite.addTest(new TestRemove("testRemoveSupplier"));
        suite.addTest(new TestRemove("testRemoveReason"));
        suite.addTest(new TestRemove("testRemoveDepartment"));
        suite.addTest(new TestRemove("testRemoveState"));

        return suite;
    }

    public TestRemove() {
        super();
    }
    
    public TestRemove(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        _jdo = JDOManager.createInstance(DATABASE_NAME);
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testRemoveState() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + State.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " state objects in " + (System.currentTimeMillis() - time) + "ms.");
    }

    public void testRemoveDepartment() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + Department.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " department objects in " + (System.currentTimeMillis() - time) + "ms.");
    }

    public void testRemoveReason() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + Reason.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " reason objects in " + (System.currentTimeMillis() - time) + "ms.");
    }

    public void testRemoveSupplier() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + Supplier.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " supplier objects in " + (System.currentTimeMillis() - time) + "ms.");
    }

    public void testRemoveType() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + Type.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " type objects in " + (System.currentTimeMillis() - time) + "ms.");
    }

    public void testRemoveEquipment() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + Equipment.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " equipment objects in " + (System.currentTimeMillis() - time) + "ms.");
    }

    public void testRemoveService() throws Exception {
        long time = System.currentTimeMillis();
        int count = 0;
        
        Database db = _jdo.getDatabase();
        db.begin();
        
        OQLQuery query = db.getOQLQuery("SELECT o FROM " + Service.class.getName() + " o");
        QueryResults results = query.execute();
        while(results.hasMore()) {
            db.remove(results.next());
            count++;
        }
        
        db.commit();
        db.close();

        LOG.info("Removed " + count + " service objects in " + (System.currentTimeMillis() - time) + "ms.");
    }
}
