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


import java.util.Arrays;


public final class EntityFieldInfo {

    public boolean id;

    public boolean foreign;

    public boolean complex;

    public boolean join;

    public boolean blob;

    public boolean sameDataStore;

    public int cardinality;

    public boolean dirtyCheck;

    public Class expectedType;

    public Class declaredType;

    public String entityClass;

    public String fieldName;

    public String[] fieldNames;

    public String relatedEntityClass;

    public String relatedEntityIdFieldName;

    public String[] relatedEntityIdFieldNames;

    public EntityFieldInfo() {
        // implements it....
    }

    public boolean equals( Object object ) {
        if ( !( object instanceof EntityFieldInfo ) )
            return false;

        EntityFieldInfo info = (EntityFieldInfo) object;

        return 
          ( this.id            == info.id &&
            this.foreign       == info.foreign &&
            this.complex       == info.complex &&
            this.join          == info.join &&
            this.blob          == info.blob &&
            this.sameDataStore == info.sameDataStore &&
            this.cardinality   == info.cardinality &&
            this.dirtyCheck    == info.dirtyCheck &&
            this.expectedType  == info.expectedType &&
            this.declaredType  == info.declaredType &&
            this.entityClass   == info.entityClass &&
            this.fieldName     == info.fieldName &&
            this.relatedEntityClass == info.relatedEntityClass &&
            this.relatedEntityIdFieldName == info.relatedEntityIdFieldName &&
            Arrays.equals( this.fieldNames, info.fieldNames ) && 
            Arrays.equals( this.relatedEntityIdFieldNames, info.relatedEntityIdFieldNames ) );
    }

    public int hashCode() {
        return entityClass == null? 0: entityClass.hashCode();
    }


}