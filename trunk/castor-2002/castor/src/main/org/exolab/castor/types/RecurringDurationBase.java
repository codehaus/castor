
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
import java.util.StringTokenizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.exolab.castor.types.TimeDuration;
/**
 * the base class for recurring Duration types
 * This base class contains all the time fields (including the time zone ones)
 * and also the facets period and duration
 * The validation of the time fields is done in the set methods and follows
 * <a href="http://www.iso.ch/markete/8601.pdf>the ISO8601 Date and Time Format</a>
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @see RecurringDuration
 * @see Time
 * @see TimeInstant
 * @see TimePeriod
 */
public class RecurringDurationBase {

    /**
     * the facets of recurringDuration
     */
    protected TimeDuration _period = new TimeDuration();
    protected TimeDuration _duration = new TimeDuration();

    //Protected variables
    protected short _hour = 0;
    protected short _minute = 0;
    protected short _second = 0;
    protected short _millsecond = 0;
    protected short _zoneHour = 0;
    protected short _zoneMinute = 0;
    protected boolean _UTC = false;
    protected boolean _zoneNegative = false;
    protected boolean _isNegative = false;

    /**
     * the constructor for a Recurring Duration
     */
    public RecurringDurationBase() {
    }

    /**
     * returns a recurringDuration with the facets
     * duration and period set up
     * @param duration the TimeDuration representing the duration facet
     * @param period the TimeDuration reprensenting the period facet
     * @return a recurringDuration with the facets
     *          duration and period set up
     */
    public RecurringDurationBase(TimeDuration duration, TimeDuration period) {
        this.setDuration(duration);
        this.privateSetPeriod(period);
    }

    /**
     * returns a recurringDuration with the facets
     * duration and period set up
     * @param duration the String representing the duration facet
     * @param period the String reprensenting the period facet
     * @return a recurringDuration with the facets
     *          duration and period set up
     */
    public RecurringDurationBase(String duration, String period)
        throws IllegalArgumentException
    {
        try {
            this.setDuration(TimeDuration.parse(duration));
            this.privateSetPeriod(TimeDuration.parse(period));
        } catch (java.text.ParseException e) {
            System.out.println("Error in constructor of RecurringDuration: "+e);
            throw new IllegalArgumentException();
        }
    }

    /**
     * set the period facet this recurringDuration
     * this method is used to avoid calling the setPeriod method
     * of subclasses
     * @param period the period to set
     */
    private void privateSetPeriod (TimeDuration period) {
        _period = period;
    }

    /**
     * set the period facet for this recurringDuration
     * @param period the period to set
     */
    public void setPeriod (TimeDuration period) {
        privateSetPeriod(period);
    }


    /**
     * set the duration facet for this recurringDuration
     * @param duration the period to set
     */
     public void setDuration (TimeDuration duration) {
        _duration = duration;
    }
    /**
     * set the hour field for this recurringDuration
     * @param hour the hour to set
     */
    public void setHour(short hour) {
        String err = "";
        if (hour > 23) {
            err = "the hour field ("+hour+")must be strictly lower than 24";
            throw new IllegalArgumentException(err);
        }
        _hour = hour;
    }

    /**
     * set the minute field for this recurringDuration
     * @param minute the minute to set
     */
    public void setMinute(short minute) {
         String err = "";
         if (minute > 59) {
            err = "the minute field ("+minute+")must be lower than 59";
            throw new IllegalArgumentException(err);
        }
        _minute = minute ;
    }

    /**
     * set the second field for this recurringDuration
     * @param second the second to set
     * @param millsecond the millisecond to set
     */
    public void setSecond(short second,short millsecond) {
         String err = "";
         if (second > 60) {
            err = "the second field ("+second+")must be lower than 60";
            throw new IllegalArgumentException(err);
        }
        _second = second;
        _millsecond = millsecond;
    }

    /**
     * set the time zone fields for this recurringDuration
     * @param hour the time zone hour to set
     * @param minute the time zone minute to set
     */
    public void setZone(short hour, short minute) {
         String err = "";
         if (hour > 23) {
            err = "the zone hour field ("+hour+")must be strictly lower than 24";
            throw new IllegalArgumentException(err);
        }
        _zoneHour = hour;
         if (minute > 59) {
            err = "the minute field ("+minute+")must be lower than 59";
            throw new IllegalArgumentException(err);
        }
        _zoneMinute = minute;
    }

    /**
     * set the negative field to true
     */
    public void setNegative() {
        _isNegative = true;
    }


    /**
     * set the time zone negative field to true
     */

    public void setZoneNegative() {
        _zoneNegative = true;
    }


    /**
     * set the UTC field to true
     */
    public void setUTC() {
        _UTC = true;
    }

    //Get methods
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
     * return true if this recurring Duration type is UTC
     * i.e if there is no time zone.
     * @returns true if this recurringDuration type is UTC
     *          else false
     */
    public boolean isUTC() {
        _UTC = ( (_zoneHour == 0) && (_zoneMinute == 0) );
        return(_UTC);
    }

    public boolean isNegative() {
        return _isNegative;
    }

    public boolean isZoneNegative() {
        return _zoneNegative;
    }


}