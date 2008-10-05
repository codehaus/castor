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
package org.castor.jaxb.naming;

import java.beans.Introspector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.xml.XMLNaming;
import org.exolab.castor.xml.util.DefaultNaming;

/**
 * JAXB specific implementation of XMLNaming to create names that conform
 * to XML naming rules. Currently the code was copied from Castor core...
 * as that implementation is final and I'm sure a special implementation
 * is required for JAXB.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class JAXBXmlNaming implements XMLNaming {
    /** 
     * Logger to be used.
     */
    private static final Log LOG = LogFactory.getLog(JAXBXmlNaming.class);
    
    /**
     * The Castor implementation of XMLNaming.
     */
    private XMLNaming _castorXmlNaming = new DefaultNaming();
    
    /**
     * {@inheritDoc}
     * @see org.exolab.castor.xml.AbstractXMLNaming#createXMLName(java.lang.Class)
     */
    public String createXMLName(final Class c) {
//        String name = c.getName();
//        int idx = name.lastIndexOf('.');
//        if (idx >= 0) {
//            name = name.substring(idx+1);
//        }
//        return toXMLName(name);
        UnsupportedOperationException e =
            new UnsupportedOperationException("createXMLName(Class) shouldn't be used!");
        LOG.warn(e);
        throw e;
    }

    /**
     * {@inheritDoc}
     * @see org.exolab.castor.xml.AbstractXMLNaming#toXMLName(java.lang.String)
     */
    public String toXMLName(final String name) {
        return Introspector.decapitalize(name);
//        return _castorImplementation.toXMLName(name);
//        if (name == null) {
//            return null;
//        }
//        if (name.length() == 0) {
//            return name;
//        }
//        if (name.length() == 1) {
//            return name.toLowerCase();
//        }
//
//        //-- Follow the Java beans Introspector::decapitalize
//        //-- convention by leaving alone String that start with
//        //-- 2 uppercase characters.
//        if (Character.isUpperCase(name.charAt(0)) && Character.isUpperCase(name.charAt(1))) {
//            return name;
//        }
//
//        //-- process each character
//        StringBuffer cbuff = new StringBuffer(name);
//        cbuff.setCharAt(0, Character.toLowerCase(cbuff.charAt(0)));
//
//        boolean ucPrev = false;
//        for (int i = 1; i < cbuff.length(); i++) {
//            char ch = cbuff.charAt(i);
//            if (Character.isUpperCase(ch)) {
//                if (ucPrev) {
//                    continue;
//                }
//                ucPrev = true;
//                cbuff.insert(i, '-');
//                ++i;
//                cbuff.setCharAt(i, Character.toLowerCase(ch));
//            } else if (ch == '.') {
//                //-- do not add '-' if preceeded by '.'
//                ucPrev = true;
//            } else {
//                ucPrev = false;
//            }
//        }
//        return cbuff.toString();
    }

}
