/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnlageType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnsprechpartnerMitEmailType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnsprechpartnerMitRolleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.BereitstellungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.DokumenttypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.GebaeudeteilType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.GeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.OnkzAnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.OrtType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.SchaltungKVZTALType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.SchaltungKupferType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StandortAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StandortAnbieterWechselErweitertType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StandortType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StrasseType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.UebertragungsverfahrenType;
import de.mnet.wita.message.StandortWithPerson;
import de.mnet.wita.message.auftrag.Ansprechpartner;
import de.mnet.wita.message.auftrag.Auftragsmanagement;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.Personenname;

/**
 * Generator to create the &lt;geschaeftsfallProdukt&gt; element.
 */
@SuppressWarnings("Duplicates")
public abstract class GeschaeftsfallMarshallerV2<T extends GeschaeftsfallType> extends AbstractBaseMarshallerV2 {

    public abstract de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall generate(Geschaeftsfall input);

    protected String vertragsNummer(Geschaeftsfall input) {
        return StringUtils.trimToNull(input.getVertragsNummer());
    }

    protected AnsprechpartnerType ansprechPartner(Geschaeftsfall input) {
        AnsprechpartnerType ansprechPartner = new AnsprechpartnerType();
        ansprechPartner.setAuftragsmanagement(auftragsManagement(input));
        ansprechPartner.getAnsprechpartner().addAll(ansprechpartnerMitRolle(input));
        return ansprechPartner;
    }

    private AnsprechpartnerMitEmailType auftragsManagement(Geschaeftsfall geschaeftsfall) {
        Auftragsmanagement input = geschaeftsfall.getGfAnsprechpartner().getAuftragsmanagement();
        AnsprechpartnerMitEmailType auftragsManagement = OBJECT_FACTORY.createAnsprechpartnerMitEmailType();

        auftragsManagement.setAnrede(AnredeConverterV2.toWita(input.getAnrede(), false));
        auftragsManagement.setVorname(input.getVorname());
        auftragsManagement.setNachname(input.getNachname());
        auftragsManagement.setTelefonnummer(input.getTelefonnummer());
        auftragsManagement.setEmailadresse(input.getEmail());

        return auftragsManagement;
    }

    private List<AnsprechpartnerMitRolleType> ansprechpartnerMitRolle(Geschaeftsfall geschaeftsfall) {
        // MnetWitaFormat supports just one further Ansprechparter currently
        Ansprechpartner input = geschaeftsfall.getGfAnsprechpartner().getAnsprechpartner();
        if (input == null) {
            return new ArrayList<>();
        }
        AnsprechpartnerMitRolleType ansprechpartnerMitRolle = OBJECT_FACTORY.createAnsprechpartnerMitRolleType();

        ansprechpartnerMitRolle.setRolle(MwfToWitaConverterV2.convertAnsprechpartnerRolle(input.getRolle()));
        ansprechpartnerMitRolle.setAnrede(AnredeConverterV2.toWita(input.getAnrede(), false));
        ansprechpartnerMitRolle.setVorname(input.getVorname());
        ansprechpartnerMitRolle.setNachname(input.getNachname());
        ansprechpartnerMitRolle.setTelefonnummer(input.getTelefonnummer());
        ansprechpartnerMitRolle.setEmailadresse(input.getEmail());

        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(ansprechpartnerMitRolle);
    }

