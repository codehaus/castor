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


import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * Entity resolved for mapping DTD/schema. Can be used as wrapper to
 * another entity resolver. For example, if a resolver is used for
 * external entities in the mapping file, construct a new resolver
 * using the {@link #DTDResolver(EntityResolver)} constructor.
 * <p>
 * This resolver will support both the public and system identifier
 * of either the DTD or XML schema. It will return an input stream
 * to the DTD/schema obtained from the Castor JAR.
 *
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DTDResolver
    implements EntityResolver
{


    /**
     * List of public identifiers.
     */
    private static final String[] PublicId = new String[] {
        "-//EXOLAB/Castor Mapping DTD Version 1.0//EN",
        "-//EXOLAB/Castor Mapping Schema Version 1.0//EN"
    };

    /**
     * List of system identifiers.
     */
    private static final String[] SystemId = new String[] {
        "http://castor.exolab.org/mapping.dtd",
        "http://castor.exolab.org/mapping.xsd"
    };

    /**
     * List of resources for the actual files.
     */
    private static final String[] Resource = new String[] {
        "/org/exolab/castor/mapping/schema/mapping.dtd",
        "/org/exolab/castor/mapping/schame/mapping.xsd"
    };


    public static final String NamespacePrefix =
        "castor";

    public static final String NamespaceURI =
        "http://castor.exolab.org/mapping";


    /**
     * The wrapped resolver.
     */
    private EntityResolver _resolver;


    /**
     * Base URL, if known.
     */
    private URL            _baseUrl;


    /**
     * Constructs a new DTD resolver. This resolver wraps another
     * resolver and will delegate all resolving not related to the
     * Castor mapping files to that resolver. The wrapper resolver
     * will typically be used for entities appearing in the actual
     * mapping file.
     */
    public DTDResolver( EntityResolver resolver )
    {
        _resolver = resolver;
    }


    /**
     * Constructs a new DTD resolver.
     */
    public DTDResolver()
    {
    }


    public void setBaseURL( URL baseUrl )
    {
        _baseUrl = baseUrl;
    }


    public InputSource resolveEntity( String publicId, String systemId )
        throws IOException, SAXException
    {
        int         i;
        InputSource source;
        
        // First, resolve all the DTD/schema.
        for ( i = 0 ; i < PublicId.length ; ++i ) {
            if ( PublicId[ i ].equals( publicId ) || SystemId[ i ].equals( systemId ) ) {
                return new InputSource( getClass().getResourceAsStream( Resource[ i ] ) );
            }
        }
        // If a resolver was specified, use it.
        if ( _resolver != null ) {
            source = _resolver.resolveEntity( publicId, systemId );
            if ( source != null )
                return source;
        }


        // Can't resolve public id, but might be able to resolve relative
        // system id, since we have a base URI.
        if ( systemId != null && _baseUrl != null ) {
            URL url;

            try {
                url = new URL( systemId );
                return new InputSource( url.openStream() );
            } catch ( MalformedURLException except ) {
                try {
                    url = new URL( _baseUrl, systemId );
                    return new InputSource( url.openStream() );
                } catch ( MalformedURLException ex2 ) { }
            }
            return null;
        }

        // No resolving.
        return null;
    }


}
