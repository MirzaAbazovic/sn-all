/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH All rights reserved. -------------------------------------------------------
 * File created: 12.08.2009 11:47:00
 */
package de.augustakom.hurrican.service;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import de.augustakom.common.AbstractTransactionalServiceTest;
import de.augustakom.common.BaseTest;
import de.augustakom.common.TaifunDatabaseConnectionsCheck;
import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.exmodules.sap.ISAPService;
import de.augustakom.hurrican.service.internet.IInternetService;
import de.augustakom.hurrican.service.reporting.IReportService;

/**
 * Abstrakte Basisklasse fuer TestNG Service-Tests.
 */
public abstract class AbstractHurricanBaseServiceTest extends AbstractTransactionalServiceTest {

    private static final Logger LOGGER = Logger.getLogger(AbstractHurricanBaseServiceTest.class);
    private static final String TRANS_BEAN_POST_PROCESSOR = "de/augustakom/hurrican/service/cc/resources/TransactionBeanPostProcessor.xml";
    protected static final String[] CONFIGS = new String[] {
            "de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml",
            "de/augustakom/common/service/resources/HttpClient.xml",
            "de/augustakom/hurrican/service/reporting/resources/ReportServicesServer.xml",
            "de/augustakom/hurrican/service/exmodules/sap/resources/SAPServices.xml",
            "de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml",
            "de/augustakom/hurrican/service/billing/resources/taifun-datasource.xml",
            "de/augustakom/hurrican/service/cc/resources/CCTestService.xml",
            "de/augustakom/hurrican/service/cc/resources/CCAvailabilityService.xml",
            TRANS_BEAN_POST_PROCESSOR,
            "de/augustakom/hurrican/service/cc/resources/ESAATalServices.xml",
            "de/augustakom/hurrican/service/wholesale/WholesaleServices.xml",
            "de/augustakom/hurrican/service/wholesale/resources/wholesale-test-context.xml",
    };
    private static HurricanContextStarter hurricanContextStarter = null;
    private boolean isAutowired = false;

    protected HurricanContextStarter getHurricanContextStarter() {
        return hurricanContextStarter;
    }

    protected void setHurricanContextStarter(HurricanContextStarter hurricanContextStarter) {
        AbstractHurricanBaseServiceTest.hurricanContextStarter = hurricanContextStarter;
    }

    protected String[] getConfigs() {
        Test testAnnotation = getClass().getAnnotation(Test.class);
        if ((testAnnotation != null)
                && ArrayUtils.contains(testAnnotation.groups(), AbstractTransactionalServiceTest.NO_ROLLBACK_TEST)) {
            return (String[]) ArrayUtils.removeElement(CONFIGS, TRANS_BEAN_POST_PROCESSOR);
        }
        return CONFIGS;
    }

    /**
     * Liefert einen spezifischen Builder zurueck.
     *
     * @return Kann {@code null} zurueckgeben, wenn der Builder nicht gefunden wird.
     */
    public <T extends EntityBuilder<?, ?>> T getBuilder(Class<T> builderClass) {
        return getBean(builderClass);
        // return getCCService(builderClass);
    }

    /**
     * Initialisiert die benoetigten ServiceLocator und fuehrt den Login am Authentication-System durch. Wird von
     * setUpTransactions() aus {@link AbstractTransactionalServiceTest#setUpTransactions()} aufgerufen.
     */
    @Override
    @BeforeSuite
    protected final void setUp() throws Exception {
        LOGGER.info("Starting Contexts, started by: " + this.getClass());
        initHurricanContextStarter();
        LOGGER.info("Starting Contexts, started!");

        checkTaifunConnection();

        afterSetup();
    }

    private void checkTaifunConnection() {
        TaifunDatabaseConnectionsCheck check = getBean4Type(TaifunDatabaseConnectionsCheck.class);
        check.validateTaifunConnections();
    }

    /**
     * Callback called after setup is done. Subclasses can overwrite in order to add custom initializing steps.
     */
    protected void afterSetup() {
    }

    /**
     * Load property files to system before initializing the Spring application context. Subclasses can overwrite in
     * order to add custom
     */
    protected void loadProperties() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DC_DOUBLECHECK",
            justification = "Performanceoptimierung: 2. Check im synchronized block immer noetig")
    private synchronized void initHurricanContextStarter() throws Exception {
        if (getHurricanContextStarter() != null) {
            String msg = String.format(
                    "skip hurrican starter initialization in class <%s>, already initialized",
                    getClass().getName());
            LOGGER.info(msg);
            return;
        }

        LOGGER.info("Loading properties, started by: " + this.getClass());
        loadProperties();
        LOGGER.info("Loading properties, done!");

        LOGGER.info("set hurrican starter in: " + this.getClass());
        setHurricanContextStarter(new HurricanContextStarter(Arrays.asList(getConfigs())));
        LOGGER.info("start hurrican context in: " + this.getClass());
        getHurricanContextStarter().startContext();
        setApplicationContext(getHurricanContextStarter().getApplicationContext());
    }

    @BeforeMethod(groups = { BaseTest.SERVICE, BaseTest.SLOW })
    protected void autowireBean() {
        LOGGER.info("starting autowire bean for test: " + getClass());
        if (!isAutowired) {
            try {
                LOGGER.info("autowire beans for test " + getClass());
                initHurricanContextStarter();
                getHurricanContextStarter().autowireBean(this);
                isAutowired = true;
            }
            catch (Exception e) {
                LOGGER.error("Error initialising the spring test context in class: " + this.getClass(), e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Abmeldung vom Authentication-System und schliessen der Service-Locator.
     */
    @AfterSuite
    protected void tearDown() throws Exception {
        getHurricanContextStarter().tearDown();
    }

    /**
     * Gibt die Session-ID des Authentication-Systems zurueck.
     */
    protected Long getSessionId() {
        return getHurricanContextStarter().getSessionId();
    }

    protected <T> T getBean(Class<T> type) {
        return getApplicationContext().getBean(type.getName(), type);
    }

    protected <T> T getBean4Type(Class<T> type) {
        return getApplicationContext().getBean(type);
    }

    protected Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }

    /**
     * Sucht nach einem Billing-Service mit dem angegebenen Typ und gibt diesen zurueck.
     *
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected <T extends IBillingService> T getBillingService(Class<T> type) {
        try {
            return getBean(type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * Sucht nach einem CC-Service mit dem angegebenen Typs und gibt diesen zurueck.
     *
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected <T extends IServiceObject> T getCCService(Class<T> type) {
        try {
            return getBean(type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    protected Object getReportServerBean(String beanName) {
        try {
            return getBean(beanName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * Sucht nach einem Internet-Service des angegebenen Typs.
     *
     * @param type Typ des gesuchten Services - gleichzeitig der Name
     * @return
     */
    protected <T extends IInternetService> T getInternetService(Class<T> type) {
        try {
            return getBean(type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * Sucht nach einem SAP-Service des angegebenen Typs.
     *
     * @param type Typ des gesuchten Services - gleichzeitig der Name
     * @return
     */
    protected <T extends ISAPService> T getSAPService(Class<T> type) {
        try {
            return getBean(type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * Sucht nach einem ReportService des angegebenen Typs.
     *
     * @param type Typ des gesuchten Services - gleichzeitig der Name
     * @return
     */
    protected <T extends IReportService> T getReportService(Class<T> type) {
        try {
            return getBean(type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    protected TaifunDataFactory getTaifunDataFactory() {
        return getApplicationContext().getBean(TaifunDataFactory.class);
    }

}
