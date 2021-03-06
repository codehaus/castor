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
 * Copyright 1999-2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema;

import org.exolab.castor.xml.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * The XML Schema ComplexType class
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class ComplexType extends XMLType
   implements ContentModelGroup, Referable
{


    /**
     * The abstract flag for this ComplexType
    **/
    private boolean _abstract = false;

    /**
     * A wildcard that represents an <anyAttribute> element if any.
     * Only one <anyAttribute> can appear inside the global scope of
     * a complexType
    **/
    private Wildcard _anyAttribute = null;

    /**
     * The attribute declarations for this ComplexType.
    **/
    private AttributeGroupDecl _attributes = null;

	/**
     * The base type used in <restriction> or <extension>
    **/
    private String _baseType = null;

    /**
     * The value of the 'block' property for this ComplexType. This
     * value may be null.
    **/
    private BlockList _block = null;

	/**
	 * a flag set to true if this complexType is a complexContent
	 */
	private boolean _complexContent = false;

    /**
     * The content type ("mixed", "elemOnly") for this ComplexType.
    **/
    private ContentType _content  = ContentType.elemOnly;

    /**
     * The ContentModel for this ComplexType
    **/
    private ContentModelGroup _contentModel = null;

    /**
     * The final property for this ComplexType. This value may be null.
    **/
    private FinalList _final = null;

    /**
     * The unique id for this ComplexType (optional)
    **/
    private String _id = null;

    /**
     * The parent structure for this ComplexType
     * (either an ElementDecl or a Schema)
    **/
    private Structure _parent = null;

    /**
	 * a flag set to true if this complexType is a restriction
	 */
	private boolean _restricted = false;

    /**
	 * a flag set to true if this complexType is a simpleContent
	 */
	private boolean _simpleContent = false;





    //------------------/
    //- Constructor(s) -/
    //------------------/

    /**
     * Creates a new Complextype, with no name
     * @param schema the owning Schema document
    **/
    public ComplexType(Schema schema) {
        this(schema,null);
    } //-- Complextype

    /**
     * Creates a new Complextype with the given name
     * @param schema the owning Schema
     * @param name of the Complextype
    **/
    public ComplexType(Schema schema, String name) {
        super();
        if (schema == null) {
            String err = NULL_ARGUMENT + "; 'schema' must not be null.";
            throw new IllegalArgumentException(err);
        }

        setSchema(schema);
        setName(name);
        _attributes = new AttributeGroupDecl(schema);
        _contentModel = new ContentModelGroupImpl();
    } //-- Complextype

    /**
     * Adds the given AttributeDecl to this ComplexType
     *
     * @param attrDecl the AttributeDecl to add to this ComplexType
     * @exception SchemaException when an AttributeDecl already
     * exists with the same name as the given AttributeDecl
    **/
    public void addAttributeDecl(AttributeDecl attrDecl)
        throws SchemaException
    {
        _attributes.addAttribute(attrDecl);
    } //-- addAttributeDecl

    /**
     * Removes the given AttributeDecl from this ComplexType
     * @param attrDecl the AttributeDecl to remove.
     */
     public void removeAttributeDecl(AttributeDecl attrDecl) {
        _attributes.removeAttribute(attrDecl);
     }

    /**
     * Adds the given AttributeGroupReference to this ComplexType
     *
     * @param attrGroupRef the AttributeGroupReference to add to this
     * ComplexType
    **/
    public void addAttributeGroupReference
        (AttributeGroupReference attrGroupRef)
    {
        _attributes.addReference(attrGroupRef);
    } //-- addAttributeGroupReference


    /**
     * Removes the given AttributeGroupReference from this ComplexType
     * @param attrGroupRef the AttributeGroupReference to remove.
     */
     public void removeAttributeGroupReference(AttributeGroupReference attrGroupRef) {
        _attributes.removeReference(attrGroupRef);
     }

    /**
     * Creates an AttributeDecl with the given name. The attribute
     * declaration will still need to be added to this Complextype,
     * or another archetype in the same schema, by making a call
     * to #addAttributeDecl
     * @param name the name of the attribute
     * @return the new AttributeDecl
    **/
    public AttributeDecl createAttributeDecl(String name) {
        return new AttributeDecl(getSchema(), name);
    } //-- createAttributeDecl

    /**
     * Returns the wilcard used in this complexType (can be null)
     * @return the wilcard used in this complexType (can be null)
     */
    public Wildcard getAnyAttribute() {
        return _anyAttribute;
    }

    /**
     * Returns the AttributeDecl associated with the given name
     * @return the AttributeDecl associated with the given name, or
     *  null if no AttributeDecl with the given name was found.
    **/
    public AttributeDecl getAttributeDecl(String name) {
        return _attributes.getAttribute(name);
    } //-- getAttributeDecl

    /**
     * Returns an Enumeration of all the AttributeDecl objects
     * declared within this Complextype
     * @return an Enumeration of all the AttributeDecl objects
     * declared within this Complextype
    **/
    public Enumeration getAttributeDecls() {
        return _attributes.getAttributes();
    } //-- getAttributeDecls

    /**
     * Returns the base type that this type inherits from.
     * @return the parent type.
    **/
    public XMLType getBaseType() {
        if ( (_baseType != null) && (super.getBaseType() == null) ) {
            setBaseType( getSchema().getType(_baseType) );
        }
        return super.getBaseType();
    } //-- getBaseType

	/**
	 * Returns the value of the 'block' attribute for this element
	 *
     * @return the value of the 'block' attribute for this element
	 */
	public BlockList getBlock() {
		return _block;
	} //-- getBlock

    /**
     * Returns the content type of this ComplexType
     * @return the content type of this ComplexType
    **/
    public ContentType getContentType() {
        return _content;
    } //-- getContentType

    /**
     * Returns the list of values for the final property for this
     * ComplexType, or null if no final values have been set.
     *
     * @return the FinalList for this ComplexType
    **/
    public FinalList getFinal() {
        return _final;
    } //-- getFinal

    /**
     * Returns the Id for this ComplexType, or null if no Id has been set.
     *
     * @return the Id for this ComplexType, or null if no Id has been set.
    **/
    public String getId() {
        return _id;
    } //-- getId

    /**
     * Returns the parent of this ComplexType, this value may be null if
     * no parent has been set.
     *
     * @return the parent Structure of this ComplexType.
    **/
    public Structure getParent() {
        return _parent;
    } //-- getParent

    /**
     * Returns the Id used to Refer to this Object
     * @return the Id used to Refer to this Object
     * @see Referable
    **/
    public String getReferenceId() {
        return "archetype:"+getName();
    } //-- getReferenceId


    /**
     * Returns true if this ComplexType has been marked as Abstract.
     *
     * @return true if this ComplexType is "abstract".
    **/
    public boolean isAbstract() {
        return _abstract;
    } //-- isAbstract

    /**
     * Returns true if this is a top level Complextype
     * @return true if this is a top level Complextype
    **/
    public boolean isTopLevel() {
        if (getName() == null) return false;
        if (getSchema() == null) return false;
        return (getSchema().getComplexType(getName()) == this);
    } //-- isTopLevel

	/**
	 * Returns true if this complexType is a 'complexContent'
	 * @returns true if this complexType is a 'complexContent'
	 */
	public boolean isComplexContent() {
	       return _complexContent;
	}

	/**
	 * Returns true if this complexType is a 'simpleContent'
	 * @returns true if this complexType is a 'simpleContent'
	 */
	public boolean isSimpleContent() {
	       return _simpleContent;
	}

	/**
	 * Returns true if this complexType is a restriction
	 * @returns true if this complexType is a restriction
	 */
	public boolean isRestricted() {
	       return _restricted;
	}

    /**
     * Sets whether or not this ComplexType should be abstract.
     *
     * @param isAbstract a boolean that when true makes this ComplexType
     * abstract.
    **/
    public void setAbstract(boolean isAbstract) {
        _abstract = isAbstract;
    } //-- setAbstract

    /**
     * Sets the wildcard (anyAttribute) of the complexType
     * @exception SchemaException thrown when a wildcard as already be set
     * or when the wildCard is not an <anyAttribute>.
     */
     public void setAnyAttribute(Wildcard wildcard)
            throws SchemaException
     {
        if (wildcard != null) {
           if (_anyAttribute != null) {
              String err = "<anyAttribute> already set in this complexType: "
                           + this.getName();
              throw new SchemaException(err);
           }

           if (!wildcard.isAttributeWildcard()){
              String err = "In complexType, "+this.getName()
                            +"the wildcard must be an <anyAttribute>";
               throw new SchemaException(err);
           }
        }
        _anyAttribute = wildcard;

     }


    /**
     * Removes the given Wildcard from this Group.
     * @param wilcard the Wildcard to remove.
     * @return true if the wildcard has been successfully removed, false otherwise.
     */
     public boolean removeWildcard(Wildcard wildcard) {
         if (wildcard == null)
            return false;
         if (wildcard.equals(_anyAttribute)) {
             _anyAttribute = null;
             return true;
         }
         return false;

     }

     public void addWildcard(Wildcard wildcard)
         throws SchemaException
     {
        setAnyAttribute(wildcard);
     }


    /**
     * Sets the base type that this type is derived from
     * @param base the type that this type is derived from
    **/
    public void setBase(String base) {
        _baseType = base;
    } //-- setBase

	/**
	 * Sets the value of the 'block' attribute for this ComplexType.
	 *
	 * @param block the value of the block attribute for this
	 * ComplexType definition.
	**/
	public void setBlock(BlockList block) {

	    if (block != null) {
	        if (block.hasSubstitution()) {
	            String err = "'substitution' is an illegal value of the "+
	                "'block' attribute for a complexType definition.";
	            throw new IllegalArgumentException(err);
	        }
	    }
	    _block = block;
	} //-- setBlock

	/**
	 * Sets the value of the 'block' attribute for this ComplexType.
	 *
	 * @param block the value of the block attribute for this
	 * ComplexType definition.
	**/
	public void setBlock(String block) {
	    if (block == null)
	        _block = null;
	    else {
	        setBlock(new BlockList(block));
	    }
	} //-- setBlock

	/**
	 * Sets whether or not this complexType is a 'complexContent'
	 * @param complexContent true if this complexType is a 'complexContent'
	 */
	public void setComplexContent(boolean complexContent) {
	       this._complexContent = complexContent;
	}

	/**
     * Sets the content type of this archetype
     * @param contentType the ContentType for this archetype
    **/
    public void setContentType(ContentType contentType)
    {
        _content = contentType;
    } //-- setContentType

	/**
	 * Sets the value of the 'final' attribute for this ComplexType
	 * definition.
	 *
	 * @param finalList the value of the final attribute for this
	 * ComplexType definition.
	**/
	public void setFinal(FinalList finalList) {
	        _final = finalList;
	} //-- setFinal

	/**
	 * Sets the value of the 'final' attribute for this ComplexType
	 * definition.
	 *
	 * @param finalValue the value of the final attribute for this
	 * ComplexType definition.
	**/
	public void setFinal(String finalValue) {
	    if (finalValue == null)
	        _final = null;
	    else
	        _final = new FinalList(finalValue);
	} //-- setFinal

    /**
     * Sets the Id for this ComplexType. The Id must be globally unique
     * within the Schema. Use a null value to remove the Id.
     *
     * @param id the unique Id for this ComplexType
    **/
    public void setId(String id) {
        _id = id;
    } //-- setId

	/**
	 * Sets whether or not this complexType is a 'simpleContent'
	 * @param complexContent true if this complexType is a 'simpleContent'
	 */
	public void setSimpleContent(boolean simpleContent) {
	       this._simpleContent = simpleContent;
	}

	/**
	 * Sets whether or not this complexType is a restriction
	 * @param complexContent true if this complexType is a restriction
	 */
	public void setRestriction(boolean restricted) {
	       this._restricted = restricted;
	}


    public void useResolver(Resolver resolver) {
        // do nothing for now
    }

    //---------------------------------------/
    //- Implementation of ContentModelGroup -/
    //---------------------------------------/

    /**
     * Adds the given ElementDecl to this ContentModelGroup
     * @param elementDecl the ElementDecl to add
     * @exception SchemaException when an ElementDecl already
     * exists with the same name as the given ElementDecl
    **/
    public void addElementDecl(ElementDecl elementDecl)
        throws SchemaException
    {
        _contentModel.addElementDecl(elementDecl);
    } //-- addElementDecl

    /**
     * Removes the given ElementDecl from this ContentModelGroup.
     * @param elementDecl the ElementDecl to remove.
     * @return true if the element has been successfully removed, false otherwise.
     */
     public boolean removeElementDecl(ElementDecl element) {
         return _contentModel.removeElementDecl(element);
     }



    /**
     * Adds the given Group to this ContentModelGroup
     * @param group the Group to add
     * @exception SchemaException when a group with the same name as the
     * specified group already exists in the current scope
    **/
    public void addGroup(Group group)
        throws SchemaException
    {
        _contentModel.addGroup(group);

        //-- set reference to parent
        group.setParent(this);
    } //-- addGroup


    /**
     * Removes the given Group from this ContentModelGroup.
     * @param group the Group to remove.
     * @return true if the group has been successfully removed, false otherwise.
     */
     public boolean removeGroup(Group group){
        boolean result = _contentModel.removeGroup(group);
        group.setParent(null);
        return result;
     }

    /**
     * Adds the given ModelGroup Definition to this ContentModelGroup
     * @param group the ModelGroup to add
     * @exception SchemaException when a group with the same name as the
     * specified group already exists in the current scope
    **/
    public void addGroup(ModelGroup group)
        throws SchemaException
    {
        _contentModel.addGroup(group);

        //-- set reference to parent
        group.setParent(this);
    } //-- addGroup


    /**
     * Removes the given ModelGroup Definition from this ContentModelGroup.
     * @param group the ModelGroup Definition to remove.
     * @return true if the group has been successfully removed, false otherwise.
     */
     public boolean removeGroup(ModelGroup group) {
         boolean result = _contentModel.removeGroup(group);
         group.setParent(null);
         return result;
     }

    /**
     * Returns an enumeration of all the Particles of this
     * ContentModelGroup
     *
     * @return an enumeration of the Particles contained
     * within this ContentModelGroup
    **/
    public Enumeration enumerate() {
        return _contentModel.enumerate();
    } //-- enumerate

    /**
     * Returns the element declaration with the given name, or null if no
     * element declaration with that name exists in this ContentModelGroup.
     *
     * @param name the name of the element.
     * @return the ElementDecl with the given name, or null if no
     * ElementDecl exists in this ContentModelGroup.
    **/
    public ElementDecl getElementDecl(String name) {
        return _contentModel.getElementDecl(name);
    } //-- getElementDecl

    /**
     * Returns the maximum number of occurances that this ContentModelGroup
     * may appear
     * @return the maximum number of occurances that this ContentModelGroup
     * may appear.
     * A non positive (n < 1) value indicates that the
     * value is unspecified (ie. unbounded).
    **/
    public int getMaxOccurs() {

        if (_contentModel.getParticleCount() > 0) {
            Particle particle = _contentModel.getParticle(0);
            if (particle instanceof ContentModelGroup) {
                return particle.getMaxOccurs();
            }
        }

        return _contentModel.getMaxOccurs();
    } //-- getMaxOccurs

    /**
     * Returns the minimum number of occurances that this ContentModelGroup
     * must appear
     * @return the minimum number of occurances that this ContentModelGroup
     * must appear
     * A negative (n < 0) value indicates that the value is unspecified.
    **/
    public int getMinOccurs() {
        if (_contentModel.getParticleCount() > 0) {
            Particle particle = _contentModel.getParticle(0);
            if (particle instanceof ContentModelGroup) {
                return particle.getMinOccurs();
            }
        }
        return _contentModel.getMinOccurs();
    } //-- getMinOccurs

    /**
     * Returns the Particle at the specified index
     * @param index the index of the particle to return
     * @returns the CMParticle at the specified index
    **/
    public Particle getParticle(int index) {
        return _contentModel.getParticle(index);
    } //-- getParticle

    /**
     * Returns the number of particles contained within
     * this ContentModelGroup
     *
     * @return the number of particles
    **/
    public int getParticleCount() {
        return _contentModel.getParticleCount();
    } //-- getParticleCount





    //-------------------------------/
    //- Implementation of Structure -/
    //-------------------------------/

    /**
     * Returns the type of this Schema Structure
     * @return the type of this Schema Structure
    **/
    public short getStructureType() {
        return Structure.COMPLEX_TYPE;
    } //-- getStructureType

    //---------------------/
    //- Protected Methods -/
    //---------------------/

    /**
     * Sets the parent for this ComplexType
     *
     * @param parent the parent Structure for this ComplexType
    **/
    protected void setParent(Structure parent) {
        if (parent != null) {
            switch (parent.getStructureType()) {
                case Structure.SCHEMA:
                case Structure.ELEMENT:
                    break;
                default:
                    String error = "Invalid parent for ComplexType";
                    throw new IllegalArgumentException(error);
            }
        }
        _parent = parent;
    } //-- setParent


} //-- Complextype
