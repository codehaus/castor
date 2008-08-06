/*
 * Copyright 2007 Joachim Grueneis
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

import org.castor.core.nature.BaseNature;
import org.castor.core.nature.PropertyHolder;

/**
 * The package info class holds all package related information gathered
 * using reflection and concentrating on JAXB annotations and those that
 * somehow relevant when working with JAXB. It is no reflection wrapper
 * just some class to hold information.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class OoPackageNature extends BaseNature {

    /**
     * @param holder the holder of the nature properties
     */
    public OoPackageNature(final PropertyHolder holder) {
        super(holder);
    }

    /**
     * @return the unique identifier of this nature
     * @see org.castor.core.nature.Nature#getId()
     */
    public String getId() {
        return getClass().getName();
    }

    /**
     * @param pkg the Package to describe
     */
    public void setPackage(final Package pkg) {
        setProperty(Properties.PACKAGE, pkg);
    }
    
    /**
     * @return the Package to describe
     */
    public Package getPackage() {
        return (Package) getProperty(Properties.PACKAGE);
    }
    
    static interface Properties {

        String PACKAGE = "PACKAGE";
    }
}
