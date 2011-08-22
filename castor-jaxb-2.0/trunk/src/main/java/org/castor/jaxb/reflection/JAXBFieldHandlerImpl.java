/*
 * Copyright 2008 Joachim Grueneis
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
package org.castor.jaxb.reflection;

import org.castor.jaxb.exceptions.AdapterException;
import org.castor.jaxb.resolver.JAXBAdapterRegistry;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.ValidityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This JAXB specific field handler is instantiated for every class that is mapped.
 *
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class JAXBFieldHandlerImpl implements FieldHandler {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /** The type of the field. */
    private Class < ? > _type;
    /** Factory to create instances of the Field. */
    private Class < ? > _typeFactoryClass;
    /** Factory method name to create instances of the Field. */
    private String _typeFactoryMethod;
    /** The Field to set or get directly - no access methods given! */
    private Field _field;
    /** Get Method to access the property. */
    private Method _getMethod;
    /** Set Method to access the property. */
    private Method _setMethod;

    /**
     * The class that extends the {@link XmlAdapter} used for handling marshalling and unmarshalling of this
     * concrete field.
     */
    private Class<? extends XmlAdapter> adapterClass;

    /**
     * The instance of {@link JAXBAdapterRegistry} used for retrieving the adapters.
     */
    private JAXBAdapterRegistry jaxbAdapterRegistry;

    /**
     * Empty default constructor.
     */
    public JAXBFieldHandlerImpl() {
        super();
    }

    /**
     * No validity checking in JAXB!!
     * {@inheritDoc}
     * @see org.exolab.castor.mapping.FieldHandler#checkValidity(java.lang.Object)
     */
    public void checkValidity(final Object object)
    throws ValidityException, IllegalStateException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Currently validation is not supported!");
        }
        // nothing to do!
    }

    /**
     * Either the value of object can be extracted or a JAXB specific adapter
     * will be used.
     *
     * {@inheritDoc}
     * @see org.exolab.castor.mapping.FieldHandler#getValue(java.lang.Object)
     */
    public Object getValue(final Object parentOfField)
    throws IllegalStateException {
        Object value;
        // first step is to read the value from the parent
        if (_getMethod != null) {
            try {
                if (!_getMethod.isAccessible()) {
                    _getMethod.setAccessible(true);
                }
                value = _getMethod.invoke(parentOfField);
            } catch (IllegalArgumentException e) {
                AdapterException ex = new AdapterException(
                        "Call to Method.invoke failed", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (IllegalAccessException e) {
                AdapterException ex = new AdapterException(
                        "Call to Method.invoke failed", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (InvocationTargetException e) {
                AdapterException ex = new AdapterException(
                        "Call to Method.invoke failed", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else if (_field != null) {
            try {
                if (!_field.isAccessible()) {
                    _field.setAccessible(true);
                }
                value = _field.get(parentOfField);
            } catch (IllegalArgumentException e) {
                AdapterException ex = new AdapterException(
                        "Call to Field.get failed", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (IllegalAccessException e) {
                AdapterException ex = new AdapterException(
                        "Call to Field.get failed", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else {
            AdapterException ex = new AdapterException(
                    "No XmlAdapter, no Method, no Field - getValue is not possible");
            LOG.warn(ex.toString());
            throw ex;
        }
        // then use the XmlAdapter if one is set
        if (adapterClass != null) {
            try {
                return getXmlAdapter().unmarshal(value);
            } catch (Exception e) {
                AdapterException ex = new AdapterException(
                        "Call to XmlAdapter.unmarshal failed", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        }
        return value;
    }

    /**
     * Either an empty constructor is available or factory class and method
     * are known.
     * {@inheritDoc}
     * @see org.exolab.castor.mapping.FieldHandler#newInstance(java.lang.Object)
     */
    public Object newInstance(final Object parent)
    throws IllegalStateException {
        if (_typeFactoryClass != null && _typeFactoryMethod != null) {
            try {
                Object typeFactory = _typeFactoryClass.newInstance();
                Method factoryMethod =
                    _typeFactoryClass.getMethod(_typeFactoryMethod, new Class[] {});
                return factoryMethod.invoke(typeFactory, new Object[]{});
            } catch (InstantiationException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using type factory", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (IllegalAccessException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using type factory", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (SecurityException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using type factory", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (NoSuchMethodException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using type factory", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (IllegalArgumentException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using type factory", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (InvocationTargetException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using type factory", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else if (_type != null) {
            try {
                return (Object) _type.newInstance();
            } catch (InstantiationException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using Class.newInstance()", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (IllegalAccessException e) {
                AdapterException ex = new AdapterException(
                        "Failed to create new instance using Class.newInstance()", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("newInstance not possible - returning null");
            }
            return null;
        }
    }

    /**
     * Not supported - throws exception!
     * {@inheritDoc}
     * @see org.exolab.castor.mapping.FieldHandler#resetValue(java.lang.Object)
     */
    public void resetValue(final Object object)
    throws IllegalStateException, IllegalArgumentException {
    }

    /**
     * Sets the value into the already instantiated object.
     * {@inheritDoc}
     * @see org.exolab.castor.mapping.FieldHandler#setValue(java.lang.Object, java.lang.Object)
     */
    public void setValue(final Object object, final Object value)
    throws IllegalStateException, IllegalArgumentException {
        // an XMLAdapter is specified, so it is the first step... transforming
        // from value type to bound type...
        Object marshalAbleValue;
        if (adapterClass != null) {
            try {
                marshalAbleValue = getXmlAdapter().marshal(value);
            } catch (Exception e) {
                AdapterException ex = new AdapterException(
                        "Call to XmlAdapter.marshal failed", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else {
            marshalAbleValue = value;
        }
        if (_setMethod != null) {
            try {
                if (!_setMethod.isAccessible()) {
                    _setMethod.setAccessible(true);
                }
                _setMethod.invoke(object, marshalAbleValue);
            } catch (IllegalAccessException e) {
                AdapterException ex = new AdapterException(
                        "Call to Method.invoke failed", e);
                LOG.warn(ex.toString());
                throw ex;
            } catch (InvocationTargetException e) {
                AdapterException ex = new AdapterException(
                        "Call to Method.invoke failed", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else if (_field != null) {
            try {
                if (!_field.isAccessible()) {
                    _field.setAccessible(true);
                }
                _field.set(object, marshalAbleValue);
            } catch (IllegalAccessException e) {
                AdapterException ex = new AdapterException(
                        "Call to Field.set failed", e);
                LOG.warn(ex.toString());
                throw ex;
            }
        } else {
            AdapterException ex = new AdapterException(
                    "No XmlAdapter, no Method, no Field - setValue is not possible");
            LOG.warn(ex.toString());
            throw ex;
        }
    }
    /**
     * If object factory class and method have been specified in XMLType, set it
     * in this place.
     * @param typeFactoryClass the class to use for type creation
     * @param typeFactoryMethod the method to call for type creation
     */
    public void setTypeFactory(final Class < ? > typeFactoryClass, final String typeFactoryMethod) {
        _typeFactoryClass = typeFactoryClass;
        _typeFactoryMethod = typeFactoryMethod;
    }
    /**
     * To set the Class of the described field! Required if no type factory
     * is set but newInstance should create the instance of the type.
     * @param type the type to instantiate in newInstance()
     */
    public void setType(final Class < ? > type) {
        _type = type;
    }
    /**
     * To set the Class.Field to work with.
     *
     * @param field the Class.Field to fill
     */
    public void setField(final Field field) {
        _field = field;
    }

    /**
     * The setter and getter of the property.
     *
     * @param getMethod getter of the property
     * @param setMethod setter of the property
     */
    public void setMethods(final Method getMethod, final Method setMethod) {
        _getMethod = getMethod;
        _setMethod = setMethod;
    }

    /**
     * Sets the adapter class for this hanlder, that should be used for marshalling and unmarshalling.
     *
     * @param adapterClass the {@link XmlAdapter} class
     */
    public void setXmlAdapterClass(Class<? extends XmlAdapter> adapterClass) {
        this.adapterClass = adapterClass;
    }

    /**
     * Sets the instance of {@link JAXBAdapterRegistry} used to retrieve the adapter instance.
     * @param jaxbAdapterRegistry the {@link JAXBAdapterRegistry} used for retrieving the adapter instances.
     */
    public void setJaxbAdapterRegistry(JAXBAdapterRegistry jaxbAdapterRegistry) {
        this.jaxbAdapterRegistry = jaxbAdapterRegistry;
    }

    /**
     * Retrieves the xml adapter if it was specified in {@link org.castor.jaxb.reflection.info.JaxbFieldNature}.
     *
     * @return the {@link XmlAdapter} instance or null
     */
    private XmlAdapter getXmlAdapter() {

        XmlAdapter xmlAdapter;

        try {
            xmlAdapter = jaxbAdapterRegistry.getAdapter(adapterClass);

            // if there were no adapter then tries to create new instance of the adapter by
            // instantiating the class using the default constructor
            if (xmlAdapter == null) {

                xmlAdapter = adapterClass.newInstance();
            }

            return xmlAdapter;
        } catch (InstantiationException e) {
            // ignores exception
        } catch (IllegalAccessException e) {
            // ignores exception
        }

        return null;
    }
}
