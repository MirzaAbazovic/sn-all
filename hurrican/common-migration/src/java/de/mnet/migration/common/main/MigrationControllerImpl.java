/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2009 16:55:36
 */
package de.mnet.migration.common.main;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.mnet.migration.common.DatabaseLogger;
import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.MigrationController;
import de.mnet.migration.common.MigrationControllerTransactionStatusAware;
import de.mnet.migration.common.MigrationDao;
import de.mnet.migration.common.MigrationPostHook;
import de.mnet.migration.common.MigrationResultDao;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.exception.TransformationException;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.MigrationStatus;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;
import de.mnet.migration.common.util.Pair;


/**
 *
 *
 */
public abstract class MigrationControllerImpl<T> implements MigrationController {
    private static final Logger LOGGER = Logger.getLogger(MigrationControllerImpl.class);

    protected PlatformTransactionManager transactionManager;
    protected DatabaseLogger databaseLogger;
    protected MigrationResultDao migrationResultDao;

    @Autowired(required = false)
    List<MigrationPostHook> migrationPostHooks;

    protected ExecutorService executorService;

    private int numberOfThreadsInThreadPool = 1;
    private int tasks = 0;
    private boolean removeIndices = false;

    @Autowired(required = false)
    List<MigrationAdditionalData> additionalDataBeans;

    @Autowired(required = false)
    private List<MigrationControllerTransactionStatusAware> transactionStatusAwareBeans;

    @Value("${migration.failonerror}")
    boolean failOnError;

    private MigrationDao<T> migrationDao;
    protected String migrationName;
    protected String migrationSuite;
    protected Boolean simulate = Boolean.TRUE;


    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @see de.mnet.migration.common.MigrationController#doMigration(java.lang.String)
     */
    @Override
    public MigrationResult doMigration(String migrationSuite) {
        this.migrationSuite = migrationSuite;
        initializeThreadPool();

        MigrationResult migrationResult = new MigrationResult(migrationName);
        beforeMigration(migrationResult);

        prepareData(migrationResult);
        if (MigrationStatus.ERROR == migrationResult.getStatus()) {
            // If something went wrong on Startup save the MigrationResult and return!
            migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
            return migrationResult;
        }

        if (removeIndices) {
            databaseLogger.dropIndices();
        }
        executeMigrationThreadSafe(migrationResult);
        if (removeIndices) {
            databaseLogger.createIndices();
        }

        if (MigrationStatus.ERROR != migrationResult.getStatus() || !failOnError) {
            migrationResult = postMigrationHooks(migrationResult);
        }

        afterMigration(migrationResult);
        return migrationResult;
    }

    private void initializeThreadPool() {
        executorService = Executors.newFixedThreadPool(numberOfThreadsInThreadPool);
    }

    protected void beforeMigration(MigrationResult migrationResult) {
        migrationResult.setStart(LocalDateTime.now());
        migrationResult.setStatus(MigrationStatus.RUNNING);
        migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
    }

    private void prepareData(MigrationResult migrationResult) {
        ExecutorService initExecutor = Executors.newFixedThreadPool(numberOfThreadsInThreadPool);
        List<Future<List<TransformationResult>>> results = new ArrayList<Future<List<TransformationResult>>>();
        if ((additionalDataBeans != null) && (!additionalDataBeans.isEmpty())) {
            for (MigrationAdditionalData data : additionalDataBeans) {
                Future<List<TransformationResult>> result = initExecutor.submit(data);
                results.add(result);
            }
        }
        Future<List<TransformationResult>> dataPreparation = initExecutor.submit(new Callable<List<TransformationResult>>() {
            @Override
            public List<TransformationResult> call() throws Exception {
                migrationDao.prepare();
                return migrationDao.getPreparationResult();
            }
        });
        results.add(dataPreparation);
        // manuelle Überprüfung, ob alle Daten
        // erfolgreich ausgeführt worden sind
        for (Future<List<TransformationResult>> future : results) {
            try {
                List<TransformationResult> transformationResults = future.get();
                for (TransformationResult transformationResult : transformationResults) {
                    if (!databaseLogger.log(transformationResult, migrationResult)) {
                        migrationResult.setStatus(MigrationStatus.ERROR);
                    }
                    migrationResult.addTransformationResult(transformationResult);
                    LOGGER.info("Initializing Data: " + migrationResult.getCounter() + ": "
                            + transformationResult.toString());
                }
                migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
            }
            catch (Exception e) {
                LOGGER.error("doMigration() - Exception during initialization of data", e);
                migrationResult.setStatus(MigrationStatus.ERROR);
            }
        }
        // Ohne shutdown bleibt ein Thread hängen
        initExecutor.shutdown();
    }

