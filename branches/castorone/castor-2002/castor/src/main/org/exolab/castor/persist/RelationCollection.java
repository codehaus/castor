



package org.exolab.castor.persist;


import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.ArrayVector;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectNotFoundException;

public class RelationCollection implements Collection, Lazy {

    TransactionContext _tx;

    LockEngine _engine;    
    
    ClassMolder _molder;

    AccessMode _accessMode;

    OID _oid;

    /* Vector of identity */
    // to be change to oid for multi-pk support
    ArrayVector _ids;

    /* Vector of identity */
    ArrayVector _deleted;

    /* Vector of identity */ 
    ArrayVector _added;

    /* Vector of object */
    Map _loaded;

    int _changecount;

	int _size;

    public RelationCollection( TransactionContext tx, OID enclosing, LockEngine engine, 
            ClassMolder molder, AccessMode amode, ArrayVector ids ) {
        _tx = tx;
        _oid = enclosing;
        _molder = molder;
        _engine = engine;
        _accessMode = amode;
        _ids = ids;
		_size = _ids.size();
        _deleted = new ArrayVector();
        _added = new ArrayVector();
        _loaded = new ArrayKeysHashMap();
    }


    public boolean add(Object o) {
        Object[] id = _molder.getIdentities(o);
        //boolean changed = false;
        if ( _ids.contains( id ) ) {
            if ( _deleted.contains( id ) ) {
                _deleted.remove( id );
                _loaded.put( id, o );
                _changecount++;
				_size++;
                return true;
            } else {
                return (_loaded.put( id, o )!=o);
            }
        } else {
            if ( _deleted.contains( id ) ) 
				throw new RuntimeException("Illegal Internal State.");

            if ( _added.add( id ) ) {
                _loaded.put( id, o );
                _changecount++;
				_size++;
                return true;
            } else {
                return (_loaded.put( id, o )!=o);
            }
        }
    }

    public Class getBaseClass() {
        return _molder.getJavaClass();
    }

    public boolean addAll(Collection c) {
        boolean changed = false;
        Iterator a = c.iterator();
        while ( a.hasNext() ) {
            if ( add( a ) ) {
                changed = true;
            }
        }
        if ( changed )
            _changecount++;
        return changed;
    }

    public void clear() {
        Object o;
        Object id;

        for ( int i=0; i<_ids.size(); i++ ) {
			id = _ids.get(i);
			if ( !_deleted.contains(id) ) {
				_ids.remove(id);
				_size--;
			}
        }
        for ( int i=0; i<_added.size(); i++ ) {
			id = _added.get(i);
            _added.remove(id);
			_size--;
        }
		if ( _size != 0 )
			throw new RuntimeException("Illegal Internal State!");

        _changecount++; // need more accurate count?? or it's fine
    }

    public boolean contains(Object o) {
        Object[] ids = _molder.getIdentities(o);
        if ( _added.contains( ids ) )
            return true;
        if ( _ids.contains( ids ) && !_deleted.contains( ids ) )
            return true;
        return false;
    }

