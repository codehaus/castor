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


package org.exolab.castor.mapping;


/**
 * Descriptor for a container field. A container field is a complex
 * object consisting of multiple fields, that are treated as an integral
 * part of the object to which the container field belongs. A container
 * field is not used to describe a one to one relation. Generally the
 * container field is used for multi-field identities and grouping.
 * <p>
 * Although a container field extends {@link FieldDesc} and is
 * constructed from one, it is not handled directly. Rather, the
 * fields in the container field (the contained fields) are stored in
 * the class descriptor, and rely on the container field to access
 * their containing object.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class ContainerFieldDesc
    extends FieldDesc
{


    /**
     * The fields contained in this object, each one is described
     * through a {@link ContainedFieldDesc}.
     */
    private FieldDesc[]  _contained;


    public ContainerFieldDesc( FieldDesc fieldDesc, FieldDesc[] contained )
    {
	super( fieldDesc );
	if ( contained == null )
	    throw new IllegalArgumentException( "Argument 'contained' is null" );
	_contained = contained;
    }


    /**
     * Returns all the fields contained in the container field.
     *
     * @return An array of zero of more contained fields
     */
    public FieldDesc[] getContainedFields()
    {
	return (FieldDesc[]) _contained.clone();
    }


    public void copyInto( Object source, Object target )
    {
	source = getValue( source );
	if ( source == null )
	    setValue( target, null );
	else {
	    Object value;

	    value = Types.newInstance( getFieldType() );
	    setValue( target, value );
	    for ( int i = 0 ; i < _contained.length ; ++i )
		_contained[ i ].copyInto( source, value );
	}
    }


    public String canStore( Object obj )
    {
	String reason;

	for ( int i = 0 ; i < _contained.length ; ++i ) {
	    reason = _contained[ i ].canStore( obj );
	    if ( reason != null )
		return reason;
	}
	return null;
    }


    public boolean isModified( Object obj, Object cached )
    {
	for ( int i = 0 ; i < _contained.length ; ++i )
	    if ( _contained[ i ].isModified( obj, cached ) )
		return true;
	return false;
    }
	

}

