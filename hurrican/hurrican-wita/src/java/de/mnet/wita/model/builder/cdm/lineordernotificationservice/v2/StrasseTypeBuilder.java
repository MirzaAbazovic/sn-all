/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StrasseType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class StrasseTypeBuilder implements LineOrderNotificationTypeBuilder<StrasseType> {

    private String strassenname;
    private String hausnummer;
    private String hausnummernZusatz;

    @Override
    public StrasseType build() {
        StrasseType strasseType = new StrasseType();
        strasseType.setHausnummer(hausnummer);
        strasseType.setHausnummernZusatz(hausnummernZusatz);
        strasseType.setStrassenname(strassenname);
        return strasseType;
    }

    public StrasseTypeBuilder withStrassenname(String strassenname) {
        this.strassenname = strassenname;
        return this;
    }

    public StrasseTypeBuilder withHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
        return this;
    }

    public StrasseTypeBuilder withHausnummernZusatz(String hausnummernZusatz) {
        this.hausnummernZusatz = hausnummernZusatz;
        return this;
    }

}
