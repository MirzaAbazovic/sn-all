/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2015
 */
package de.mnet.wita.acceptance.authentication;

import java.util.*;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.tools.session.UserSessionHolder;

/**
 * Authentication context loader creates new Spring bean application context for authentication. Reads
 * service beans and exposes those beans to application context that loads this configuration class.
 *
 *
 */
public class AuthenticationContextBeanLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationContextBeanLoader.class);

    /**
     * Loads authentication application context and initializes beans.
     */
    public void initialize(ConfigurableApplicationContext context) {
        List<String> propertyHierarchy = ImmutableList.of("common", "authentication-test", "hurrican-wita-test");
        Properties loadedProps = PropertyUtil.loadPropertyHierarchy(propertyHierarchy, ".properties", true);
        SystemPropertyTools.instance().setSystemProperties(loadedProps);

        AuthenticationApplicationContextInitializer authInit = new AuthenticationApplicationContextInitializer();
        ConfigurableApplicationContext authContext = authInit.initializeApplicationContext(loadedProps
                .getProperty(IServiceMode.SYSTEM_PROPERTY_MODE));

        AKLoginService akLoginService = authContext.getBean(AKLoginService.class);
        AKUserService akUserService = authContext.getBean(AKUserService.class);

        authContext.getAutowireCapableBeanFactory().autowireBean(akLoginService);
        authContext.getAutowireCapableBeanFactory().autowireBean(akUserService);

        context.getBeanFactory().registerSingleton("akLoginService", akLoginService);
        context.getBeanFactory().registerSingleton("akUserService", akUserService);
        context.getBeanFactory().registerAlias("akUserService", "de.augustakom.authentication.service.AKUserService");

        try {
            AKLoginContext akLoginCtx = doLogin(akLoginService);
            context.getBeanFactory().registerSingleton("akLoginCtx", akLoginCtx);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to perform user login", e);
        }

    }

    /**
     * Login am Authentication-Service
     */
    protected AKLoginContext doLogin(AKLoginService loginService) throws ServiceNotFoundException, AKAuthenticationException, AKPasswordException {
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

        String applicationName = System.getProperty("test.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "Hurrican";
        }

        AKLoginContext loginCtx = loginService.ldapLogin(user, password, applicationName, null);
        UserSessionHolder.sessionId = (loginCtx.getUserSession() != null) ? loginCtx.getUserSession().getSessionId() : null;
        return loginCtx;
    }
}
