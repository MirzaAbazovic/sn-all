/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class OnkzDurchwahlAbfragestelleTypeBuilder implements LineOrderNotificationTypeBuilder<OnkzDurchwahlAbfragestelleType> {

    private String onkz;
    private String durchwahlnummer;
    private String abfragestelle;

    @Override
    public OnkzDurchwahlAbfragestelleType build() {
        OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelleType = new OnkzDurchwahlAbfragestelleType();
        onkzDurchwahlAbfragestelleType.setONKZ(onkz);
        onkzDurchwahlAbfragestelleType.setAbfragestelle(abfragestelle);
        onkzDurchwahlAbfragestelleType.setDurchwahlnummer(durchwahlnummer);
        return onkzDurchwahlAbfragestelleType;
    }

    public OnkzDurchwahlAbfragestelleTypeBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public OnkzDurchwahlAbfragestelleTypeBuilder withDurchwahlnummer(String durchwahlnummer) {
        this.durchwahlnummer = durchwahlnummer;
        return this;
    }

    public OnkzDurchwahlAbfragestelleTypeBuilder withAbfragestelle(String abfragestelle) {
        this.abfragestelle = abfragestelle;
        return this;
    }

}
