package org.castor.spring.orm;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.spring.orm.support.ClassDescriptorResolverProxy;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.mapping.MappingException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class LocalCastorFactoryBean implements FactoryBean, InitializingBean /*
                                                                                 * ,
                                                                                 * DisposableBean
                                                                                 */{

    private static final String RESOLVER_INTERFACE_131_AND_LATER = "org.castor.cpa.util.JDOClassDescriptorResolver";
    private static final String RESOLVER_CLASS_131_AND_LATER = "org.castor.cpa.util.JDOClassDescriptorResolverImpl";

    private static final String RESOLVER_INTERFACE_13_AND_LESS = "org.exolab.castor.xml.util.JDOClassDescriptorResolver";
    private static final String RESOLVER_CLASS_13_AND_LESS = "org.exolab.castor.xml.util.JDOClassDescriptorResolverImpl";

    /**
     * Log instance
     */
    private static final Log LOG = LogFactory
            .getLog(LocalCastorFactoryBean.class);

    /**
     * Castor JDO Manager instance
     */
    private JDOManager jdoManager;

    /**
     * Name of the database configured in the JDO configuration file.
     */
    private String databaseName;

    /**
     * Location of Castor JDO configuration file
     */
    private Resource configLocation;

    /**
     * Whether to use 'autoStore' mode for Castor JDO
     */
    private boolean autoStore;

    /**
     * {@link EntityResolver} instance used to resolve cached entities, e.g. for
     * external mapping documents.
     */
    private EntityResolver entityResolver;

    /**
     * A {@link ClassDescriptorResolverProxy} instance.
     */
    private ClassDescriptorResolverProxy classDescriptorResolverProxy;

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
     *                 in case of creation errors
     * @see FactoryBeanNotInitializedException
     */
    public Object getObject() throws Exception {
        return this.jdoManager;
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

        if (this.jdoManager != null) {
            return this.jdoManager.getClass();
        }

        return JDOManager.class;
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
        return true;
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>
     * This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an exception
     * in the event of misconfiguration.
     * 
     * @throws MappingException
     *                 If Castor cannot resolve the mapping file successfully.
     * @throws Exception
     *                 in the event of misconfiguration (such as failure to set
     *                 an essential property) or if initialization fails.
     */
    public void afterPropertiesSet() throws IllegalArgumentException,
            MappingException {
        if (this.databaseName == null || this.databaseName.length() < 1) {
            throw new IllegalArgumentException(
                    "Please specify a valid database name as defined in the JDO configuration file.");
        }
        if (this.configLocation == null) {
            throw new IllegalArgumentException(
                    "Please specify a valid JDO configuration file location.");
        }

        try {
            LOG.debug("About to load JDO configuration file from "
                    + configLocation.getURL().toExternalForm());
            this.jdoManager = createJDOManager();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Problem loading JDO configuration file from "
                            + configLocation.getDescription());
        }

    }

    /**
     * Creates a JDOManager instance for the configuration specified.
     * 
     * @param configuration
     *                JDOManager configuration file
     * @param databaseName
     *                Name of the database instance.
     * @return A fully-configured JDOManager instance
     * @throws MappingException
     *                 If the JDOManager cannot be initialized/configured.
     * @author <a href="werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
     * @throws IOException
     */
    protected JDOManager createJDOManager() throws MappingException,
            IOException {
        JDOManager jdoManagerToBeCreated = null;

        try {
            InputSource inputSource = new InputSource(this.configLocation
                    .getURL().toExternalForm());
            Object classDescriptorResolve = configureJDOClassDescriptorResolver();
            Class<?> resolved = resolveResolverInterface();
            Method loadConfigurationMethod = JDOManager.class.getMethod("loadConfiguration", 
                    InputSource.class, EntityResolver.class, ClassLoader.class, 
                    resolved);
            loadConfigurationMethod.invoke(null, inputSource, this.entityResolver,
                    getClass().getClassLoader(), classDescriptorResolve);
            jdoManagerToBeCreated = JDOManager.createInstance(this.databaseName);

            // set autostore mode
            jdoManagerToBeCreated.setAutoStore(this.autoStore);
        } catch (Exception e) {
            LOG.warn("problem", e);
            // ignore, as this cannot happen
        }
        return jdoManagerToBeCreated;
    }
    
    /**
     * Creates and configures a Castor JDO-specific {@link ClassDescriptorResolver}
     * with the values as provided by the {@link ClassDescriptorResolverProxy}
     * instance. 
     * @return A pre-configured JDO-specific {@link ClassDescriptorResolver} instance. 
     */
    private Object configureJDOClassDescriptorResolver() {
        
        Object resolver = null;

        Class<?> resolverClass = resolveResolverClass();

        try {
            resolver = resolverClass.newInstance();

            Method addPackageMethod = resolverClass.getMethod("addPackage", String.class);
            Method addClassMethod = resolverClass.getMethod("addClass", Class.class);

            for (String packageName : classDescriptorResolverProxy.getPackages()) {
                addPackageMethod.invoke(resolver, packageName);
            }

            for (String className : classDescriptorResolverProxy.getClasses()) {
                addClassMethod.invoke(resolver, Class.forName(className));
            }
        } catch (Exception e) {
            // ignore, should never happen, as either one has to be on the class path
        }
        
        return resolver;
    }

    /**
     * Resolves the {@link Class} instance for the Castor JDO {@link ClassDescriptorResolver},
     * basically implementing a fallback solution that looks for the classes in the 
     * following sequence:
     * 
     * <ol>
     *   <li>org.exolab.castor.xml.util.JDOClassDescriptorResolverImpl</li>
     *   <li>org.castor.cpa.util.JDOClassDescriptorResolverImpl</li>
     * </ol>
     * 
     * @return The actual JDP-specific {@link ClassDescriptorResolver}.
     */
    private Class<?> resolveResolverClass() {
        Class<?> resolverClass;
        try {
            resolverClass = Class.forName(RESOLVER_CLASS_131_AND_LATER);
        }
        catch (ClassNotFoundException e) {
            try {
                resolverClass = Class.forName(RESOLVER_CLASS_13_AND_LESS);
            }
            catch (ClassNotFoundException e1) {
                throw new IllegalArgumentException("Castor JDO classes not on the classpath");
            }
        }
        return resolverClass;
    }

    /**
     * Resolves the {@link Class} instance for the Castor JDO {@link ClassDescriptorResolver},
     * basically implementing a fallback solution that looks for the classes in the 
     * following sequence:
     * 
     * <ol>
     *   <li>org.exolab.castor.xml.util.JDOClassDescriptorResolverImpl</li>
     *   <li>org.castor.cpa.util.JDOClassDescriptorResolverImpl</li>
     * </ol>
     * 
     * @return The actual JDP-specific {@link ClassDescriptorResolver}.
     */
    private Class<?> resolveResolverInterface() {
        Class<?> resolverClass;
        try {
            resolverClass = Class.forName(RESOLVER_INTERFACE_131_AND_LATER);
        }
        catch (ClassNotFoundException e) {
            try {
                resolverClass = Class.forName(RESOLVER_INTERFACE_13_AND_LESS);
            }
            catch (ClassNotFoundException e1) {
                throw new IllegalArgumentException("Castor JDO classes not on the classpath");
            }
        }
        return resolverClass;
    }

    // /**
    // * Invoked on destruction of a singleton.
    // * @throws Exception in case of shutdown errors.
    // * Exceptions will get logged but not rethrown to allow
    // * other beans to release their resources too.
    // */
    // public void destroy() throws Exception {
    // // TODO [WG]: I think we should introduce a life-cycle method with
    // JDOManager
    // // this.jdoManager.close();
    // }

    /**
     * Specifies the name of the <tt>database</tt> as configured in the JDO
     * configuration file.
     * 
     * @param databasename
     *                name of the <tt>database</tt>
     */
    public void setDatabaseName(final String databasename) {
        this.databaseName = databasename;
    }

    /**
     * Specifies the location of Castor JDO configuration file
     * 
     * @param configLocation
     *                the location of Castor JDO configuration file
     */
    public void setConfigLocation(final Resource configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Specifies the {@link EntityResolver} instance used to resolve cached
     * entities, e.g. for external mapping documents.
     * 
     * @param entityResolver
     *                the {@link EntityResolver} instance used to resolve cached
     *                entities
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    /**
     * Specifies whether the Database instances as created by this
     * {@link JDOManager} instance will use autostore mode.
     * 
     * @param autoStore
     *                the {@link EntityResolver} instance used to resolve cached
     *                entities
     */
    public void setAutostore(final boolean autoStore) {
        this.autoStore = autoStore;
    }

    public void setClassDescriptorResolver(
            ClassDescriptorResolverProxy classDescriptorResolver) {
        this.classDescriptorResolverProxy = classDescriptorResolver;
    }

}
