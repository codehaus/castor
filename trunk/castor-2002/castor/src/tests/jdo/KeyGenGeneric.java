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
import org.exolab.castor.jdo.DataObjects;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.jtf.CWTestCategory;
import org.exolab.exceptions.CWClassConstructorException;


/**
 * Test for generic key generators (MAX and HIGH/LOW).
 */
public class KeyGenGeneric
    extends CWTestCase
{


    private JDOCategory    _category;


    public KeyGenGeneric( CWTestCategory category )
        throws CWClassConstructorException
    {
        super( "TC07", "Key generator" );
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
            db = _category.getDatabase( stream.verbose() );
            TestAllKeyGens( stream, db );
            db.close();
        } catch ( Exception except ) {
            stream.writeVerbose( "Error: " + except );
            except.printStackTrace();
            result = false;
        }
        return result;
    }

    protected void TestAllKeyGens( CWVerboseStream stream, Database db) 
            throws Exception
    {
        TestOneKeyGen( stream, db, TestMaxObject.class, TestMaxExtends.class );
        TestOneKeyGen( stream, db, TestHighLowObject.class, TestHighLowExtends.class );
    }


    /**
     * The main goal of the test is to verify key generators in the case
     * of "extends" relation between two classes.
     * For each key generator we have a pair of classes: TestXXXObject and
     * TestXXXExtends which use key generator XXX.
     */
    protected void TestOneKeyGen( CWVerboseStream stream, Database db,
                                  Class objClass, Class extClass )
            throws Exception
    {
        OQLQuery            oql;
        TestKeyGenObject    object;
        TestKeyGenObject    ext;
        Enumeration         enum;
        
        // Open transaction in order to perform JDO operations
        db.begin();
        
        // Create first object 
        object = (TestKeyGenObject) objClass.newInstance();
        stream.writeVerbose( "Creating first object: " + object );
        db.create( object );
        stream.writeVerbose( "Created first object: " + object );

        // Create second object
        ext = (TestKeyGenObject) extClass.newInstance();
        stream.writeVerbose( "Creating second object: " + ext );
        db.create( ext );
        stream.writeVerbose( "Created second object: " + ext );

        db.commit();

        db.begin();

        // Find the first object and remove it 
        object = (TestKeyGenObject) db.load( objClass, object.getId() );
        stream.writeVerbose( "Removing first object: " + object );
        db.remove( object );

        // Find the second object and remove it
        ext = (TestKeyGenObject) db.load( extClass, ext.getId() );
        stream.writeVerbose( "Removing second object: " + ext );
        db.remove( ext );

        db.commit();
    }


}
