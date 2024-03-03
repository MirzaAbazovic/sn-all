/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2009 12:30:01
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unused")
public class BAuftragPosBuilder extends BillingEntityBuilder<BAuftragPosBuilder, BAuftragPos> {

    private Long itemNo;
    private Long itemNoOrig;
    private Long auftragNoOrig;
    private Long createAuftragNo;
    private Long terminateAuftragNo;
    private Boolean isPlanned;
    private Long leistungNoOrig;
    private String parameter;
    private Long menge;
    private Date chargeFrom;
    private Date chargeTo;
    private Date chargedUntil;
    private Float preis;
    private Float listenpreis;
    private Long deviceNo;
    private Long timestamp;

    public BAuftragPosBuilder withItemNo(Long itemNo) {
        this.itemNo = itemNo;
        return this;
    }

    public BAuftragPosBuilder withItemNoOrig(Long itemNoOrig) {
        this.itemNoOrig = itemNoOrig;
        return this;
    }

    public BAuftragPosBuilder withChargeFrom(Date chargeFrom) {
        this.chargeFrom = chargeFrom;
        return this;
    }

    public BAuftragPosBuilder withChargeTo(Date chargeTo) {
        this.chargeTo = chargeTo;
        return this;
    }

    public BAuftragPosBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public BAuftragPosBuilder withCreateAuftragNo(Long createAuftragNo) {
        this.createAuftragNo = createAuftragNo;
        return this;
    }

    public BAuftragPosBuilder withTerminateAuftragNo(Long terminateAuftragNo) {
        this.terminateAuftragNo = terminateAuftragNo;
        return this;
    }

    public BAuftragPosBuilder withMenge(Long menge) {
        this.menge = menge;
        return this;
    }

    public BAuftragPosBuilder withLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
        return this;
    }

    public BAuftragPosBuilder withParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    public BAuftragPosBuilder withPreis(Float preis) {
        this.preis = preis;
        return this;
    }

    public BAuftragPosBuilder withDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
        return this;
    }

    public BAuftragPosBuilder withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

}


