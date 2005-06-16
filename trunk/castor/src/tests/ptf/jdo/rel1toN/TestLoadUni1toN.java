package ptf.jdo.rel1toN;

import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.engine.DatabaseImpl;
import org.exolab.castor.mapping.AccessMode;

public final class TestLoadUni1toN extends TestCase {
    private static final String JDO_CONF_FILE = "uni-jdo-conf.xml";
    private static final String DATABASE_NAME = "rel1toN_uni";
    
    private static final DecimalFormat df = new DecimalFormat("#,##0");
    
    private static final Log LOG = LogFactory.getLog(TestLoadUni1toN.class);
    private static boolean _logHeader = false;
    
    private JDOManager _jdo = null;
    
    private String[]   _tests = new String[8]; 
    private long[][]   _times = new long[8][5];

    private DatabaseImpl db = null;
    private OQLQuery queryState = null;
    private OQLQuery queryStateOID = null;
    private OQLQuery queryEquipment = null;
    private OQLQuery queryEquipmentOID = null;
    private OQLQuery queryService = null;
    private OQLQuery queryServiceOID = null;
    
    public static Test suite() throws Exception {
        String config = TestLoadUni1toN.class.getResource(JDO_CONF_FILE).toString();
        JDOManager.loadConfiguration(config, TestLoadUni1toN.class.getClassLoader());
        
        TestSuite suite = new TestSuite("Test load 1:n with unidirectional mapping");

        suite.addTest(new TestLoadUni1toN("testReadWriteEmpty"));
        suite.addTest(new TestLoadUni1toN("testReadWriteCached"));
        suite.addTest(new TestLoadUni1toN("testReadWriteOidEmpty"));
        suite.addTest(new TestLoadUni1toN("testReadWriteOidCached"));

        suite.addTest(new TestLoadUni1toN("testReadOnlyEmpty"));
        suite.addTest(new TestLoadUni1toN("testReadOnlyCached"));
        suite.addTest(new TestLoadUni1toN("testReadOnlyOidEmpty"));
        suite.addTest(new TestLoadUni1toN("testReadOnlyOidCached"));

        suite.addTest(new TestLoadUni1toN("testReadOnlyOidOnly"));

        return suite;
    }

    public TestLoadUni1toN() {
        super();
    }
    
    public TestLoadUni1toN(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        if (!_logHeader) {
            LOG.info(format("", "begin", "result", "iterate", "commit", "close"));
            _logHeader = true;
        }

        _jdo = JDOManager.createInstance(DATABASE_NAME);
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testReadWriteEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Locked.class.getName() + " o order by o.id");
        QueryResults results = query.execute();
        
        long result = System.currentTimeMillis();
        
        initIterateQueries();

        int count = 0;
        while (results.hasMore()) {
            iterateStates((Locked) results.next(), Database.Shared);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteEmpty",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadWriteCached() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Locked.class.getName() + " o order by o.id");
        QueryResults results = query.execute();
        
        long result = System.currentTimeMillis();
        
        initIterateQueries();

        int count = 0;
        while (results.hasMore()) {
            iterateStates((Locked) results.next(), Database.Shared);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteCached",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadOnlyEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Locked.class.getName() + " o order by o.id");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        initIterateQueries();

        int count = 0;
        while (results.hasMore()) {
            iterateStates((Locked) results.next(), Database.ReadOnly);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyEmpty",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadOnlyCached() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Locked.class.getName() + " o order by o.id");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        initIterateQueries();

        int count = 0;
        while (results.hasMore()) {
            iterateStates((Locked) results.next(), Database.ReadOnly);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyCached",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }

    public void testReadWriteOidEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_LOCKED.ID as ID " +
                "from PTF_LOCKED order by PTF_LOCKED.ID " +
                "AS ptf.jdo.rel1toN.OID");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        initIterateQueriesOID();

        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            iterateStatesOID((Locked) db.load(Locked.class, oid.getId()), Database.Shared);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteOidEmpty",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadWriteOidCached() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_LOCKED.ID as ID " +
                "from PTF_LOCKED order by PTF_LOCKED.ID " +
                "AS ptf.jdo.rel1toN.OID");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        initIterateQueriesOID();

        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            iterateStatesOID((Locked) db.load(Locked.class, oid.getId()), Database.Shared);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteOidCached",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadOnlyOidEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_LOCKED.ID as ID " +
                "from PTF_LOCKED order by PTF_LOCKED.ID " +
                "AS ptf.jdo.rel1toN.OID");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        initIterateQueriesOID();

        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            iterateStatesOID((Locked) db.load(Locked.class, oid.getId(), Database.ReadOnly),
                    Database.ReadOnly);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyOidEmpty",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadOnlyOidCached() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_LOCKED.ID as ID " +
                "from PTF_LOCKED order by PTF_LOCKED.ID " +
                "AS ptf.jdo.rel1toN.OID");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        initIterateQueriesOID();

        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            iterateStatesOID((Locked) db.load(Locked.class, oid.getId(), Database.ReadOnly),
                    Database.ReadOnly);

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyOidCached",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }
    
