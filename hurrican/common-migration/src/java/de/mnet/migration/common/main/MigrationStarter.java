/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2009 10:20:21
 */

package de.mnet.migration.common.main;

import java.util.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.mnet.migration.common.MigrationController;
import de.mnet.migration.common.MigrationResultDao;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.MigrationStatus;
import de.mnet.migration.common.util.CollectionUtil;
import de.mnet.migration.common.util.InitializeLog4J;

/**
 * Used by MigrationStarter classes.
 */
public class MigrationStarter {
    private static final Logger LOGGER = Logger.getLogger(MigrationStarter.class);

    private String migrationSuite;
    private String migrationName;
    private MigrationController migrationController;

    protected List<String> getBaseConfiguration() {
        return CollectionUtil.list(
                "de/mnet/migration/common/resources/base-migration.xml"
        );
    }

    /**
     * Override to configure
     */
    protected ConfigurableApplicationContext getParentApplicationContext() throws Exception {
        return null;
    }


    public MigrationStarter() {
        this("log4j-migration");
    }

    public MigrationStarter(String log4jConfiguration) {
        InitializeLog4J.initializeLog4J(log4jConfiguration);
    }


    public Exit startMigration(String[] args, String... migrationConfigLocation) {
        return startMigration(args, Arrays.asList(migrationConfigLocation), true);
    }

    /**
     * @param skipIfAlreayRun true (default) bedeutet, dass die Migration geskippt wird, falls sie bereits erfolgreich
     *                        durchgefuehrt wurde. false bedeutet, dass die Migration auch mehrfach ausgefuehrt wird.
     */
    public Exit startMigration(String[] args, String migrationConfigLocation, boolean skipIfAlreayRun) {
        return startMigration(args, CollectionUtil.list(migrationConfigLocation), skipIfAlreayRun);
    }

    public Exit startMigration(String[] args, List<String> migrationConfigLocations, boolean skipIfAlreadyRun) {
        LOGGER.info("startMigration() - Starting migration for contexts: " + migrationConfigLocations.toString());

        ConfigurableApplicationContext migrationAppplicationContext = initializeApplicationContext(migrationConfigLocations);
        this.migrationSuite = fetchMigrationSuiteFromArgs(args);

        MigrationResultDao migrationResultDao = (MigrationResultDao) migrationAppplicationContext.getBean("migrationResultDao");

        this.migrationController = (MigrationController) migrationAppplicationContext.getBean("migrationController");
        this.migrationName = migrationController.getMigrationName();
        Exit exitCode = null;

        if (skipIfAlreadyRun && migrationResultDao.wasMigrationAlreadySuccesful(migrationName, migrationSuite)) {
            LOGGER.info("startMigration() - " + migrationName + " in migrationSuite: " + migrationSuite +
                    " has been already successful run - skipping migration.");
            exitCode = new Exit(Exit.Code.ALREADY_EXECUTED);
        }
        else {
            MigrationResult migrationResult = migrateAndGetResult();
            migrationResultDao.saveMigrationResult(migrationResult, migrationSuite);
            exitCode = getExitCode(migrationResult);
        }

        migrationAppplicationContext.close();
        return exitCode;
    }


    public ConfigurableApplicationContext initializeApplicationContext(List<String> migrationConfigLocations) {
        // To make BeanUtils copy nulls as nulls!
        // Do not remove (except if you have a better idea where to put it)!
        BeanUtilsBean.getInstance().getConvertUtils().register(false, true, 0);

        List<String> springConfigFiles = getBaseConfiguration();
        springConfigFiles.addAll(migrationConfigLocations);
        ConfigurableApplicationContext migrationAppplicationContext;
        try {
            migrationAppplicationContext = new ClassPathXmlApplicationContext(
                    springConfigFiles.toArray(new String[] { }), true, getParentApplicationContext());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Error during initialization of Application Context: " + e.getMessage(), e);
        }
        return migrationAppplicationContext;
    }


    private String fetchMigrationSuiteFromArgs(String[] args) {
        String migrationSuite = "<keine Suite angegeben>";
        if (args.length > 0) {
            migrationSuite = args[0];
        }
        return migrationSuite;
    }

    private MigrationResult migrateAndGetResult() {
        MigrationResult migrationResult = new MigrationResult(migrationName);
        try {
            migrationResult = migrationController.doMigration(migrationSuite);
        }
        catch (Throwable e) {  // NOSONAR squid:S1181 ; all errors should result in an error state for the migration
            LOGGER.error("Migration Failed", e);
            migrationResult.setStatus(MigrationStatus.ERROR);
        }
        return migrationResult;
    }

    private Exit getExitCode(MigrationResult migrationResult) {
        Exit exitCode = null;
        String message = "startMigration() - " + migrationName + " in migrationSuite: " + migrationSuite;
        if (migrationResult.getStatus() == MigrationStatus.SUCCESS) {
            LOGGER.info(message + " was successful");
            exitCode = new Exit(Exit.Code.SUCCESS);
        }
        else {
            LOGGER.info(message + " was not successful - returning 1");
            exitCode = new Exit(Exit.Code.FAILURE);
        }
        return exitCode;
    }


    /**
     * In case of failure, program must return non-zero return code
     */
    public static class Exit {
        public enum Code {SUCCESS, FAILURE, ALREADY_EXECUTED}

        private final Code code;

        Exit(Code code) {
            this.code = code;
        }

        @edu.umd.cs.findbugs.annotations.SuppressWarnings(justification = "MigrationStarter is controlling lifecycle of application", value = "DM_EXIT")
        public void finish() {
            switch (code) {
                case FAILURE:
                    System.exit(1); //$FALL-THROUGH$
                    break;
                default: // NOSONAR squid:S128 ; Falls kein Failure soll das System normal herunterfahren
            }
        }

        public Code getCode() {
            return code;
        }
    }
}
