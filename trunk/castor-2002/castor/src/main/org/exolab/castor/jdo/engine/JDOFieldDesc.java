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
import org.exolab.castor.mapping.TypeConvertor;


/**
 * JDO field descriptor. Wraps {@link FieldDesc} and adds SQL-related
 * information, type conversion, and set/get for JDBC.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDOFieldDesc
    extends FieldDesc
{


    /**
     * True if the field requires dirty checking.
     */
    private boolean        _dirtyCheck;


    /**
     * Convertor from Java type to SQL type.
     */
    private TypeConvertor  _javaToSql;


    /**
     * Convertor from SQL type to Java type.
     */
    private TypeConvertor  _sqlToJava;


    /**
     * The SQL name of the field.
     */
    private String         _sqlName;


    /**
     * Construct a new field descriptor for the specified field. This is
     * a JDO field descriptor wrapping a field descriptor and adding JDO
     * related properties and methods.
     *
     * @param fieldDesc The field descriptor
     * @throws MappingException Invalid mapping information
     */
    public JDOFieldDesc( FieldDesc fieldDesc, String sqlName, Class sqlType, boolean dirtyCheck )
        throws MappingException
    {
        super( fieldDesc );

        if ( sqlName == null )
            throw new IllegalArgumentException( "Argument 'sqlName' is null" );
        _sqlName = sqlName;
        if ( sqlType == null )
            throw new IllegalArgumentException( "Argument 'sqlType' is null" );
        if ( getFieldType() != sqlType ) {
            _javaToSql = Types.getConvertor( getFieldType(), sqlType );
            _sqlToJava = Types.getConvertor( sqlType, getFieldType() );
        }
        _dirtyCheck = _dirtyCheck;
    }


    /**
     * Constructor used by derived classes.
     */
    protected JDOFieldDesc( JDOFieldDesc fieldDesc )
    {
        super( fieldDesc );
        _sqlName = fieldDesc._sqlName;
        _dirtyCheck = fieldDesc._dirtyCheck;
        _javaToSql = fieldDesc._javaToSql;
        _sqlToJava = fieldDesc._sqlToJava;
    }


    /**
     * Returns the SQL name of this field.
     *
     * @return The SQL name of this field
     */
    public String getSQLName()
    {
        return _sqlName;
    }


    /**
     * Returns true if dirty checking is required for this field.
     *
     * @return True if dirty checking required
     */
    public boolean isDirtyCheck()
    {
        return _dirtyCheck;
    }


    /**
     * Obtains the value of the field and places it directly in the
     * prepared statement.
     *
     * @param obj The object
     * @param stmt A prepared statement with parameters
     * @param index The parameter index
     * @throws SQLException An exception occured attempting to set
     *  the field in the prepared statement
     */
    public void getValue( Object obj, PreparedStatement stmt, int index )
        throws SQLException
    {
        if ( _javaToSql == null )
            stmt.setObject( index, super.getValue( obj ) );
        else
            stmt.setObject( index, _javaToSql.convert( super.getValue( obj ) ) );
    }
    

    /**
     * Sets the value of the field from the result set.
     *
     * @param obj The object
     * @param rs The result set
     * @param index The field in the result set
     * @throws SQLException An exception occured attempting to get
     *  the field from the result set
     */
    public void setValue( Object obj, ResultSet rs, int index )
        throws SQLException
    {
        if ( _sqlToJava == null )
            super.setValue( obj, rs.getObject( index ) );
        else
            super.setValue( obj, _sqlToJava.convert( rs.getObject( index ) ) );
    }
    
    
    public Object getValue( Object obj )
    {
        if ( _javaToSql == null )
            return super.getValue( obj );
        else
            return _javaToSql.convert( super.getValue( obj ) );
    }
    
    
    public void setValue( Object obj, Object value )
    {
        if ( _sqlToJava == null )
            super.setValue( obj, value );
        else
            super.setValue( obj, _sqlToJava.convert( value ) );
    }
    
    
    public String toString()
    {
        return super.toString() + " AS " + _sqlName;
    }


}

