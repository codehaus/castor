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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

public class DataBindingTemplate implements DataBindingOperations {
    /**
     * Log instance
     */
    private static final Log LOG = LogFactory.getLog(DataBindingTemplate.class);

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    /**
     * @see org.castor.spring.xml.DataBindingOperations#marshal(java.lang.Object, java.io.Writer)
     */
    public void marshal(Object object, Writer writer) 
        throws MarshallingException, IOException, ValidationException {
        marshaller.setWriter(writer);
        try {
            marshaller.marshal(object);
        } catch (MarshalException e) {
            LOG.warn("Unmarshal failed with exception: " + e);
            throw new MarshallingException(e.getMessage(), e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            LOG.warn("Unmarshal failed with exception: " + e);
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.xml.DataBindingOperations#unmarshal(org.xml.sax.InputSource, java.lang.Class)
     */
    public Object unmarshal(InputSource inputSource, Class type) throws MarshallingException, IOException, ValidationException {
        Object object;
        try {
            object = unmarshaller.unmarshal(inputSource);
        } catch (MarshalException e) {
            LOG.warn("Unmarshal failed with exception: " + e);
            throw new MarshallingException(e.getMessage(), e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            LOG.warn("Unmarshal failed with exception: " + e);
            throw new ValidationException(e.getMessage(), e);
        }
        return object;
    }

    /**
     * Sets the Marshaller instance to use for marshalling.
     * @param marshaller Castor XML Marshaller instance
     */
    public void setMarshaller(final Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    /**
     * Sets the Unmarshaller instance to use for unmarshalling.
     * @param unmarshaller Castor XML UNmarshaller instance
     */
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

//    /** 
//     * @inheritDoc
//     * @see org.springframework.xml.castor.DataBindingOperations#unmarshal(javax.xml.transform.sax.SAXSource, java.lang.Class)
//     */
//    public Object unmarshal(SAXSource saxSource, Class type) throws MarshallingException, IOException, ValidationException {
//        Object object;
//        try {
//            object = unmarshaller.unmarshal(saxSource);
//        } catch (MarshalException e) {
//            throw new MarshallingException(e.getMessage(), e);
//        } catch (org.exolab.castor.xml.ValidationException e) {
//            throw new ValidationException(e.getMessage(), e);
//        }
//        return object;
//    }

//    /**
//     * @inheritDoc
//     * @see org.springframework.xml.castor.DataBindingOperations#unmarshal(javax.xml.transform.dom.DOMSource, java.lang.Class)
//     */
//    public Object unmarshal(DOMSource domSource, Class type) throws MarshallingException, IOException, ValidationException {
//        Object object;
//        try {
//            object = unmarshaller.unmarshal(domSource);
//        } catch (MarshalException e) {
//            throw new MarshallingException(e.getMessage(), e);
//        } catch (org.exolab.castor.xml.ValidationException e) {
//            throw new ValidationException(e.getMessage(), e);
//        }
//        return object;
//    }

    /**
     * @inheritDoc
     * @see org.castor.spring.xml.DataBindingOperations#unmarshal(java.io.Reader, java.lang.Class)
     */
    public Object unmarshal(Reader reader, Class type) throws MarshallingException, IOException, ValidationException {
        Object object;
        try {
            object = unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            LOG.warn("Unmarshal failed with exception: " + e);
            throw new MarshallingException(e.getMessage(), e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            LOG.warn("Unmarshal failed with exception: " + e);
            throw new ValidationException(e.getMessage(), e);
        }
        return object;
    }
    
    

}
