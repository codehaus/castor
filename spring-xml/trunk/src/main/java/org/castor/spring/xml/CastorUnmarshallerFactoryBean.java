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
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.util.ObjectFactory;
import org.exolab.castor.xml.UnmarshalListener;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * Spring FactoryBean for the Unmarshaller component of Castor XML.
 * 
 * @since 0.9 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 */
public class CastorUnmarshallerFactoryBean extends AbstractCastorPrototypingXMLFactoryBean {

    /**
     * Log instance
     */
    private static final Log LOG = LogFactory.getLog(CastorUnmarshallerFactoryBean.class);
    
    /**
     * Indicates whether extra attributes should be ignored; default is 'true'.
     */
    private boolean ignoreExtraAttributes = true;

    /**
     * Indicates whether extra elements should be ignored
     */
    private boolean ignoreExtraElements = false;

    /**
     * Indicates that objects contained within the object model should be 
     * re-used where appropriate. This is only valid when unmarshalling
     * to an existing object.
     */
    private boolean reuseObjects = false;

    /**
     * Indicates whether or not validation should be performed during 
     * umarshalling; default is 'true' 
     */
    private boolean validation = true;

    /**
    /**
     * Sets the top-level whitespace mode (&lt;xml:space&gt;) to either
     * preserving or non preserving. Within the XML document, it is possible 
     * to override this value using &lt;xml:space&gt; on specific
     * elements. 
     */
    private boolean preserveWhitespace = false;
    
    /**
     * Sets the {@link Class} instance of the Unmarshaller instance.
     */
    private Class rootClass;

    /**
     * An (optional) {@link org.exolab.castor.xml.UnmarshalListener} to receive pre and
     * post unmarshal notification for each Object in the tree. An UnmarshalListener is 
     * often used to allow objects to appropriately initialize themselves by taking 
     * appliction specific behavior as they are unloaded.
     */
    private UnmarshalListener unmarshalListener;
    
    /**
     * An (optional) {@link import org.exolab.castor.util.ObjectFactory} to use the Spring
     * ApplicationContext to load beans. 
     */
    private ObjectFactory factory;
    
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
        
        Unmarshaller unmarshaller;
        if (getXmlContext() != null) {
            unmarshaller = getXmlContext().createUnmarshaller();
        } else {
            unmarshaller = new Unmarshaller();
            
            if (getSpringXMLContext() != null) {
                getSpringXMLContext().setContext(unmarshaller);
            } else {
                if (this.getResolver() != null) {
                    unmarshaller.setResolver(this.getResolver());
                }
            }
        }
        
        unmarshaller.setIgnoreExtraAttributes(this.ignoreExtraAttributes);
        unmarshaller.setIgnoreExtraElements(this.ignoreExtraElements);
        unmarshaller.setReuseObjects(this.reuseObjects);
        unmarshaller.setValidation(this.validation);
        unmarshaller.setWhitespacePreserve(this.preserveWhitespace);
        
        if (this.unmarshalListener != null) {
            unmarshaller.setUnmarshalListener(this.unmarshalListener);
        }
        
        if (this.factory != null) {
            unmarshaller.setObjectFactory(this.factory);
        }
        
        return unmarshaller;
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
        return Unmarshaller.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.castor.spring.xml.AbstractCastorPrototypingXMLFactoryBean#getLog()
     */
    protected Log getLog() {
        return CastorUnmarshallerFactoryBean.LOG;
    }
    
    /**
     * Sets whether to ignore extra attributes
     * @param ignoreExtraAttributes True if extra attributes should be ignored.
     */
    public void setIgnoreExtraAttributes(boolean ignoreExtraAttributes) {
        this.ignoreExtraAttributes = ignoreExtraAttributes;
    }

    /**
     * Sets whether to ignore extra attributes
     * @param ignoreExtraAttributes True if extra attributes should be ignored.
     */
    public void setIgnoreExtraElements(boolean ignoreExtraElements) {
        this.ignoreExtraElements= ignoreExtraElements;
    }

    /**
     * Indicates that objects contained within the object model should be 
     * re-used where appropriate. This is only valid when unmarshalling
     * to an existing object.
     *
     * @param reuse indicates whether or not to re-use existing objects in the object model.
    **/
    public void setReuseObjects(boolean reuseObjects) {
        this.reuseObjects = reuseObjects;
    }

    /**
     * Indicates whether or not validation should be performed during umarshalling 
     * @param validate True if validation should be performed during umarshalling; 
     * default is 'true'.
    **/
    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    /**
     * Sets the top-level whitespace mode (&lt;xml:space&gt;) to either
     * preserving or non preserving. Within the XML document, it is possible 
     * to override this value using &lt;xml:space&gt; on specific
     * elements. This sets the "default" behavior when xml:space="default".
     *
     * @param preserve True to enable whitespace preserving by default. 
     */
    public void setPreserveWhitespace(boolean preserveWhitespace) {
        this.preserveWhitespace = preserveWhitespace;
    }

    /**
     * Sets an optional {@link org.exolab.castor.xml.UnmarshalListener} to receive pre and
     * post unmarshal notification for each Object in the tree.
     * An UnmarshalListener is often used to allow objects to
     * appropriately initialize themselves by taking appliction
     * specific behavior as they are unloaded.
     * Current only one listener is allowed. If you need register multiple 
     * listeners, you will have to create your own master listener that will
     * forward the event notifications and manage the multiple listeners.
     *
     * @param listener The {@link org.exolab.castor.xml.UnmarshalListener} to set.
    **/
    public void setUnmarshalListener(UnmarshalListener listener) {
        this.unmarshalListener = listener;
    }
    
    /**
     * Set the object factory for this Unmarshaller.
     * @param factory
     */
    public void setObjectFactory(ObjectFactory factory) {
        this.factory = factory;
    }

}
