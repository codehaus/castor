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
package org.castor.spring.xml;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.mapping.BindingType;
import org.castor.mapping.MappingUnmarshaller;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingLoader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.xml.sax.InputSource;

/**
 * Responsible to return the SpringXMLContext implementation matching the Castor
 * version used. This can either be an InternalContext adapter or something to
 * be used if no InternalContext exists.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class CastorXMLContextFactoryBean implements FactoryBean, InitializingBean {

    /**
     * Log instance
     */
    private static final Log LOG = LogFactory.getLog(CastorXMLContextFactoryBean.class);

    /**
     * XMLContext interface
     */
    private SpringXMLContext springXmlContext;

    /**
     * Spring resource defining mapping file locations
     */
    private List mappingLocations;

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if (internalContextExists()) {
            springXmlContext = new InternalContextAdapter();
        } else {
            springXmlContext = new NoInternalContextAdapter();
        }
        readMappings();
    }

    /**
     * To detect if the interface InternalContext exists in Castor and can be used...
     * @return true if InternalContext exists
     */
    private boolean internalContextExists() {
        try {
            Class.forName("org.castor.xml.InternalContext");
            return true;
        } catch (ClassNotFoundException e) {
            LOG.debug("failed to load InternalContext class - working against old Castor release... - exception was: " + e);
        }
        return false;
    }

    /**
     * Read all mappings that have been provided via configuration.
     * @throws MappingException if the mapping file could not be interpreted
     * @throws IOException if the mapping file could not be opened for reading
     */
    private void readMappings() throws MappingException, IOException {
        String mappingLocation = null;
        if (mappingLocations != null && mappingLocations.size() > 0) {
            Iterator iter = mappingLocations.iterator();
            try {
                Mapping mapping = new Mapping();
                while (iter.hasNext()) {
                    mappingLocation = (String) iter.next();
                    URL mappingResource = getClass().getClassLoader()
                            .getResource(mappingLocation);
                    mapping.loadMapping(new InputSource(mappingResource.openStream()));
                }

                MappingUnmarshaller mappingUnmarshaller = new MappingUnmarshaller();
                MappingLoader loader = mappingUnmarshaller
                        .getMappingLoader(mapping, BindingType.XML);
                springXmlContext.setMappingLoader(loader);
            } catch (MappingException e) {
                LOG.error(
                        "Problem locating/loading Castor mapping file from location "
                                + mappingLocation, e);
                throw e;
            } catch (IOException e) {
                LOG.error(
                        "Problem locating/loading Castor mapping file from location "
                                + mappingLocation, e);
                throw e;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {
        return springXmlContext;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        if (this.springXmlContext == null) {
            return SpringXMLContext.class;
        }

        return this.springXmlContext.getClass();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Sets a collection of mapping (file) locations.
     * 
     * @param mappingLocations
     *            A collection of mapping (file) locations.
     */
    public void setMappingLocations(List mappingLocations) {
        this.mappingLocations = mappingLocations;
    }
}