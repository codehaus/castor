/*
 * Copyright 2008 Joachim Grueneis
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

package org.castor.jaxb.reflection.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.castor.core.nature.PropertyHolder;

/**
 * Class ClassInfo.<br/>
 * Represents the information collected about a single class. It has associated
 * FieldInfo instances for each field identified in the class and a PackageInfo.
 */
public class ClassInfo implements PropertyHolder {
    /**
     * The name of the class.
     */
    private String _className;
    /**
     * All fields.
     */
    private List<FieldInfo> _fieldInfos = new ArrayList<FieldInfo>();
    /**
     * The package of the class.
     */
    private PackageInfo _packageInfo;

    /**
     * Map holding the properties set and read by Natures.
     */
    private Map<String, Object> _properties = new HashMap<String, Object>();

    /**
     * Set holding applicable natures.
     */
    private Set<String> _natures = new HashSet<String>();

    /**
     * A ClassInfo has always a class name.
     * @param className the class name
     */
    public ClassInfo(final String className) {
        _className = className;
        addNature(OoClassNature.class.getName());
        addNature(JaxbClassNature.class.getName());
    }

    /**
     * To get the class name.
     * @return the class name.
     */
    public String getClassName() {
        return _className;
    }

    /**
     * To add another FieldInfo.
     * @param fieldInfo the FieldInfo to add
     */
    public final void addFieldInfo(final FieldInfo fieldInfo) {
        _fieldInfos.add(fieldInfo);
    }

    /**
     * Get all FieldInfo s.
     * @return List of FieldInfo s
     */
    public final List<FieldInfo> getFieldInfos() {
        return _fieldInfos;
    }

    /**
     * @param fieldName the name of the field to find
     * @return null if not found or the field identified
     */
    public FieldInfo getFieldInfo(final String fieldName) {
        FieldInfo foundFieldInfo = null;
        for (FieldInfo fi : _fieldInfos) {
            if (fi.getFieldName().equals(fieldName)) {
                foundFieldInfo = fi;
            }
        }
        return foundFieldInfo;
    }
    
    /**
     * To get the package info for the class.
     * @return the package info
     */
    public final PackageInfo getPackageInfo() {
        return _packageInfo;
    }
    
    /**
     * To set the package info.
     * @param packageInfo the package info
     */
    public final void setPackageInfo(final PackageInfo packageInfo) {
        _packageInfo = packageInfo;
    }

    /**
     * @param name the name of the property to read
     * @return the property
     * @see org.castor.core.nature.PropertyHolder#getProperty(java.lang.String)
     */
    public final Object getProperty(final String name) {
        return _properties.get(name);
    }

    /**
     * @param name the name of the property to set
     * @param value the value to set the property to
     * @see org.castor.core.nature.PropertyHolder#setProperty(java.lang.String, java.lang.Object)
     */
    public final void setProperty(final String name, final Object value) {
        _properties.put(name, value);
    }

    /**
     * @param nature the name of the Nature to register
     * @see org.castor.core.nature.NatureExtendable#addNature(java.lang.String)
     */
    public final void addNature(final String nature) {
        _natures.add(nature);
    }

    /**
     * @param nature the name of the Nature to check
     * @return true if the Nature was registered before
     * @see org.castor.core.nature.NatureExtendable#hasNature(java.lang.String)
     */
    public final boolean hasNature(final String nature) {
        return _natures.contains(nature);
    }

}
