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


package org.exolab.castor.xml;


import java.util.Vector;
import java.util.Enumeration;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.loader.MappingLoader;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.loader.FieldDescriptorImpl;
import org.exolab.castor.mapping.xml.Mapping;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;


/**
 * An XML implementation of mapping helper. Creates XML class
 * descriptors from the mapping file.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class XMLMappingLoader
    extends MappingLoader
{


    /**
     * The type for the name of a compiled class.
     */
    private static final String CompiledType = "XML";


    public XMLMappingLoader( ClassLoader loader )
        throws MappingException
    {
        super( loader );
    }


    /*
    protected void resolveRelations( ClassDescriptor clsDesc )
        throws MappingException
    {
        super.resolveRelations( clsDesc );

        // At this point the descriptor may contain only JDO fields,
        // and all container fields must be flattened.
        FieldDesc[] fields;
        Vector      allFields;

        allFields = new Vector();
        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof ContainerFieldDesc ) {
                FieldDesc[] cFields;
                
                cFields = ( (ContainerFieldDesc) fields[ i ] ).getFields();
                for ( int j = 0 ; j < cFields.length ; ++j )
                    allFields.add( cFields[ j ] );
            } else if ( fields[ i ] instanceof XMLFieldDesc ) {
                allFields.add( fields[ i ] );
            } else {
                allFields.add( new XMLFieldDesc( fields[ i ], null, null ) );
            }
        }
        if ( clsDesc.getIdentity() != null ) {
            if ( clsDesc.getIdentity() instanceof ContainerFieldDesc ) {
                FieldDesc[] cFields;
                
                cFields = ( (ContainerFieldDesc) clsDesc.getIdentity() ).getFields();
                for ( int j = 0 ; j < cFields.length ; ++j )
                    allFields.add( cFields[ j ] );
            } else if ( clsDesc.getIdentity() instanceof XMLFieldDesc ) {
                allFields.add( clsDesc.getIdentity() );
            } else {
                allFields.add( new XMLFieldDesc( clsDesc.getIdentity(), null, null ) );
            }
        }
        
        XMLFieldDesc[] xmlFields;

        xmlFields = new XMLFieldDesc[ allFields.size() ];
        allFields.copyInto( xmlFields );
        ( (XMLClassDesc) clsDesc ).setXMLFields( xmlFields );
    }
    */


    protected ClassDescriptor createDescriptor( ClassMapping clsMap )
        throws MappingException
    {
        ClassDescriptor clsDesc;
        String          xmlName;
        
        // See if we have a compiled descriptor.
        clsDesc = loadClassDescriptor( clsMap.getClassName(), CompiledType, XMLClassDescriptor.class );
        if ( clsDesc != null )
            return clsDesc;

        // Use super class to create class descriptor. Field descriptors will be
        // generated only for supported fields, see createFieldDesc later on.
        clsDesc = super.createDescriptor( clsMap );
        if ( clsMap.getXmlSchema() == null || clsMap.getXmlSchema().getXmlName() == null )
            xmlName = clsDesc.getJavaClass().getName();
        else
            xmlName = clsMap.getXmlSchema().getXmlName();
        return new XMLClassDescriptor( clsDesc, xmlName );
    }


    protected FieldDescriptor createFieldDesc( Class javaClass, FieldMapping fieldMap )
        throws MappingException
    {
        FieldDescriptor fieldDesc;
        String          xmlName;
        NodeType        nodeType;
        
        // Create an XML field descriptor
        fieldDesc = super.createFieldDesc( javaClass, fieldMap );
        if ( fieldMap.getXmlInfo() == null || fieldMap.getXmlInfo().getName() == null )
            xmlName = null;
        else
            xmlName = fieldMap.getXmlInfo().getName();
        if ( fieldMap.getXmlInfo() == null || fieldMap.getXmlInfo().getNodeType() == null )
            nodeType = null;
        else
            nodeType = NodeType.getNodeType( fieldMap.getXmlInfo().getNodeType() );
        return new XMLFieldDescriptor( (FieldDescriptorImpl) fieldDesc, xmlName, nodeType );
    }


}



