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


import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.MappingException;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDOClassDesc
    extends ClassDesc
{


    private boolean _dirtyCheck;


    private String  _tableName;


    /**
     * Constructs a new descriptor for the specified object and target
     * mapping. The object type and target name are mandatory. When
     * describing inheritence, the descriptor of the parent object may
     * be used and only the fields added in this object must be
     * supplied here.
     * 
     * @param objType The Java type of this object
     * @param targetName The target name of this object
     * @param fields The fields described for this object.
     * @param extend The descriptor of the object which this object extends,
     * or null if this is a top-level object
     * @throws MappingException The extended descriptor does not match
     *   a parent class of this object type
     */
    public JDOClassDesc( Class objType, String tableName, JDOFieldDesc[] fields,
			  JDOClassDesc extend )
	throws MappingException
    {
	super( objType, fields, null, extend );
	for ( int i = 0 ; i < fields.length ; ++i ) {
	    if ( fields[ i ].isDirtyCheck() )
		_dirtyCheck = true;
	}
	_tableName = tableName;
    }


    public JDOClassDesc( Class objType, String tableName, JDOFieldDesc[] fields,
			  PrimaryKeyDesc primKey, JDOFieldDesc keyField,
			  JDOClassDesc extend, RelationDesc[] related )
	throws MappingException
    {
	super( objType, fields, keyField, extend );
	for ( int i = 0 ; i < fields.length ; ++i ) {
	    if ( fields[ i ].isDirtyCheck() )
		_dirtyCheck = true;
	}
	_primKey = primKey;
	_related = ( related == null ? new RelationDesc[ 0 ] : related );
	_tableName = tableName;
    }


    protected JDOClassDesc( JDOClassDesc source, FieldDesc parentField,
			     FieldDesc parentRefField, String foreRef )
	throws MappingException
    {
	this( source.getObjectType(), source.getSQLName(),
	      toContained( source.getJDOFields(), parentField, parentRefField ),
	      source.getPrimaryKey(), new JDOContainedFieldDesc( source.getPrimaryKeyField(), parentField, parentRefField ),
	      (JDOClassDesc) source.getExtends(), source.getRelated() );
	
	JDOFieldDesc[] descs;
	descs = source.getPrimaryKey().getJDOFields();
	if ( descs != null ) {
             for ( int i = 0 ; i < descs.length ; ++i )
                descs[ i ] = new JDOContainedFieldDesc( descs[ i ], parentField, parentRefField );
            _primKey = new PrimaryKeyDesc( source.getPrimaryKey().getPrimaryKeyType(), descs );
        } else {
            _primKey = source.getPrimaryKey();
        }
	for ( int i = 0 ; i < _fields.length ; ++i ) {
	    if ( ( (JDOFieldDesc) _fields[ i ] ).isDirtyCheck() )
		_dirtyCheck = true;
	}
	_related = source.getRelated();
	_tableName = source._tableName;
	//_foreRef = foreRef;
    }


    private static JDOFieldDesc[] toContained( JDOFieldDesc[] fields, FieldDesc parentField,
					       FieldDesc parentRefField )
	throws MappingException
    {
	for ( int i = 0 ; i < fields.length ; ++i ) {
	    fields[ i ] = new JDOContainedFieldDesc( fields[ i ], parentField, parentRefField );
	}
	return fields;

    }


    public boolean isDirtyCheck()
    {
	return _dirtyCheck;
    }


    /**
     * Returns the fields described for this object. An array of field
     * descriptors is returned, allowing the set/get methods to be
     * called on each field against this object. The returned array
     * may be of size zero.
     *
     * @return The fields described for this object
     */
    public JDOFieldDesc[] getJDOFields()
    {
	FieldDesc[]    fields = getFields();
	JDOFieldDesc[] jdos = new JDOFieldDesc[ fields.length ];
	for ( int i = 0 ; i < fields.length ; ++i )
	    jdos[ i ] = (JDOFieldDesc) fields[ i ];
	return jdos;
    }


    private PrimaryKeyDesc _primKey;
    private RelationDesc[] _related;

    public RelationDesc[] getRelated()
    {
	return (RelationDesc[]) _related.clone();
    }

    public PrimaryKeyDesc getPrimaryKey()
    {
	return _primKey;
    }

    public JDOFieldDesc getPrimaryKeyField()
    {
	return (JDOFieldDesc) getIdentityField();
    }

    public String getSQLName()
    {
	return _tableName;
    }

    public String getSQLName( String colName )
    {
	return _tableName + "." + colName;
    }

    public String getSQLName( JDOFieldDesc field )
    {
	return _tableName + "." + field.getSQLName();
    }


    public void copyInto( Object source, Object target )
    {
	super.copyInto( source, target );
        for ( int i = 0 ; i < _related.length ; ++i ) {
            if ( _related[ i ].getParentField().getValue( source ) == null ) {
                Object relTarget;
 
                relTarget = _related[ i ].getParentField().getValue( target );
                if ( relTarget != null ) {
                    _related[ i ].getPrimaryKeyField().setValue( relTarget, null );
                }
                _related[ i ].getParentField().setValue( target, null );
            } else {
                _related[ i ].copyInto( source, target );
                if ( _related[ i ].getRelationType() == Relation.OneToOne ) {
                    _related[ i ].getPrimaryKeyField().setValue( target, target );
                }
            }
        }
    } 


}


