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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLRelationLoader {
    private String tableName;

	private int[] leftType;

	private int[] rightType;

    private String[] left;
    
    private String[] right;

	private String select;

	private String insert;

	private String delete;

	private String deleteAll;


	/*
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
        
    }*/

    public SQLRelationLoader( String table, String[] key, int[] keyType, String[] otherKey, int[] otherKeyType ) {
        tableName = table;
        left = key;
        right = otherKey;
		leftType = keyType;
		rightType = otherKeyType;

		// construct select statement
		StringBuffer sb = new StringBuffer();
		int count = 0;
		sb.append("SELECT ");
		for ( int i=0; i < left.length; i++ ) {
			if ( i > 0 ) sb.append(",");
			sb.append( left[i] );
			count++;
		}
		for ( int i=0; i < right.length; i++ ) {
			sb.append(",");
			sb.append( right[i] );	
			count++;
		}
		sb.append(" FROM ");
		sb.append( tableName );
		sb.append(" WHERE ");
		for ( int i=0; i < left.length; i++ ) {
			if ( i > 0 ) sb.append(" AND ");
			sb.append( left[i] );
			sb.append("=?");
		}
		for ( int i=0; i < right.length; i++ ) {
			sb.append(" AND ");
			sb.append( right[i] );
			sb.append("=?");
		}
		select = sb.toString();

		// construct insert statement
		sb = new StringBuffer();
		count = 0;
		sb.append("INSERT INTO ");
		sb.append( tableName );
		sb.append(" (");
		for ( int i=0; i < left.length; i++ ) {
			if ( i > 0 ) sb.append(",");
			sb.append( left[i] );
			count++;
		}
		for ( int i=0; i < right.length; i++ ) {
			sb.append(",");
			sb.append( right[i] );	
			count++;
		}
		sb.append(") VALUES (");
		for ( int i=0; i < count; i++ ) {
			if ( i > 0 ) sb.append(",");
			sb.append("?");
		}
		sb.append(")");
		insert = sb.toString();

		// construct delete statement
		sb = new StringBuffer();
		count = 0;
		sb.append("DELETE FROM ");
		sb.append( tableName );
		sb.append(" WHERE ");
		for ( int i=0; i < left.length; i++ ) {
			if ( i > 0 ) sb.append(" AND ");
			sb.append( left[i] );
			sb.append("=?");
		}
		for ( int i=0; i < right.length; i++ ) {
			sb.append(" AND ");
			sb.append( right[i] );
			sb.append("=?");
		}
		delete = sb.toString();

		// construct delete statement for the left side only
		sb = new StringBuffer();
		count = 0;
		sb.append("DELETE FROM ");
		sb.append( tableName );
		sb.append(" WHERE ");
		for ( int i=0; i < left.length; i++ ) {
			if ( i > 0 ) sb.append(" AND ");
			sb.append( left[i] );
			sb.append("=?");
		}
		deleteAll = sb.toString();

    }
    public void createRelation( Connection conn, Object[] leftValue, Object[] rightValue ) 
            throws PersistenceException {
        
		try {
			int count = 1;
			ResultSet rset;
            PreparedStatement stmt = conn.prepareStatement( select );
			for ( int i=0; i < leftValue.length; i++ ) {				
				stmt.setObject( count, leftValue[i], leftType[i] );
				count++;
			}
			for ( int i=0; i < rightValue.length; i++ ) {
				stmt.setObject( count, rightValue[i], rightType[i] );
				count++;
			}
			count = 1;
            rset = stmt.executeQuery();
            if ( ! rset.next() ) {
				stmt = conn.prepareStatement( insert );
				for ( int i=0; i < leftValue.length; i++ ) {				
					stmt.setObject( count, leftValue[i], leftType[i] );
					count++;
				}
				for ( int i=0; i < rightValue.length; i++ ) {
					stmt.setObject( count, rightValue[i], rightType[i] );
					count++;
				}
				int r = stmt.executeUpdate();
            } 
        } catch ( SQLException e ) {
			e.printStackTrace();
            throw new PersistenceException( e.toString() );
        }
    }
    public void deleteRelation( Connection conn, Object[] leftValue ) 
            throws PersistenceException {

        try {
			int count = 1;
            PreparedStatement stmt = conn.prepareStatement( deleteAll );
			for ( int i=0; i < leftValue.length; i++ ) {				
				stmt.setObject( count, leftValue[i], leftType[i] );
				count++;
			}
            int i = stmt.executeUpdate();
        } catch ( SQLException e ) {
			e.printStackTrace();
            throw new PersistenceException( e.toString() );
        }
    }

    public void deleteRelation( Connection conn, Object[] leftValue, Object[] rightValue ) 
            throws PersistenceException {

        try {
			int count = 1;
            PreparedStatement stmt = conn.prepareStatement( delete );
			for ( int i=0; i < leftValue.length; i++ ) {				
				stmt.setObject( count, leftValue[i], leftType[i] );
				count++;
			}
			for ( int i=0; i < rightValue.length; i++ ) {
				stmt.setObject( count, rightValue[i], rightType[i] );
				count++;
			}
            int i = stmt.executeUpdate();
        } catch ( SQLException e ) {
			e.printStackTrace();
            throw new PersistenceException( e.toString() );
        }
    }

}

