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
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.jtf.CWTestCategory;
import org.exolab.exceptions.CWClassConstructorException;


/**
 * Concurrent access test. Tests a JDO modification and concurrent
 * JDBC modification to determine if JDO can detect the modification
 * with dirty checking.
 */
public class Race extends CWTestCase {

	private final static int NUM_OF_RACING_THREADS = 5;

	private final static int NUM_OF_VALUE_PAIRS = 1;

	private final static int NUM_OF_TRIALS = 50;

    private JDOCategory    _category;

    public Race( CWTestCategory category )
        throws CWClassConstructorException
    {
        super( "TC12", "Race" );
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


    public boolean run( CWVerboseStream stream ) {
        OQLQuery      oql;
        TestObjectEx    object;
        Enumeration   enum;
		Database db2;

        boolean result = true;

        try {
            Database _db = _category.getDatabase( stream.verbose() );
            Connection _conn = _category.getJDBCConnection(); 

			// clear the table
            int del = _conn.createStatement().executeUpdate( "DELETE FROM test_race" );
			stream.writeVerbose( "row deleted in table test_race: " + del );
            _conn.commit();

			// create pairs of number
			_db.begin();
			TestRace[] jdos = new TestRace[NUM_OF_VALUE_PAIRS];
			TestRaceSyn[] controls = new TestRaceSyn[NUM_OF_VALUE_PAIRS];
			for ( int i=0; i<jdos.length; i++ ) {
				controls[i] = new TestRaceSyn();
				jdos[i] = new TestRace();
				_db.create( jdos[i] );
			}
			_db.commit();

			// create threads, make a race so each thread 
			// keeping increment to the pairs of number.
			RaceThread[] ts = new RaceThread[NUM_OF_RACING_THREADS];
			
			for ( int i=0; i<ts.length; i++ ) {
				ts[i] = new RaceThread( stream, _category, controls, NUM_OF_TRIALS );
				ts[i].start();
			}

			// wait till everybody done
			boolean isAllDone = false;
			int num;
			while ( !isAllDone ) {
				Thread.currentThread().sleep( 1000 );				
				num = 0;
				for ( int i=0; i<ts.length; i++ ) {
					if ( ts[i].isDone() ) {
						num++;
					}
				}
				if ( num == ts.length ) 
					isAllDone = true;
			}

			// see if their sum agree
			_db.begin();
			num = 0;
			for ( int i=0; i<jdos.length; i++ ) {
	            oql = _db.getOQLQuery( "SELECT object FROM jdo.TestRace object WHERE id = $1" );
	            oql.bind( i );
	            enum = oql.execute();
	            if ( enum.hasMoreElements() ) {
	                TestRace tr = (TestRace) enum.nextElement();
					if ( tr.getValue1() == controls[i].getValue1() && controls[i].getValue1() == (ts.length * NUM_OF_TRIALS) ) 
						num++;
					System.out.println( "Number Pair "+i+" -- JDO: "+tr.getValue1()+" control: "+controls[i].getValue1());	
				}
			}
			_db.commit();

			// report result
			if ( num != jdos.length ) {
				result = false;
				stream.writeVerbose("Error: Racing condition occurs!");
			} else {
				stream.writeVerbose("Racing condition passed! :)");
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
	class RaceThread extends Thread {
		Database db;
		TestRaceSyn[] tr;
		int trial;
		boolean isDone;
		Random ran;
		CWVerboseStream stream;
		RaceThread( CWVerboseStream stream, JDOCategory c, TestRaceSyn[] tr, int n ) throws Exception {
			this.db = c.getDatabase( stream.verbose() );
			this.tr = tr;
			this.trial = n;
			this.stream = stream;
			this.ran = new Random();
		}
		public void run() {
			int num = 0;
			System.out.println("start");
			out:
			for ( int j=0; j<trial; j++ ) {
				for ( int i=0; i<tr.length; i++ ) {
					boolean isOk = false;
					// inc the control value. (these objects are thread safe)
					tr[i].incValue1();

					// select and inc the jdo object.
					little:
					while ( !isOk ) {
						try {
							db.begin();
				            OQLQuery oql = db.getOQLQuery( "SELECT object FROM jdo.TestRace object WHERE id = $1" );
				            oql.bind( i );
				            Enumeration enum = oql.execute();
				            if ( enum.hasMoreElements() ) {
				                TestRace tr = (TestRace) enum.nextElement();
								tr.incValue1();
								db.commit();
								isOk = true;
							} else {
								System.out.println("Error: "+" element not found!! missed in cache????");
								if ( db.isActive() ) try { db.rollback(); } catch ( Exception e ) {}
								break little;
							}
						} catch ( TransactionAbortedException e ) {
							// this exception should happen one in a while.
							stream.writeVerbose( "Excepted exception: "+e );
							if ( db.isActive() ) try { db.rollback(); } catch ( Exception ee ) {}
						} catch ( QueryException e ) {
							stream.writeVerbose( "Thread will be killed. Unexcepted exception: "+e );
							if ( db.isActive() ) try { db.rollback(); } catch ( Exception ee ) {}
							break out;
						} catch ( TransactionNotInProgressException e ) {
							stream.writeVerbose( "Thread will be killed. Unexcepted exception: "+e );
							if ( db.isActive() ) try { db.rollback(); } catch ( Exception ee ) {}
							break out;
						} catch ( PersistenceException e ) {
							stream.writeVerbose( "Thread will be killed. Unexcepted exception: "+e );
							if ( db.isActive() ) try { db.rollback(); } catch ( Exception ee ) {}
							break out;
						} catch ( Exception e ) {
							stream.writeVerbose( "Thread will be killed. Unexcepted exception: "+e );
							if ( db.isActive() ) try { db.rollback(); } catch ( Exception ee ) {}
							break out;
						}
					}

					// make some non-deterministicity. otherwise, we are just lining up
					// thread and won't discover problem.
					if ( ran.nextDouble() < 0.3 ) {
						try {
							Thread.currentThread().sleep( 100 );
						} catch ( InterruptedException e ) {
							System.out.println(e);
							break out;
						}
					}
				}
			}
			isDone = true;
		}
		boolean isDone() {
			return isDone;
		}
	}
}