    BereitstellungTermineType terminNeu(Geschaeftsfall geschaeftsfall) {
        BereitstellungTermineType termin = OBJECT_FACTORY.createBereitstellungTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    protected KundenwunschterminType createKundenwunschtermin(Geschaeftsfall geschaeftsfall) {
        KundenwunschterminType kundenwunschtermin = OBJECT_FACTORY.createKundenwunschterminType();
        kundenwunschtermin.setDatum(DateConverterUtils.toXmlGregorianCalendar(geschaeftsfall.getKundenwunschtermin()
                .getDatum()));

        Kundenwunschtermin.Zeitfenster zeitfenster = geschaeftsfall.getKundenwunschtermin().getZeitfenster();
        if (zeitfenster != null) {
            kundenwunschtermin.setZeitfenster(zeitfenster.witaZeitfenster);
        }
        return kundenwunschtermin;
    }

    protected ProduktType produkt(Auftragsposition auftragsposition) {
        ProduktType result = OBJECT_FACTORY.createProduktType();
        switch (auftragsposition.getProdukt()) {
            case TAL:
                result.setKategorie("TAL");
                break;
            default:
                throw new RuntimeException("Unknown value for field produkt or not supported yet");
        }
        result.setBezeichner(auftragsposition.getProduktBezeichner().getProduktName());
        return result;
    }

    AnsprechpartnerBaseType ansprechPartnerMontage(GeschaeftsfallProdukt input) {
        if ((input.getMontageleistung() != null) && (input.getMontageleistung().getPersonenname() != null)) {
            Personenname person = input.getMontageleistung().getPersonenname();
            AnsprechpartnerBaseType ansprechpartner = OBJECT_FACTORY.createAnsprechpartnerBaseType();
            ansprechpartner.setAnrede(AnredeConverterV2.toWita(person.getAnrede(), false));
            ansprechpartner.setNachname(person.getNachname());
            ansprechpartner.setVorname(person.getVorname());
            ansprechpartner.setEmailadresse(input.getMontageleistung().getEmailadresse());
            ansprechpartner.setTelefonnummer(input.getMontageleistung().getTelefonnummer());
            return ansprechpartner;
        }
        else {
            return null;
        }
    }

    String montageHinweis(GeschaeftsfallProdukt input) {
        if ((input.getMontageleistung() != null)) {
            return input.getMontageleistung().getMontagehinweis();
        }
        else {
            return null;
        }
    }

    String montageReservierungsID(GeschaeftsfallProdukt input) {
        if (input.getMontageleistung() != null) {
            return input.getMontageleistung().getTerminReservierungsId();
        }
        else {
            return null;
        }
    }


    protected UebertragungsverfahrenType getUebertragungsverfahren(GeschaeftsfallProdukt input) {
        if (input.getSchaltangaben() != null) {

            if (!CollectionUtils.isEmpty(input.getSchaltangaben().getSchaltungKupfer())) {
                SchaltungKupfer schaltung = input.getSchaltangaben().getSchaltungKupfer().iterator().next();
                if (schaltung.getUebertragungsverfahren() != null) {
                    return UebertragungsverfahrenType.fromValue(schaltung.getUebertragungsverfahren().toString());
                }
            }
            else if (!CollectionUtils.isEmpty(input.getSchaltangaben().getSchaltungKvzTal())) {
                SchaltungKvzTal kvz = input.getSchaltangaben().getSchaltungKvzTal().iterator().next();
                if (kvz.getUebertragungsverfahren() != null) {
                    return UebertragungsverfahrenType.fromValue(kvz.getUebertragungsverfahren().toString());
                }
            }
        }
        return null;
    }

    protected StandortAType standortKunde(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        StandortKunde input = geschaeftsfallProdukt.getStandortKunde();
        StandortAType standort = null;

        if (input != null) {
            standort = OBJECT_FACTORY.createStandortAType();
            convertStandortKunde(input, standort);
            standort.setLageTAEONT(input.getLageTAEDose());
        }
        return standort;
    }

    StandortAnbieterWechselErweitertType standortKundeErweitert(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        StandortAnbieterWechselErweitertType standort = null;
        StandortKunde input = geschaeftsfallProdukt.getStandortKunde();
        if (input != null) {
            standort = OBJECT_FACTORY.createStandortAnbieterWechselErweitertType();
            StandortAType standortA = OBJECT_FACTORY.createStandortAType();
            convertStandortKunde(input, standortA);
            standort.setFirma(standortA.getFirma());
            standort.setGebaeudeteil(standortA.getGebaeudeteil());
            standort.setPerson(standortA.getPerson());
            standort.setLageTAEONT(input.getLageTAEDose());
        }
        return standort;
    }

    private StandortType convertStandortKunde(StandortKunde input, StandortType standort) {
        Kundenname kundenname = input.getKundenname();
        if (kundenname instanceof Firmenname) {
            standort.setFirma(StandortPersonConverterV2.firma((Firmenname) kundenname));
        }
        else {
            standort.setPerson(StandortPersonConverterV2.person((Personenname) kundenname));
        }

        standort.setStrasse(strasse(input));
        standort.setPostleitzahl(input.getPostleitzahl());
        standort.setLand(input.getLand());

        OrtType ort = OBJECT_FACTORY.createOrtType();
        ort.setOrtsname(input.getOrtsname());
        ort.setOrtsteil(input.getOrtsteil());
        standort.setOrt(ort);

        if (StringUtils.isNotBlank(input.getGebaeudeteilName())) {
            GebaeudeteilType gebaeude = OBJECT_FACTORY.createGebaeudeteilType();
            gebaeude.setGebaeudeteilName(input.getGebaeudeteilName());
            gebaeude.setGebaeudeteilZusatz(input.getGebaeudeteilZusatz());
            standort.setGebaeudeteil(gebaeude);
        }
        return standort;
    }

    private StrasseType strasse(StandortWithPerson input) {
        StrasseType strasse = OBJECT_FACTORY.createStrasseType();
        strasse.setStrassenname(input.getStrassenname());
        strasse.setHausnummer(input.getHausnummerTrimmed());
        strasse.setHausnummernZusatz(input.getHausnummernZusatz());
        return strasse;
    }

    protected StandortBType standortKollokation(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        StandortBType standort = null;
        StandortKollokation input = geschaeftsfallProdukt.getStandortKollokation();

        if (input != null) {
            standort = OBJECT_FACTORY.createStandortBType();

            StrasseType strasse = OBJECT_FACTORY.createStrasseType();
            strasse.setStrassenname(input.getStrassenname());
            strasse.setHausnummer(input.getHausnummerTrimmed());
            strasse.setHausnummernZusatz(input.getHausnummernZusatz());
            standort.setStrasse(strasse);
            standort.setPostleitzahl(input.getPostleitzahl());

            OnkzAnschlussType onkzAsb = OBJECT_FACTORY.createOnkzAnschlussType();
            onkzAsb.setAnschlussbereich(input.getAsb());
            onkzAsb.setONKZ(input.getOnkz());
            standort.setLageKollokationsraum(onkzAsb);

            OrtType ort = OBJECT_FACTORY.createOrtType();
            ort.setOrtsname(input.getOrtsname());
            ort.setOrtsteil(input.getOrtsteil());
            standort.setOrt(ort);
            standort.setLand(input.getLand());
        }

        return standort;
    }

    protected SchaltangabenType schaltAngaben(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        SchaltangabenType schaltangabenType = null;
        Schaltangaben schaltangaben = geschaeftsfallProdukt.getSchaltangaben();
        if (schaltangaben != null) {
            schaltangabenType = OBJECT_FACTORY.createSchaltangabenType();
            List<SchaltungKupfer> kupferList = schaltangaben.getSchaltungKupfer();
            if (kupferList != null) {
                for (SchaltungKupfer schaltungKupfer : kupferList) {
                    SchaltungKupferType schaltungKupferType = OBJECT_FACTORY.createSchaltungKupferType();
                    schaltungKupferType.setUEVT(schaltungKupfer.getUEVT());
                    schaltungKupferType.setEVS(StringUtils.leftPad(schaltungKupfer.getEVS(), 2, "0"));
                    schaltungKupferType.setDoppelader(StringUtils.leftPad(schaltungKupfer.getDoppelader(), 2, "0"));
                    SchaltangabenType.Schaltung schaltung = OBJECT_FACTORY.createSchaltangabenTypeSchaltung();
                    schaltung.setKupfer(schaltungKupferType);
                    schaltangabenType.getSchaltung().add(schaltung);
                }
            }
            List<SchaltungKvzTal> kvzList = schaltangaben.getSchaltungKvzTal();
            if (kvzList != null) {
                for (SchaltungKvzTal schaltungKVZTAL : kvzList) {
                    SchaltungKVZTALType schaltungKVZTALType = OBJECT_FACTORY.createSchaltungKVZTALType();
                    schaltungKVZTALType.setKVZNr(schaltungKVZTAL.getKvz());
                    schaltungKVZTALType.setKVZSchaltnummer(schaltungKVZTAL.getKvzSchaltnummer());
                    SchaltangabenType.Schaltung schaltung = OBJECT_FACTORY.createSchaltangabenTypeSchaltung();
                    schaltung.setKvzTal(schaltungKVZTALType);
                    schaltangabenType.getSchaltung().add(schaltung);
                }
            }

            if (schaltangabenType.getSchaltung() == null) {
                throw new RuntimeException("Unknown value for field schaltung or not supported yet");
            }
        }
        return schaltangabenType;
    }

    protected LeitungsbezeichnungType leitungsbezeichnung2(Auftragsposition auftragsposition) {
        GeschaeftsfallProdukt geschaeftsfallProdukt = auftragsposition.getGeschaeftsfallProdukt();

        LeitungsBezeichnung lbz = geschaeftsfallProdukt.getLeitungsBezeichnung();
        if (lbz == null) {
            return null;
        }
        LeitungsbezeichnungType leitungsbezeichnungType = new LeitungsbezeichnungType();
        leitungsbezeichnungType.setLeitungsschluesselzahl(lbz.getLeitungsSchluesselZahl());
        leitungsbezeichnungType.setOnkzA(lbz.getOnkzKunde());
        leitungsbezeichnungType.setOnkzB(lbz.getOnkzKollokation());
        leitungsbezeichnungType.setOrdnungsnummer(lbz.getOrdnungsNummer());
        return leitungsbezeichnungType;
    }

    //    protected RufnummernportierungType portierung(GeschaeftsfallProdukt input) {
    //        return witaRufnummernPortierungMarshaller.generate(input.getRufnummernPortierung());
    //    }

    AnlageType kuendigungsOrLageplanAnlage(Anlage input) {
        AnlageType anlage = new AnlageType();
        anlage.setBeschreibung(input.getBeschreibung());
        anlage.setDateiname(input.getDateiname());
        anlage.setDateityp(dateiTyp(input.getDateityp()));
        anlage.setInhalt(input.getInhalt());
        return anlage;
    }

    protected AnlageMitTypType sonstigeAnlage(Anlage input) {
        AnlageMitTypType anlage = new AnlageMitTypType();
        anlage.setAnlagentyp(anlagenTyp(input.getAnlagentyp()));
        anlage.setBeschreibung(input.getBeschreibung());
        anlage.setDateiname(input.getDateiname());
        anlage.setDateityp(dateiTyp(input.getDateityp()));
        anlage.setInhalt(input.getInhalt());
        return anlage;
    }

    private AnlagentypType anlagenTyp(Anlagentyp input) {
        AnlagentypType result = AnlagentypType.fromValue(input.value);
        if (result == null) {
            throw new IllegalArgumentException("Cannot match anlagentyp " + input);
        }
        return result;
    }

    private DokumenttypType dateiTyp(Dateityp input) {
        DokumenttypType result = DokumenttypType.fromValue(input.mimeTyp);
        if (result == null) {
            throw new IllegalArgumentException("Cannot match anlagentyp " + input);
        }
        return result;
    }

}
