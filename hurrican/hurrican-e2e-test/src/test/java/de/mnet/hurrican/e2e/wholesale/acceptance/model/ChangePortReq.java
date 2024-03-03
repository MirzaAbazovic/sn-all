/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2012 07:04:27
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import de.mnet.hurrican.wholesale.fault.clearance.ChangePortRequest;

public class ChangePortReq {

    public String lineId;
    public long newPortId;
    public long changeReasonId;

    public ChangePortRequest toXmlBean() {
        ChangePortRequest changePortRequest = new ChangePortRequest();
        changePortRequest.setLineId(lineId);
        changePortRequest.setNewPortId(newPortId);
        changePortRequest.setChangeReasonId(changeReasonId);
        return changePortRequest;
    }

    public ChangePortReq lineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public ChangePortReq newPortId(long newPortId) {
        this.newPortId = newPortId;
        return this;
    }

    public ChangePortReq changeReasonId(long changeReasonId) {
        this.changeReasonId = changeReasonId;
        return this;
    }

}


