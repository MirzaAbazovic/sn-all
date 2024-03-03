/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 14:23:06
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import java.time.*;

import de.mnet.hurrican.wholesale.workflow.Ekp;
import de.mnet.hurrican.wholesale.workflow.ModifyPortRequest;

/**
 *
 */
public class ModifyPortReq {
    public String lineId;
    public String ekpId = "DTAG1";
    public String ekpFrameId = "DTAG1-001";
    public Produkt produkt = Produkt.fttb50();
    public boolean changeOfPortAllowed = false;
    public LocalDate desiredExecutionDate = LocalDate.now().plusDays(1);

    public ModifyPortRequest toXmlBean() {
        ModifyPortRequest modifyPortRequest = new ModifyPortRequest();
        modifyPortRequest.setLineId(lineId);
        Ekp ekp = new Ekp();
        ekp.setId(ekpId);
        ekp.setFrameContractId(ekpFrameId);
        modifyPortRequest.setEkp(ekp);
        modifyPortRequest.setProduct(produkt.toXmlBean());
        modifyPortRequest.setChangeOfPortAllowed(changeOfPortAllowed);
        modifyPortRequest.setDesiredExecutionDate(desiredExecutionDate);
        return modifyPortRequest;
    }

    public ModifyPortReq lineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public ModifyPortReq ekpId(String ekpId) {
        this.ekpId = ekpId;
        return this;
    }

    public ModifyPortReq ekpFrameId(String ekpFrameId) {
        this.ekpFrameId = ekpFrameId;
        return this;
    }

    public ModifyPortReq produkt(Produkt produkt) {
        this.produkt = produkt;
        return this;
    }

    public ModifyPortReq changeOfPortAllowed(boolean changeOfPortAllowed) {
        this.changeOfPortAllowed = changeOfPortAllowed;
        return this;
    }

    public ModifyPortReq desiredExecutionDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
        return this;
    }
}


