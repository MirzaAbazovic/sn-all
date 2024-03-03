/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2010 17:38:10
 */
package de.mnet.migration.base;

import java.io.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;


/**
 *
 */
public abstract class AbstractMigrationLogDumpTest extends MigrationIntegrationTest {
    private static final String table = "MIGRATION_MIG_RESULT";
    private static final String logTable = "MIGRATION_MIG_LOG";

    private String getTable() {
        return table;
    }

    private String getLogtable() {
        return logTable;
    }

    public abstract DataSource getDataSource();

    public abstract String getFilePrefix();

    @Test(groups = "postmigration")
    public void dumpMigrationLog() throws IOException {
        String queryMigResult = "SELECT * FROM " + getTable() + " order by INSERT_TIMESTAMP";
        SqlResultDumpRowCallbackHandler cbhMigResult = new SqlResultDumpRowCallbackHandler(getFilePrefix() + "migrationresult-e2e.csv");
        new JdbcTemplate(getDataSource()).query(queryMigResult, cbhMigResult);
        cbhMigResult.closeFile();

        String queryMigLog = "select count(*) anz, mr.migration_name, ml.severity, ml.classification, max(ml.classification_string) classification_string, max(ml.message) example_message" +
                " from " + getLogtable() + " ml " +
                " join " + getTable() + " mr " +
                " on ML.MIGRESULT_ID = MR.ID " +
                " where ml.severity not in ('OK', 'SKIPPED', 'INFO') " +
                " group by ml.classification, ml.severity, mr.migration_name " +
                " order by anz desc";
        SqlResultDumpRowCallbackHandler cbhMigLog = new SqlResultDumpRowCallbackHandler(getFilePrefix() + "miglog-e2e.csv");
        new JdbcTemplate(getDataSource()).query(queryMigLog, cbhMigLog);
        cbhMigLog.closeFile();
    }


}
