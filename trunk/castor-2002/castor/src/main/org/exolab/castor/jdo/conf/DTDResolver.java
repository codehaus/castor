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


package org.exolab.castor.jdo.conf;


import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * Entity resolved for the JDO configuration DTD/schema. Can be used as wrapper
 * to another entity resolver. For example, if a resolver is used for external
 * entities in the mapping file, construct a new resolver using the {@link
 * #DTDResolver(EntityResolver)} constructor.
 * <p>
 * This resolver will support both the public and system identifier
 * of either the DTD or XML schema. It will return an input stream
 * to the DTD/schema obtained from the Castor JAR.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DTDResolver
    implements EntityResolver
{


    /**
     * List of public identifiers.
     */
    private static final String[] PublicId = new String[] {
        "-//EXOLAB/Castor JDO Configuration DTD Version 1.0//EN",
        "-//EXOLAB/Castor JDO Configuration Schema Version 1.0//EN"
    };

    /**
     * List of system identifiers.
     */
    private static final String[] SystemId = new String[] {
        "http://castor.exolab.org/jdo-conf.dtd",
        "http://castor.exolab.org/jdo-conf.xsd"
    };

    /**
     * List of resources for the actual files.
     */
    private static final String[] Resource = new String[] {
        "/org/exolab/castor/jdo/conf/jdo-conf.dtd",
        "/org/exolab/castor/jdo/conf/jdo-conf.xsd"
    };


    /**
     * The wrapped resolver.
     */
    private EntityResolver _resolver;


    /**
     * Constructs a new DTD resolver. This resolver wraps another
     * resolver and will delegate all resolving not related to the
     * Castor mapping files to that resolver. The wrapper resolver
     * will typically be used for entities appearing in the actual
     * mapping file.
     */
    public DTDResolver( EntityResolver resolver )
    {
        _resolver = new org.exolab.castor.mapping.loader.DTDResolver( resolver );
    }


    /**
     * Constructs a new DTD resolver.
     */
    public DTDResolver()
    {
        _resolver = new org.exolab.castor.mapping.loader.DTDResolver();
    }


    public InputSource resolveEntity( String publicId, String systemId )
        throws IOException, SAXException
    {
        int i;

        for ( i = 0 ; i < PublicId.length ; ++i ) {
            if ( PublicId[ i ].equals( publicId ) ||
                 ( publicId == null && SystemId[ i ].equals( systemId ) ) )
                return new InputSource( getClass().getResourceAsStream( Resource[ i ] ) );
        }
        if ( _resolver == null )
            return null;
        else
            return _resolver.resolveEntity( publicId, systemId );
    }


}
