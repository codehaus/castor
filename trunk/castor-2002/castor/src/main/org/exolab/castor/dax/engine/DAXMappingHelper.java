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


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingHelper;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.mapping.ContainerFieldDesc;
import org.exolab.castor.mapping.Types;
import org.exolab.castor.mapping.xml.Mapping;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.xml.ContainerMapping;
import org.exolab.castor.mapping.xml.LdapInfo;


/**
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DAXMappingHelper
    extends MappingHelper
{


    public DAXMappingHelper( ClassLoader loader, Mapping mapping )
        throws MappingException
    {
	Enumeration   enum;
	ClassMapping  clsMap;
	ClassDesc     clsDesc;

	enum = mapping.enumerateClassMapping();
	while ( enum.hasMoreElements() ) {
	    clsMap = (ClassMapping) enum.nextElement();
	    clsDesc = createDescriptor( loader, clsMap );
	    if ( clsDesc != null )
		addDescriptor( clsDesc );
	}
    }


    protected ClassDesc createDescriptor( ClassLoader loader, ClassMapping objMap )
        throws MappingException
    {
	ClassDesc clsDesc;
	FieldDesc  attrSet;

	if ( objMap.getLdapEntry() == null )
	    return null;
	clsDesc = super.createDescriptor( loader, objMap );
	if ( clsDesc.getIdentity() == null ) {
	    return null;
	}
	return new DAXClassDesc( clsDesc, null, objMap.getLdapEntry().getObjectClass() );
    }


    protected FieldDesc[] createFieldDescs( ClassLoader loader, Class objType, FieldMapping[] fieldMaps )
	throws MappingException
    {
	Vector      fields;
	FieldDesc   fieldDesc;
	FieldDesc[] array;

	fields = new Vector();
	for ( int i = 0 ; i < fieldMaps.length ; ++i ) {
	    if ( fieldMaps[ i ].getLdapInfo() != null ) {
		fieldDesc = createFieldDesc( loader, objType, fieldMaps[ i ] );
		fieldDesc = new DAXFieldDesc( fieldDesc, fieldMaps[ i ].getLdapInfo().getName() );
		fields.addElement( fieldDesc );
	    }
	}
	array = new FieldDesc[ fields.size() ];
	fields.copyInto( array );
	return array;
    }


}
