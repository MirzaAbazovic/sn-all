/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AngabenZurLeitungABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungIsisOpalMeldungType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALABMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALABMType> {

    private String vertragsnummer;
    private AngabenZurLeitungABMType leitung;
    private AnschlussType anschluss;
    private SchaltungIsisOpalMeldungType schaltangaben;
    private RufnummernportierungMeldungType rnrPortierung;

    @Override
    public MeldungsattributeTALABMType build() {
        MeldungsattributeTALABMType meldungsattribute = new MeldungsattributeTALABMType();
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setAnschluss(anschluss);
        meldungsattribute.setLeitung(leitung);
        meldungsattribute.setRnrPortierung(rnrPortierung);
        meldungsattribute.setSchaltangaben(schaltangaben);
        return meldungsattribute;
    }

    public MeldungsattributeTALABMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeTALABMTypeBuilder withLeitung(AngabenZurLeitungABMType leitung) {
        this.leitung = leitung;
        return this;
    }

    public MeldungsattributeTALABMTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }

    public MeldungsattributeTALABMTypeBuilder withSchaltangaben(SchaltungIsisOpalMeldungType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

    public MeldungsattributeTALABMTypeBuilder withRnrPortierung(RufnummernportierungMeldungType rnrPortierung) {
        this.rnrPortierung = rnrPortierung;
        return this;
    }

}
