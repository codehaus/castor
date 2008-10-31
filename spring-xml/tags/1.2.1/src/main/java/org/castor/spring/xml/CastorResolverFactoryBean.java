/*
 * Copyright 2007 Werner Guttmann
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

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
// import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.mapping.BindingType;
import org.castor.mapping.MappingUnmarshaller;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ClassDescriptorResolver;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.xml.sax.InputSource;

/**
 * Factory bean to instantiate a XMLClassDescriptorResolver and fill it with
 * the mappings found in the mapping files specified in the bean definition.
 * This implementation is deprecated since 2008-01-02 as newer releases of
 * Castor (>1.1.2.1) work internally with InternalContext to hold descriptors
 * instead of XMLClassDescriptorResolver purely.
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * 
 * @deprecated
 */
public class CastorResolverFactoryBean implements FactoryBean, InitializingBean {

    /**
     * Log instance
     */
    private static final Log LOG = LogFactory
            .getLog(CastorResolverFactoryBean.class);

    /**
     * Spring resource defining Castor properties
     */
    private Properties castorProperties;

    /**
     * Spring resource defining mapping file locations
     */
    private List mappingLocations;

    /**
     * XMlClassDescriptorResolver interface
     */
    private XMLClassDescriptorResolver resolver;

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        this.resolver = (XMLClassDescriptorResolver) ClassDescriptorResolverFactory
                .createClassDescriptorResolver(BindingType.XML);
        if (this.castorProperties != null) {

            // String mappingLocation =
            // this.castorProperties.getProperty("mappingLocation");
            //            
            // if (mappingLocation != null) {
            // try {
            // Mapping mapping = new Mapping();
            // URL mappingResource =
            // getClass().getClassLoader().getResource(mappingLocation);
            // mapping.loadMapping(new
            // InputSource(mappingResource.openStream()));
            //
            // MappingUnmarshaller mappingUnmarshaller = new
            // MappingUnmarshaller();
            // MappingLoader loader = mappingUnmarshaller
            // .getMappingLoader(mapping, BindingType.XML);
            // this.resolver.setMappingLoader(loader);
            // } catch (MappingException e) {
            // LOG.error(
            // "Problem locating/loading Castor mapping file from location "
            // + mappingLocation, e);
            // throw e;
            // }
            // }

        }
        
        String mappingLocation = null;
        if (mappingLocations != null && mappingLocations.size() > 0) {
            Iterator iter = mappingLocations.iterator();
            try {
                Mapping mapping = new Mapping();
                while (iter.hasNext()) {
                    mappingLocation = (String) iter.next();
                    URL mappingResource = getClass().getClassLoader()
                            .getResource(mappingLocation);
                    mapping.loadMapping(new InputSource(mappingResource
                            .openStream()));
                }

                MappingUnmarshaller mappingUnmarshaller = new MappingUnmarshaller();
                MappingLoader loader = mappingUnmarshaller
                        .getMappingLoader(mapping, BindingType.XML);
                this.resolver.setMappingLoader(loader);
            } catch (MappingException e) {
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
        return this.resolver;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        if (this.resolver == null) {
            return ClassDescriptorResolver.class;
        }

        return this.resolver.getClass();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    public void setCastorProperties(Properties castorProperties) {
        this.castorProperties = castorProperties;
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