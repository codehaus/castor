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


import java.util.Hashtable;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.KeyGeneratorDescriptor;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.KeyGeneratorFactory;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.util.Messages;


/**
 * Registry for {@link KeyGenerator} instances (flyweight pattern).
 * 
 * @author <a href="on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 */
public final class KeyGeneratorRegistry
{

    /**
     * Association between pairs (KeyGeneratorDescriptor, PersistenceFactory)
     * and key generator instances.
     */
    private static Hashtable  _keyGens = new Hashtable();


    /**
     * Returns a key generator with the specified description
     * for the specified persistence factory.
     *
     * @param factory The persistence factory
     * @param desc The key generator description
     * @return The {@link KeyGenerator}
     */
    public static KeyGenerator getKeyGenerator( PersistenceFactory factory,
                                                KeyGeneratorDescriptor desc )
            throws MappingException
    {
        KeyGeneratorFactory keyGenFactory;
        KeyGenerator keyGen;
        KeyGenInfo info;

        info = new KeyGenInfo( factory.getFactoryName(), desc );
        keyGen = (KeyGenerator) _keyGens.get( info );
        if ( keyGen == null ) {
            keyGenFactory = KeyGeneratorFactoryRegistry.getKeyGeneratorFactory(
                    desc.getKeyGeneratorFactoryName() );

            if (keyGenFactory != null) {
                keyGen = keyGenFactory.getKeyGenerator( factory, desc.getParams() );
            }
            if ( keyGen == null ) {
                throw new MappingException( Messages.format( "mapping.noKeyGen",
                                            desc.getKeyGeneratorFactoryName() ) );
            }
            _keyGens.put( info, keyGen );
        }    
        return keyGen;
    }

    private static class KeyGenInfo {
        private final String factoryName;
        private final KeyGeneratorDescriptor desc;

        KeyGenInfo( String factoryName, KeyGeneratorDescriptor desc) {
            this.factoryName = factoryName;
            this.desc = desc;
        }

        public boolean equals( Object obj ) {
            KeyGenInfo k;

            if ( obj == null || !(obj instanceof KeyGenInfo) ) {
                return false;
            }

            k = (KeyGenInfo) obj;

            if ( factoryName == null && k.factoryName != null ) {
                return false;
            }
            if ( !factoryName.equals( k.factoryName ) ) {
                return false;
            }

            if ( desc == null && k.desc != null ) {
                return false;
            }
            if ( !desc.equals( k.desc ) ) {
                return false;
            }

            return true;
        }
    }
}
