/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 11:16:44
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import java.time.*;
import java.util.*;

import de.mnet.hurrican.wholesale.workflow.Ekp;
import de.mnet.hurrican.wholesale.workflow.ReservePortRequest;

public class ReservePortReq {
    public String ekpId = "DTAG1";
    public String frameContractId = "DTAG1-001";
    public Long geoId = 32146L;
    public Produkt produkt = new Produkt();
    public LocalDate desiredExecutionDate = LocalDate.now();

    private Random random = new Random();

    public ReservePortReq ekpId(String ekpId) {
        this.ekpId = ekpId;
        return this;
    }

    public ReservePortReq frameContractId(String frameContractId) {
        this.frameContractId = frameContractId;
        return this;
    }

    public ReservePortRequest toXmlBean() {
        ReservePortRequest request = new ReservePortRequest();
        request.setExtOrderId(String.valueOf(random.nextInt(100000)));
        Ekp ekp = new Ekp();
        ekp.setId(ekpId);
        ekp.setFrameContractId(frameContractId);
        request.setEkp(ekp);
        request.setGeoId(geoId);
        request.setProduct(produkt.toXmlBean());
        request.setDesiredExecutionDate(desiredExecutionDate);
        return request;
    }

    public ReservePortReq produkt(Produkt produkt) {
        this.produkt = produkt;
        return this;
    }

    public ReservePortReq geoId(long geoId) {
        this.geoId = geoId;
        return this;
    }

    public ReservePortReq desiredExecutionDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
        return this;
    }

}

