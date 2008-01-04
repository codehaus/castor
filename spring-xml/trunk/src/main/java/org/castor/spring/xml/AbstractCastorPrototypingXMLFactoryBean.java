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

import org.apache.commons.logging.Log;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Abstract implementation to share the snippet of setting the SpringXMLContext into
 * Marshaller or Unmarshaller instances.
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 */
public abstract class AbstractCastorPrototypingXMLFactoryBean implements FactoryBean, InitializingBean {

    /**
     * XMLClassDescriptorResolver set from Spring.
     * Is deprecated since (2008-01-02) because newer releases
     * of Castor will work with an InternalContext instead of
     * holding the resolver itself.
     * @deprecated
     */
    private XMLClassDescriptorResolver resolver;
    
    /**
     * The SpringXMLContext mimics the InternalContext of newer
     * Castor implementations (>1.1.2.1).
     * It is meant to be set by Spring and hold the source of
     * descriptors for Marshaller and Unmarshaller instances.
     */
    private SpringXMLContext springXMLContext;

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>
     * This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an exception
     * in the event of misconfiguration.
     * 
     * @throws IllegalArgumentException
     *             If the properties are not properly set.
     */
    public void afterPropertiesSet() throws IllegalArgumentException {
        if (this.resolver == null && this.springXMLContext == null) {
            this.getLog().error("Neither 'resolver' nor 'springXmlContext' is set.");
            throw new IllegalArgumentException("Please specify either Resolver ('receiver') or SpringXmlContext ('springXmlContext') as property.");
        }
    }

    /**
     * Is the bean managed by this factory a singleton or a prototype? That is,
     * will getObject() always return the same object?
     * <p>
     * The singleton status of the FactoryBean itself will generally be provided
     * by the owning BeanFactory; usually, it has to be defined as singleton
     * there.
     * 
     * @return true if this bean is a singleton
     */
    public boolean isSingleton() {
        return false;
    }

    /**
     * Sets the ClassDescriptorResolver instance to use for descriptor caching.
     * @param resolver ClassDescriptorResolver instance to use for descriptor caching.
     */
    public void setResolver(final XMLClassDescriptorResolver resolver) {
        this.resolver = resolver;
    }
    
    /**
     * Sets the SpringXMLContext instance to use for descriptor caching.
     * @param springXMLContext SpringXMLContext instance to use for descriptor caching
     */
    public void setSpringXmlContext(final SpringXMLContext springXMLContext) {
        this.springXMLContext = springXMLContext;
    }

    protected abstract Log getLog();

    /**
     * Returns the ClassDescriptorResolver instance currently in use; null if none.
     * @return the ClassDescriptorResolver instance currently in use
     */
    protected XMLClassDescriptorResolver getResolver() {
        return this.resolver;
    }
    
    /**
     * Returns the SpringXMLContext instance currently in use; null if none.
     * @return the SpringXMLContext instance currently in use
     */
    protected SpringXMLContext getSpringXMLContext() {
        return springXMLContext;
    }
}