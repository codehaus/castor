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
 * $Id: ArrayVector.java,
 */

package org.exolab.castor.persist;


import java.util.Arrays;
import java.util.ArrayList;


/**
 * ArrayVector extends {@link java.util.ArrayList} and work exactly
 * like {@link java.util.ArrayList} except the way it compares 
 * elements' equalness. 
 * <p>
 * If the elements to be compared are array (instanceOf Object[]), 
 * then the method, {@link java.util.Arrays.equals( Object[], Object[])}, 
 * will be used, instead of Object.equals( Object ). 
 *
 * @author <a href="yip@intalio.com">Thomas Yip</a>
 */

public class ArrayVector extends ArrayList {

    public ArrayVector() {
        super();
    }

    public ArrayVector( int initsize ) {
        super( initsize );
    }

    /**
     * Searches for the first occurence of the given argument, testing 
     * for equality using the Arrays.equals() method if the object is
     * an instanceOf Object[], using Object.equals() otherwise. 
     *
     * @param   elem   an object.
     * @return  the index of the first occurrence of the argument in this
     *          list; returns <tt>-1</tt> if the object is not found.
     * @see     Object#equals(Object)
     */
    public int indexOf(Object elem) {
        int size = size();

        if ( !(elem instanceof Object[]) ) 
            return super.indexOf(elem);

        if (elem == null) {
            for (int i = 0; i < size; i++)
                if (get(i)==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++) {
                Object o = get(i);
                if ( o instanceof Object[] )
                    if (Arrays.equals((Object[])elem,(Object[])o))
                        return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified object in
     * this list.
     *
     * @param   elem   the desired element.
     * @return  the index of the last occurrence of the specified object in
     *          this list; returns <tt>-1</tt> if the object is not found.
     */
    public int lastIndexOf(Object elem) {
        if ( !(elem instanceof Object[]) )
            return super.lastIndexOf(elem);

        int size = size();
        if (elem == null) {
            for (int i = size-1; i >= 0; i--)
                if (get(i)==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--) {
                Object o = get(i);
                if ( o instanceof Object[] )
                    if (Arrays.equals((Object[])elem, (Object[])o))
                        return i;
            }
        }
        return -1;
    }

}