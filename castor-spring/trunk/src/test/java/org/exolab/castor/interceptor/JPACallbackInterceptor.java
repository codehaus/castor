package org.exolab.castor.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.spi.CallbackInterceptor;

public class JPACallbackInterceptor implements CallbackInterceptor {
    
    private final Log log = LogFactory.getLog(getClass());

    public void created(Object object) throws Exception {
        log.info("... created invoked");
    }

    public void creating(Object object, Database db) throws Exception {
        // TODO Auto-generated method stub

    }

    public Class<?> loaded(Object object, AccessMode accessMode)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public void releasing(Object object, boolean committed) {
        // TODO Auto-generated method stub

    }

    public void removed(Object object) throws Exception {
        // TODO Auto-generated method stub

    }

    public void removing(Object object) throws Exception {
        // TODO Auto-generated method stub

    }

    public void storing(Object object, boolean modified) throws Exception {
        // TODO Auto-generated method stub

    }

    public void updated(Object object) throws Exception {
        // TODO Auto-generated method stub

    }

    public void using(Object object, Database db) {
        // TODO Auto-generated method stub

    }

}
