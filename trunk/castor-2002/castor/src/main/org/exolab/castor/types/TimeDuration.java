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

/**
 * Represents timeDuration utterly
 * TODO List :
 * <ul>
 *      <li> Check the validity of the fields</li>
 *      <li> write the constructor TimeDuration (long l)</li>
 *      <li> add a setValue(long l) method</li>
 *      <li> optimize the parse method </li>
 *      <li> write the full Javadoc and follow Exolab Conventions </li>
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$
**/
public class TimeDuration  {

     /** Set to true and recompile to include debugging code in class. */
    private static final boolean DEBUG = false;

    //Private variables
    private short _year = 0;
    private short _month = 0;
    private short _day = 0;
    private short _hour = 0;
    private short _minute = 0;
    private short _second = 0;
    private boolean _isNegative = false;

    public TimeDuration() {
    }

    /**
     * Set the fields to the proper value
     * TODO
     */
    public TimeDuration(long l) {
    }

    //Set methods
    public void setYear(short year) {
        _year = year;
    }

    public void setMonth(short month) {
        _month = month;
    }

    public void setDay(short day) {
        _day = day;
    }

    public void setHour(short hour) {
        _hour = hour;
    }

    public void setMinute(short minute) {
        _minute = minute ;
    }

    public void setSeconds(short second) {
        _second = second;
    }

    public void setNegative() {
        _isNegative = true;
    }

    //Get methods
     public short getYear() {
        return(_year);
    }

    public short getMonth() {
        return(_month);
    }

    public short getDay() {
        return(_day);
    }

    public short getHour() {
        return(_hour);
    }

    public short getMinute() {
        return(_minute);
    }

    public short getSeconds() {
        return(_second);
    }

    public boolean isNegative() {
        return _isNegative;
    }

   /**
    * Convert a timeDuration to a long
    * which represents the duration in milliseconds
    */
    public long toLong() {
        long result = 0;
        //30.416 days in a month--> to change
        //Horner method
        result = ( (long) ( ((((( (_year*12L) +_month )
                                    +_day)*730L  //730 = 24 (#hours in a day) * 30.416(#days in a month)
                                    +_hour)*60L
                                    +_minute)*60L
                                    +_second)*1000L ));

        result = isNegative()? -result : result;
        return result;
    }

    /**
     * Convert a timeDuration to a String
     * conforming to ISO8601 and Schema specs
     */
     public String toString() {
        String result = null;
        result = "P"+_year+"Y";
        result = result + _month+"M";
        result = result + _day+"D";
        boolean isThereTime = ( (_hour != 0) || (_minute != 0) || (_second != 0) );
        if (isThereTime) {
            result = result + "T";
            result = result + _hour +"H";
            result = result + _minute +"M";
            result = result + _second +"S";
        }
        result = (_isNegative) ? "-" + result : result;
        return result;
    } //toString

    /**
     * <p>Parse the given string and return a time duration
     * which represents this string
     * @param String str the string to parse
     * @return a TimeDuration instance which represent the string
     * @throws ParseException
     */
    public static TimeDuration parse (String str) throws ParseException {

        TimeDuration result = new TimeDuration();
        int toProceed = 0;
        int lastProceed = 0;
        String temp = null;
        boolean isThereTime = (str.indexOf("T") != -1);
        //Remove the '-' if so
        if (str.startsWith("-")) {
            result.setNegative();
            str=str.substring(1);
        }

        if (DEBUG) {
            System.out.println("in parse method of TimeDuration");
            System.out.println("String to parse : "+str);
            System.out.println("Time ? :"+isThereTime);
            System.out.println("isNegative? : "+result.isNegative());
        }

        //Test if the string is correct
        if (!str.startsWith("P"))
            throw new ParseException(str+": A time duration must begin with a P",0);

        if ( (!isThereTime) && ((str.indexOf("H") != -1) ||
                                ( (str.lastIndexOf("M") != -1) &&
                                  (str.lastIndexOf("M") > str.indexOf("T")) )||
                                (str.indexOf("S") != -1)))
            throw new ParseException(str+": the time duration must contain a T",0);
        if ( (isThereTime) && ( str.substring(str.indexOf("T")+1)).equals("") )
            throw new ParseException(str+": the 'T' must be omitted",0);

        //proceed date
        lastProceed = str.indexOf("P");
        toProceed = str.indexOf("Y");
        if (toProceed != -1) {
            if (DEBUG) {
                System.out.println("Year in process");
            }
            temp = str.substring(lastProceed+1,toProceed);
            result.setYear(Short.parseShort(temp) );
            lastProceed = str.indexOf("Y");
        }
        toProceed = str.indexOf("M");
        if ( (toProceed != -1) && (toProceed < str.indexOf("T")) )
        {
             if (DEBUG) {
                System.out.println("Month in process");
            }
            temp = str.substring(lastProceed+1,toProceed);
            result.setMonth(Short.parseShort(temp));
            lastProceed = str.indexOf("M");
        }
        toProceed = str.indexOf("D");
        if (toProceed != -1) {
             if (DEBUG) {
                System.out.println("Day in process");
            }
             temp = str.substring(lastProceed+1,toProceed);
             result.setDay(Short.parseShort(temp));
        }

        //proceed time
        if (isThereTime) {
            toProceed = str.indexOf("H");
            if (toProceed != -1) {
                 if (DEBUG) {
                    System.out.println("Hour in process");
                 }
                temp = str.substring(str.indexOf("T")+1,toProceed);
                result.setHour(Short.parseShort(temp));
                lastProceed = str.indexOf("H");
            }
            toProceed = str.lastIndexOf("M");
            if ( (toProceed != -1) &&
                 (toProceed > str.indexOf("T")) )
            {
                 if (DEBUG) {
                    System.out.println("Minute in process");
                 }
                temp = str.substring(lastProceed+1,str.lastIndexOf("M"));
                result.setMinute( Short.parseShort(temp));
                lastProceed = str.lastIndexOf("M");
            }
            toProceed = str.indexOf("S");
            if (toProceed != -1) {
                 if (DEBUG) {
                    System.out.println("Second in process");
                 }
                temp = str.substring(lastProceed+1,toProceed);
                result.setSeconds(Short.parseShort(temp));
           }
        }

       return result;

    } //parse
}




