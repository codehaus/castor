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


import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.SQLException;
import org.exolab.castor.mapping.ObjectDesc;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.CacheEngine;
import org.exolab.castor.persist.Persistence;
import org.exolab.castor.persist.PersistenceFactory;
import org.exolab.castor.jdo.MappingTable;
import org.exolab.castor.jdo.desc.JDOObjectDesc;
import org.exolab.castor.util.Logger;


/**
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DatabaseEngine
    extends CacheEngine
{


    private DatabaseSource _dbs;


    private static Hashtable  _engines = new Hashtable();


    /**
     * Construct a new cache engine with the specified name, mapping
     * table and persistence engine.
     */
    DatabaseEngine( DatabaseSource dbs, PrintWriter logWriter )
	throws MappingException
    {
	super( dbs.getName(), dbs.getMappingTable(), new PersistenceFactory() {
	    public Persistence getPersistence( CacheEngine cache, ObjectDesc objDesc,
					       PrintWriter logWriter )
		{
		    try {
			return new SQLEngine( (JDOObjectDesc) objDesc, logWriter );
		    } catch ( MappingException except ) {
			Logger.getSystemLogger().println( except.toString() );
			return null;
		    }
		}
	}, logWriter );
	_dbs = dbs;
    }


    public Connection createConnection()
	throws SQLException
    {
	return _dbs.getConnection();
    }


    static DatabaseEngine getDatabaseEngine( DatabaseSource dbs, PrintWriter logWriter )
	throws MappingException
    {
	DatabaseEngine engine;

	synchronized ( _engines ) {
	    engine = (DatabaseEngine) _engines.get( dbs );
	    if ( engine == null ) {
		engine = new DatabaseEngine( dbs, logWriter );
		_engines.put( dbs, engine );
	    }
	    return engine;
	}
    }


    public static DatabaseEngine getDatabaseEngine( Class type )
    {
	DatabaseEngine engine;
	Enumeration    enum;

	enum = _engines.elements();
	while ( enum.hasMoreElements() ) {
	    engine = (DatabaseEngine) enum.nextElement();
	    if ( engine.getObjectDesc( type ) != null )
		return engine;
	}
	return null;
    }


}


