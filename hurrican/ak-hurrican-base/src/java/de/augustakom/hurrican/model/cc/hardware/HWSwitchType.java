/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 14:45:20
 */
package de.augustakom.hurrican.model.cc.hardware;

import org.apache.commons.lang.ArrayUtils;

/**
 * Repraesentiert den Typ eines Switches.
 *
 *
 * @since Release 10
 */
public enum HWSwitchType {

    EWSD,
    SOFTSWITCH,
    IMS,
    NSP;

    // NOSONAR findbugs:MS_PKGPROTECT
    public final static HWSwitchType[] IMS_OR_NSP = new HWSwitchType[] { IMS, NSP };

    public static boolean isImsOrNsp(HWSwitchType hwSwitchType) {
        return hwSwitchType != null && ArrayUtils.contains(IMS_OR_NSP, hwSwitchType);
    }
}
