/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 14:35:57
 */
package de.augustakom.hurrican.model.cc.command;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Abbildung eines ServiceCommands.
 *
 *
 */
public class ServiceCommand extends AbstractCCIDModel {

    /**
     * Wert fuer <code>type</code> kennzeichnet Commands, die Aktionen fuer die Auftrags-Physik durchfuehren.
     */
    public static final String COMMAND_TYPE_PHYSIK = "PHYSIK";

    /**
     * Wert fuer <code>type</code> kennzeichnet Commands, die Ueberpruefungen fuer die Verlaufserstellung durchfuehren.
     */
    public static final String COMMAND_TYPE_VERLAUF_CHECK = "VERLAUF_CHECK";

    /**
     * Wert fuer <code>type</code> kennzeichnet Commands, die pruefen, ob eine bestimmte Leistung auf einem Auftrag
     * realisiert werden kann.
     */
    public static final String COMMAND_TYPE_LS_CHECK_ZUGANG = "LS_CHECK_ZUGANG";

    /**
     * Wert fuer <code>type</code> kennzeichnet Commands, die Aktionen fuer einen Leistungs-Zugang durchfuehren.
     */
    public static final String COMMAND_TYPE_LS_ZUGANG = "LS_ZUGANG";

    /**
     * Wert fuer <code>type</code> kennzeichnet Commands, die pruefen, ob eine bestimmte Leistung von einem Auftrag
     * entfernt werden kann/darf.
     */
    public static final String COMMAND_TYPE_LS_CHECK_KUENDIGUNG = "LS_CHECK_KUEND";

    /**
     * Wert fuer <code>type</code> kennzeichnet Commands, die Aktionen fuer eine Leistungs-Kuendigung durchfuehren.
     */
    public static final String COMMAND_TYPE_LS_KUENDIGUNG = "LS_KUENDIGUNG";
    /**
     * /** Wert fuer <code>type</code> kennzeichnet Commands, die Daten für die Report-Erstellung sammeln
     */
    public static final String COMMAND_TYPE_REPORT = "REPORT";

    private String name = null;
    private String className = null;
    private String type = null;
    private String description = null;

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

}


