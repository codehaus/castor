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
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.loader.MappingLoader;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.loader.FieldDescriptorImpl;
import org.exolab.castor.mapping.xml.Mapping;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.xml.ContainerMapping;
import org.exolab.castor.mapping.xml.LdapInfo;


/**
 * A DAX implementation of mapping helper. Creates DAX class descriptors
 * from the mapping file.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DAXMappingLoader
    extends MappingLoader
{


    /**
     * The type for the name of a compiled class.
     */
    private static final String CompiledType = "DAX";


    public DAXMappingLoader( ClassLoader loader )
        throws MappingException
    {
        super( loader );
    }


    /*
    protected void resolveRelations( ClassDesc clsDesc )
        throws MappingException
    {
        super.resolveRelations( clsDesc );
        // At this point the descriptor may contain only DAX fields,
        // and all container fields must be flattened.
        FieldDesc[] fields;
        Vector      allFields;

        allFields = new Vector();
        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof ContainerFieldDesc ) {
                FieldDesc[] cFields;
                
                cFields = ( (ContainerFieldDesc) fields[ i ] ).getFields();
                for ( int j = 0 ; j < cFields.length ; ++j ) {
                    if ( cFields[ j ] instanceof DAXFieldDesc )
                        allFields.add( cFields[ j ] );
                }
            } else if ( fields[ i ] instanceof DAXFieldDesc ) {
                allFields.add( fields[ i ] );
            }
        }
        
        DAXFieldDesc[] daxFields;

        daxFields = new DAXFieldDesc[ allFields.size() ];
        allFields.copyInto( daxFields );
        ( (DAXClassDesc) clsDesc ).setDAXFields( daxFields );
    }
    */


    protected ClassDescriptor createDescriptor( ClassMapping clsMap )
        throws MappingException
    {
        ClassDescriptor   clsDesc;
        FieldDescriptor[] fields;
        Vector            jdoFields;
        
        // If no LDAP information for class, ignore it. DAX only
        // supports DAX class descriptors.
        if ( clsMap.getLdapEntry() == null )
            return NoDescriptor;

        // See if we have a compiled descriptor.
        clsDesc = loadClassDescriptor( clsMap.getClassName(), CompiledType, DAXClassDescriptor.class );
        if ( clsDesc != null )
            return clsDesc;

        // Use super class to create class descriptor. Field descriptors will be
        // generated only for supported fields, see createFieldDesc later on.
        // This class may only extend a DAX class, otherwise no mapping will be
        // found for the parent.
        clsDesc = super.createDescriptor( clsMap );

        // DAX descriptor must include an identity field, the identity field
        // is either a field, or a container field containing only DAX fields.
        // If the identity field is not a JDO field, it will be cleaned later
        // on (we need the descriptor for relations mapping).
        if ( clsDesc.getIdentity() == null )
            throw new MappingException( "mapping.noIdentity", clsDesc.getJavaClass().getName() );
        /*
        if ( clsDesc.getIdentity() instanceof ContainerFieldDesc ) {
            FieldDescriptor[] idFields;
            
            idFields = ( (ContainerFieldDesc) clsDesc.getIdentity() ).getFields();
            for ( int i = 0 ; i < idFields.length ; ++i )
                if ( ! ( idFields[ i ] instanceof DAXFieldDesc ) )
                    throw new MappingException( "dax.identityNotDAX", idFields[ i ] );
        }
        */
        
        return new DAXClassDescriptor( clsDesc, clsMap.getLdapEntry().getObjectClass(), null );
    }


    protected FieldDescriptor createFieldDesc( Class javaClass, FieldMapping fieldMap )
        throws MappingException
    {
        FieldDescriptor fieldDesc;
        String          ldapName;
        
        // If not an LDAP field, return a stock field descriptor.
        if ( fieldMap.getLdapInfo() == null )
            return super.createFieldDesc( javaClass, fieldMap );
        
        // Create a DAX field descriptor
        fieldDesc = super.createFieldDesc( javaClass, fieldMap );
        if ( fieldMap.getLdapInfo().getName() == null )
            ldapName = fieldDesc.getFieldName();
        else
            ldapName = fieldMap.getLdapInfo().getName();
        return new DAXFieldDescriptor( (FieldDescriptorImpl) fieldDesc, ldapName );
    }


}
