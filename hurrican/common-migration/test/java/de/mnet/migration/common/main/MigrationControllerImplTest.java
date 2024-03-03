/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2009 18:16:34
 */
package de.mnet.migration.common.main;

import static de.mnet.migration.common.MigrationTransformator.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.DatabaseLogger;
import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.MigrationPostHook;
import de.mnet.migration.common.MigrationResultDao;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.dao.DataLoaderMigrationDao;
import de.mnet.migration.common.dao.impl.JdbcDataLoader;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.MigrationStatus;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.CollectionUtil;


/**
 * Tests both MigrationControllerImpl and SqlMigrationDao.
 *
 *
 */
public class MigrationControllerImplTest extends MigrationBaseTest implements ICCService {

    private static final int dataSize = 10000;


    @Test(groups = "unit")
    public void testControllerSuccess() {
        Answer<TransformationResult> answer = new Answer<TransformationResult>() {
            @Override
            public TransformationResult answer(InvocationOnMock invocation) throws Throwable {
                return ok(source("<"), target(">"), "ok");
            }
        };
        MigrationControllerImpl<TestEntity> controller = createController(answer, null, null, true);

        MigrationResult result = controller.doMigration("Suite");

        MigrationResult expectedResult = new MigrationResult("Test", MigrationStatus.SUCCESS, dataSize, dataSize, 0, 0, 0, 0, 0);
        assertEquals(result, expectedResult);
    }


    @Test(groups = "unit")
    public void testControllerErrorIfLoggingFailed() {
        Answer<TransformationResult> answer = new Answer<TransformationResult>() {
            @Override
            public TransformationResult answer(InvocationOnMock invocation) throws Throwable {
                return ok(source("<"), target(">"), "ok");
            }
        };
        MigrationControllerImpl<TestEntity> controller = createController(answer, null, null, false);
        MigrationResult result = controller.doMigration("Suite");
        MigrationResult expectedResult = new MigrationResult("Test", MigrationStatus.ERROR, dataSize, dataSize, 0, 0, 0, 0, 0);
        assertEquals(result, expectedResult);
    }


    @Test(groups = "unit")
    public void testControllerErrorOnPrepare() {
        Answer<TransformationResult> answer = new Answer<TransformationResult>() {
            @Override
            public TransformationResult answer(InvocationOnMock invocation) throws Throwable {
                return ok(source("<"), target(">"), "ok");
            }
        };
        MigrationControllerImpl<TestEntity> controller = createController(answer, null, null, true);
        controller.additionalDataBeans = new ArrayList<MigrationAdditionalData>();
        controller.additionalDataBeans.add(new MigrationAdditionalData() {
            @Override
            public List<TransformationResult> call() throws Exception {
                throw new RuntimeException("Initialization failed");
            }
        });

        MigrationResult result = controller.doMigration("Suite");

        MigrationResult expectedResult = new MigrationResult("Test", MigrationStatus.ERROR, 0, 0, 0, 0, 0, 0, 0);
        assertEquals(result, expectedResult);
    }


    @Test(groups = "unit")
    public void testControllerErrorOnPrepareLoggingError() {
        Answer<TransformationResult> answer = new Answer<TransformationResult>() {
            @Override
            public TransformationResult answer(InvocationOnMock invocation) throws Throwable {
                return ok(source("<"), target(">"), "ok");
            }
        };
        MigrationControllerImpl<TestEntity> controller = createController(answer, null, null, false);
        controller.additionalDataBeans = new ArrayList<MigrationAdditionalData>();
        controller.additionalDataBeans.add(new MigrationAdditionalData() {
            @Override
            public List<TransformationResult> call() throws Exception {
                return CollectionUtil.list(ok(source("<"), target(">"), "ok"));
            }
        });

        MigrationResult result = controller.doMigration("Suite");

        MigrationResult expectedResult = new MigrationResult("Test", MigrationStatus.ERROR, 1, 1, 0, 0, 0, 0, 0);
        assertEquals(result, expectedResult);
    }


