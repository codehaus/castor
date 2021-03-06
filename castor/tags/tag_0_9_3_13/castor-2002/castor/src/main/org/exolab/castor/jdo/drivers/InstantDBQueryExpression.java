package org.exolab.castor.jdo.drivers;

import org.exolab.castor.jdo.engine.JDBCSyntax;
import org.exolab.castor.persist.spi.PersistenceFactory;
import java.util.Enumeration;
import java.util.Hashtable;

/**
  *  specialized generic driver for InstantDB database.
  *
  *  @author I. Burak Ozyurt
  *  @version 1.0
  */

public class InstantDBQueryExpression extends JDBCQueryExpression {

    public InstantDBQueryExpression( PersistenceFactory factory ) {
        super( factory );
    }


    public String getStatement( boolean lock )
    {
        // Do not use FOR UPDATE to lock query.
        return getStandardStatement( lock, false ).toString();
    }
}