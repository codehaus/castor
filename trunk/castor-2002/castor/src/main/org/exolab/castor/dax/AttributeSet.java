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


package org.exolab.castor.dax;


import java.util.Hashtable;
import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * Holds an arbitrary set of LDAP attributes. LDAP attributes that do
 * not map to a particular object field can still be accessed through
 * this collection of attributes. Each attribute has a name and one or
 * more values.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see Attribute
 */
public class AttributeSet
{


    /**
     * A collection of attributes ({@link Attribute}) accessible by name.
     */
    private Hashtable  _attrs = new Hashtable();


    /**
     * Return an attribute with the specified name, null if no such
     * attribute exists in this set.
     *
     * @param name The attribute's name
     * @return The attribute
     */
    public Attribute getAttribute( String name )
    {
	return (Attribute) _attrs.get( name );
    }


    /**
     * Removes an attribute with the specified name, returning the
     * removed attribute or null if there was no such attribute in
     * this set.
     *
     * @param name The attribute's name
     * @return The old attribute, or null
     */
    public Attribute removeAttribute( String name )
    {
	return (Attribute) _attrs.remove( name );
    }


    /**
     * Adds an attribute to this set. If an attribute with the same
     * name already exists in that set, the old attribute is removed
     * and returned.
     *
     * @param attr The attribute
     * @return The old attribute, or null
     */
    public Attribute setAttribute( Attribute attr )
    {
	return (Attribute) _attrs.put( attr.getName(), attr );
    }


    /**
     * Adds an attribute to this set. If an attribute with the same
     * name already exists in that set, the old attribute is removed
     * and returned. The attribute's values is an array of zero or
     * more string values.
     *
     * @param name The attribute's name
     * @param value The attribute's string values
     * @return The old attribute, or null
     */
    public Attribute setAttribute( String name, String[] values )
    {
	Attribute attr;

	attr = new Attribute( name );
	attr.setValues( values );
	return (Attribute) _attrs.put( name, attr );
    }


    /**
     * Returns an enumeration of all the attributes in this set.
     *
     * @return Enumeration of {@link Attribute}
     */
    public Enumeration listAttributes()
    {
	return _attrs.elements();
    }


    /**
     * Returns an enumeration of the names of all attributes in this set.
     *
     * @return Enumeration of attribute names
     */
    public Enumeration listAttributeNames()
    {
	return _attrs.keys();
    }


    /**
     * Returns a textual presentation of the attribute set.
     */
    public String toString()
    {
	StringBuffer str;
	Enumeration  enum;

	str = new StringBuffer();
	enum = _attrs.elements();
	while ( enum.hasMoreElements() ) {
	    str.append( enum.nextElement().toString() );
	}
	return str.toString();
    }


    public Object clone()
    {
	AttributeSet copy;
	Attribute    attr;
	Enumeration  enum;

	copy = new AttributeSet();
	enum = _attrs.elements();
	while ( enum.hasMoreElements() ) {
	    attr = (Attribute) enum.nextElement();
	    attr = (Attribute) attr.clone();
	    copy._attrs.put( attr.getName(), attr );
	}
	return copy;
    }


}
