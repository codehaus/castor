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
import java.util.Date;
import java.util.Enumeration;
import org.exolab.castor.jdo.DataObjects;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.engine.LongTransactionSupport;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.jtf.CWTestCategory;
import org.exolab.exceptions.CWClassConstructorException;


/**
 */
public class Persistent
    extends CWTestCase
{


    private JDOCategory    _category;


    public Persistent( CWTestCategory category )
        throws CWClassConstructorException
    {
        super( "TC16", "Persistence interface tests" );
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


    public boolean run( CWVerboseStream stream )
    {
        boolean result = true;
        Database db;

        try {
            OQLQuery      oql;
            TestPersistent parent;
            TestPersistent child;
            QueryResults  qres;
            long          beforeModTime;
            long          afterModTime;
            long          modTime;
            
            db = _category.getDatabase( stream.verbose() );

            stream.writeVerbose( "Delete everything" );
            db.begin();
            oql = db.getOQLQuery( "SELECT p FROM jdo.TestPersistent p WHERE id=$1" );
            oql.bind( TestPersistent.DefaultId );
            qres = oql.execute();
            while ( qres.hasMore() ) {
                db.remove( qres.next() );
            }
            db.commit();
            
            stream.writeVerbose( "Attempt to create parent with children" );
            db.begin();
            parent = new TestPersistent();
            parent.addChild( new TestPersistent( 71 ) );
            parent.addChild( new TestPersistent( 72 ) );
            child = new TestPersistent( 73 );
            child.addChild( new TestPersistent( 731 ) );
            child.addChild( new TestPersistent( 732 ) );
            parent.addChild( child );
            db.create( parent );
            db.commit();
            db.begin();
            parent = (TestPersistent) db.load( TestPersistent.class, new Integer( TestPersistent.DefaultId ) );
            if ( parent != null ) {
                if ( parent.getChildren() == null || parent.getChildren().size() != 3 ||
                     ! parent.getChildren().contains( new TestPersistent( 71 ) ) ||
                     ! parent.getChildren().contains( new TestPersistent( 72 ) ) ||
                     ! parent.getChildren().contains( new TestPersistent( 73 ) ) ) {
                    stream.writeVerbose( "Error: loaded parent without three children: " + parent );					
                    result  = false;
                } else {
	                child = parent.findChild( 73 );
	                if ( child == null || child.getChildren() == null || 
	                     child.getChildren().size() != 2 ||
	                     ! child.getChildren().contains( new TestPersistent( 731 ) ) ||
	                     ! child.getChildren().contains( new TestPersistent( 732 ) ) ) {
	                    stream.writeVerbose( "Error: loaded child without two grandchildren: " + child );
	                    result  = false;
	                } else {
						child.setValue("new value");
					}
				}
            } else {
                stream.writeVerbose( "Error: failed to create parent with children" );
                result = false;
            }
            if ( result )
                stream.writeVerbose( "Created parent with children: " + parent );
            beforeModTime = System.currentTimeMillis() / 1000;
            db.commit();
            afterModTime = System.currentTimeMillis() / 1000;
            db.begin();
            child = (TestPersistent) db.load( TestPersistent.class, new Integer( 73 ) );
            if ( child == null ) {
                stream.writeVerbose( "Error: child not loaded" );
                result  = false;
            } else if ( child.getModificationTime() == null ) {
                stream.writeVerbose( "Error: wrong modification time: " + child );
                result  = false;
            } else {
                modTime = child.getModificationTime().getTime() / 1000;
                if ( modTime < beforeModTime || modTime  > afterModTime ) {
                    stream.writeVerbose( "Error: wrong modification time: " + child );
                    result  = false;
                }
            }
            db.commit();
            if ( ! result )
                return false;


            db.close();
        } catch ( Exception except ) {
            stream.writeVerbose( "Error: " + except );
            except.printStackTrace();
            result = false;
        }
        return result;
    }


}
