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


import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.jdo.engine.JDBCSyntax;


/**
 * QueryExpression for DB 2.
 *
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class DB2QueryExpression
    extends JDBCQueryExpression
{


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

        // Use join syntax for all joins (LEFT OUTER and INNER).
        // Tables the appear in the join are removed from the
        // tables list in the FROM clause.
        tables = (Hashtable) _tables.clone();
        first = true;
        for ( int i = 0 ; i < _joins.size() ; ++i ) {
            Join join;

            if ( first )
                first = false;
            else
                sql.append( JDBCSyntax.TableSeparator );
            join = (Join) _joins.elementAt( i );

            sql.append( join.leftTable );
            if ( join.outer )
                sql.append( JDBCSyntax.LeftJoin );
            else
                sql.append( JDBCSyntax.InnerJoin );
            sql.append( join.rightTable ).append( JDBCSyntax.On );
            for ( int j = 0 ; j < join.leftColumns.length ; ++j ) {
                if ( j > 0 )
                    sql.append( JDBCSyntax.And );
                sql.append( join.leftColumns[ j ] ).append( OpEquals ).append( join.rightColumns[ j ] );
            }
            
            tables.remove( join.leftTable );
            tables.remove( join.rightTable );
        }
        enum = tables.elements();
        while ( enum.hasMoreElements() ) {
            if ( first )
                first = false;
            else
                sql.append( JDBCSyntax.TableSeparator );
            sql.append( (String) enum.nextElement() );
        }
        addWhereClause( sql, true );

        if ( _order != null )
          sql.append(JDBCSyntax.OrderBy).append(_order);
          
        // Do not use FOR UPDATE to lock query.
        return sql.toString();
    }


}


