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
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALLeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UebertragungsverfahrenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALLeistungsaenderungTypeBuilder implements LineOrderTypeBuilder<TALLeistungsaenderungType> {

    private StandortAType standortA;
    private StandortBType standortB;
    private MontageleistungMitReservierungType montageleistung;
    private UebertragungsverfahrenType uebertragungsverfahren;
    private SchaltangabenType schaltangaben;
    private LeitungsbezeichnungType leitungsbezeichnungType;

    @Override
    public TALLeistungsaenderungType build() {
        TALLeistungsaenderungType talLeistungsaenderungType = new TALLeistungsaenderungType();
        talLeistungsaenderungType.setMontageleistung(montageleistung);
        talLeistungsaenderungType.setSchaltangaben(schaltangaben);
        talLeistungsaenderungType.setStandortA(standortA);
        talLeistungsaenderungType.setStandortB(standortB);
        talLeistungsaenderungType.setUebertragungsverfahren(uebertragungsverfahren);
        talLeistungsaenderungType.setBestandsvalidierung2(leitungsbezeichnungType);
        return talLeistungsaenderungType;
    }
    public TALLeistungsaenderungTypeBuilder withLeitungsbezeichnungType(LeitungsbezeichnungType leitungsbezeichnungType) {
        this.leitungsbezeichnungType = leitungsbezeichnungType;
        return this;
    }

    public TALLeistungsaenderungTypeBuilder withStandortA(StandortAType standortA) {
        this.standortA = standortA;
        return this;
    }

    public TALLeistungsaenderungTypeBuilder withStandortB(StandortBType standortB) {
        this.standortB = standortB;
        return this;
    }

    public TALLeistungsaenderungTypeBuilder withMontageleistung(MontageleistungMitReservierungType montageleistung) {
        this.montageleistung = montageleistung;
        return this;
    }

    public TALLeistungsaenderungTypeBuilder withUebertragungsverfahren(UebertragungsverfahrenType uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
        return this;
    }

    public TALLeistungsaenderungTypeBuilder withSchaltangaben(SchaltangabenType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

}
