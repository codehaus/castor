/*
 * Copyright 2011 Johannes Venzke, Ralf Joachim
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
 */

package org.castor.cpa.test.test3183;

import org.exolab.castor.jdo.TimeStampable;

/**
 * @version $Id$
 * @author <a href="johannes DOT venzke AT revival DOT de">Johannes Venzke</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 */
public class PolyDoc implements TimeStampable {
    private Integer _id;
    private String _name;
    private long _timeStamp;
    
    public final int getId() { return _id; }
    public final void setId(final int id) { _id = id; }

    public final String getName() { return _name; }
    public final void setName(final String name) { _name = name; }
    
    /**
     * @see org.exolab.castor.jdo.TimeStampable#jdoSetTimeStamp(long)
     */
    public final void jdoSetTimeStamp(final long timestamp) {
        _timeStamp = timestamp;
    }

    /**
     * @see org.exolab.castor.jdo.TimeStampable#jdoGetTimeStamp()
     */
    public final long jdoGetTimeStamp() {
        return _timeStamp;
    }
    
    public final String toString () {
        return this.getClass().getName() + "/" + getId();
    }
}
