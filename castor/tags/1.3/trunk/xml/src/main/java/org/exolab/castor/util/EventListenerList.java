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
 *
 * Contribution(s):
 *
 * - Jeff Norris, Jeff.Norris@jpl.nasa.gov
 *     - Original Author
 *
 * $Id$
 */

package org.exolab.castor.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;

/**
 * <p>This class is an efficient repository for EventListeners based
 * on javax.swing.EventListenerList.</p>
 *
 * <p>This modification of javax.swing.EventListenerList retains the
 * core functionality of that class but changes the basic API and adds
 * a few more features, as summarized below:<p>
 * 
 * <ol>
 * <li>javax.swing.EventListenerList requires all listeners to be
 * added in conjunction with a class object that identified the type
 * of the listener.  This implementation's add methods simply take the
 * listener object.
 *
 * <li>The listener list returned from javax.swing.EventListenerList
 * had to be iterated over in a cumbersome manner because the
 * listeners were stored along with their Class objects in the array.
 * Since listener classes are not stored in this listener list, the
 * returned listener array can be iterated over normally (1 element at
 * a time).
 *
 * <li>The remove method in javax.swing.EventListenerList had return
 * type "void".  This implementation's remove method returns true if
 * the specified listener was found in the listener array and false
 * otherwise.
 *
 * <li>This implementation adds {@link #add(EventListener, int)},
 * which allows the addition of a listener at a specific position in
 * the array.  
 *
 * <li>The add and remove methods in this implementation throw
 * IllegalArgumentExceptions when their arguments are null.
 * </ol>
 *
 * <p>As is the case with javax.swing.EventListenerList, this class
 * provides multi-threaded safety through a copy-on-modify strategy.
 * It is optimized to provide high performance when events are being
 * fired, with slightly slower performance than the Collection API
 * when listeners are being added and removed.  Like its predecessor,
 * this class will never return a null array from getListenerList.</p>
 *
 * <p>The most important thing to keep in mind when using this class
 * is that the array returned by getListenerList is the actual
 * internal array of this class and MUST NOT BE MODIFIED UNDER ANY
 * CIRCUMSTANCES.  Below is an example of how to use this class,
 * borrowed (and slightly modified) from the
 * javax.swing.EventListenerList documentation:</p>
 *
 * <p>Usage example:
 *    Say one is defining a class that sends out FooEvents, and one wants
 * to allow users of the class to register FooListeners and receive 
 * notification when FooEvents occur.  The following should be added
 * to the class definition:</p>
 * <pre>
 * EventListenerList listenerList = new EventListenerList();
 * FooEvent fooEvent = null;
 *
 * public void addFooListener(FooListener l) {
 *     listenerList.add(l);
 * }
 *
 * public void removeFooListener(FooListener l) {
 *     listenerList.remove(l);
 * }
 *
 * // Notify all listeners that have registered interest for
 * // notification on this event type.  The event instance 
 * // is lazily created using the parameters passed into 
 * // the fire method.
 *
 * protected void fireFooXXX() {
 *     // Guaranteed to return a non-null array
 *     EventListener[] listeners = listenerList.getListenerList();
 *     // Process the listeners last to first, notifying
 *     // those that are interested in this event
 *     for (int i = 0 ; i &lt; listeners.length ; i++) {
 *         // Lazily create the event:
 *         if (fooEvent == null)
 *             fooEvent = new FooEvent(this);
 *         ((FooListener)listeners[i]).fooXXX(fooEvent);
 *     }
 * }
 * </pre>
 *
 * <p>foo should be changed to the appropriate name, and fireFooXxx to the
 * appropriate method name.  One fire method should exist for each
 * notification method in the FooListener interface.</p>
 *
 * <p>The authors of javax.swing.EventListenerList are Georges Saab,
 * Hans Muller, and James Gosling.</p>
 *
 * @author <a href="mailto:Jeff.Norris@jpl.nasa.gov">Jeff Norris</a>
 * @version $Revision$ $Date: 2005-12-13 14:58:48 -0700 (Tue, 13 Dec 2005) $
 */
public class EventListenerList implements Serializable {
    /** SerialVersionUID */
    private static final long serialVersionUID = 4472874989562384564L;

  /**
   * A null array to be shared by all empty listener lists
   */
  private final static EventListener[] NULL_ARRAY = new EventListener[0];
  
  /** 
   * The internal list of listeners that is returned from
   * getListenerList 
   */
  protected transient EventListener[] listenerList = NULL_ARRAY;

  /**
   * <p>Passes back the event listener list as an array of
   * EventListeners.</p>
   *
   * <p>Note that for performance reasons, this implementation passes
   * back the actual data structure in which the listener data is
   * stored internally!  This method is guaranteed to pass back a
   * non-null array, so that no null-checking is required in fire
   * methods.  A zero-length array of Object will be returned if
   * there are currently no listeners.</p>
   * 
   * WARNING!!! Absolutely NO modification of
   * the data contained in this array should be made -- if
   * any such manipulation is necessary, it should be done
   * on a copy of the array returned rather than the array 
   * itself.
   */
  public EventListener[] getListenerList() {
    return listenerList;
  }

