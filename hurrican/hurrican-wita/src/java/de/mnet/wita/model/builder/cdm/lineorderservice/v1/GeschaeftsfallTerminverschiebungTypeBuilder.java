/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.GeschaeftsfallTerminverschiebungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallTerminverschiebungTypeBuilder implements LineOrderTypeBuilder<GeschaeftsfallTerminverschiebungType> {

    private LocalDate kundenwunschtermin;
    private String zeitfenster;

    @Override
    public GeschaeftsfallTerminverschiebungType build() {
        return enrich(new GeschaeftsfallTerminverschiebungType());
    }

    protected <GT extends GeschaeftsfallTerminverschiebungType> GT enrich(GT geschaeftsfallTerminverschiebung) {
        geschaeftsfallTerminverschiebung.setZeitfenster(zeitfenster);
        geschaeftsfallTerminverschiebung.setKundenwunschtermin(DateConverterUtils.toXmlGregorianCalendar(kundenwunschtermin));
        return geschaeftsfallTerminverschiebung;

    }

    public GeschaeftsfallTerminverschiebungTypeBuilder withKundenwunschtermin(LocalDate kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

    public GeschaeftsfallTerminverschiebungTypeBuilder withZeitfenster(String zeitfenster) {
        this.zeitfenster = zeitfenster;
        return this;
    }

}
