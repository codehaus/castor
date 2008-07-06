package org.castor.spring.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.util.JDOClassDescriptorResolver;
import org.exolab.castor.xml.util.JDOClassDescriptorResolverImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

public class ClassDescriptorResolverFactoryBean implements FactoryBean,
        InitializingBean /*, DisposableBean */{

    /**
     * Castor JDO Manager instance
     */
    private JDOClassDescriptorResolver classDescriptorResolver;

    /**
     * List of class names.
     */
    private List classes = new ArrayList();

    /**
     * List of packages.
     */
    private List packages = new ArrayList();

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
        return this.classDescriptorResolver;
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

        // TODO should we drop the next three lines ....
        if (this.classDescriptorResolver != null) {
            return this.classDescriptorResolver.getClass();
        }

        return JDOClassDescriptorResolver.class;
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
     * @throws ClassNotFoundException
     *                 If any of the specified classes cannot be found.
     * @throws Exception
     *                 in the event of mis-configuration (such as failure to set
     *                 an essential property) or if initialization fails.
     */
    public void afterPropertiesSet() throws IllegalArgumentException,
            MappingException, ClassNotFoundException {
        if (this.classes.isEmpty() && this.packages.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please specify at least one class name or package name.");
        }

        JDOClassDescriptorResolver classDescriptorResolver = new JDOClassDescriptorResolverImpl();

        if (!this.packages.isEmpty()) {
            for (Iterator iterator = this.packages.iterator(); iterator
                    .hasNext();) {
                String packageName = (String) iterator.next();
                classDescriptorResolver.addPackage(packageName);
            }
        }
        if (!this.classes.isEmpty()) {
            for (Iterator iterator = this.classes.iterator(); iterator
                    .hasNext();) {
                String className = (String) iterator.next();
                classDescriptorResolver.addClass(Class.forName(className));
            }
        }
        
        this.classDescriptorResolver = classDescriptorResolver;
    }

     /**
     * Invoked on destruction of a singleton.
     * @throws Exception in case of shutdown errors.
     * Exceptions will get logged but not rethrown to allow
     * other beans to release their resources too.
     */
     public void destroy() throws Exception {
         // TODO add such a method to JDOClassDescriptoResolver
//         this.classDescriptorResolver.close();
     }

    /**
     * Specifies a list of classes to be loaded from the file system.
     * 
     * @param classes
     *                List of classes to be loaded from the file system.
     */
    public void setClasses(final List classes) {
        this.classes = classes;
    }

    /**
     * Specifies a list of packages to be loaded from the file system.
     * 
     * @param packages
     *                List of packages to be loaded from the file system.
     */
    public void setPackages(final List packages) {
        this.packages = packages;
    }

}
