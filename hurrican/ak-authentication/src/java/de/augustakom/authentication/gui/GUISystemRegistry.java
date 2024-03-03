/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 10:04:37
 */
package de.augustakom.authentication.gui;

import java.util.*;

import de.augustakom.authentication.gui.system.MDIMainFrame;


/**
 * Registry-Klasse fuer das GUI-System. <br> In dieser Klasse koennen Daten gespeichert werden, die von den GUI-Klassen
 * benoetigt werden - z.B. der aktive Benutzer oder eine Referenz auf das Main-Frame der Applikation.
 *
 *
 */
public final class GUISystemRegistry {

    /**
     * Schluessel fuer den Zugriff auf das Main-Frame der Applikation.
     */
    public static final String REGKEY_MAINFRAME = "mainframe";

    /**
     * Schluessel fuer den Zugriff auf das Filter-Objekt fuer den Tree.
     */
    public static final String REGKEY_TREE_FILTER = "tree.filter";

    /**
     * Schluessel fuer den Zugriff auf das TreePanel.
     */
    public static final String REGKEY_TREE_PANEL = "tree.panel";

    /**
     * Schluessel fuer den Zugriff auf den Tree.
     */
    public static final String REGKEY_TREE = "tree";

    /**
     * Schluessel fuer den Zugriff auf das TreeModel.
     */
    public static final String REGKEY_TREE_MODEL = "tree.model";

    /**
     * Key fuer den Zugriff auf ein Objekt des Typs <code>AuthenticationSystem</code>.
     */
    public static final String AUTHENTICATION_SYSTEM = "authentication.system";

    private static GUISystemRegistry instance = null;
    private final Map<String, Object> registry;

    /**
     * Konstruktor fuer die GUISystemRegistry.
     */
    private GUISystemRegistry() {
        registry = new HashMap<String, Object>();
    }

    /**
     * Gibt eine Singleton-Instanz der Registry zurueck.
     *
     * @return
     */
    public static GUISystemRegistry instance() {
        if (instance == null) {
            instance = new GUISystemRegistry();
        }

        return instance;
    }

    /**
     * Speichert ein Objekt in der Registry unter dem angegebenen Schluessel.
     *
     * @param key   Eindeutiger Name, unter dem das Objekt gespeichert werden soll.
     * @param value Objekt, das gespeichert werden soll.
     */
    public void setValue(String key, Object value) {
        registry.put(key, value);
    }

    /**
     * Gibt das Object zurueck, das mit dem angegebenen Schluessel hinterlegt wurde.
     *
     * @param key Eindeutiger Name fuer ein Objekt.
     * @return Objekt, das dem Schluessel zugeordnet ist.
     */
    public Object getValue(String key) {
        return registry.get(key);
    }

    /**
     * Gibt das MainFrame der Applikation zurueck.
     */
    public MDIMainFrame getMainFrame() {
        return (MDIMainFrame) registry.get(REGKEY_MAINFRAME);
    }
}
