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
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id $
 */
package org.exolab.castor.persist;


import org.exolab.castor.persist.sql.KeyGeneratorDescriptor;


/**
 * EntityInfo specify a Entity
 *
 */
public final class EntityInfo {

    /**
     * The base entity class which this object represent.
     */
    public final String entityClass;

    /**
     * The information of all logical fields
     */
    public final EntityFieldInfo[] fieldInfo;

    /**
     * The information of all identity fields
     */
    public final EntityFieldInfo[] idInfo;

    /**
     * All entities which extends the base entities
     */
    public final EntityInfo[] subEntities;

    /**
     *
     */
    public final Object discriminator;

    public final KeyGeneratorDescriptor keyGen;

    public EntityInfo( String entityClass, EntityFieldInfo[] idInfo,
                       EntityFieldInfo[] fieldInfo, EntityInfo[] subEntities,
                       Object discriminator, KeyGeneratorDescriptor keyGen ) {
        this.entityClass   = entityClass;
        this.fieldInfo     = fieldInfo;
        this.idInfo        = idInfo;
        this.subEntities   = subEntities;
        this.discriminator = discriminator;
        this.keyGen        = keyGen;
    }

    public boolean equals( Object object ) {
        if ( !( object instanceof EntityInfo ) || object == null )
            return false;

        EntityInfo info = (EntityInfo) object;

        return entityClass.equals(info.entityClass);
    }

    public int hashCode() {
        return entityClass == null? 0: entityClass.hashCode();
    }
}