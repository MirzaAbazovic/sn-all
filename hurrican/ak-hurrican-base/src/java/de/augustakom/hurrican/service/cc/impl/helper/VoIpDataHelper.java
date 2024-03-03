/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2015
 */
package de.augustakom.hurrican.service.cc.impl.helper;

import de.augustakom.hurrican.model.cc.AuftragDaten;

/**
 *
 */
public class VoIpDataHelper {

    public static final String PPPOE_PW = "voice";

    public static final String getPppoeUser(AuftragDaten auftragDaten) {
        if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
            return String.format("%d-VOIP", auftragDaten.getAuftragNoOrig());
        }
        else {
            return null;
        }
    }

}
