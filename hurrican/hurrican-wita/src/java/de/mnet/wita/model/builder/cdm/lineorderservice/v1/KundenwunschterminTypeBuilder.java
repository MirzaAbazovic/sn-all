/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class KundenwunschterminTypeBuilder implements LineOrderTypeBuilder<KundenwunschterminType> {

    private LocalDate datum;
    private String zeitfenster;

    @Override
    public KundenwunschterminType build() {
        KundenwunschterminType geschaeftsfallTerminverschiebung = new KundenwunschterminType();
        geschaeftsfallTerminverschiebung.setZeitfenster(zeitfenster);
        geschaeftsfallTerminverschiebung.setDatum(DateConverterUtils.toXmlGregorianCalendar(datum));
        return geschaeftsfallTerminverschiebung;
    }

    public KundenwunschterminTypeBuilder withDatum(LocalDate datum) {
        this.datum = datum;
        return this;
    }

    public KundenwunschterminTypeBuilder withZeitfenster(String zeitfenster) {
        this.zeitfenster = zeitfenster;
        return this;
    }

}
