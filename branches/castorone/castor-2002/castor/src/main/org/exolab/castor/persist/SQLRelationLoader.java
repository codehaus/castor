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
 */


package org.exolab.castor.persist;


import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.loader.MappingLoader;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.mapping.loader.RelationDescriptor;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.spi.CallbackInterceptor;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.util.Messages;
import org.exolab.castor.jdo.PersistenceException;
import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLRelationLoader {
    private String tableName;
    
    private String left;
    
    private String right;

    public SQLRelationLoader( RelationDescriptor rd, String type ) throws MappingException {
        if ( rd.type1 != type && rd.type2 != type )
            throw new MappingException("Wrong relation descriptor for type:"+type);

        left = type;
        if ( rd.type1.equals(type) ) {
            left = rd.sql1;
            right = rd.sql2;
        } else {
            left = rd.sql2;
            right = rd.sql1;
        }
        
        tableName = rd.table;        
    }

    public SQLRelationLoader( String table, String key, String otherKey ) {
        tableName = table;
        left = key;
        right = otherKey;
    }
    /* always return vector of string for now */
    public Vector loadRelation( Connection conn, String id ) throws PersistenceException {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT " + left + ", " + right + " FROM " + tableName + 
                    " WHERE " + left + " = " + id + " ;" );

            Vector result = new Vector();
            while ( rset.next() ) {
                result.add( rset.getString( right ) );
            }
            return result;
        } catch ( SQLException e ) {
            throw new PersistenceException( e.toString() );
        }
    }

    public void createRelation( Connection conn, String leftValue, Vector rightValues ) 
            throws PersistenceException {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rset;
            for ( int i=0; i<rightValues.size(); i++ ) {
                /*
                String sStmt = "SELECT \"" + tableName+"\".\""+ left + 
                    "\", \"" + tableName+"\".\"" + right + "\" FROM " + tableName + 
                    " WHERE \""+tableName+"\".\""+ left + "\"=" + leftvalue + 
                    " AND \""+tableName+"\".\"" + right + "\"="  + rightvalues.elementAt(i);
                */
                String sStmt = "SELECT "+left + ", " + right + " FROM " + tableName 
                        + " WHERE " + left + "=" + leftValue + " AND " + right + "=" + rightValues.elementAt(i);
                rset = stmt.executeQuery(  "select group_id, person_id FROM test_group_person WHERE group_id=1 AND person_id=0" );
                if ( ! rset.next() ) {
                    sStmt = "INSERT INTO " + tableName + 
                        "( " + left + ", " + right + " ) VALUES ( " +
                        leftValue + ", " + rightValues.elementAt(i) + " )" ;
                    i = stmt.executeUpdate( sStmt );
                }
            }
        } catch ( SQLException e ) {
            throw new PersistenceException( e.toString() );
        }
    }
    public void createRelation( Connection conn, Object[] leftValue, Object[] rightValue ) 
            throws PersistenceException {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rset;

            String sStmt = "SELECT "+left + ", " + right + " FROM " + tableName 
                    + " WHERE " + left + "=" + leftValue[0] + " AND " + right + "=" + rightValue[0];
            System.out.println( sStmt );
            rset = stmt.executeQuery( sStmt );
            if ( ! rset.next() ) {
                sStmt = "INSERT INTO " + tableName + 
                    "( " + left + ", " + right + " ) VALUES ( " +
                    leftValue[0] + ", " + rightValue[0] + " )" ;
                int i = stmt.executeUpdate( sStmt );
            }
        } catch ( SQLException e ) {
            throw new PersistenceException( e.toString() );
        }
    }
    public void deleteRelation( Connection conn, Object[] leftValue ) 
            throws PersistenceException {

        try {
            Statement stmt = conn.createStatement();
            String sStmt;
            ResultSet rset;

            sStmt = "DELETE FROM " + tableName + " WHERE " + left + "=" + leftValue[0];
            int i = stmt.executeUpdate( sStmt );
        } catch ( SQLException e ) {
            throw new PersistenceException( e.toString() );
        }
    }

    public void deleteRelation( Connection conn, Object[] leftValue, Object[] rightValue ) 
            throws PersistenceException {

        try {
            Statement stmt = conn.createStatement();
            String sStmt;
            ResultSet rset;

            sStmt = "DELETE FROM " + tableName + " WHERE " + left + "=" + leftValue[0] +" AND " + right + "=" + rightValue[0];
            System.out.println(sStmt);
            int i = stmt.executeUpdate( sStmt );
        } catch ( SQLException e ) {
            throw new PersistenceException( e.toString() );
        }
    }

}