    public boolean containsAll(Collection c) {
        Object id;
        Iterator it = c.iterator();
        while ( it.hasNext() ) 
            if ( !contains( it.next() ) )
                return false;
        return true;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private class IteratorImp implements Iterator {
        private int changestamp;
        private int cursor;
        private RelationCollection parent;

        private IteratorImp( RelationCollection rc ) {
            parent = rc;
            changestamp = rc._changecount;
        }
        public boolean hasNext() {
            if ( changestamp != parent._changecount )
                throw new ConcurrentModificationException("Concurrent Modification is not allowed!");
            if ( cursor >= parent._size )
                return false;
            return true;
        }
        public Object next() {
            if ( changestamp != parent._changecount )
                throw new ConcurrentModificationException("Concurrent Modification is not allowed!");
            if ( cursor >= parent._size )
                throw new NoSuchElementException("Read after the end of iterator!");
            
            Object[] id;
            Object o;
            if ( cursor < _added.size() ) {
                id = (Object[]) _added.get( cursor++ );
				o = _loaded.get( id );
				if ( o != null )
					return o;
                return lazyLoad( id );
            } else {
				// skip to the first "not deleted" id
				id = (Object[])_ids.get(cursor++);
				while ( _deleted.contains(id) ) {
				    id = (Object[])_ids.get(cursor++);
				}
				o = _loaded.get( id );
				if ( o != null )
					return o;
                return lazyLoad( id );
            }
        }
        private Object lazyLoad( Object[] ids ) {
            Object o;

			if ( ! _tx.isOpen() ) 
                throw new RuntimeException("Transaction is closed!");

            try {
                o = parent._tx.load( parent._engine, parent._molder, ids, AccessMode.ReadOnly );
                parent._loaded.put( ids, o );
                return o;
            } catch ( LockNotGrantedException e ) {
                throw new RuntimeException("Lock Not Granted for lazy loaded object\n"+e);
            } catch ( PersistenceException e ) {
                throw new RuntimeException("PersistenceException for lazy loaded object\n"+e);
            } 
        }

        public void remove() {
            if ( cursor <= 0 )
                throw new IllegalStateException("Method next() must be called before remove!");
            if ( changestamp != parent._changecount )
                throw new ConcurrentModificationException("Concurrent Modification is not allowed!");

            Object id;
            cursor--;
            if ( cursor < _added.size() ) {
                parent._added.remove( cursor );
				parent._size--;
				parent._changecount++;
				changestamp = parent._changecount;
            } else {
				// backward to the first not deleted ids
                id = _ids.get(cursor);
				while ( _deleted.contains(id) ) {
				    id = _ids.get(cursor--);
				}
	            if ( cursor < _added.size() ) {
	                parent._added.remove( id );
					parent._size--;
					parent._changecount++;
					changestamp = parent._changecount;
				} else {
					parent._deleted.add( id );
					parent._size--;
					parent._changecount++;
					changestamp = parent._changecount;
	            }
	        }
		}
    }

    public Iterator iterator() {
        return new IteratorImp( this );
    }

    public boolean remove(Object o) {
        Object[] id = _molder.getIdentities( o );
        boolean changed = false;

        if ( _deleted.contains( id ) )
            return false;

        if ( _added.contains( id ) ) {
            _added.remove( id );
            _changecount++;
			_size--;
            return true;
        } else if ( _ids.contains( o ) ) {
            _deleted.add( id );
            _changecount++;
			_size--;
            return true;
        }

        return false;
    }

    public boolean removeAll(Collection c) {
        Object o;
        boolean changed = false;
        Iterator it = c.iterator();
        while ( it.hasNext() ) {
            if ( remove( it.next() ) )
                changed = true;
        }
        if ( changed )
            _changecount++;
        return changed;
    }

    public boolean retainAll(Collection c) {
        Object o;
        boolean changed = false;
        Iterator org = iterator();
        while ( org.hasNext() ) {
            o = org.next();
            if ( ! c.contains( o ) ) {
                changed = true;
                org.remove();
            }
        }
        if ( changed )
            _changecount++;
        return changed;
    }

    public int size() {
        return _size;
    }

    public Object[] toArray() {
        Object[] result = new Object[size()];
		Iterator itor = iterator();

		while ( itor.hasNext() ) {
			result = (Object[])itor.next();
		}
		return result;
    }

    public Object[] toArray(Object[] a) {
		if ( a == null )
			throw new NullPointerException();

        Object[] result;
		int size = size();

		if ( a.length < size )
			result = a;
		else 
			result = (Object[])Array.newInstance( a.getClass().getComponentType(), size );
		
		Iterator itor = iterator();
		int count = 0;
		while ( itor.hasNext() ) {
		    result[count++] = itor.next();
		}

		// patch the extra space with null
		while ( count < result.length ) {
			result[count++] = null;
		}
		return result;
    }

    public ArrayList getIdentitiesList() {
		ArrayList result = new ArrayVector();
        result.addAll(_ids);
		result.addAll(_added);
		result.removeAll(_deleted);
		return result;
    }

	public Object find( Object[] ids ) {
		return _loaded.get( ids );
	}

	public ArrayList getDeleted() {
		return (ArrayList)_deleted.clone();
	}

	public ArrayList getAdded() {
		return (ArrayList)_added.clone();
	}
}
