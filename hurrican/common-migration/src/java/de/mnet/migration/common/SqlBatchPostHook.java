/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2011 14:25:00
 */

package de.mnet.migration.common;

import java.util.*;
import javax.sql.*;
import org.springframework.beans.factory.annotation.Required;

import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Fuehrt nach einer Migration beliebige Anzahl SQL Statements aus.
 */
public class SqlBatchPostHook implements MigrationPostHook {

    List<String> sqlStatements;
    SqlPostHook sqlPostHook;
    DataSource dataSource;

    @Override
    public MigrationResult call(MigrationResult migrationResult) {
        if ((sqlStatements == null) || (sqlStatements.isEmpty()) || (dataSource == null)) {
            migrationResult.addTransformationResult(new TransformationResult(TransformationStatus.WARNING, null, null,
                    "Keine Data Source oder keine SQL Statements verfügbar.", 0L, "SQL_BATCH", null));
            return migrationResult;
        }

        sqlPostHook = new SqlPostHook();
        sqlPostHook.setDataSource(dataSource);
        for (String sqlStatement : sqlStatements) {
            sqlPostHook.setSql(sqlStatement);
            try {
                migrationResult = sqlPostHook.call(migrationResult);
            }
            catch (Exception e) {
                migrationResult.addTransformationResult(new TransformationResult(TransformationStatus.WARNING, null, null,
                        "SQL '" + sqlStatement + "' konnte nicht ausgefuehrt werden", 0L, "SQL_BATCH", e));
            }
        }
        return migrationResult;
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setSqlStatements(List<String> sqlStatements) {
        this.sqlStatements = sqlStatements;
    }

}
