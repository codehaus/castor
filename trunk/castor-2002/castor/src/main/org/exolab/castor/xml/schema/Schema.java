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

package org.exolab.castor.xml.schema;

import org.exolab.castor.xml.*;
import org.exolab.castor.xml.schema.types.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * An XML Schema Definition. This class also contains the Factory methods for
 * creating Top-Level structures.
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class Schema extends Structure {
    
    public static final String DEFAULT_SCHEMA_NS
        = "http://www.w3.org/TR/1999/09/24-xmlschema";
        
        
    private static final String NULL_ARGUMENT
        = "A null argument was passed to " + 
           Schema.class.getName() + "#";
           
    private String name     = null;
    private String schemaNS = null;
    private String targetNS = null;
    
    
    /**
     * A list of defined architypes
    **/
    private Hashtable complexTypes = null;
    
    
    /**
     * A list of defined SimpleTypes
    **/
    private Hashtable simpleTypes = null;

    /**
     * A list of defined elements
    **/
    private Hashtable elements = null;
    
    /**
     * Creates a new SchemaDef
    **/
    public Schema() {
        this(DEFAULT_SCHEMA_NS);
    } //-- ScehamDef

    
    /**
     * Creates a new SchemaDef
    **/
    public Schema(String schemaNS) {
        super();
        complexTypes = new Hashtable();
        simpleTypes  = new Hashtable();
        elements   = new Hashtable();
        this.schemaNS = schemaNS;
        init();
    } //-- ScehamDef
    
    private void init() {
        
        //-- create default built-in types for this Schema
       
        try {
            //-- ID
            addSimpleType(new IDType(this));
            //-- IDREF
            addSimpleType(new IDREFType(this));
            //-- NCName
            addSimpleType(new NCNameType(this));
            //-- NMTOKEN
            addSimpleType(new NMTokenType(this));
            
            //-- binary
            addSimpleType(new BinaryType(this));
            //-- boooean
            addSimpleType(new BooleanType(this));
            //-- double
            addSimpleType(new DoubleType(this));
            //-- integer
            addSimpleType(new IntegerType(this));
            //-- long
            addSimpleType(new LongType(this));
            //-- negative-integer
            addSimpleType(new NegativeIntegerType(this));
            //-- positive-integer
            addSimpleType(new PositiveIntegerType(this));
            //-- string
            addSimpleType(new StringType(this));
            //-- timeInstant
            addSimpleType(new TimeInstantType(this));
        }
        catch (SchemaException sx) {
            //-- will never be thrown here since we
            //-- are not adding invalid SimpleTypes
        }
    } //-- init
    
    /**
     * Adds the given Complextype definition to this Schema defintion
     * @param complextype the Complextype to add to this Schema
     * @exception SchemaException if the Complextype does not have
     * a name or if another Complextype already exists with the same name
    **/
    public synchronized void addComplexType(ComplexType complexType) 
        throws SchemaException 
    {
        
        String name = complexType.getName();
        
        if (name == null) {
            String err = "a global ComplexType must contain a name.";
            throw new SchemaException(err);
        }
        if (complexType.getSchema() != this) {
            String err = "invalid attempt to add an ComplexType which ";
            err += "belongs to a different Schema; type name: " + name;
        }
        if (complexTypes.get(name) != null) {
            String err = "a ComplexType already exists with the given name: ";
            throw new SchemaException(err + name);
        }
        complexTypes.put(name, complexType);
        
    } //-- addComplextype

    /**
     * Adds the given SimpletType definition to this Schema defintion
     * @param simpletype the SimpleType to add to this Schema
     * @exception SchemaException if the ComplexType does not have
     * a name or if another ComplexType already exists with the same name
    **/
    public synchronized void addSimpleType(SimpleType simpleType) 
        throws SchemaException 
    {
        
        String name = simpleType.getName();
        
        if (simpleType.getSchema() != this) {
            String err = "invalid attempt to add a SimpleType which ";
            err += "belongs to a different Schema; type name: " + name;
        }
        if (simpleTypes.get(name) != null) {
            String err = "a SimpleType already exists with the given name: ";
            throw new SchemaException(err + name);
        }
        simpleTypes.put(name, simpleType);
        
    } //-- addSimpleType

    /**
     * Adds the given Element declaration to this Schema defintion
     * @param elementDecl the ElementDecl to add to this SchemaDef
     * @exception SchemaException when an ElementDecl already
     * exists with the same name as the given ElementDecl
    **/
    public void addElementDecl(ElementDecl elementDecl) 
        throws SchemaException 
    {
        
        String name = elementDecl.getName();
        
        if (name == null) {
            String err = "an element declaration must contain a name.";
            throw new SchemaException(err);
        }
        if (elements.get(name) != null) {
            String err = "an element declaration already exists with the given name: ";
            throw new SchemaException(err + name);
        }
        
        elements.put(name, elementDecl);
        
        
    } //-- addElementDecl
    
    
    /**
     * Creates a new ComplexType using this Schema as the owning Schema
     * document. A call to #addComplexType must still be made in order
     * to add the complexType to this Schema.
     * @return the new ComplexType
    **/
    public ComplexType createComplexType() {
        return new ComplexType(this);
    } //-- createComplexType
    
    /**
     * Creates a new ComplexType using this Schema as the owning Schema
     * document. A call to #addComplexType must still be made in order
     * to add the complexType to this Schema.
     * @param name the name of the ComplexType 
     * @return the new ComplexType
    **/
    public ComplexType createComplexType(String name) {
        return new ComplexType(this, name);
    } //-- createComplexType
    
    /**
     * Creates a new SimpleType using this Schema as the owning Schema
     * document. A call to #addSimpleType must till be made in order
     * to add the SimpleType to this Schema.
     * @param name the name of the SimpleType
     * @return the new SimpleType.
    **/
    public SimpleType createSimpleType(String name) {
        return new SimpleType(this, name);
    } //-- createSimpleType(String)
    
    /**
     * Returns the ComplexType of associated with the given name
     * @return the ComplexType of associated with the given name, or
     *  null if no ComplexType with the given name was found.
    **/
    public ComplexType getComplexType(String name) {
        if (name == null)  {
            String err = NULL_ARGUMENT + "getComplexType: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }
        // [Remm] Strip namespace prefix
        String canonicalName = name;
        int colon = name.indexOf(':');
        if (colon != -1)
            canonicalName = name.substring(colon + 1);
        return (ComplexType)complexTypes.get(canonicalName);
    } //-- getComplexType
    
    /**
     * Returns an Enumeration of all top-level ComplexType declarations
     * @return an Enumeration of all top-level ComplexType declarations
    **/
    public Enumeration getComplexTypes() {
        return complexTypes.elements();
    } //-- getComplextypes
    
    /**
     * Returns the SimpleType associated with the given name,
     * or null if no such SimpleType exists.
     * @return the SimpleType associated with the given name,
     * or null if no such SimpleType exists.
    **/
    public SimpleType getSimpleType(String name) {
        if (name == null)  {
            String err = NULL_ARGUMENT + "getSimpleType: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }
        // [Remm] Strip namespace prefix
        String canonicalName = name;
        int colon = name.indexOf(':');
        if (colon != -1)
            canonicalName = name.substring(colon + 1);
        return (SimpleType)simpleTypes.get(canonicalName);
    } //-- getSimpleType
    
    /**
     * Returns an Enumeration of all SimpleType declarations
     * @return an Enumeration of all SimpleType declarations
    **/
    public Enumeration getSimpleTypes() {
        return simpleTypes.elements();
    } //-- getSimpleTypes
    
    /**
     * Returns the ElementDecl of associated with the given name
     * @return the ElementDecl of associated with the given name, or
     *  null if no ElementDecl with the given name was found.
    **/
    public ElementDecl getElementDecl(String name) {
        return (ElementDecl)elements.get(name);
    } //-- getElementDecl

    /**
     * Returns an Enumeration of all top-level element declarations
     * @return an Enumeration of all top-level element declarations
    **/
    public Enumeration getElementDecls() {
        return elements.elements();
    } //-- getElementDecls
    
    /**
     * Returns the target namespace for this Schema, or null if no
     * namespace has been defined.
     * @return the target namespace for this Schema, or null if no
     * namespace has been defined
    **/
    public String getTargetNamespace() {
        return this.targetNS;
    } //-- getTargetNamespace
    
    /**
     * Removes the given top level ComplexType from this Schema
     * @param complexType the ComplexType to remove
     * @return true if the complexType has been removed, or
     * false if the complexType wasn't top level or
     * didn't exist in this Schema
    **/
    public boolean removeComplexType(ComplexType complexType) {
        if (complexType.isTopLevel()) {
            if (complexTypes.contains(complexType)) {
                complexTypes.remove(complexType);
                return true;
            }
        }
        return false;
    } //-- removeComplexType
    
    /**
     * Sets the name of this Schema definition
    **/
    public void setName(String name) {
        this.name = name;
    } //-- setName
    
    /**
     * Sets the target namespace for this Schema
     * @param targetNamespace the target namespace for this Schema
     * @see <B>&sect; 2.7 XML Schema Part 1: Structures</B>
    **/
    public void setTargetNamespace(String targetNamespace) {
        this.targetNS = targetNamespace;
    } //-- setTargetNamespace

    //-------------------------------/
    //- Implementation of Structure -/
    //-------------------------------/
    
    /**
     * Returns the type of this Schema Structure
     * @return the type of this Schema Structure
    **/
    public short getStructureType() {
        return Structure.SCHEMA;
    } //-- getStructureType
    
    /**
     * Checks the validity of this Schema defintion.
     * @exception ValidationException when this Schema definition
     * is invalid.
    **/
    public void validate()
        throws ValidationException
    {
        //-- do nothing
    } //-- validate
    
} //-- SchemaDef
    
    
