
package org.exolab.castor.jdo.drivers;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.exolab.castor.jdo.engine.JDBCSyntax;
import org.exolab.castor.persist.spi.PersistenceFactory;


/**
 * QueryExpression for Interbase.
 *
 */
public final class InterbaseQueryExpression
    extends JDBCQueryExpression
{


    public InterbaseQueryExpression( PersistenceFactory factory )
    {
        super( factory );
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

        if ( _select == null )
          sql.append( getColumnList() );
        else
          sql.append( _select ).append(" ");

        sql.append( JDBCSyntax.From );

        tables = (Hashtable) _tables.clone();
        // Use outer join syntax for all outer joins. Inner joins come later.
        first = true;
        for ( int i = 0 ; i < _joins.size() ; ++i ) {
            Join join;

            join = (Join) _joins.elementAt( i );
            if ( join.outer ) {
                //else sql.append( JDBCSyntax.TableSeparator );

                //sql.append( "{oj " );
                if(first) sql.append( _factory.quoteName( join.leftTable ) );
                sql.append( JDBCSyntax.LeftJoin );
                sql.append( _factory.quoteName( join.rightTable ) ).append( JDBCSyntax.On );
                for ( int j = 0 ; j < join.leftColumns.length ; ++j ) {
                    if ( j > 0 )
                        sql.append( JDBCSyntax.And );
                    sql.append( _factory.quoteName( join.leftTable + JDBCSyntax.TableColumnSeparator +
                                                    join.leftColumns[ j ] ) ).append( OpEquals );
                    sql.append( _factory.quoteName( join.rightTable + JDBCSyntax.TableColumnSeparator +
                                                    join.rightColumns[ j ] ) );
                }
                //sql.append( "}" );
                tables.remove( join.leftTable );
                tables.remove( join.rightTable );

                if ( first ) first = false;
            }
        }
        enum = tables.elements();
        while ( enum.hasMoreElements() ) {
            if ( first )
                first = false;
            else
                sql.append( JDBCSyntax.TableSeparator );
            sql.append( _factory.quoteName( (String) enum.nextElement() ) );
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
                    sql.append( _factory.quoteName( join.leftTable + JDBCSyntax.TableColumnSeparator +
                                                    join.leftColumns[ j ] ) ).append( OpEquals );
                    sql.append( _factory.quoteName( join.rightTable + JDBCSyntax.TableColumnSeparator +
                                                    join.rightColumns[ j ] ) );
                }
            }
        }
        first = addWhereClause( sql, first );

        if ( _order != null )
          sql.append(JDBCSyntax.OrderBy).append(_order);

        // There is no standard way to lock selected tables.
        return sql.toString();
    }

}


