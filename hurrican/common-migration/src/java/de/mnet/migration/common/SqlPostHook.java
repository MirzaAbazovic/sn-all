/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2010 16:41:28
 */

package de.mnet.migration.common;

import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Fuehrt nach einer Migration beliebiges SQL aus.
 */
public class SqlPostHook implements MigrationPostHook {
    private static final Logger LOGGER = Logger.getLogger(SqlPostHook.class);

    private JdbcTemplate jdbcTemplate;
    private String sql;
    private boolean ignoreError = false;

    @Autowired
    private DatabaseLogger databaseLogger;


    @Override
    public MigrationResult call(MigrationResult migrationResult) {
        try {
            jdbcTemplate.execute(sql);
        }
        catch (RuntimeException e) {
            if (!ignoreError) {
                throw e;
            }
            LOGGER.info("call() - SQL Call resulted in exception, but I was configured to ignore it", e);
            TransformationResult transformationResult =
                    new TransformationResult(TransformationStatus.INFO, null, null,
                            "Error (ignored) in post hook: " + e.getMessage(), MigrationTransformator.CLASS_DEFAULT, "", e);
            if (databaseLogger != null) {
                databaseLogger.log(transformationResult, migrationResult);
            }
            migrationResult.addTransformationResult(transformationResult);
        }
        return migrationResult;
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Required
    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

    public void setDatabaseLogger(DatabaseLogger databaseLogger) {
        this.databaseLogger = databaseLogger;
    }
}
