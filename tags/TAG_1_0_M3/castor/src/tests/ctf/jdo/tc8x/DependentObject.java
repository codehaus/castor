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

package ctf.jdo.tc8x;

import org.exolab.castor.jdo.TimeStampable;

/**
 * Dependent object for test case in the ctf.jdo.tc8x package
 * @author nstuart
 */
public class DependentObject implements TimeStampable{
    
    private int id;
    private String descrip;
    private MasterObject master;
    
    private long timestamp;
    
    /** Creates a new instance of Dependent */
    public DependentObject() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public MasterObject getMaster() {
        return master;
    }

    public void setMaster(MasterObject master) {
        this.master = master;
    }

    public void jdoSetTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long jdoGetTimeStamp() {
        return timestamp;
    }
    
}
