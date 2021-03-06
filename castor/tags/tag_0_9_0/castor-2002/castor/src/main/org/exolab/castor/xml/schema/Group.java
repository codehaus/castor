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
 * Copyright 1999, 2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema;

import org.exolab.castor.xml.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * An XML Schema Group
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class Group extends Particle
    implements ContentModelGroup, Referable
{


    /**
     * the implementation of ContentModelGroup
    **/
    private ContentModelGroup _contentModel = null;

    /**
     * The name of this Group
    **/
    private String    name       = null;

    /**
     *
    **/
    private boolean   export     = false;

    /**
     * The Compositor for the Group
    **/
    private Order order = Order.seq;

    private String _id  = null;

    /**
     * True if was created for a group tag, false otherwise
     *  (all, choice, sequence)
     *  */
    private boolean _isModelGroupDefinition= false;

    /**
     * Creates a new Group, with no name
    **/
    public Group() {
        this(null);
    } //-- Group

    /**
     * Creates a new Group with the given name
     * @param name of the Group
    **/
    public Group(String name) {
        super();
        this.name  = name;
        _contentModel = new ContentModelGroupImpl();
    } //-- Group


    /**
     * Returns the Model Group Collection type for this Group
     *
     * This should be removed?
     * @return the Collection type for this Model Group
    **
    public Collection getCollection() {
        return this.collection;
    } //-- getCollection

    /**
     * Returns the ID for this Group
     * @return the ID for this Group, or null if no ID is present
    **/
    public String getId() {
        return _id;
    } //-- getId

    /**
     * Returns the name of this Group, or null if no name was defined.
     * @return the name of this Group, or null if no name was defined
    **/
    public String getName() {
        return name;
    } //-- getName

    /**
     * Returns the compositor for this Group
     * @return the compositor for this Group
    **/
    public Order getOrder() {

        //-- Return proper compositor...
        //-- according to XML Schema spec 20000407 section 4.3.5

        //-- Note: it's important not to simply call
        //-- #getParticleCount or #getParticle because those
        //-- methods also perform some trickery
        if (_contentModel.getParticleCount() == 1) {
            Particle particle = _contentModel.getParticle(0);
            if (particle.getStructureType() == Structure.GROUP) {
                if ((getMinOccurs() == 1) && (getMaxOccurs() == 1))
                    return ((Group)particle).getOrder();
            }
        }
        return this.order;
    } //-- getOrder


    /**
     * Sets if the group is a model group definition
     */
    public void setIsModelGroupDefinition(boolean isModelGroupDefinition) {
        _isModelGroupDefinition= isModelGroupDefinition;
    }


    /**
     * Tells if the group is a model group definition
     * @return true if the group is a model group definition (<group> tag), false
     * otherwise (<all>, <choice>, or <sequence> tags.
     */
    public boolean isModelGroupDefinition() {
        return _isModelGroupDefinition;
    }


    /**
     * Returns the Id used to Refer to this Object
     * @return the Id used to Refer to this Object
     * @see Referable
    **/
    public String getReferenceId() {
        if (name != null) return "group:"+name;
        return null;
    } //-- getReferenceId

    /**
     * Sets the name of this Group
     * @param name the new name for this Group
    **/
    public void setName(String name) {
        this.name = name;
    } //--setName

    /**
     * Sets the ID for this Group
     * @param id the ID for this Group
    **/
    public void setId(String id) {
        _id = id;
    } //-- setId

    /**
     * Sets the Order option for this Group
     * @param order the type of order that this group is restricted to
    **/
    public void setOrder(Order order) {
        if (order == null) this.order = Order.all;
        else this.order = order;
    } //-- setOrder

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
     * Adds the given Group to this ContentModelGroup
     * @param group the Group to add
     * @exception SchemaException when a group with the same name as the
     * specified group already exists in the current scope
    **/
    public void addGroup(Group group)
        throws SchemaException
    {
        _contentModel.addGroup(group);
    } //-- addGroup

    /**
     * Returns an enumeration of all the Particles of this
     * ContentModelGroup
     *
     * @return an enumeration of the Particles contained
     * within this ContentModelGroup
    **/
    public Enumeration enumerate() {
        //-- Some trickery to properly handle
        //-- XML Schema spec 20000407 section 4.3.5
        if (_contentModel.getParticleCount() == 1) {
            Particle particle = _contentModel.getParticle(0);
            if (particle.getStructureType() == Structure.GROUP) {
                if ((getMinOccurs() == 1) && (getMaxOccurs() == 1))
                    return ((Group)particle).enumerate();
            }
        }
        return _contentModel.enumerate();
    } //-- enumerate

    /**
     * Returns the Particle at the specified index
     * @param index the index of the particle to return
     * @returns the CMParticle at the specified index
    **/
    public Particle getParticle(int index) {
        //-- Some trickery to properly handle
        //-- XML Schema spec 20000407 section 4.3.5
        if (_contentModel.getParticleCount() == 1) {
            Particle particle = _contentModel.getParticle(0);
            if (particle.getStructureType() == Structure.GROUP) {
                if ((getMinOccurs() == 1) && (getMaxOccurs() == 1))
                    return ((Group)particle).getParticle(index);
            }
        }
        return _contentModel.getParticle(index);
    } //-- getParticle

    /**
     * Returns the number of particles contained within
     * this ContentModelGroup
     *
     * @return the number of particles
    **/
    public int getParticleCount() {
        //-- Some trickery to properly handle
        //-- XML Schema spec 20000407 section 4.3.5
        if (_contentModel.getParticleCount() == 1) {
            Particle particle = _contentModel.getParticle(0);
            if (particle.getStructureType() == Structure.GROUP) {
                if ((getMinOccurs() == 1) && (getMaxOccurs() == 1))
                    return ((Group)particle).getParticleCount();
            }
        }
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
        return Structure.GROUP;
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

} //-- Group