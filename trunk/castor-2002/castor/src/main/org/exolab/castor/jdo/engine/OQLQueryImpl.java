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


package org.exolab.castor.jdo.engine;


import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.oql.Lexer;
import org.exolab.castor.jdo.oql.Parser;
import org.exolab.castor.jdo.oql.Token;
import org.exolab.castor.jdo.oql.TokenTypes;
import org.exolab.castor.jdo.oql.ParseTreeNode;
import org.exolab.castor.jdo.oql.ParseTreeWalker;
import org.exolab.castor.jdo.oql.ParamInfo;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.QueryResults;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceExceptionImpl;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.util.Messages;
import org.exolab.castor.util.Logger;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class OQLQueryImpl
    implements OQLQuery
{


    private PersistenceEngine  _dbEngine;


    private DatabaseImpl       _dbImpl;


    private Class              _objClass;


    private QueryExpression    _expr;


    private Class[]            _bindTypes;


    private Object[]           _bindValues;

    private Hashtable          _paramInfo;


    private int                _fieldNum;


    OQLQueryImpl( DatabaseImpl dbImpl )
    {
        _dbImpl = dbImpl;
    }

   public void bind( Object value )
    {
        if ( _expr == null )
            throw new IllegalStateException( "Must create query before using it" );
        if ( _fieldNum == _paramInfo.size() )
            throw new IllegalArgumentException( "Only " + _paramInfo.size() +
                                                " fields in this query" );
        try {
            ParamInfo info = (ParamInfo) _paramInfo.get(new Integer( _fieldNum + 1 ));
            
            if ( value != null && ! info.getTheClass().isAssignableFrom( value.getClass() ) )
                throw new IllegalArgumentException( "Query paramter " + _fieldNum + " is not of the expected type " + 
                                                    _bindTypes[ _fieldNum ].getName() );
            if ( _bindValues == null )
                _bindValues = new Object[ _bindTypes.length ];

            for (Enumeration e = info.getParamMap().elements(); e.hasMoreElements(); )
            {
                int fieldNum = ( (Integer) e.nextElement() ).intValue();
                _bindValues[ fieldNum - 1 ] = value;
            }
            
        } catch ( IllegalArgumentException except ) {
            throw except;
        }
        ++_fieldNum;
    }

    public void bind( boolean value )
    {
        bind( new Boolean( value ) );
    }


    public void bind( short value )
    {
        bind( new Short( value ) );
    }


    public void bind( int value )
    {
        bind( new Integer( value ) );
    }


    public void bind( long value )
    {
        bind( new Long( value ) );
    }


    public void bind( String value )
    {
        bind( (Object) value );
    }


    public void bind( float value )
    {
        bind( new Float( value ) );
    }


    public void bind( double value )
    {
        bind( new Double( value ) );
    }

    public void create( String oql )
        throws QueryException    
    {

        _fieldNum = 0;
        _expr = null;
        
        Lexer lexer = new Lexer(oql);
        Parser parser = new Parser(lexer);
        ParseTreeNode parseTree = parser.getParseTree();

        _dbEngine = _dbImpl.getPersistenceEngine(); 
        if ( _dbEngine == null )
            throw new QueryException( "Could not get a persistence engine" );

        ParseTreeWalker walker = new ParseTreeWalker(_dbEngine, parseTree);

        _objClass = walker.getObjClass();
        _expr = walker.getQueryExpression();
        _paramInfo = walker.getParamInfo();


        //port param info types back to the format of old bind types.
        //first get the maximum SQL param.
        int max = 0;
        for (Enumeration e = _paramInfo.elements(); e.hasMoreElements(); ) {
            ParamInfo info = (ParamInfo) e.nextElement();
            for (Enumeration f = info.getParamMap().elements(); f.hasMoreElements(); ) 
            {
                int paramIndex = ( (Integer) f.nextElement() ).intValue();
                if (  paramIndex > max )
                    max = paramIndex;
            }
        }
        
        //then create the types array and fill it
        _bindTypes = new Class[max];
        for (Enumeration e = _paramInfo.elements(); e.hasMoreElements(); ) 
        {
            ParamInfo info = (ParamInfo) e.nextElement();
            for (Enumeration f = info.getParamMap().elements(); f.hasMoreElements(); ) 
            {
                int paramIndex = ( (Integer) f.nextElement() ).intValue();
                _bindTypes[ paramIndex - 1 ] = f.getClass();
            }        
        }
         
    }
    
   
    public Enumeration execute()
        throws QueryException, PersistenceException, TransactionNotInProgressException
    {
        return execute( null );
    }


    public Enumeration execute( short accessMode )
        throws QueryException, PersistenceException, TransactionNotInProgressException
    {
        switch ( accessMode ) {
        case Database.ReadOnly:
            return execute( AccessMode.ReadOnly );
        case Database.Shared:
            return execute( AccessMode.Shared );
        case Database.Exclusive:
            return execute( AccessMode.Exclusive );
        case Database.DbLocked:
            return execute( AccessMode.DbLocked );
        default:
            throw new IllegalArgumentException( "Value for 'accessMode' is invalid" );
        }
    }


    private Enumeration execute( AccessMode accessMode )
        throws QueryException, PersistenceException, TransactionNotInProgressException
    {
        QueryResults      results;
        PersistenceQuery  query;
        SQLEngine         engine;
        
        if ( _expr == null )
            throw new IllegalStateException( "Must create query before using it" );
        try {
            try {
                engine = (SQLEngine) _dbEngine.getPersistence( _objClass );
                query = engine.createQuery( _expr, _bindTypes );
                if ( _bindValues != null ) {
                    for ( int i = 0 ; i < _bindValues.length ; ++i )
                        query.setParameter( i, _bindValues[ i ] );
                }
            } catch ( QueryException except ) {
                throw new QueryException( except.getMessage() );
            }
            results = _dbImpl.getTransaction().query( _dbEngine, query, accessMode );
            _fieldNum = 0;
            return new OQLEnumeration( results );
        } catch ( PersistenceException except ) {
            throw except;
        }
    }


    static class OQLEnumeration
        implements Enumeration
    {

        
        private Object       _lastObject;


        private QueryResults _results;


        OQLEnumeration( QueryResults results )
        {
            _results = results;
        }


        public boolean hasMoreElements()
        {
            Object identity;

            if ( _lastObject != null )
                return true;
            if ( _results == null )
                return false;
            try {
                identity = _results.nextIdentity();
                while ( identity != null ) {
                    try {
                        _lastObject = _results.fetch();
                        if ( _lastObject != null )
                            break;
                    } catch ( PersistenceException except ) {
                        identity = _results.nextIdentity();
                    }
                }
                if ( identity == null ) {
                    _results.close();
                    _results = null;
                }
            } catch ( PersistenceException except ) {
                _results.close();
                _results = null;
            }
            return ( _lastObject != null );
        }


        public Object nextElement()
        {
            Object identity;

            if ( _lastObject != null ) {
                Object result;
                
                result = _lastObject;
                _lastObject = null;
                return result;
            }
            if ( _results == null )
                throw new NoSuchElementException();
            try {
                identity = _results.nextIdentity();
                while ( identity != null ) {
                    try {
                        Object result;
                        
                        result = _results.fetch();
                        if ( result != null )
                            return result;
                    } catch ( PersistenceException except ) {
                    }
                    identity = _results.nextIdentity();
                }
                if ( identity == null ) {
                    _results.close();
                    _results = null;
                }
            } catch ( PersistenceException except ) { 
                _results.close();
                _results = null;
            }
            throw new NoSuchElementException();
        }


        protected void finalize()
            throws Throwable
        {
            if ( _results != null ) {
                _results.close();
                _results = null;
            }
        }


    }
    

}
