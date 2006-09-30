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
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */
package org.exolab.castor.tests.framework;

import java.util.Random;
import java.util.Vector;
import java.util.ArrayList;
import org.exolab.castor.types.*;

import java.math.BigDecimal;

/**
 * Assists in the generation of random instances of a given object model.
 *
 * @author <a href="mailto:gignoux@intalio.com">Sebastien Gignoux</a>
 * @version $Revision$ $Date: 2006-04-13 06:47:36 -0600 (Thu, 13 Apr 2006) $
 */
public class RandomHelper {

    /**
     * The seed which was used to initialize the pseudo-random number generator.
     */
    private static long _seed;

    /**
     * The pseudo random number generator.
     */
    private static Random _rand;

    static {
        _seed = System.currentTimeMillis();
        _rand = new Random(_seed);
    }

    /**
     * The maximum length of a string to be generated by rndString().
     */
    private static final int MAX_STRING_LENGTH = 50;

    /**
     * The maximum length of a collection (like a Vector) generated by rndString().
     */
    private static final int MAX_COLLECTION_LENGTH = 50;

    /**
     * List of the characters that can be used to compose a string.
     */
    private static final String PRINTABLE_CHAR = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_:.,=+~!@#$%^&*()[]{}\\|?";

    /**
     * Returns a populated array of int of random length.
     *
     * @param array An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a populated array of String of random length.
     */
    public static int[] getRandom(int[] array, Class c) {
        int size = _rand.nextInt(MAX_COLLECTION_LENGTH);

        int[] ret = new int[size];
        for (int i=0; i<size; ++i) {
            ret[i] = _rand.nextInt();
        }

        return ret;
    }

    /**
     * Returns a populated array of String of random length.
     *
     * @param array An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a populated array of String of random length.
     */
    public static String[] getRandom(String[] array, Class c) {
        int size = _rand.nextInt(MAX_COLLECTION_LENGTH);

        String[] newArray = new String[size];
        for (int i=0; i<size; ++i) {
            newArray[i] = getRandom(new String(), null);
        }

        return newArray;
    }

    /**
     * Creates a populated array of type c of random length. If the class to put
     * into the vector implement CastorTestable, randomizeFields() will be
     * called on the objects.
     *
     * @param array An unused parameter, used only for polymorphism.
     * @param c the type of object to put in the array
     * @return a populated array of random length.
     * @throws InstantiationException if the class cannot be instantiated.
     * @throws IllegalAccessException if the class cannot be accessed.
     */
    public static Object[] getRandom(Object[] array, Class c)
        throws InstantiationException, IllegalAccessException {

        int size = _rand.nextInt(MAX_COLLECTION_LENGTH);
        Object[] newArray = new Object[size];

        for (int i=0; i<size; ++i) {
            newArray[i] = c.newInstance();
            if (CastorTestable.class.isAssignableFrom(c)) {
                ((CastorTestable)newArray[i]).randomizeFields();
            }
        }

        return newArray;
    }


    /**
     * Returns a populated vector of random length. If the class to put into the
     * vector implement CastorTestable, randomizeFields() will be called on the
     * objects.
     *
     * @param vect the vector to populate, if null a new Vector will be created.
     * @param c the type of object to put in the vector.
     * @return a populated Vector of random length.
     * @throws InstantiationException if the class cannot be instantiated.
     * @throws IllegalAccessException if the class cannot be accessed.
     */
    public static Vector getRandom(Vector vect, Class c)
        throws InstantiationException, IllegalAccessException {

        Vector vector = (vect != null) ? vect : new Vector();

        int size = _rand.nextInt(MAX_COLLECTION_LENGTH);
        for (int i=0; i<size; ++i) {
            Object object = c.newInstance();
            vector.add(object);
            if (CastorTestable.class.isAssignableFrom(c)) {
                ((CastorTestable)object).randomizeFields();
            }
        }

        return vector;
    }

