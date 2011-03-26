package org.exolab.castor.dao;

import org.exolab.castor.jdo.TimeStampable;

public class Product implements TimeStampable {

    private int id;
    private String name;
    private long timeStamp;
    
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void jdoSetTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public long jdoGetTimeStamp() {
        return this.timeStamp;
    }
}