    protected void executeMigrationThreadSafe(MigrationResult migrationResult) {
        if (!MigrationStatus.ERROR.equals(migrationResult.getStatus())) {
            ExecutorService migrationResultMergerExecutor = Executors.newSingleThreadExecutor();
            LinkedBlockingQueue<Future<MigrationResult>> taskQueue = new LinkedBlockingQueue<Future<MigrationResult>>(2 * numberOfThreadsInThreadPool);
            MigrationTaskResultConsumer migrationTaskResultConsumer = new MigrationTaskResultConsumer(migrationResult, taskQueue);
            Future<MigrationResult> finalResult = migrationResultMergerExecutor.submit(migrationTaskResultConsumer);
            migrationResultMergerExecutor.shutdown();
            try {
                boolean valid;
                do {
                    Iterator<T> block = migrationDao.getNextBlock();
                    valid = (block != null) && block.hasNext();
                    if (valid) {
                        try {
                            taskQueue.put(addTask(block));
                        }
                        catch (InterruptedException e) {
                            LOGGER.error("Interrupted while submitting migration tasks", e);
                            migrationResult.setStatus(MigrationStatus.ERROR);
                        }
                    }
                }
                while (valid);
            }
            catch (Exception e) {
                LOGGER.error("Error when reading blocks", e);
                migrationResult.setStatus(MigrationStatus.ERROR);
            }
            migrationTaskResultConsumer.isFinished();
            executorService.shutdown();
            try {
                finalResult.get();
            }
            catch (Exception e) {
                LOGGER.error("Exception while waiting for tasks to finish", e);
                migrationResult.setStatus(MigrationStatus.ERROR);
            }

        }
    }

    private Future<MigrationResult> addTask(Iterator<T> block) {
        MigrationTransformator<T> migrationTransformator = createMigrationTransformator();
        MigrationTask task = new MigrationTask(getMigrationName() + "-" + tasks++, block, migrationTransformator);
        return executorService.submit(task);
    }

    protected MigrationResult postMigrationHooks(MigrationResult migrationResult) {
        MigrationResult updatedResult = migrationResult;
        if ((migrationPostHooks != null) && (!migrationPostHooks.isEmpty())) {
            for (MigrationPostHook postHook : migrationPostHooks) {
                LOGGER.info("Starting Postmigration-Hook " + postHook.getClass().getSimpleName() +
                        " at " + (LocalDateTime.now()).toString());
                try {
                    updatedResult = postHook.call(migrationResult);
                }
                catch (Exception e) { //catch all Exception is ok
                    LOGGER.error(e.getLocalizedMessage(), e);
                    TransformationResult transformationResult =
                            new TransformationResult(TransformationStatus.ERROR, null, null,
                                    "Error in post hook: " + e.getMessage(), MigrationTransformator.CLASS_DEFAULT, "", e);
                    databaseLogger.log(transformationResult, updatedResult);
                    updatedResult.addTransformationResult(transformationResult);
                }
                LOGGER.info("Postmigration-Hook finished at " + (LocalDateTime.now()).toString());
            }
        }
        return updatedResult;
    }

