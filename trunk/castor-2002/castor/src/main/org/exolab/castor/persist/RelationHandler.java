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


package org.exolab.castor.persist;


import java.util.Enumeration;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.CollectionHandler;
import org.exolab.castor.mapping.loader.IndirectFieldHandler;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class RelationHandler
{


    private final FieldHandler      _handler;


    private final CollectionHandler _colHandler;


    private final ClassHandler      _relHandler;


    private final String            _fieldName;


    private final boolean           _attached;


    RelationHandler( FieldDescriptor fieldDesc, ClassHandler relHandler, boolean attached )
    {
        _fieldName = fieldDesc.getFieldName();
        if ( fieldDesc.getHandler() instanceof IndirectFieldHandler )
            _handler = ( (IndirectFieldHandler) fieldDesc.getHandler() ).getHandler();
        else
            _handler = fieldDesc.getHandler();
        _colHandler = fieldDesc.getCollectionHandler();
        _relHandler = relHandler;
        _attached = attached;
    }


    public String getFieldName()
    {
        return _fieldName;
    }


    public FieldHandler getFieldHandler()
    {
        return _handler;
    }


    public Object getIdentity( Object object )
    {
        return _relHandler.getIdentity( object );
    }


    public Object getRelated( Object object )
    {
        return _handler.getValue( object );
    }


    public Enumeration listRelated( Object object )
    {
        Object collection;

        collection = _handler.getValue( object );
        if ( collection == null )
            return null;
        return _colHandler.getValues( collection );
    }


    public void setRelated( Object object, Object related )
    {
        _handler.setValue( object, related );
    }


    public boolean isMulti()
    {
        return ( _colHandler != null );
    }


    public boolean isAttached()
    {
        return _attached;
    }


    public ClassHandler getRelatedHandler()
    {
        return _relHandler;
    }


    public Class getRelatedClass()
    {
        return _relHandler.getJavaClass();
    }


    public Object newInstance()
    {
        return _relHandler.newInstance();
    }


    public String toString()
    {
        return _handler + " to " + _relHandler;
    }


}