    public void testReadOnlyOidOnly() throws Exception {
        long start = System.currentTimeMillis();
        
        db = (DatabaseImpl) _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_LOCKED.ID as ID " +
                "from PTF_LOCKED order by PTF_LOCKED.ID " +
                "AS ptf.jdo.rel1toN.OID");
        QueryResults results = query.execute(Database.ReadOnly);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();

            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyOidOnly",
                         df.format(begin - start),
                         df.format(result- begin),
                         df.format(iterate - result),
                         df.format(commit - iterate),
                         df.format(close - commit)));
    }

    private void initIterateQueries() throws Exception {
        queryState = db.getOQLQuery(
                "select o from " + State.class.getName() + " o " +
                "where o.locked=$1 order by o.id");
        queryEquipment = db.getOQLQuery(
                "select o from " + Equipment.class.getName() + " o " +
                "where o.state=$1 order by o.id");
        queryService = db.getOQLQuery(
                "select o from " + Service.class.getName() + " o " +
                "where o.equipment=$1 order by o.id");
    }
    
    private void initIterateQueriesOID() throws Exception {
        queryStateOID = db.getOQLQuery(
                "CALL SQL select PTF_STATE.ID as ID from PTF_STATE " +
                "where PTF_STATE.LOCKED_ID=$1 order by PTF_STATE.ID " +
                "AS ptf.jdo.rel1toN.OID");
        queryEquipmentOID = db.getOQLQuery(
                "CALL SQL select PTF_EQUIPMENT.ID as ID from PTF_EQUIPMENT " +
                "where PTF_EQUIPMENT.STATE_ID=$1 order by PTF_EQUIPMENT.ID " +
                "AS ptf.jdo.rel1toN.OID");
        queryServiceOID = db.getOQLQuery(
                "CALL SQL select PTF_SERVICE.ID as ID from PTF_SERVICE " +
                "where PTF_SERVICE.EQUIPMENT_ID=$1 order by PTF_SERVICE.ID " +
                "AS ptf.jdo.rel1toN.OID");
    }
    
    private void iterateStates(final Locked locked, final AccessMode mode)
    throws Exception {
        queryState.bind(locked.getId());
        QueryResults results = queryState.execute(mode);
        
        while (results.hasMore()) {
            iterateEquipments((State) results.next(), mode);
        }
    }
    
    private void iterateStatesOID(final Locked locked, final AccessMode mode)
    throws Exception {
        queryStateOID.bind(locked.getId());
        QueryResults results = queryStateOID.execute(mode);
        
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            iterateEquipmentsOID((State) db.load(State.class, oid.getId(), mode), mode);
        }
    }
    
    private void iterateEquipments(final State state, final AccessMode mode)
    throws Exception {
        queryEquipment.bind(state.getId());
        QueryResults results = queryEquipment.execute(mode);
        
        while (results.hasMore()) {
            iterateServices((Equipment) results.next(), mode);
        }
    }
    
    private void iterateEquipmentsOID(final State state, final AccessMode mode)
    throws Exception {
        queryEquipmentOID.bind(state.getId());
        QueryResults results = queryEquipmentOID.execute(Database.ReadOnly);
        
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            iterateServicesOID((Equipment) db.load(Equipment.class, oid.getId(), mode),
                    mode);
        }
    }
    
    private void iterateServices(final Equipment equipment, final AccessMode mode)
    throws Exception {
        queryService.bind(equipment.getId());
        QueryResults results = queryService.execute(mode);
        
        while (results.hasMore()) {
            Service service = (Service) results.next();
        }
    }
    
    private void iterateServicesOID(final Equipment equipment, final AccessMode mode)
    throws Exception {
        queryServiceOID.bind(equipment.getId());
        QueryResults results = queryServiceOID.execute(Database.ReadOnly);
        
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            Service service = (Service) db.load(Service.class, oid.getId(), mode);
        }
    }
    
    private static String format(String head, String begin, String result,
                                 String iterate, String commit, String close) {
        
        StringBuffer sb = new StringBuffer();
        sb.append(format(head, 20, true));
        sb.append(format(begin, 10, false));
        sb.append(format(result, 10, false));
        sb.append(format(iterate, 10, false));
        sb.append(format(commit, 10, false));
        sb.append(format(close, 10, false));
        return sb.toString();
    }
    
    private static String format(String str, int len, boolean after) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            sb.append(str);
            for (int i = str.length(); i < len; i++) {
                if (after) {
                    sb.append(' ');
                } else {
                    sb.insert(0, ' ');
                }
            }
        } else {
            for (int i = 0; i < len; i++) {
                if (after) {
                    sb.append(' ');
                } else {
                    sb.insert(0, ' ');
                }
            }
        }
        return sb.toString();
    }
}
