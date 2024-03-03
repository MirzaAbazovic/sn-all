/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 16:48:44
 */
package de.mnet.migration.common.dao;

import java.sql.*;
import java.util.*;
import javax.sql.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import de.mnet.migration.common.MigrationDao;


/**
 * Dao, das via einem Connected SqlResultSet Daten liest. <b>Nicht Thread-Safe!</b>
 *
 *
 */
public class ResultSetMigrationDao<T> extends MigrationDao<T> {
    private static final Logger LOGGER = Logger.getLogger(ResultSetMigrationDao.class);

    private DataSource dataSource;
    private boolean finished = false;

    /**
     * This is able to load chunks of data. It works only if a single ID column is present.
     */
    private static final String SQL = "SELECT * FROM ";

    private RowMapper<T> rowMapper;
    private String tableName;

    private Connection connection;
    private ResultSet resultSet;
    private Statement statement;
    /**
     * optional where clause for the generated sqlStatement
     */
    protected String whereClause;


    @Override
    protected void prepareInternal() {
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String sqlStatement = SQL + "\"" + tableName + "\"";
            if (StringUtils.isNotBlank(whereClause)) {
                sqlStatement += " " + whereClause;
            }
            resultSet = statement.executeQuery(sqlStatement);
        }
        catch (SQLException e) {
            closeResultSet();
            closeConnection();
            throw new RuntimeException("Error creating connection for MigrationDao", e);
        }
    }

    @Override
    protected Iterator<T> getAllInternal() {
        throw new UnsupportedOperationException("Cannot read all sets at once using SqlResultSetMigrationDao!");
    }

    @Override
    protected Iterator<T> getNextBlockInternal() {
        if (finished) {
            return emptyResult;
        }
        List<T> resultList = new ArrayList<T>(blockSize);
        try {
            for (int i = 0; i < blockSize; i++) {
                if (!resultSet.next()) {
                    finished = true;
                    closeAllDatabaseConnections();
                    break;
                }
                T row = rowMapper.mapRow(resultSet, resultSet.getRow());
                resultList.add(row);
            }
            return resultList.iterator();
        }
        catch (SQLException e) {
            closeAllDatabaseConnections();
            throw new RuntimeException("Error reading data in ResultSetMigrationDao", e);
        }
    }

    /**
     * Injected
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Injected
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Injected
     */
    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    private void closeConnection() {
        if (null != connection) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing connection for ResultSetMigrationDao", e);
            }
        }
    }

    private void closeResultSet() {
        if (null != resultSet) {
            try {
                resultSet.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing result set for ResultSetMigrationDao", e);
            }
        }
    }

    private void closeStatement() {
        if (null != statement) {
            try {
                statement.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing statement for ResultSetMigrationDao", e);
            }
        }
    }

    private void closeAllDatabaseConnections() {
        closeResultSet();
        closeStatement();
        closeConnection();
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

}
