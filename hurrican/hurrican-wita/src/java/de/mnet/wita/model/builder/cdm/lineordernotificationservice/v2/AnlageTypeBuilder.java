/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DokumenttypType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class AnlageTypeBuilder implements LineOrderNotificationTypeBuilder<AnlageType> {

    private String dateiname;
    private DokumenttypType dateityp;
    private String beschreibung;
    private byte[] inhalt;

    @Override
    public AnlageType build() {
        return enrich(new AnlageType());
    }

    protected <AT extends AnlageType> AT enrich(AT anlageType) {
        anlageType.setBeschreibung(beschreibung);
        anlageType.setDateiname(dateiname);
        anlageType.setDateityp(dateityp);
        anlageType.setInhalt(inhalt);
        return anlageType;
    }

    public AnlageTypeBuilder withDateiname(String value) {
        this.dateiname = value;
        return this;
    }

    public AnlageTypeBuilder withDateityp(DokumenttypType value) {
        this.dateityp = value;
        return this;
    }

    public AnlageTypeBuilder withBeschreibung(String value) {
        this.beschreibung = value;
        return this;
    }

    public AnlageTypeBuilder withInhalt(byte[] value) {
        this.inhalt = ((byte[]) value);
        return this;
    }

}
