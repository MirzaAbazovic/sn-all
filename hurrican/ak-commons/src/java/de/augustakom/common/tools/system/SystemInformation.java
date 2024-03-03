/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2004
 */
package de.augustakom.common.tools.system;

import java.net.*;
import org.apache.log4j.Logger;

/**
 * Hilfsklasse, um div. System-Informationen zu erhalten.
 */
public class SystemInformation {
    private static final Logger LOGGER = Logger.getLogger(SystemInformation.class);

    /**
     * Rueckgabewert, falls die gewuenschte Information nicht ermittelt werden konnte.
     */
    public static final String SYSTEM_INFORMATION_UNKNOWN = "<unknown>";

    /**
     * Gibt den Namen des lokalen Hosts zurueck.
     *
     * @return Name des lokalen Hosts oder Wert von <code>SYSTEM_INFORMATION_UNKNOWN</code>, falls der Name nicht
     * ermittelt werden konnte.
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            LOGGER.warn("getLocalHostName() - local hostname is unknown", e);
        }
        return SYSTEM_INFORMATION_UNKNOWN;
    }

    /**
     * Gibt die IP-Adresse des lokalen Hosts zurueck. <br>
     *
     * @return IP-Adresse des lokalen Hosts oder Wert von <code>SYSTEM_INFORMATION_UNKNOWN</code>, falls die Adresse
     * nicht ermittelt werden konnte.
     */
    public static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            LOGGER.warn("getLocalHostAddress() - local host is unknown", e);
        }

        return SYSTEM_INFORMATION_UNKNOWN;
    }
}
