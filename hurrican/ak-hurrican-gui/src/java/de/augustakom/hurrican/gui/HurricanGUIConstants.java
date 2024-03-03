/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.05.2004 09:16:20
 */
package de.augustakom.hurrican.gui;

import org.apache.commons.lang.SystemUtils;

import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Interface definiert Konstanten, die systemweite Gueltigkeit besitzen.
 *
 *
 */
public interface HurricanGUIConstants {

    /**
     * Pfad der Datei fuer die User-Konfigurationen.
     */
    public static final String USER_CONFIG_FILE =
            SystemUtils.USER_HOME + SystemUtils.FILE_SEPARATOR + "ak-hurrican.properties";

    /**
     * Name der Applikation (Hurrican).
     */
    public static final String APPLICATION_NAME = "Hurrican";

    /**
     * Zugriff auf das Property der benutzerabhaengigen Konfiguration fuer den Applikationsmodus.
     */
    public static final String USER_CONFIG_APP_MODUS = "hurrican.modus";

    /**
     * Ueber das Property ist die ID der {@link Abteilung} hinterlegt, die in der abteilungs-uebergreifenden
     * Bauauftrags-Maske automatisch vorbelegt sein soll.
     */
    public static final String ABTEILUNG_4_BAUAUFTRAG_GUI = "abteilung.id.bauauftrag";

    /**
     * Ueber das Property ist die ID der {@link Abteilung} hinterlegt, die in der abteilungs-uebergreifenden
     * Projektierungs-Maske automatisch vorbelegt sein soll.
     */
    public static final String ABTEILUNG_4_PROJEKTIERUNG_GUI = "abteilung.id.projektierung";

}
