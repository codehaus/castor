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
 * Copyright 1999-2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id $
 */
package org.exolab.castor.types;
import java.text.ParseException;
import java.util.StringTokenizer;

import org.exolab.castor.types.TimePeriod;
/**
 * Describe an XML schema Date
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$
 */

public class Date extends TimePeriod {

     /** Set to true and recompile to include debugging code in class. */
    private static final boolean DEBUG = false;

    public Date() {
        super();
        try{
            setPeriod(TimeDuration.parse("P1D"));
        } catch (ParseException e) {
            System.out.println("Error in constructor Date");
            System.out.println(e);
        }
    }

    /*TODO disallow access to time methods
           write toDate(), toString()*/

     public String toString() {

        String result = null;

        result = String.valueOf(this.getCentury());

        String temp = String.valueOf(this.getYear());
        if (temp.length()==1)
            temp = "0"+temp;
        result =  result  + temp;

        temp = String.valueOf(this.getMonth());
        if (temp.length()==1)
            temp = "0"+temp;
        result = result + "-" + temp;

        temp=String.valueOf(this.getDay());
        if (temp.length()==1)
            temp = "0"+temp;
        result = result + "-" + temp;


        result = isNegative() ? "-"+result : result;


        return result;

    }//toString

    /**
     * parse a String and convert it into a recurringDuration
     */
    public static Date parseDate(String str) throws ParseException {

        //TODO check the length of the string
        Date result = new Date();

        if ( str.startsWith("-") )
            result.setNegative();


        if (DEBUG) {
            System.out.println("In parsing method of Date");
            System.out.println("String to parse : "+str);
            System.out.println("Negative ? "+result.isNegative());
        }



        // proceed date
        StringTokenizer token = new StringTokenizer(str,"-");

        if (token.countTokens() != 3)
            throw new ParseException(str+": Bad date format",0);

        String temp = token.nextToken();
        if (temp.length() != 4)
            throw new ParseException(str+": Bad year format",1);
        if (DEBUG) {
            System.out.println("Processing century: "+temp.substring(0,2));
        }
        result.setCentury(Short.parseShort( temp.substring(0,2) ));
        if (DEBUG) {
            System.out.println("Processing year: "+temp.substring(2,4));
        }
        result.setYear(Short.parseShort( temp.substring(2,4) ));

        temp=token.nextToken();
        if (temp.length() != 2)
            throw new ParseException(str+": Bad month format",5);
        if (DEBUG) {
            System.out.println("Processing month: "+temp);
        }
        result.setMonth(Short.parseShort(temp));

        temp=token.nextToken();
        if (temp.length() != 2)
            throw new ParseException(str+": Bad day format",8);
        if (DEBUG) {
            System.out.println("Processing day: "+temp);
        }
        result.setDay(Short.parseShort(temp));

        temp = null;
        return result;
    }//parse


}