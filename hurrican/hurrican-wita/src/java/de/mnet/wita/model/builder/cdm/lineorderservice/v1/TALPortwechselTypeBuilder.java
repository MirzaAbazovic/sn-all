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
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALPortwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALPortwechselTypeBuilder implements LineOrderTypeBuilder<TALPortwechselType> {

    private StandortBType standortB;
    private MontageleistungMitReservierungType montageleistung;
    private SchaltangabenType schaltangaben;
    private LeitungsbezeichnungType leitungsbezeichnungType;

    @Override
    public TALPortwechselType build() {
        TALPortwechselType talLeistungsaenderungType = new TALPortwechselType();
        talLeistungsaenderungType.setMontageleistung(montageleistung);
        talLeistungsaenderungType.setSchaltangaben(schaltangaben);
        talLeistungsaenderungType.setStandortB(standortB);
        talLeistungsaenderungType.setBestandsvalidierung2(leitungsbezeichnungType);
        return talLeistungsaenderungType;
    }
    public TALPortwechselTypeBuilder withLeitungsbezeichnungType(LeitungsbezeichnungType leitungsbezeichnungType) {
        this.leitungsbezeichnungType = leitungsbezeichnungType;
        return this;
    }

    public TALPortwechselTypeBuilder withStandortB(StandortBType standortB) {
        this.standortB = standortB;
        return this;
    }

    public TALPortwechselTypeBuilder withMontageleistung(MontageleistungMitReservierungType montageleistung) {
        this.montageleistung = montageleistung;
        return this;
    }

    public TALPortwechselTypeBuilder withSchaltangaben(SchaltangabenType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

}
