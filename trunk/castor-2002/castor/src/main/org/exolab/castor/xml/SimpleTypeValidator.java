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

package org.exolab.castor.xml;

/**
 * A class for defining simple rules used for validating a content model
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class SimpleTypeValidator implements TypeValidator {
    
    /**
     * The minimum occurance that the node must appear
    **/
    private int minOccurs = 0;
    
    /**
     * The maximum occurance that the node must appear
    **/
    private int maxOccurs = -1;
    
    /**
     * The type validate to delegate validation to
    **/
    private TypeValidator validator = null;
    
    /**
     * Creates a default SimpleTypeValidator
    **/
    public SimpleTypeValidator() {
        super();
    } //-- SimpleTypeValidator
    
    /**
     * Creates a SimpleTypeValidator using the given TypeValidator for
     * delegating validation
    **/
    public SimpleTypeValidator(TypeValidator validator) {
        super();
        this.validator = validator;
    } //-- SimpleTypeValidator

    /**
     * Sets the maximum occurance that the described field may occur
     * @param maxOccurs the maximum occurance that the descibed field 
     * may occur.
    **/
    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    } //-- setMaxOccurs

    /**
     * Sets the minimum occurance that the described field may occur
     * @param minOccurs the minimum occurance that the descibed field 
     * may occur.
    **/
    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    } //-- setMinOccurs
    
    /**
     * Validates the given Object
     * @param object the Object to validate
    **/
    public void validate(Object object)
        throws ValidationException {
            if (validator != null) validator.validate(object);
    } //-- validate
    
} //-- SimpleTypeValidator