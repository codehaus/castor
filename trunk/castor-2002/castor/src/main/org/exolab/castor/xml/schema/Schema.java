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
    private Hashtable complextypes = null;
    
    
    /**
     * A list of defined simpletypes
    **/
    private Hashtable simpletypes = null;

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
        complextypes = new Hashtable();
        simpletypes  = new Hashtable();
        elements   = new Hashtable();
        this.schemaNS = schemaNS;
        init();
    } //-- ScehamDef
    
    private void init() {
        
        //-- create default built-in types for this Schema
       
        try {
            //-- ID
            addSimpletype(new IDType(this));
            //-- IDREF
            addSimpletype(new IDREFType(this));
            //-- NCName
            addSimpletype(new NCNameType(this));
            //-- NMTOKEN
            addSimpletype(new NMTokenType(this));
            
            //-- binary
            addSimpletype(new BinaryType(this));
            //-- boooean
            addSimpletype(new BooleanType(this));
            //-- double
            addSimpletype(new DoubleType(this));
            //-- integer
            addSimpletype(new IntegerType(this));
            //-- long
            addSimpletype(new LongType(this));
            //-- negative-integer
            addSimpletype(new NegativeIntegerType(this));
            //-- positive-integer
            addSimpletype(new PositiveIntegerType(this));
            //-- string
            addSimpletype(new StringType(this));
            //-- timeInstant
            addSimpletype(new TimeInstantType(this));
        }
        catch (SchemaException sx) {
            //-- will never be thrown here since we
            //-- are not adding invalid simpletypes
        }
    } //-- init
    
    /**
     * Adds the given Complextype definition to this Schema defintion
     * @param complextype the Complextype to add to this Schema
     * @exception SchemaException if the Complextype does not have
     * a name or if another Complextype already exists with the same name
    **/
    public synchronized void addComplextype(Complextype complextype) 
        throws SchemaException 
    {
        
        String name = complextype.getName();
        
        if (name == null) {
            String err = "a global complextype must contain a name.";
            throw new SchemaException(err);
        }
        if (complextype.getSchema() != this) {
            String err = "invalid attempt to add an complextype which ";
            err += "belongs to a different Schema; type name: " + name;
        }
        if (complextypes.get(name) != null) {
            String err = "an complextype already exists with the given name: ";
            throw new SchemaException(err + name);
        }
        complextypes.put(name, complextype);
        
    } //-- addComplextype

    /**
     * Adds the given Simpletype definition to this Schema defintion
     * @param simpletype the Simpletype to add to this Schema
     * @exception SchemaException if the Complextype does not have
     * a name or if another Complextype already exists with the same name
    **/
    public synchronized void addSimpletype(Simpletype simpletype) 
        throws SchemaException 
    {
        
        String name = simpletype.getName();
        
        if (simpletype.getSchema() != this) {
            String err = "invalid attempt to add a simpletype which ";
            err += "belongs to a different Schema; type name: " + name;
        }
        if (simpletypes.get(name) != null) {
            String err = "a simpletype already exists with the given name: ";
            throw new SchemaException(err + name);
        }
        simpletypes.put(name, simpletype);
        
    } //-- addSimpletype

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
     * Creates a new Complextype using this Schema as the owning Schema
     * document. A call to #addComplextype must still be made in order
     * to add the complextype to this Schema.
     * @return the new Complextype
    **/
    public Complextype createComplextype() {
        return new Complextype(this);
    } //-- createComplextype
    
    /**
     * Creates a new Complextype using this Schema as the owning Schema
     * document. A call to #addComplextype must still be made in order
     * to add the complextype to this Schema.
     * @param name the name of the Complextype 
     * @return the new Complextype
    **/
    public Complextype createComplextype(String name) {
        return new Complextype(this, name);
    } //-- createComplextype
    
    /**
     * Creates a new Simpletype using this Schema as the owning Schema
     * document. A call to #addSimpletype must till be made in order
     * to add the Simpletype to this Schema.
     * @param name the name of the Simpletype
     * @return the new Simpletype.
    **/
    public Simpletype createSimpletype(String name) {
        return new Simpletype(this, name);
    } //-- createSimpletype(String)
    
    /**
     * Returns the Complextype of associated with the given name
     * @return the Complextypel of associated with the given name, or
     *  null if no Complextype with the given name was found.
    **/
    public Complextype getComplextype(String name) {
        if (name == null)  {
            String err = NULL_ARGUMENT + "getComplextype: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }
        // [Remm] Strip namespace prefix
        String canonicalName = name;
        int colon = name.indexOf(':');
        if (colon != -1)
            canonicalName = name.substring(colon + 1);
        return (Complextype)complextypes.get(canonicalName);
    } //-- getComplextype
    
    /**
     * Returns an Enumeration of all top-level Complextype declarations
     * @return an Enumeration of all top-level Complextype declarations
    **/
    public Enumeration getComplextypes() {
        return complextypes.elements();
    } //-- getComplextypes
    
    /**
     * Returns the Simpletype associated with the given name,
     * or null if no such Simpletype exists.
     * @return the Simpletype associated with the given name,
     * or null if no such Simpletype exists.
    **/
    public Simpletype getSimpletype(String name) {
        if (name == null)  {
            String err = NULL_ARGUMENT + "getSimpletype: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }
        // [Remm] Strip namespace prefix
        String canonicalName = name;
        int colon = name.indexOf(':');
        if (colon != -1)
            canonicalName = name.substring(colon + 1);
        return (Simpletype)simpletypes.get(canonicalName);
    } //-- getSimpletype
    
    /**
     * Returns an Enumeration of all Simpletype declarations
     * @return an Enumeration of all Simpletype declarations
    **/
    public Enumeration getSimpletypes() {
        return simpletypes.elements();
    } //-- getSimpletypes
    
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
     * Removes the given top level Complextype from this Schema
     * @param complextype the Complextype to remove
     * @return true if the complextype has been removed, or
     * false if the complextype wasn't top level or
     * didn't exist in this Schema
    **/
    public boolean removeComplextype(Complextype complextype) {
        if (complextype.isTopLevel()) {
            if (complextypes.contains(complextype)) {
                complextypes.remove(complextype);
                return true;
            }
        }
        return false;
    } //-- removeComplextype
    
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
    
    
