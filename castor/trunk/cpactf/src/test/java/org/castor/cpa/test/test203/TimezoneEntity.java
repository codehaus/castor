/*
 * Copyright 2009 Udai Gupta, Ralf Joachim
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
package org.castor.cpa.test.test203;

import java.util.Date;

import org.junit.Ignore;

@Ignore
public final class TimezoneEntity {
    private Integer _id;
    private String _name;
    private Date _startDate;
    private Date _startTime;
    private Date _startTimestamp;
    
    public Integer getId() {
        return _id;
    }
    
    public void setId(final Integer id) {
        _id = id;
    }
    
    public String getName() {
        return _name;
    }
    
    public void setName(final String name) {
        _name = name;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(final Date startDate) {
        _startDate = startDate;
    }

    public Date getStartTime() {
        return _startTime;
    }

    public void setStartTime(final Date startTime) {
        _startTime = startTime;
    }

    public Date getStartTimestamp() {
        return _startTimestamp;
    }

    public void setStartTimestamp(final Date startTimestamp) {
        _startTimestamp = startTimestamp;
    }
}
