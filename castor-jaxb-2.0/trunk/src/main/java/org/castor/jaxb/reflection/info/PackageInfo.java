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


/**
 * Class PacakgeInfo.<br/>
 * The package info for a class.
 * 
 * @author Joachim Grueneis, jgr uen eisATg mailDOTcom
 */
public class PackageInfo extends BaseInfo {
    /**
     * The name of the package.
     */
    private String _packageName;
    
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
}
