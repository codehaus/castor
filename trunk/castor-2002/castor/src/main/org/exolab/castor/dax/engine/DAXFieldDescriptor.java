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


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttribute;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.loader.FieldDescriptorImpl;
import org.exolab.castor.util.Serializer;


/**
 * DAX field descriptor. Wraps {@link FieldDesc} and adds LDAP-related
 * information, type conversion, and set/get for LDAP.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DAXFieldDescriptor
    extends FieldDescriptorImpl
{


    private String   _ldapName;


    public DAXFieldDescriptor( FieldDescriptorImpl fieldDesc, String ldapName )
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
            getHandler().setValue( obj, (Object) null );
        else  if ( getFieldType() == byte[].class ) {
            byte[][] values;
            int      count;
            byte[]   bytes;
            
            // Pretty much takes an array of bytes and stuffs it
            // into a single array of bytes
            values = attr.getByteValueArray();
            if ( values.length == 0 )
                getHandler().setValue( obj, (Object) null );
            else if ( values.length == 1 )
                getHandler().setValue( obj, (Object) values[ 0 ] );
            else {
                count = 0;
                for ( int i = 0 ; i < values.length ; ++i )
                    count += values[ i ].length;
                bytes = new byte[ count ];
                count = 0;
                for ( int i = 0 ; i < values.length ; ++i ) {
                    for ( int j = 0 ; j < values.length ; ++j )
                        bytes[ count + j ] = values[ i ][ j ];
                    count += values[ i ].length;
                }
                getHandler().setValue( obj, (Object) values );
            }
        } else if ( getFieldType() == String[].class ) {
            getHandler().setValue( obj, (Object) attr.getStringValueArray() );
        } else {
            // Type conversion comes here
            String[]  values;
            
            values = attr.getStringValueArray();
            if ( values.length == 0 )
                getHandler().setValue( obj, (Object) null );
            else if ( values.length == 1 )
                getHandler().setValue( obj, (Object) values[ 0 ] );
            else
                // Need to assemble all strings together
                getHandler().setValue( obj, (Object) values[ 0 ] );
        }
    }
    
    
    public LDAPAttribute getAttribute( Object obj )
    {
        Object  value;
        
        value = getHandler().getValue( obj );
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
        } else if ( Types.isSimpleType( value.getClass() ) ) {
            // Simple objects are stored as String
            return new LDAPAttribute( _ldapName, value.toString() );
        } else {
            // Complex objects are serialized
            try {
                return new LDAPAttribute( _ldapName, Serializer.serialize( value ) );
            } catch ( IOException except ) {
                throw new IllegalArgumentException( "Cannot serialize field value of type " +
                                                    value.getClass().getName() );
            }
        }
    }


    public String toString()
    {
        return super.toString() + " AS " + _ldapName;
    }


}



