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


package org.exolab.castor.dax.engine;


import java.util.Enumeration;
import java.util.Hashtable;
import netscape.ldap.LDAPUrl;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.persist.CacheEngine;
import org.exolab.castor.mapping.ObjectDesc;
import org.exolab.castor.persist.Persistence;
import org.exolab.castor.persist.PersistenceFactory;
import org.exolab.castor.persist.PersistenceException;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
final class DirectoryEngine
    extends CacheEngine
{


    private static Hashtable  _engines = new Hashtable();


    private ObjectDesc  _objDesc;


    DirectoryEngine( LDAPUrl url, ObjectDesc objDesc )
	throws MappingException
    {
	super( url.toString(), new SingleMapping( objDesc ), new EngineFactory( url.getDN() ) );
	_objDesc = objDesc;
    }


    ObjectDesc getObjectDesc()
    {
	return _objDesc;
    }


    public static DirectoryEngine getEngine( LDAPUrl url, ObjectDesc objDesc )
	throws MappingException
    {
	DirectoryEngine engine;

	synchronized ( _engines ) {
	    engine = (DirectoryEngine) _engines.get( url );
	    if ( engine == null ) {
		engine = new DirectoryEngine( url, objDesc );
		_engines.put( url, engine );
	    }
	    return engine;
	}
    }


    static class SingleMapping
	implements MappingResolver
    {

	private Hashtable _objDescs;

	SingleMapping( ObjectDesc objDesc )
	{
	    _objDescs = new Hashtable();
	    _objDescs.put( objDesc.getObjectType(), objDesc );
	}

	public ObjectDesc getDescriptor( Class type )
	{
	    return (ObjectDesc) _objDescs.get( type );
	}

	public Enumeration listDescriptors()
	{
	    return _objDescs.elements();
	}

	public Enumeration listObjectTypes()
	{
	    return _objDescs.keys();
	}

    }


    static class EngineFactory
	implements PersistenceFactory
    {

	private String _rootDN;

	EngineFactory( String rootDN )
	{
	    _rootDN = rootDN;
	}
	
	public Persistence getPersistence( CacheEngine cache, ObjectDesc objDesc )
	{
	    try {
		return new MozillaEngine( (DAXObjectDesc) objDesc, _rootDN );
	    } catch ( MappingException except ) {
		return null;
	    }
	}

    }


}
