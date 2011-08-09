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

import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.util.XMLClassDescriptorImpl;

/**
 * An implementation of XMLClassDescriptor specific for the JAXB implementation
 * of Castor. Currently it simply extends XMLClassdescriptorImpl...
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class JAXBClassDescriptorImpl extends XMLClassDescriptorImpl implements XMLClassDescriptor {
    
    /**
     * Creates an IntrospectedXMLClassDescriptor.
     */
    JAXBClassDescriptorImpl() {
        super();
    }
    
    private boolean transientClass;

    public boolean isTransientClass() {
        return transientClass;
    }

    public void setTransientClass(boolean transientClass) {
        this.transientClass = transientClass;
    }
    
}
