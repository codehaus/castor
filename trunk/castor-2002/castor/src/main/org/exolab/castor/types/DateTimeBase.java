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
 * Copyright 2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 * Date         Author          Changes
 * 05/22/2001   Arnaud Blandin  Created
 */

package org.exolab.castor.types;

import org.exolab.castor.xml.ValidationException;

import java.util.Date;
import java.util.StringTokenizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <p>The base class for date/time XML Schema types.
 * <p>The validation of the date/time fields is done in the set methods and follows
 * <a href="http://www.iso.ch/markete/8601.pdf">the ISO8601 Date and Time Format</a>.
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$
 * @see DateTime
 * @see Date
 * @see Time
 */
public abstract class DateTimeBase
    implements java.io.Serializable {

   /////////////////////////////Private members////////////////////////////////
   /**
    * The century field
    */
   private short _century = 0;
    /**
     * The year field
     */

   private short _year = 0;
   /**
    * The month field
    */

   private short _month = 0;
   /**
    * The day field
    */
   private short _day = 0;

    /**
     * the hour field
     */
    private short _hour = 0;
    /**
     * the minute field
     */
    private short _minute = 0;
    /**
     * the second field
     */
    private short _second = 0;
    /**
     * the millsecond field
     */
    private short _millsecond = 0;
    /**
     * the time zone hour field
     */
    private short _zoneHour = 0;
    /**
     * the time zone minute field
     */
    private short _zoneMinute = 0;
    /**
     * true if this date/time type is UTC related
     */
    private boolean _UTC = false;
    /**
     * true if the time zone is negative
     */
    private boolean _zoneNegative = false;
    /**
     * true if this date/time type is negative
     */
    private boolean _isNegative = false;
    ////////////////////////////////////////////////////////////////////////////

    //////////////////////////Setter methods////////////////////////////////////
    /**
     * set the century field
     * @param century the value to set up
     */
    public void setCentury(short century) {
        String err ="";
        if (century < -1) {
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
    public void setYear(short year)
        throws OperationNotSupportedException
    {
        String err ="";
        if (year < -1) {
            err = "year : "+year+" must not be a negative value.";
            throw new IllegalArgumentException(err);
        }
        else if ( (year == -1) && (_century != -1) ) {
            err = "year can not be omitted if century is not omitted.";
            throw new IllegalArgumentException(err);
        }
        else if ( (year ==0) && (_century==0)) {
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
    public void setMonth(short month)
        throws OperationNotSupportedException
    {
        String err ="";
        if (month == -1) {
            if (_century != -1) {
                 err = "month cannot be omitted if the previous component is not omitted.\n"+
                       "only higher level components can be omitted.";
                 throw new IllegalArgumentException(err);
            }
        }
        else if (month < 1) {
            err = "month : "+month+" is not a correct value."
                  +"\n 1<month<12";
            throw new IllegalArgumentException(err);
        }

        else if (month > 12) {
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

    public void setDay(short day)
        throws OperationNotSupportedException
    {
        String err = "";
        if  (day == -1) {
            if (_month != -1) {
                 err = "day cannot be omitted if the previous component is not omitted.\n"+
                       "only higher level components can be omitted.";
                 throw new IllegalArgumentException(err);
            }
        }
        else if (day < 1) {
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

    /**
     * set the hour field for this date/time type.
     * @param hour the hour to set
     * @throws OperationNotSupportedException this exception is thrown when
     *         changing the value of the hour field is not allowed
     */
    public void setHour(short hour)
        throws OperationNotSupportedException
    {
        if (hour > 23) {
            String err = "the hour field ("+hour+")must be strictly lower than 24";
            throw new IllegalArgumentException(err);
        }
        _hour = hour;
    }

    /**
     * set the minute field for this date/time type.
     * @param minute the minute to set.
     * @throws OperationNotSupportedException this exception is thrown when
     *         changing the value of the minute field is not allowed
     */
    public void setMinute(short minute)
        throws OperationNotSupportedException
    {
         if (minute > 59) {
            String err = "the minute field ("+minute+")must be lower than 59.";
            throw new IllegalArgumentException(err);
        }
        _minute = minute ;
    }

    /**
     * set the second field for this date/time type
     * @param second the second to set
     * @param millsecond the millisecond to set
     * @throws OperationNotSupportedException this exception is thrown when
     *         changing the value of the second field is not allowed
     */
    public void setSecond(short second,short millsecond)
        throws OperationNotSupportedException
     {
         if (second > 60) {
            String err = "the second field ("+second+")must be lower than 60";
            throw new IllegalArgumentException(err);
        }
        _second = second;
        _millsecond = millsecond;
    }

    /**
     * set the time zone fields for this date/time type.
     * A call to this method means that the date/time type
     * used is UTC.
     * @param hour the time zone hour to set
     * @param minute the time zone minute to set
     * @throws OperationNotSupportedException this exception is thrown when
     *         changing the value of the time zone fields is not allowed
     */
    public void setZone(short hour, short minute)
        throws OperationNotSupportedException
    {
         if (hour > 23) {
            String err = "the zone hour field ("+hour+")must be strictly lower than 24";
            throw new IllegalArgumentException(err);
        }
        _zoneHour = hour;
         if (minute > 59) {
            String err = "the minute field ("+minute+")must be lower than 59";
            throw new IllegalArgumentException(err);
        }
        _zoneMinute = minute;
        //any call to setZone means that you
        //use the date/time you use is UTC
        setUTC();
    }

    /**
     * Sets all the fields by reading the values in an array
     * @param values an array of shorts with the values
     */
     public abstract void setValues(short[] values);

    /**
     * set the negative field to true
     */
    public void setNegative() {
        _isNegative = true;
    }

    /**
     * Returns a java.util.Date that represents
     * the XML Schema Date datatype
     */
     public abstract Date toDate();


    /**
     * set the time zone negative field to true
     * @throws OperationNotSupportedException this exception is thrown when
     *         changing the time zone fields is not allowed
     */

    public void setZoneNegative()
        throws OperationNotSupportedException
    {
        _zoneNegative = true;
    }

    /**
     * set the UTC field.
     */
    public void setUTC() {
        _UTC = true;
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////Getter methods//////////////////////////////////////
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

    public short getHour() {
        return(_hour);
    }

    public short getMinute() {
        return(_minute);
    }

    public short getSeconds() {
        return(_second);
    }

    public short getMilli() {
        return(_millsecond);
    }

    public short getZoneHour() {
        return(_zoneHour);
    }

    public short getZoneMinute() {
        return(_zoneMinute);
    }

    /**
     * returns an array of short with all the fields that describe
     * a date/time type.
     * @return  an array of short with all the fields that describe
     * a date/time type.
     */
    public abstract short[] getValues();
////////////////////////////////////////////////////////////////////////////////

    /**
     * return true if this date/time type is UTC.
     * A date/time type is UTC if a 'Z' appears at the end of the
     * lexical representation type or if it contains a time zone.
     * @returns true if this recurringDuration type is UTC
     *          else false
     */
    public boolean isUTC() {
        return(_UTC);
    }

    public boolean isNegative() {
        return _isNegative;
    }

    public boolean isZoneNegative() {
        return _zoneNegative;
    }

     /**
     * Override the java.lang.equals method
     * @see equal
     */
     /*public boolean equals(Object object) {
        if (object instanceof DateTimeBase) {
            try {
                return equal( (DateTimeBase) object);
            } catch (ValidationException e) {
                e.printStackTrace();
                return false;
            }
        }
        else return false;
    }*/

    //------------------------TO IMPLEMENT CORRECTLY ---------------------------
    /**
     * <p> Returns true if the present instance of date/time type is equal to
     * the parameter.
     * <p> The equals relation is the following :
     * <tt>P equals Q iff each field of rd1 is equal to the corresponding field of rd2 </tt>
     * @param reccD the recurring duration to compare with the present instance
     * @return true if the present instance is equal to the parameter false if not
     */
    /* public boolean equal(RecurringDurationBase reccD) throws ValidationException
     {
        boolean result = false;
         if ( !(this.getPeriod().equals(reccD.getPeriod())) ||
             !(this.getDuration().equals(reccD.getDuration())) )
        {
                String err = " Recurring Duration which have different values "
                            +"for the duration and period can not be compared";
                throw new ValidationException(err);
        }
        result = (this.getHour() == reccD.getHour());
        result = result && (this.getMinute() == reccD.getMinute());
        result = result && (this.getSeconds() == reccD.getSeconds());
        result = result && (this.getMilli() == reccD.getMilli());
        result = result && (this.isNegative() == this.isNegative());
        if (!reccD.isUTC()) {
            result = result && (!this.isUTC());
            result = result && (this.getZoneHour() == reccD.getZoneHour());
            result = result && (this.getZoneMinute() == reccD.getZoneMinute());
        }
        return result;
    }//equals*/

    /**
     * <p>Returns true if the present instance of RecurringDurationBase is greater than
     * the parameter
     * <p>Note : the order relation follows the W3C XML Schema draft i.e
     * <tt>rd1 < rd2 iff rd2-rd1>0</tt>
     * @param reccD the recurring duration base to compare with the present instance
     * @return true if the present instance is the greatest, false if not
     */
    /*public boolean isGreater(RecurringDurationBase reccD) throws ValidationException
    {
        boolean result = false;
        if ( !(this.getPeriod().equals(reccD.getPeriod())) ||
             !(this.getDuration().equals(reccD.getDuration())) )
        {
                String err = " Recurring Duration which have different values "
                            +"for the duration and period can not be compared";
                throw new ValidationException(err);
        }
        short[] val_this = this.getValues();
        short[] val_reccD = reccD.getValues();
        int i = 0;
        while ( (result != true) || (i< val_this.length) ) {
            result = val_this[i] > val_reccD[i];
            if ( val_this[i] < val_reccD[i])
                return false;
            i++;
        }
        return result;
    }//isGreater*/
    //--------------------------------------------------------------------------
}//-- DateTimeBase