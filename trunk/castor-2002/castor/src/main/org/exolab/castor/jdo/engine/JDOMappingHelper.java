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


package org.exolab.castor.jdo.engine;


import java.util.Vector;
import java.util.Enumeration;
import org.exolab.castor.mapping.Types;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.ContainerFieldDesc;
import org.exolab.castor.mapping.MappingHelper;
import org.exolab.castor.mapping.Types;
import org.exolab.castor.mapping.xml.Mapping;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;


/**
 * A JDO implementation of mapping helper. Creates JDO class descriptors
 * from the mapping file.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDOMappingHelper
    extends MappingHelper
{


    /**
     * The type for the name of a compiled class.
     */
    private static final String CompiledType = "JDO";


    private static final String DirtyCheck = "check";


    public JDOMappingHelper( ClassLoader loader )
        throws MappingException
    {
        super( loader );
    }


    protected void resolveRelations( ClassDesc clsDesc )
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
                for ( int j = 0 ; j < cFields.length ; ++j ) {
                    if ( cFields[ j ] instanceof JDOFieldDesc )
                        allFields.add( cFields[ j ] );
                }
            } else if ( fields[ i ] instanceof JDOFieldDesc ) {
                allFields.add( fields[ i ] );
            }
        }
        
        JDOFieldDesc[] jdoFields;

        jdoFields = new JDOFieldDesc[ allFields.size() ];
        allFields.copyInto( jdoFields );
        ( (JDOClassDesc) clsDesc ).setJDOFields( jdoFields );
    }


    protected ClassDesc createDescriptor( ClassMapping clsMap )
        throws MappingException
    {
        ClassDesc   clsDesc;
        FieldDesc[] fields;
        Vector      jdoFields;
        
        // If no SQL information for class, ignore it. JDO only
        // supports JDO class descriptors.
        if ( clsMap.getSqlTable() == null )
            return ClassDesc.NoDescriptor;

        // See if we have a compiled descriptor.
        clsDesc = loadClassDescriptor( clsMap.getClassName(), CompiledType, JDOClassDesc.class );
        if ( clsDesc != null )
            return clsDesc;

        // Use super class to create class descriptor. Field descriptors will be
        // generated only for supported fields, see createFieldDesc later on.
        // This class may only extend a JDO class, otherwise no mapping will be
        // found for the parent.
        clsDesc = super.createDescriptor( clsMap );

        // JDO descriptor must include an identity field, the identity field
        // is either a field, or a container field containing only JDO fields.
        // If the identity field is not a JDO field, it will be cleaned later
        // on (we need the descriptor for relations mapping).
        if ( clsDesc.getIdentity() == null )
            throw new MappingException( "mapping.noIdentity", clsDesc.getJavaClass().getName() );
        if ( clsDesc.getIdentity() instanceof ContainerFieldDesc ) {
            FieldDesc[] idFields;
            
            idFields = ( (ContainerFieldDesc) clsDesc.getIdentity() ).getFields();
            for ( int i = 0 ; i < idFields.length ; ++i )
                if ( ! ( idFields[ i ] instanceof JDOFieldDesc ) )
                    throw new MappingException( "jdo.identityNotJDO", idFields[ i ] );
        }
        
        return new JDOClassDesc( clsDesc, clsMap.getSqlTable().getTableName() );
    }


    protected FieldDesc createFieldDesc( Class javaClass, FieldMapping fieldMap )
        throws MappingException
    {
        FieldDesc  fieldDesc;
        Class      sqlType;
        String     sqlName;
        
        // If not an SQL field, return a stock field descriptor.
        if ( fieldMap.getSqlInfo() == null )
            return super.createFieldDesc( javaClass, fieldMap );
        
        // Create a JDO field descriptor
        fieldDesc = super.createFieldDesc( javaClass, fieldMap );
        if ( fieldMap.getSqlInfo().getType() == null )
            sqlType = fieldDesc.getFieldType();
        else
            sqlType = SQLTypes.typeFromName( fieldMap.getSqlInfo().getType() );
        if ( fieldMap.getSqlInfo().getName() == null )
            sqlName = SQLTypes.javaToSqlName( fieldDesc.getFieldName() );
        else
            sqlName = fieldMap.getSqlInfo().getName();
        return new JDOFieldDesc( fieldDesc, sqlName, sqlType,
                                 DirtyCheck.equals( fieldMap.getSqlInfo().getDirty() ) );
    }


}



