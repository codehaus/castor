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


import java.util.Properties;
import org.exolab.castor.mapping.KeyGeneratorDescriptor;


/**
 * Describes the properties of a key generator for a given class, 
 * with resolved alias and parameters.
 *
 * @author <a href="on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 */
public final class KeyGeneratorDescriptorImpl implements KeyGeneratorDescriptor
{

    private final String _keyGenFactoryName;


    private final Properties _params;


    public KeyGeneratorDescriptorImpl( String keyGenFactoryName, Properties params ) {
        _keyGenFactoryName = keyGenFactoryName;
        _params = params;
    }

    /**
     * Returns the name of the key generator factory.
     *
     * @return Key generator factory name
     */
    public String getKeyGeneratorFactoryName() {
        return _keyGenFactoryName;
    }


    /**
     * Returns the key generator parameters.
     *
     * @return key generator parameters.
     */
    public Properties getParams() {
        return _params;
    }

    public boolean equals( Object obj ) {
        KeyGeneratorDescriptorImpl k;

        if ( obj == null || !(obj instanceof KeyGeneratorDescriptorImpl) ) {
            return false;
        }

        k = (KeyGeneratorDescriptorImpl) obj;

        if ( _keyGenFactoryName == null && k._keyGenFactoryName != null ) {
            return false;
        }
        if ( !_keyGenFactoryName.equals( k._keyGenFactoryName ) ) {
            return false;
        }

        if ( _params == null && k._params != null ) {
            return false;
        }
        if ( !_params.equals( k._params ) ) {
            return false;
        }

        return true;
    }
}

