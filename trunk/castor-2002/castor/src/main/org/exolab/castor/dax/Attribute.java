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


import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.exolab.castor.util.MimeBase64Encoder;


/**
 * Holds an arbitrary LDAP attribute and it's set of values. LDAP
 * attributes that do not map to a particular object field can still
 * be accessed through a collection of attributes. Each attribute has
 * a name and one or more values. Values are either all string or all
 * binary (byte arrays).
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see AttributeSet
 */
public class Attribute
{


    /**
     * Multi value separator used for printing out the multi
     * values of an attribute.
     */
    public static final String MultiValueSeparator = ",";


    /**
     * Name value separator used for printing out the attribute's
     * name and value.
     */
    public static final String NameValueSeparator  = ": ";


    /**
     * The string values of the attribute.
     */
    private String[] _string;


    /**
     * The binary values of the attribute.
     */
    private byte[][] _bytes;


    /**
     * The name of the attribute.
     */
    private String   _name;


    /**
     * Construct a new attribute with the specified name.
     *
     * @param name The name of this attribute
     */
    public Attribute( String name )
    {
	if ( name == null || name.length() == 0 )
	    throw new IllegalArgumentException( "Argument 'name' is null or empty string" );
	_name = name;
    }


    /**
     * Returns the name of the attribute.
     *
     * @return The attribute's name
     */
    public String getName()
    {
	return _name;
    }


    /**
     * Returns the string values of the attribute. The return value is
     * an array of zero or more strings.
     *
     * @return An array of zero or more strings
     */
    public String[] getStringValues()
    {
	if ( _string == null )
	    return new String[ 0 ];
	else
	    return (String[]) _string.clone();
    }


    /**
     * Returns the binary values of the attribute. The return value is
     * an array of zero or more arrays of bytes.
     *
     * @return An array of zero or more arrays of bytes
     */
    public byte[][] getByteValues()
    {
	if ( _bytes == null )
	    return new byte[ 0 ][ 0 ];
	else
	    return (byte[][]) _bytes.clone();
    }


    /**
     * Returns the string value of the attribute. If the attribute has
     * one string value, this value is returned. If the attribute has
     * multiple string values, the values are returned separated with
     * a comma. If the attribute has no string values, an empty string
     * is returned.
     *
     * @return The string value of this attribute
     */
    public String getStringValue()
    {
	StringBuffer str;

	if ( _string == null )
	    return new String();
	str = new StringBuffer();
	for ( int i = 0 ; i < _string.length ; ++i ) {
	    if ( i > 0 )
		str.append( MultiValueSeparator );
	    str.append( _string[ i ] );
	}
	return str.toString();
    }


    /**
     * Returns the string values of the attribute as an enumeration.,
     *
     * @return An enumeration of zero or more strings
     */
    public Enumeration listStringValues()
    {
	return new ArrayEnumeration( _string );
    }


    /**
     * Returns the binary values of the attribute as an enumeration.,
     *
     * @return An enumeration of zero or more byte arrays
     */
    public Enumeration listByteValues()
    {
	return new ArrayEnumeration( _bytes );
    }


    /**
     * The number of values for the attribute. Returns the number of
     * string values or binary values that this attribute has. Note:
     * an attribute can have either string or binary values.
     *
     * @return The number of values
     */
    public int size()
    {
	return ( _string == null ? 0 : _string.length ) +
	    ( _bytes == null ? 0 : _bytes.length );
    }


    /**
     * Assigns the string values to the attribute. These are the new
     * values of the attribute replacing any previous values.
     *
     * @param values An array of zero or more string values
     */
    public void setValues( String[] values )
    {
	_string = (String[]) values.clone();
	_bytes = null;
    }


    /**
     * Assigns the binary values to the attribute. These are the new
     * values of the attribute replacing any previous values.
     *
     * @param values An array of zero or more byte arrays
     */
    public void setValues( byte[][] values )
    {
	_string = null;
	_bytes = new byte[ values.length ][];
	for ( int i = 0 ; i < _bytes.length ; ++i ) {
	    _bytes[ i ] = (byte[]) values[ i ].clone();
	}
    }


    /**
     * Returns a textual presentation of the attribute and its values.
     */
    public String toString()
    {
	StringBuffer str;

	str = new StringBuffer();
	if ( _string != null ) {
	    for ( int i = 0 ; i < _string.length ; ++i ) {
		str.append( _name ).append( NameValueSeparator ).append( _string[ i ] ).append( '\n' );
	    }
	} else {
	    for ( int i = 0 ; i < _bytes.length ; ++i ) {
		MimeBase64Encoder encoder;

		encoder = new MimeBase64Encoder();
		encoder.translate( _bytes[ i ] );
		str.append( _name ).append( NameValueSeparator );
		str.append( new String( encoder.getCharArray() ) ).append( '\n' );
	    }
	}
	return str.toString();
    }


    public Object clone()
    {
	Attribute copy;

	copy = new Attribute( getName() );
	if ( _string != null ) {
	    copy._string = (String[]) _string.clone();
	} else if ( _bytes != null ) {
	    copy._bytes = new byte[ _bytes.length ][];
	    for ( int i = 0 ; i < copy._bytes.length ; ++i )
		copy._bytes[ i ] = (byte[]) _bytes[ i ].clone();
	}
	return copy;
    }


    /**
     * An enumeration from an array.
     */
    static class ArrayEnumeration
	implements Enumeration
    {

	private Object[] _array;

	private int      _index;

	ArrayEnumeration( Object[] array )
	{
	    _array = array;
	}

	public boolean hasMoreElements()
	{
	    return ( _array != null && _index < _array.length );
	}

	public Object nextElement()
	{
	    if ( _array == null || _index >= _array.length )
		throw new NoSuchElementException();
	    return _array[ _index ];
	}

    }


}
