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


package org.exolab.castor.jdo.engine;


import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.Types;


/**
 * A contained field descriptor. A contained field is a field in
 * object X, where object X is contained in object Y. When object Y
 * is persisted, the contained field is persisted through object Y
 * directly, by being listed as a field of object Y. Contained fields
 * are used to flatten out one-one relations.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDOContainedFieldDesc
    extends JDOFieldDesc
{


    /**
     * The field in the parent object that should be used to
     * access this object.
     */
    protected FieldDesc   _parentField;


    /**
     * Constructs a new contained field for the specified field.
     *
     * @param fieldDesc The field descriptor
     * @param parentField The field in the parent object
     * @throws MappingException The container object is not constructable
     */
    public JDOContainedFieldDesc( JDOFieldDesc fieldDesc, FieldDesc parentField )
        throws MappingException
    {
        super( fieldDesc );
        if ( parentField == null )
            throw new IllegalArgumentException( "Argument 'parentField' is null" );
        if ( ! Types.isConstructable( parentField.getFieldType() ) )
            throw new MappingException( "mapping.classNotConstructable",
                                        parentField.getFieldType().getName() );
        _parentField = parentField;
    }


    /**
     * Returns the field in the parent object which holds the
     * container object for this field.
     *
     * @return The parent field
     */
    FieldDesc getParentField()
    {
        return _parentField;
    }


    public Object getValue( Object obj )
    {
        // This is always called with an instance of the parent
        // object, so we have to obtain the contained object
        // first before retrieving the field from it.
        obj = _parentField.getValue( obj );
        if ( obj == null )
            return null;
        return super.getValue( obj );
    }
    
    
    public void getValue( Object obj, PreparedStatement stmt, int column )
        throws SQLException
    {
        stmt.setObject( column, getValue( obj ) );
    }


    public void setValue( Object obj, Object value )
    {
        Object self;
        
        // This is always called with an instance of the parent
        // object, so we have to obtain the contained object
        // first before retrieving the field from it.
        self = _parentField.getValue( obj );
        if ( self == null ) {
            // If the contained object does not exist, it is
            // created at this point.
            self = Types.newInstance( _parentField.getFieldType() );
            _parentField.setValue( obj, self );
        }
        super.setValue( self, value );
    }
    
    
    public void setValue( Object obj, ResultSet rs, int column )
        throws SQLException
    {
        setValue( obj, rs.getObject( column ) );
    }
    
    
    public String toString()
    {
        return super.toString() + " on " + _parentField.getFieldName();
    }


}

