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

package org.exolab.castor.xml;


//-- castor imports
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.Configuration;
import org.exolab.castor.xml.util.*;

//-- misc xml related imports
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.helpers.XMLReaderAdapter;

//-- Java imports
import java.io.IOException;
import java.io.Reader;
import java.io.PrintWriter;

/**
 * An unmarshaller to allowing unmarshalling of XML documents to
 * Java Objects. The Class must specify
 * the proper access methods (setters/getters) in order for instances
 * of the Class to be properly unmarshalled.
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class Unmarshaller {

    //----------------------------/
    //- Private Member Variables -/
    //----------------------------/

    /**
     * The Class that this Unmarshaller was created with
    **/
    private Class _class = null;

    
    /**
     * The class descriptor resolver
    **/
    private ClassDescriptorResolver _cdResolver = null;

    /**
     * A user specified IDResolver for resolving IDREFs
    **/
    private IDResolver _idResolver = null;

    /**
     * A boolean that specifies whether or not
     * non-matched attributes should be ignored upon
     * unmarshalling.
    **/
    private boolean _ignoreExtraAtts = true;
    
    /**
     * The instance of _class to Unmarshal into (optional)
    **/
    private Object _instanceObj = null;
    
    /**
     * The EntityResolver used for resolving entities
    **/
    EntityResolver entityResolver = null;

    /**
     * The class loader to use
    **/
    private ClassLoader _loader = null;

    /**
     * The print writer used for log information
    **/
    private PrintWriter _pw = null;

    /**
     * The flag indicating whether or not to display debug information
    **/
    private boolean _debug = false;

    /**
     * A boolean to indicate that objects should
     * be re-used where appropriate
    **/
    private boolean _reuseObjects = false;

    /**
     * A boolean that indicates attribute processing should
     * be strict and an error should be flagged if any
     * extra attributes exist.
    **/
    private boolean _strictAttributes = false;
    
    /**
     * The flag indicating whether or not to validate during
     * unmarshalling
    **/
    private boolean _validate = false;

    //----------------/
    //- Constructors -/
    //----------------/


    /**
     * Creates a new Unmarshaller with the given Class
     * @param c the Class to create the Unmarshaller for, this
     * may be null, if the Unmarshaller#setMapping is called
     * to load a mapping for the root element of xml document.
    **/
    public Unmarshaller(Class c) {
        this(c, null);
    } //-- Unmarshaller(Class)

    /**
     * Creates a new Unmarshaller with the given Class
     * @param c the Class to create the Unmarshaller for, this
     * may be null, if the Unmarshaller#setMapping is called
     * to load a mapping for the root element of xml document.
     * @param loader, the ClassLoader to use.
    **/
    public Unmarshaller(Class c, ClassLoader loader) {
        super();
        initConfig();
        this._class = c;
        _loader = loader;
        _cdResolver = new ClassDescriptorResolverImpl(loader);
    } //-- Unmarshaller(Class)


    /**
     * Creates a new Unmarshaller with the given Mapping
     * @param mapping, the Mapping to use
    **/
    public Unmarshaller(Mapping mapping)
        throws MappingException
    {
        super();
        initConfig();
        if (mapping != null) {
            setMapping(mapping);
            this._loader = mapping.getClassLoader();
        }
    } //-- Unmarshaller(Mapping)
    
    
    /**
     * Creates a new Unmarshaller with the given Object
     * @param root the instance to unmarshal into. This
     * may be null, if the Unmarshaller#setMapping is called
     * to load a mapping for the root element of xml document.
     *
     * @param root the Object instance to unmarshal into
    **/
    public Unmarshaller(Object root) {
        initConfig();
        if (root != null) {
            _class = root.getClass();
            _loader = _class.getClassLoader();
        }
        _instanceObj = root;
    } //-- Unmarshaller(Class)

    /**
     * Used by constructors to get configuration information
    **/
    private void initConfig() {
        _debug = Configuration.debug();
        _validate = Configuration.marshallingValidation();
    } //-- initConfig
    
    /**
     * Creates and initalizes an UnmarshalHandler
     * @return the new UnmarshalHandler
    **/
    public UnmarshalHandler createHandler() {
        
        UnmarshalHandler handler = new UnmarshalHandler(_class);
        handler.setResolver(_cdResolver);
        handler.setLogWriter(_pw);
        handler.setDebug(_debug);
        handler.setReuseObjects(_reuseObjects);
        handler.setValidation(_validate);
        handler.setIgnoreExtraAttributes(_ignoreExtraAtts);

        if (_instanceObj != null) {
            handler.setRootObject(_instanceObj);
        }
        if (_idResolver != null)
            handler.setIDResolver(_idResolver);

        if (_loader != null)
            handler.setClassLoader(_loader);

        return handler;
    } //-- createHandler
    
    /**
     * Sets the ClassLoader to use when loading new classes.
     * <br />
     * <b>Note:</b>This ClassLoader is used for classes
     * loaded by the unmarshaller only. If a Mapping has
     * been set, the Mapping has it's own ClassLoader and
     * may also need to be set propertly.
     * <br /> 
     *
     * @param loader the ClassLoader to use
    **/
    public void setClassLoader(ClassLoader loader) {
        this._loader = loader;
    } //-- setClassLoader


    /**
     * Turns debuging on or off. If no Log Writer has been set, then
     * System.out will be used to display debug information
     * @param debug the flag indicating whether to generate debug information.
     * A value of true, will turn debuggin on.
     * @see #setLogWriter
    **/
    public void setDebug(boolean debug) {
        _debug = debug;
    } //-- setDebug

    /**
     * Sets the EntityResolver to use when resolving system and
     * public ids with respect to entites and Document Type.
     * @param entityResolver the EntityResolver to use when
     * resolving System and Public ids.
    **/
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    } //-- entityResolver

    /**
     * Sets the IDResolver to use when resolving IDREFs for
     * which no associated element may exist in XML document.
     *
     * @param idResolver the IDResolver to use when resolving
     * IDREFs for which no associated element may exist in the
     * XML document.
    **/
    public void setIDResolver(IDResolver idResolver) {
        _idResolver = idResolver;
    } //-- idResolver

    /**
     * Sets whether or not attributes that do not match
     * a specific field should simply be ignored or
     * reported as an error. By default, extra attributes
     * are ignored.
     *
     * @param ignoreExtraAtts a boolean that when true will
     * allow non-matched attributes to simply be ignored.
    **/
    public void setIgnoreExtraAttributes(boolean ignoreExtraAtts) {
        _ignoreExtraAtts = ignoreExtraAtts;    
    } //-- setIgnoreExtraAttributes
    
    /**
     * Sets the PrintWriter used for logging
     * @param printWriter the PrintWriter to use for logging
    **/
    public void setLogWriter(PrintWriter printWriter) {
        _pw = printWriter;
    } //-- setLogWriter

    /**
     * Sets the Mapping to use during unmarshalling. If the
     * Mapping has a ClassLoader it will be used during
     * unmarshalling.
     *
     * @param mapping the Mapping to use during unmarshalling.
     * @see #setResolver
    **/
    public void setMapping( Mapping mapping )
        throws MappingException
    {
        if (_loader == null) {
            _loader = mapping.getClassLoader();
        }
        if (_cdResolver == null) {
            _cdResolver = new ClassDescriptorResolverImpl(_loader);
        }
        _cdResolver.setMappingLoader( (XMLMappingLoader) mapping.getResolver( Mapping.XML ) );
    } //-- setMapping


    /**
     * Sets a boolean that when true indicates that objects
     * contained within the object model should be re-used 
     * where appropriate. This is only valid when unmarshalling 
     * to an existing object.
     *
     * @param reuse the boolean indicating whether or not
     * to re-use existing objects in the object model.
    **/
    public void setReuseObjects(boolean reuse) {
        _reuseObjects = reuse;
    } //-- setReuseObjects

    /**
     * Sets the ClassDescriptorResolver to use during unmarshalling
     * @param cdr the ClassDescriptorResolver to use
     * @see #setMapping
     * <BR />
     * <B>Note:</B> This method will nullify any Mapping
     * currently being used by this Unmarshaller
    **/
    public void setResolver( ClassDescriptorResolver cdr ) {

        if (cdr != null)
            _cdResolver = cdr;
        else
            _cdResolver = new ClassDescriptorResolverImpl(_loader);

    } //-- setResolver

    /**
     * Sets the flag for validation
     * @param validate, a boolean to indicate whether or not
     * validation should be done during umarshalling. <br/>
     * By default validation will be performed.
    **/
    public void setValidation(boolean validate) {
        _validate = validate;
    } //-- setValidation


    /**
     * Unmarshals Objects of this Unmarshaller's Class type.
     * The Class must specify the proper access methods
     * (setters/getters) in order for instances of the Class
     * to be properly unmarshalled.
     * @param reader the Reader to read the XML from
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public Object unmarshal(Reader reader)
        throws MarshalException, ValidationException
    {
        return unmarshal(new InputSource(reader));
    } //-- unmarshal(Reader reader)

    /**
     * Unmarshals Objects of this Unmarshaller's Class type.
     * The Class must specify the proper access methods
     * (setters/getters) in order for instances of the Class
     * to be properly unmarshalled.
     * @param eventProducer the EventProducer which produces
     * the SAX events
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public Object unmarshal(EventProducer eventProducer)
        throws MarshalException, ValidationException
    {
        UnmarshalHandler handler = createHandler();
        eventProducer.setDocumentHandler(handler);
        try {
            eventProducer.start();
        }
        catch(org.xml.sax.SAXException sx) {
            Exception except = sx.getException();
            if (except == null) except = sx;
            MarshalException marshalEx = new MarshalException(except);
            if(handler.getDocumentLocator()!=null)
            {
                FileLocation location = new FileLocation();
                location.setFilename(handler.getDocumentLocator().getSystemId());
                location.setLineNumber(handler.getDocumentLocator().getLineNumber());
                location.setColumnNumber(handler.getDocumentLocator().getColumnNumber());
                marshalEx.setLocation(location);
            }
            throw marshalEx;
        }
        return handler.getObject();

    } //-- unmarshal(EventProducer)



    /**
     * Unmarshals Objects of this Unmarshaller's Class type.
     * The Class must specify the proper access methods
     * (setters/getters) in order for instances of the Class
     * to be properly unmarshalled.
     * @param source the InputSource to read the XML from
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public Object unmarshal(InputSource source)
        throws MarshalException, ValidationException
    {
        Parser parser = Configuration.getParser();

        if (parser == null)
            throw new MarshalException("unable to create parser");

        if (entityResolver != null)
            parser.setEntityResolver(entityResolver);

        UnmarshalHandler handler = createHandler();
        parser.setDocumentHandler(handler);
        parser.setErrorHandler(handler);

        try {
            parser.parse(source);
        }
        catch(java.io.IOException ioe) {
            throw new MarshalException(ioe);
        }
        catch(org.xml.sax.SAXException sx) {
            Exception except = sx.getException();
            if (except == null) except = sx;
            MarshalException marshalEx = new MarshalException(except);
            if ( handler.getDocumentLocator() != null ) {
                FileLocation location = new FileLocation();
                location.setFilename(handler.getDocumentLocator().getSystemId());
                location.setLineNumber(handler.getDocumentLocator().getLineNumber());
                location.setColumnNumber(handler.getDocumentLocator().getColumnNumber());
                marshalEx.setLocation(location);
            }
            throw marshalEx;
        }

        return handler.getObject();
    } //-- unmarshal(InputSource)


    /**
     * Unmarshals Objects of this Unmarshaller's Class type.
     * The Class must specify the proper access methods
     * (setters/getters) in order for instances of the Class
     * to be properly unmarshalled.
     * @param node the DOM node to read the XML from
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public Object unmarshal(Node node)
        throws MarshalException, ValidationException
    {
		return unmarshal(new DOMEventProducer(node));
    } //-- unmarshal(EventProducer)
    
    //-------------------------/
    //- Public Static Methods -/
    //-------------------------/

    
    /**
     * Wraps the given UnmarshalHandler with a SAX 2 ContentHandler.
     * Allows for improved integration with SAX 2 applications
     *
     * @return the ContentHandler
    **/
    public static ContentHandler getContentHandler(UnmarshalHandler handler) 
        throws SAXException
    {
        XMLReaderAdapter adapter = new XMLReaderAdapter();
        adapter.setDocumentHandler(handler);
        return adapter;
    } //-- getContentHandler
    
    /**
     * Unmarshals Objects of the given Class type. The Class must specify
     * the proper access methods (setters/getters) in order for instances
     * of the Class to be properly unmarshalled.
     * @param c the Class to create a new instance of
     * @param reader the Reader to read the XML from
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public static Object unmarshal(Class c, Reader reader)
        throws MarshalException, ValidationException
    {
        Unmarshaller unmarshaller = new Unmarshaller(c);
        return unmarshaller.unmarshal(reader);
    } //-- void unmarshal(Writer)

    /**
     * Unmarshals Objects of the given Class type. The Class must specify
     * the proper access methods (setters/getters) in order for instances
     * of the Class to be properly unmarshalled.
     * @param c the Class to create a new instance of
     * @param source the InputSource to read the XML from
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public static Object unmarshal(Class c, InputSource source)
        throws MarshalException, ValidationException
    {
        Unmarshaller unmarshaller = new Unmarshaller(c);
        return unmarshaller.unmarshal(source);
    } //-- void unmarshal(Writer)

    /**
     * Unmarshals Objects of the given Class type. The Class must specify
     * the proper access methods (setters/getters) in order for instances
     * of the Class to be properly unmarshalled.
     * @param c the Class to create a new instance of
     * @param source the DOM Node to read the XML from
     * @exception MarshalException when there is an error during
     * the unmarshalling process
     * @exception ValidationException when there is a validation error
    **/
    public static Object unmarshal(Class c, Node node)
        throws MarshalException, ValidationException
    {
        Unmarshaller unmarshaller = new Unmarshaller(c);
        return unmarshaller.unmarshal(node);
    } //-- void unmarshal(Writer)


    
} //-- Unmarshaller

