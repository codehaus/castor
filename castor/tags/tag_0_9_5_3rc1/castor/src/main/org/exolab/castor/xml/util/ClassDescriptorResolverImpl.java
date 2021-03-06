/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999-2002 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.xml.util;

import org.exolab.castor.xml.*;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.util.Configuration;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * The default implementation of the ClassDescriptorResolver interface
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
 */
public class ClassDescriptorResolverImpl
    implements ClassDescriptorResolver 
{
 
    private static final String DESCRIPTOR_PREFIX = "Descriptor";
    
    /**
     * internal cache for class descriptors
     * with the class as the key
     */
    private Hashtable _cacheViaClass = null;
    
    /**
     * internal cache for class descriptors
     * with the xml-name as the key
     */
    private Hashtable _cacheViaName = null;
    
    /**
     * Keeps a list of ClassNotFoundExceptions
     * for faster lookups than using loadClass().
     * hopefully this won't eat up too much
     * memory.
     */
    private Hashtable _classNotFoundList = null;
    
    /**
     * A flag indicating an error has occured
    **/
    private boolean   _error      = false;
    
    
    /**
     * Error message
    **/
    private String    _errMessage = null;
    
    /**
     * The introspector to use, if necessary, to 
     * create dynamic ClassDescriptors
     */
    private Introspector _introspector = null;
    
    /**
     * The classloader to use
     */
    private ClassLoader _loader  = null;
    
    /**
     * MappingLoader instance for finding user-defined
     * mappings from a mapping-file
     */
    private XMLMappingLoader _mappingLoader = null;
    
   
    
    /** 
     * A flag to indicate the use of introspection
     */
    private boolean _useIntrospection = true;
    
    /**
     * Creates a new ClassDescriptorResolverImpl
     */
    public ClassDescriptorResolverImpl() {
        _cacheViaClass = new Hashtable();
        _cacheViaName  = new Hashtable();
    } //-- ClassDescriptorResolverImpl


    /**
     * Creates a new ClassDescriptorResolverImpl with the given ClassLoader
     *
     * @param loader the ClassLoader to use when loading ClassDescriptors
     */
    public ClassDescriptorResolverImpl(ClassLoader loader) {
        this();
        _loader = loader;
    } //-- ClassDescriptorResolverImpl

    
    /**
     * Associates (or binds) a class type with a given ClassDescriptor
     *
     * @param type the Class to associate with the given descriptor
     * @param classDesc the ClassDescriptor to associate the given 
     * class with
     */
    public void associate(Class type, XMLClassDescriptor classDesc) {
        
        if (type == null) {
            throw new IllegalArgumentException("argument 'type' must not be null.");
        }
        
        //-- handle remove
        if (classDesc == null) {
            if (type != null) {
                _cacheViaClass.remove(type);
            }
        }
        else {
            _cacheViaClass.put(type, classDesc);
            String xmlName = classDesc.getXMLName();
            if ((xmlName != null) && (xmlName.length() > 0)) {
                String nameKey = xmlName;
                String ns = classDesc.getNameSpaceURI();             
                if ((ns != null) && (ns.length() > 0)) {
                    nameKey = ns + ':' + xmlName;
                }
                _cacheViaName.put(nameKey, classDesc);
            }
        }
    } //-- associate
    
    /**
     * Returns the last error message generated
     * If no error String exists, null will be returned
     * @return the last error message generated.
     * If no error String exists, null will be returned
    **/
    public String getErrorMessage() {
        return _errMessage;
    } //-- getErrorMessage
    
    /**
     * Returns the Introspector being used by this ClassDescriptorResolver.
     * This allows for configuration of the Introspector.
     *
     * @return the Introspector being used by this ClassDescriptorResolver
    **/
    public Introspector getIntrospector() {
        if (_introspector == null)
            _introspector = new Introspector();
        return _introspector;
    } //-- getIntrospector
    
    public XMLMappingLoader getMappingLoader() {
        return _mappingLoader;
    } //-- getXMLMappingLoader
    
    /**
     * Returns true if an error was generated on the last call
     * to one of the resolve methods
     * @return true if an error was generated on the last call
     * to one of the resolve methods
    **/
    public boolean error() {
        return _error;
    } //-- error
    
    /**
     * Returns the XMLClassDescriptor for the given class
     * @param type the Class to find the XMLClassDescriptor for
     * @return the XMLClassDescriptor for the given class
    **/
    public XMLClassDescriptor resolve(Class type) {
        
        clearError();

        if (type == null) return null;
        
        XMLClassDescriptor classDesc = (XMLClassDescriptor) _cacheViaClass.get(type);
        
        if (classDesc != null) return classDesc;

        //-- check mapping loader first 
        //-- [proposed by George Stewart]
        if (_mappingLoader != null) {            
            classDesc = (XMLClassDescriptor)_mappingLoader.getDescriptor(type);
            if (classDesc != null) {
               _cacheViaClass.put(type, classDesc);
               return classDesc;
            }
        }
       
         
        String className = type.getName() + DESCRIPTOR_PREFIX;
        try {
            ClassLoader loader = type.getClassLoader();
            Class dClass = loadClass(className, loader);            
            classDesc = (XMLClassDescriptor) dClass.newInstance();
            _cacheViaClass.put(type, classDesc);
        }
        catch(ClassNotFoundException cnfe) { 
            /* 
             This is ok, since we are just checking if the
             Class exists...if not we create one.
            */ 
        }
        catch(Exception ex) {
            String err = "instantiation error for class: " + className;
            err += "; " + ex.toString();
            setError(err);
            return null;
        }
        
        //-- create classDesc automatically if necessary
        if ((classDesc == null) && _useIntrospection) {
            try {
                classDesc = getIntrospector().generateClassDescriptor(type);
                if (classDesc != null) {
                    _cacheViaClass.put(type, classDesc);
                }
            }
            catch (MarshalException mx) {
                String err = mx.toString();
                setError(err);
                return null;
            }
        }
        return classDesc;
    } //-- resolve
    
    /**
     * Returns the XMLClassDescriptor for the given class name
     * @param className the class name to find the XMLClassDescriptor for
     * @return the XMLClassDescriptor for the given class name
    **/
    public XMLClassDescriptor resolve(String className) {
        return resolve(className, null);
    } //-- resolve(String)
    
    /**
     * Returns the XMLClassDescriptor for the given class name
     * @param className the class name to find the XMLClassDescriptor for
     * @param loader the ClassLoader to use
     * @return the XMLClassDescriptor for the given class name
    **/
    public XMLClassDescriptor resolve(String className, ClassLoader loader) {
        
        XMLClassDescriptor classDesc = null;
        
        if ((className == null) || (className.length() == 0)) {
            clearError(); //-- clear previous error flag
            setError("Cannot resolve a null or zero-length class name.");
            return null;
        }
            
        //-- first check mapping loader
        if (_mappingLoader != null) {
            classDesc = (XMLClassDescriptor)_mappingLoader.getDescriptor(className);
            if (classDesc != null)
                return classDesc;
        }
        
        //-- try and load class to check cache,
        Class _class = null;
        try {
            _class = loadClass(className, loader);
        }
        catch(ClassNotFoundException cnfe) { 
            //-- save exception, for future calls with
            //-- the same classname
            if (_classNotFoundList == null)
                _classNotFoundList = new Hashtable();
            _classNotFoundList.put(className, cnfe);
        }
        
        if (_class != null) {
            classDesc = resolve(_class);
        }
        else clearError(); //-- clear error flag
        
        //-- try to load ClassDescriptor with no class being
        //-- present...does this make sense?
        if ((classDesc == null) && (_class == null)) {
            String dClassName = className+DESCRIPTOR_PREFIX;
            try {
	            Class dClass = loadClass(dClassName, loader);
                classDesc = (XMLClassDescriptor) dClass.newInstance();
                if (classDesc.getJavaClass() != null) {
                    associate(classDesc.getJavaClass(), classDesc);
                }
            }
            catch(InstantiationException ie) {  
                //-- do nothing for now
            }
            catch(IllegalAccessException iae)  {
                //-- do nothing for now
            }
            catch(ClassNotFoundException cnfe) { 
                //-- save exception, for future calls with
                //-- the same classname
                if (_classNotFoundList == null)
                    _classNotFoundList = new Hashtable();
                _classNotFoundList.put(className, cnfe);
            }
        }
        return classDesc;
    } //-- resolve(String, ClassLoader)
    
    /**
     * Returns the first XMLClassDescriptor that matches the given
     * XML name and namespaceURI. Null is returned if no descriptor
     * can be found.
     *
     * @param className the class name to find the XMLClassDescriptor for
     * @param loader the ClassLoader to use
     * @return the XMLClassDescriptor for the given XML name
    **/
    public XMLClassDescriptor resolveByXMLName
        (String xmlName, String namespaceURI, ClassLoader loader) 
    {
        if ((xmlName == null) || (xmlName.length() == 0)) {
            clearError(); //-- clear previous error flag
            setError("Cannot resolve a null or zero-length xml name.");
            return null;
        }
        
        
        XMLClassDescriptor classDesc = null;
        Enumeration enum             = null;
        
        //-- check name cache first...
        String nameKey = xmlName;
        if ((namespaceURI != null) && (namespaceURI.length() > 0))
            nameKey = namespaceURI + ':' + xmlName;
            
        classDesc = (XMLClassDescriptor)_cacheViaName.get(nameKey);
        if(classDesc != null)
            return classDesc;

        //-- next check mapping loader...
        XMLClassDescriptor possibleMatch = null;
        if (_mappingLoader != null) {
            enum = _mappingLoader.listDescriptors();
            while (enum.hasMoreElements()) {
                classDesc = (XMLClassDescriptor)enum.nextElement();
                if (xmlName.equals(classDesc.getXMLName())) {
                    if (namespaceEquals(namespaceURI, classDesc.getNameSpaceURI())) {
                        _cacheViaName.put(nameKey, classDesc);
                        return classDesc;
                    }
                    possibleMatch = classDesc;
                }
                classDesc = null;
            }
        }
        
        //-- next look in local cache
        enum = _cacheViaClass.elements();
        while (enum.hasMoreElements()) {
            classDesc = (XMLClassDescriptor)enum.nextElement();
            if (xmlName.equals(classDesc.getXMLName())) {
                if (namespaceEquals(namespaceURI, classDesc.getNameSpaceURI())) {
                    _cacheViaName.put(nameKey, classDesc);
                    return classDesc;
                }
                if (possibleMatch == null) 
                    possibleMatch = classDesc;
                else if (possibleMatch != classDesc) {
                    //-- too many possible matches, return null.
                    possibleMatch = null;
                }
            }
            classDesc = null;
        }
        
        if (classDesc == null) 
            classDesc = possibleMatch;
            
        return classDesc;
   
    } //-- resolveByXMLName

    /**
     * Returns an enumeration of XMLClassDescriptor objects that
     * match the given xml name
     *
     * @param className the class name to find the XMLClassDescriptor for
     * @param loader the ClassLoader to use
     * @return an enumeration of XMLClassDescriptor objects.
    **/
    public ClassDescriptorEnumeration resolveAllByXMLName
        (String xmlName, String namespaceURI, ClassLoader loader)
    {
        if ((xmlName == null) || (xmlName.length() == 0)) {
            clearError(); //-- clear previous error flag
            setError("Cannot resolve a null or zero-length xml name.");
            return null;
        }
        
        XCDEnumerator xcdEnumerator  = new XCDEnumerator();
        XMLClassDescriptor classDesc = null;
        Enumeration enum             = null;
        
        //-- check mapping loader first
        if (_mappingLoader != null) {
            enum = _mappingLoader.listDescriptors();
            while (enum.hasMoreElements()) {
                classDesc = (XMLClassDescriptor)enum.nextElement();
                if (xmlName.equals(classDesc.getXMLName())) {
                    xcdEnumerator.add(classDesc);
                }
            }
        }
        
        //-- next look in local cache
        enum = _cacheViaClass.elements();
        while (enum.hasMoreElements()) {
            classDesc = (XMLClassDescriptor)enum.nextElement();
            if (xmlName.equals(classDesc.getXMLName())) {
                xcdEnumerator.add(classDesc);
            }
        }
        
        return xcdEnumerator;
        
    } //-- resolveByXMLName
    
    /**
     * Sets the ClassLoader to use when loading class descriptors
     * @param loader the ClassLoader to use
    **/
    public void setClassLoader(ClassLoader loader) {
        this._loader = loader;
    } //-- setClassLoader
    
    /**
     * Enables or disables introspection. Introspection is
     * enabled by default.
     *
     * @param enable a flag to indicate whether or not introspection
     * is allowed.
    **/
    public void setIntrospection(boolean enable) {
        _useIntrospection = enable;
    } //-- setIntrospection
    
    public void setMappingLoader(XMLMappingLoader mappingLoader) {
        _mappingLoader = mappingLoader;
    } //-- setMappingLoader
    
    //-------------------/
    //- Private Methods -/
    //-------------------/
    
    /**
     * Loads and returns the class with the given class name using the 
     * given loader.
     * @param className the name of the class to load
     * @param loader the ClassLoader to use, this may be null.
    **/
    private Class loadClass(String className, ClassLoader loader) 
        throws ClassNotFoundException
    {
        //-- for performance, check the classNotFound list
        //-- first to prevent searching through all the 
        //-- possible classpaths etc for something we
        //-- already know doesn't exist.
        //-- check classNotFoundList 
        if (_classNotFoundList != null) {
            Object exception = _classNotFoundList.get(className);
            if (exception != null)
                throw (ClassNotFoundException)exception;
        }
        
        //-- use passed in loader
	    if ( loader != null )
		    return loader.loadClass(className);
		//-- use internal loader
		else if (_loader != null)
		    return _loader.loadClass(className);
		//-- no loader available use Class.forName
		return Class.forName(className);
    } //-- loadClass
    
    /**
     * Clears the error flag
    **/
    private void clearError() {
        _error = false;
    } //-- clearError
    
    /**
     * Sets the current error message to the given String
     * @param message the error message
    **/
    private void setError(String message) {
        _error = true;
        _errMessage = message;
    } //-- setError
    
    
    /** 
     * Compares the two strings for equality. A Null and empty
     * strings are considered equal.
     *
     * @return true if the two strings are considered equal.
     */
    private boolean namespaceEquals(String ns1, String ns2) {
        if (ns1 == null) {
            return ((ns2 == null) || (ns2.length() == 0));
        }
        
        if (ns2 == null) {
            return (ns1.length() == 0);
        }
        
        return ns1.equals(ns2);
    } //-- namespaceEquals

    /**
     * A locally used implementation of ClassDescriptorEnumeration
     */
    class XCDEnumerator implements ClassDescriptorEnumeration {
        
        private Entry _current = null;
        
        private Entry _last = null;
        
        /**
        * Creates an XCDEnumerator
        **/
        XCDEnumerator() {
            super();
        } //-- XCDEnumerator
        
        /**
        * Adds the given XMLClassDescriptor to this XCDEnumerator
        **/
        protected void add(XMLClassDescriptor classDesc) {
            Entry entry = new Entry();
            entry.classDesc = classDesc;
            if (_current == null) {
                _current = entry;
                _last    = entry;
            }
            else {
                _last.next = entry;
                _last = entry;
            }
        } //-- add
        
        /** 
        * Returns true if there are more XMLClassDescriptors
        * available.
        *
        * @return true if more XMLClassDescriptors exist within this
        * enumeration.
        **/
        public boolean hasNext() {
            return (_current != null);
        } //-- hasNext
        
        /**
        * Returns the next XMLClassDescriptor in this enumeration.
        *
        * @return the next XMLClassDescriptor in this enumeration.
        **/
        public XMLClassDescriptor getNext() {
            if (_current == null) return null;
            Entry entry = _current;
            _current = _current.next;
            return entry.classDesc;
        } //-- getNext
            
        class Entry {
            XMLClassDescriptor classDesc = null;
            Entry next = null;
        }
        
    } //-- ClassDescriptorEnumeration
    
} //-- ClassDescriptorResolverImpl




