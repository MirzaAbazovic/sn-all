/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MontageleistungMitReservierungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALProduktgruppenWechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UebertragungsverfahrenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALProduktgruppenWechselTypeBuilder implements LineOrderTypeBuilder<TALProduktgruppenWechselType> {

    private StandortAType standortA;
    private StandortBType standortB;
    private MontageleistungMitReservierungType montageleistung;
    private UebertragungsverfahrenType uebertragungsverfahren;
    private SchaltangabenType schaltangaben;
    private LeitungsbezeichnungType leitungsbezeichnungType;

    @Override
    public TALProduktgruppenWechselType build() {
        TALProduktgruppenWechselType talLeistungsaenderungType = new TALProduktgruppenWechselType();
        talLeistungsaenderungType.setMontageleistung(montageleistung);
        talLeistungsaenderungType.setSchaltangaben(schaltangaben);
        talLeistungsaenderungType.setStandortA(standortA);
        talLeistungsaenderungType.setStandortB(standortB);
        talLeistungsaenderungType.setUebertragungsverfahren(uebertragungsverfahren);
        talLeistungsaenderungType.setBestandsvalidierung2(leitungsbezeichnungType);
        return talLeistungsaenderungType;
    }
    public TALProduktgruppenWechselTypeBuilder withLeitungsbezeichnungType(LeitungsbezeichnungType leitungsbezeichnungType) {
        this.leitungsbezeichnungType = leitungsbezeichnungType;
        return this;
    }

    public TALProduktgruppenWechselTypeBuilder withStandortA(StandortAType standortA) {
        this.standortA = standortA;
        return this;
    }

    public TALProduktgruppenWechselTypeBuilder withStandortB(StandortBType standortB) {
        this.standortB = standortB;
        return this;
    }

    public TALProduktgruppenWechselTypeBuilder withMontageleistung(MontageleistungMitReservierungType montageleistung) {
        this.montageleistung = montageleistung;
        return this;
    }

    public TALProduktgruppenWechselTypeBuilder withUebertragungsverfahren(UebertragungsverfahrenType uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
        return this;
    }

    public TALProduktgruppenWechselTypeBuilder withSchaltangaben(SchaltangabenType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

}
