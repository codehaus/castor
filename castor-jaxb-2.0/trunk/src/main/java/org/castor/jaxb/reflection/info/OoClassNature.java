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
import java.util.List;

import org.castor.core.nature.BaseNature;

/**
 * @author joachim
 *
 */
public class OoClassNature extends BaseNature { //implements Nature {
    
    private ClassInfo classInfo;

    /**
     * @param classInfo the property holder to get the properties from
     */
    public OoClassNature(final ClassInfo classInfo) {
        super(classInfo);
        this.classInfo = classInfo;
    }
    /**
     * @return simply the class name
     * @see org.castor.core.nature.Nature#getId()
     */
    public String getId() {
        return getClass().getName();
    }
    /**
     * @param type the Class we're talking about
     */
    public void setType(final Class < ? > type) {
        setProperty(Properties.CLASS_TYPE, type);
    }
    /**
     * @return the Class described
     */
    public Class < ? > getType() {
        return (Class < ? > ) getProperty(Properties.CLASS_TYPE);
    }
    /**
     * @param supertype The super class.
     */
    public void setSupertype(final Class < ? > supertype) {
        setProperty(Properties.CLASS_SUPERTYPE, supertype);
    }
    /**
     * @return get the super class of the described class
     */
    public Class < ? > getSupertype() {
        return (Class < ? > ) getProperty(Properties.CLASS_SUPERTYPE);
    }
    /**
     * @param interfaces the Interfaces of the class
     */
    public void setInterfaces(final Class < ? > [] interfaces) {
        ArrayList<Class<?>> intrfcs = new ArrayList<Class<?>>();
        for(Class<?> i : interfaces) {
            intrfcs.add(i);
        }
        setProperty(Properties.CLASS_INTERFACES, intrfcs);
    }
    /**
     * @return get the interfaces of the described class
     */
    public List < Class < ? > > getInterfaces() {
        return (List < Class < ? > > ) getProperty(Properties.CLASS_INTERFACES);
    }
    /**
     * @param hasPublicEmptyConstructor hasPublicEmptyConstructor
     */
    public void setHasPublicEmptyConstructor(final boolean hasPublicEmptyConstructor) {
        setProperty(Properties.CLASS_HAS_PUBLIC_EMPTY_CONSTRUCTOR, 
                new Boolean(hasPublicEmptyConstructor));
    }
    /**
     * @return hasPublicEmptyConstructor
     */
    public boolean isHasPublicEmptyConstructor() {
        Boolean b = (Boolean) getProperty(Properties.CLASS_HAS_PUBLIC_EMPTY_CONSTRUCTOR);
        if (b == null || Boolean.FALSE.equals(b)) {
            return false;
        }
        return true;
    }
    
    /**
     * The class name which had been set or Class.getName if nothing
     * have been set.
     * @return the class name set or Class.getName()
     */
    public String getClassName() {
        return classInfo.getClassName();
    }

    /** . */
    private static interface Properties {
        /** . */
        String CLASS_NAME = "CLASS_NAME";
        /** . */
        String CLASS_HAS_PUBLIC_EMPTY_CONSTRUCTOR = "CLASS_HAS_PUBLIC_EMPTY_CONSTRUCTOR";
        /** . */
        String CLASS_INTERFACES = "CLASS_INTERFACES";
        /** . */
        String CLASS_SUPERTYPE = "CLASS_SUPERTYPE";
        /** . */
        String CLASS_TYPE = "CLASS_TYPE";
    }
}
