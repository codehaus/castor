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

import java.util.Hashtable;

/**
 * An XML Schema Datatype
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$ 
**/
public class Datatype extends SchemaBase 
    implements org.exolab.castor.xml.Referable
{

    /**
     * Error message for a null argument
    **/
    private static String NULL_ARGUMENT
        = "A null argument was passed to the constructor of " +
           Datatype.class.getName();
          
          
       
    private String baseType = null;    
    private String name = null;
    
    private Hashtable facets = null;

    /**
     * The owning Schema to which this Datatype belongs
    **/
    private Schema schema = null;
    
    /**
     * Creates a new Datatype with the given name and basetype reference.
     * @param name of the DataType
     * @param schema the Schema to which this Datatype belongs
    **/
    public Datatype(Schema schema, String name) {
        this(schema, name, null);
    } //-- DataType
    
    /**
     * Creates a new Datatype with the given name and basetype reference.
     * @param name of the DataType
     * @param schema the Schema to which this Datatype belongs
    **/
    public Datatype(Schema schema, String name, String baseTypeRef) {
        super();
        if (schema == null) {
            String err = NULL_ARGUMENT + "; 'schema' must not be null.";
            throw new IllegalArgumentException(err);
        }
        if ((name == null) || (name.length() == 0)) {
            String err = NULL_ARGUMENT + 
                "; 'name' must not be null or zero-length.";
            throw new IllegalArgumentException(err);
        }
        
        this.schema = schema;
        this.name = name;
        this.baseType = baseTypeRef;
        facets = new Hashtable();
    } //-- DataType
    
    /**
     * Adds the given Facet to this DataType.
    **/
    public void addFacet(Facet facet) {
        if (facet != null) {
            String name = facet.getName();
            if (name != null) facets.put(name, facet);
        }
    } //-- addFacet
    
    /**
     * Returns the type of this SchemaBase
     * @return the type of this SchemaBase
     * @see org.exolab.xml.schema.SchemaBase
    **/
    public short getDefType() {
        return SchemaBase.DATATYPE;
    } //-- getDefType
    
    /**
     * Returns the facet associated with the given name
     * @return the facet associated with the given name, or
     * null if no facet was found
    **/
    public Facet getFacet(String name) {
        
        if (name == null) return null;
        return (Facet) facets.get(name);
        
    } //-- getFacet 
    
    /**
     * Returns the name of this DataType
     * @return the name of this DataType
    **/
    public String getName() {
        return name;
    } //-- getName
    
    
    public String getBaseTypeRef() {
        return baseType;
    } //-- getBaseTypeRef
    
    /**
     * Returns the Id used to Refer to this Object. 
     * @return the Id used to Refer to this Object
     * @see org.exolab.castor.xml.Referable
    **/
    public String getReferenceId() {
        return "datatype:"+name;
    } //-- getReferenceId
    
    /**
     * Returns the schema to which this Datatype belongs
     * @return the Schema to which this Datatype belongs
    **/
    public Schema getSchema() {
        return schema;
    } //-- getSchema
    
    public void setBaseTypeRef(String baseTypeRef) {
        this.baseType = baseTypeRef;
    } //-- setBaseTypeRef
    
    /**
     * Checks the validity of this Attribute declaration
     * @exception ValidationException when this Attribute declaration
     * is invalid
    **/
    public void validate() throws ValidationException {
        //if (name == null)  {
        //    String err = "<attribute> is missing required 'name' attribute.";
        //    throw new ValidationException(err);
        //}
    } //-- validate
    
} //-- DataType