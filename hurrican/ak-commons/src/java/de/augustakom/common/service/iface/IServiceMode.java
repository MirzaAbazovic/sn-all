/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 15:00:29
 */
package de.augustakom.common.service.iface;


/**
 * Interface zur Definition von Konstanten, die den Modus eines Services betreffen, z.B. Test-Modus oder
 * Produktiv-Modus.
 *
 *
 */
public interface IServiceMode {

    /**
     * Key fuer den Zugriff auf das SystemProperty, das den Modus bestimmt, in dem die Applikation laeuft.
     */
    static final String SYSTEM_PROPERTY_MODE = "application.modus";

    /**
     * Key für System Property, die einen Application Mode fest definiert
     */
    static final String SYSTEM_PROPERTY_DEFAULT_MODE = "authenticationsystem.default";

    /**
     * Ist der Wert dieses Property-Elements auf <code>true</code> gesetzt, wird ein automatischer Login durchgefuehrt.
     * <br> Als Benutzername und Password werden die Werte der System-Properties <code>SYSTEM_PROPERTY_AUTOLOGIN_USER</code>
     * und <code>SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD</code> verwendet.
     */
    static final String SYSTEM_PROPERTY_AUTOLOGIN = "autologin";

    /**
     * Der Wert von diesem System-Property wird als Benutzername fuer den Login verwendet.
     */
    static final String SYSTEM_PROPERTY_AUTOLOGIN_USER = "autologin.user";

    /**
     * Der Wert von diesem System-Property wird als Passwort fuer den Login verwendet.
     */
    static final String SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD = "autologin.password";

}
