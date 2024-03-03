/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2010 14:46:39
 */
package de.mnet.migration.common.main;

import static org.testng.Assert.*;

import java.time.*;
import javax.annotation.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationIntegrationTest;
import de.mnet.migration.common.MigrationResultDao;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 *
 */
@TestExecutionListeners({ TransactionalTestExecutionListener.class })
@Transactional
@ContextConfiguration({ "/de/mnet/migration/common/resources/base-migration.xml",
        "/de/mnet/migration/common/main/database-logger-test.xml" })
public class DatabaseLoggerTest extends MigrationIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseLoggerTest.class);
    public static final long TEST_DATA_LENGTH = 1000;

    @Autowired
    private DatabaseLoggerImpl log;
    @Autowired
    private MigrationResultDao migResDao;
    @Resource(name = "testDataSource")
    private DataSource dataSource;

    @Test(groups = "service")
    public void testDatabaseLogger() throws Exception {

        JdbcTemplate template = new JdbcTemplate(dataSource);

        MigrationResult migrationResult = new MigrationResult("TEST");

        final Object simulateOldValueMigResDao = ReflectionTestUtils.getField(migResDao, "simulate");
        final Object simulateOldValueLog = ReflectionTestUtils.getField(log, "simulate");
        ReflectionTestUtils.setField(migResDao, "simulate", Boolean.FALSE);
        ReflectionTestUtils.setField(log, "simulate", Boolean.FALSE);
        try {
            migResDao.saveMigrationResult(migrationResult, "TEST");

            LocalDateTime start = LocalDateTime.now();

            for (long i = 0; i < TEST_DATA_LENGTH; ++i) {
                SourceIdList sources = new SourceIdList(new SourceTargetId("test-1", i), new SourceTargetId("test-2", i));
                TargetIdList targets = new TargetIdList(new SourceTargetId("test-1", i), new SourceTargetId("test-2", i));
                TransformationResult transformationResult = new TransformationResult(TransformationStatus.OK,
                        sources, targets, "Info", 0L, "TEST", null);
                log.log(transformationResult, migrationResult);
            }

            LocalDateTime end = LocalDateTime.now();

            dataSource.getConnection().commit();

            int num = template.queryForObject("SELECT count(*) FROM " + log.getLogIdTableName() + " lid " +
                    "JOIN " + log.getLogTableName() + " l ON l.LOG_ID = lid.LOG_ID " +
                    "WHERE l.MIGRESULT_ID = ?", new Object[] { migrationResult.getId() }, Integer.class);
            assertEquals(num, 4 * TEST_DATA_LENGTH);

            long duration = Duration.between(start, end).toMillis();
            LOGGER.info("testDatabaseLogger() - Time: " + (duration / 1000) + "." + (duration % 1000) + " seconds");
        }
        finally {
            ReflectionTestUtils.setField(migResDao, "simulate", simulateOldValueMigResDao);
            ReflectionTestUtils.setField(log, "simulate", simulateOldValueLog);
        }
    }

}
