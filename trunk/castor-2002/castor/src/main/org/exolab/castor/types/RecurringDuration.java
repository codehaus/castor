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
 * $Id$
 * Date         Author          Changes
 * 11/02/2000   Arnaud Blandin  Changed the constructor
 * 26/10/2000   Arnaud Blandin  Created
 */

package org.exolab.castor.types;

import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.exolab.castor.types.TimeDuration;
import org.exolab.castor.types.RecurringDurationBase;

/**
 * Represents recurringDuration utterly
 * a recurringDuration must contain all the fields :
 * (+|-)CCYY-MM-DDThh:mm:ss.sss(Z|(+|-)hh:mm)
 * The validation of the date fields is done in the set methods and follows
 * <a href="http://www.iso.ch/markete/8601.pdf>the ISO8601 Date and Time Format</a>
 * TODO : write a constructor RecurringDuration(java.util.Date date)
 *
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$
**/
public class RecurringDuration extends RecurringDurationBase{

    /** The date format used by the toDate() method */
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /** Set to true and recompile to include debugging code in class. */
    private static final boolean DEBUG = false;


    //Private variables
    private short _century = 0;
    private short _year = 0;
    private short _month = 0;
    private short _day = 0;


    public RecurringDuration() {
    }

    /**
     * returns a recurringDuration with the facets
     * duration and period set up
     * @param duration the TimeDuration representing the duration facet
     * @param period the TimeDuration reprensenting the period facet
     * @return a recurringDuration with the facets
     *          duration and period set up
     */
    public RecurringDuration(TimeDuration duration, TimeDuration period) {
        super(duration,period);
    }

    /**
     * returns a recurringDuration with the facets
     * duration and period set up
     * @param duration the String representing the duration facet
     * @param period the String reprensenting the period facet
     * @return a recurringDuration with the facets
     *          duration and period set up
     */
    public RecurringDuration(String duration, String period)
        throws IllegalArgumentException
    {
        super(duration, period);
    }

    /**
     * set the century field
     * @param century the value to set up
     */
    public void setCentury(short century) {
        String err ="";
        if (century < 0) {
            err = "century : "+century+" must not be a negative value.";
            throw new IllegalArgumentException(err);
        }
        _century = century;
    }

    /**
     * set the Year field
     * Note: 0000 is not allowed
     * @param the year to set up
     */
    public void setYear(short year) {
        String err ="";
        if (year < 0) {
            err = "year : "+year+" must not be a negative value.";
            throw new IllegalArgumentException(err);
        }

        if ( (year ==0) && (_century==0)) {
            err = "0000 is not an allowed year";
            throw new IllegalArgumentException(err);
        }

        _year = year;
    }

    /**
     * set the Month Field
     * @param month the value to set up
     * Note 1<month<12
     */
    public void setMonth(short month) {
        String err ="";
        if (month < 1) {
            err = "month : "+month+" is not a correct value.";
            err+= "\n 1<month<12";
            throw new IllegalArgumentException(err);
        }

         if (month > 12) {
            err = "month : "+month+" is not a correct value.";
            err+= "\n 1<month<12";
            throw new IllegalArgumentException(err);
        }
        _month = month;
    }

    /**
     * set the Day Field
     * @param day the value to set up
     * Note a validation is done on the day field
     */

    public void setDay(short day) {
        String err = "";
        if (day < 1) {
            err = "day : "+day+" is not a correct value.";
            err+= "\n 1<day";
            throw new IllegalArgumentException(err);
        }
        // in february
        if (_month == 2) {
            if (isLeap()) {
                if (day > 29) {
                    err = "day : "+day+" is not a correct value.";
                    err+= "\n day<30 (leap year and month is february)";
                    throw new IllegalArgumentException(err);
                }
            } else if (day > 28) {
                    err = "day : "+day+" is not a correct value.";
                    err+= "\n day<30 (not a leap year and month is february)";
                    throw new IllegalArgumentException(err);
            } //february
        } else if ( (_month == 4) || (_month == 6) ||
                    (_month == 9) || (_month == 11) )
                {
                    if (day > 30) {
                    err = "day : "+day+" is not a correct value.";
                    err+= "\n day<31 ";
                    throw new IllegalArgumentException(err);
                }
        } else if (day > 31) {
                    err = "day : "+day+" is not a correct value.";
                    err+= "\n day<=31 ";
                    throw new IllegalArgumentException(err);
                }

        _day = day;
    }