    /**
     * Returns a populated ArrayList of random length. If the class of the
     * object contained into the vector implement CastorTestable,
     * randomizeFields() will be called on the objects.
     *
     * @param al the ArrayList to populate
     * @param c the type of object to put in the vector
     * @return a populated ArrayList of random length.
     * @throws InstantiationException if the class cannot be instantiated
     * @throws IllegalAccessException if the class cannot be accessed
     */
    public static ArrayList getRandom(ArrayList al, Class c)
        throws InstantiationException, IllegalAccessException {
        return new ArrayList(RandomHelper.getRandom(new Vector(al), c));
    }

    /**
     * Returns a random string that will not have leading or trailing whitespace
     * and that will not have consecutive internal whitespace. To get a random
     * string without these restrictions, use
     * {@link #getRandom(String, Class, boolean)} with the boolean argument
     * <code>false</code>.
     *
     * @param s An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a random string
     */
    public static String getRandom(String s, Class c) {
        return getRandom(s, c, true);
    }

    /**
     * Returns a random string, optionally with leading and trailing whitespace
     * removed and internal consecutive whitespace collapsed.
     *
     * @param s An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @param collapseWhitespace if true, removes leading and trailing
     *            whitespace and collapses multiple consecutive spaces.
     * @return a random string
     * @see <a href="http://www.w3.org/TR/xmlschema-2/#rf-whiteSpace">the XML
     *      schema definition of whitespace</a>
     */
    public static String getRandom(String s, Class c, boolean collapseWhitespace) {
        int size = 1 + _rand.nextInt(MAX_STRING_LENGTH - 1);
        char[] data = new char[size];
        for (int i=0; i < size; ++i) {
            data[i] = rndPrintableChar();
        }

        if (! collapseWhitespace) {
            return new String(data);
        }

        // Make sure the first character is not whitespace
        while (Character.isWhitespace(data[0])) {
            data[0] = rndPrintableChar();
        }

        // Make sure the last character is not whitespace
        while (Character.isWhitespace(data[size-1])) {
            data[size-1] = rndPrintableChar();
        }

        // Make sure there are no consecutive whitespace characters
        for (int i = 0; i < size - 1; i++) {
            while (Character.isWhitespace(data[i]) && Character.isWhitespace(data[i+1])) {
                data[i] = rndPrintableChar();
            }
        }

        return new String(data);
    }

    /**
     * Returns a random java.util.date.
     * @param date An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a random java.util.Date.
     */
    public static java.util.Date getRandom(java.util.Date date, Class c) {
        long milli = _rand.nextLong();
        return new java.util.Date(milli);
    }

    /**
     * Returns a random Castor timeDuration.
     * @param date An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a random Castor timeDuration.
     */
    public static TimeDuration getRandom(TimeDuration date, Class c) {
        long randLong = _rand.nextInt();
        randLong = (randLong > 0) ? randLong : -randLong;
        return new TimeDuration(randLong);
    }

    /**
     * Returns a random Castor recurringDuration.
     *
     * @param recurring An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a random Castor recurringDuration.
     */
    public static RecurringDuration getRandom(RecurringDuration recurring, Class c) {
        short randShort;
        long randLong = _rand.nextLong();
        TimeDuration randDuration = new TimeDuration(randLong);
        RecurringDuration result = new RecurringDuration(randDuration, randDuration);
        short[] values = new short[10];
        //only positive values are allowed
        //century
        randShort = (short) _rand.nextInt(99);
        values[0] = (randShort > 0)? randShort:(short)-randShort;
        //year
        randShort = (short)_rand.nextInt(99);
        values[1] = (randShort > 0)? randShort:(short)-randShort;
        //month
        randShort = (short)_rand.nextInt(12);
        values[2] = (randShort > 0)? randShort:(short)-randShort;
        //day
        randShort = (short)_rand.nextInt(30);
        values[3] = (randShort > 0)? randShort:(short)-randShort;
        //hour
        randShort = (short)_rand.nextInt(24);
        values[4] = (randShort > 0)? randShort:(short)-randShort;
        //minute
        randShort = (short)_rand.nextInt(60);
        values[5] = (randShort > 0)? randShort:(short)-randShort;
        //second
        randShort = (short)_rand.nextInt(60);
        values[6] = (randShort > 0)? randShort:(short)-randShort;
        //millisecond
        randShort = (short)_rand.nextInt(99);
        values[7] = (randShort > 0)? randShort:(short)-randShort;
        //time zone hour
        randShort = (short)_rand.nextInt(12);
        values[8] = randShort;
        //time zone minute
        randShort = (short)_rand.nextInt(60);
        values[9] = (randShort > 0)? randShort:(short)-randShort;
        result.setValues(values);

        values = null;
        randDuration = null;

        return result;
    }

