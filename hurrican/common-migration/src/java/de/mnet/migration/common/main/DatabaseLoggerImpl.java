/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 10:45:42
 */

package de.mnet.migration.common.main;

import java.util.*;
import javax.sql.*;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import de.mnet.migration.common.DatabaseLogger;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.SourceTargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;

/**
 * Der DatabaseLogger wird genutzt, um Migrations-Logs in die Hurrican-Datenbank zu schreiben.
 */
public class DatabaseLoggerImpl implements DatabaseLogger {
    private static final Logger LOGGER = Logger.getLogger(DatabaseLoggerImpl.class);

    /**
     * SQL Statement to get next ID
     */
    private static final String LOG_GET_ID_PRE = "SELECT "; // hier kommt jetzt der Sequenzname dazwischen!
    private static final String LOG_GET_ID_POST = ".nextval FROM dual";

    /**
     * SQL Insert-Statement-String fuer Loggings.
     */
    private static final String INSERT_PRE = "INSERT ALL "; // hier kommt jetzt der Tabellenname dazwischen!
    private static final String INSERT_INTO = " INTO "; // hier kommt jetzt der Tabellenname dazwischen!
    private static final String LOG_INSERT_POST = " (LOG_ID, MIGRESULT_ID, SCRIPT_NAME, SEVERITY, "
            + "CLASSIFICATION, CLASSIFICATION_STRING, MESSAGE, EXCEPTION_MSG) VALUES (?,?,?,?,?,?,?,?)";
    private static final String ID_INSERT_POST = " (LOG_ID, NUMERIC_ID, STRING_ID, "
            + "NAME, SOURCE) VALUES (?,?,?,?,?)";
    private static final String INSERT_POST = " SELECT * FROM DUAL";

    private static final String LOG_COUNT_PRE = "SELECT COUNT(*) FROM "; // hier kommt jetzt der Tabellenname dazwischen!
    private static final String LOG_COUNT_POST = " ML WHERE ML.MIGRESULT_ID = ? AND ML.SEVERITY=?";

    private Boolean simulate;
    protected DataSource loggingDataSource;

    private String logTableName;
    private String logIdTableName;
    private String logSequenceName;


    private static class IdLog {
        public IdLog(String stmtPart, List<Object> params) {
            this.stmtPart = stmtPart;
            this.params = params;
        }

        String stmtPart;
        List<Object> params;
    }


