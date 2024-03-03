/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 08:27:51
 */
package de.augustakom.hurrican.gui;

import java.util.*;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.HurricanConstants;


/**
 * Registry-Klasse fuer das GUI-System von Hurrican. <br> In dieser Klasse koennen Daten gespeichert werden, die von den
 * GUI-Klassen benoetigt werden - z.B. der aktive Benutzer oder eine Referenz auf das Main-Frame der Applikation.
 *
 *
 */
public final class HurricanSystemRegistry {

    /**
     * Schluessel fuer den Zugriff auf das Main-Frame der Applikation.
     */
    public static final String REGKEY_MAINFRAME = "mainframe";

    /**
     * Schluessel fuer den Zugriff auf den Login-Daten.
     */
    public static final String REGKEY_LOGIN_CONTEXT = "login.context";

    /**
     * Key fuer benutzerabhaengige Properties, die aus einem File geladen werden.
     */
    public static final String HURRICAN_USER_PROPERTIES = "hurrican.user.properties";

    /**
     * Key fuer den Zugriff auf das verwendete AuthenticationSystem-Objekt.
     */
    public static final String AUTHENTICATION_SYSTEM = "authentication.system";

    private static HurricanSystemRegistry instance = null;
    private Map<Object, Object> registry = null;
    private Long sessionId = null;
    private Properties userConfigs = null;

    /**
     * Konstruktor fuer die GUISystemRegistry.
     */
    private HurricanSystemRegistry() {
        init();
    }

    /**
     * Gibt eine Singleton-Instanz der Registry zurueck.
     *
     * @return
     */
    public synchronized static HurricanSystemRegistry instance() {
        if (instance == null) {
            instance = new HurricanSystemRegistry();
        }

        return instance;
    }

    /* Initialisiert die Registry. */
    private void init() {
        registry = new HashMap<Object, Object>();
    }

    /**
     * Speichert ein Objekt in der Registry unter dem angegebenen Schluessel.
     *
     * @param key   Eindeutiger Name, unter dem das Objekt gespeichert werden soll.
     * @param value Objekt, das gespeichert werden soll.
     */
    public void setValue(Object key, Object value) {
        registry.put(key, value);
    }

    /**
     * Gibt das Object zurueck, das mit dem angegebenen Schluessel hinterlegt wurde.
     *
     * @param key Eindeutiger Name fuer ein Objekt.
     * @return Objekt, das dem Schluessel zugeordnet ist.
     */
    public Object getValue(Object key) {
        return registry.get(key);
    }

    /**
     * Setzt die aktuelle Session-ID des angemeldeten Benutzers.
     *
     * @param sId Session-ID des Benutzers.
     */
    public void setSessionId(Long sId) {
        this.sessionId = sId;
    }

    /**
     * Gibt die aktuelle Session-ID des angemeldeten Benutzers zurueck.
     *
     * @return Session-ID des Benutzers.
     */
    public Long getSessionId() {
        return sessionId;
    }

    /**
     * Gibt das MainFrame der Applikation zurueck.
     *
     * @return
     */
    public AbstractMDIMainFrame getMainFrame() {
        return (AbstractMDIMainFrame) registry.get(REGKEY_MAINFRAME);
    }

    /**
     * Gibt den Namen des aktuell angemeldeten Benutzers zurueck.
     *
     * @return
     */
    public String getCurrentUserName() {
        Object ctx = getValue(REGKEY_LOGIN_CONTEXT);
        if (ctx instanceof AKLoginContext) {
            return ((AKLoginContext) ctx).getUserName();
        }
        return "";
    }

    public String getCurrentUserNameAndFirstName() {
        Object ctx = getValue(REGKEY_LOGIN_CONTEXT);
        if (ctx instanceof AKLoginContext) {
            return ((AKLoginContext) ctx).getUser().getNameAndFirstName();
        }
        return "";
    }

    /**
     * Gibt den Login-Namen des aktuell angemeldeten Benutzers zurueck.
     *
     * @return
     */
    public String getCurrentLoginName() {
        Object ctx = getValue(REGKEY_LOGIN_CONTEXT);
        if (ctx instanceof AKLoginContext) {
            return ((AKLoginContext) ctx).getUser().getLoginName();
        }
        return HurricanConstants.UNKNOWN;
    }

    /**
     * Gibt vom aktuell angemeldeten Benutzer das User-Objekt zurueck.
     *
     * @return Instanz von AKUser mit den Daten des angemeldeten Benutzers.
     *
     */
    public AKUser getCurrentUser() {
        AKLoginContext loginCtx = (AKLoginContext)
                HurricanSystemRegistry.instance().getValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
        return loginCtx.getUser();
    }

    /**
     * Ermittelt die benutzerabhaengige Einstellung <code>property</code>.
     *
     * @param property
     */
    public String getUserConfig(String property) {
        if (userConfigs == null) {
            userConfigs = SystemPropertyTools.instance().getPropertiesFromFile(HurricanGUIConstants.USER_CONFIG_FILE);
            if (userConfigs == null) { userConfigs = new Properties(); }
        }

        return userConfigs.getProperty(property);
    }

    /**
     * Ermittelt die benutzerabhaengige Einstellung fuer {@code property}.
     * Falls der Wert nicht in einen Long gewandelt werden kann, wird {@code null} zurueck gegeben.
     *
     * @see HurricanSystemRegistry#getUserConfig(String)
     * @param property
     * @return
     */
    public Long getUserConfigAsLong(String property) {
        String value = getUserConfig(property);
        try {
            return Long.valueOf(value);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Speichert eine benutzerabhaengige Einstellung.
     *
     * @param property
     * @param value
     */
    public void setUserConfig(String property, String value) {
        if (userConfigs == null) {
            userConfigs = new Properties();
        }

        userConfigs.put(property, value);
        SystemPropertyTools.instance().storeProperties(userConfigs, HurricanGUIConstants.USER_CONFIG_FILE);
    }

    /**
     * Speichert die benutzerabgaengige Einstellung.
     *
     * @see HurricanSystemRegistry#setUserConfig(String, String)
     * @param property
     * @param value
     */
    public void setUserConfig(String property, Long value) {
        String valueAsString = (value != null) ? String.format("%s", value) : "";
        setUserConfig(property, valueAsString);
    }

}
