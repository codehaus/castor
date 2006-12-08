package org.castor.jaxb;

import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

public class JAXBContext extends javax.xml.bind.JAXBContext {
    
    /**
     * Class to (un)marshall ith JAXB
     */
    private Class aClass;

    /**
     * Creates an instance of JAXBContext, configured for (un)marshalling
     * the specified class
     * @param aClass The class to be (un)marshalled.
     */
    public JAXBContext (Class aClass) {
        this.aClass = aClass;
    }

    /**
     * Creates an instance of this class, and binds the classes specified 
     * for further use.
     * @param classesToBeBound Classes to be bound for further use
     * @param properties Configuration properties
     * @throws JAXBException If there's no valid classes specified
     */
    public JAXBContext(Class[] classesToBeBound, Map properties) 
    throws JAXBException {
        
        if (classesToBeBound == null) {
            throw new JAXBException ("No classes specified.");
        }

        if (classesToBeBound.length == 0) {
            throw new JAXBException ("No classes specified.");
        }
        
        // TODO [WG]: this is a hack !!! remove
        this.aClass = classesToBeBound[0];
    }

    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return new org.castor.jaxb.Marshaller();
    }

    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        return new org.castor.jaxb.Unmarshaller(this.aClass);
    }

    @Override
    public Validator createValidator() throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static JAXBContext newInstance(String contextPath) {
        throw new UnsupportedOperationException();
    }
    
    public static JAXBContext newInstance(String contextPath, ClassLoader contextPathCL) {
        throw new UnsupportedOperationException();
    }

    public static JAXBContext newInstance(String contextPath, ClassLoader contextPathCL, Map properties) {
        throw new UnsupportedOperationException();
    }

    public static JAXBContext newInstance(Class... classesToBeBound) throws JAXBException {
        return new JAXBContext(classesToBeBound, null);
    }

    public static JAXBContext newInstance(Class[] classesToBeBound, Map properties) throws JAXBException {
        return new JAXBContext (classesToBeBound, properties);
    }

    public JAXBIntrospector createJAXBIntrospector() {
        throw new UnsupportedOperationException();
    }
    
    public <T> Binder<T> createBinder(Class<T> domType) {
        throw new UnsupportedOperationException();
    }
        
    public Binder<org.w3c.dom.Node> createBinder() {
        throw new UnsupportedOperationException();
    }
    
    public void generateSchema(SchemaOutputResolver schemaOutputResolver) {
        throw new UnsupportedOperationException();
    }

}
