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


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class Attribute
{


    public static final String MultiValueSeparator = ",";
    public static final String NameValueSeparator  = ": ";


    private String[] _string;


    private byte[][] _bytes;


    private String   _name;


    public Attribute( String name )
    {
	_name = name;
    }


    public String getName()
    {
	return _name;
    }


    public String[] getStringValues()
    {
	if ( _string == null )
	    return new String[ 0 ];
	else
	    return (String[]) _string.clone();
    }


    public byte[][] getByteValues()
    {
	if ( _bytes == null )
	    return new byte[ 0 ][ 0 ];
	else
	    return (byte[][]) _bytes.clone();
    }


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


    public Enumeration listStringValues()
    {
	return new ArrayEnumeration( _string );
    }


    public Enumeration listByteValues()
    {
	return new ArrayEnumeration( _bytes );
    }


    public void setValues( String[] values )
    {
	_string = (String[]) values.clone();
	_bytes = null;
    }


    public void setValues( byte[][] values )
    {
	_string = null;
	_bytes = (byte[][]) values.clone();
    }


    public int size()
    {
	return ( _string == null ? 0 : _string.length ) +
	    ( _bytes == null ? 0 : _bytes.length );
    }


    public String toString()
    {
	StringBuffer str;

	str = new StringBuffer();
	if ( _string != null ) {
	    for ( int i = 0 ; i < _string.length ; ++i ) {
		str.append( _name ).append( NameValueSeparator ).append( _string[ i ] ).append( '\n' );
	    }
	} else {
	    str.append( _name ).append( NameValueSeparator );
	}
	return str.toString();
    }


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
