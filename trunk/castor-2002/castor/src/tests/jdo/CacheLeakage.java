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


package jdo;


import java.io.IOException;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.lang.Math;
import java.util.Random;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.persist.CacheEngine;
import org.exolab.castor.persist.Cache;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.jtf.CWTestCategory;
import org.exolab.exceptions.CWClassConstructorException;


/**
 * Concurrent access test. Tests a JDO modification and concurrent
 * JDBC modification to determine if JDO can detect the modification
 * with dirty checking.
 */
public class CacheLeakage extends CWTestCase {


    private Database       _db;


    private Connection     _conn;


    private JDOCategory    _category;


    static final String    JDBCValue = "jdbc value";


    static final String    JDOValue = "jdo value";


	static final int CACHE_SIZE = CacheEngine.DEFAULT_CACHE_VALUE;


    public CacheLeakage( CWTestCategory category ) throws CWClassConstructorException {
        super( "TC11", "Cache Leakage" );
        _category = (JDOCategory) category;
    }

    public void preExecute()
    {
        super.preExecute();
    }


    public void postExecute()
    {
        super.postExecute();
    }

	private int deleteTable( CWVerboseStream stream, Connection conn, String table ) {
        // Perform direct JDBC access and delete everyting in the table
		try {
	        int del = _conn.createStatement().executeUpdate( "DELETE FROM "+ table );
			stream.writeVerbose( "Rows deleted from " + table + ": " + del );
	        _conn.commit();
			return del;
		} catch ( Exception e ) {
			return 0;
		}
	}

    public boolean run( CWVerboseStream stream ) {
        OQLQuery      oql;
        TestObjectEx    object;
        Enumeration   enum;
		Database db2;

        boolean result = true;

        try {
            _db = _category.getDatabase( stream.verbose() );
            _conn = _category.getJDBCConnection(); 
            _conn.setAutoCommit( false );

			// - delete all row of a table
			deleteTable( stream, _conn, "test_table_ex" );

            // Open transaction in order to perform JDO operations
            _db.begin();			
			// - create "cachesize * 2" objects for count limited
			Object[] ooo = new Object[Math.max(1,CACHE_SIZE*2)];
			for ( int i=0; i<ooo.length; i++ ) {
				ooo[i] = new TestObjectEx();
				_db.create( ooo[i] );
			}

			// create the same object again. see if DuplicatedIdentity throws 
			db2 = _category.getDatabase( stream.verbose() );
			db2.begin();
			int count = 0;
			// - create "cachesize - 5" objects for count limited
			for ( int i=0; i<ooo.length; i++ ) {
				try {
					db2.create( ooo[i] );
				} catch ( DuplicateIdentityException e ) {
					// good. expected exception throws
					count++;
				}
			}
			db2.commit();

			if ( count != ooo.length ) {
				result = false;
				stream.writeVerbose( "Error: some object ate by the cache" );
			} else {
				stream.writeVerbose( "all objects in the cache" );
			}

			// - check if each object have the right value
			stream.writeVerbose( "checking if each object have the right value" );
			breakpoint:
			for ( int i=0; i<ooo.length; i++ ) {
				//stream.writeVerbose( "Object identity of " + i + " " + _db.getIdentity(ooo[i]) );

	            oql = _db.getOQLQuery( "SELECT object FROM jdo.TestObjectEx object WHERE id = $1" );
	            oql.bind( i );
	            enum = oql.execute();
	            if ( enum.hasMoreElements() ) {
	                object = (TestObjectEx) enum.nextElement();
	                //stream.writeVerbose( "Retrieved object: " + object );
					if ( object.getId() != i || object.getIntValue2() != i ) {
						stream.writeVerbose( "Error: wrong object" );
						result = false;
						break breakpoint;
					} 

					if ( enum.hasMoreElements() ) {
						stream.writeVerbose( "Error: two objects with the same id" );
						result = false;
						break breakpoint;
					}
				} else {
					result = false;
					stream.writeVerbose( "Error: Object id=" + i + " not exist!" );
					break breakpoint;
				}
			}
			_db.commit();

			stream.writeVerbose( "checking if each object in the database" );
			// check the database using sql, see if everything is there
            ResultSet rs = _conn.createStatement().executeQuery( "SELECT * FROM test_table_ex" );
			int temp = 0;
			while ( rs.next() ) {
				temp++;
			}
            _conn.commit();
			if ( temp != ooo.length ) {
				result = false;
				stream.writeVerbose( "Error: size mismatch on object number and table rows. Object: " + ooo.length + " table row: " + temp );
			}

			stream.writeVerbose( "force the cache to dispose all the object and test if it is still valid" );
			// force the cache to dispose all the object and test if it is still valid
			if ( CacheEngine.DEFAULT_CACHE_TYPE == Cache.CACHE_TIME_LIMITED ) {
				Thread.currentThread().sleep( 1500 * CacheEngine.DEFAULT_CACHE_VALUE );
			} else if ( CacheEngine.DEFAULT_CACHE_TYPE == Cache.CACHE_COUNT_LIMITED ) {
				_db.begin();
				count = 0;
				// - create "cachesize - 5" objects for count limited
				for ( int i=0; i<ooo.length; i++ ) {
					try {
						_db.create( ooo[i] );
					} catch ( DuplicateIdentityException e ) {
						count++;
						stream.writeVerbose( "expected exception: " + e );
					}
				}
				_db.commit();

				if ( count != ooo.length ) {
					result = false;
					stream.writeVerbose( "Error: size mismatch on object number and table rows. Object: " + ooo.length + " table row: " + temp );
				}
			}

            _db.close();
            _conn.close();

		} catch ( Exception except ) {
            stream.writeVerbose( "Error: " + except );
            except.printStackTrace();
            result = false;
        }
        return result;
    }
}

