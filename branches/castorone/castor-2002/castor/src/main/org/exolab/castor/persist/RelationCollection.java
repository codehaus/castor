



package org.exolab.castor.persist;


import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
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
    Vector _deleted;

    /* Vector of identity */
    Vector _added;

    /* Vector of object */
    Hashtable _loaded;

    int _changecount;

    public RelationCollection( TransactionContext tx, OID enclosing, LockEngine engine, 
            ClassMolder molder, AccessMode amode, ArrayVector ids ) {
        _tx = tx;
        _oid = enclosing;
        _molder = molder;
        _engine = engine;
        _accessMode = amode;
        _ids = ids;
        _deleted = new Vector();
        _added = new Vector();
        _loaded = new Hashtable();
    }


    public boolean add(Object o) {
        Object[] ids = _molder.getIdentities(o);
        //boolean changed = false;
        if ( _ids.contains( ids ) ) {
            if ( _deleted.contains( ids ) ) {
                _loaded.put( ids, o );
                _deleted.remove( ids );
                _changecount++;
                return true;
            } else {
                _loaded.put( ids, o );
                return false;
            }
        } else {
            if ( _deleted.contains( ids ) ) {
                _loaded.put( ids, o );
                _deleted.remove( ids );
                _added.add( ids );
                _changecount++;
                return true;
            }
            if ( _added.add( ids ) ) {
                _loaded.put( ids, o );
                _changecount++;
                return true;
            } else {
                return false;
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
            _deleted.add( id );
            _loaded.remove( id );
        }


        for ( int i=0; i<_added.size(); i++ ) {
            id = _ids.get(i);
            _loaded.remove( id );
        }
        _changecount++; // need more accurate count?? or it's fine
        _added.clear();
    }

    public boolean contains(Object o) {
        Object[] ids = _molder.getIdentities(o);
        if ( _added.contains( ids ) )
            return true;
        if ( _ids.contains( ids ) && ! _deleted.contains( ids ) )
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

        private IteratorImp( RelationCollection rc, int count ) {
            changestamp = count;
            parent = rc;
        }
        public boolean hasNext() {
            if ( changestamp != parent._changecount )
                throw new ConcurrentModificationException("Concurrent Modification is not allowed!");
            if ( cursor >= parent.size() )
                return false;
            return true;
        }
        public Object next() {
            if ( changestamp != parent._changecount )
                throw new ConcurrentModificationException("Concurrent Modification is not allowed!");
            if ( cursor >= parent.size() )
                throw new NoSuchElementException("Read after the end of iterator!");
            
            Object[] ids;
            Object o;
            if ( cursor < _added.size() ) {
                ids = (Object[]) _added.elementAt( cursor++ );
                return lazyLoad( ids );
            } else {
                ids = (Object[]) parent._ids.get(translate( cursor - _added.size() ) );
                return lazyLoad( ids );
            }
        }
        private int translate( int cur ) {
            Object id;
            int relatedCursor = cursor - _added.size();
            int i = 0;
            int idcur = 0;
            while ( i <= relatedCursor ) {
                id = parent._ids.get( idcur );
                if ( !parent._deleted.contains( id ) )
                    i++;
                idcur++;
            }
            while ( parent._deleted.contains( parent._ids.get( idcur )) ) {
                idcur++;
            }
            return idcur;
        }
        private Object lazyLoad( Object[] ids ) {
            Object o;
            if ( _loaded.contains( ids ) )
                return _loaded.get( ids );
            else {
                if ( ! _tx.isOpen() ) {
                    return null;
                } else {
                    try {
                        o = parent._tx.load( parent._engine, parent._molder, ids, AccessMode.ReadOnly );
                        parent._loaded.put( ids, o );
                        return o;
                    } catch ( LockNotGrantedException e ) {
                        throw new RuntimeException("Lock Not Granted for lazy loaded object\n"+e);
                    } catch ( PersistenceException e ) {
                        throw new RuntimeException("PersistenceException for lazy loaded object\n"+e);
                    //} catch ( ObjectNotFoundException e ) {
                      //  throw new RuntimeException("ObjectNotFound for lazy loaded object\n"+e);
                    } finally {
                        cursor--;
                    }
                }
            }
        }
        public void remove() {
            if ( cursor == 0 )
                throw new IllegalStateException("next() should be called before remove()");
            if ( changestamp != parent._changecount )
                throw new ConcurrentModificationException("Concurrent Modification is not allowed!");

            Object id;
            int cur = cursor -1;
            if ( cur < _added.size() ) {
                id = parent._added.elementAt( cur );
                parent.remove( id );
            } else {
                id = parent._ids.get(translate( cursor-_added.size() ) );
                parent.remove( id );
            }
        }
    }

    public Iterator iterator() {
        return new IteratorImp( this, _changecount ) ;
    }

    public boolean remove(Object o) {
        Object[] id = _molder.getIdentities( o );
        boolean changed = false;

        if ( _deleted.contains( id ) )
            return false;

        if ( _added.contains( id ) ) {
            _added.remove( id );
            _loaded.remove( id );
            _changecount++;
            return true;
        }

        if ( _ids.contains( o ) ) {
            _deleted.add( id );
            _loaded.remove( id );
            _changecount++;
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
        return _ids.size() - _deleted.size() + _added.size();
    }

    public Object[] toArray() {
        return null;
    }

    public Object[] toArray(Object[] a) {
        return null;
    }

    public Object[] getIdentity() {
        return null;
    }

    public Object[] getIdentities() {
        return null;
    }

    public Vector getIdentitiesList() {
        return null;
    }

    public boolean isLoaded() {
        return false;
    }
}
