package org.castor.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.TransactionNotInProgressException;

public class CastorQuery implements Query
{
    private Database database = null;
    private OQLQuery query = null;
    private Map parameters = new HashMap();

    public CastorQuery(String ejbqlString, Database database)
    {
        setDatabase(database);
        try {
            query = this.database.getNamedQuery(ejbqlString);
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("Problem creating query instance", e);
        }
    }
    
    public CastorQuery(String sqlString, Class resultClass, Database database) {
        setDatabase(database);
        try {
            query = this.database.getOQLQuery("CALL SQL " + sqlString + " AS " + resultClass.getName());
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("Problem creating query instance", e);
        }
    }

    /**
     * Creates an instance of CastorQuery, deriving the query string from the 
     * OQLQuery passed as an argument.
     * @param query An OQLQuery instance.
     */
    public CastorQuery(OQLQuery query)
    {
        setQuery(query);
    }

    private void setQuery(OQLQuery query)
    {
        this.query = query;
    }

    private void setDatabase(Database database) {
        this.database = database;
    }
    
    private void bindParameters (OQLQuery query, Map parameters) {  //FIXME: Not robust. Parameters must be sorted in the map (same order as in the query itself). Possible: sort parameters first
        if (!parameters.isEmpty()) {
            Iterator values = parameters.values().iterator();
            while (values.hasNext()) {
                query.bind(values.next());
            }
        }
    }
    public List getResultList()
    {
        List results = new ArrayList();
        bindParameters(query, parameters);
        QueryResults queryResults;
        
        try {
            queryResults = query.execute();
            while (queryResults.hasMore()) {
                results.add (queryResults.next());
            }
        } catch (QueryException e) {
            throw new javax.persistence.PersistenceException("Problem executing EJBQL query", e);
        } catch (TransactionNotInProgressException e) {
            throw new javax.persistence.PersistenceException("Problem executing EJBQL query", e);
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("Problem executing EJBQL query", e);
        }
        return results;
    }

    public Object getSingleResult()
    {
        Object result = null;
        bindParameters(query, parameters);
        QueryResults queryResults;
        try {
            queryResults = query.execute();
            if (queryResults.hasMore()) {
                result = queryResults.next();
            }
        } catch (QueryException e) {
            throw new javax.persistence.PersistenceException("Problem executing EJBQL query", e);
        } catch (TransactionNotInProgressException e) {
            throw new javax.persistence.PersistenceException("Problem executing EJBQL query", e);
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("Problem executing EJBQL query", e);
        }
        return result;
    }

    public int executeUpdate()
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setMaxResults(int maxResult)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setFirstResult(int startPosition)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setHint(String hintName, Object value)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setParameter(String name, Object value)
    {
        parameters.put(name, value);
        return this;
    }

    public Query setParameter(String name, Date value, TemporalType temporalType)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setParameter(String name, Calendar value, TemporalType temporalType)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setParameter(int position, Object value)
    {
        parameters.put(new Integer(position), value);
        return this;
    }

    public Query setParameter(int position, Date value, TemporalType temporalType)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setParameter(int position, Calendar value, TemporalType temporalType)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

    public Query setFlushMode(FlushModeType flushMode)
    {
        // TODO: Implement !!!
        throw new UnsupportedOperationException();
    }

}
