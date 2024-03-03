/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2011 13:27:38
 */
package de.augustakom.hurrican.service;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.HurricanSystemPropertyWriter;
import de.augustakom.hurrican.tools.session.UserSessionHolder;

/**
 * Hilfsklasse, um die Spring-Kontexte von Hurrican hochzufahren.
 */
public class HurricanContextStarter {
    private static final Logger LOGGER = Logger.getLogger(HurricanContextStarter.class);

    private ConfigurableApplicationContext applicationContext;
    public static final ImmutableList<String> DEFAULT_CONFIGS = ImmutableList.of(
            "de/augustakom/hurrican/service/reporting/resources/ReportServicesServer.xml",
            "de/augustakom/common/service/resources/HttpClient.xml",
            "de/augustakom/hurrican/service/exmodules/sap/resources/SAPServices.xml",
            "de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml",
            "de/augustakom/hurrican/service/cc/resources/CCTestService.xml",
            "de/augustakom/hurrican/service/cc/resources/TransactionBeanPostProcessor.xml",
            "de/augustakom/hurrican/service/cc/resources/ESAATalServices.xml",
            "de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml");

    public static final ImmutableList<String> DEFAULT_PROPERTY_HIERARCHY = ImmutableList.of("common",
            "authentication-test");

    private final ImmutableList<String> configs;
    private final ImmutableList<String> propertyHierarchy;
    protected AKLoginContext loginCtx;

    public HurricanContextStarter(List<String> configs) {
        this(configs, DEFAULT_PROPERTY_HIERARCHY);
    }

    public HurricanContextStarter(List<String> configs, List<String> propertyHierarchy) {
        this.configs = ImmutableList.copyOf(configs);
        this.propertyHierarchy = ImmutableList.copyOf(propertyHierarchy);
    }

    public HurricanContextStarter() {
        this(DEFAULT_CONFIGS, DEFAULT_PROPERTY_HIERARCHY);
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void autowireBean(Object bean) {
        LOGGER.debug("Autowiring bean " + bean.getClass() + " with beans from context " + applicationContext.toString());
        AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
        beanFactory.initializeBean(bean, getClass().getName());
    }

    public void startContext() throws Exception {
        try {
            LOGGER.info("Starting Contexts, started by: " + this.getClass());

            Properties loadedProps = PropertyUtil.loadPropertyHierarchy(propertyHierarchy, ".properties", true);
            SystemPropertyTools.instance().setSystemProperties(loadedProps);

            AuthenticationApplicationContextInitializer authInit = new AuthenticationApplicationContextInitializer();
            ConfigurableApplicationContext authContext = authInit.initializeApplicationContext(loadedProps
                    .getProperty(IServiceMode.SYSTEM_PROPERTY_MODE));

            LOGGER.info("Login");
            doLogin(authContext);
            readAccounts();

            LOGGER.info("Starting Application Context for Service");
            ConfigurableApplicationContext hurricanContext = new ClassPathXmlApplicationContext(
                    configs.toArray(new String[] { }), authContext);
            LOGGER.info("Setting Application Context for Service");
            setApplicationContext(hurricanContext);
        }
        catch (Exception e) {
            LOGGER.error("+++++++++++++++ ERROR initializing Spring Context  ++++++++++++++++");
            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
            throw e;
        }
    }

    /**
     * Login am Authentication-Service
     */
    protected void doLogin(ConfigurableApplicationContext authContext) throws ServiceNotFoundException,
            AKAuthenticationException, AKPasswordException {
        String user = System.getProperty("test.user");
        if (StringUtils.isBlank(user)) {
            user = "UnitTest";
        }
        LOGGER.debug("User used for testing: " + user);
        String password = System.getProperty("test.password");
        if (StringUtils.isBlank(password)) {
            password = "1#Unit@Test";
        }
        LOGGER.debug("Password for user '" + user + "': " + password);

        AKLoginService login = authContext.getBean(AKLoginService.class);

        String applicationName = System.getProperty("test.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "Hurrican";
        }

        this.loginCtx = login.ldapLogin(user, password, applicationName, null);
        UserSessionHolder.sessionId = (loginCtx.getUserSession() != null) ? loginCtx.getUserSession().getSessionId() : null;
    }

    /**
     * Liest die DB-Accounts aus und speichert sie in den System-Properties.
     */
    private void readAccounts() {
        List<AKAccount> accounts = loginCtx.getAccounts();
        Map<Long, AKDb> databases = loginCtx.getDatabases();

        String prefix = HurricanConstants.HURRICAN_NAME.toLowerCase() + ".";
        for (int i = 0; i < accounts.size(); i++) {
            AKAccount account = accounts.get(i);

            AKDb db = databases.get(account.getDbId());
            if (db == null) {
                fail("DB fuer Account-Name " + account.getName() + " nicht gefunden!");
            }

            HurricanSystemPropertyWriter.setSystemPropertiesForDb(account, db, prefix);
        }
    }

    /**
     * Logout vom Authentication-Service
     */
    private void doLogout() throws AKAuthenticationException, ServiceNotFoundException {
        AKLoginService loginService = applicationContext.getBean(AKLoginService.class);

        LOGGER.info("Logout from Authentication-System");
        loginService.logout(UserSessionHolder.sessionId);
    }

    public void tearDown() throws Exception {
        LOGGER.info("Logout");
        doLogout();
        LOGGER.info("Shutting down Application Context for Service");
        applicationContext.close();
    }

    /**
     * Gibt die Session-ID des Authentication-Systems zurueck.
     */
    public Long getSessionId() {
        return UserSessionHolder.sessionId;
    }

}
