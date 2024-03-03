/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2010 16:59:47
 */

package de.mnet.migration.common.main;

import javax.annotation.*;
import javax.sql.*;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.migration.common.DatabaseLogger;
import de.mnet.migration.common.MigrationPostHook;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.exception.TransformationException;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;

/**
 *
 */
public class StoredProcedurePostHook implements MigrationPostHook {

    @Autowired
    private DatabaseLogger databaseLogger;
    private DataSource dataSource;
    private String procedureName;
    private int maxNumberNotMigrated = Integer.MAX_VALUE;
    MigrationStoredProcedure migrationStoredProcedure;

    /**
     * @see de.mnet.migration.common.MigrationPostHook#call(de.mnet.migration.common.result.MigrationResult)
     */
    @Override
    public MigrationResult call(MigrationResult migrationResult) {
        Validate.notNull(migrationResult, "The migration result cannot be null");

        int anzNotMigration = migrationResult.getBadData() + migrationResult.getErrors();
        if (anzNotMigration > maxNumberNotMigrated) {
            throw new TransformationException(
                    new TransformationResult(TransformationStatus.ERROR, null, null,
                            String.format("Not running PostHook because too many bad data: #Not Migrated: %d, Threshold: %d", anzNotMigration, maxNumberNotMigrated),
                            MigrationTransformator.CLASS_DEFAULT, "", null)
            );
        }

        migrationStoredProcedure.execute(migrationResult.getId());

        MigrationResult updatedResult = databaseLogger
                .readMigrationResultFromDatabase(migrationResult);
        return updatedResult;
    }

    @PostConstruct
    public void setup() {
        migrationStoredProcedure = new MigrationStoredProcedure(dataSource, procedureName);
    }

    /**
     * injected by Spring
     */
    public void setDatabaseLogger(DatabaseLogger dbLogger) {
        this.databaseLogger = dbLogger;
    }

    /**
     * Injected
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;

    }

    /**
     * Injected
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxNumberNotMigrated(int maxNumberNotMigrated) {
        this.maxNumberNotMigrated = maxNumberNotMigrated;
    }
}
