/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MontageleistungMitReservierungType;

/**
 *
 */
public class MontageleistungMitReservierungTypeBuilder extends MontageleistungTypeBuilder {

    private String terminReservierungsID;

    @Override
    public MontageleistungMitReservierungType build() {
        MontageleistungMitReservierungType montageleistung = new MontageleistungMitReservierungType();
        montageleistung.setTerminReservierungsID(terminReservierungsID);
        return enrich(montageleistung);
    }

    public MontageleistungMitReservierungTypeBuilder withTerminReservierungsID(String terminReservierungsID) {
        this.terminReservierungsID = terminReservierungsID;
        return this;
    }

}
