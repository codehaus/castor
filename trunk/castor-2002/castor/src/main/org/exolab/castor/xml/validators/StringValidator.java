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


package org.exolab.castor.xml.validators;

import org.exolab.castor.xml.*;

/**
 * The String Validation class
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class StringValidator implements TypeValidator {
    
    
    private String  fixed      = null;
    
    private String  regex      = null;
    
    private boolean required   = false;
    
    private int     minLength  = 0;
    
    private int     maxLength  = -1;
    
    
    /**
     * Creates a new StringValidator with no restrictions
    **/
    public StringValidator() {
        super();
    } //-- StringValidator
    
    
    /**
     * Sets the fixed value in which all valid Strings must match.
     * @param fixedValue the fixed value that all Strings must match
    **/
    public void setFixedValue(String fixedValue) {
        this.fixed = fixedValue;
    } //-- setFixedValue
    
    /**
     * Sets the maximum length of that a valid String must be.
     * To remove the max length facet, use a negative value.
     * @param maxLength the maximum length for valid Strings
    **/
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    } //-- setMaxLength
    
    /**
     * Sets the minimum length that valid Strings must be
     * @param minLength the minimum length that valid Strings must be
    **/
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    } //-- setMinLength
    
    /**
     * Sets the Regular Expression that Strings validated with this
     * String Validator must match. (Currently does nothing)
    **/
    public void setRegEx(String regex) {
        this.regex = regex;
    } //-- setRegEx
    
    /** 
     * Sets whether or not a String is required (non null)
     * @param required the flag indicating whether Strings are required
    **/
    public void setRequired(boolean required) {
        this.required = required;
    } //-- setRequired
    
    public void validate(String value) 
        throws ValidationException 
    {
        
        if (value == null) {
            if (required) {
                String err = "this is a required field and cannot be null.";
                throw new ValidationException(err);
            }
        }
        else {
            
            if (fixed != null) {
                if (!fixed.equals(value)) {
                    String err = "strings of this type must be equal to the "
                        + "fixed value of " + fixed;
                    throw new ValidationException(err);
                }
            }
            int len = value.length();
            if ((minLength > 0) && (len < minLength)) {
                String err = "strings of this type must have a minimum "
                    + "length of " + minLength;
                throw new ValidationException(err);
            }
            else if ((maxLength >= 0) && (len > maxLength)) {
                String err = "strings of this type must have a maximum "
                    + "length of " + maxLength;
                throw new ValidationException(err);
            }
        }
    } //-- validate
    
    /**
     * Validates the given Object
     * @param object the Object to validate
    **/
    public void validate(Object object)
        throws ValidationException 
    {
        if (object == null) {
            if (required) {
                String err = "this is a required field and cannot be null.";
                throw new ValidationException(err);
            }
        }
        validate(object.toString());
    } //-- validate
    
} //-- StringValidator