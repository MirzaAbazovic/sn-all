/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.2004 11:54:53
 */
package de.augustakom.hurrican;

import de.augustakom.common.AKDefaultConstants;


/**
 * Interface mit Konstanten fuer das Hurrican-System.
 */
public interface HurricanConstants extends AKDefaultConstants {

    /**
     * String-Wert der verwendet werden kann, wenn etwas unbekannt ist.
     */
    public static final String UNKNOWN = "unknown";

    /**
     * 'Applikationsname' fuer das Basis-System von Hurrican. Dieser Name wird benoetigt, um die System-Properties, auf
     * die die Konfigurationsdateien angepasst sind, zu setzen.
     */
    public static final String HURRICAN_NAME = "hurrican";

    /**
     * Trennzeichen zwischen EMail-Adressen.
     */
    public static final String EMAIL_SEPARATOR = ";";

    /**
     * Property-Name, um den Schema-Namen des Billing-Systems zu setzen/abzufragen.
     */
    public static final String SYSTEM_PROPERTY_BILLING_SCHEMA = "hurrican.billing.schema";

    /**
     * Start-Parameter um eine automatisches Updatepruefung durchzufuehren.
     */
    public static final String AUTO_UPDATE = "auto.update";

    /**
     * Parameter, um den Testmodus mit WITA-Simulator für die Wita Schnittstelle zu aktivieren (auf "true" setzen)
     */
    public static final String WITA_SIMULATOR_TEST_MODE = "wita.simulator.test.mode";

    public static final String SYSTEM_USER = "SYSTEM_USER";

    /**
     * Key fuer ein ServletContext-Attribut, um die Hurrican Session-ID zu speichern / laden.
     */
    public static final String HURRICAN_SESSION_ID = "hurrican.session.id";
}