    @Override
    public boolean log(TransformationResult transformationResult, MigrationResult migrationResult) {
        try {
            long logId = new JdbcTemplate(loggingDataSource).queryForObject(LOG_GET_ID_PRE + getLogSequenceName() + LOG_GET_ID_POST, Long.class);

            String infoText = transformationResult.getInfoText();
            if ((infoText != null) && (infoText.length() >= 4000)) {
                infoText = infoText.substring(0, 2990) + "[...]" + infoText.substring(infoText.length() - 1000);
            }
            String classificationString = transformationResult.getClassificationString();
            if ((classificationString != null) && (classificationString.length() >= 200)) {
                classificationString = classificationString.substring(0, 180) + "[...]" + classificationString.substring(classificationString.length() - 10);
            }

            List<Object> stmtParams = new ArrayList<Object>();
            stmtParams.add(logId);
            stmtParams.add(migrationResult.getId());
            stmtParams.add(migrationResult.getMigrationName());
            stmtParams.add(transformationResult.getTranformationStatus().name());
            stmtParams.add(transformationResult.getClassification());
            stmtParams.add(classificationString);
            stmtParams.add(infoText);
            stmtParams.add(transformationResult.getStackTrace());

            List<IdLog> idLogList = new ArrayList<>();

            idLogList.add(new IdLog(getLogTableName() + LOG_INSERT_POST, stmtParams));
            saveIds(logId, transformationResult.getSourceValues(), idLogList);
            saveIds(logId, transformationResult.getTargetValues(), idLogList);

            if (Boolean.FALSE.equals(simulate)) {
                int count = 0;
                List<Object> params = new ArrayList<Object>();
                StringBuilder builder = new StringBuilder(INSERT_PRE);
                for (IdLog idLog : idLogList) {
                    builder.append(INSERT_INTO + idLog.stmtPart);
                    params.addAll(idLog.params);
                    count += 1;
                    if (count == 100) {
                        doLogIds(builder, params);
                        builder = new StringBuilder(INSERT_PRE);
                        params.clear();
                        count = 0;
                    }
                }
                if (count > 0) {
                    doLogIds(builder, params);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    private void doLogIds(StringBuilder builder, List<Object> params) {
        builder.append(INSERT_POST);
        new JdbcTemplate(loggingDataSource).update(builder.toString(), params.toArray());
    }


    private void saveIds(Long logId, SourceTargetIdList<?> ids, List<IdLog> idLogList) {
        if (ids != null) {
            for (SourceTargetId id : ids.getList()) {
                List<Object> params = new ArrayList<Object>();
                params.add(logId);
                params.add(id.getNumericId());
                params.add(id.getStringId());
                params.add(id.getName());
                params.add(id.getSource());
                idLogList.add(new IdLog(getLogIdTableName() + ID_INSERT_POST, params));
            }
        }
    }

    @Override
    public int loadNumberOfLogEntries(Long migresultId, TransformationStatus severity) {
        return new JdbcTemplate(loggingDataSource)
                .queryForObject(LOG_COUNT_PRE + getLogTableName() + LOG_COUNT_POST, Integer.class, migresultId, severity.name());
    }

    @Override
    public MigrationResult readMigrationResultFromDatabase(MigrationResult migrationResult) {
        Preconditions.checkNotNull(migrationResult);
        Preconditions.checkNotNull(migrationResult.getId());
        migrationResult.setErrors(loadNumberOfLogEntries(migrationResult.getId(), TransformationStatus.ERROR));
        migrationResult.setSkipped(loadNumberOfLogEntries(migrationResult.getId(), TransformationStatus.SKIPPED));
        migrationResult.setBadData(loadNumberOfLogEntries(migrationResult.getId(), TransformationStatus.BAD_DATA));
        migrationResult.setWarnings(loadNumberOfLogEntries(migrationResult.getId(), TransformationStatus.WARNING));
        migrationResult.setInfos(loadNumberOfLogEntries(migrationResult.getId(), TransformationStatus.INFO));
        migrationResult.setMigrated(loadNumberOfLogEntries(migrationResult.getId(), TransformationStatus.OK));
        migrationResult.setCounter(migrationResult.getErrors() + migrationResult.getSkipped() + migrationResult.getBadData() +
                migrationResult.getWarnings() + migrationResult.getInfos() + migrationResult.getMigrated());
        return migrationResult;
    }

    @Override
    public void createIndices() {
        executeSilent("CREATE INDEX IX_NAVI_MIG_IDS_LOG_ID ON NAVI_MIG_IDS (LOG_ID)");
        executeSilent("CREATE INDEX IX_NAVI_MIG_IDS_NUM_ID ON NAVI_MIG_IDS (NUMERIC_ID)");
        executeSilent("CREATE INDEX IX_NAVI_MIG_IDS_STR_ID ON NAVI_MIG_IDS (STRING_ID)");
        executeSilent("CREATE INDEX IX_NAVI_MIG_LOG_SCRIPT ON NAVI_MIG_LOG (SCRIPT_NAME)");
    }

    @Override
    public void dropIndices() {
        executeSilent("DROP INDEX IX_NAVI_MIG_IDS_LOG_ID");
        executeSilent("DROP INDEX IX_NAVI_MIG_IDS_STR_ID");
        executeSilent("DROP INDEX IX_NAVI_MIG_IDS_NUM_ID");
        executeSilent("DROP INDEX IX_NAVI_MIG_LOG_SCRIPT");
    }

    private void executeSilent(String sql) {
        try {
            new JdbcTemplate(loggingDataSource).execute(sql);
        }
        catch (Exception e) {
            LOGGER.warn("Error executing SQL: " + sql, e);
        }
    }


    public String getLogTableName() {
        return logTableName;
    }

    public String getLogSequenceName() {
        return logSequenceName;
    }

    public String getLogIdTableName() {
        return logIdTableName;
    }


    /**
     * Injected by Spring
     */
    @Override
    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }

    /**
     * Injected by Spring
     */
    public void setLoggingDataSource(DataSource loggingDataSource) {
        this.loggingDataSource = loggingDataSource;
    }

    /**
     * Injected by Spring
     */
    public void setLogTableName(String logTableName) {
        this.logTableName = logTableName;
    }

    /**
     * Injected by Spring
     */
    public void setLogSequenceName(String logSequenceName) {
        this.logSequenceName = logSequenceName;
    }

    /**
     * Injected by Spring
     */
    public void setLogIdTableName(String logIdTableName) {
        this.logIdTableName = logIdTableName;
    }
}
