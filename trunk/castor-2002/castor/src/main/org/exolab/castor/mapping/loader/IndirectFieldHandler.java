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


package org.exolab.castor.mapping.loader;


import org.exolab.castor.mapping.IntegrityException;
import org.exolab.castor.mapping.FieldHandler;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class IndirectFieldHandler
    implements FieldHandler
{


    private FieldHandler   _indirect;


    private FieldHandler   _handler;


    private boolean        _required;


    public IndirectFieldHandler( FieldHandler handler, FieldHandler indirect, boolean required )
    {
        if ( indirect == null || handler == null )
            throw new IllegalArgumentException( "Argument 'handler' or 'indirect' is null" );
        _handler = handler;
        _indirect = indirect;
        _required = required;
    }


    public FieldHandler getHandler()
    {
        return _indirect;
    }


    public Object getValue( Object object )
    {
        object = _indirect.getValue( object );
        if ( object == null )
            return null;
        return _handler.getValue( object );
    }
    
    
    public void setValue( Object object, Object value )
    {
        Object ref;

        // XXX If value is null in some cases create the indirect object, in others don't        
        ref = _indirect.getValue( object );
        if ( ref == null ) {
            ref = _indirect.newInstance( object );
            _indirect.setValue( object, ref );
        }
        _handler.setValue( ref, value );
    }


    public void checkIntegrity( Object object )
        throws IntegrityException
    {
        Object ref;

        ref = _indirect.getValue( object );
        if ( ref == null ) {
            if ( _required )
                throw new IntegrityException( "mapping.requiredField",
                                              object.getClass().getName(), _indirect.toString() );
        } else
            _handler.checkIntegrity( ref );
    }


    public Object newInstance( Object object )
        throws IllegalStateException
    {
        return _handler.newInstance( object );
    }


    public String toString()
    {
        return _handler + " on " + _indirect;
    }
    
    
}

