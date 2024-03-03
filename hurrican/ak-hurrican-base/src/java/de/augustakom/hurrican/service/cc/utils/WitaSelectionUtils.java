/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2011 11:23:15
 */
package de.augustakom.hurrican.service.cc.utils;

import static de.augustakom.hurrican.HurricanConstants.*;

import org.apache.commons.lang.StringUtils;

/**
 * Wrapper fuer Systemeinstellungen bezueglich WITA
 */
public class WitaSelectionUtils {

    public static boolean isWitaSimulatorTestMode() {
        return StringUtils.equalsIgnoreCase(System.getProperty(WITA_SIMULATOR_TEST_MODE), "true");
    }

}
