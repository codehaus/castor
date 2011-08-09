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
 * Class FieldInfo.<br/>
 * Contains the information for a single field - either for a field of a class or
 * a field interpreted from the methods.
 * 
 * @author Joachim Grueneis, jgrueneisATgmailDOTcom
 */
public class FieldInfo extends BaseInfo {
    /**
     * The field name.
     */
    private String _fieldName;
    
    /**
     * The owning parent class.
     */
    private ClassInfo _parentClassInfo;
    
    /**
     * To create a new FieldInfo with just a name.
     * @param fieldName the name of the field
     */
    public FieldInfo(final String fieldName) {
        super();
        _fieldName = fieldName;
        addNature(OoFieldNature.class.getName());
        addNature(JaxbFieldNature.class.getName());
    }

    public ClassInfo getParentClassInfo() {
        return _parentClassInfo;
    }

    public void setParentClassInfo(ClassInfo parentClassInfo) {
        this._parentClassInfo = parentClassInfo;
    }

    /**
     * To get the field name.
     * @return the field name
     */
    public String getFieldName() {
        return _fieldName;
    }

}
