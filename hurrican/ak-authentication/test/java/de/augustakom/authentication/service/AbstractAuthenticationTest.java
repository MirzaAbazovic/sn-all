/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2009 15:11:55
 */
package de.augustakom.authentication.service;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterGroups;

import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.AbstractTransactionalServiceTest;
import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;


/**
 * Basisklasse fuer alle TestCases des AuthenticationServices.
 *
 *
 */
public abstract class AbstractAuthenticationTest extends AbstractTransactionalServiceTest {
    private static final Logger LOGGER = Logger.getLogger(AbstractAuthenticationTest.class);

    private static final String TEST_AUTHENTICATION_APPLICATION_XML_CONFIG =
            "de/augustakom/authentication/service/resources/AKAuthenticationTestService.xml";

    /**
     * Initialisiert den ServiceLocator (und dadurch die Services). Wird von setUpTransactions() aus {@link
     * AbstractTransactionalTest#setUpTransactions()} aufgerufen.
     */
    @Override
    protected void setUp() throws Exception {
        Properties loadedProps = PropertyUtil.loadPropertyHierarchy(Arrays.asList("common", "authentication-test"), ".properties", true);
        SystemPropertyTools.instance().setSystemProperties(loadedProps);

        LOGGER.debug("Starting Application Context for Authentication");
        AuthenticationApplicationContextInitializer authInit = new AuthenticationApplicationContextInitializer();
        authInit.setXmlConfiguration(TEST_AUTHENTICATION_APPLICATION_XML_CONFIG);
        LOGGER.debug("Setting Application Context for Authentication");
        setApplicationContext(authInit.initializeApplicationContext(
                loadedProps.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE)));
    }

    /**
     * @see TestCase#tearDown()
     */
    @AfterGroups(groups = { "service" })
    protected void tearDown() throws Exception {
        LOGGER.info("Shutting down Application Context");
        getApplicationContext().close();
    }


    /**
     * Liefert einen spezifischen Builder zurueck.
     *
     * @return Kann {@code null} zurueckgeben, wenn der Builder nicht gefunden wird.
     */
    protected <T extends EntityBuilder<?, ?>> T getBuilder(Class<T> builderClass) {
        return getApplicationContext().getBean(builderClass.getName(), builderClass);
    }

    /**
     * Gibt eine Instanz des AKLoginService zurueck.
     */
    protected AKLoginService getLoginService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.LOGIN_SERVICE, AKLoginService.class);
    }

    /**
     * Gibt eine Instanz des AKDepartmentService zurueck.
     */
    protected AKDepartmentService getDepartmentService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.DEPARTMENT_SERVICE, AKDepartmentService.class);
    }

    /**
     * Gibt eine Instanz des UserServices zurueck.
     */
    protected AKUserService getUserService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
    }

    /**
     * Gibt eine Instanz des AKRoleServices zurueck.
     */
    protected AKRoleService getRoleService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.ROLE_SERVICE, AKRoleService.class);
    }

    /**
     * Gibt eine Instanz des AKTeamServices zurueck.
     */
    protected AKTeamService getTeamService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.TEAM_SERVICE, AKTeamService.class);
    }

    /**
     * Gibt eine Instanz des AKApplicationService zurueck.
     */
    protected AKApplicationService getApplicationService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.APPLICATION_SERVICE, AKApplicationService.class);
    }

    /**
     * Gibt eine Instanz des AKAccountService zurueck.
     */
    protected AKAccountService getAccountService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.ACCOUNT_SERVICE, AKAccountService.class);
    }

    protected AKDbService getDbService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.DB_SERVICE, AKDbService.class);
    }

    /**
     * Gibt eine Instanz von AKGUIService zurueck.
     */
    protected AKGUIService getGUIService() {
        return getApplicationContext().getBean(
                AKAuthenticationServiceNames.GUI_SERVICE, AKGUIService.class);
    }
}
