/*
 * Copyright 2005 Werner Guttmann, Ralf Joachim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.jdo.engine;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.jdo.conf.Database;
import org.exolab.castor.jdo.conf.DatabaseChoice;
import org.exolab.castor.jdo.conf.Param;
import org.exolab.castor.jdo.drivers.ConnectionProxy;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.Messages;
import org.exolab.castor.xml.UnmarshalHandler;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 * @since 0.9.9
 */
public final class DataSourceConnectionFactory extends AbstractConnectionFactory {
    //--------------------------------------------------------------------------

    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     *  Commons Logging</a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(DataSourceConnectionFactory.class);

    //--------------------------------------------------------------------------

    /**
     * Initialize JDBC DataSource instance with the given database configuration
     * instances and the given class loader.
     * 
     * @param  database     Database configuration.
     * @param  loader       ClassLoader to use. 
     * @return The initalized DataSource.
     * @throws MappingException Problem related to analysing the JDO configuration.
     */
    public static DataSource loadDataSource(final Database database,
                                            final ClassLoader loader) 
    throws MappingException {
        DataSource dataSource;
        Param[] parameters;
        Param param;

        DatabaseChoice dbChoice = database.getDatabaseChoice();
        String className = dbChoice.getDataSource().getClassName();
        ClassLoader classLoader = loader;
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        try {
            Class dsClass = Class.forName(className, true, classLoader);
            dataSource = (DataSource) dsClass.newInstance();
        } catch (Exception e) {
            String msg = Messages.format("jdo.engine.classNotInstantiable", className);
            LOG.error(msg, e);
            throw new MappingException(msg, e);
        }

        parameters = database.getDatabaseChoice().getDataSource().getParam();
        
        Unmarshaller unmarshaller = new Unmarshaller(dataSource);
        UnmarshalHandler handler = unmarshaller.createHandler();
        
        try {
            handler.startDocument();
            handler.startElement("data-source", null);
            
            for (int i = 0; i < parameters.length; i++) {
               param = (Param) parameters[i];
               handler.startElement(param.getName(), null);
               handler.characters(param.getValue().toCharArray(), 0,
                                  param.getValue().length());
               handler.endElement(param.getName());
            }
            
            handler.endElement("data-source");
            handler.endDocument();
        } catch (SAXException e) {
            String msg = Messages.format("jdo.engine.unableToParseDataSource", className);
            LOG.error(msg, e);
            throw new MappingException(msg, e);
        }

        return dataSource;
    }

    //--------------------------------------------------------------------------

    /** The data source when using a JDBC dataSource. */
    private DataSource        _dataSource = null;

    //--------------------------------------------------------------------------

    /**
     * Constructs a new DataSourceConnectionFactory with given database and mapping.
     * 
     * @param database  The database configuration.
     * @param mapping   The mapping to load.
     */
    public DataSourceConnectionFactory(final Database database, final Mapping mapping) {
        super(database, mapping);
    }

    /**
     * @see org.castor.jdo.engine.AbstractConnectionFactory#initializeFactory()
     */
    protected void initializeFactory() throws MappingException {
        _dataSource = loadDataSource(getDatabase(), getMapping().getClassLoader());

        if (LOG.isDebugEnabled()) {
            DatabaseChoice dbc = getDatabase().getDatabaseChoice();
            LOG.debug("Using DataSource: " + dbc.getDataSource().getClassName());
        }
    }

    //--------------------------------------------------------------------------

    /**
     * @see org.castor.jdo.engine.ConnectionFactory#createConnection()
     */
    public Connection createConnection () throws SQLException {
        return ConnectionProxy.newConnectionProxy(
                _dataSource.getConnection(), getClass().getName());
    }

    //--------------------------------------------------------------------------
}
