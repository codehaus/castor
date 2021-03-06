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
package org.castor.cpa.test.test20;

/**
 * The base class of all test object for key generators.
 */
public abstract class AbstractKeyGenObjectInteger {
    public static final String DEFAULT_ATTR = "attr";
    public static final String DEFAULT_EXTENDS = "ext";

    private Integer _id;
    private String _attr;
    private String _ext;

    public AbstractKeyGenObjectInteger() {
        _attr = DEFAULT_ATTR;
        _ext = DEFAULT_EXTENDS;
    }

    public final void setId(final Integer id) { _id = id; }
    public final Integer getId() { return _id; }

    public final void setAttr(final String attr) { _attr = attr; }
    public final String getAttr() { return _attr; }

    public final void setExt(final String ext) { _ext = ext; }
    public final String getExt() { return _ext; }

    public final String toString() {
        String str = ((_id == null) ? "null" : _id.toString());
        str = str + " / " + _attr + " / " + _ext;
        return str;
    }
}
