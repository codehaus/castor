
package org.exolab.castor.persist;


import java.util.Arrays;
import java.util.ArrayList;


public class ArrayVector extends ArrayList {

    public ArrayVector() {
        super();
    }

    public ArrayVector( int initsize ) {
        super( initsize );
    }
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