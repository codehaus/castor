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
 * Copyright 1999-2001 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema.util;


/**
 * A class used for "guessing" the proper datatype of
 * an XML attribute or an XML element with simpleContent.
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class DatatypeHandler {

    /**
     * The name of the XML Schema boolean type
    **/
    public static final String BOOLEAN_TYPE = "boolean";
    
    /**
     * The name of the XML Schema double type
    **/
    public static final String DOUBLE_TYPE = "double";
    
    /**
     * The name of the XML Schema integer type
    **/
    public static final String INTEGER_TYPE = "integer";

    /**
     * The name of the XML Schema string type
    **/
    public static final String STRING_TYPE = "string";
    
    
    private static final String TRUE    = "true";
    private static final String FALSE   = "false";
    
    
    /**
     * Creates a new DatatypeHandler
     *
    **/
    private DatatypeHandler() {
        super();
    } //-- DatatypeHandler

    /**
     * Guesses the datatype for the given value. When the type
     * cannot be determined, it simply defaults to 
     * DatatypeHandler.STRING_TYPE.
     * <BR />
     * <B>Note:</B> This may be a slow process.
     *
     * @param value the value to determine the type for
     * @return the type that the value may be
    **/
    public static String guessType(String value) {
        if (value == null) return null;

        //-- check for integer
        try {
            Integer.parseInt(value);
            return INTEGER_TYPE;
        }
        catch(NumberFormatException nfe) {};
        
        //-- check for double
        try {
            Double.parseDouble(value);
            return DOUBLE_TYPE;
        }
        catch(NumberFormatException nfe) {};
        
        //-- check for boolean
        if (value.equals(TRUE) || value.equals(FALSE)) {
            return BOOLEAN_TYPE;
        }
            
        //-- when all else fails :-)
        return STRING_TYPE;
    } //-- guessType
    
} //-- DatatypeHandler
