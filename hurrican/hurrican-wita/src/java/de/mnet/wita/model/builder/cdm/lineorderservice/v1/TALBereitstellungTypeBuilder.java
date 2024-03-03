/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MontageleistungMitReservierungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UebertragungsverfahrenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VormieterAnschlussType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALBereitstellungTypeBuilder implements LineOrderTypeBuilder<TALBereitstellungType> {

    private String vorabstimmungsID;
    private StandortAType standortA;
    private StandortBType standortB;
    private VormieterAnschlussType vormieter;
    private MontageleistungMitReservierungType montageleistung;
    private UebertragungsverfahrenType uebertragungsverfahren;
    private SchaltangabenType schaltangaben;

    @Override
    public TALBereitstellungType build() {
        TALBereitstellungType talBereitstellungType = new TALBereitstellungType();
        talBereitstellungType.setMontageleistung(montageleistung);
        talBereitstellungType.setSchaltangaben(schaltangaben);
        talBereitstellungType.setStandortA(standortA);
        talBereitstellungType.setStandortB(standortB);
        talBereitstellungType.setUebertragungsverfahren(uebertragungsverfahren);
        talBereitstellungType.setVorabstimmungsID(vorabstimmungsID);
        talBereitstellungType.setVormieter(vormieter);
        return talBereitstellungType;
    }

    public TALBereitstellungTypeBuilder withVorabstimmungsID(String vorabstimmungsID) {
        this.vorabstimmungsID = vorabstimmungsID;
        return this;
    }

    public TALBereitstellungTypeBuilder withStandortA(StandortAType standortA) {
        this.standortA = standortA;
        return this;
    }

    public TALBereitstellungTypeBuilder withStandortB(StandortBType standortB) {
        this.standortB = standortB;
        return this;
    }

    public TALBereitstellungTypeBuilder withVormieter(VormieterAnschlussType vormieter) {
        this.vormieter = vormieter;
        return this;
    }

    public TALBereitstellungTypeBuilder withMontageleistung(MontageleistungMitReservierungType montageleistung) {
        this.montageleistung = montageleistung;
        return this;
    }

    public TALBereitstellungTypeBuilder withUebertragungsverfahren(UebertragungsverfahrenType uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
        return this;
    }

    public TALBereitstellungTypeBuilder withSchaltangaben(SchaltangabenType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

}
