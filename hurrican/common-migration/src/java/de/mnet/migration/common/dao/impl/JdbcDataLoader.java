/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 13:33:28
 */

package de.mnet.migration.common.dao.impl;

import java.util.*;
import javax.sql.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.mnet.migration.common.dao.DataLoader;

/**
 * @param <TYPE> Datentyp des Source-Objekt, auf das der RowMappper abbildet.
 */
public class JdbcDataLoader<TYPE> implements DataLoader<TYPE> {
    private static final Logger LOGGER = Logger.getLogger(JdbcDataLoader.class);

    protected RowMapper<TYPE> rowMapper;
    protected DataSource dataSource;
    /**
     * Name of the source table or view
     */
    protected String tableName;
    /**
     * optional where clause for the generated sqlStatement
     */
    protected String whereClause;

    @Override
    public List<TYPE> getSourceData() {
        List<TYPE> result = new JdbcTemplate(dataSource).query(generateSqlSatement(), rowMapper);
        LOGGER.info(result.size() + " rows for table = " + getSourceObjectName() + " loaded");
        return result;
    }

    private String generateSqlSatement() {
        String sqlStatement = "SELECT * FROM " + tableName;
        if (StringUtils.isNotBlank(whereClause)) {
            sqlStatement += " " + whereClause;
        }
        LOGGER.info("generateSqlSatement() - generated SQL-Statement is: " + sqlStatement);
        return sqlStatement;
    }

    @Override
    public String getSourceObjectName() {
        return tableName;
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setRowMapper(RowMapper<TYPE> rowMapper) {
        this.rowMapper = rowMapper;
    }

    @Required
    public void setTableName(String tableNameToSet) {
        String tableNameToSet1 = tableNameToSet;
        if (!tableNameToSet1.startsWith("\"")) {
            tableNameToSet1 = "\"" + tableNameToSet1;
        }
        if (!tableNameToSet1.endsWith("\"")) {
            tableNameToSet1 = tableNameToSet1 + "\"";
        }
        this.tableName = tableNameToSet1;
    }


    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

}
