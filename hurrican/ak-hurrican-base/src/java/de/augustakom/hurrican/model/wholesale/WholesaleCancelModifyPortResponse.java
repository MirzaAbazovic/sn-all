/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.03.2012 13:24:41
 */
package de.augustakom.hurrican.model.wholesale;

import de.augustakom.hurrican.service.wholesale.WholesaleService;

/**
 * Response Objekt fuer die Methode {@link WholesaleService#cancelModifyPort(WholesaleCancelModifyPortRequest)}
 */
public class WholesaleCancelModifyPortResponse {

    private String previousLineId;

    public String getPreviousLineId() {
        return previousLineId;
    }

    public void setPreviousLineId(String previousLineId) {
        this.previousLineId = previousLineId;
    }

}


