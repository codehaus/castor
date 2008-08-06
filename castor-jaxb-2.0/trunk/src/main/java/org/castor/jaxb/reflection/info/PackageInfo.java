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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.castor.core.nature.PropertyHolder;

/**
 * Class PacakgeInfo.<br/>
 * The package info for a class.
 * 
 * @author Joachim Grueneis, jgr uen eisATg mailDOTcom
 */
public class PackageInfo implements PropertyHolder {
    /**
     * The name of the package.
     */
    private String _packageName;
    
    /**
     * Map holding the properties set and read by Natures.
     */
    private Map < String, Object > _properties = new HashMap < String, Object > ();
    
    /**
     * Set holding applicable natures.
     */
    private Set < String > _natures = new HashSet < String > ();

    /**
     * A package info instance has to have a package name.
     * @param packageName the package name
     */
    public PackageInfo(final String packageName) {
        super();
        _packageName = packageName;
        addNature(OoPackageNature.class.getName());
        addNature(JaxbPackageNature.class.getName());
    }
    
    /**
     * To get the package name.
     * @return the package name
     */
    public String getPackageName() {
        return _packageName;
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