    @Test(groups = "unit")
    public void testPostMigrationHook() {
        MigrationResult migrationResult;
        migrationResult = new MigrationResult("name", MigrationStatus.ERROR, 21, 6, 5, 4, 3, 2, 1);

        MigrationPostHook migrationPostHook = mock(MigrationPostHook.class);
        when(migrationPostHook.call(any(MigrationResult.class))).thenReturn(migrationResult);
        MigrationControllerImpl<TestEntity> controller = new MigrationControllerImpl<TestEntity>() {
            @Override
            protected MigrationTransformator<TestEntity> createMigrationTransformator() {
                return null;
            }
        };
        controller.migrationPostHooks = Arrays.asList(migrationPostHook, migrationPostHook);

        MigrationResult result = controller.postMigrationHooks(migrationResult);
        verify(migrationPostHook, times(2)).call(any(MigrationResult.class));

        assertEquals(result.getCounter(), migrationResult.getCounter());
    }


    @Test(groups = "unit")
    public void testController() {
        final List<MigrationTransformator<TestEntity>> transformators = new ArrayList<MigrationTransformator<TestEntity>>();
        Answer<TransformationResult> answer = new Answer<TransformationResult>() {
            @Override
            public TransformationResult answer(InvocationOnMock invocation) throws Throwable {
                switch (((TestEntity) (invocation.getArguments()[0])).id) {
                    case 1111:
                        return error(source("<!"), "error", CLASS_DEFAULT, "", null);
                    case 3333:
                        return error(source("<!"), "error", CLASS_DEFAULT, "", null);
                    case 5555:
                        return badData(source("<!"), "bad data", CLASS_DEFAULT, "", null);
                    case 6666:
                        return info(source("<!"), target(">!"), "info", null, null);
                    case 7777:
                        return warning(source("<!"), target(">!"), "warning", null, null);
                    case 9999:
                        return skipped(source("<!"), "skipped", CLASS_DEFAULT, "");
                    case 99:
                        return skipped(source("<!"), "skipped", CLASS_DEFAULT, "");
                    case 999:
                        return skipped(source("<!"), "skipped", CLASS_DEFAULT, "");
                    default:
                        return ok(source("<"), target(">"), "ok");
                }
            }
        };
        EtAl etAl = new EtAl();
        MigrationControllerImpl<TestEntity> controller = createController(answer, transformators, etAl, true);

        MigrationResult result = controller.doMigration("Suite");

        MigrationResult expectedResult = new MigrationResult("Test", MigrationStatus.ERROR, dataSize, dataSize - 6, 1, 1, 3, 1, 2);
        assertEquals(result, expectedResult);

        verify(etAl.txStatus, times(dataSize + 6)).setRollbackOnly();
        // commit is called all times, in the commit, a rollback request is detected
        verify(etAl.txManager, times(10000)).commit(etAl.txStatus);
        verify(etAl.txManager, never()).rollback(etAl.txStatus);
        // logging
        verify(etAl.logger, times(dataSize - 8)).log(argThat(new TransformationResultMatcher(TransformationStatus.OK)), any(MigrationResult.class));
        verify(etAl.logger, times(3)).log(argThat(new TransformationResultMatcher(TransformationStatus.SKIPPED)), any(MigrationResult.class));
        verify(etAl.logger, times(2)).log(argThat(new TransformationResultMatcher(TransformationStatus.ERROR)), any(MigrationResult.class));
        verify(etAl.logger, times(1)).log(argThat(new TransformationResultMatcher(TransformationStatus.INFO)), any(MigrationResult.class));
        verify(etAl.logger, times(1)).log(argThat(new TransformationResultMatcher(TransformationStatus.WARNING)), any(MigrationResult.class));
        verify(etAl.logger, times(1)).log(argThat(new TransformationResultMatcher(TransformationStatus.BAD_DATA)), any(MigrationResult.class));
    }


    private static class TransformationResultMatcher extends ArgumentMatcher<TransformationResult> {
        private final TransformationStatus status;

        public TransformationResultMatcher(TransformationStatus status) {
            this.status = status;
        }

        @Override
        public boolean matches(Object argument) {
            return ((TransformationResult) argument).getTranformationStatus().equals(status);
        }
    }


    private static class TestDataLoader extends JdbcDataLoader<TestEntity> {

