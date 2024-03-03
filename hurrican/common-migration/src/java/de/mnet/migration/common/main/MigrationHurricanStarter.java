/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2010
 */
package de.mnet.migration.common.main;

import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.gui.iface.AKCommonGUIConstants;
import de.augustakom.common.service.exceptions.InitializationException;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemInformation;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.HurricanSystemPropertyWriter;
import de.mnet.migration.common.util.CollectionUtil;

/**
 * Used by MigrationStarter classes.
 */
@SuppressWarnings("deprecation") // ServiceLocatorRegistry ist deprecated
public class MigrationHurricanStarter extends MigrationStarter {

    private static final Logger LOGGER = Logger.getLogger(MigrationHurricanStarter.class);

    public Long sessionId;

    @Override
    protected List<String> getBaseConfiguration() {
        return CollectionUtil.list(
                "de/mnet/migration/common/resources/base-migration.xml",
                "de/mnet/migration/common/resources/hurrican-migration.xml"
        );
    }

    protected List<String> getPropertyFiles() {
        return CollectionUtil.list("common", "hurrican-base", "migration");
    }

    protected String[] getApplicationContextLocations() {
        return new String[] {
                "de/mnet/migration/common/resources/hurrican-context.xml",
                "de/mnet/migration/common/resources/hurrican-context-wita.xml"
        };
    }


    public MigrationHurricanStarter() {
    }

    public MigrationHurricanStarter(String log4jConfiguration) {
        super(log4jConfiguration);
    }


    private void initializeProperties() {
        List<String> propertyFiles = getPropertyFiles();
        Properties defaultProps = PropertyUtil.loadPropertyHierarchy(propertyFiles, "properties", true);
        SystemPropertyTools.instance().setSystemProperties(defaultProps);
    }


    @Override
    protected ConfigurableApplicationContext getParentApplicationContext() throws InitializationException {
        try {
            initializeProperties();

            AuthenticationApplicationContextInitializer authInit = new AuthenticationApplicationContextInitializer();
            ConfigurableApplicationContext authContext = authInit.initializeApplicationContext(
                    System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE));

            AKLoginService loginService = authContext.getBean(AKAuthenticationServiceNames.LOGIN_SERVICE, AKLoginService.class);
            AKLoginContext login = new AKLoginContext();
            login.setUserName(System.getProperty("migration.user"));
            login.setPassword(System.getProperty("migration.password"));
            login.setApplicationName("Hurrican");
            login.setHostUser(SystemUtils.USER_NAME);
            login.setHostName(SystemInformation.getLocalHostName());
            login.setIpAddress(SystemInformation.getLocalHostAddress());
            login.setApplicationVersion("1.0");
            login.setNoDeprecation(true);  // UserSession immer gueltig!

            AKLoginContext loginContext = loginService.ldapLogin(System.getProperty("migration.user"), System.getProperty("migration.password"), "Hurrican", "1.0");
            login.setNoDeprecation(true);
            sessionId = loginContext.getUserSession().getSessionId();

            // Account-Daten auslesen, um weitere Services zu initialisieren
            List<AKAccount> accounts = loginContext.getAccounts();
            Map<Long, AKDb> databases = loginContext.getDatabases();
            HurricanSystemPropertyWriter.readDBInfosIntoSystem(accounts, databases);

            System.setProperty(AKCommonGUIConstants.ADMIN_APPLICATION_ID, loginContext.getApplication().getId().toString());
            System.setProperty("hurrican.cc.schema", System.getProperty("db.hurrican.migration.user.schema"));

            ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(getApplicationContextLocations(), true, authContext);
            ServiceLocatorRegistry.instance().setApplicationContext(context);
            return context;
        }
        catch (InitializationException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InitializationException(e.getMessage(), e);
        }
    }
}
