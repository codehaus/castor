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
package org.castor.jaxb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.castor.jaxb.adapters.UnmarshalListenerAdapter;
import org.castor.xml.InternalContext;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Wraps the Castor unmarshaller with a JAXB compliant API.
 * @see javax.xml.bind.Unmarshaller
 * @see org.exolab.castor.xml.Unmarshaller
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class Unmarshaller implements javax.xml.bind.Unmarshaller {

    /**
     * Logger to use.
     */
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * The Castor unmarshaller doing the work.
     */
    private org.exolab.castor.xml.Unmarshaller _castorUnmarshaller;

    /**
     * UnmarshalListener to use.
     */
    private UnmarshalListenerAdapter _unmarshalListener = null;

    /**
     * The Castor XML context to use.
     */
    private InternalContext _internalContext;
    
    /**
     * Creates new instance of {@link Unmarshaller}.
     * </p>
     * Constructor with package scope prevents from instantiation outside this component.
     *
     * @param castorUnmarshaller the Castor Unmarshaller to use
     *
     * @see JAXBContext#createUnmarshaller()
     */
    Unmarshaller(final org.exolab.castor.xml.Unmarshaller castorUnmarshaller) {
        _castorUnmarshaller = castorUnmarshaller;
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getAdapter(java.lang.Class)
     */
    public < A extends XmlAdapter > A getAdapter(final Class < A > arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getAttachmentUnmarshaller()
     */
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getEventHandler()
     */
    public ValidationEventHandler getEventHandler() throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getListener()
     */
    public Listener getListener() {
        return (_unmarshalListener == null) ? null : _unmarshalListener.getJAXBListener();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getProperty(java.lang.String)
     */
    public Object getProperty(final String property) throws PropertyException {
        return _internalContext.getProperty(property);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getSchema()
     */
    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#getUnmarshallerHandler()
     */
    public UnmarshallerHandler getUnmarshallerHandler() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#isValidating()
     */
    public boolean isValidating() throws JAXBException {
        return _castorUnmarshaller.isValidating();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setAdapter(javax.xml.bind.annotation.adapters.XmlAdapter)
     */
    public void setAdapter(final XmlAdapter xmlAdapter) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setAdapter(java.lang.Class, javax.xml.bind.annotation.adapters.XmlAdapter)
     */
    public < A extends XmlAdapter > void setAdapter(final Class < A > type, final A xmlAdapter) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setAttachmentUnmarshaller(javax.xml.bind.attachment.AttachmentUnmarshaller)
     */
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller attachmentUnmarshaller) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setEventHandler(javax.xml.bind.ValidationEventHandler)
     */
    public void setEventHandler(final ValidationEventHandler validationEventHandler) 
    throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setListener(javax.xml.bind.Unmarshaller.Listener)
     */
    public void setListener(final Listener listener) {
        if (listener != null) {
            _unmarshalListener = new UnmarshalListenerAdapter();
            _unmarshalListener.setJAXBListener(listener);
            _castorUnmarshaller.setUnmarshalListener(_unmarshalListener);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(final String property, final Object value) throws PropertyException {
        _internalContext.setProperty(property, value);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setSchema(javax.xml.validation.Schema)
     */
    public void setSchema(final Schema schema) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#setValidating(boolean)
     */
    public void setValidating(final boolean validate) throws JAXBException {
        _castorUnmarshaller.setValidation(validate);
    }

    //-------------------------------------
    // The Object unmarshal(xy) methods...
    //-------------------------------------

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.File)
     */
    public Object unmarshal(final File file) throws JAXBException {
        try {
            return unmarshal(new InputSource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw Utils.convertToJAXBException(LOG, "Problem unmarshalling from file " + file.getAbsolutePath(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.InputStream)
     */
    public Object unmarshal(final InputStream inputStream) throws JAXBException {
        return unmarshal(new InputSource(inputStream));
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.Reader)
     */
    public Object unmarshal(final Reader reader) throws JAXBException {
        return unmarshal(new InputSource(reader));
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.net.URL)
     */
    public Object unmarshal(final URL url) throws JAXBException {
        try {
            return unmarshal(new InputSource(url.openStream()));
        } catch (IOException e) {
            throw Utils.convertToJAXBException(LOG, "Problem unmarshalling from URL " + url.toExternalForm(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.xml.sax.InputSource)
     */
    public Object unmarshal(final InputSource inputSource) throws JAXBException {
        try {
            return _castorUnmarshaller.unmarshal(inputSource);
        } catch (MarshalException e) {
            throw Utils.convertToJAXBException(LOG, "Problem occurred when unmarshalling from InputSource.", e);
        } catch (ValidationException e) {
            throw Utils.convertToJAXBException(LOG, "Problem occurred when unmarshalling from InputSource.", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.w3c.dom.Node)
     */
    public Object unmarshal(final Node node) throws JAXBException {
        try {
            return _castorUnmarshaller.unmarshal(node);
        } catch (MarshalException e) {
            throw Utils.convertToJAXBException(LOG, "Problem occurred when unmarshalling from Node.", e);
        } catch (ValidationException e) {
            throw Utils.convertToJAXBException(LOG, "Problem occurred when unmarshalling from Node.", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.transform.Source)
     */
    public Object unmarshal(final Source source) throws JAXBException {
        // TODO starting from the Castor 1.3.3 this can be replaced
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            if(streamSource.getReader() != null) {
                // delegates to unmarshal method
                return unmarshal(streamSource.getReader());
            } else if(streamSource.getInputStream() != null) {
                // delegates to unmarshal method
                return unmarshal(streamSource.getInputStream());
            }
        } else if (source instanceof DOMSource) {
            DOMSource domSource = (DOMSource) source;
            if(domSource.getNode() != null) {
                // delegates to unmarshal method
                return unmarshal(domSource.getNode());
            }
        } else if (source instanceof SAXSource) {
            SAXSource saxSource = (SAXSource) source;
            if(saxSource.getInputSource() != null) {
                // delegates to unmarshal method
                return unmarshal(saxSource.getInputSource());
            }
        }

        throw new JAXBException("Could not unmarshall the passed Source object.");
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLStreamReader)
     */
    public Object unmarshal(final XMLStreamReader xmlStreamReader) throws JAXBException {
        try {
            return _castorUnmarshaller.unmarshal(xmlStreamReader);
        } catch (MarshalException e) {
            throw Utils.convertToJAXBException(LOG, "Error occurred when unmarshalling from XMLStreamReader.", e);
        } catch (ValidationException e) {
            throw Utils.convertToJAXBException(LOG, "Error occurred when unmarshalling from XMLStreamReader.", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLEventReader)
     */
    public Object unmarshal(final XMLEventReader xmlEventReader) throws JAXBException {
        try {
            return _castorUnmarshaller.unmarshal(xmlEventReader);
        } catch (MarshalException e) {
            throw Utils.convertToJAXBException(LOG, "Error occurred when unmarshalling from XMLEventReader.", e);
        } catch (ValidationException e) {
            throw Utils.convertToJAXBException(LOG, "Error occurred when unmarshalling from XMLEventReader.", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.w3c.dom.Node, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> JAXBElement<T> unmarshal(
            final Node node, final Class<T> type) throws JAXBException {

        return createJAXBElement(type, (T) unmarshal(node));
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.transform.Source, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public < T > JAXBElement < T > unmarshal(
            final Source source, final Class < T > type) throws JAXBException {

        return createJAXBElement(type, (T) unmarshal(source));
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLStreamReader, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public < T > JAXBElement < T > unmarshal(
            final XMLStreamReader xmlStreamReader, final Class < T > type) throws JAXBException {

        return createJAXBElement(type, (T) unmarshal(xmlStreamReader));
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLEventReader, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public < T > JAXBElement < T > unmarshal(
            final XMLEventReader xmlEventReader, final Class < T > type) throws JAXBException {

        return createJAXBElement(type, (T) unmarshal(xmlEventReader));
    }

    /**
     * Sets the Castor XML context to use.
     * @param internalContext the Castor XML Context to use
     */
    protected void setInternalContext(final InternalContext internalContext) {
        _internalContext = internalContext;
        _castorUnmarshaller.setInternalContext(_internalContext);
    }

    /**
     * Creates a instance of {@link JAXBElement} that wraps the passed object.</p>
     *
     * @param clazz the class of the wrapped object
     * @param obj the object to wrap
     * @param <T> the type of the wrapped object
     * @return the newly created instance of {@link JAXBElement} that wrapps the passed object
     */
    private <T> JAXBElement<T> createJAXBElement(Class<T> clazz, T obj) {
        // TODO currently it is impossible to enquire the name of the unmarshalled object
        // this will be probably possible with help of JAXBIntrospector
        return new JAXBElement<T>(new QName(""), clazz , obj);
    }
}
