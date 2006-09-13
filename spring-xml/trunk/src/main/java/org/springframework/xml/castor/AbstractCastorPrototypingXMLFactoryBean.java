package org.springframework.xml.castor;

import org.apache.commons.logging.Log;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractCastorPrototypingXMLFactoryBean implements FactoryBean, InitializingBean {

    /**
     * XMLClassDescriptorResolver set from Spring.
     */
    private XMLClassDescriptorResolver resolver;

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
        if (this.resolver == null) {
            this.getLog().error("Property 'resolver' is not set.");
            throw new IllegalArgumentException("Please specify a XMLClassDescriptorResolver as property 'resolver'.");
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

    protected abstract Log getLog();

    /**
     * Returns the ClassDescriptorResolver instance currently in use; null if none.
     * @return the ClassDescriptorResolver instance currently in use
     */
    protected XMLClassDescriptorResolver getResolver() {
        return this.resolver;
    }
}