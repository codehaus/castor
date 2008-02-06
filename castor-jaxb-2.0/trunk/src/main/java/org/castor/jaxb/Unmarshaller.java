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
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jaxb.adapters.UnmarshalListenerAdapter;
import org.castor.xml.InternalContext;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
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
    /** Logger to use. */
    private static final Log LOG = LogFactory.getLog(Unmarshaller.class);
    /** The Castor unmarshaller doing the work ;-) . */
    private org.exolab.castor.xml.Unmarshaller _castorUnmarshaller;
    /** UnmarshalListener to use. */
    private UnmarshalListenerAdapter _unmarshalListener = null;
    /** The Castor XML context to use. */
    private InternalContext _internalContext;
    
    /**
     * The Unmarshaller is meant to be instantiated by JAXBContext only!
     * @param castorUnmarshaller the Castor Unmarshaller to use
     */
    protected Unmarshaller(final org.exolab.castor.xml.Unmarshaller castorUnmarshaller) {
        _castorUnmarshaller = castorUnmarshaller;
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getAdapter(java.lang.Class)
     */
    public < A extends XmlAdapter > A getAdapter(final Class < A > arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getAttachmentUnmarshaller()
     */
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getEventHandler()
     */
    public ValidationEventHandler getEventHandler() throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getListener()
     */
    public Listener getListener() {
        return (_unmarshalListener == null) ? null : _unmarshalListener.getJAXBListener();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getProperty(java.lang.String)
     */
    public Object getProperty(final String property) throws PropertyException {
        return _internalContext.getProperty(property);
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getSchema()
     */
    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#getUnmarshallerHandler()
     */
    public UnmarshallerHandler getUnmarshallerHandler() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#isValidating()
     */
    public boolean isValidating() throws JAXBException {
        return _castorUnmarshaller.isValidating();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#setAdapter(javax.xml.bind.annotation.adapters.XmlAdapter)
     */
    public void setAdapter(final XmlAdapter xmlAdapter) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#setAdapter(java.lang.Class, javax.xml.bind.annotation.adapters.XmlAdapter)
     */
    public < A extends XmlAdapter > void setAdapter(final Class < A > type, final A xmlAdapter) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#setAttachmentUnmarshaller(javax.xml.bind.attachment.AttachmentUnmarshaller)
     */
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller attachmentUnmarshaller) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#setEventHandler(javax.xml.bind.ValidationEventHandler)
     */
    public void setEventHandler(final ValidationEventHandler validationEventHandler) 
    throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
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
     * @see javax.xml.bind.Unmarshaller#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(final String property, final Object value) throws PropertyException {
        _internalContext.setProperty(property, value);
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#setSchema(javax.xml.validation.Schema)
     */
    public void setSchema(final Schema schema) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
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
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.File)
     */
    public Object unmarshal(final File file) throws JAXBException {
        try {
            return unmarshal(new InputSource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new JAXBException("Problem unmarshalling from file " + file.getAbsolutePath(), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.InputStream)
     */
    public Object unmarshal(final InputStream inputStream) throws JAXBException {
        return unmarshal(new InputSource(inputStream));
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.Reader)
     */
    public Object unmarshal(final Reader reader) throws JAXBException {
        return unmarshal(new InputSource(reader));
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.net.URL)
     */
    public Object unmarshal(final URL url) throws JAXBException {
        try {
            return unmarshal(new InputSource(url.openStream()));
        } catch (IOException e) {
            throw new JAXBException("Problem unmarshalling from URL " + url.toExternalForm(), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.xml.sax.InputSource)
     */
    public Object unmarshal(final InputSource inputSource) throws JAXBException {
        try {
            return _castorUnmarshaller.unmarshal(inputSource);
        } catch (MarshalException e) {
            String message = "Problem unmarshalling from InputSource " 
                + inputSource.toString() + " exception: " + e;
            LOG.warn(message, e);
            throw new JAXBException(message, e);
        } catch (ValidationException e) {
            String message = "Problem unmarshalling from InputSource " 
                + inputSource.toString() + " exception: " + e;
            LOG.warn(message, e);
            throw new JAXBException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.w3c.dom.Node)
     */
    public Object unmarshal(final Node node) throws JAXBException {
        try {
            return _castorUnmarshaller.unmarshal(node);
        } catch (MarshalException e) {
            String message = "Problem unmarshalling from Node " 
                + node.toString() + " exception: " + e;
            LOG.warn(message);
            throw new JAXBException(message, e);
        } catch (ValidationException e) {
            String message = "Problem unmarshalling from Node " 
                + node.toString() + " exception: " + e;
            LOG.warn(message);
            throw new JAXBException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.transform.Source)
     */
    public Object unmarshal(final Source source) throws JAXBException {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            return unmarshal(streamSource.getInputStream());
        } else if (source instanceof DOMSource) {
            DOMSource domSource = (DOMSource) source;
            return unmarshal(domSource.getNode());
        } else if (source instanceof SAXSource) {
            SAXSource saxSource = (SAXSource) source;
            return unmarshal(saxSource.getInputSource());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLStreamReader)
     */
    public Object unmarshal(final XMLStreamReader xmlStreamReader) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLEventReader)
     */
    public Object unmarshal(final XMLEventReader xmlEventReader) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.w3c.dom.Node, java.lang.Class)
     */
    public < T > JAXBElement < T > unmarshal(
            final Node node, final Class < T > type) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.transform.Source, java.lang.Class)
     */
    public < T > JAXBElement < T > unmarshal(
            final Source node, final Class < T > type) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLStreamReader, java.lang.Class)
     */
    public < T > JAXBElement < T > unmarshal(
            final XMLStreamReader xmlStreamReader, final Class < T > type) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLEventReader, java.lang.Class)
     */
    public < T > JAXBElement < T > unmarshal(
            final XMLEventReader xmlEventReader, final Class < T > type) 
    throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * To set the Castor XML context to use.
     * @param internalContext the Castor XML Context to use
     */
    protected void setInternalContext(final InternalContext internalContext) {
        _internalContext = internalContext;
        _castorUnmarshaller.setInternalContext(_internalContext);
    }

}
