/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 12:39:43
 */
package de.augustakom.authentication.service.impl;


/**
 * Initializer fuer den AuthenticationService-Locator. <br> Im Gegensatz zu der Basisklasse ist dieser Initializer dazu
 * gedacht, dass fuer den Login der Benutzername und das Benutzerpasswort nicht ueber eine Konfigurationsdatei sondern
 * programmtechnisch gesetzt werden.
 *
 *
 */
public class AKAdminApplicationContextInitializer extends AuthenticationApplicationContextInitializer {

    @Override
    protected boolean isDefaultLogin() {
        return false;
    }

}
