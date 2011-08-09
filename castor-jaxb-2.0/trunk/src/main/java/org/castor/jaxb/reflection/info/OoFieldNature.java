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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.castor.core.nature.BaseNature;

/**
 * A Nature that provides access to the OO properties like Java-field,
 * Java-methods or Java-enum.
 * 
 * @author Joachim Grueneis, jgrueneisATcodehausDOTorg
 */
public class OoFieldNature extends BaseNature {

    /**
     * Besides using FieldInfo as PropertyHolder by handling it
     * down to BaseNature... it is also needed directly.
     */
    private FieldInfo _fieldInfo;

    /**
     * @param fieldInfo the holder of the nature properties
     */
    public OoFieldNature(final FieldInfo fieldInfo) {
        super(fieldInfo);
        _fieldInfo = fieldInfo;
    }

    /**
     * @return the unique identifier of this nature
     * @see org.castor.core.nature.Nature#getId()
     */
    public String getId() {
        return getClass().getName();
    }

    /**
     * @param field
     *            the Field of this property
     */
    public void setField(final Field field) {
        setProperty(Properties.FIELD, field);
    }

    /**
     * @return the field
     */
    public Field getField() {
        return (Field) getProperty(Properties.FIELD);
    }

    /**
     * @return true if the field is an enumeration constant
     */
    public boolean isEnumConstant() {
        return getField().isEnumConstant();
    }

    /**
     * @return the methodAdd
     */
    public Method getMethodAdd() {
        return (Method) getProperty(Properties.METHOD_ADD);
    }

    /**
     * @param methodAdd
     *            the methodAdd to set
     */
    public void setMethodAdd(final Method methodAdd) {
        setProperty(Properties.METHOD_ADD, methodAdd);
    }

    /**
     * @return the methodGet
     */
    public Method getMethodGet() {
        return (Method) getProperty(Properties.METHOD_GET);
    }

    /**
     * @param methodGet
     *            the methodGet to set
     */
    public void setMethodGet(final Method methodGet) {
        setProperty(Properties.METHOD_GET, methodGet);
    }

    /**
     * @return the methodSet
     */
    public Method getMethodSet() {
        return (Method) getProperty(Properties.METHOD_SET);
    }

    /**
     * @param methodSet
     *            the methodSet to set
     */
    public void setMethodSet(final Method methodSet) {
        setProperty(Properties.METHOD_SET, methodSet);
    }

    /**
     * @return the methodCreate
     */
    public Method getMethodCreate() {
        return (Method) getProperty(Properties.METHOD_CREATE);
    }

    /**
     * @param methodCreate
     *            the methodCreate to set
     */
    public void setMethodCreate(final Method methodCreate) {
        setProperty(Properties.METHOD_CREATE, methodCreate);
    }

    /**
     * @return the methodIs
     */
    public Method getMethodIs() {
        return (Method) getProperty(Properties.METHOD_IS);
    }

    /**
     * @param methodIs
     *            the methodIs to set
     */
    public void setMethodIs(final Method methodIs) {
        setProperty(Properties.METHOD_IS, methodIs);
    }

    /**
     * @return true if the field can be multivalued
     */
    public boolean isMultivalue() {
        Boolean multivalued = (Boolean) getProperty(Properties.MULTIVALUED);
        if (multivalued == null || Boolean.FALSE.equals(multivalued)) {
            return false;
        }
        return true;
    }

    /**
     * Marks the field as multivalued.
     * 
     * @param multivalued
     *            true to mark the field as multivalued
     */
    public void setMultivalued(final boolean multivalued) {
        setProperty(Properties.MULTIVALUED, Boolean.valueOf(multivalued));
    }

    /**
     * To set the generic type. Used for multivalued fields.
     * 
     * @param genericType
     *            the generic type of the field
     */
    public void setGenericType(final Type genericType) {
        setProperty(Properties.GENERIC_TYPE, genericType);
    }

    /**
     * To get the generic type of the field. Used for multivalued fields.
     * 
     * @return the generic type of the field
     */
    public Type getGenericType() {
        return (Type) getProperty(Properties.GENERIC_TYPE);
    }
    
    /**
     * To get the field name.
     * @return the field name
     */
    public String getFieldName() {
        return _fieldInfo.getFieldName();
    }

    /**
     * I'm not sure if I really want to keep this method... it is not only a
     * question of 'if methods exist' but also of the access mode to use...
     * 
     * @deprecated
     * @return true if it is a pure field without access methods
     */
    public boolean isPureField() {
        return (getMethodGet() == null && getMethodSet() == null);
    }

    public FieldInfo getFieldInfo() {
        return _fieldInfo;
    }
    
    /**
     * An interface holding the string constants for properties.
     */
    static interface Properties {
        /** . */
        String MULTIVALUED = "MULTIVALUED";
        /** . */
        String GENERIC_TYPE = "GENERIC_TYPE";
        /** . */
        String METHOD_GET = "METHOD_GET";
        /** . */
        String METHOD_SET = "METHOD_SET";
        /** . */
        String METHOD_CREATE = "METHOD_CREATE";
        /** . */
        String METHOD_IS = "METHOD_IS";
        /** . */
        String METHOD_ADD = "METHOD_ADD";
        /** . */
        String FIELD = "FIELD";
    }
}
