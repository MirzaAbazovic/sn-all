/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.03.2012 13:24:29
 */
package de.augustakom.hurrican.model.wholesale;

import de.augustakom.hurrican.service.wholesale.WholesaleService;

/**
 * Request Objekt fuer die Methode {@link WholesaleService#cancelModifyPort(WholesaleCancelModifyPortRequest)}
 */
public class WholesaleCancelModifyPortRequest {

    private String lineId;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

}


