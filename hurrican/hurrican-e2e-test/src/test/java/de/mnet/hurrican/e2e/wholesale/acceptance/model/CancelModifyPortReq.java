/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2012 15:59:04
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import de.mnet.hurrican.wholesale.workflow.CancelModifyPortRequest;

/**
 *
 */
public class CancelModifyPortReq {

    public String lineId;

    CancelModifyPortReq lineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public CancelModifyPortRequest toXmlBean() {
        CancelModifyPortRequest cancelModifyPortRequest = new CancelModifyPortRequest();
        cancelModifyPortRequest.setLineId(lineId);
        return cancelModifyPortRequest;
    }
}


