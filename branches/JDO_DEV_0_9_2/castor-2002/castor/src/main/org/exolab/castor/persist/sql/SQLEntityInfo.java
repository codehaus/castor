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
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.persist.sql;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.TypeConvertor;
import org.exolab.castor.persist.EntityInfo;
import org.exolab.castor.persist.EntityFieldInfo;
import org.exolab.castor.persist.types.SQLTypes;

/**
 * This class hold SQL-specific information and is used internally by the bridge layer.
 * It aggregates EntityInfo.
 * It follows the Factory pattern and caches instances.
 *
 * @author <a href="mailto:on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 */
public class SQLEntityInfo
{

    private static HashMap _instances = new HashMap();

    /**
     * The info for the table;
     */
    public final EntityInfo info;

    public final SQLFieldInfo[] idInfo;

    /**
     * The positions of idInfo elements in fieldInfo array (idInfo.length == idPos.length).
     *
     */
    public final int[] idPos;

    /**
     * The names of SQL columns that the identity consists of.
     * idNames.length may be more than idInfo.length if identity contains foreign keys.
     */
    public final String idNames[];

    /**
     * The array is parallel to info.fieldInfo array, but on positions of join fields there are nulls.
     */
    public final SQLFieldInfo[] fieldInfo;

    /**
     * The array is parallel to info.subEntities array.
     */
    public final SQLEntityInfo[] subEntities;


    private SQLEntityInfo(EntityInfo info) throws MappingException {
        ArrayList idNamesList = new ArrayList();
        String[] fieldNames;
        boolean isId;

        this.info = info;

        // "Linearize" the identity columns
        idInfo = new SQLFieldInfo[info.idInfo.length];
        for (int i = 0; i < idInfo.length; i++) {
            idInfo[i] = new SQLFieldInfo(info.idInfo[i]);
            fieldNames = info.idInfo[i].fieldNames;
            for (int j = 0; j < fieldNames.length; j++) {
                idNamesList.add(fieldNames[j]);
            }
        }
        idNames = new String[idNamesList.size()];
        idNamesList.toArray(idNames);

        // Prepare field info
        fieldInfo = new SQLFieldInfo[info.fieldInfo.length];
        for (int i = 0; i < fieldInfo.length; i++) {
            if (!info.fieldInfo[i].join) {
                isId = false;
                for (int j = 0; j < idInfo.length; j++) {
                    if (info.fieldInfo[i].fieldNames[0].equals(info.idInfo[j].fieldNames[0])) {
                        isId = true;
                        idPos[j] = i;
                        break;
                    }
                }
                if (!isId) {
                    fieldInfo[i] = new SQLFieldInfo(info.fieldInfo[i]);
                }
            }
        }

        // Prepare sub-entities info
        if (info.subEntities == null) {
            subEntities = null;
        } else {
            subEntities = new SQLEntityInfo[info.subEntities.length];
            for (int i = 0; i < subEntities.length; i++) {
                subEntities[i] = SQLEntityInfo.getInstance(info.subEntities[i]);
            }
        }
    }

    public static SQLEntityInfo getInstance(EntityInfo info) throws MappingException {
        SQLEntityInfo res;

        res = (SQLEntityInfo) _instances.get(info);
        if (res == null) {
            res = new SQLEntityInfo(info);
            _instances.put(info, res);
        }
        return res;
    }

    public String toString() {
        return info.toString();
    }
}
