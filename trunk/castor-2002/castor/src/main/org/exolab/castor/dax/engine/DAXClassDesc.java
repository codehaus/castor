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


import java.util.Hashtable;
import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.RelationContext;
import org.exolab.castor.persist.PersistenceException;


/**
 * DAX class descriptors. Extends {@link ClassDesc} to include the
 * object class and other LDAP-related information. All fields are of
 * type {@link DAXFieldDesc}, identity field is part of the returned
 * field list, and contained fields are flattened out for efficiency
 * (thus all fields are directly accessible).
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DAXClassDesc
    extends ClassDesc
{


    private FieldDesc  _attributeSet;


    private String     _ldapClass;


    public DAXClassDesc( ClassDesc clsDesc, String ldapClass, FieldDesc attributeSet )
        throws MappingException
    {
        super( clsDesc );

        if ( attributeSet != null ) {
            if ( attributeSet.getFieldType() != Hashtable.class )
                throw new MappingException( "Not attribute set" );
            _attributeSet = attributeSet;
        }
        _ldapClass = ldapClass;
    }


    public FieldDesc getAttributeSetField()
    {
        return _attributeSet;
    }


    public String getLdapClass()
    {
        return _ldapClass;
    }


    /**
     * Mutator method can only be used by {@link DAXMappingHelper}.
     */
    final void setDAXFields( DAXFieldDesc[] fields )
    {
        setFields( fields );
    }


    public void copyInto( Object source, Object target, RelationContext rtx )
        throws PersistenceException
    {
        Hashtable attrSet;
        
        super.copyInto( source, target, rtx );
        if ( _attributeSet != null ) {
            attrSet = (Hashtable) _attributeSet.getValue( source );
            if ( attrSet == null )
                _attributeSet.setValue( target, null );
            else
                _attributeSet.setValue( target, attrSet.clone() );
        }
    } 


    public String toString()
    {
        return super.toString() + " AS " + _ldapClass;
    }


}