    protected void afterMigration(MigrationResult migrationResult) {
        migrationResult.setEnd(LocalDateTime.now());
        if (MigrationStatus.RUNNING.equals(migrationResult.getStatus())) {
            migrationResult.setStatus(MigrationStatus.SUCCESS);
        }
        if (migrationResult.getCounter() != 0) {
            LOGGER.info("doMigration() - Migrated dataset #" + migrationResult.getCounter() + " - migration finished: " + migrationResult.toString());
        }
        else {
            LOGGER.warn("doMigration() - No source data found to migrate");
        }
        migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
    }

    private class MigrationTaskResultConsumer implements Callable<MigrationResult> {

        private final BlockingQueue<Future<MigrationResult>> taskQueue;
        private MigrationResult migrationResult;
        private volatile boolean finished = false;

        public MigrationTaskResultConsumer(MigrationResult migrationResult, BlockingQueue<Future<MigrationResult>> taskQueue) {
            this.migrationResult = migrationResult;
            this.taskQueue = taskQueue;
        }

        @Override
        public MigrationResult call() {
            while (!taskQueue.isEmpty() || !finished) {
                try {
                    Future<MigrationResult> futureResult = taskQueue.poll(30, TimeUnit.SECONDS);
                    if (futureResult != null) {
                        MigrationResult subMigrationResult = futureResult.get();
                        migrationResultDao.mergeMigrationResult(migrationResult, subMigrationResult);
                    }
                }
                catch (ExecutionException e) {
                    LOGGER.error("Exception while executing task", e);
                    migrationResult.setStatus(MigrationStatus.ERROR);
                }
                catch (InterruptedException e) {
                    LOGGER.error("Interrupted while polling for task or waiting for task result", e);
                    migrationResult.setStatus(MigrationStatus.ERROR);
                }
                migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
            }
            return migrationResult;
        }

        public void isFinished() {
            finished = true;
        }
    }

    class MigrationTask implements Callable<MigrationResult>, TransactionCallback<Pair<TransformationResult, Boolean>> {
        private MigrationTransformator<T> migrationTransformator;
        private MigrationResult migrationResult;
        private Iterator<T> block;

        public MigrationTask(String migrationName, Iterator<T> block, MigrationTransformator<T> migrationTransformator) {
            this.block = block;
            this.migrationTransformator = migrationTransformator;
            this.migrationResult = new MigrationResult(migrationName);
        }

        @Override
        public MigrationResult call() throws Exception {
            try {
                if ((block != null) && block.hasNext()) {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    beforeMigration(migrationResult);
                    migrationTransformator.setMigResultId(migrationResult.getId());
                    doTransformations(transactionTemplate);
                    if (migrationTransformator.shouldReadResultFromDatabase()) {
                        migrationResult = databaseLogger.readMigrationResultFromDatabase(migrationResult);
                    }
                    afterMigration(migrationResult);
                }
            }
            catch (Exception e) {
                LOGGER.error(e);
                migrationResult.reset();
                migrationResult.setStatus(MigrationStatus.ERROR);
            }
            return migrationResult;
        }

