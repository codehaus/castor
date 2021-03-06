package org.castor.cpa.test.test1206;

import org.exolab.castor.jdo.TimeStampable;

/**
 * @author cwichoski
 */
public final class Country implements TimeStampable  {
    private String _oid;
    private String _name;
    private long _timeStamp;
    
    public String getOid() { return _oid; }
    public void setOid(final String oid) { _oid = oid; }

    public String getName() { return _name; }
    public void setName(final String name) { _name = name; }
    
    public long jdoGetTimeStamp() { return _timeStamp; }
    public void jdoSetTimeStamp(final long timeStamp) {
        _timeStamp = timeStamp;
    }

    public String toString() {
        return super.toString() + " { oid: '" + getOid() + "', name: '" + getName()
            + "', timestamp: " + jdoGetTimeStamp() + " }";
    }
}
