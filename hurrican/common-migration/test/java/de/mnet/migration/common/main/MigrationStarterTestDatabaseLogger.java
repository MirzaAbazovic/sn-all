/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 14:17:35
 */
package de.mnet.migration.common.main;

import com.google.common.base.Preconditions;

import de.mnet.migration.common.DatabaseLogger;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 *
 */
public class MigrationStarterTestDatabaseLogger implements DatabaseLogger {
    @Override
    public void setSimulate(Boolean simulate) {
    }

    @Override
    public boolean log(TransformationResult transformationResult, MigrationResult migrationResult) {
        return true;
    }

    @Override
    public int loadNumberOfLogEntries(Long migresultId, TransformationStatus severity) {
        return 0;
    }

    @Override
    public MigrationResult readMigrationResultFromDatabase(MigrationResult migrationResult) {
        Preconditions.checkNotNull(migrationResult);
        Preconditions.checkNotNull(migrationResult.getId());
        return migrationResult;
    }

    @Override
    public void createIndices() {
    }

    @Override
    public void dropIndices() {
    }
}