    /**
     * Returns a random Object of the type provided by class c
     *
     * @param object An unused parameter, used only for polymorphism.
     * @param c the type of object we will create a randomized instance of. This
     *            class must implement CastorTestable.
     * @return a random Object.
     */
    public static Object getRandom(Object object, Class c) {
        try {
            Object obj = c.newInstance();
            if (obj.getClass().isAssignableFrom(CastorTestable.class)) {
                ((CastorTestable)obj).randomizeFields();
            }
            return obj;
        } catch (Exception e) {
            // TODO: find a better way of handling this failure
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a random BigDecimal.
     *
     * @param bg An unused parameter, used only for polymorphism.
     * @param c An unused parameter that indicates we are making a random
     *            Object, not a random primitive
     * @return a random BigDecimal.
     */
    public static BigDecimal getRandom(BigDecimal bg, Class c) {
        return new BigDecimal(_rand.nextDouble());
    }

    /**
     * Returns a random int.
     * @param i An unused parameter, used only for polymorphism.
     * @return a random int.
     */
    public static int getRandom(int i) {
        return _rand.nextInt();
    }

    /**
     * Returns a random float.
     * @param f An unused parameter, used only for polymorphism.
     * @return a random float.
     */
    public static float getRandom(float f) {
        return _rand.nextFloat();
    }


    /**
     * Returns a random boolean.
     * @param b An unused parameter, used only for polymorphism.
     * @return a random boolean.
     */
    public static boolean getRandom(boolean b) {
        return _rand.nextBoolean();
    }

    /**
     * Returns a random long.
     * @param l An unused parameter, used only for polymorphism.
     * @return a random long.
     */
    public static long getRandom(long l) {
        return _rand.nextLong();
    }

    /**
     * Returns a random double.
     * @param d An unused parameter, used only for polymorphism.
     * @return a random double.
     */
    public static double getRandom(double d) {
        return _rand.nextDouble();
    }

    /**
     * Returns a random printable char.
     * @param c An unused parameter, used only for polymorphism.
     * @return a random printable char.
     */
    public static char getRandom(char c) {
        return rndPrintableChar();
    }

    /**
     * Returns a random byte.
     * @param b An unused parameter, used only for polymorphism.
     * @return a random byte.
     */
    public static byte getRandom(byte b) {
        byte[] tmp = new byte[1]; // TODO: Cache more...
        _rand.nextBytes(tmp);
        return tmp[0];
    }


    /**
     * Returns true or false randomly with equal propability.
     * @return true or false randomly with equal propability.
     */
    public static boolean flip() {
        return _rand.nextBoolean();
    }

    /**
     * Returns true randomly with the probability p.
     *
     * @param p A probability for returning true
     * @return true p% of the time
     */
    public static boolean flip(double p) {
        return (_rand.nextDouble() < p)? true : false;
    }

    /**
     * Returns a random printable character from the {@link #PRINTABLE_CHAR}
     * string.
     *
     * @return a random printable character from the PRINTABLE_CHAR string.
     */
    public static char rndPrintableChar() {
        return PRINTABLE_CHAR.charAt(_rand.nextInt(PRINTABLE_CHAR.length()));
    }

    /**
     * Returns the seed which was used to initialize the pseudo-random number
     * generator
     *
     * @return the seed which was used to initialize the pseudo-random number
     *         generator
     */
    public static long getSeed() {
        return _seed;
    }

    /**
     * Reinitializes the random number generator with the given seed.
     *
     * @param seed the new seed for the random number generator.
     */
    public static void setSeed(long seed) {
        _seed = seed;
        _rand = new Random(_seed);
    }

}
