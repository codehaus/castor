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
 * An class descriptor is used to describe the mapping between a Java
 * class and a target type (XML element, SQL table, LDAP namespace,
 * etc). The class descriptor uses field descriptors to describe the
 * mapping of each field and to provide access to them.
 * <p>
 * Engines will extend this class to provide additional functionality.
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class RelationDesc
{


    private ClassDesc      _relClsDesc;


    private FieldDesc      _relField;


    private boolean        _attached;


    private boolean        _many;


    public RelationDesc( ClassDesc relClsDesc, FieldDesc relField, boolean attached )
    {
        if ( relClsDesc == null )
            throw new IllegalArgumentException( "Argument 'relClsDesc' is null" );
        if ( relField == null )
            throw new IllegalArgumentException( "Argument 'relField' is null" );
        _relClsDesc = relClsDesc;
        _relField = relField;
        _attached = attached;
    }


    public ClassDesc getRelatedClassDesc()
    {
        return _relClsDesc;
    }


    public FieldDesc getRelationField()
    {
        return _relField;
    }


    public boolean isAttached()
    {
        return _attached;
    }


    public boolean isMany()
    {
        return _many;
    }


    /**
     * Determines if the object can be stored. Returns successfully if
     * the object can be stored. Throws an exception if relation is
     * broken.
     *
     * @param obj The object
     * @throws IntegrityException Cannot store object due to
     *  integrity violation
     */
    public void canStore( Object obj )
        throws IntegrityException
    {
        Object rel;

        // Check if the relation field is not null and required.
        rel = _relField.getValue( obj );
        if ( rel == null && _relField.isRequired() )
            throw new IntegrityException( "mapping.requiredField",
                                          obj.getClass().getName(), _relField.getFieldName() );
        if ( rel != null ) {
            if ( _attached ) {
                // Attached relation -- related object must point back
                // to parent using its identity fields
                if ( _relClsDesc.getIdentity().getValue( rel ) != obj )
                    throw new IntegrityException( "mapping.relNotReciprocal",
                                                  obj.getClass().getName(), _relField.getFieldName() );
            }
        }
    }


    /**
     * Determines if the object has been modified from its original
     * cached value. Returns true if the object has been modified.
     *
     * @param obj The object
     * @param cached The cached copy
     * @return True if the object has been modified
     */
    public boolean isModified( Object obj, Object cached )
    {
        Object oRel;
        Object cRel;

        oRel = _relField.getValue( obj );
        cRel = _relField.getValue( cached );
        // If both are the same (or both null) return true.
        if ( oRel == cRel )
            return false;
        // If both are not null, compare the related objects.
        if ( oRel != null && cRel != null )
            return _relClsDesc.isModified( oRel, cRel );
        // One is null, the other is not.
        return false;
    }
    
    
    public String toString()
    {
        return "Relation " + _relField.getDeclaringClass().getName() +
            " to " + _relClsDesc.getJavaClass().getName();
    }


}


