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
package org.castor.engine;

/**
 * Holds information about table names and counters.
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 */
public class CounterRef {
    private int     _counter;
    
    private String  _tableName;
    
    public String getTableName() {
        return _tableName;
    }
    public void setTableName(final String tableName) {
        _tableName = tableName;
    }
    public int getCounter() {
        return _counter;
    }
    public void setCounter(final int counter) {
        _counter = counter;
    }
}
