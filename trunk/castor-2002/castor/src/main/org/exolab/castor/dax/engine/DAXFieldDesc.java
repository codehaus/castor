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


package org.exolab.castor.dax.engine;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttribute;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.FieldDesc;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DAXFieldDesc
    extends FieldDesc
{


    private String   _ldapName;


    public DAXFieldDesc( FieldDesc fieldDesc, String ldapName )
	throws MappingException
    {
	super( fieldDesc );
	_ldapName = ldapName;
    }


    public String getLdapName()
    {
	return _ldapName;
    }


    public void setValue( Object obj, LDAPEntry entry )
    {
	LDAPAttribute attr;

	attr = entry.getAttribute( _ldapName );
	if ( attr == null )
	    super.setValue( obj, null );
	else  if ( getFieldType() == byte[].class ) {
	    // XXX Not implemented
	} else if ( getFieldType() == String[].class ) {
	    super.setValue( obj, attr.getStringValueArray() );
	} else {
	    // Type conversion comes here
	    String[]  values;

	    values = attr.getStringValueArray();
	    if ( values.length == 0 )
		super.setValue( obj, null );
	    else if ( values.length == 1 )
		super.setValue( obj, values[ 0 ] );
	    else
		super.setValue( obj, values );
	}
    }


    public LDAPAttribute getAttribute( Object obj )
    {
	Object  value;

	value = super.getValue( obj );
	if ( value == null ) {
	    return null;
	} else if ( value instanceof byte[] ) {
	    return new LDAPAttribute( _ldapName, (byte[]) value );
	} else if ( value instanceof String[] ) {
	    return  new LDAPAttribute( _ldapName, (String[]) value );
	} else if ( value instanceof String ) {
	    return new LDAPAttribute( _ldapName, (String) value );
	} else if ( value instanceof char[] ) {
	    return new LDAPAttribute( _ldapName, new String( (char[]) value ) );
	} else {
	    return new LDAPAttribute( _ldapName, value.toString() );
	}
	// XXX Need to deal with Other through serialization
    }


}

