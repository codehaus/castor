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
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml;

import org.exolab.castor.xml.util.XMLClassDescriptorImpl;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.mapping.loader.FieldHandlerImpl;
import org.exolab.castor.mapping.loader.TypeInfo;
import org.exolab.castor.mapping.MappingException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Hashtable;

/**
 * A Helper class for the Marshaller and Unmarshaller,
 * basically the common code base between the two.
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class MarshalHelper {
    
          
    private static final String ADD = "add";
    private static final String GET = "get";
    private static final String SET = "set";
    
    
    private static final Class[] EMPTY_CLASS_ARGS = new Class[0];
    
    private static final StringMarshalInfo stringMarshalInfo =
        new StringMarshalInfo();

    /**
     * Returns the XMLClassDescriptor associated with a class of the given name
     * @param className, the class to find a XMLClassDescriptor for
     * @return the XMLClassDescriptor for the given class or null if not found.
    **/
    public static XMLClassDescriptor getClassDescriptor(String className) 
    {
	    return getClassDescriptor( className, null );
    } //-- getClassDescriptor

    /**
     * Returns the XMLClassDescriptor associated with a class of the given name
     * @param className, the class to find a XMLClassDescriptor for
     * @param loader the ClassLoader to use when loading classes, this can
     * be null to signal using the default ClassLoader.
     * @return the XMLClassDescriptor for the given class or null if not found.
    **/
    public static XMLClassDescriptor getClassDescriptor
        (String className, ClassLoader loader) 
    {
        XMLClassDescriptor  cdesc = null;
        
        if ((className == null) || (className.length() == 0)) 
            return cdesc;
            
        String dClassName = className+"Descriptor";

        // JDK 1.2 not JDK 1.1
	    //if ( loader == null ) 
	    //    loader = Thread.currentThread().getContextClassLoader();
	        
        try {
	        Class dClass;
	        if ( loader != null )
		        dClass = loader.loadClass(dClassName);
	        else
		        dClass = Class.forName(dClassName);
		        
            cdesc = (XMLClassDescriptor) dClass.newInstance();
        }
        catch(InstantiationException ie)   { /* :-) */ }
        catch(ClassNotFoundException cnfe) { /* ;-) */ }
        catch(IllegalAccessException iae)  { /* :-Þ */ }
        
        return cdesc;
    } //-- getClassDescriptor
    
    /**
     * Returns the XMLClassDescriptor for the given class, if no 
     * XMLClassDescriptor is found, an XMLClassDescriptor will
     * be automatically generated using Reflection.
     * @param c the Class to return the XMLClassDescriptor for
     * @return the XMLClassDescriptor for the given class, or one is created by
     * using reflection
     * @exception MarshalException when an error occurs during the 
     * retrieval or creation of a XMLClassDescriptor
    **/
    public static XMLClassDescriptor getClassDescriptor(Class c) 
        throws MarshalException
    {
        return getClassDescriptor(c, null);
    } //-- getClassDescriptor
    
    
    /**
     * Returns the XMLClassDescriptor for the given class, if no 
     * XMLClassDescriptor is found, an XMLClassDescriptor will
     * be automatically generated using Reflection.
     * @param c the Class to return the XMLClassDescriptor for
     * @param errorWriter a PrintWriter to report errors to
     * @return the XMLClassDescriptor for the given class, or one is created by
     * using reflection
     * @exception MarshalException when an error occurs during the 
     * retrieval or creation of a XMLClassDescriptor
    **/
    public static XMLClassDescriptor getClassDescriptor
        (Class c, PrintWriter errorWriter) throws MarshalException
    {
        if (c == null) return null;
        XMLClassDescriptor cdesc 
            = getClassDescriptor(c.getName(),c.getClassLoader());
        //-- generate ClassDescriptor if we need to
        if (cdesc == null) cdesc = generateClassDescriptor(c, errorWriter);
        return cdesc;
    } //-- getClassDescriptor
    
    /**
     * Creates an XMLClassDescriptor for the given class by using Reflection.
     * @param c the Class to create the XMLClassDescriptor for
     * @return the new XMLClassDescriptor created for the given class
     * @exception MarshalException when an error occurs during the creation
     * of the ClassDescriptor.
    **/
    public static XMLClassDescriptor generateClassDescriptor(Class c) 
        throws MarshalException
    {
        return generateClassDescriptor(c, null);
    } //-- generateClassDescriptor(Class)
    
    /**
     * Creates an XMLClassDescriptor for the given class by using Reflection.
     * @param c the Class to create the XMLClassDescriptor for
     * @param errorWriter a PrintWriter to print error information to
     * @return the new XMLClassDescriptor created for the given class
     * @exception MarshalException when an error occurs during the creation
     * of the ClassDescriptor.
    **/
    public static XMLClassDescriptor generateClassDescriptor
        (Class c, PrintWriter errorWriter) throws MarshalException
    {
        
        if (c == null) return null;
        
        //-- handle arrays
        if (c.isArray()) return null;
        
        //-- handle Strings
        if (c == String.class) {
            return stringMarshalInfo;
        }
        
        //-- handle base objects
        if ((c == Void.class) || 
            (c == Class.class)||
            (c == Object.class)) {
            throw new MarshalException (
                MarshalException.BASE_CLASS_OR_VOID_ERR );
        }
        
		if ((!c.isPrimitive()) && 
		    (!Serializable.class.isAssignableFrom( c ))) {
		    if (errorWriter != null) {
		        errorWriter.print("cannot create an XMLClassDescriptor for \'");
		        errorWriter.print(c.getName());
		        errorWriter.print("', it's not a primitive, or it doesn't");
		        errorWriter.println(" implement java.io.Serializable.");
		    }
            return null;
        }
            
        XMLClassDescriptorImpl classDesc = new XMLClassDescriptorImpl(c);
        
        //-- handle primitives...should we have default MarshalInfo classes
        //-- for primitives...probably
        
        //-- handle complex objects
        Hashtable descriptors = new Hashtable();
        Method[] methods = c.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            //-- if method comes from the Object base class, ignore
            if (method.getDeclaringClass() == Object.class) continue;
            
            String methodName = method.getName();
            
            //-- read methods
            if (methodName.startsWith(GET)) {
                if (method.getParameterTypes().length != 0) continue;
            
                //-- caclulate name from Method name
                String fieldName = methodName.substring(3);
                String xmlName   = toXMLName(fieldName);
            
                Class type = method.getReturnType();

                if (!isDescriptable(type)) continue;
                
			    XMLFieldDescriptorImpl fieldDesc 
			        = (XMLFieldDescriptorImpl) descriptors.get(xmlName);
            			
			    if (fieldDesc == null) {
			        fieldDesc = createFieldDescriptor(type, fieldName, xmlName);
			        descriptors.put(xmlName, fieldDesc);
			        classDesc.addFieldDescriptor(fieldDesc);
			    }
			    
			    FieldHandlerImpl handler 
			        = (FieldHandlerImpl)fieldDesc.getHandler();
			        
			    if (handler == null) {
			        TypeInfo typeInfo = new TypeInfo(type);
			        try {
			            handler = new FieldHandlerImpl(fieldName,
			                                           method,
			                                           null, 
			                                           typeInfo);
			        }
			        catch (MappingException mx) {
			            throw new MarshalException(mx);
			        }
			        
			        fieldDesc.setHandler(handler);
			    }
			    else {
			        try {
			            handler.setReadMethod(method);
			        }
			        catch(MappingException mx) {
			            throw new MarshalException(mx);
			        }
			    }
			    
            }
            //-- write methods
            else if (methodName.startsWith(ADD) ||
                     methodName.startsWith(SET) ) {
        
                if (method.getParameterTypes().length != 1) continue;
                //-- caclulate name from Method name
                String fieldName = methodName.substring(3);
                String xmlName   = toXMLName(fieldName);
            
                Class type = method.getParameterTypes()[0];
                if (!isDescriptable(type)) continue;
                
			    XMLFieldDescriptorImpl fieldDesc 
			        = (XMLFieldDescriptorImpl) descriptors.get(xmlName);
            			
			    if (fieldDesc == null) {
			        fieldDesc = createFieldDescriptor(type, fieldName, xmlName);
			        descriptors.put(xmlName, fieldDesc);
			        classDesc.addFieldDescriptor(fieldDesc);
			    }
			    //-- collection
			    if (methodName.startsWith(ADD)) {
                    fieldDesc.setNodeType(NodeType.Element);
                }
			    
			    FieldHandlerImpl handler 
			        = (FieldHandlerImpl)fieldDesc.getHandler();
			        
			    if (handler == null) {
			        TypeInfo typeInfo = new TypeInfo(type);
			        try {
			            handler = new FieldHandlerImpl(fieldName,
			                                           null,
			                                           method, 
			                                           typeInfo);
			        }
			        catch (MappingException mx) {
			            throw new MarshalException(mx);
			        }
			        fieldDesc.setHandler(handler);
			    }
			    else {
			        try {
    			        handler.setWriteMethod(method);
			        }
			        catch(MappingException mx) {
			            throw new MarshalException(mx);
			        }
			    }
            }
        }
        return classDesc;
    } //-- generateClassDescriptor
    
    public static boolean marshallable(Class type) {
        
        //-- make sure type is not Void, or Class;
		if  (type == Void.class || type == Class.class ) return false;
		        
		if (( !type.isInterface() || (type == Object.class))) {
			if (!type.isPrimitive()) {
			            
			    //-- make sure type is serializable
			    if (!Serializable.class.isAssignableFrom( type ))
                   return false;
                
                //-- make sure we can construct the Object
				if (!type.isArray() ) {
		            //-- try to get the default constructor and make
		            //-- sure we are only looking at classes that can 
		            //-- be instantiated by calling Class#newInstance
			        try {
			            type.getConstructor( EMPTY_CLASS_ARGS );
			        }
				    catch ( NoSuchMethodException e ) { 
				        return false;
				    }
				}
			}
		}
		return true;
    } //-- marshallable
    
    /**
     * Converts the given xml name to a Java name.
     * @param name the name to convert to a Java Name
     * @param upperFirst a flag to indicate whether or not the
     * the first character should be converted to uppercase. 
    **/
    public static String toJavaName(String name, boolean upperFirst) {
        
        int size = name.length();
        char[] ncChars = name.toCharArray();
        int next = 0;
        
        boolean uppercase = upperFirst;
        
        for (int i = 0; i < size; i++) {
            char ch = ncChars[i];
            
            switch(ch) {
                case ':':
                case '-':
                    uppercase = true;
                    break;
                default:
                    if (uppercase == true) {
                        ncChars[next] = Character.toUpperCase(ch);
                        uppercase = false;
                    }
                    else ncChars[next] = ch;
                    ++next;
                    break;
            }
        }
        return new String(ncChars,0,next);
    } //-- toJavaName
    
    /**
     * Converts a String to the given primitive object type
     * @param type the class type of the primitive in which
     * to convert the String to
     * @param value the String to convert to a primitive
     * @return the new primitive Object
    **/
    public static Object toPrimitiveObject(Class type, String value) {
        
        Object primitive = null;
        
        //-- I tried to order these in the order in which
        //-- (I think) types are used more frequently
        // int
        if (type == Integer.TYPE) {
            primitive = new Integer(value);
        }
        // boolean
        else if (type == Boolean.TYPE)
            primitive = new Boolean(value);
        // double
        else if (type == Double.TYPE)
            primitive = new Double(value);
        // long
        else if (type == Long.TYPE)
            primitive = new Long(value);
        // char
        else if (type == Character.TYPE) {
            if (value.length() > 0)
                primitive = new Character(value.charAt(0));
            else 
                primitive = new Character('\0');
        }
        // short
        else if (type == Short.TYPE)
            primitive = new Short(value);
        // float
        else if (type == Float.TYPE)
            primitive = new Float(value);
        // byte
        else if (type == Byte.TYPE)
            primitive = new Byte(value);
        // do nothing
        else 
            primitive = value;
            
        return primitive;
    } //-- toPrimitiveObject
    
    /**
     * Converts the given name to an XML name. It would be nearly
     * impossible for this method to please every one, so I picked
     * common "de-facto" XML naming conventions. This can be overridden
     * by implementing the MarshalInfo for your classes.
     * @param name the String to convert to an XML name
     * @return the xml name representation of the given String
     * <BR /><B>examples:</B><BR />
     * "Blob" becomes "blob" and "DataSource" becomes "data-source".
     * An XML
    **/
    public static String toXMLName(String name) {
        
        if (name == null) return null;
        if (name.length() == 0) return name;
        if (name.length() == 1) return name.toLowerCase();
        
        //-- Follow the Java beans Introspector::decapitalize
        //-- convention by leaving alone String that start with
        //-- 2 uppercase characters.
        if (Character.isUpperCase(name.charAt(0)) &&
            Character.isUpperCase(name.charAt(1))) return name;
            
        //-- process each character
        StringBuffer cbuff = new StringBuffer(name);
        cbuff.setCharAt(0, Character.toLowerCase(cbuff.charAt(0)));
        
        boolean ucPrev = false;
        for (int i = 1; i < cbuff.length(); i++) {
            char ch = cbuff.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (ucPrev) continue;
                ucPrev = true;
                cbuff.insert(i,'-');
                ++i;
                cbuff.setCharAt(i, Character.toLowerCase(ch));
            }
            else ucPrev = false;
        }
        return cbuff.toString();
    }

    //-------------------/
    //- Private Methods -/
    //-------------------/
    
    private static XMLFieldDescriptorImpl createFieldDescriptor
        (Class type, String fieldName, String xmlName) 
    {
	
        XMLFieldDescriptorImpl fieldDesc =
            new XMLFieldDescriptorImpl(type, fieldName, xmlName, null);
            
        if (type.isArray()) {
            fieldDesc.setNodeType(NodeType.Element);
        }
        //-- primitive types are converted to attributes by default
        else if ((type.isPrimitive()) || (type == String.class)) {
            fieldDesc.setNodeType(NodeType.Attribute);
        }
        else {
            fieldDesc.setNodeType(NodeType.Element);
        }
        
        return fieldDesc;
    } //-- createFieldDescriptor

    /**
     * Returns true if we are allowed to create a descriptor
     * for a given class type
     * @param type the Class type to test
     * @return true if we are allowed to create a descriptor
     * for a given class type
    **/
    private static boolean isDescriptable(Class type) {
        //-- make sure type is not Void, or Class;
		if  (type == Void.class || type == Class.class ) return false;
		        
		if ( (!type.isInterface())  && 
		     (type != Object.class) &&
			 (!type.isPrimitive())) {
			            
			//-- make sure type is serializable
			if (!Serializable.class.isAssignableFrom( type ))
                return false;
                
            //-- make sure we can construct the Object
			if (!type.isArray() ) {
		        //-- try to get the default constructor and make
		        //-- sure we are only looking at classes that can 
		        //-- be instantiated by calling Class#newInstance
			    try {
			        type.getConstructor( EMPTY_CLASS_ARGS );
			    }
				catch ( NoSuchMethodException e ) { 
				    return false;
				}
			}
		}
		return true;
    } //-- isDescriptable
    
} //-- MarshalHelper
