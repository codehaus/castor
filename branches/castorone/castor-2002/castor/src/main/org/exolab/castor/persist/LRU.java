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
 */
package org.exolab.castor.persist;


import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <p>Base interface for all Least Recently Used Cache. 
 *
 * @author <a href="tyip@leafsoft.com">Thomas Yip</a>
 */
public abstract class LRU {
    /**
     * Specify no caching as the caching mechanism of this Cache. All released object
     * will be discarded.
     */
    public final static int CACHE_NONE = 0;
    
    /**
     * Specify Count-Limited Least Recently Used is used as caching mechanism of this Cache.
     * Object Lock which is not hold by any transcation will be put in the cache, until
     * the cache is full and other object overwritten it.
     */
    public final static int CACHE_COUNT_LIMITED = 1;
    
    /**
     * Specify Time-Limited Least Recently Used is used as caching mechanism of this Cache.
     * Object Lock which is not hold by any transcation will be put in the cache, until
     * timeout is reached.
     */
    public final static int CACHE_TIME_LIMITED = 2;
    
    /**
     * Specify unlimited cache as caching mechanism of this Cache.
     * Object Lock which is not hold by any transcation will be put in the cache 
     * for later use.
     */
    public final static int CACHE_UNLIMITED = 3;

	/**
	 * ...work like Hashtable's <code>put</code>...except it's LRU limited
	 */
	public abstract Object put(Object key, Object value);

	/**
	 * ...work like Hashtable's <code>get</code>...except it's LRU limited
	 */
	public abstract Object get(Object key);

	/**
	 * ...work like Hashtable's <code>remove</code>...except it's LRU limited
	 */
	public abstract Object remove(Object key);

	/*
	 * ...work like Hashtable's <code>elements</code>...except it's LRU limited
	 */
	public abstract Enumeration elements();

	public static LRU create( int type, int param ) {
		LRU cache;

        switch ( type ) {
        case CACHE_COUNT_LIMITED :
            if ( param > 0 ) 
                cache = new LRU.CountLimited( param );
            else 
                cache = new LRU.NoCache();
            break;
        case CACHE_TIME_LIMITED :
            if ( param > 0 ) 
                cache = new LRU.TimeLimited( param );
            else 
                cache = new LRU.NoCache();
            break;
        case CACHE_UNLIMITED :
            cache = new LRU.Unlimited();
            break;
        case CACHE_NONE :
            cache = new LRU.NoCache();
            break;
        default :
            cache = new LRU.CountLimited( 100 );
        }
		return cache;
	}


	/**
	 * <p>A count limted LRU hashtable for caching objects.
	 * 
	 * <p>
	 * Every object being put in the hashtable will live for until the
	 * hashtable is full, then an object which <em>one of the</em> 
	 * least recent used object will be disposed. 
	 *
	 * <p>
	 * If you are interested in the disposing object, extend it class
	 * and override the method <code>dispose(Object o)</code>.
	 * Otherwise, the object is silently discarded.
	 *
	 */
	public static class CountLimited extends LRU {

		private final static int LRU_OLD = 0;
		private final static int LRU_NEW = 1;

		private Hashtable mapKeyPos;
		private Object[] keys;
		private Object[] values;
		private int[] status;
		private int cur;
		private int size;

		public CountLimited( int size ) {
			keys = new Object[size];
			values = new Object[size];
			status = new int[size];
			mapKeyPos = new Hashtable(size);
			this.size = size;
		}