        /**
         * @see de.mnet.migration.common.dao.DataLoader#getSourceData()
         */
        @Override
        public List<TestEntity> getSourceData() {
            ResultSet resultSet = mock(ResultSet.class);
            try {
                final AtomicInteger id = new AtomicInteger(0);
                // This is needed for the RowMapperResultSetExtractor
                when(resultSet.next()).thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        return id.getAndIncrement() < dataSize;
                    }
                });
                //This is needed for TestRowMapper
                when(resultSet.getInt(1)).thenAnswer(new Answer<Integer>() {
                    @Override
                    public Integer answer(InvocationOnMock invocation) throws Throwable {
                        return id.get();
                    }
                });
                //This is needed for TestRowMapper
                when(resultSet.getString(2)).thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return String.valueOf(id.get());
                    }
                });
                RowMapperResultSetExtractor<TestEntity> extractor = new RowMapperResultSetExtractor<TestEntity>(rowMapper);
                return extractor.extractData(resultSet);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Creates an instance of the migration controller, implementing getTransformator, returning a transformator mock
     * using the given answer when transform is called.
     */
    private MigrationControllerImpl<TestEntity> createController(final Answer<TransformationResult> answer,
            final List<MigrationTransformator<TestEntity>> transformators, EtAl etAl, boolean logResult) {
        MigrationControllerImpl<TestEntity> controller = new MigrationControllerImpl<TestEntity>() {
            @Override
            protected MigrationTransformator<TestEntity> createMigrationTransformator() {
                @SuppressWarnings("unchecked")
                MigrationTransformator<TestEntity> transformator = mock(MigrationTransformator.class);
                when(transformator.transform(any(TestEntity.class))).thenAnswer(answer);
                if (transformators != null) {
                    transformators.add(transformator);
                }
                return transformator;
            }
        };

        if (etAl == null) {
            etAl = new EtAl();
        }

        etAl.logger = mock(DatabaseLogger.class);
        when(etAl.logger.log(any(TransformationResult.class), any(MigrationResult.class))).thenReturn(logResult);
        etAl.resultDao = createResultDao();
        etAl.txStatus = mock(TransactionStatus.class);
        etAl.txManager = mock(PlatformTransactionManager.class);
        when(etAl.txManager.getTransaction(any(TransactionDefinition.class))).thenReturn(etAl.txStatus);
        etAl.migrationDao = new DataLoaderMigrationDao<TestEntity>();
        TestDataLoader dataLoader = new TestDataLoader();
        dataLoader.setRowMapper(new TestRowMapper());
        etAl.migrationDao.setDataLoader(dataLoader);

        controller.setDatabaseLogger(etAl.logger);
        controller.setMigrationDao(etAl.migrationDao);
        controller.setMigrationName("Test");
        controller.setMigrationResultDao(etAl.resultDao);
        controller.setNumberOfThreadsInThreadPool(10);
        controller.setSimulate(true);
        controller.setTransactionManager(etAl.txManager);

        return controller;
    }


    /**
     * Returns a stub that correctly merges migration results
     */
    private MigrationResultDao createResultDao() {
        MigrationResultDao resultDao = mock(MigrationResultDao.class);
        when(resultDao.mergeMigrationResult(any(MigrationResult.class), any(MigrationResult.class)))
                .thenAnswer(new Answer<MigrationResult>() {
                    @Override
                    public MigrationResult answer(InvocationOnMock invocation) throws Throwable {
                        return ((MigrationResult) (invocation.getArguments()[0])).mergeWith((MigrationResult) (invocation.getArguments()[1]));
                    }
                });
        return resultDao;
    }


    private static class EtAl {
        DataLoaderMigrationDao<TestEntity> migrationDao;
        PlatformTransactionManager txManager;
        TransactionStatus txStatus;
        MigrationResultDao resultDao;
        DatabaseLogger logger;
    }


    private class TestEntity {
        public int id;
        @SuppressWarnings("unused")
        public String name;

        @SuppressWarnings("unused")
        public TestEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public TestEntity() {
        }
    }


    private class TestRowMapper implements RowMapper<TestEntity> {
        @Override
        public TestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            TestEntity result = new TestEntity();
            result.id = rs.getInt(1);
            result.name = rs.getString(2);
            return result;
        }
    }
}
