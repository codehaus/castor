package org.castor.spring.xml;

import org.exolab.castor.util.DefaultObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringObjectFactory 
    extends DefaultObjectFactory 
    implements ApplicationContextAware {
    private ApplicationContext context;

    public Object createInstance(Class type, Class[] argTypes, Object[] args)
            throws IllegalAccessException, InstantiationException {

        Object bean = null;

        if (this.context != null) {
            String[] beanNames = this.context.getBeanNamesForType(type);
            if (beanNames == null || beanNames.length == 0) {
                // no beans: bean is already null, fall back to super behavior,
                // below
            } else if (beanNames.length == 1) {
                // exactly one bean, use it!
                if (!this.context.isSingleton(beanNames[0])) {
                    bean = this.context.getBean(beanNames[0], type);
                }
            } else {
                // there are several beans: see if one matches
                for (int i = 0; i < beanNames.length; i++) {
                    // TODO: getSimpleName() is Java 5.0 specific
                    if (beanNames[i].equals(type.getName())
                            || beanNames[i].equals(getSimpleName(type))) {
                        // Found a matching bean, use it
                        if (!this.context.isSingleton(beanNames[i])) {
                            bean = this.context.getBean(beanNames[i], type);
                        }
                        break;
                    }
                }
            }
        }

        // bean might still be null, if context is null or no
        // matching beans are found
        if (bean == null) {
            // create the POJO the standard way
            bean = super.createInstance(type, argTypes, args);
        }

        return bean;
    }

    /**
     * Returns the 'simple' name of the given type, i.e. the fully qualified
     * type name without the package name (if existing).
     * @param type A given type
     * @return The 'simple' name of the given type.
     */
    private String getSimpleName(Class type) {
        String qualifiedName = type.getName();
        return qualifiedName.substring(qualifiedName.lastIndexOf('.'));
        // Java 5.0-specific: return type.getSimpleName();
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
    
   
}
