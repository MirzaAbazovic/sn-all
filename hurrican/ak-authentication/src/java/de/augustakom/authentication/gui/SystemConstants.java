/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.05.2004 09:16:20
 */
package de.augustakom.authentication.gui;

import de.augustakom.common.AKDefaultConstants;


/**
 * Interface definiert Konstanten, die systemweite Gueltigkeit besitzen.
 *
 *
 */
public interface SystemConstants extends AKDefaultConstants {

    /**
     * Logischer Name fuer die Datenbank.
     */
    public static final String DB_NAME = "authentication";

    /**
     * Ist der Wert dieses Property-Elements auf <code>true</code> gesetzt, wird ein automatischer Login durchgefuehrt.
     * <br> Als Benutzername und Password werden die Werte der System-Properties <code>SYSTEM_PROPERTY_AUTOLOGIN_USER</code>
     * und <code>SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD</code> verwendet.
     */
    public static final String SYSTEM_PROPERTY_AUTOLOGIN = "autologin";

    /**
     * Der Wert von diesem System-Property wird als Benutzername fuer den Login verwendet.
     */
    public static final String SYSTEM_PROPERTY_AUTOLOGIN_USER = "autologin.user";

    /**
     * Der Wert von diesem System-Property wird als Passwort fuer den Login verwendet.
     */
    public static final String SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD = "autologin.password";

    /**
     * Definition fuer die Action-Eigenschaft 'tree.node'. <br> Unter diesem Key kann in einer Action ein TreeNode
     * gespeichert werden.
     */
    public static final String ACTION_PROPERTY_TREENODE = "tree.node";

    /**
     * Definition fuer die Action-Eigenschaft 'user.object'. <br> Unter diesem Key kann in einer Action ein UserObject
     * gespeichert werden.
     */
    public static final String ACTION_PROPERTY_USEROBJECT = "user.object";
}
