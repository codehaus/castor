package org.springframework.xml.castor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Marshaller;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;

public class CastorMarshallerFactoryBean extends AbstractCastorPrototypingXMLFactoryBean {

    /**
     * Log instance
     */
    private static final Log LOG = LogFactory.getLog(CastorMarshallerFactoryBean.class);

    /**
     * Return an instance (possibly shared or independent) of the object managed
     * by this factory. As with a BeanFactory, this allows support for both the
     * Singleton and Prototype design pattern.
     * <p>
     * If this method returns null, the factory will consider the FactoryBean as
     * not fully initialized and throw a corresponding
     * FactoryBeanNotInitializedException.
     * 
     * @return an instance of the bean (should not be null; a null value will be
     *         considered as an indication of incomplete initialization)
     * @throws Exception
     *             in case of creation errors
     * @see FactoryBeanNotInitializedException
     */
    public Object getObject() throws Exception {
        Marshaller marshaller = new Marshaller();
        marshaller.setResolver(this.getResolver());
        return marshaller;
    }

    /**
     * Return the type of object that this FactoryBean creates, or null if not
     * known in advance. This allows to check for specific types of beans
     * without instantiating objects, for example on autowiring.
     * <p>
     * For a singleton, this should try to avoid singleton creation as far as
     * possible; it should rather estimate the type in advance. For prototypes,
     * returning a meaningful type here is advisable too.
     * <p>
     * This method can be called <i>before</i> this FactoryBean has been fully
     * initialized. It must not rely on state created during initialization; of
     * course, it can still use such state if available.
     * <p>
     * <b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return null
     * here. Therefore it is highly recommended to implement this method
     * properly, using the current state of the FactoryBean.
     * 
     * @return the type of object that this FactoryBean creates, or null if not
     *         known at the time of the call
     * @see ListableBeanFactory#getBeansOfType
     */
    public Class getObjectType() {
        return Marshaller.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.xml.castor.AbstractCastorPrototypingXMLFactoryBean#getLog()
     */
    protected Log getLog() {
        return CastorMarshallerFactoryBean.LOG;
    }
}