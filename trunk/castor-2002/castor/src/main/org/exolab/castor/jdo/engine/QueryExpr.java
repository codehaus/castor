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


import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class QueryExpr
{

    static class Syntax
    {
        static final char TableSeparator = ',';
        static final char TableColumnSeparator = '.';
        static final char ColumnSeparator = ',';
        static final String Parameter = "?";
        static final String OpAnd = " AND ";
        static final String OpEqual = "=";
        static final String Select = "SELECT ";
        static final String From = " FROM ";
        static final String Where = " WHERE ";
        static final String SelectLock = " FOR UPDATE";
        static final String TableLock = " HOLDLOCK";
    }


    private Hashtable _tables = new Hashtable();


    private Vector    _cols = new Vector();


    private Vector    _relCols = new Vector();


    private Vector    _conds = new Vector();


    private Vector    _joins = new Vector();


    public void addTable( String name )
    {
        _tables.put( name, name );
    }

    
    public void addTable( JDOClassDesc clsDesc )
    {
        addTable( clsDesc.getTableName() );
    }


    public void addColumn( String tableName, String columnName )
    {
        _cols.addElement( tableName + Syntax.TableColumnSeparator + columnName  );
    }


    public void addColumn( JDOClassDesc clsDesc, JDOFieldDesc field )
    {
        addColumn( clsDesc.getTableName(), field.getSQLName() );
    }


    public void addRelationColumn( String tableName, String columnName )
    {
        _relCols.addElement( tableName + Syntax.TableColumnSeparator + columnName  );
    }


    public void addRelationColumn( JDOClassDesc clsDesc, JDOFieldDesc field )
    {
        addRelationColumn( clsDesc.getTableName(), field.getSQLName() );
    }


    public void addParameter( String tableName, String columnName, String op )
    {
        addCondition( tableName, columnName, op, Syntax.Parameter );
    }


    public void addParameter( JDOClassDesc clsDesc, JDOFieldDesc field, String op )
    {
        addCondition( clsDesc.getTableName(), field.getSQLName(), op, Syntax.Parameter );
    }


    public void addCondition( String tableName, String columnName,
                              String op, String value )
    {
        if ( op == null )
            op = Syntax.OpEqual;
        _conds.addElement( tableName + Syntax.TableColumnSeparator + columnName +
                           op + value );
    }


    public void addCondition( JDOClassDesc clsDesc, JDOFieldDesc field,
                              String op, String value )
    {
        addCondition( clsDesc.getTableName(), field.getSQLName(), op, value );
    }


    public void addJoin( String leftTable, String leftColumn,
                         String rightTable, String rightColumn )
    {
        _joins.addElement( new Join( leftTable, leftColumn, rightTable, rightColumn ) );
    }


    public void addJoin( JDOClassDesc leftTable, JDOFieldDesc leftColumn,
                         JDOClassDesc rightTable, JDOFieldDesc rightColumn )
    {
        _joins.addElement( new Join( leftTable.getTableName(), leftColumn.getSQLName(),
                                     rightTable.getTableName(), rightColumn.getSQLName() ) );
    }


    public void addJoin( String leftTable, String[] leftColumns,
                         String rightTable, String[] rightColumns )
    {
        _joins.addElement( new Join( leftTable, leftColumns, rightTable, rightColumns ) );
    }


    public void addJoin( JDOClassDesc leftTable, JDOFieldDesc[] leftColumns,
                         JDOClassDesc rightTable, JDOFieldDesc[] rightColumns )
    {
        String[] left;
        String[] right;

        left = new String[ leftColumns.length ];
        for ( int i = 0 ; i < leftColumns.length ; ++i )
            left[ i ] = leftColumns[ i ].getSQLName();
        right = new String[ rightColumns.length ];
        for ( int i = 0 ; i < rightColumns.length ; ++i )
            right[ i ] = rightColumns[ i ].getSQLName();
        _joins.addElement( new Join( leftTable.getTableName(), left,
                                     rightTable.getTableName(), right ) );
    }


    protected String getTableList()
    {
        Enumeration  enum;
        StringBuffer sql;

        sql = new StringBuffer();
        enum = _tables.elements();
        while ( enum.hasMoreElements() ) {
            sql.append( (String) enum.nextElement() );
            if ( enum.hasMoreElements() )
                sql.append( Syntax.TableSeparator );
        }
        return sql.toString();
    }


    protected String getColumnList()
    {
        StringBuffer sql;

        sql = new StringBuffer();
        for ( int i = 0 ; i < _cols.size() ; ++i ) {
            if ( i > 0 )
                sql.append( Syntax.ColumnSeparator );
            sql.append( (String) _cols.elementAt( i ) );
        }
        if ( _cols.size() > 0 && _relCols.size() > 0 )
            sql.append( Syntax.ColumnSeparator );
        for ( int i = 0 ; i < _relCols.size() ; ++i ) {
            if ( i > 0 )
                sql.append( Syntax.ColumnSeparator );
            sql.append( (String) _relCols.elementAt( i ) );
        }
        return sql.toString();
    }


    protected boolean hasColumns()
    {
        return ( _cols.size() + _relCols.size() > 0 );
    }


    protected String getConditionList()
    {
        Enumeration  enum;
        StringBuffer sql;

        sql = new StringBuffer();
        enum = _conds.elements();
        while ( enum.hasMoreElements() ) {
            sql.append( (String) enum.nextElement() );
            if ( enum.hasMoreElements() )
                sql.append( Syntax.OpAnd );
        }
        return sql.toString();
    }


    protected boolean hasConditions()
    {
        return ( _conds.size() > 0 );
    }


    public String getQuery( boolean lock )
    {
        StringBuffer sql;
        boolean      first;

        sql = new StringBuffer();
        sql.append( Syntax.Select );
        if ( ! hasColumns() )
            ;
        sql.append( getColumnList() );
        sql.append( Syntax.From );
        sql.append( getTableList() );

        first = true;
        if ( hasConditions() ) {
            if ( first ) {
                sql.append( Syntax.Where );
                first = false;
            } else
                sql.append( Syntax.OpAnd );
            sql.append( getConditionList() );
        }
        for ( int i = 0 ; i < _joins.size() ; ++i ) {
            if ( first ) {
                sql.append( Syntax.Where );
                first = false;
            } else
                sql.append( Syntax.OpAnd );
            sql.append( ( (Join) _joins.elementAt( i ) ).asWhere() );
        }

        if ( lock )
            sql.append( " FOR UPDATE" );
        return sql.toString();
    }


    public String toString()
    {
        return "<" + getQuery( false ) + ">";
    }


    public Object clone()
    {
        QueryExpr clone;

        clone = new QueryExpr();
        clone._tables = (Hashtable) _tables.clone();
        clone._conds = (Vector) _conds.clone();
        clone._cols = (Vector) _cols.clone();
        clone._relCols = (Vector) _relCols.clone();
        clone._joins = (Vector) _joins.clone();
        return clone;
    }


    static class Join
    {

        private String   _leftTable;

        private String[] _leftColumns;

        private String   _rightTable;

        private String[] _rightColumns;

        Join( String leftTable, String leftColumn, String rightTable, String rightColumn )
        {
            _leftTable = leftTable;
            _leftColumns = new String[] { leftColumn };
            _rightTable = rightTable;
            _rightColumns = new String[] { rightColumn };
        }

        Join( String leftTable, String[] leftColumns, String rightTable, String[] rightColumns )
        {
            _leftTable = leftTable;
            _leftColumns = (String[]) leftColumns.clone();
            _rightTable = rightTable;
            _rightColumns =(String[]) rightColumns.clone();
        }

        String asWhere()
        {
            StringBuffer sql;

            sql = new StringBuffer();
            for ( int i = 0 ; i < _leftColumns.length ; ++i ) {
                if ( i > 0 )
                    sql.append( Syntax.OpAnd );
                sql.append( _leftTable + Syntax.TableColumnSeparator + _leftColumns[ i ] );
                sql.append( Syntax.OpEqual );
                sql.append( _rightTable + Syntax.TableColumnSeparator + _rightColumns[ i ] );
            }
            return sql.toString();
        }

    }


}


