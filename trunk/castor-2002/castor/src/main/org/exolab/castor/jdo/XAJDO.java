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


package org.exolab.castor.jdo;


import java.io.InputStream;
import java.io.Reader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Hashtable;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import java.rmi.Remote;
import javax.naming.Referenceable;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.RefAddr;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.jdo.engine.XADatabaseImpl;
import org.exolab.castor.jdo.engine.OQLQueryImpl;
import org.exolab.castor.jdo.engine.DatabaseRegistry;
import org.exolab.castor.util.Messages;


/**
 * Implementation of the JDO engine used for obtaining XA database
 * connection. An XAJDO object is constructed with the name of a database
 * and other properties, and {@link #getXADatabase} is used to obtain a
 * new database connection. This object is used by the transaction manager.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class XAJDO
    implements XADataObjects, Referenceable,
	       ObjectFactory, Serializable
{


    /**
     * The default lock timeout for this database is 10 seconds.
     */
    public static final int DefaultLockTimeout = 10;


    /**
     * Tthe URL of the database configuration file. If the URL is
     * specified, the first attempt to load a database of this type
     * will use the specified configuration file.
     */
    private String          _dbConf;


    /**
     * The log writer is a character output stream to which all
     * logging and tracing messages will be printed.
     */
    private PrintWriter    _logWriter;


    /**
     * The lock timeout for this database. Zero for immediate
     * timeout, an infinite value for no timeout. The timeout is
     * specified in seconds.
     */
    private int            _lockTimeout = DefaultLockTimeout;


    /**
     * The name of this database.
     */
    private String          _dbName;


    /**
     * Description of this database.
     */
    private String         _description = "Castor JDO";


    /**
     * Returns the log writer for this database source.
     * <p>
     * The log writer is a character output stream to which all
     * logging and tracing messages will be printed.
     *
     * @return The log writer, null if disabled
     */
    public void setLogWriter( PrintWriter logWriter )
    {
        _logWriter = logWriter;
    }


    /**
     * Sets the log writer for this database source.
     * <p>
     * The log writer is a character output stream to which all
     * logging and tracing messages will be printed.
     *
     * @param logWriter The log writer, null if disabled
     */
    public PrintWriter getLogWriter()
    {
        return _logWriter;
    }


    /**
     * Sets the description of this database.
     * <p>
     * The standard name for this property is <tt>description</tt>.
     *
     * @param description The description of this database
     */
    public void setDescription( String description )
    {
	if ( description == null )
	    throw new NullPointerException( "DataSource: Argument 'description' is null" );
	_description = description;
    }


    /**
     * Returns the description of this database.
     * <p>
     * The standard name for this property is <tt>description</tt>.
     *
     * @return The description of this database
     */
    public String getDescription()
    {
	return _description;
    }


    /**
     * Sets the name of this database. This attribute is required
     * in order to identify which database to open.
     * <p>
     * The standard name for this property is <tt>databaseName</tt>.
     *
     * @param name The name of this database
     */
    public void setDatabaseName( String name )
    {
        _dbName = name;
    }


    /**
     * Returns the name of this database.
     * <p>
     * The standard name for this property is <tt>databaseName</tt>.
     *
     * @return The name of this database
     */
    public String getDatabaseName()
    {
        return _dbName;
    }


    /**
     * Sets the lock timeout for this database. Use zero for immediate
     * timeout, an infinite value for no timeout. The timeout is
     * specified in seconds.
     * <p>
     * The standard name for this property is <tt>lockTimeout</tt>.
     *
     * @param seconds The lock timeout, specified in seconds
     */
    public void setLockTimeout( int seconds )
    {
        _lockTimeout = seconds;
    }


    /**
     * Returns the lock timeout for this database.
     * <p>
     * The standard name for this property is <tt>lockTimeout</tt>.
     *
     * @return The lock timeout, specified in seconds
     */
    public int getLockTimeout()
    {
        return _lockTimeout;
    }


    /**
     * Sets the URL of the database configuration file. If the URL is
     * specified, the first attempt to load a database of this type
     * will use the specified configuration file.
     * <p>
     * The standard name for this property is <tt>configuration</tt>.
     *
     * @param url The URL of the database configuration file
     */
    public void setConfiguration( String url )
    {
        _dbConf = url;
    }


    /**
     * Return the URL of the database configuration file.
     * <p>
     * The standard name for this property is <tt>configuration</tt>.
     *
     * @return The URL of the database configuration file
     */
    public String getConfiguration()
    {
        return _dbConf;
    }


    /**
     * Opens and returns an XA connection to the database. Throws an
     * {@link DatabaseNotFoundException} if the database named was not
     * set in the constructor or with a call to {@link #setDatabaseName},
     * or if no database configuration exists for the named database.
     *
     * @return An open connection to the database
     * @throws DatabaseNotFoundException Attempted to open a database
     *  that does not exist
     * @throws PersistenceException Database access failed
     */
    public XADatabase getXADatabase()
        throws DatabaseNotFoundException, PersistenceException
    {
        if ( _dbName == null )
            throw new IllegalStateException( "Called 'getDatabase' without first setting database name" );
        if ( DatabaseRegistry.getDatabaseRegistry( _dbName ) == null ) {
            if ( _dbConf == null )
                throw new DatabaseNotFoundException( Messages.format( "jdo.dbNoMapping", _dbName ) );
            try {
                DatabaseRegistry.loadDatabase( new InputSource( _dbConf ), null, _logWriter, null );
            } catch ( MappingException except ) {
                throw new DatabaseNotFoundException( Messages.format( "persist.nested", except.toString() ) );
            }
        }
        return new XADatabaseImpl( _dbName, _lockTimeout, _logWriter );
    }


    public synchronized Reference getReference()
    {
	Reference ref;

	// We use same object as factory.
	ref = new Reference( getClass().getName(), getClass().getName(), null );

        if ( _description != null )
            ref.add( new StringRefAddr( "description", _description ) );
        if ( _dbName != null )
            ref.add( new StringRefAddr( "databaseName", _dbName ) );
        if ( _dbConf != null )
            ref.add( new StringRefAddr( "configuration", _dbConf ) );
        ref.add( new StringRefAddr( "lockTimeout", Integer.toString( _lockTimeout ) ) );
 	return ref;
    }


    public Object getObjectInstance( Object refObj, Name name, Context nameCtx, Hashtable env )
        throws NamingException
    {
	Reference ref;

	// Can only reconstruct from a reference.
	if ( refObj instanceof Reference ) {
	    ref = (Reference) refObj;
	    // Make sure reference is of datasource class.
	    if ( ref.getClassName().equals( getClass().getName() ) ) {

		XAJDO   ds;
		RefAddr addr;

		try {
		    ds = (XAJDO) Class.forName( ref.getClassName() ).newInstance();
		} catch ( Exception except ) {
		    throw new NamingException( except.toString() );
		}
		addr = ref.get( "description" );
		if ( addr != null )
		    ds._description = (String) addr.getContent();
		addr = ref.get( "databaseName" );
		if ( addr != null )
		    ds._dbName = (String) addr.getContent();
		addr = ref.get( "configuration" );
		if ( addr != null )
		    ds._dbConf = (String) addr.getContent();
		addr = ref.get( "lockTimeout" );
		if ( addr != null )
                    ds._lockTimeout = Integer.parseInt( (String) addr.getContent() );
		return ds;

	    } else
		throw new NamingException( "JDO: Reference not constructed from class " + getClass().getName() );
	} else if ( refObj instanceof Remote )
	    return refObj;
	else
	    return null;
    }

}


