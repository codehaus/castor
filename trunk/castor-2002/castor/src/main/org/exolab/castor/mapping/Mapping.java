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


package org.exolab.castor.mapping;


import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.mapping.xml.MappingRoot;
import org.exolab.castor.mapping.xml.Include;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.jdo.engine.JDOMappingLoader;
import org.exolab.castor.dax.engine.DAXMappingLoader;
import org.exolab.castor.xml.XMLMappingLoader;
import org.exolab.castor.util.Messages;
import org.exolab.castor.util.DTDResolver;



/**
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class Mapping
{


    /**
     * Log writer to report progress. May be null.
     */
    private PrintWriter _logWriter;


    /**
     * The class loader to use.
     */
    private ClassLoader _loader;


    /**
     * The entity resolver to use. May be null.
     */
    private DTDResolver _resolver = new DTDResolver();


    /**
     * The loaded mapping.
     */
    private MappingRoot  _mapping;


    /**
     * The loaded JDO mapping.
     */
    private JDOMappingLoader _jdoMapping;


    /**
     * The loaded XML mapping.
     */
    private XMLMappingLoader _xmlMapping;


    /**
     * The loaded DAX mapping.
     */
    private DAXMappingLoader _daxMapping;


    /**
     * Constructs a new mapping.
     *
     * @param loader The class loader to use, null for the default
     */
    public Mapping( ClassLoader loader )
    {
        if ( loader == null )
            loader = getClass().getClassLoader();
        _loader = loader;
    }


    /**
     * Constructs a new mapping.
     */
    public Mapping()
    {
        this( null );
    }


    public MappingResolver getJDOMapping()
        throws MappingException
    {
        if ( _mapping == null )
            throw new MappingException( "Must call loadMapping first" );
        if ( _jdoMapping == null ) {
            _jdoMapping = new JDOMappingLoader( _loader, _logWriter );
            _jdoMapping.loadMapping( _mapping );
        }
        return _jdoMapping;
    }


    public MappingResolver getXMLMapping()
        throws MappingException
    {
        if ( _mapping == null )
            throw new MappingException( "Must call loadMapping first" );
        if ( _xmlMapping == null ) {
            _xmlMapping = new XMLMappingLoader( _loader, _logWriter );
            _xmlMapping.loadMapping( _mapping );
        }
        return _xmlMapping;
    }


    public MappingResolver getDAXMapping()
        throws MappingException
    {
        if ( _mapping == null )
            throw new MappingException( "Must call loadMapping first" );
        if ( _daxMapping == null ) {
            _daxMapping = new DAXMappingLoader( _loader, _logWriter );
            _daxMapping.loadMapping( _mapping );
        }
        return _daxMapping;
    }


    /**
     * Sets the log writer. If not null, errors and other messages
     * will be directed to that log writer.
     *
     * @param logWriter The log writer to use
     */
    public void setLogWriter( PrintWriter logWriter )
    {
        _logWriter = logWriter;
    }


    /**
     * Sets the entity resolver. The entity resolver can be used to
     * resolve external entities and cached documents that are used
     * from within mapping files.
     *
     * @param resolver The entity resolver to use
     */
    public void setEntityResolver( EntityResolver resolver )
    {
        _resolver = new DTDResolver( resolver );
    }


    /**
     * Sets the base URL for the mapping and related files. If the base
     * URL is known, files can be included using relative names. Any URL
     * can be passed, if the URL can serve as a base URL it will be used.
     *
     * @param url The base URL
     */
    public void setBaseURL( String url )
    {
        try {
            _resolver.setBaseURL( new URL( url ) );
        } catch ( MalformedURLException except ) { }
    }


    /**
     * Loads the mapping from the specified URL. If an entity resolver
     * was specified, will use the entity resolver to resolve the URL.
     * This method is also used to load mappings referenced from another
     * mapping or configuration file.
     *
     * @param url The URL of the mapping file
     * @throws IOException An error occured when reading the mapping
     *  file
     * @throws MappingException The mapping file is invalid
     */
    public void loadMapping( String url )
        throws IOException, MappingException
    {
        if ( _resolver.getBaseURL() == null )
            setBaseURL( url );
        loadMappingInternal( url );
    }


    /**
     * Loads the mapping from the specified URL.
     *
     * @param url The URL of the mapping file
     * @throws IOException An error occured when reading the mapping
     *  file
     * @throws MappingException The mapping file is invalid
     */
    public void loadMapping( URL url )
        throws IOException, MappingException
    {
        InputSource source;

        try {
            if ( _resolver.getBaseURL() == null )
                _resolver.setBaseURL( url );
            source = _resolver.resolveEntity( null, url.toString() );
            if ( source == null ) {
                source = new InputSource( url.toString() );
                source.setByteStream( url.openStream() );
            } else
                source.setSystemId( url.toString() );
            if ( _logWriter != null )
                _logWriter.println( Messages.format( "mapping.loadingFrom", url.toString() ) );
            loadMappingInternal( source );
        } catch ( SAXException except ) {
            throw new MappingException( except );
        }
    }


    /**
     * Loads the mapping from the specified input source.
     *
     * @param source The input source
     * @throws IOException An error occured when reading the mapping
     *  file
     * @throws MappingException The mapping file is invalid
     */
    public void loadMapping( InputSource source )
        throws IOException, MappingException
    {
        loadMappingInternal( source );
    }


    /**
     * Internal recursive loading method. This method will load the
     * mapping document into a mapping object and load all the included
     * mapping along the way into a single collection.
     *
     * @param url The URL of the mapping file
     * @throws IOException An error occured when reading the mapping
     *  file
     * @throws MappingException The mapping file is invalid
     */
    private void loadMappingInternal( String url )
        throws IOException, MappingException
    {
        InputSource source;

        try {
            source = _resolver.resolveEntity( null, url );
            if ( source == null )
                source = new InputSource( url );
            else
                source.setSystemId( url );
            if ( _logWriter != null )
                _logWriter.println( Messages.format( "mapping.loadingFrom", url ) );
            loadMappingInternal( source );
        } catch ( SAXException except ) {
            throw new MappingException( except );
        }
    }


    /**
     * Internal recursive loading method. This method will load the
     * mapping document into a mapping object and load all the included
     * mapping along the way into a single collection.
     *
     * @param source The input source
     * @throws IOException An error occured when reading the mapping
     *  file
     * @throws MappingException The mapping file is invalid
     */
    private void loadMappingInternal( InputSource source )
        throws IOException, MappingException
    {
        MappingRoot  loaded;
        Unmarshaller unm;
        Enumeration  enum;
       
        try {
            if ( _mapping == null )
                _mapping = new MappingRoot();

            unm = new Unmarshaller( MappingRoot.class );
            unm.setEntityResolver( _resolver );
            if ( _logWriter != null )
                unm.setLogWriter( _logWriter );
            loaded = (MappingRoot) unm.unmarshal( source );
            enum = loaded.enumerateClassMapping();
            while ( enum.hasMoreElements() )
                _mapping.addClassMapping( (ClassMapping) enum.nextElement() );

            // Load all the included mapping by reference
            Enumeration   includes;

            includes = loaded.enumerateInclude();
            while ( includes.hasMoreElements() ) {
                try {
                    loadMappingInternal( ( (Include) includes.nextElement() ).getHref() );
                } catch ( Exception except ) {
                    throw new MappingException( except );
                }
            }
        } catch ( MarshalException except ) {
            if ( except.getException() != null )
                throw new MappingException( except.getException() );
            throw new MappingException( except );
        } catch ( Exception except ) {
            throw new MappingException( except );
        }
    }


}


