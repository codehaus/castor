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


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.FieldDesc;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDOFieldDesc
    extends FieldDesc
{


    private boolean        _dirtyCheck;


    private TypeConvertor  _convertorTo;


    private TypeConvertor  _convertorFrom;


    private String         _sqlName;


    public JDOFieldDesc( Field field, String sqlName, Class sqlType, boolean dirtyCheck )
	throws MappingException
    {
	super( field, true, true, true );
	if ( sqlType != null && getFieldType() != sqlType ) {
	    _convertorTo = SQLTypes.getConvertor( getFieldType(), sqlType );
	    _convertorFrom = SQLTypes.getConvertor( sqlType, getFieldType() );
	}
	_sqlName = sqlName;
    }


    public JDOFieldDesc( String fieldName, Class fieldType, Method getMethod, Method setMethod,
			 String sqlName, Class sqlType, boolean dirtyCheck )
	throws MappingException
    {
	super( fieldName, fieldType, getMethod, setMethod, true, true, true );
	if ( sqlType != null && getFieldType() != sqlType ) {
	    _convertorTo = SQLTypes.getConvertor( getFieldType(), sqlType );
	    _convertorFrom = SQLTypes.getConvertor( sqlType, getFieldType() );
	}
	_dirtyCheck = dirtyCheck;
	_sqlName = sqlName;
    }


    protected JDOFieldDesc( JDOFieldDesc desc )
    {
	super( desc );
	_convertorTo = desc._convertorTo;
	_convertorFrom = desc._convertorFrom;
	_dirtyCheck = desc._dirtyCheck;
	_sqlName = desc._sqlName;
    }


    public String getSQLName()
    {
	return _sqlName;
    }


    public boolean isDirtyCheck()
    {
	return _dirtyCheck;
    }


    public void getValue( Object obj, PreparedStatement stmt, int column )
	throws SQLException
    {
	if ( _convertorTo == null )
	    stmt.setObject( column, super.getValue( obj ) );
	else
	    stmt.setObject( column, _convertorTo.convert( super.getValue( obj ) ) );
    }
    

    public void setValue( Object obj, ResultSet rs, int column )
	throws SQLException
    {
	if ( _convertorFrom == null )
	    super.setValue( obj, rs.getObject( column ) );
	else {
	    super.setValue( obj, _convertorFrom.convert( rs.getObject( column ) ) );
	}
    }


    public Object getValue( Object obj )
    {
	if ( _convertorTo == null )
	    return super.getValue( obj );
	else
	    return _convertorTo.convert( super.getValue( obj ) );
    }


    public void setValue( Object obj, Object value )
    {
	if ( _convertorFrom == null )
	    super.setValue( obj, value );
	else
	    super.setValue( obj, _convertorFrom.convert( value ) );
    }


}

