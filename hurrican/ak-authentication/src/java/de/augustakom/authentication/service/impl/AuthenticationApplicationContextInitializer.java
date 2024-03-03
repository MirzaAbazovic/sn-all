/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 13:56:08
 */
package de.augustakom.authentication.service.impl;

import java.sql.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.AuthenticationSystemReader;
import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.common.service.exceptions.InitializationException;
import de.augustakom.common.service.iface.IServiceLocatorNames;

/**
 * Initializer, um den Authentication-Application-Context zu initialisieren.
 *
 *
 */
@AuthenticationTx
public class AuthenticationApplicationContextInitializer {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationApplicationContextInitializer.class);

    protected static final String AUTHENTICATION = "authentication";
    protected static final String AUTHENTICATION_JDBC_PREFIX = "authentication.jdbc.";

    private String serviceLocatorName = IServiceLocatorNames.AUTHENTICATION_SERVICE;
    private String xmlConfiguration = "de/augustakom/authentication/service/resources/AKAuthenticationService.xml";

    public ConfigurableApplicationContext initializeApplicationContext(String mode) {
        return initializeApplicationContext(AuthenticationSystemReader.instance().getAuthenticationSystem(mode));
    }

    public ConfigurableApplicationContext initializeApplicationContext(AuthenticationSystem authSystem)
            throws InitializationException {
        if (authSystem == null) {
            throw new InitializationException("Der zu verwendende Applikationsmodus konnte nicht ermittelt werden!");
        }
        LOGGER.info("****** Application Modus: " + authSystem.getBeanName() + "******");

        // Werte fuer DB-Connection in System-Properties schreiben
        authSystem.saveDriverAndUrl2SystemProperties(AUTHENTICATION_JDBC_PREFIX);
        authSystem.saveHibernateDialect2SystemProperties(AUTHENTICATION);
        authSystem.loadDefaultConnectionValues2SystemProperties(AUTHENTICATION_JDBC_PREFIX);
        if (isDefaultLogin()) {
            authSystem.saveUserAndPassword2SystemProperties(AUTHENTICATION_JDBC_PREFIX);
        }

        // Abfrage auf ApplicationContext absetzen, damit sich dieser komplett initialisiert.
        // Ausserdem wird hier geprueft, ob eine DB-Connection hergestellt wurde.
        try {
            ConfigurableApplicationContext authenticationApplicationContext =
                    new ClassPathXmlApplicationContext(getXmlConfiguration());

            LOGGER.info("URL for Authentication: " + System.getProperty("authentication.jdbc.url"));
            authenticationApplicationContext.getBean(AKLoginService.class);

            DataSource datasource = authenticationApplicationContext.getBean(AKAuthenticationServiceNames.DATA_SOURCE,
                    DataSource.class);
            if (datasource == null) {
                throw new InitializationException("DataSource not found. Name of DS: "
                        + AKAuthenticationServiceNames.DATA_SOURCE);
            }
            Connection conn = datasource.getConnection();
            conn.close();

            return authenticationApplicationContext;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InitializationException(e);
        }
    }

    /**
     * Gibt an, ob der Service ueber ein Standard-Login initialisiert werden soll.
     */
    protected boolean isDefaultLogin() {
        return true;
    }

    public String getServiceLocatorName() {
        return serviceLocatorName;
    }

    /**
     * Has to be set before {@link #initializeService()} is called
     */
    public void setServiceLocatorName(String serviceLocatorName) {
        this.serviceLocatorName = serviceLocatorName;
    }

    public String getXmlConfiguration() {
        return xmlConfiguration;
    }

    /**
     * Has to be set before {@link #initializeService()} is called
     */
    public void setXmlConfiguration(String xmlConfiguration) {
        this.xmlConfiguration = xmlConfiguration;
    }
}
