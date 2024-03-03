/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MontageleistungMitReservierungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRnrRngNrType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernportierungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UebertragungsverfahrenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALVerbundleistungTypeBuilder implements LineOrderTypeBuilder<TALVerbundleistungType> {

    private String vorabstimmungsID;
    private StandortAType standortA;
    private StandortBType standortB;
    private MontageleistungMitReservierungType montageleistung;
    private UebertragungsverfahrenType uebertragungsverfahren;
    private SchaltangabenType schaltangaben;
    private OnkzRnrRngNrType bestandssuche;
    private RufnummernportierungType rufnummerPortierung;

    @Override
    public TALVerbundleistungType build() {
        TALVerbundleistungType talLeistungsaenderungType = new TALVerbundleistungType();
        talLeistungsaenderungType.setVorabstimmungsID(vorabstimmungsID);
        talLeistungsaenderungType.setMontageleistung(montageleistung);
        talLeistungsaenderungType.setSchaltangaben(schaltangaben);
        talLeistungsaenderungType.setStandortA(standortA);
        talLeistungsaenderungType.setStandortB(standortB);
        talLeistungsaenderungType.setUebertragungsverfahren(uebertragungsverfahren);
        talLeistungsaenderungType.setBestandssuche(bestandssuche);
        talLeistungsaenderungType.setRufnummernPortierung(rufnummerPortierung);
        return talLeistungsaenderungType;
    }

    public TALVerbundleistungTypeBuilder withVorabstimmungsID(String vorabstimmungsID) {
        this.vorabstimmungsID = vorabstimmungsID;
        return this;
    }

    public TALVerbundleistungTypeBuilder withBestandssuche(OnkzRnrRngNrType bestandssuche) {
        this.bestandssuche = bestandssuche;
        return this;
    }

    public TALVerbundleistungTypeBuilder withRufnummerPortierung(RufnummernportierungType rufnummerPortierung) {
        this.rufnummerPortierung = rufnummerPortierung;
        return this;
    }

    public TALVerbundleistungTypeBuilder withStandortA(StandortAType standortA) {
        this.standortA = standortA;
        return this;
    }

    public TALVerbundleistungTypeBuilder withStandortB(StandortBType standortB) {
        this.standortB = standortB;
        return this;
    }

    public TALVerbundleistungTypeBuilder withMontageleistung(MontageleistungMitReservierungType montageleistung) {
        this.montageleistung = montageleistung;
        return this;
    }

    public TALVerbundleistungTypeBuilder withUebertragungsverfahren(UebertragungsverfahrenType uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
        return this;
    }

    public TALVerbundleistungTypeBuilder withSchaltangaben(SchaltangabenType schaltangaben) {
        this.schaltangaben = schaltangaben;
        return this;
    }

}
