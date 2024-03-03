/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2011 09:54:11
 */
package de.mnet.wita.message.builder;

import java.time.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.builder.auftrag.SchaltangabenBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKvzTalBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;

public class AuftragBuilder extends MnetWitaRequestBuilder<Auftrag> {

    public AuftragBuilder(GeschaeftsfallTyp geschaeftsfallTyp) {
        this(geschaeftsfallTyp, WitaCdmVersion.getDefault());
    }

    public AuftragBuilder(GeschaeftsfallTyp geschaeftsfallTyp, WitaCdmVersion witaCdmVersion) {
        super(geschaeftsfallTyp, witaCdmVersion);
    }

    public AuftragBuilder(GeschaeftsfallBuilder geschaeftsfallBuilder) {
        super(geschaeftsfallBuilder);
    }

    @Override
    public Auftrag buildValid() {
        return buildValidRequest();
    }

    public Auftrag buildKueKdWithLeitungsSchluesselZahl(String leitungsSchluesselZahl) {
        Auftrag auftrag = buildValid();

        LeitungsBezeichnung lbz = new LeitungsBezeichnung();
        lbz.setLeitungsSchluesselZahl(leitungsSchluesselZahl);

        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setLeitungsBezeichnung(lbz);
        return auftrag;
    }

    public Auftrag buildAuftragWithSchaltungKupfer(SchaltungKupfer schaltungKupfer) {
        Auftrag auftrag = buildValid();
        Schaltangaben schaltangaben = new SchaltangabenBuilder(witaCdmVersion).withSchaltungKupferZweiDraht(schaltungKupfer).buildValid();
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);
        return auftrag;
    }

    public Auftrag buildAuftragWithSchaltungKupfer() {
        Auftrag auftrag = buildValid();
        Schaltangaben schaltangaben = new SchaltangabenBuilder(witaCdmVersion).withSchaltungKupferZweiDraht(
                new SchaltungKupferBuilder(witaCdmVersion).withUebertragungsverfahren(Uebertragungsverfahren.H01).buildValid()).buildValid();
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);
        return auftrag;
    }

    public Auftrag buildAuftragWithSchaltungKupferVierDraht() {
        Auftrag auftrag = buildValid();
        Schaltangaben schaltangaben = new SchaltangabenBuilder(witaCdmVersion).withSchaltungKupferVierDraht(
                new SchaltungKupferBuilder(witaCdmVersion).buildValid()).buildValid();
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);
        return auftrag;
    }

    public Auftrag buildAuftragWithSchaltungKvz() {
        Auftrag auftrag = buildValid();
        Schaltangaben schaltangaben = new SchaltangabenBuilder(witaCdmVersion).withSchaltungKvzTal(
                new SchaltungKvzTalBuilder(witaCdmVersion).buildValid()).buildValid();
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);
        return auftrag;
    }

    public Auftrag buildAuftragWithKundenwunschTermnin(LocalDate kwt) {
        Auftrag auftrag = buildValid();
        auftrag.getGeschaeftsfall().getKundenwunschtermin().setDatum(kwt);
        return auftrag;
    }

    public Auftrag buildAuftragWithEinzelrufnummer(int countOfDns) {
        Auftrag auftrag = buildValid();
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setRufnummernPortierung(createRufnummernPortierungEinzelanschluss(countOfDns));
        return auftrag;
    }

    public Auftrag buildAuftragWithAnlagenrufnummer() {
        Auftrag auftrag = buildValid();
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setRufnummernPortierung(createRufnummernPortierungAnlagenanschluss());
        return auftrag;
    }

    private RufnummernPortierungEinzelanschluss createRufnummernPortierungEinzelanschluss(int countOfDns) {
        RufnummernPortierungEinzelanschluss portierung = new RufnummernPortierungEinzelanschluss();
        for (int i = 0; i < countOfDns; i++) {
            EinzelanschlussRufnummer dn = new EinzelanschlussRufnummer();
            dn.setOnkz("821");
            dn.setRufnummer(String.format("1234%s", i));

            portierung.addRufnummer(dn);
        }
        return portierung;
    }

    private RufnummernPortierungAnlagenanschluss createRufnummernPortierungAnlagenanschluss() {
        RufnummernPortierungAnlagenanschluss portierung = new RufnummernPortierungAnlagenanschluss();
        portierung.setOnkz("821");
        portierung.setDurchwahl("1234");
        portierung.setAbfragestelle("1");
        portierung.setDurchwahl("0");
        return portierung;
    }

}
