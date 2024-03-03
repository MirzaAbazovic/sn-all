/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH All rights reserved. -------------------------------------------------------
 * File created: 13.08.2009 15:01:27
 */
package de.augustakom.common;

import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;

import de.augustakom.common.tools.lang.Pair;

/**
 * Stellt Methoden zur Verfuegung, um TransactionManager aus den ApplicationContexten zu holen und vor jedem Test eine
 * Transaktion aufzumachen, die nach dem Test zurueckgerollt wird.
 */
public abstract class AbstractTransactionalServiceTest extends BaseTest {

    /**
     * Kennzeichen für Test Groups die mit ECHTEN Transaktionen ablaufen sollen, also ohne Rollback!!!
     */
    public static final String NO_ROLLBACK_TEST = "no_rollback_test";

    private static final Logger LOGGER = Logger.getLogger(AbstractTransactionalServiceTest.class);

    protected static Map<PlatformTransactionManager, String> txManagers;
    protected static List<Pair<PlatformTransactionManager, TransactionStatus>> transactions;
    private static TransactionDefinition txDefinition; // make changeable by children if custom behaviour is needed

    private static ConfigurableApplicationContext applicationContext;

    protected static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        LOGGER.info("Application Context on AbstractTransactionalBaseTest set");
        AbstractTransactionalServiceTest.applicationContext = applicationContext;
    }

    /**
     * Ableitende Klassen sollten eine setUp-Methode definieren, die die Services initialisiert.
     */
    protected abstract void setUp() throws Exception;

    /**
     * Laeuft nach der setUp-Methode, benoetigt Services
     */
    @BeforeGroups(groups = { BaseTest.SERVICE, BaseTest.SLOW })
    protected void setUpTransactions() throws Exception {
        LOGGER.info("Calling setup for Transactions on class " + this.getClass());
        setUp();

        LOGGER.info("Setting up Transactions from class " + this.getClass() + " for applicationContext "
                + getApplicationContext().toString());
        transactions = new ArrayList<>();
        txManagers = new HashMap<>();

        if (getApplicationContext() == null) {
            Assert.fail("No application context found. Configuration problem?");
        }
        else {
            Map<String, PlatformTransactionManager> txManagerMap = getApplicationContext().getBeansOfType(
                    PlatformTransactionManager.class);

            if (txManagerMap != null) {
                LOGGER.debug("txManagerMap.size() = " + txManagerMap.size());
            }
            else {
                LOGGER.warn("txManagerMap is null");
            }

            if (txManagerMap != null) {
                for (Map.Entry<String, PlatformTransactionManager> entry : txManagerMap.entrySet()) {
                    PlatformTransactionManager txManager = entry.getValue();
                    LOGGER.debug("Hibernate Transaction Manager: " + entry.getKey() + " (" + txManager + ")");
                    if (txManagers.containsKey(txManager)) {
                        LOGGER.error("Transaction Manager Map already contains the given PlatformTransactionManager: "
                                + entry.getValue());
                    }
                    else {
                        txManagers.put(txManager, entry.getKey());
                    }
                }
            }
            txDefinition = new DefaultTransactionDefinition();
        }
    }

    /**
     * Starten einer Transaktion fuer jeden TransactionManager
     */
    @BeforeMethod(groups = { BaseTest.SERVICE, BaseTest.SLOW })
    protected void beginTransactions() {
        if (txManagers != null) {
            LOGGER.debug("txManagers size = " + txManagers.size());
            for (Map.Entry<PlatformTransactionManager, String> txManager : txManagers.entrySet()) {
                LOGGER.debug("Opening a new transaction with transaction manager " + txManager.getValue());
                try {
                    PlatformTransactionManager platformTxMgr = txManager.getKey();
                    TransactionStatus newStatus = platformTxMgr.getTransaction(txDefinition);
                    transactions.add(Pair.create(txManager.getKey(), newStatus));
                }
                catch (NullPointerException e) {
                    String message = String.format("transaction not opened for TX-Manager %s - maybe it is mocked.",
                            txManager.getValue());
                    LOGGER.warn(message);
                }
                catch (Exception ex) {
                    String message = String.format("transaction not opened for TX-Manager %s - error '%s'.",
                            txManager.getValue(), ex.getMessage());
                    LOGGER.error(message,ex);
                    throw ex;
                }
            }
        }
        else {
            LOGGER.warn("txManagers is null");
        }

    }

    /**
     * Rollback der Transaktionen
     */
    @AfterMethod(groups = { BaseTest.SERVICE, BaseTest.SLOW }, alwaysRun = true)
    protected void rollbackTransactions() {
        // Transaktionen muessen in umgekehrter Reihenfolge geschlossen werden, damit
        // der SynchronizationManager nicht durcheinander kommt
        if (txManagers != null) {
            for (int i = transactions.size() - 1; i >= 0; --i) {
                PlatformTransactionManager txManager = transactions.get(i).getFirst();
                TransactionStatus status = transactions.get(i).getSecond();
                if ((status != null) && !status.isCompleted()) {
                    tryRollback(txManager, status);
                }
            }
        }
    }

    /**
     * Versuche eine Transaktion zurueckzurollen, fange und logge Exceptions
     */
    protected void tryRollback(PlatformTransactionManager txManager, TransactionStatus status) {
        try {
            txManager.rollback(status);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Transaction rolled back for transaction manager " + txManagers.get(txManager));
            }
        }
        catch (Exception e) {
            LOGGER.warn(
                    "Exception trying to rollback transaction for transaction manager " + txManagers.get(txManager), e);
        }
    }

    protected void flushAndClear() {
        if (txManagers != null) {
            for (int i = transactions.size() - 1; i >= 0; --i) {
                PlatformTransactionManager txManager = transactions.get(i).getFirst();
                if (txManager instanceof ResourceTransactionManager) {
                    Object resourceFactory = ((ResourceTransactionManager) txManager).getResourceFactory();
                    if (resourceFactory instanceof SessionFactory) {
                        Session session = ((SessionFactory) resourceFactory).getCurrentSession();
                        if (session != null) {
                            try {
                                session.flush();
                            }
                            catch (HibernateException e) {
                                throw SessionFactoryUtils.convertHibernateAccessException(e);
                            }
                            session.clear();
                        }
                    }
                }
            }
        }
    }
}