		/**
		 * ...work like Hashtable's <code>put</code>...except it's time limited
		 */
		public synchronized Object put( Object key, Object object ) {
			Object oldPos = mapKeyPos.get(key);
			if ( oldPos != null ) {
				int pos = ((Integer)oldPos).intValue();
				Object oldObject = values[pos];
				values[pos] = object;
				status[pos] = LRU_NEW;
				dispose( oldObject );
				return oldObject;
			} else {
				// skip to new pos -- for Cache, change walkStatus() to get lock....
				while (walkStatus() != LRU_OLD) {}

				Object intvalue;// = null;
				if ( keys[cur] != null ) {
					intvalue = mapKeyPos.remove(keys[cur]);
	//				if ( intvalue == null )
	//					intvalue = new Integer(cur);
				} else {
					intvalue = new Integer(cur);
				}
				Object oldObject = values[cur];
				keys[cur] = key;
				values[cur] = object;
				status[cur] = LRU_NEW;
				//System.out.println("mapKeyPos, key: "+key+" intvalue: "+intvalue);
				mapKeyPos.put(key, intvalue);
				cur++;
				if ( cur >= size ) cur = 0;
				if ( oldObject != null )
					dispose( oldObject );
				return oldObject;
			}
		}
		/**
		 * ...work like Hashtable's <code>get</code>...except it's count limited
		 */
		public synchronized Object get( Object key ) {
			Object intvalue = mapKeyPos.get(key);
			if ( intvalue != null ) {
				int pos = ((Integer)intvalue).intValue();
				status[pos] = LRU_NEW;
				//System.out.println("CountLimiteLRU: get("+key+") = "+values[pos]);
				return values[pos];
			}
			return null;
		}
		/**
		 * ...work like Hashtable's <code>remove</code>...except it's count limited
		 */
		public synchronized Object remove( Object key ) {
			Object intvalue = mapKeyPos.remove(key);
			if ( intvalue != null ) {
				int pos = ((Integer)intvalue).intValue();
				Object temp = values[pos];
				keys[pos] = null;
				values[pos] = null;
				status[pos] = LRU_OLD;
				//System.out.println("CountLimiteLRU: remove("+key+") = "+temp);
				return temp;
			}
			return null;
		}
		/*
		 * ...work like Hashtable's <code>elements</code>...except it's count limited
		 */
		public Enumeration elements() {
			return new ValuesEnumeration(values);
		}
		/**
		 * This method is called when an object is disposed.
		 * Override this method if you interested in the disposed object.
		 */
		protected void dispose( Object o ) {
			//System.out.println("diposed: "+o+" by CountLimitedLRU");
		}
		private int walkStatus() {
			int s = status[cur];
			if ( status[cur] == LRU_NEW ) {
				status[cur] = LRU_OLD;
				cur++;
				if ( cur >= size ) cur = 0;
				return LRU_NEW;
			} else {
				return LRU_OLD;
			}
		}
		private class ValuesEnumeration implements Enumeration {
			private int cur;
			private Object[] values;
			private ValuesEnumeration( Object[] v ) {
				Vector t = new Vector(v.length);
				for ( int i=0; i<v.length; i++ ) {
					if ( v[i] != null ) {
						t.add(v[i]);
					}
				}
				values = t.toArray();
			}
			public boolean hasMoreElements() {
				if ( values != null && values.length > cur ) 
					return true;
				return false;
			}
			public Object nextElement() throws NoSuchElementException {
				if ( values == null || values.length <= cur )
					throw new NoSuchElementException();
				return values[cur++];
			}
		}
		/*
		 * inner class for putting test cases only. Safe to delete.
		 */
		private static class Test {
			public static void main (String args[]) {
				class TestLRU extends CountLimited {
					public TestLRU(int count) {
						super(count);
					}
					protected void dispose(Object o) {
						//System.out.println("dispose: " + o );
					}
				}
				CountLimited cl = new TestLRU(3);
				Enumeration e = cl.elements();
				System.out.println( "<empty>" );
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "</empty>" );

				System.out.println( "<put(a,a)>" );
				cl.put("a","#a");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(a,a)>" );

				System.out.println( "<put(b,b)>" );
				cl.put("b","#b");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(b,b)>" );

				System.out.println( "<put(c,c)>" );
				cl.put("c","#c");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(c,c)>" );

				System.out.println( "<put(d,d)>" );
				cl.put("d","#d");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(d,d)>" );

				System.out.println( "<put(c,c1)>" );
				cl.put("c","#c1");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(c,c1)>" );

				System.out.println( "<put(c,c2)>" );
				cl.put("c","#c2");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(c,c2)>" );

				System.out.println( "<put(c,c3)>" );
				cl.put("c","#c3");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(c,c3)>" );

				System.out.println( "<put(b,b)>" );
				cl.put("b","#b");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(b,b)>" );

				System.out.println( "<put(e,e)>" );
				cl.put("e","#e");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(e,e)>" );

				System.out.println( "<put(f,f)>" );
				cl.put("f","#f");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(f,f)>" );

				System.out.println( "<remove(e,e)>" );
				System.out.println(cl.remove("e")+" is removed!");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</remove(e,e)>" );

				System.out.println( "<put(g,g)>" );
				cl.put("g","#g");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(g,g)>" );

