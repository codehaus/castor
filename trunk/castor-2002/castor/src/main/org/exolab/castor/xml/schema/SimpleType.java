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
 * Copyright 1999-2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema;

import org.exolab.castor.xml.*;

import java.util.Enumeration;

/**
 * An XML Schema SimpleType
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$ 
**/
public class SimpleType extends XMLType 
    implements Referable
{

    /**
     * Error message for a null argument
    **/
    private static String NULL_ARGUMENT
        = "A null argument was passed to the constructor of " +
           SimpleType.class.getName();
          
          
       
    /**
     * The base datatype reference
    **/
    private String base = null;    
    
    /**
     * The name for this simpleType
    **/
    private String name = null;
    
    /**
     * The constraining facets of this type
    **/
    private FacetList facets     = null;

    /**
     * The owning Schema to which this Simpletype belongs
    **/
    private Schema schema = null;
    
    /**
     * Creates a new SimpleType with the given name and basetype reference.
     * @param name of the SimpleType
     * @param schema the Schema to which this SimpleType belongs
    **/
    public SimpleType(Schema schema, String name) {
        this(schema, name, null);
    } //-- SimpleType
    
    /**
     * Creates a new SimpleType with the given name and basetype reference.
     * @param name of the SimpleType
     * @param schema the Schema to which this SimpleType belongs
     * @param base the base simpleType which this simpleType inherits from.
     * If the simpleType does not "extend" any other, base may be null.
    **/
    public SimpleType(Schema schema, String name, String base) {
        super();
        if (schema == null) {
            String err = NULL_ARGUMENT + "; 'schema' must not be null.";
            throw new IllegalArgumentException(err);
        }
        
        /* in-line simpleTypes don't need a name
        if ((name == null) || (name.length() == 0)) {
            String err = NULL_ARGUMENT + 
                "; 'name' must not be null or zero-length.";
            throw new IllegalArgumentException(err);
        }
        */
        
        this.schema  = schema;
        this.name    = name;
        this.base  = base;
        this.facets  = new FacetList();
    } //-- SimpleType
    
    /**
     * Adds the given Facet to this Simpletype.
     * @param facet the Facet to add to this Simpletype
    **/
    public void addFacet(Facet facet) {
        
        if (facet == null) return;
        
        String name = facet.getName();
        
        if (name == null) return;
        
        facets.add(facet);
        
    } //-- addFacet
    
    /**
     * Returns the facets associated with the given name
     * @return the facets associated with the given name
    **/
    public Enumeration getFacets(String name) {
        FacetListEnumerator fle = null;
        SimpleType datatype = getBase();
        if (datatype != null) {
            fle = (FacetListEnumerator)datatype.getFacets(name);
        }
        fle = new FacetListEnumerator(facets, fle);
        fle.setMask(name);
        return fle;
    } //-- getFacets 
    
    /**
     * Returns an Enumeration of all the Facets (including inherited)
     * facets for this type.
     * @return an Enumeration of all the Facets for this type
    **/
    public Enumeration getFacets() {
        FacetListEnumerator fle = null;
        SimpleType datatype = getBase();
        if (datatype != null) {
            fle = (FacetListEnumerator)datatype.getFacets();
        }
        fle = new FacetListEnumerator(facets, fle);
        return fle;
    } //-- getFacets
    
    /**
     * Returns the name of this SimpleType
     * @return the name of this SimpleType
    **/
    public String getName() {
        return name;
    } //-- getName
    
    
    /**
     * Returns the base SimpleType that this SimpleType inherits from.
     * If this Simpletype does not inherit from any other, or if
     * reference cannot be resolved this will be null.
     * @return the base SimpleType that this SimpleType inherits from.
    **/
    public SimpleType getBase() {
        if (base == null) return null;
        return this.schema.getSimpleType(base);
    } //-- getBase
    
    /**
     * Returns the name of the base type for this datatype.
     * If this datatype does not inherit from any other, this
     * will be null.
     * @return the name of the base type for this datatype.
    **/
    public String getBaseRef() {
        return base;
    } //-- getBaseRef
    
    /**
     * Returns the Id used to Refer to this Object. 
     * @return the Id used to Refer to this Object
     * @see org.exolab.castor.xml.Referable
    **/
    public String getReferenceId() {
        return "datatype:"+name;
    } //-- getReferenceId
    
    /**
     * Returns the schema to which this Simpletype belongs
     * @return the Schema to which this Simpletype belongs
    **/
    public Schema getSchema() {
        return schema;
    } //-- getSchema
    
    /**
     * Returns true if this Simpletype has a specified Facet
     * with the given name.
     * @param name the name of the Facet to look for
     * @return true if this Simpletype has a specified Facet
     * with the given name
    **/
    public boolean hasFacet(String name) {
        if (name == null) return false;
        for (int i = 0; i < facets.size(); i++) {
            Facet facet = (Facet) facets.get(i);
            if (name.equals(facet.getName())) return true;
        }
        return false;
    } //-- hasFacet
    
    /**
     * Sets the base type for this datatype
     * @param base the base type which this datatype inherits from
    **/
    public void setBaseRef(String base) {
        this.base = base;
    } //-- setBaseTypeRef
    
    //-------------------------------/
    //- Implementation of Structure -/
    //-------------------------------/
    
    /**
     * Returns the type of this Schema Structure
     * @return the type of this Schema Structure
    **/
    public short getStructureType() {
        return Structure.SIMPLE_TYPE;
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
    
} //-- DataType
