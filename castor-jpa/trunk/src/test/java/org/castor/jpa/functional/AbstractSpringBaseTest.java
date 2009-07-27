/*
 * Copyright 2009 Lukas Lang
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
package org.castor.jpa.functional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.castor.jpa.functional.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Provides a base class with a Spring application context and a few helper
 * methods.
 * 
 * @author lukas.lang
 * 
 */
@ContextConfiguration(locations = { "/spring-test-applicationContext.xml" })
public abstract class AbstractSpringBaseTest extends AbstractJUnit4SpringContextTests {

    /**
     * The {@link DataSource} to use. Injected by Spring.
     */
    @Autowired
    private DataSource dataSource;

    /**
     * This helper method executes a prepared statement including the given
     * parameters.
     * 
     * @param query
     *            is a native SQL query.
     * @param parameters
     *            is an array of {@link Object}s used as parameters.
     * @return the same as {@link PreparedStatement#executeUpdate()}.
     * @throws SQLException
     *             in case execution fails.
     */
    protected final int executeUpdate(String query, Object... parameters) throws SQLException {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
                Object parameter = parameters[parameterIndex];
                preparedStatement.setObject(parameterIndex + 1, parameter);
            }

            // Execute query.
            int numberAffected = preparedStatement.executeUpdate();
            // Release resources.
            preparedStatement.close();
            connection.close();

            return numberAffected;

        } catch (SQLException e) {
            throw e;
        }

    }

    /**
     * Deletes all tuples from the specified relation.
     * 
     * @param table
     *            an existing relation.
     * @throws SQLException
     *             in case delete fails.
     */
    protected final void deleteFromTable(String table) throws SQLException {
        executeUpdate("DELETE FROM " + table);
    }

    /**
     * Returns the number of tuples in the specified relation.
     * 
     * @param table
     *            an existing relation.
     * @return the number of tuples.
     * @throws SQLException
     *             in case query fails.
     */
    protected final int countRowsInTable(String table) throws SQLException {
        String query = "SELECT COUNT(0) AS count FROM " + table;
        Connection connection = this.dataSource.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);
        int count = -1;
        if (resultSet.next()) {
            count = resultSet.getInt("count");
        } else {
            throw new IllegalStateException("Could not get result from query >" + query + "<.");
        }
        // Release resources.
        stmt.close();
        connection.close();
        if (count == -1) {
            throw new IllegalStateException("Could not get result from query >" + query + "<.");
        }
        return count;
    }

    protected final void verifyPersistentBook(Book book) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int fetchSize = 0;
        long isbn = 0;
        String title = null;

        try {
            // Load the book from the database.
            connection = this.dataSource.getConnection();
            preparedStatement = connection
                    .prepareStatement("SELECT isbn, title FROM book WHERE isbn = ?");
            preparedStatement.setObject(1, Long.valueOf(book.getIsbn()));
            resultSet = preparedStatement.executeQuery();

            fetchSize = resultSet.getFetchSize();
            resultSet.next();

            // Get values from result set.
            isbn = resultSet.getLong(1);
            title = resultSet.getString(2);

        } catch (SQLException e) {
            fail("Could not verify book instance: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release resources.
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }

        // Verify result.
        assertEquals(1, fetchSize);
        assertEquals(book.getIsbn(), isbn);
        assertEquals(book.getTitle(), title);
    }
}
