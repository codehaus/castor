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

package org.exolab.castor.xml.schema.reader;

//-- imported classes and packages
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.schema.*;
import org.xml.sax.*;

import java.util.Hashtable;

/**
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class SchemaUnmarshaller extends SaxUnmarshaller {
    
    public static final String XSD_NAMESPACE 
        = "http://www.w3.org/1999/XMLSchema";
        
        
      //--------------------/
     //- Member Variables -/
    //--------------------/

    private static final String XMLNS        = "xmlns";
    private static final String XMLNS_PREFIX = "xmlns:";
    
    /**
     * The current SaxUnmarshaller
    **/
    private SaxUnmarshaller unmarshaller;
    
    /**
     * The current branch depth
    **/
    private int depth = 0;
        
    boolean skipAll = false;
            
    /**
     * The ID Resolver
    **/
    Resolver _resolver = null;
    
    Schema _schema = null;
    
    private boolean foundSchemaDef = false;
    
    
    private String defaultNS = null;
    
    private Hashtable namespaces = null;
    
      //----------------/
     //- Constructors -/
    //----------------/

    public SchemaUnmarshaller() {
        this(null, null);
        foundSchemaDef = false;
    } //-- SchemaUnmarshaller
    
    public SchemaUnmarshaller(AttributeList atts, Resolver resolver) {
        super();
        _schema = new Schema();
        setResolver(resolver);        
        foundSchemaDef = true;
        namespaces = new Hashtable();
        init(atts);
    } //-- SchemaUnmarshaller
    
    public Schema getSchema() {
        return _schema;
    }
    
    /**
     * Returns the Object created by this SaxUnmarshaller
     * @return the Object created by this SaxUnmarshaller
    **/
    public Object getObject() {
        return getSchema();
    } //-- getObject
    
    /**
     * Returns the name of the element that this SaxUnmarshaller
     * handles
     * @return the name of the element that this SaxUnmarshaller
     * handles
    **/
    public String elementName() {
        return SchemaNames.SCHEMA;
    } //-- elementName
        
    
    /**
     * initializes the Schema object with the given attribute list
     * @param atts the AttributeList for the schema
    **/
    private void init(AttributeList atts) {
        if (atts == null) return;
        
        String nsURI = atts.getValue(SchemaNames.TARGET_NS_ATTR);
        if ((nsURI != null) && (nsURI.length() > 0))
            _schema.setTargetNamespace(nsURI);
            
    } //-- init
    
    /**
     * Handles namespace attributes
    **/
    private void handleXMLNS(String attName, String attValue) {
        
        if ((attName == null) || (!attName.startsWith(XMLNS))) {
            throw new IllegalArgumentException(attName + 
                " is not a namespace attribute.");
        }
        if ((attValue == null) || (attValue.length() == 0))
            throw new IllegalArgumentException("error null or empty " + 
                "namespace value");
        
        if (attName.equals(XMLNS)) {
            defaultNS = attValue;
            return;
        }
        
        String prefix = attName.substring(XMLNS_PREFIX.length());
        
        //-- register namespace
        namespaces.put(prefix, attValue);

    } //-- handleXMLNS
    
    private void processNamespaces(AttributeList atts) {
        if (atts == null) return;
        //-- loop through atts
        for (int i = 0; i < atts.getLength(); i++) {
            String attName = atts.getName(i);
            if (attName.equals(XMLNS) || attName.startsWith(XMLNS_PREFIX))
                handleXMLNS(attName, atts.getValue(i));
        }
    } //-- processNamespaces
    
    
    public void setResolver(Resolver resolver) {
        if (resolver == null) resolver = new ScopableResolver();
        super.setResolver(resolver);
        _resolver = resolver;
    } //-- setResolver
    
    //-------------------------------------------------/
    //- implementation of org.xml.sax.DocumentHandler -/
    //-------------------------------------------------/
    
    public void startElement(String name, AttributeList atts)
        throws SAXException
    {
        
        if (skipAll) return;
        
        String rawName = name;
        
        //-- handle namespaces
        processNamespaces(atts);
        String namespace = null;
        int idx = name.indexOf(':');
        if (idx >= 0 ) {
            String prefix = name.substring(0,idx);
            name = name.substring(idx+1);
            namespace = (String)namespaces.get(prefix);
        }
        else namespace = defaultNS;
        
        
        //-- backward compatibility, we'll need to 
        //-- remove this at some point
        if ((!foundSchemaDef) && (idx < 0)) {
            if (defaultNS == null) {
                defaultNS = XSD_NAMESPACE;
                namespace = XSD_NAMESPACE;
                System.out.println("No namespace declaration has been " +
                    "found for " + name);
                System.out.print("   * assuming default namespace of ");
                System.out.println(XSD_NAMESPACE);
            }
        }
        //-- end of backward compatibility
        
        //-- check namespace
        if (!XSD_NAMESPACE.equals(namespace)) {
            throw new SAXException(rawName + 
                " has not been declared in the XML Schema namespace");
        }
        
        //-- Do delagation if necessary
        if (unmarshaller != null) {
            unmarshaller.startElement(name, atts);
            ++depth;
            return;
        }
        
        
        
        //-- use VM internal String of name
        name = name.intern();
                
        if (name == SchemaNames.SCHEMA) {
            
            if (foundSchemaDef)
                illegalElement(name);
                
            foundSchemaDef = true;
            init(atts);
            return;
        }
        
        //-- <type>
        if (name == SchemaNames.COMPLEXTYPE) {
            unmarshaller 
                = new ComplextypeUnmarshaller(_schema, atts, _resolver);
        } 
        //-- <element>
        else if (name == SchemaNames.ELEMENT) {
            unmarshaller 
                = new ElementUnmarshaller(_schema, atts, _resolver);
        }
        //-- <simpletype>
        else if (name == SchemaNames.SIMPLETYPE) {
            unmarshaller 
                = new SimpletypeUnmarshaller(_schema, atts, _resolver);
        }
        else {
            //-- we should throw a new Exception here
            //-- but since we don't support everything
            //-- yet, simply add an UnknownDef object
            System.out.print(name);
            System.out.print(" elements are either currently unsupported ");
            System.out.println("or not valid schema elements.");
            unmarshaller = new UnknownUnmarshaller(name);
        }
        
        unmarshaller.setDocumentLocator(getDocumentLocator());
        
    } //-- startElement

    public void endElement(String name) throws SAXException {
                
        if (skipAll) return;
        
        String rawName = name;
        
        //-- handle namespaces
        String namespace = null;
        int idx = name.indexOf(':');
        if (idx >= 0 ) {
            String prefix = name.substring(0,idx);
            name = name.substring(idx+1);
            namespace = (String)namespaces.get(prefix);
        }
        else namespace = defaultNS;
        
        //-- Do delagation if necessary
        if ((unmarshaller != null) && (depth > 0)) {
            unmarshaller.endElement(name);
            --depth;
            return;
        }
        
        
        //-- use internal JVM String
        name = name.intern();        
                
        if (name == SchemaNames.SCHEMA) return;
        
        
        //-- check for name mismatches
        if ((unmarshaller != null)) {
            if (!name.equals(unmarshaller.elementName())) {
                String err = "error: missing end element for ";
                err += unmarshaller.elementName();
                throw new SAXException(err);
            }
        }
        else {
            String err = "error: missing start element for " + name;
            throw new SAXException(err);
        }
        
        //-- call unmarshaller.finish() to perform any necessary cleanup
        unmarshaller.finish();
        
        if (name == SchemaNames.COMPLEXTYPE) {
            Complextype complextype = null;
            complextype = ((ComplextypeUnmarshaller)unmarshaller).getComplextype();
            _schema.addComplextype(complextype);
            if (complextype.getName() != null) {
                _resolver.addResolvable(complextype.getReferenceId(), complextype);
            }
            else {
                System.out.println("warning: top-level complextype with no name.");
            }
        }
        else if (name == SchemaNames.SIMPLETYPE) {
            Simpletype simpletype = null;
            simpletype = ((SimpletypeUnmarshaller)unmarshaller).getSimpletype();
            _schema.addSimpletype(simpletype);
            _resolver.addResolvable(simpletype.getReferenceId(), simpletype);
        }
        else if (name == SchemaNames.ELEMENT) {
            ElementDecl element = null;
            element = ((ElementUnmarshaller)unmarshaller).getElement();
            _schema.addElementDecl(element);
        }
        unmarshaller = null;
    } //-- endElement
    
    public void characters(char[] ch, int start, int length) 
        throws SAXException
    {
        //-- Do delagation if necessary
        if (unmarshaller != null) {
            unmarshaller.characters(ch, start, length);
        }
    } //-- characters
    
} //-- SGDocumentHandler
 