  /**
   * <p>Returns the total number of listeners in this listener
   * list.</p>
   *
   */
  public int getListenerCount() {
    return listenerList.length;
  }

  /**
   * <p>Adds the listener to the end of the listener list.</p>
   *
   * @param newListener the listener to be added
   * @exception IllegalArgumentException if the specified newListener
   * is null
   */
  public synchronized void add(EventListener newListener) 
  {
    
    if (newListener==null) 
      throw new IllegalArgumentException("Listener to add must not be null.");
    
    if (listenerList == NULL_ARRAY) {
	    // if this is the first listener added, 
	    // initialize the lists
	    listenerList = new EventListener[] { newListener };
    } else {
	    // Otherwise copy the array and add the new listener
	    int oldLength = listenerList.length;
	    EventListener[] tmp = new EventListener[oldLength+1];
	    System.arraycopy(listenerList, 0, tmp, 0, oldLength);

	    tmp[oldLength] = newListener;

	    listenerList = tmp;
    }
  }

  /**
   * <p>Adds the listener at the specified index in the listener
   * list.</p>
   *
   * @param newListener the listener to be added
   * @exception IllegalArgumentException if the specified newListener
   * is null, or the specified index is less than zero or greater than
   * the length of the listener list array.
   */
  public synchronized void add(EventListener newListener, int index) 
  {
    if (newListener==null) 
      throw new IllegalArgumentException("Listener to add must not be null.");
    
    if ((index < 0) || (index > listenerList.length))
      throw new IllegalArgumentException("Index to add listener (" + index + 
                                         ") is out of bounds. List length is " +
                                         listenerList.length);
    
    if (listenerList == NULL_ARRAY) {
	    // if this is the first listener added, initialize the lists
	    listenerList = new EventListener[] { newListener };
    } else {
      // Otherwise copy the array and add the new listener
      int oldLength = listenerList.length;
	    EventListener[] tmp = new EventListener[oldLength+1];
      // Copy up to the index where the new listener should go
	    System.arraycopy(listenerList, 0, tmp, 0, index);
      // Skip a cell and copy the rest of the list
      System.arraycopy(listenerList, index, tmp, index+1, oldLength-index);
      // Insert the new listener
	    tmp[index] = newListener;

	    listenerList = tmp;
    }
  }

  /**
   * Removes the listener as a listener of the specified type.
   *
   * @param listenerToRemove the listener to be removed
   * @exception IllegalArgumentException if the specified listener is
   * null
   */
  public synchronized boolean remove(EventListener listenerToRemove) {
    if (listenerToRemove ==null)
      throw new IllegalArgumentException("Listener to remove must " +
                                         "not be null.");
    // Is listenerToRemove on the list?
    int index = -1;
    for (int i = listenerList.length-1; i>=0; i--) {
	    if (listenerList[i].equals(listenerToRemove) == true) {
        index = i;
        break;
	    }
    }

    // If so,  remove it
    if (index != -1) {
	    EventListener[] tmp = new EventListener[listenerList.length-1];
	    // Copy the list up to index
	    System.arraycopy(listenerList, 0, tmp, 0, index);
	    // Copy from one past the index, up to the end of tmp (which is
	    // one element shorter than the old list)
	    if (index < tmp.length)
        System.arraycopy(listenerList, index+1, tmp, index, 
                         tmp.length - index);
	    // set the listener array to the new array or null
	    listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
      return true;
    }
    return false;
  }

  // Serialization support.  
  private void writeObject(ObjectOutputStream s) throws IOException {
    Object[] lList = listenerList;
    s.defaultWriteObject();
	
    // Save the non-null event listeners:
    for (int i = 0; i < lList.length; i+=1) {
	    EventListener l = (EventListener)lList[i];
	    if ((l!=null) && (l instanceof Serializable)) {
        s.writeObject(l);
	    }
    }
	
    s.writeObject(null);
  }

  private void readObject(ObjectInputStream s) 
    throws IOException, ClassNotFoundException {
    listenerList = NULL_ARRAY;
    s.defaultReadObject();
    EventListener listenerOrNull;
    
    while (null != (listenerOrNull = (EventListener)s.readObject())) {
      add(listenerOrNull);
    }	    
  }
  
  /**
   * Returns a string representation of the EventListenerList.
   */
  public String toString() {
    Object[] lList = listenerList;
    String s = "EventListenerList: ";
    s += lList.length + " listeners: ";
    for (int i = 0 ; i < lList.length ; i++) {
	    s += " listener " + lList[i+1];
    }
    return s;
  }

} //-- class: EventListenerList
