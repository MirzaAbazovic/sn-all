/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALLeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UebertragungsverfahrenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALLeistungsmerkmalAenderungTypeBuilder implements LineOrderTypeBuilder<TALLeistungsmerkmalAenderungType> {

    private StandortAType standortA;
    private StandortBType standortB;
    private UebertragungsverfahrenType uebertragungsverfahren;
    private SchaltangabenType schaltangaben;
    private LeitungsbezeichnungType leitungsbezeichnungType;

    @Override
    public TALLeistungsmerkmalAenderungType build() {
        TALLeistungsmerkmalAenderungType talLeistungsaenderungType = new TALLeistungsmerkmalAenderungType();
        talLeistungsaenderungType.setSchaltangaben(schaltangaben);
        talLeistungsaenderungType.setStandortA(standortA);
        talLeistungsaenderungType.setStandortB(standortB);
        talLeistungsaenderungType.setUebertragungsverfahren(uebertragungsverfahren);
        talLeistungsaenderungType.setBestandsvalidierung2(leitungsbezeichnungType);
        return talLeistungsaenderungType;
    }

    public TALLeistungsmerkmalAenderungTypeBuilder withLeitungsbezeichnungType(LeitungsbezeichnungType leitungsbezeichnungType) {
        this.leitungsbezeichnungType = leitungsbezeichnungType;
        return this;
    }

    public TALLeistungsmerkmalAenderungTypeBuilder withStandortA(StandortAType standortA) {
        this.standortA = standortA;
        return this;
    }

    public TALLeistungsmerkmalAenderungTypeBuilder withStandortB(StandortBType standortB) {
        this.standortB = standortB;
        return this;
    }

    public TALLeistungsmerkmalAenderungTypeBuilder withUebertragungsverfahren(UebertragungsverfahrenType uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
        return this;
    }

    public TALLeistungsmerkmalAenderungTypeBuilder withSchaltangaben(SchaltangabenType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

}
