package de.mnet.wbci.helper;/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.14
 */

import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wita.message.GeschaeftsfallTyp;

/**
 *
 */
public class WbciRequestStatusHelper {

    public static WbciRequestStatus getActiveWbciRequestStatus(GeschaeftsfallTyp witaGeschaeftsfallTyp) {
        if (GeschaeftsfallTyp.KUENDIGUNG_KUNDE.equals(witaGeschaeftsfallTyp)) {
            return WbciRequestStatus.AKM_TR_EMPFANGEN;
        }
        return WbciRequestStatus.AKM_TR_VERSENDET;
    }
}
