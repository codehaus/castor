package org.springframework.xml.castor;

import java.util.Properties;

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
            String mappingLocation = this.castorProperties
                    .getProperty("mappingLocation");
            if (mappingLocation != null) {
                try {
                    Mapping mapping = new Mapping();
                    mapping.loadMapping(new InputSource(mappingLocation));

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
}