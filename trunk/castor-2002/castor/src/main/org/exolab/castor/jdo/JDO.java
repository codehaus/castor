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
import org.exolab.castor.jdo.engine.DatabaseImpl;
import org.exolab.castor.jdo.engine.OQLQueryImpl;
import org.exolab.castor.jdo.engine.DatabaseRegistry;
import org.exolab.castor.util.Messages;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDO
    implements JDOSource, Referenceable,
	       ObjectFactory, Serializable
{


    private PrintWriter     _logWriter;


    private String          _dbName;


    private String          _dbConf;


    /**
     * Description of this datasource.
     */
    private String _description = "Castor JDOSource";


    public void setLogWriter( PrintWriter logWriter )
    {
        _logWriter = logWriter;
    }


    public PrintWriter getLogWriter()
    {
        return _logWriter;
    }


    /**
     * Sets the description of this datasource.
     * The standard name for this property is <tt>description</tt>.
     *
     * @param description The description of this datasource
     */
    public synchronized void setDescription( String description )
    {
	if ( description == null )
	    throw new NullPointerException( "DataSource: Argument 'description' is null" );
	_description = description;
    }


    /**
     * Returns the description of this datasource.
     * The standard name for this property is <tt>description</tt>.
     *
     * @return The description of this datasource
     */
    public String getDescription()
    {
	return _description;
    }


    public void setDatabaseName( String dbName )
    {
        _dbName = dbName;
    }


    public String getDatabaseName()
    {
        return _dbName;
    }


    public void setConfiguration( String url )
    {
        _dbConf = url;
    }


    public String getConfiguration()
    {
        return _dbConf;
    }


    public Database getDatabase()
        throws DatabaseNotFoundException
    {
        DatabaseImpl db;
        
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
        db = new DatabaseImpl( _dbName, _logWriter );
        return db;
    }


    /**
     * Load database configuration from the specified URL. <tt>url</tt>
     * must point to a JDO configuration file describing the database
     * name, connection factory and mappings.
     * 
     * @param url The JDO configuration file
     * @throw MappingException The mapping file is invalid, or any
     *  error occured trying to load the JDO configuration/mapping
     */
    public static void loadDatabase( String url )
        throws MappingException
    {
        DatabaseRegistry.loadDatabase( new InputSource( url ), null, null, null );
    }


    /**
     * Load database configuration from the specified URL. <tt>url</tt>
     * must point to a JDO configuration file describing the database
     * name, connection factory and mappings. <tt>loader</tt> is
     * optional, if null the default class loader is used.
     * 
     * @param url The JDO configuration file
     * @param loader The class loader to use, null for the default
     * @param logWriter Mapping information is printed there, if not null
     * @throw MappingException The mapping file is invalid, or any
     *  error occured trying to load the JDO configuration/mapping
     */
    public static void loadDatabase( String url, ClassLoader loader, PrintWriter logWriter )
        throws MappingException
    {
        DatabaseRegistry.loadDatabase( new InputSource( url ), null, logWriter, loader );
    }
    
    
    /**
     * Load database configuration from the specified input source.
     * <tt>source</tt> must point to a JDO configuration file describing
     * the database* name, connection factory and mappings.
     * <tt>resolver</tt> can be used to resolve cached entities, e.g.
     * for external mapping documents. <tt>loader</tt> is optional, if
     * null the default class loader is used.
     * 
     * @param source The JDO configuration file
     * @param resolve An optional entity resolver
     * @param loader The class loader to use, null for the default
     * @param logWriter Mapping information is printed there, if not null
     * @throw MappingException The mapping file is invalid, or any
     *  error occured trying to load the JDO configuration/mapping
     */
    public static void loadDatabase( InputSource source, EntityResolver resolver,
                                     ClassLoader loader, PrintWriter logWriter )
        throws MappingException
    {
        DatabaseRegistry.loadDatabase( source, resolver, logWriter, loader );
    }


    public synchronized Reference getReference()
    {
	Reference ref;

	// We use same object as factory.
	ref = new Reference( getClass().getName(), getClass().getName(), null );

        if ( _description != null )
            ref.add( new StringRefAddr( "description", _description ) );
        if ( _dbName != null )
            ref.add( new StringRefAddr( "dbName", _dbName ) );
        if ( _dbConf != null )
            ref.add( new StringRefAddr( "dbConf", _dbConf ) );
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

		JDO     ds;
		RefAddr addr;

		try {
		    ds = (JDO) Class.forName( ref.getClassName() ).newInstance();
		} catch ( Exception except ) {
		    throw new NamingException( except.toString() );
		}
		addr = ref.get( "description" );
		if ( addr != null )
		    ds._description = (String) addr.getContent();
		addr = ref.get( "dbName" );
		if ( addr != null )
		    ds._dbName = (String) addr.getContent();
		addr = ref.get( "dbConf" );
		if ( addr != null )
		    ds._dbConf = (String) addr.getContent();
		return ds;

	    } else
		throw new NamingException( "JDOSource: Reference not constructed from class " + getClass().getName() );
	} else if ( refObj instanceof Remote )
	    return refObj;
	else
	    return null;
    }

}