        /**
         * Calls {@link #doInTransaction(TransactionStatus)} with a surrounding transaction
         */
        protected void doTransformations(TransactionTemplate txTemplate) {
            while (block.hasNext()) {
                Pair<TransformationResult, Boolean> transactionResult = null;
                TransformationResult transformationResult = null;
                Boolean transactionCommitted = null;
                try {
                    // calls doInTransaction with a surrounding transaction
                    transactionResult = txTemplate.execute(this);
                    transformationResult = transactionResult.getFirst();
                    transactionCommitted = transactionResult.getSecond();
                }
                catch (Exception e) {
                    transformationResult = getResultFromMessageOrException(e);
                    transactionCommitted = Boolean.FALSE;
                    LOGGER.error("Error during batch processing", e);
                }
                notifyTransactionStatusAwareBeans(transactionCommitted);
                migrationResult.addTransformationResult(transformationResult);
                if (!databaseLogger.log(transformationResult, migrationResult)) {
                    migrationResult.setStatus(MigrationStatus.ERROR);
                }
                if (migrationResult.getCounter() % 10 == 0) {
                    migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
                    //super.clearHibernateSession();
                }

                // Log OK messages to level TRACE others (INFO, WARNING, BAD_DATA, ERROR, SKIPPED) to level DEBUG
                if (TransformationStatus.OK.equals(transformationResult.getTranformationStatus())) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Transformation of dataset #" + migrationResult.getCounter() + ": "
                                + transformationResult.toString());
                    }
                }
                else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Transformation of dataset #" + migrationResult.getCounter() + ": "
                                + transformationResult.toString());
                    }
                }
            }
        }


        private void notifyTransactionStatusAwareBeans(Boolean transactionCommitted) {
            if (transactionStatusAwareBeans != null) {
                if (Boolean.TRUE.equals(transactionCommitted)) {
                    for (MigrationControllerTransactionStatusAware bean : transactionStatusAwareBeans) {
                        bean.transactionIsCommit();
                    }
                }
                else {
                    for (MigrationControllerTransactionStatusAware bean : transactionStatusAwareBeans) {
                        bean.transactionIsRollback();
                    }
                }
            }
        }


        @Override
        public Pair<TransformationResult, Boolean> doInTransaction(TransactionStatus status) {
            TransformationResult transformationResult = null;
            try {
                transformationResult = migrationTransformator.transform(block.next());
            }
            catch (TransformationException e) {
                transformationResult = e.getResult();
            }
            catch (Exception e) {
                transformationResult = getResultFromMessageOrException(e);
            }

            if (!transformationResult.getTranformationStatus().commitAllowed()) {
                status.setRollbackOnly();
            }
            if (Boolean.TRUE.equals(simulate)) {
                status.setRollbackOnly();
            }
            return Pair.create(transformationResult, !status.isRollbackOnly());
        }


        private TransformationResult getResultFromMessageOrException(Exception e) {
            TransformationResult transformationResult;
            Messages messages = migrationTransformator.getMessagesIfDefined();
            if (messages != null) {
                messages.ERROR.add(e, e.getMessage());
                transformationResult = messages.evaluate(null, null);
            }
            else {
                transformationResult = new TransformationResult(TransformationStatus.ERROR, null, null,
                        "Error in transaction template: " + e.getMessage(), MigrationTransformator.CLASS_DEFAULT, "", e);
            }
            return transformationResult;
        }
    }

    @Override
    public String getMigrationName() {
        return migrationName;
    }


    /**
     * Implemented by Spring
     */
    protected abstract MigrationTransformator<T> createMigrationTransformator();

    /**
     * Injected by Spring
     */
    public void setNumberOfThreadsInThreadPool(int numberOfThreadsInThreadPool) {
        this.numberOfThreadsInThreadPool = numberOfThreadsInThreadPool;
    }

    /**
     * Injected by Spring
     */
    public void setRemoveIndices(boolean removeIndices) {
        this.removeIndices = removeIndices;
    }

    /**
     * Injected by Spring
     */
    public void setMigrationDao(MigrationDao<T> migrationDao) {
        this.migrationDao = migrationDao;
    }

    /**
     * Injected by Spring
     */
    public void setMigrationResultDao(MigrationResultDao migrationResultDao) {
        this.migrationResultDao = migrationResultDao;
    }

    /**
     * Injected by Spring
     */
    public void setMigrationName(String migrationName) {
        this.migrationName = migrationName;
    }

    /**
     * Injected by Spring
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Injected by Spring
     */
    public void setDatabaseLogger(DatabaseLogger databaseLogger) {
        this.databaseLogger = databaseLogger;
    }

    /**
     * Injected by Spring
     */
    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }
}
