/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2011 09:54:11
 */
package de.mnet.wita.message.builder.auftrag.geschaeftsfall;

import java.time.*;
import java.util.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.Ansprechpartner;
import de.mnet.wita.message.auftrag.AnsprechpartnerRolle;
import de.mnet.wita.message.auftrag.Auftragsmanagement;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.GeschaeftsfallAnsprechpartner;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.TestAnlage;
import de.mnet.wita.message.builder.auftrag.AuftragspositionBuilder;
import de.mnet.wita.message.builder.auftrag.GeschaeftsfallProduktBuilder;
import de.mnet.wita.message.builder.auftrag.MontageleistungBuilder;
import de.mnet.wita.message.builder.auftrag.StandortKundeBuilder;
import de.mnet.wita.message.builder.common.portierung.RufnummernPortierungBuilder;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.Uebertragungsverfahren;

public class GeschaeftsfallBuilder {

    private final GeschaeftsfallTyp geschaeftsfallTyp;
    private final WitaCdmVersion witaCdmVersion;
    private String ansprechpartnerNachname = "Default";
    private Kundenwunschtermin kundenwunschtermin;
    private BestandsSuche bestandsSuche;
    private String vertragsnummer = "0000000123";
    private final List<Anlage> anlagen = new ArrayList<>();
    private GeschaeftsfallProdukt geschaeftsfallProdukt;

    public GeschaeftsfallBuilder(GeschaeftsfallTyp geschaeftsfallTyp) {
        this(geschaeftsfallTyp, WitaCdmVersion.getDefault());
    }

    public GeschaeftsfallBuilder(GeschaeftsfallTyp geschaeftsfallTyp, WitaCdmVersion witaCdmVersion) {
        this.witaCdmVersion = witaCdmVersion;
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        this.kundenwunschtermin = createDefaultKundenwunschtermin(witaCdmVersion);
        GeschaeftsfallProduktBuilder geschaeftsfallProduktBuilder = new GeschaeftsfallProduktBuilder();
        this.geschaeftsfallProdukt = geschaeftsfallProduktBuilder.withGeschaeftsfallTyp(geschaeftsfallTyp).buildValid();
    }

    public GeschaeftsfallBuilder withKundenwunschtermin(LocalDate datum, Zeitfenster zeitfenster) {
        this.kundenwunschtermin = createKundenwunschtermin(datum, zeitfenster);
        return this;
    }

    public GeschaeftsfallBuilder withKundenwunschtermin(LocalDate datum) {
        this.kundenwunschtermin = createKundenwunschtermin(datum, null);
        return this;
    }

    public GeschaeftsfallBuilder withAnsprechpartnerNachname(String nachname) {
        this.ansprechpartnerNachname = nachname;
        return this;
    }

    public GeschaeftsfallBuilder withBestandsSuche(BestandsSuche bestandsSuche) {
        this.bestandsSuche = bestandsSuche;
        return this;
    }

    public GeschaeftsfallBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public GeschaeftsfallBuilder withVormieter(Vormieter vormieter) {
        this.geschaeftsfallProdukt.setVormieter(vormieter);
        return this;
    }

