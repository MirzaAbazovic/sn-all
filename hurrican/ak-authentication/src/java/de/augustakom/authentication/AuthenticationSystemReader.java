/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2005 11:14:44
 */
package de.augustakom.authentication;

import java.util.*;
import java.util.stream.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.model.AuthenticationSystem;


/**
 * Hilfsklasse, um die moeglichen Authentication-Systeme zu erhalten.
 */
public final class AuthenticationSystemReader {

    private static final String SYSTEM_CONFIG = "de/augustakom/authentication/service/resources/AuthenticationDatabases.xml";

    private static AuthenticationSystemReader sysReader;
    private Map<String, AuthenticationSystem> authSystems;

    /**
     * Instantiierung nicht erlaubt (Singleton)
     */
    private AuthenticationSystemReader() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     */
    public static AuthenticationSystemReader instance() {
        if (sysReader == null) {
            sysReader = new AuthenticationSystemReader();
        }
        return sysReader;
    }

    /**
     * Gibt eine sortierte Liste mit allen konfigurierten Authentication-Systemen zurueck.
     */
    public final List<AuthenticationSystem> getAuthenticationSystems() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(SYSTEM_CONFIG);
        authSystems = appContext.getBeansOfType(AuthenticationSystem.class);
        if (authSystems != null) {
            return authSystems.values().stream()
                    .sorted((as1, as2) -> as1.getDisplayName().compareTo(as2.getDisplayName()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Gibt ein Objekt vom Typ <code>AuthenticationSystem</code> zurueck, das dem Bean-Namen <code>beanName</code>
     * entspricht.
     */
    public AuthenticationSystem getAuthenticationSystem(String beanName) {
        if (authSystems == null) {
            getAuthenticationSystems();
        }
        return authSystems.get(beanName);
    }
}


