



package org.exolab.castor.persist;

import java.util.Vector;



public interface Lazy {

    public Object[] getIdentities();
 
    public Vector getIdentitiesList();

    public boolean isLoaded();

    public Class getBaseClass();
}
