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


package org.exolab.castor.jdo.drivers;


import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.exolab.castor.jdo.engine.JDBCSyntax;
import org.exolab.castor.persist.spi.QueryExpression;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDBCQueryExpression
    implements QueryExpression
{


    protected Hashtable _tables = new Hashtable();


    protected Vector    _cols = new Vector();


    protected Vector    _conds = new Vector();


    protected Vector    _joins = new Vector();


    protected String    _where;

    protected String    _order;

    protected boolean   _distinct = false;


    public void setDistinct(boolean distinct)
    {
        _distinct = distinct;
    }

    public void addColumn( String tableName, String columnName )
    {
        _tables.put( tableName, tableName );
        _cols.addElement( tableName + JDBCSyntax.TableColumnSeparator + columnName  );
    }


    public void addParameter( String tableName, String columnName, String condOp )
    {
        addCondition( tableName, columnName, condOp, JDBCSyntax.Parameter );
    }


    public void addCondition( String tableName, String columnName,
                              String condOp, String value )
    {
        _tables.put( tableName, tableName );
        _conds.addElement( tableName + JDBCSyntax.TableColumnSeparator + columnName +
                           condOp + value );
    }


    public void addInnerJoin( String leftTable, String leftColumn,
                              String rightTable, String rightColumn )
    {
        _tables.put( leftTable, leftTable );
        _tables.put( rightTable, rightTable );
        _joins.addElement( new Join( leftTable, leftColumn, rightTable, rightColumn, false ) );
    }


    public void addOuterJoin( String leftTable, String leftColumn,
                              String rightTable, String rightColumn )
    {
        _tables.put( leftTable, leftTable );
        _tables.put( rightTable, rightTable );
        _joins.addElement( new Join( leftTable, leftColumn, rightTable, rightColumn, true ) );
    }


    public void addWhereClause( String where )
    {
        _where = where;
    }


    public void addOrderClause( String order ) {
        _order = order;
    }

    protected String getColumnList()
    {
        StringBuffer sql;

        if ( _cols.size() == 0 )
            return "1";

        sql = new StringBuffer();
        for ( int i = 0 ; i < _cols.size() ; ++i ) {
            if ( i > 0 )
                sql.append( JDBCSyntax.ColumnSeparator );
            sql.append( (String) _cols.elementAt( i ) );
        }
        return sql.toString();
    }


    protected boolean addWhereClause( StringBuffer sql, boolean first )
    {
        if ( _conds.size() > 0 ) {
            if ( first ) {
                sql.append( JDBCSyntax.Where );
                first = false;
            } else
                sql.append( JDBCSyntax.And );
            for ( int i = 0 ; i < _conds.size() ; ++i ) {
                if ( i > 0 )
                    sql.append( JDBCSyntax.And );
                sql.append( (String) _conds.elementAt( i ) );
            }
        }
        if ( _where != null ) {
            if ( first ) {
                sql.append( JDBCSyntax.Where );
                first = false;
            } else
                sql.append( JDBCSyntax.And );
            sql.append( _where );
        }
        return first;
    }


    public String getStatement( boolean lock )
    {
        StringBuffer sql;
        Enumeration  enum;
        boolean      first;
        Hashtable    tables;

        sql = new StringBuffer();
        sql.append( JDBCSyntax.Select );
        if ( _distinct )
          sql.append( JDBCSyntax.Distinct );
        sql.append( getColumnList() );
        sql.append( JDBCSyntax.From );

        tables = (Hashtable) _tables.clone();
        // Use outer join syntax for all outer joins. Inner joins come later.
        first = true;
        for ( int i = 0 ; i < _joins.size() ; ++i ) {
            Join join;

            join = (Join) _joins.elementAt( i );
            if ( join.outer ) {
                if ( first )
                    first = false;
                else
                    sql.append( JDBCSyntax.TableSeparator );
                sql.append( "{oj " );
                sql.append( join.leftTable );
                sql.append( JDBCSyntax.LeftJoin );
                sql.append( join.rightTable ).append( JDBCSyntax.On );
                for ( int j = 0 ; j < join.leftColumns.length ; ++j ) {
                    if ( j > 0 )
                        sql.append( JDBCSyntax.And );
                    sql.append( join.leftTable ).append( JDBCSyntax.TableColumnSeparator ).append( join.leftColumns[ j ] ).append( OpEquals );
                    sql.append( join.rightTable ).append( JDBCSyntax.TableColumnSeparator ).append( join.rightColumns[ j ] );
                }
                sql.append( "}" );
                tables.remove( join.leftTable );
                tables.remove( join.rightTable );
            }
        }
        enum = tables.elements();
        while ( enum.hasMoreElements() ) {
            if ( first )
                first = false;
            else
                sql.append( JDBCSyntax.TableSeparator );
            sql.append( (String) enum.nextElement() );
        }

        // Use standard join syntax for all inner joins.
        first = true;
        for ( int i = 0 ; i < _joins.size() ; ++i ) {
            Join join;
            
            join = (Join) _joins.elementAt( i );
            if ( ! join.outer ) {
                if ( first ) {
                    sql.append( JDBCSyntax.Where );
                    first = false;
                } else
                    sql.append( JDBCSyntax.And );
                for ( int j = 0 ; j < join.leftColumns.length ; ++j ) {
                    if ( j > 0 )
                        sql.append( JDBCSyntax.And );
                    sql.append( join.leftTable ).append( JDBCSyntax.TableColumnSeparator ).append( join.leftColumns[ j ] ).append( OpEquals );
                    sql.append( join.rightTable ).append( JDBCSyntax.TableColumnSeparator ).append( join.rightColumns[ j ] );
                }
            }
        } 
        first = addWhereClause( sql, first );

        if ( _order != null )
          sql.append(JDBCSyntax.OrderBy).append(_order);
          
        // There is no standard way to lock selected tables.
        return sql.toString();
    }


    public String toString()
    {
        return "<" + getStatement( false ) + ">";
    }


    public Object clone()
    {
        JDBCQueryExpression clone;

        try {
            clone = (JDBCQueryExpression) getClass().newInstance();
        } catch ( Exception except ) {
            // This should never happen
            throw new RuntimeException( except.toString() );
        }
        clone._tables = (Hashtable) _tables.clone();
        clone._conds = (Vector) _conds.clone();
        clone._cols = (Vector) _cols.clone();
        clone._joins = (Vector) _joins.clone();
        return clone;
    }


    static class Join
    {

        final String   leftTable;

        final String[] leftColumns;

        final String   rightTable;

        final String[] rightColumns;

        final boolean  outer;

        Join( String leftTable, String leftColumn, String rightTable, String rightColumn,
              boolean outer )
        {
            this.leftTable = leftTable;
            this.leftColumns = new String[] { leftColumn };
            this.rightTable = rightTable;
            this.rightColumns = new String[] { rightColumn };
            this.outer = outer;
        }

        Join( String leftTable, String[] leftColumns, String rightTable, String[] rightColumns,
              boolean outer )
        {
            this.leftTable = leftTable;
            this.leftColumns = (String[]) leftColumns.clone();
            this.rightTable = rightTable;
            this.rightColumns =(String[]) rightColumns.clone();
            this.outer = outer;
        }

    }


}


