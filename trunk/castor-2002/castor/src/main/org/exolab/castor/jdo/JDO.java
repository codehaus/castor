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
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.jdo.engine.TransactionImpl;
import org.exolab.castor.jdo.engine.DatabaseImpl;
import org.exolab.castor.jdo.engine.OQLQueryImpl;
import org.exolab.castor.jdo.engine.DatabaseRegistry;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDO
    implements DatabaseSource
{


    private PrintWriter     _logWriter;


    private String          _dbName;


    public void setLogWriter( PrintWriter logWriter )
    {
        _logWriter = logWriter;
    }


    public PrintWriter getLogWriter()
    {
        return _logWriter;
    }


    public void setDatabaseName( String dbName )
    {
        _dbName = dbName;
    }


    public String getDatabaseName()
    {
        return _dbName;
    }


    public Database getDatabase()
        throws DatabaseNotFoundException
    {
        DatabaseImpl db;
        
        if ( _dbName == null )
            throw new IllegalStateException( "Called 'getDatabase' without first setting database name" );
        db = new DatabaseImpl( _dbName, _logWriter );
        return db;
    }


    public Transaction newTransaction()
    {
        return new TransactionImpl();
    }


    public Transaction currentTransaction()
    {
        return TransactionImpl.getCurrent();
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
     * @throw MappingException The mapping file is invalid, or any
     *  error occured trying to load the JDO configuration/mapping
     */
    public static void loadDatabase( String url, ClassLoader loader )
        throws MappingException
    {
        DatabaseRegistry.loadDatabase( new InputSource( url ), null, null, loader );
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
     * @throw MappingException The mapping file is invalid, or any
     *  error occured trying to load the JDO configuration/mapping
     */
    public static void loadDatabase( InputSource source, EntityResolver resolver, ClassLoader loader )
        throws MappingException
    {
        DatabaseRegistry.loadDatabase( source, resolver, null, loader );
    }


}

