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
package org.castor.jaxb.reflection;

import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;

/**
 * Meant to be used when creating a new XMLFieldDescriptor by JAXB reflection. It is meant
 * as data container without logic.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class JAXBFieldDescriptorImpl extends XMLFieldDescriptorImpl implements XMLFieldDescriptor {
    /**
     * The constructor to use in the JAXB implementation.
     * 
     * @param fieldType fieldType
     * @param fieldName fieldName
     * @param xmlName xmlName
     * @param nodeType nodeType
     */
    public JAXBFieldDescriptorImpl(
            final Class < ? > fieldType, final String fieldName,
            final String xmlName, final NodeType nodeType) {
        super(fieldType, fieldName, xmlName, nodeType);
    }
}
