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
 * A JDO implementation of mapping helper. Creates JDO class
 * descriptors from the mapping file.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class JDOMappingHelper
    extends MappingHelper
{


    public JDOMappingHelper( ClassLoader loader )
        throws MappingException
    {
        super( loader );
    }


    protected void loadMapping( Mapping mapping )
        throws MappingException
    {
        Enumeration   enum;
        ClassMapping  clsMap;
        ClassDesc     clsDesc;
        
        enum = mapping.enumerateClassMapping();
        while ( enum.hasMoreElements() ) {
            clsMap = (ClassMapping) enum.nextElement();
            clsDesc = createDescriptor( clsMap );
            if ( clsDesc != null ) {
                addDescriptor( clsDesc );
            } else {
                if ( getLogWriter() != null ) {
                    getLogWriter().println( "Ignored mapping for class " + clsMap.getClassName() + " - no SQL information available" );
                }
            }
        }
    }


    protected ClassDesc createDescriptor( ClassMapping clsMap )
        throws MappingException
    {
        ClassDesc   clsDesc;
        FieldDesc[] fields;
        FieldDesc   identity;
        Vector      jdoFields;
        Vector      jdoRels;
        
        // If no SQL information for class, ignore it.
        if ( clsMap.getSqlTable() == null )
            return null;

        // Use super class to create class descriptor. Field descriptors will be
        // generated only for supported fields, see createFieldDesc later on.
        // This class may only extend a JDO class, otherwise no mapping will be
        // found for the parent.
        clsDesc = super.createDescriptor( clsMap );

        // JDO descriptor must include an identity field.
        identity = clsDesc.getIdentity();
        if ( identity == null )
            throw new MappingException( "mapping.noIdentity", clsDesc.getJavaClass().getName() );

        // Accumulate all the fields, flatten them, remove the identity
        // field, and use that to create the descriptor.
        jdoFields = new Vector();
        jdoRels = new Vector();
        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( ! fields[ i ].getFieldName().equals( identity.getFieldName() ) ) {
                if ( fields[ i ] instanceof ContainerFieldDesc ) {
                    FieldDesc[] cFields;
                    
                    cFields = ( (ContainerFieldDesc) fields[ i ] ).getFields();
                    for ( int j = 0 ; j < cFields.length ; ++j )
                        jdoFields.add( cFields[ j ] );
                } else if ( fields[ i ] instanceof JDOFieldDesc ) {
                    Class type;

                    type = fields[ i ].getFieldType();
                    if ( Types.isSimpleType( type ) ) {
                        // Simple type. Map field directly.
                        jdoFields.add( fields[ i ] );
                    } else {
                        // Complex type. This might be a relation.
                        JDOClassDesc rel;

                        rel = (JDOClassDesc) getDescriptor( fields[ i ].getFieldType() );
                        if ( rel == null ) {
                            // XXX Need some handling here. Serializable, conversion, error?
                        } else {
                            rel = new JDORelationDesc( rel, Relation.ManyToOne, fields[ i ], null,
                                                       ( (JDOFieldDesc) fields[ i ] ) );
                            jdoRels.add( rel );
                        }
                    }
                } else {
                    JDOClassDesc rel;
                    
                    rel = (JDOClassDesc) getDescriptor( fields[ i ].getFieldType() );
                    if ( rel != null ) {
                        // ???
                        rel = new JDORelationDesc( rel, Relation.OneToOne, fields[ i ], null, null );
                        jdoRels.add( rel );
                    }
                }
            }
        }
        
        JDOFieldDesc[] array;
        array = new JDOFieldDesc[ jdoFields.size() ];
        jdoFields.copyInto( array );
        JDORelationDesc[] related;
        related = new JDORelationDesc[ jdoRels.size() ];
        jdoRels.copyInto( related );
        return new JDOClassDesc( clsDesc.getJavaClass(), clsMap.getSqlTable().getTableName(),
                                 array, identity, related, (JDOClassDesc) clsDesc.getExtends() );
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
        return new JDOFieldDesc( fieldDesc, sqlName, sqlType, "check".equals( fieldMap.getSqlInfo().getDirty() ) );
    }


}



