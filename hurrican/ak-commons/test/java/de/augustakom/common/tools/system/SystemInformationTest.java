/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.tools.system;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class SystemInformationTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(SystemInformationTest.class);

    /**
     * Test fuer die Methode SystemInformation.getLocalHostName <br> <b>Hinweis:</b> Dieser Test kann nicht automatisch
     * ueberprueft werden, da die Host-Adresse nicht immer bekannt ist. <br> Sollte das Ergebnis jedoch '<unknown>'
     * sein, dann wird ein Fehler generiert.
     */

    public void testGetLocalHostName() {
        String hostName = SystemInformation.getLocalHostName();
        LOGGER.debug("Host name: " + hostName);
        assertFalse(StringUtils.equals(SystemInformation.SYSTEM_INFORMATION_UNKNOWN, hostName),
                "Could not get the local host name!");
    }

    /**
     * Test fuer die Metode SystemInformation.getLocalHostAddress <br> <b>Hinweis:</b> Dieser Test kann nicht
     * automatisch ueberprueft werden, da die Host-Adresse nicht immer bekannt ist. <br> Sollte das Ergebnis jedoch
     * '<unknown>' sein, dann wird ein Fehler generiert.
     */
    public void testGetLocalHostAddress() {
        String address = SystemInformation.getLocalHostAddress();
        LOGGER.debug("IP-Address: " + address);
        assertFalse(StringUtils.equals(SystemInformation.SYSTEM_INFORMATION_UNKNOWN, address),
                "Could not get the local host address!");
    }
}