    public GeschaeftsfallBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.geschaeftsfallProdukt.setVorabstimmungsId(vorabstimmungsId);
        return this;
    }

    public GeschaeftsfallBuilder withGeschaeftsfallProdukt(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        this.geschaeftsfallProdukt = geschaeftsfallProdukt;
        return this;
    }

    public GeschaeftsfallBuilder addTestAnlage(Anlage anlage) {
        anlagen.add(anlage);
        return this;
    }

    public GeschaeftsfallBuilder addTestAnlage(TestAnlage testAnlage, Anlagentyp anlagentyp) {
        return addTestAnlage(createAnlage(testAnlage, anlagentyp));
    }

    public GeschaeftsfallBuilder addTestAnlagen(TestAnlage testAnlage, Anlagentyp anlagentyp, int number) {
        for (int i = 1; i <= number; i++) {
            addTestAnlage(testAnlage, anlagentyp);
        }
        return this;
    }

    public static Anlage createAnlage(TestAnlage testAnlage, Anlagentyp anlagentyp) {
        Anlage anlage = new Anlage();
        anlage.setDateiname(testAnlage.resourceFileName);
        anlage.setDateityp(testAnlage.dateityp);
        anlage.setAnlagentyp(anlagentyp);
        anlage.setInhalt(testAnlage.getAnlageInhalt());
        return anlage;
    }

    public Geschaeftsfall buildValid() {
        Geschaeftsfall geschaeftsfall;
        try {
            geschaeftsfall = geschaeftsfallTyp.getClazz().newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        geschaeftsfall.setVertragsNummer(vertragsnummer);
        geschaeftsfall.setGfAnsprechpartner(createGfAnsprechpartner());
        geschaeftsfall.setKundenwunschtermin(kundenwunschtermin);
        geschaeftsfall.setAuftragsPosition(createAuftragsposition(geschaeftsfallProdukt));
        geschaeftsfall.getAnlagen().addAll(anlagen);
        geschaeftsfall.setBktoFatkura("5883000320");

        switch (geschaeftsfallTyp) {
            case KUENDIGUNG_KUNDE:
                geschaeftsfallProdukt.setLeitungsBezeichnung(createLeitungsBezeichnung());
                break;
            case LEISTUNGS_AENDERUNG:
                geschaeftsfallProdukt.setLeitungsBezeichnung(createLeitungsBezeichnung());
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                geschaeftsfallProdukt.setStandortKollokation(createStandortKollokation());
                geschaeftsfallProdukt.setSchaltangaben(createSchaltangaben("0101", "12", "23",
                        Uebertragungsverfahren.H13));
                break;
            case LEISTUNGSMERKMAL_AENDERUNG:
                geschaeftsfallProdukt.setStandortKunde(new StandortKundeBuilder().build());
                geschaeftsfallProdukt.setStandortKollokation(createStandortKollokation());
                geschaeftsfallProdukt.setLeitungsBezeichnung(createLeitungsBezeichnung());
                geschaeftsfallProdukt.setSchaltangaben(createSchaltangaben("0111", "02", "03",
                        Uebertragungsverfahren.H04));

                Auftragsposition mainAuftragsposition = geschaeftsfall.getAuftragsPosition();

                GeschaeftsfallProdukt subGeschaeftsfallProdukt = new GeschaeftsfallProdukt();
                subGeschaeftsfallProdukt.setStandortKollokation(geschaeftsfallProdukt.getStandortKollokation());
                subGeschaeftsfallProdukt.setSchaltangaben(createSchaltangaben("0002", "01", "01",
                        Uebertragungsverfahren.H13));

                Auftragsposition subAuftragsPosition = new Auftragsposition();
                subAuftragsPosition.setAktionsCode(AktionsCode.AENDERUNG);
                subAuftragsPosition.setProdukt(mainAuftragsposition.getProdukt());
                subAuftragsPosition.setProduktBezeichner(mainAuftragsposition.getProduktBezeichner());
                subAuftragsPosition.setGeschaeftsfallProdukt(subGeschaeftsfallProdukt);

                mainAuftragsposition.setPosition(subAuftragsPosition);

                break;
            case PROVIDERWECHSEL:
                geschaeftsfallProdukt.setLeitungsBezeichnung(createLeitungsBezeichnung());
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                geschaeftsfallProdukt.setStandortKollokation(createStandortKollokation());
                geschaeftsfallProdukt.setSchaltangaben(createSchaltangaben());
                geschaeftsfall.setAbgebenderProvider(Carrier.OTHER);
                break;
            case BEREITSTELLUNG:
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                geschaeftsfallProdukt.setStandortKunde(new StandortKundeBuilder().build());
                geschaeftsfallProdukt.setStandortKollokation(createStandortKollokation());
                geschaeftsfallProdukt.setSchaltangaben(createSchaltangaben());
                geschaeftsfall.setVertragsNummer(null);
                break;
            case BESTANDSUEBERSICHT:
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                break;
            case KUENDIGUNG_TELEKOM:
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                break;
            case PORTWECHSEL:
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                geschaeftsfallProdukt.setSchaltangaben(createSchaltangaben());
                geschaeftsfallProdukt.setStandortKollokation(createStandortKollokation());
                break;
            case PRODUKTGRUPPENWECHSEL:
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                break;
            case RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG:
                geschaeftsfallProdukt.setBestandsSuche(bestandsSuche == null ? createBestandsSuche(true)
                        : bestandsSuche);
                geschaeftsfallProdukt.setRufnummernPortierung(new RufnummernPortierungBuilder()
                        .buildAuftragPortierung(false));
                geschaeftsfallProdukt.setStandortKunde(new StandortKundeBuilder().build());
                geschaeftsfall.setAbgebenderProvider(Carrier.DTAG);
                break;
            case VERBUNDLEISTUNG:
                geschaeftsfall.setVertragsNummer(null);
                geschaeftsfallProdukt.setMontageleistung(new MontageleistungBuilder().buildValid());
                geschaeftsfallProdukt.setRufnummernPortierung(new RufnummernPortierungBuilder()
                        .buildAuftragPortierung(true));
                geschaeftsfallProdukt.setStandortKollokation(createStandortKollokation());
                geschaeftsfallProdukt.setSchaltangaben(createSchaltangaben());
                geschaeftsfallProdukt.setBestandsSuche(bestandsSuche == null ? createBestandsSuche(false)
                        : bestandsSuche);
                geschaeftsfall.setAbgebenderProvider(Carrier.DTAG);
                break;
            default:
        }
        return geschaeftsfall;
    }

    private BestandsSuche createBestandsSuche(boolean extended) {
        BestandsSuche bestandSuche;
        if (!extended) {
            bestandSuche = new BestandsSuche("1234", "0123456789", null, null, null);
        }
        else {
            bestandSuche = new BestandsSuche(null, null, "2345", "1234", "2344");
        }
        return bestandSuche;
    }

    private Auftragsposition createAuftragsposition(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        return new AuftragspositionBuilder().withGeschaeftsfallTyp(geschaeftsfallTyp).withGeschaeftsfallProdukt(
                geschaeftsfallProdukt).build();
    }

    private Schaltangaben createSchaltangaben() {
        return createSchaltangaben("0001", "01", "01", Uebertragungsverfahren.H04);
    }

    private Schaltangaben createSchaltangaben(String uevt, String evs, String doppelader, Uebertragungsverfahren uetv) {
        SchaltungKupfer schaltung = new SchaltungKupfer();
        schaltung.setUEVT(uevt);
        schaltung.setEVS(evs);
        schaltung.setDoppelader(doppelader);
        schaltung.setUebertragungsverfahren(uetv);

        Schaltangaben schaltangaben = new Schaltangaben();
        schaltangaben.setSchaltungKupfer(Collections.singletonList(schaltung));
        return schaltangaben;
    }

    private Kundenwunschtermin createDefaultKundenwunschtermin(WitaCdmVersion witaCdmVersion) {
        switch (geschaeftsfallTyp) {
            case BEREITSTELLUNG:
            case LEISTUNGS_AENDERUNG:
            case PROVIDERWECHSEL:
            case VERBUNDLEISTUNG:
            case RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG:
                return createKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_2);
            case PRODUKTGRUPPENWECHSEL:
            case PORTWECHSEL:
            case KUENDIGUNG_KUNDE:
            case LEISTUNGSMERKMAL_AENDERUNG:
            case KUENDIGUNG_TELEKOM:
            case BESTANDSUEBERSICHT:
            default:
        }
        switch (witaCdmVersion) {
            case V1:
            case V2:
                return createKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_2);
            default:
                throw new IllegalArgumentException("Unexpected Wita version provided: " + witaCdmVersion);
        }
    }

    private Kundenwunschtermin createKundenwunschtermin(LocalDate datum, Zeitfenster zeitfenster) {
        Kundenwunschtermin kundenwunschtermin = new Kundenwunschtermin();
        kundenwunschtermin.setDatum(datum);
        kundenwunschtermin.setZeitfenster(zeitfenster);
        return kundenwunschtermin;
    }

    private GeschaeftsfallAnsprechpartner createGfAnsprechpartner() {
        GeschaeftsfallAnsprechpartner ansprechpartner = new GeschaeftsfallAnsprechpartner();
        ansprechpartner.setAuftragsmanagement(createAuftragsmanagement());
        ansprechpartner.setAnsprechpartner(createAnsprechpartner());
        return ansprechpartner;
    }

    private Auftragsmanagement createAuftragsmanagement() {
        Auftragsmanagement auftragsmanagement = new Auftragsmanagement();
        auftragsmanagement.setAnrede(Anrede.HERR);
        auftragsmanagement.setNachname(ansprechpartnerNachname);
        auftragsmanagement.setTelefonnummer("089 452003461");
        auftragsmanagement.setEmail("validAuftrag@wita.m-net.de");
        return auftragsmanagement;
    }

    private Ansprechpartner createAnsprechpartner() {
        Ansprechpartner ansprechpartner1 = new Ansprechpartner();
        ansprechpartner1.setRolle(AnsprechpartnerRolle.TECHNIK);
        ansprechpartner1.setAnrede(Anrede.FRAU);
        ansprechpartner1.setNachname("Müller");
        ansprechpartner1.setTelefonnummer("089 452003461");
        ansprechpartner1.setEmail("ansprechpartnerValidAuftrag@wita.m-net.de");
        return ansprechpartner1;
    }

    private LeitungsBezeichnung createLeitungsBezeichnung() {
        LeitungsBezeichnung leitungsBezeichnung = new LeitungsBezeichnung();
        leitungsBezeichnung.setOrdnungsNummer("0000000001");
        leitungsBezeichnung.setOnkzKunde("89");
        leitungsBezeichnung.setOnkzKollokation("89");
        leitungsBezeichnung.setLeitungsSchluesselZahl("086");
        return leitungsBezeichnung;
    }

    private StandortKollokation createStandortKollokation() {
        StandortKollokation standortKollokation = new StandortKollokation();
        standortKollokation.setStrassenname("Emmy-Noether-Str.");
        standortKollokation.setHausnummer("2");
        standortKollokation.setPostleitzahl("82000");
        standortKollokation.setOrtsname("München");
        standortKollokation.setAsb("123");
        standortKollokation.setOnkz("12345");
        return standortKollokation;
    }

    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

}