    /**
     * return true if the year field represents a leap year
     * A specific year is a leap year if it is either evenly
     * divisible by 400 OR evenly divisible by 4 and not evenly divisible by 100
     * @return true if the year field represents a leap year
     */
    public boolean isLeap () {
        int temp = (_century * 100 + _year) ;
        boolean result =( ((temp % 4) == 0) && ((temp % 100) != 0) );
        result = (result || ((temp % 400)==0) );
        return result;
    }

    //Get methods
    public short getCentury() {
        return(_century);
    }

    public short getYear() {
        return(_year);
    }

    public short getMonth() {
        return(_month);
    }

    public short getDay() {
        return(_day);
    }

    /**
     * convert this recurringDuration into a local Date
     * <p>Note : Be aware a the 'local' property of the date i.e <tt>toDate()</tt> will de the
     * conversion between a UTC date and your computer date format.
     * For instance if you have set up your computer time zone on the Pacific Day Time
     * the conversion of <tt>2000-10-20T00:00:00.000</tt> into a <tt>java.util.Date</tt>
     * will return <tt>Thu Oct 19 17:00:00 PDT 2000</tt>
     * @return a local date representing this recurringDuration
     * @throws ParseException
     */
    public Date toDate() throws ParseException {
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        SimpleTimeZone timeZone = new SimpleTimeZone(0,"UTC");

        // Set the time zone
        if ( !isUTC() ) {
            int offset = 0;
            offset = (int) ( (_zoneMinute + _zoneHour*60)*60*1000);
            offset = isZoneNegative() ? -offset : offset;
            timeZone.setRawOffset(offset);
            timeZone.setID(timeZone.getAvailableIDs(offset)[0]);
        }
        df.setTimeZone(timeZone);
        date = df.parse(this.toPrivateString());
        return date;
    }//toDate()

    /**
     * convert this recurringDuration to a string
     * The format is defined by W3C XML Schema draft and ISO8601
     * i.e (+|-)CCYY-MM-DDThh:mm:ss.sss(Z|(+|-)hh:mm)
     * @return a string representing this recurringDuration
     */
    public String toString() {
        return this.toPrivateString();
    }

    /*This method is needed for the toDate() method
     */
    private final String toPrivateString() {

        String result = null;
        String timeZone = null;

        result = String.valueOf(_century);

        String temp = String.valueOf(_year);
        if (temp.length()==1)
            temp = "0"+temp;
        result =  result  + temp;

        temp = String.valueOf(_month);
        if (temp.length()==1)
            temp = "0"+temp;
        result = result + "-" + temp;

        temp=String.valueOf(_day);
        if (temp.length()==1)
            temp = "0"+temp;
        result = result + "-" + temp;

        // no where it is said in the specs that Time can be omitted
        // choose to always keep it
        result = result+"T";
        temp = String.valueOf(_hour);
        if (temp.length()==1)
            temp = "0"+temp;
        result = result  + temp;

        temp = String.valueOf(_minute);
        if (temp.length()==1)
            temp = "0"+temp;
        result = result + ":" + temp;

        temp = String.valueOf(_second);
        if (temp.length()==1)
            temp = "0"+temp;
        result = result + ":" + temp +"."+String.valueOf(_millsecond);

        result = isNegative() ? "-"+result : result;

        // by default we choose not to concat the Z
        if (!isUTC()) {
            temp = String.valueOf(_zoneHour);
            if (temp.length()==1)
                temp = "0"+temp;
            timeZone = temp;

            temp = String.valueOf(_zoneMinute);
            if (temp.length()==1)
                temp = "0"+temp;
            timeZone = timeZone + ":" + temp;

            timeZone = isZoneNegative() ? "-"+timeZone : "+"+timeZone;
            result = result + timeZone;
        }

        //ready for the garbage collector
        temp = null;
        timeZone = null;

        return result;

    }//toString

