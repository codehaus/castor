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


import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPModification;
import netscape.ldap.LDAPModificationSet;
import netscape.ldap.util.DN;
import netscape.ldap.util.RDN;
import org.exolab.castor.dax.desc.FieldDesc;
import org.exolab.castor.dax.desc.DNFieldDesc;
import org.exolab.castor.dax.desc.ObjectDesc;
import org.exolab.castor.dax.Attribute;
import org.exolab.castor.dax.AttributeSet;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class Engine
{


    private ObjectDesc  _objDesc;


    private Hashtable   _fields;


    private DNFieldDesc _dnField;


    private FieldDesc   _attrField;


    public Engine( ObjectDesc objDesc )
    {
	FieldDesc[] fields;

	_objDesc = objDesc;
	_attrField = _objDesc.getAttributeField();
	fields = objDesc.getFieldDescs();
	_fields = new Hashtable();
	for ( int i = 0 ; i < fields.length ; ++i ) {
	    _fields.put( fields[ i ].getLdapName(), fields[ i ] );
	}
	_dnField = _objDesc.getDNFieldDesc();
    }


    public Object read( LDAPConnection conn, String dn )
        throws LDAPException
    {
	LDAPAttributeSet ldapSet;
	LDAPAttribute    ldapAttr;
	LDAPEntry        entry;
	Object           obj;
	FieldDesc        field;
	Enumeration      enum;

	try {
	    entry = conn.read( dn );
	    if ( entry == null )
		return null;
	} catch ( LDAPException except ) {
	    if ( except.getLDAPResultCode() == LDAPException.NO_SUCH_OBJECT ) {
		return null;
	    }
	    throw except;
	}

	obj = _objDesc.createNew();
	ldapSet = entry.getAttributeSet();
	for ( int i = 0 ; i < ldapSet.size() ; ++i ) {
	    ldapAttr = ldapSet.elementAt( i );
	    if ( ldapAttr.getName().equals( "objectclass" ) ) {

		String[] classes;
		boolean  match;

		/*
		classes = ldapAttr.getStringValueArray();
		match = false;
		for ( int j = 0 ; j < classes.length ; ++j ) {
		    if ( classes[ j ].equals( _objDesc.getLdapClass() ) ) {
			match = true;
			break;
		    }
		}
		if ( ! match ) {
		    throw new IllegalStateException( "LDAP entry does not match object class " +
						     _objDesc.getLdapClass() );
		}
		*/

	    } else {

		field = (FieldDesc) _fields.get( ldapAttr.getName() );
		if ( field != null ) {

		    StringBuffer value;
		    String[]     values;
		    Vector       vector;
		    
		    values = ldapAttr.getStringValueArray();
		    if ( field.getObjectClass().isArray() ) {
			field.setValue( obj, values );
		    } else if ( field.getObjectClass() == Vector.class ) {
			vector = new Vector();
			for ( int j = 0 ; j < values.length ; ++j ) {
			    vector.addElement( values[ j ] );
			}
			field.setValue( obj, vector );
		    } else {
			value = new StringBuffer();
			for ( int j = 0 ; j < values.length ; ++j ) {
			    if ( j > 0 )
				value.append( Attribute.MultiValueSeparator );
			    value.append( values[ j ] );
			}
			field.setValue( obj, value.toString() );
		    }

		} else {

		    AttributeSet     attrSet;
		    
		    if ( _attrField != null ) {
			attrSet = (AttributeSet) _attrField.getValue( obj );
			if ( attrSet == null ) {
			    attrSet = new AttributeSet();
			    _attrField.setValue( obj, attrSet );
			}
			attrSet.setAttribute( ldapAttr.getName(), ldapAttr.getStringValueArray() );
		    }

		}
	    }
	}
	_dnField.setDN( obj, entry.getDN() );
	return obj;
    }


    public void delete( LDAPConnection conn, String dn )
	throws LDAPException
    {
	conn.delete( dn );
    }


    public void update( LDAPConnection conn, Object obj )
	throws LDAPException
    {
	LDAPModificationSet modifs;
	String              dn;
	Enumeration         enum;
	FieldDesc           field;
	LDAPEntry           entry;
	LDAPAttributeSet    ldapSet;
	boolean             exists;

	if ( obj.getClass() != _objDesc.getObjectClass() )
	    throw new IllegalArgumentException( "Argument 'obj' not of type " + _objDesc.getObjectClass() );
	dn = _dnField.getDN( obj );
	try {
	    entry = conn.read( dn );
	    if ( entry == null ) {
		// create( conn, obj );
		return;
	    }
	} catch ( LDAPException except ) {
	    if ( except.getLDAPResultCode() == LDAPException.NO_SUCH_OBJECT ) {
		// create( conn, obj );
		return;
	    }
	    throw except;
	}
	ldapSet = entry.getAttributeSet();

	modifs = new LDAPModificationSet();
	enum = _fields.elements();
	while ( enum.hasMoreElements() ) {
	    field = (FieldDesc) enum.nextElement();
	    exists = ( ldapSet.getAttribute( field.getLdapName() ) != null );
	    if ( exists )
		ldapSet.remove( field.getLdapName() );
	    if ( field.getObjectClass().isArray() ) {
		String[] values;

		values = (String[]) field.getValue( obj );
		if ( values == null ) {
		    if ( exists )
			modifs.add( LDAPModification.DELETE,
				    new LDAPAttribute( field.getLdapName() ) );
		} else {
		    modifs.add( ( exists ? LDAPModification.REPLACE : LDAPModification.ADD ),
				new LDAPAttribute( field.getLdapName(), values ) );
		}
	    } else {
		String value;

		exists = ( ldapSet.getAttribute( field.getLdapName() ) != null );
		value = (String) field.getValue( obj );
		if ( value == null ) {
		    if ( exists )
			modifs.add( LDAPModification.DELETE,
				    new LDAPAttribute( field.getLdapName() ) );
		} else {
		    modifs.add( ( exists ? LDAPModification.REPLACE : LDAPModification.ADD ),
				new LDAPAttribute( field.getLdapName(), value ) );
		}
	    }
	}

	if ( _attrField != null ) {
	    AttributeSet attrSet;
	    Enumeration  attrs;
	    Attribute    attr;
	    
	    attrSet = (AttributeSet) _attrField.getValue( obj );
	    attrs = attrSet.listAttributes();
	    while ( attrs.hasMoreElements() ) {
		attr = (Attribute) attrs.nextElement();
		exists = ( ldapSet.getAttribute( attr.getName() ) != null );
		if ( exists )
		    ldapSet.remove( attr.getName() );
		modifs.add( ( exists ? LDAPModification.REPLACE : LDAPModification.ADD ),
			    new LDAPAttribute( attr.getName(), attr.getStringValues() ) );
	    }
	}

	enum = ldapSet.getAttributes();
	while ( enum.hasMoreElements() ) {
	    LDAPAttribute ldapAttr;

	    ldapAttr = (LDAPAttribute) enum.nextElement();
	    if ( ! ldapAttr.getName().equals( "objectclass" ) ) {
		modifs.add( LDAPModification.DELETE, ldapAttr );
	    }
	}
	conn.modify( dn, modifs );
    }


    public void create( LDAPConnection conn, Object obj )
	throws LDAPException
    {
	LDAPAttributeSet ldapSet;
	String           dn;
	Enumeration      enum;
	FieldDesc        field;
	boolean          exists;

	if ( obj.getClass() != _objDesc.getObjectClass() )
	    throw new IllegalArgumentException( "Argument 'obj' not of type " + _objDesc.getObjectClass() );
	dn = _dnField.getDN( obj );

	ldapSet = new LDAPAttributeSet();
	enum = _fields.elements();
	while ( enum.hasMoreElements() ) {
	    field = (FieldDesc) enum.nextElement();
	    if ( field.getObjectClass().isArray() ) {
		String[] values;

		values = (String[]) field.getValue( obj );
		if ( values != null ) {
		    ldapSet.add( new LDAPAttribute( field.getLdapName(), values ) );
		}
	    } else if ( field.getObjectClass() == AttributeSet.class ) {
		AttributeSet attrSet;
		Enumeration  attrs;
		Attribute    attr;

		attrSet = (AttributeSet) field.getValue( obj );
		attrs = attrSet.listAttributes();
		while ( attrs.hasMoreElements() ) {
		    attr = (Attribute) attrs.nextElement();
		    ldapSet.add( new LDAPAttribute( attr.getName(), attr.getStringValues() ) );
		}
	    } else {
		String value;

		value = (String) field.getValue( obj );
		if ( value != null ) {
		    ldapSet.add( new LDAPAttribute( field.getLdapName(), value ) );
		}
	    }
	}
	ObjectDesc type;

	type = _objDesc;
	while ( type != null ) {
	    ldapSet.add( new LDAPAttribute( "objectclass", type.getLdapClasses() ) );
	    type = type.getExtends();
	}

	conn.add( new LDAPEntry( dn, ldapSet ) );
    }


}

