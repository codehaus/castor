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
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.dax.engine;


import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPModification;
import netscape.ldap.LDAPModificationSet;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPv2;
import netscape.ldap.LDAPDN;
import netscape.ldap.util.DN;
import netscape.ldap.util.RDN;
import org.exolab.castor.dax.engine.DAXFieldDesc;
import org.exolab.castor.dax.engine.DAXClassDesc;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.ContainerFieldDesc;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.Types;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.DuplicateIdentityException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.ObjectModifiedException;
import org.exolab.castor.persist.ObjectDeletedException;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.FatalPersistenceException;
import org.exolab.castor.persist.RelationContext;



/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class MozillaEngine
    implements Persistence
{


    private DAXClassDesc  _clsDesc;


    private Hashtable      _fields;


    private DAXFieldDesc[] _dnFields;


    private String         _dnFieldName;

 
    private FieldDesc      _attrField;


    private String         _rootDN;


    public MozillaEngine( DAXClassDesc clsDesc, String rootDN )
        throws MappingException
    {
        FieldDesc[] fields;
        FieldDesc   dnField;
        
        _clsDesc = clsDesc;
        _attrField = _clsDesc.getAttributeSetField();
        fields = clsDesc.getFields();
        _fields = new Hashtable();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof ContainerFieldDesc ) {
                FieldDesc[] contained;
                
                contained = ( (ContainerFieldDesc) fields[ i ] ).getFields();
                for ( int j = 0 ; j < contained.length ; ++j ) {
                    if ( _fields.put( ( (DAXFieldDesc) contained[ j ] ).getLdapName(),
                                      new DAXContainedFieldDesc( (DAXFieldDesc) contained[ j ], (ContainerFieldDesc) fields[ i ] ) ) != null )
                        throw new MappingException( "Duplicate LDAP attribute" );
                }
            } else {
                if ( _fields.put( ( (DAXFieldDesc) fields[ i ] ).getLdapName(), fields[ i ] ) != null )
                    throw new MappingException( "Duplicate LDAP attribute" );
            }
        }
        
        dnField = _clsDesc.getIdentity();
        if ( dnField instanceof ContainerFieldDesc ) {
            fields = ( (ContainerFieldDesc) dnField ).getFields();
            _dnFields = new DAXFieldDesc[ fields.length ];
            for ( int i = 0 ; i < fields.length ; ++i )
                _dnFields[ i ] = (DAXFieldDesc) fields[ i ];
        } else {
            _dnFieldName = ( (DAXFieldDesc) _clsDesc.getIdentity() ).getLdapName();
        }
        _rootDN = rootDN;
    }


    public Object create( Object conn, Object obj, Object identity )
        throws DuplicateIdentityException, PersistenceException
    {
        LDAPAttributeSet ldapSet;
        String           dn;
        Enumeration      enum;
        DAXFieldDesc     field;
        boolean          exists;
        
        dn = getDN( identity );
        try {
            ldapSet = new LDAPAttributeSet();
            enum = _fields.elements();
            while ( enum.hasMoreElements() ) {
                field = (DAXFieldDesc) enum.nextElement();
                ldapSet.add( field.getAttribute( obj ) );
            }
            // XXX
            // Also need to create all the attributes in the attrSet
            DAXClassDesc type;
            
            type = _clsDesc;
            while ( type != null ) {
                ldapSet.add( new LDAPAttribute( "objectclass", type.getLdapClass() ) );
                type = (DAXClassDesc) type.getExtends();
            }
            ( (LDAPConnection) conn ).add( new LDAPEntry( dn, ldapSet ) );
            return null;
        } catch ( LDAPException except ) {
            if ( except.getLDAPResultCode() == LDAPException.ENTRY_ALREADY_EXISTS )
                throw new DuplicateIdentityException( obj.getClass(), identity );
            if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                 except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                throw new FatalPersistenceException( except );
            throw new PersistenceException( except );
        }
    }


    public Object load( Object conn, RelationContext rtx, Object obj, Object identity,
                        AccessMode accessMode )
        throws ObjectNotFoundException, PersistenceException
    {
        LDAPAttributeSet ldapSet;
        LDAPAttribute    ldapAttr;
        LDAPEntry        entry;
        String           dn;
        DAXFieldDesc     field;
        Enumeration      enum;
        
        dn = getDN( identity );
        try {
            entry = ( (LDAPConnection) conn ).read( dn );
            if ( entry == null )
                throw new ObjectNotFoundException( obj.getClass(), identity );
        } catch ( LDAPException except ) {
            if ( except.getLDAPResultCode() == LDAPException.NO_SUCH_OBJECT )
                throw new ObjectNotFoundException( obj.getClass(), identity );
            if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                 except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                throw new FatalPersistenceException( except );
            throw new PersistenceException( except );
        }
        
        ldapSet = entry.getAttributeSet();
        for ( int i = 0 ; i < ldapSet.size() ; ++i ) {
            ldapAttr = ldapSet.elementAt( i );
            if ( ldapAttr.getName().equals( "objectclass" ) ) {
                
                String[] classes;
                boolean  match;
                
                /*
                  classes = ldapAttr.getStringValueArray();
                  match = false;
                  for ( int j = 0 ; j < classes.length ; ++j ) {
                  if ( classes[ j ].equals( _clsDesc.getLdapClass() ) ) {
                  match = true;
                  break;
                  }
                  }
                  if ( ! match ) {
                  throw new IllegalStateException( "LDAP entry does not match object class " +
                  _clsDesc.getLdapClass() );
                  }
                */
                
            } else {
                
                field = (DAXFieldDesc) _fields.get( ldapAttr.getName() );
                if ( field != null ) {
                    field.setValue( obj, entry );
                } else if ( _attrField != null ) {
                    Hashtable     attrSet;
                    
                    attrSet = (Hashtable) _attrField.getValue( obj );
                    if ( attrSet == null ) {
                        attrSet = new Hashtable();
                        _attrField.setValue( obj, attrSet );
                    }
                    attrSet.put( ldapAttr.getName(), ldapAttr.getStringValueArray() );
                }
            }
        }
        return null;
    }


    public Object store( Object conn, Object obj, Object identity,
                         Object original, Object stamp )
        throws ObjectModifiedException, ObjectDeletedException, PersistenceException
    {
        LDAPModificationSet modifs;
        String              dn;
        Enumeration         enum;
        LDAPEntry           entry;
        LDAPAttributeSet    ldapSet;
        boolean             exists;
        
        dn = getDN( identity );
        try {
            entry = ( (LDAPConnection) conn ).read( dn );
            if ( entry == null )
                throw new ObjectDeletedException( obj.getClass(), identity );
        } catch ( LDAPException except ) {
            if ( except.getLDAPResultCode() == LDAPException.NO_SUCH_OBJECT )
                throw new ObjectDeletedException( obj.getClass(), identity );
            if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                 except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                throw new FatalPersistenceException( except );
            throw new PersistenceException( except );
        }
        ldapSet = entry.getAttributeSet();
        
        modifs = new LDAPModificationSet();
        enum = _fields.elements();
        while ( enum.hasMoreElements() ) {
            DAXFieldDesc        field;
            LDAPAttribute       attr;
            
            field = (DAXFieldDesc) enum.nextElement();
            exists = ( ldapSet.getAttribute( field.getLdapName() ) != null );
            attr = field.getAttribute( obj );
            if ( exists )
                ldapSet.remove( field.getLdapName() );
            if ( attr == null ) {
                if ( exists )
                    modifs.add( LDAPModification.DELETE, new LDAPAttribute( field.getLdapName() ) );
            } else {
                modifs.add( ( exists ? LDAPModification.REPLACE : LDAPModification.ADD ), attr );
            }
        }
        
        if ( _attrField != null ) {
            Hashtable    attrSet;
            Enumeration  attrs;
            String       name;
            Object       value;
            
            attrSet = (Hashtable) _attrField.getValue( obj );
            attrs = attrSet.keys();
            while ( attrs.hasMoreElements() ) {
                name = (String) attrs.nextElement();
                exists = ( ldapSet.getAttribute( name ) != null );
                if ( exists )
                    ldapSet.remove( name );
                value = attrSet.get( name.toString() );
                if ( value instanceof String[] )
                    modifs.add( ( exists ? LDAPModification.REPLACE : LDAPModification.ADD ),
                                new LDAPAttribute( name, (String[]) value ) );
                else
                    modifs.add( ( exists ? LDAPModification.REPLACE : LDAPModification.ADD ),
                                new LDAPAttribute( name, value.toString() ) );
            }
        }
        
        enum = ldapSet.getAttributes();
        while ( enum.hasMoreElements() ) {
            LDAPAttribute ldapAttr;
            
            ldapAttr = (LDAPAttribute) enum.nextElement();
            if ( ! ldapAttr.getName().equals( "objectclass" ) ) {
                modifs.add( LDAPModification.DELETE, ldapAttr );
            }
        }
        try {
            ( (LDAPConnection) conn ).modify( dn, modifs );
            return null;
        } catch ( LDAPException except ) {
            if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                 except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                throw new FatalPersistenceException( except );
            throw new PersistenceException( except );
        }
    }
    
    
    public void delete( Object conn, Object obj, Object identity )
        throws PersistenceException
    {
        String dn;
        
        dn = getDN( identity );
        try {
            ( (LDAPConnection) conn ).delete( dn );
        } catch ( LDAPException except ) {
            if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                 except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                throw new FatalPersistenceException( except );
            throw new PersistenceException( except );
        }
    }


    public void writeLock( Object conn, Object obj, Object identity )
        throws ObjectDeletedException, PersistenceException
    {
    }
    
    
    public void changeIdentity( Object conn, Object obj,
                                Object oldIdentity, Object newIdentity )
        throws ObjectDeletedException, DuplicateIdentityException, PersistenceException
    {
        String oldDN;
        String newRDN;
        
        oldDN = getDN( oldIdentity, true );
        newRDN = getDN( newIdentity, false );
        try {
            ( (LDAPConnection) conn ).rename( oldDN, newRDN, false );
        } catch ( LDAPException except ) {
            if ( except.getLDAPResultCode() == LDAPException.NO_SUCH_OBJECT )
                throw new ObjectDeletedException( obj.getClass(), oldIdentity );
            if ( except.getLDAPResultCode() == LDAPException.ENTRY_ALREADY_EXISTS )
                throw new DuplicateIdentityException( obj.getClass(), newIdentity );
            if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                 except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                throw new FatalPersistenceException( except );
            throw new PersistenceException( except );
        }
    }
    

    public PersistenceQuery createQuery( RelationContext rtx, String query, Class[] types )
        throws QueryException
    {
        return new MozillaQuery( query, types );
    }


    private String getDN( Object identity )
    {
        return getDN( identity, true );
    }


    private String getDN( Object identity, boolean withRootDN )
    {
        StringBuffer dn;
        boolean      first;
        
        dn = new StringBuffer();
        if ( _dnFields != null ) {
            first = true;
            for ( int i = _dnFields.length ; i-- > 0 ; ) {
                if ( first )
                    first = false;
                else
                    dn.append( ',' );
                dn.append( _dnFields[ i ].getLdapName() ).append( '=' );
                dn.append( _dnFields[ i ].getValue( identity ).toString() );
            }
        } else {
            dn.append( _dnFieldName );
            dn.append( '=' ).append( identity.toString() );
        }
        if ( withRootDN && _rootDN != null )
            dn.append( ',' ).append( _rootDN );
        return dn.toString();
    }


    private Object getIdentityFromDN( String dn )
    {
        String[] rdns;
        
        if ( _rootDN == null )
            rdns = LDAPDN.explodeDN( dn, true );
        else
            rdns = LDAPDN.explodeRDN( dn.substring( 0, dn.length() - _rootDN.length() ), true );
        if ( _dnFields != null ) {
            Object identity;
            
            identity = Types.newInstance( _clsDesc.getIdentity().getFieldType() );
            for ( int i = _dnFields.length ; i-- > 0 ; )
                _dnFields[ i ].setValue( identity, rdns[ i ] );
            return identity;
        } else {
            return rdns[ 0 ];
        }
    }


    class MozillaQuery
        implements PersistenceQuery
    {
        
        private final Class[]     _paramTypes;
        
        
        private Object[]          _paramValues;
        
        
        private LDAPEntry         _lastResult;
        
        
        private LDAPSearchResults _results;
        
        
        private int               _position;
        
        
        private final String[]    _query;
        
        
        MozillaQuery( String query, Class[] types )
        {
            StringTokenizer token;
            
            if ( types == null )
                throw new IllegalArgumentException( "Argument 'types' is null" );
            _paramTypes = types;
            _paramValues = new Object[ _paramTypes.length ];
            if ( query == null )
                throw new IllegalArgumentException( "Argument 'query' is null" );
            token = new StringTokenizer( query, "\0" );
            _query = new String[ token.countTokens() ];
            for ( int i = 0 ; i < _query.length ; ++i )
                _query[ i ] = token.nextToken();
            if ( _query.length != _paramTypes.length + 1 )
                throw new IllegalArgumentException( "Argument 'query' and 'paramTypes' do not match in number of parameters" );
        }
        
        
        public int getParameterCount()
        {
            return _paramTypes.length;
        }
        
        
        public Class getParameterType( int index )
        {
            return _paramTypes[ index ];
        }
        
        
        public void setParameter( int index, Object value )
        {
            if ( value != null && _paramTypes[ index ] != null )
                if ( ! _paramTypes[ index ].isAssignableFrom( value.getClass() ) )
                    throw new IllegalArgumentException( "Parameter " + index + " must be of type " +
                                                        _paramTypes[ index ].getName() + " instead recieved type " +
                                                        value.getClass().getName() );
            _paramValues[ index ] = value;
        }
        
        
        public Class getResultType()
        {
            return _clsDesc.getJavaClass();
        }
        
        
        public void execute( Object conn, AccessMode accessMode )
            throws QueryException, PersistenceException
        {
            try {
                StringBuffer filter;
                
                filter = new StringBuffer();
                for ( int i = 0 ; i < _query.length - 1 ; ++i ) {
                    filter.append( _query[ i ] );
                    if ( _paramValues[ i ] != null )
                        filter.append( _paramValues[ i ].toString() );
                }
                filter.append( _query[ _query.length - 1 ] );
                _position = 0;
                _lastResult = null;
                _paramValues = new Object[ _paramTypes.length ];
                _results = ( (LDAPConnection) conn ).search( _rootDN, LDAPv2.SCOPE_ONE, filter.toString(), null, false );
            } catch ( LDAPException except ) {
                if ( except.getLDAPResultCode() == LDAPException.SERVER_DOWN || 
                     except.getLDAPResultCode() == LDAPException.CONNECT_ERROR )
                    throw new FatalPersistenceException( except );
                throw new PersistenceException( except );
            }
        }
        
        
        public Object nextIdentity()
            throws PersistenceException
        {
            _lastResult = null;
            if ( _results.hasMoreElements() ) {
                Object result;
                
                result = _results.nextElement();
                if ( result instanceof LDAPEntry ) {
                    _lastResult = (LDAPEntry) result;
                    return getIdentityFromDN( _lastResult.getDN() );
                } else if ( result instanceof LDAPReferralException ) {
                    // No support for referrals in this release
                    throw new PersistenceException( (LDAPReferralException) result );
                } else if ( result instanceof LDAPException ) {
                    throw new PersistenceException( (LDAPException) result );
                }
                ++_position;
            }
            return null;
        }
        
        
        public Object getIdentity( int index )
            throws PersistenceException
        {
            // If just retrieved this record, return it.
            if ( index == _position )
                return getIdentityFromDN( _lastResult.getDN() );
            // No going back in LDAP search results
            if ( index < _position )
                throw new PersistenceException( "Cannot obtain result at index " + index +
                                                " after obtaining result at index " + _position );
            // Traverse as much as needed until you reach new position
            Object identity = null;
            
            while ( index < _position ) {
                identity = nextIdentity();
                if ( identity == null )
                    return null;
            }
            return identity;
        }


        public int getPosition()
            throws PersistenceException
        {
            return _position;
        }
        
        
        public boolean isForwardOnly()
        {
            return true;
        }
        
        
        public Object fetch( Object obj )
            throws ObjectNotFoundException, PersistenceException
        {
            LDAPAttributeSet ldapSet;
            LDAPAttribute    ldapAttr;
            DAXFieldDesc     field;
            Enumeration      enum;
            
            if ( _lastResult == null )
                throw new PersistenceException( "Internal error: fetch called without an identity returned from getIdentity/nextIdentity" );
            
            ldapSet = _lastResult.getAttributeSet();
            for ( int i = 0 ; i < ldapSet.size() ; ++i ) {
                ldapAttr = ldapSet.elementAt( i );
                if ( ldapAttr.getName().equals( "objectclass" ) ) {
                    // No need to load or match objectclass, query took care of that
                } else {
                    field = (DAXFieldDesc) _fields.get( ldapAttr.getName() );
                    if ( field != null ) {
                        field.setValue( obj, _lastResult );
                    } else if ( _attrField != null ) {
                        Hashtable     attrSet;
                        
                        attrSet = (Hashtable) _attrField.getValue( obj );
                        if ( attrSet == null ) {
                            attrSet = new Hashtable();
                            _attrField.setValue( obj, attrSet );
                        }
                        attrSet.put( ldapAttr.getName(), ldapAttr.getStringValueArray() );
                    }
                }
            }
            _lastResult = null;
            return null;
        }
        
    }


}