				System.out.println( "<remove(f,f)>" );
				System.out.println(cl.remove("f")+" is removed!");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</remove(f,f)>" );

				System.out.println( "<remove(b,b)>" );
				System.out.println(cl.remove("b")+" is removed!");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</remove(b,b)>" );

				System.out.println( "<remove(g,g)>" );
				System.out.println(cl.remove("g")+" is removed!");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</remove(g,g)>" );

				System.out.println( "<remove(x,x)>" );
				System.out.println(cl.remove("x")+" is removed!");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</remove(x,x)>" );

				System.out.println( "<put(a,a)>" );
				cl.put("a","#a");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(a,a)>" );

				System.out.println( "<put(b,b)>" );
				cl.put("b","#b");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(b,b)>" );

				System.out.println( "<put(c,c)>" );
				cl.put("c","#c");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(c,c)>" );

				System.out.println( "<put(d,d)>" );
				cl.put("d","#d");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "\n</put(d,d)>" );

			}
		}
	}


	/**
	 * <p>A count limted LRU hashtable for caching objects.
	 * 
	 * <p>
	 * Every object being put in the hashtable will live for until the
	 * hashtable is full, then an object which <em>one of the</em> 
	 * least recent used object will be disposed. 
	 *
	 * <p>
	 * If you are interested in the disposing object, extend it class
	 * and override the method <code>dispose(Object o)</code>.
	 * Otherwise, the object is silently discarded.
	 *
	 */
	public static class NoCache extends LRU {

		/**
		 * ...work like Hashtable's <code>put</code>...except it's time limited
		 */
		public synchronized Object put( Object key, Object object ) {
			dispose( object );
			return null;
		}
		/**
		 * ...work like Hashtable's <code>get</code>...except it's time limited
		 */
		public synchronized Object get( Object key ) {
			return null;
		}
		/**
		 * ...work like Hashtable's <code>remove</code>...except it's time limited
		 */
		public synchronized Object remove( Object key ) {
			return null;
		}
		/*
		 * ...work like Hashtable's <code>elements</code>...except it's time limited
		 */
		public Enumeration elements() {
			return new EmptyEnumeration();
		}
		/**
		 * This method is called when an object is disposed.
		 * Override this method if you interested in the disposed object.
		 */
		protected void dispose( Object o ) {
			//System.out.println("dispose: "+o+" by NoCache.");
		}
		private class EmptyEnumeration implements Enumeration {

			private EmptyEnumeration() {
			}
			public boolean hasMoreElements() {
				return false;
			}
			public Object nextElement() throws NoSuchElementException {
				throw new NoSuchElementException();
			}
		}
	}

	/**
	 * <p>A time limted LRU hashtable for caching objects.
	 * 
	 * <p>
	 * Every object being put in the hashtable will live for specified
	 * amount of time and it will be disposed. Unless it is <code>put
	 * </code> or <code>get</code> before it is disposed, then the time 
	 * is reset. 
	 *
	 * <p>
	 * If you are interested in the disposing object, extend it class
	 * and override the method <code>dispose(Object o)</code>.
	 * Otherwise, the object is silently discarded.
	 *
	 * @author <a href="tyip@leafsoft.com">Thomas Yip</a>
	 */
	public static class TimeLimited extends LRU {
		/**
		 *	The Default precision in millisecond is 1000. Percision is the interval 
		 *  between each time which the timer thread will wake up and trigger 
		 *  clean up of Least Recently Used Object.
		 */
		public final static int DEFAULT_PRECISION = 1000;

		private TimeThread ticker = new TimeThread( DEFAULT_PRECISION );

		private int interval;
		private int tailtime;
		private QueueItem head;
		private QueueItem tail;
		private Hashtable map;

		/**
		 * Constructor
		 *
		 * @param interval the number of ticks an object live before it is disposed.
	     * @param tick precision in msec.
		 */
		public TimeLimited( int interval ) {
			map = new Hashtable();
			this.interval = interval+1;
			ticker.addListener( this );
		}
		/**
		 * ...work like Hashtable's <code>put</code>...except it's time limited
		 */
		public synchronized Object put(Object key, Object value) {
			QueueItem oldItem = (QueueItem) map.get(key);
			if ( oldItem != null ) {
				Object oldObject = oldItem.item;
				oldItem.item = value;
				remove(oldItem);
				add(oldItem);
				return oldObject;
			} else {
				QueueItem newitem = new QueueItem(key,value);
				map.put(key,newitem);
				add(newitem);
				return null;
			}
		}
		/**
		 * ...work like Hashtable's <code>get</code>...except it's time limited
		 */
		public synchronized Object get(Object key) {
			Object o = map.get(key);
			if ( o == null )
				return null;
			else 
				return ((QueueItem)o).item;
		}
		/**
		 * ...work like Hashtable's <code>remove</code>...except it's time limited
		 */
		public synchronized Object remove(Object key) {

			Object o = map.remove(key);
			if ( o == null ) {
				//System.out.println("TimeLimiteLRU: not in cache ... remove("+key+")");
				return null;
			} else {
				//System.out.println("TimeLimiteLRU: remove("+key+") = "+((QueueItem)o).item);
				return ((QueueItem)o).item;
			}
		}
		/*
		 * ...work like Hashtable's <code>elements</code>...except it's time limited
		 */
		public synchronized Enumeration elements() {
			return new ValuesEnumeration(map.elements());
		}
		/**
		 * This method is called when an object is disposed.
		 * Override this method if you interested in the disposed object.
		 */
		protected void dispose( Object o ) {
			//System.out.println("dispose: "+o+" by TimeLimiteLRU.");
		}

		private void remove(QueueItem item) {
			//System.out.println( "removing: <" + item + "> while...head=<"+head+"> tail=<"+tail+">" );
			QueueItem temp;
			if ( item == null ) 
				throw new NullPointerException();

			if ( item == head ) {
				temp = item;

				head = head.next;
				if ( head == null ) {
					tail = null;
				} else {
					head.prev = null;
					head.time += temp.time;
				}
				temp.prev = null;
				temp.next = null;
				temp.time = 0;
			} else if ( item == tail ) {
				tail = tail.prev;
				tailtime = 0;
			} else {
				temp = item;

				temp.prev.next = temp.next;
				temp.next.prev = temp.prev;
				temp.next.time += temp.time;

				temp.prev = null;
				temp.next = null;
				temp.time = 0;
			}
		}

		private void add(QueueItem item) {
			ticker.startTick();
			if ( head == null ) {
				head = tail = item;
				item.prev = null;
				item.next = null;
				item.time = interval;
				tailtime = interval;
			} else {
				tail.next = item;
				item.prev = tail;
				item.next = null;
				item.time = interval-tailtime;
				tailtime = interval;
				tail = item;
			}
		}

		/* 
		 * call by ticker daemon
		 */
		private synchronized void tick() {
			QueueItem temp;
			Object o;

			if ( head != null ) {
				head.time--;
				tailtime--;
			}
			while ( head != null && head.time <= 0 ) {
				
				temp = head;

				o = head.item;
				remove(temp);
				map.remove(temp.key);
				dispose(o);
			}
	//		if ( head == null ) {
	//			ticker.stopTick();
	//		}
		}

		private class ValuesEnumeration implements Enumeration {
			private Vector v = new Vector();
			private int cur;

			private ValuesEnumeration( Enumeration e ) {
				while ( e.hasMoreElements() ) {
					v.add(e.nextElement());
				}
				v.trimToSize();
			}
			public boolean hasMoreElements() {
				if ( v.size() > cur ) 
					return true;
				return false;
			}
			public Object nextElement() throws NoSuchElementException {
				if ( v.size() <= cur )
					throw new NoSuchElementException();
				Object o = v.get(cur++);
				if ( o != null )
					return ((QueueItem)o).item;
				else 
					return null;
			}
		}

		private class QueueItem {
			private QueueItem next;
			private QueueItem prev;
			private Object key;
			private Object item;
			private int time;

			private QueueItem( Object key, Object item ) {
				this.key = key;
				this.item = item;
			}
		}

		/*
		 * Ticker daemon. Generate tick in fixed interval of time.
		 */
		private static class TimeThread extends Thread {
			private int[] listenerLock = new int[0];
			private LinkList listener;
			private int[] lock = new int[0];
			private int tick;
			private long lastTime;
			private boolean isStopped;
			private boolean isStarted;

			public TimeThread(int tick) {
				this.tick = tick;
				setDaemon(true);
				setPriority( MIN_PRIORITY );
				isStopped = true;
				start();
			}
			public void startTick() {
				//System.out.println( "start tick" );
				if ( isStarted && isStopped ) {
					synchronized(lock) {
						lastTime = System.currentTimeMillis();
						lock.notify();
					}
				}
			}
			public void stopTick() {
				//System.out.println( "stop tick" );
				isStopped = true;
			}
			public void run() {
				isStarted = true;
				try {
					while ( true ) {
						if ( isStopped ) {
							synchronized(lock) { lock.wait(); }
							isStopped = false;
						} else {							
							long time = System.currentTimeMillis();
							if ( time - lastTime < tick ) 
								sleep(tick-(time-lastTime));
							lastTime = time;
						}

						LinkList temp = listener;
						while ( temp != null ) {
							temp.t.tick();
							temp = temp.next;
						}
					}
				} catch ( InterruptedException e ) {
				}
			}
			public void addListener( TimeLimited t ) {
				synchronized ( listenerLock ) {
					listener = new LinkList( listener, t );
				}
			}
			private class LinkList {
				private LinkList next;
				private TimeLimited t;
				LinkList( LinkList next, TimeLimited t ) {
					this.next = next;
					this.t = t;
				}
			}
		}
		/*
		 * inner class for putting test cases only. Safe to delete.
		 */
		private static class Test {
			public static void main (String args[]) throws Exception {
				class TestLRU extends TimeLimited {
					public TestLRU(int count) {
						super(count);
					}
					protected void dispose(Object o) {
						Enumeration e = this.elements();
						System.out.println("disposing: " + o );
						System.out.println( "list after disposed!" );
						while ( e.hasMoreElements() ) {
							System.out.print( e.nextElement() + "\t" );
						}
						System.out.println();
						super.dispose(o);
					}
				}
				TimeLimited cl = new TestLRU(2);
				Thread t = Thread.currentThread();

				Enumeration e = cl.elements();
				System.out.println( "<empty>" );
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println( "</empty>" );

				System.out.println( "<put(a,a)>" );
				cl.put("a","#a");
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				t.sleep(1000);
				System.out.println( "\n</put(a,a)>" );


				System.out.println( "<put[a,b,c,d,e,f,g,h]>" );
				cl.put("a","#a"); System.out.println( "put a" );
				t.sleep(10);

				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println();

				cl.put("b","#b"); System.out.println( "put b" );
				t.sleep(100);

				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println();

				cl.put("c","#c"); System.out.println( "put c" );
				t.sleep(1000);

				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println();

				cl.put("d","#d"); System.out.println( "put d" );
				t.sleep(500);

				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println();

				cl.put("e","#e"); System.out.println( "put e" );
				t.sleep(1000);
				cl.put("f","#f"); System.out.println( "put f" );
				t.sleep(1000);
				cl.put("g","#g"); System.out.println( "put g" );
				t.sleep(1000);
				cl.put("h","#h"); System.out.println( "put h" );
				t.sleep(500);
				e = cl.elements();
				while ( e.hasMoreElements() ) {
					System.out.print( e.nextElement() + "\t" );
				}
				System.out.println();

				for (int i=0; i<10; i++ ) {
					t.sleep(1000);
					e = cl.elements();
					while ( e.hasMoreElements() ) {
						System.out.print( e.nextElement() + "\t" );
					}
					System.out.println();
				}

				System.out.println( "\n</put[a,b,c,d,e,f,g,h]>" );

			}
		}
	}

	/**
	 * <p>A time limted LRU hashtable for caching objects.
	 * 
	 * <p>
	 * Every object being put in the hashtable will live forever.
	 *
	 */
	public static class Unlimited extends LRU {
		private Hashtable map = new Hashtable();

		/**
		 * @param interval the number of ticks an object live before it is disposed.
	     * @param tick precision in msec.
		 */
		public Unlimited() {
		}

		/**
		 * ...work like Hashtable's <code>put</code>...except it's time limited
		 */
		public Object put(Object key, Object value) {
			return map.put(key,value);
		}

		/**
		 * ...work like Hashtable's <code>get</code>...except it's time limited
		 */
		public Object get(Object key) {
			return map.get(key);
		}

		/**
		 * ...work like Hashtable's <code>remove</code>...except it's time limited
		 */
		public Object remove(Object key) {
			return map.remove(key);
		}

		/*
		 * ...work like Hashtable's <code>elements</code>...except it's time limited
		 */
		public Enumeration elements() {
			return map.elements();
		}

		/**
		 * This method is called when an object is disposed.
		 * Override this method if you interested in the disposed object.
		 */
		protected void dispose( Object o ) {
		}
	}
}