    /**
     * parse a String and convert it into a recurringDuration
     * @param str the string to parse
     * @return the recurringDuration represented by the string
     * @throws ParseException a parse exception is thrown if the string to parse
     *                        does not follow the rigth format (see the description
     *                        of this class)
     */
    public static RecurringDuration parse(String str) throws ParseException {

        RecurringDuration result = new RecurringDuration();

        //remove if necessary the Z at the end
        if ( str.endsWith("Z"))
            str = str.substring(0,str.indexOf("Z"));

        //isNegative ? is there a time zone ?
        if ( str.startsWith("-") )
            result.setNegative();

        String zoneStr = str.substring(str.length()-6,str.length());
        boolean timeZone = (  ((zoneStr.lastIndexOf("-") !=-1)  ||
                               (zoneStr.lastIndexOf("+") !=-1 )) &&
                               (zoneStr.lastIndexOf(":") !=-1)  );

        if (DEBUG) {
            System.out.println("In parsing method of RecurringDuration");
            System.out.println("String to parse : "+str);
            System.out.println("Negative ? "+result.isNegative());
            String tzone = timeZone?zoneStr:"false";
            System.out.println("Time zone :" +tzone);
        }

        if (!timeZone) zoneStr = null;
        else {
            int index =  str.lastIndexOf("+") != -1? str.lastIndexOf("+") :
                                                         str.lastIndexOf("-");
            str = str.substring(0,index);
        }

        // the 'T' is required
        if (str.indexOf('T') == -1) {
            throw new ParseException("The 'T' element is required",0);
        }
        String date = str.substring(0,str.indexOf("T"));
        String time = str.substring(str.indexOf("T"));

        // proceed date
        StringTokenizer token = new StringTokenizer(date,"-");

        if (token.countTokens() != 3)
            throw new ParseException(str+": Bad date format",0);

        //CCYY
        String temp = token.nextToken();
        if (temp.length() != 4)
            throw new ParseException(str+":Bad year format",1);
        if (DEBUG) {
            System.out.println("Processing century: "+temp.substring(0,2));
        }
        result.setCentury(Short.parseShort( temp.substring(0,2) ));
        if (DEBUG) {
            System.out.println("Processing year: "+temp.substring(2,4));
        }
        result.setYear(Short.parseShort( temp.substring(2,4) ));

        //MM
        temp=token.nextToken();
        if (temp.length() != 2)
            throw new ParseException(str+": Bad month format",5);
        if (DEBUG) {
            System.out.println("Processing month: "+temp);
        }
        result.setMonth(Short.parseShort(temp));

        //DD
        temp=token.nextToken();
        if (temp.length() != 2)
            throw new ParseException(str+":Bad day format",8);
        if (DEBUG) {
            System.out.println("Processing day: "+temp);
        }
        result.setDay(Short.parseShort(temp));

        //proceed Time
        token = new StringTokenizer(time,":");

        if ((token.countTokens() < 3) && (token.countTokens() > 5) )
            throw new ParseException(str+": Bad time format",11);

        //hh
        temp = token.nextToken();
        temp = temp.substring(temp.indexOf("T")+1);
        if (temp.length() != 2)
            throw new ParseException(str+": Bad hour format",11);
         if (DEBUG) {
            System.out.println("Processing hour: "+temp);
        }
         result.setHour(Short.parseShort( temp ));

        //mm
        temp=token.nextToken();
        if (temp.length() != 2)
            throw new ParseException(str+": Bad minute format",14);
        if (DEBUG) {
            System.out.println("Processing minute: "+temp);
        }
        result.setMinute( Short.parseShort(temp));

        //ss
        temp=token.nextToken();
        String milsecond = "0";
        if (temp.indexOf(".") != -1) {
            milsecond = temp.substring(temp.indexOf(".")+1);
            temp = temp.substring(0,temp.indexOf("."));
        }

        if (temp.length() != 2)
            throw new ParseException(str+": Bad second format",17);
        if (DEBUG) {
            System.out.println("Processing seconds: "+temp);
        }
        result.setSecond(Short.parseShort(temp.substring(0,2)),
                         Short.parseShort(milsecond));


        // proceed TimeZone if any
        if (timeZone) {
            if (zoneStr.startsWith("-")) result.setZoneNegative();
            if (zoneStr.length()!= 6)
                throw new ParseException(str+": Bad time zone format",20);
            result.setZone(Short.parseShort(zoneStr.substring(1,3)),
                           Short.parseShort(zoneStr.substring(4,6)));
        }
        else result.isUTC();
        temp = null;
        return result;
    }//parse
} //RecurringDuration