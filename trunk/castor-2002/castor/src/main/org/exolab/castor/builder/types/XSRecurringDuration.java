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
 * $Id$
 */
package org.exolab.castor.builder.types;

import org.exolab.castor.types.TimeDuration;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.Facet;
import org.exolab.javasource.JType;
import org.exolab.javasource.JClass;

import java.text.ParseException;
import java.util.Enumeration;


/**
 * The XML Schema recurring Duration type
 * This Class is used to represent a user DERIVED datatype from recurring Duration
 * It is a mistake to use it as if it was a PRIMITIVE datatype, i.e you always
 * have to set the duration and period facets.
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$ $Date$
 * @see org.exolab.castor.types.RecurringDuration
**/
public class XSRecurringDuration extends XSType {

    private TimeDuration _duration = null;
    private TimeDuration _period = null;
    /**
     * The JType represented by this XSType
     */
    private static final JType JTYPE
        = new JClass("org.exolab.castor.types.RecurringDuration");

    public XSRecurringDuration() {
        super(XSType.RECURRING_DURATION);

    }

    public XSRecurringDuration(TimeDuration duration, TimeDuration period) {
        super(XSType.RECURRING_DURATION);
        _duration=duration;
        _period=period;
    }


    public XSRecurringDuration(String duration, String period)
        throws IllegalArgumentException
    {
        super(XSType.RECURRING_DURATION);
        try {
            _duration = (TimeDuration.parse(duration));
            _period = (TimeDuration.parse(period));
        } catch (java.text.ParseException e) {
            System.out.println("Error in constructor of RecurringDuration: "+e);
            throw new IllegalArgumentException();
        }
    }//--XSRecurringDuration(String,String)

   /**
    * returns the duration facet of this recurringDuration
    * @returns the duration facet of this recurringDuration
    */
    public TimeDuration getDuration() {
        return _duration;
    }

   /**
    * returns the period facet of this recurringDuration
    * @returns the period facet of this recurringDuration
    */
    public TimeDuration getPeriod() {
        return _period;
    }

    /**
     * set the duration facet for this recurringDuration
     * @param duration the period to set
     */

    public void setDuration (TimeDuration duration) {
        _duration = duration;
    }

   /**
    * set the period facet for this recurringDuration
    * @param period the period to set
    */
    public void setPeriod (TimeDuration period) {
        _period = period;
    }

    /**
     * Reads and sets the facets for this XSrecurringDuration
     * @param simpleType the SimpleType containing the facets
     */
    public void setFacets(SimpleType simpleType) {

       //-- copy valid facets
        Enumeration enum = getFacets(simpleType);
        while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();

            try {
                //duration
                if (Facet.DURATION.equals(name))
                    setDuration(TimeDuration.parse(facet.getValue()));
                //period
                else if (Facet.PERIOD.equals(name))
                    setPeriod(TimeDuration.parse(facet.getValue()));
            } catch(ParseException e) {
                System.out.println("Error in setting the facets of recurringDuration");
                e.printStackTrace();
                return;
            }
        }

    } //setFacets

    public JType getJType() {
       return this.JTYPE;
    }

    /**
	 * Returns the Java code neccessary to create a new instance of the
	 * JType associated with this XSType
	 */
	public String newInstanceCode() {
        String result="new "+getJType().getName()+"(";
       //duration and period should never be null
        result = result +"\""+ _duration.toString()+"\", ";
        result = result + "\""+_period.toString()+"\"";
        result = result +");";
        return result;
    }
} //XSRecurringDuration