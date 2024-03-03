/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2010 10:29:17
 */

package de.mnet.migration.common.main;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import javax.sql.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.DatabaseLogger;
import de.mnet.migration.common.exception.TransformationException;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.MigrationStatus;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 *
 */
public class StoredProcedureHookImplTest extends MigrationBaseTest {

    private static final String MIG_NAME = "my migration";
    private static final int COUNTER = 21;
    private static final int MIGRATED = 6;
    private static final int INFOS = 5;
    private static final int WARNINGS = 4;
    private static final int SKIPPED = 3;
    private static final int BAD_DATA = 2;
    private static final int ERRORS = 1;

    private DatabaseLogger databaseLogger;
    private DataSource dataSource;
    private MigrationStoredProcedure migrationStoredProcedure;
    private String procedureName;

    private MigrationResult migrationResult;
    private StoredProcedurePostHook migrationPostHook;

    @BeforeMethod(groups = "unit")
    public void setup() {
        migrationResult = new MigrationResult(MIG_NAME, MigrationStatus.ERROR,
                COUNTER, MIGRATED, INFOS, WARNINGS, SKIPPED, BAD_DATA, ERRORS);
        procedureName = "myProcedure";
        configureMockito();
        migrationPostHook = new StoredProcedurePostHook();
        migrationPostHook.setDatabaseLogger(databaseLogger);
        migrationPostHook.setDataSource(dataSource);
        migrationPostHook.setProcedureName(procedureName);
        migrationPostHook.migrationStoredProcedure = migrationStoredProcedure;
    }

    @Test(groups = "unit")
    public void testSuccess() {
        migrationResult.setId(1L);
        MigrationResult result = migrationPostHook.call(migrationResult);
        verify(databaseLogger, never()).log(any(TransformationResult.class), any(MigrationResult.class));

        assertEquals(result.getMigrated(), MIGRATED + 1);
        assertEquals(result.getInfos(), INFOS - 1);
        assertEquals(result.getSkipped(), SKIPPED + 1);
        assertEquals(result.getBadData(), BAD_DATA - 1);
    }

    @Test(groups = "unit")
    public void testFailIfTooManyErrors() {
        migrationResult.setId(1L);
        migrationResult.setBadData(6);
        migrationResult.setErrors(7);
        migrationPostHook.setMaxNumberNotMigrated(10);
        try {
            migrationPostHook.call(migrationResult);
        }
        catch (TransformationException e) {
            assertEquals(TransformationStatus.ERROR, e.getResult().getTranformationStatus());
            return;
        }
        fail();
    }

    @Test(groups = "unit")
    public void testDontFailIfFewErrors() {
        migrationResult.setId(1L);
        migrationResult.setBadData(5);
        migrationResult.setErrors(0);
        migrationPostHook.setMaxNumberNotMigrated(10);
        migrationPostHook.call(migrationResult);
    }

    @Test(groups = "unit")
    public void testDontFailManyErrors() {
        migrationResult.setId(1L);
        migrationResult.setBadData(1000000);
        migrationResult.setErrors(2344566);
        migrationPostHook.call(migrationResult);
    }

    private void configureMockito() {
        databaseLogger = mock(DatabaseLogger.class);
        dataSource = mock(DataSource.class);
        migrationStoredProcedure = mock(MigrationStoredProcedure.class);

        final MigrationResult mockResult = new MigrationResult(MIG_NAME, MigrationStatus.ERROR,
                COUNTER, MIGRATED + 1, INFOS - 1, WARNINGS, SKIPPED + 1, BAD_DATA - 1, ERRORS);
        when(databaseLogger.readMigrationResultFromDatabase(any(MigrationResult.class)))
                .thenReturn(mockResult);

        doThrow(new RuntimeException()).when(migrationStoredProcedure).execute(eq(2L));

    }
}
