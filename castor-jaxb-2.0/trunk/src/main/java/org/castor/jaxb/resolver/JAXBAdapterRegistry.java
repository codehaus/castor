/*
 * Copyright 2011 Jakub Narloch
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

package org.castor.jaxb.resolver;

import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores all the registered adapters for the given marshaller.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
@Component("jaxbAdapterRegistry")
public class JAXBAdapterRegistry {

    /**
     * Represents a map of adapter, where in entry is stored the adapter class and the registered instance.
     */
    private final Map<Class, XmlAdapter> adapters = new HashMap<Class, XmlAdapter>();

    /**
     * Adds the {@link XmlAdapter} instance to the registry.
     *
     * @param adapterClass the class of the adapter
     * @param xmlAdapter the {@link XmlAdapter} instance
     */
    public void setAdapter(Class adapterClass, XmlAdapter xmlAdapter) {

        adapters.put(adapterClass, xmlAdapter);
    }

    /**
     * Retrieves the {@link XmlAdapter} instance of the given class or null if no adapter has been provided.
     *
     * @param clazz the class of the adapter to retrieve
     *
     * @return the instance of {@link XmlAdapter} of the given class or null
     */
    public XmlAdapter getAdapter(Class clazz) {

        return adapters.get(clazz);
    }
}
