/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2015
 */
package de.augustakom.hurrican.model.billing.factory;

import static de.augustakom.hurrican.model.billing.factory.om.AbstractTaifunObjectMother.*;

import java.time.*;
import java.util.*;
import javax.sql.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BAuftragPosBuilder;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Person;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.factory.om.AddressObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.AuftragObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.AuftragPosObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.DeviceFritzBoxObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.DeviceObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.KundeObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.PersonObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.RInfoObjectMother;
import de.augustakom.hurrican.model.billing.factory.om.RufnummerObjectMother;
import de.mnet.common.tools.DateConverterUtils;

// @formatter:off
/**
 * Factory-Klasse, um verschiedene Taifun Daten-Strukturen (z.B. Kunde mit Adresse und Surf&Fon Auftrag) anzulegen. Die
 * generierten Objekte werden ueber {@link GeneratedTaifunData} zurueck gegeben. <br/>
 * ACHTUNG: die generierten Daten
 * werden ueber die Factory noch nicht in der DB persistiert! Dies muss ueber einen 'persist' Aufruf auf {@link
 * GeneratedTaifunData} erfolgen! (Vorteil davon: die Test-Klassen koennen vor dem DB-Insert noch Daten veraendern.)
 */
// @formatter:on
public class TaifunDataFactory {

    private TaifunDataHandler taifunDataHandler;
    private TNB actCarrier = TNB.AKOM;
    private boolean createDeviceFritzBox;
    private boolean sapId = false;
    private boolean createKundenBetreuer = false;
    private String onkz;

    public TaifunDataFactory(DataSource taifunDataSource, DataSource hurricanDataSource) {
        taifunDataHandler = new TaifunDataHandler().configure(taifunDataSource, hurricanDataSource);
    }

    public GeneratedTaifunData createKunde(boolean privat, Long geoId, boolean createKundenBetreuer) {
        Kunde kunde = KundeObjectMother.defaultKunde(privat)
                .withKundeNo(taifunDataHandler.getNextId(Kunde.class))
                .withKundeTyp((privat) ? "PRIVAT" : "GEWERBLICH")
                .build();

        AdresseBuilder adresseBuilder = (privat) ? AddressObjectMother.herr() : AddressObjectMother.firma();
        Adresse adresse = adresseBuilder
                .withAdresseNo(taifunDataHandler.getNextId(Adresse.class))
                .withKundeNo(kunde.getKundeNo())
                .withName(kunde.getName())
                .withVorname((privat) ? kunde.getVorname() : null)
                .build();

        RInfo rInfo = RInfoObjectMother.withKunde(kunde, adresse)
                .withRinfoNo(taifunDataHandler.getNextId(RInfo.class))
                .build();

        if (createKundenBetreuer) {
            Person betreuer = PersonObjectMother.defaultPerson()
                    .withPersonNo(taifunDataHandler.getNextId(Person.class))
                    .build();
            return new GeneratedTaifunData(taifunDataHandler, kunde, betreuer, rInfo, adresse);
        }
        return new GeneratedTaifunData(taifunDataHandler, kunde, null, rInfo, adresse);
    }

    /**
     * Erstellt einen aktiven Surf&Fon Auftrag mit der angegebenen Anzahl an Einzelrufnummern für einen privaten
     * Kunden.
     *
     * @param dnCount
     * @return
     */
    public GeneratedTaifunData surfAndFonWithDns(int dnCount) {
        return surfAndFonWithDns(dnCount, true);
    }

    /**
     * Erstellt einen aktiven Surf&Fon Auftrag mit der angegebenen Anzahl an Einzelrufnummern für einen privaten
     * Kunden.
     *
     * @param dnCount
     * @return
     */
    public GeneratedTaifunData surfAndFonWithDns(int dnCount, boolean privatKunde) {
        return surfAndFonWithDns(dnCount, false, privatKunde, null);
    }

    /**
     * Erstellt n Surf&Fon Auftraege mit der angegebenen Anzahl an Einzelrufnummern. <br/>
     *
     * @param dnCount
     * @param cancelledOrder gibt an, ob der Auftrag und die zugeordneten Rufnummern aktiv (=false) oder gekuendigt
     *                       (=true) sein sollen.
     * @param privatKunde    gibt an, ob es sich um einen privaten oder geschaeftlichen Kunden handelt
     * @param geoId          die GeoId, die fuer die Standort-Adresse verwendet werden soll. (Bei Angabe von NULL wird
     *                       eine Random Id generiert)
     * @return
     */
    public GeneratedTaifunData surfAndFonWithDns(int dnCount, boolean cancelledOrder, boolean privatKunde, Long geoId) {
        GeneratedTaifunData generatedTaifunData = createKunde(privatKunde, geoId, createKundenBetreuer);
        BAuftrag auftrag = createAuftrag(generatedTaifunData, 3401L, cancelledOrder, sapId);

        Device device = createDevice();
        generatedTaifunData.addDevice(device);
        if (createDeviceFritzBox) {
            generatedTaifunData.addDeviceFritzBox(DeviceFritzBoxObjectMother.createFromDevice(device).build());
        }

        List<BAuftragPosBuilder> auftragPosBuilders =
                AuftragPosObjectMother.auftragPositionenSurfAndFon(auftrag, device);
        for (BAuftragPosBuilder auftragPosBuilder : auftragPosBuilders) {
            Long itemNo = taifunDataHandler.getNextId(BAuftragPos.class);
            BAuftragPos auftragPos = auftragPosBuilder
                    .withItemNo(itemNo).withItemNoOrig(itemNo)
                    .build();
            generatedTaifunData.addAuftragPos(auftragPos);
        }

        List<RufnummerBuilder> rufnummerBuilders =
                new RufnummerObjectMother()
                        .withActCarrier(actCarrier)
                        .withOnkz(this.onkz)
                        .singleDns(dnCount);
        for (RufnummerBuilder rufnummerBuilder : rufnummerBuilders) {
            Long dnNo = taifunDataHandler.getNextId(Rufnummer.class);
            Rufnummer rufnummer = rufnummerBuilder.withAuftragNoOrig(auftrag.getAuftragNoOrig())
                    .withDnNo(dnNo).withDnNoOrig(dnNo)
                    .withHistStatus((cancelledOrder) ? BillingConstants.HIST_STATUS_ALT : BillingConstants.HIST_STATUS_AKT)
                    .withGueltigBis((cancelledOrder) ? DateConverterUtils.asDate(LocalDateTime.now().minusDays(10)) : DateTools.getBillingEndDate())
                    .build();
            generatedTaifunData.addDialNumber(rufnummer);
        }

        return generatedTaifunData;
    }

    public GeneratedTaifunData maxiGlasfaserDsl(int dnCount, boolean cancelledOrder, boolean privatKunde, Long geoId) {
        GeneratedTaifunData generatedTaifunData = createKunde(privatKunde, geoId, createKundenBetreuer);
        BAuftrag auftrag = createAuftrag(generatedTaifunData, 2002L, cancelledOrder, sapId);

        Device device = createDevice();
        generatedTaifunData.addDevice(device);
        if (createDeviceFritzBox) {
            generatedTaifunData.addDeviceFritzBox(DeviceFritzBoxObjectMother.createFromDevice(device).build());
        }

        List<BAuftragPosBuilder> auftragPosBuilders =
                AuftragPosObjectMother.auftragPositionenMaxiGlasfaserDsl(auftrag, device);
        for (BAuftragPosBuilder auftragPosBuilder : auftragPosBuilders) {
            Long itemNo = taifunDataHandler.getNextId(BAuftragPos.class);
            BAuftragPos auftragPos = auftragPosBuilder
                    .withItemNo(itemNo).withItemNoOrig(itemNo)
                    .build();
            generatedTaifunData.addAuftragPos(auftragPos);
        }

        List<RufnummerBuilder> rufnummerBuilders =
                new RufnummerObjectMother()
                        .withActCarrier(actCarrier)
                        .withOnkz(this.onkz)
                        .singleDns(dnCount);
        for (RufnummerBuilder rufnummerBuilder : rufnummerBuilders) {
            Long dnNo = taifunDataHandler.getNextId(Rufnummer.class);
            Rufnummer rufnummer = rufnummerBuilder.withAuftragNoOrig(auftrag.getAuftragNoOrig())
                    .withDnNo(dnNo).withDnNoOrig(dnNo)
                    .withHistStatus((cancelledOrder) ? BillingConstants.HIST_STATUS_ALT : BillingConstants.HIST_STATUS_AKT)
                    .withGueltigBis((cancelledOrder) ? DateConverterUtils.asDate(LocalDateTime.now().minusDays(10)) : DateTools.getBillingEndDate())
                    .build();
            generatedTaifunData.addDialNumber(rufnummer);
        }

        return generatedTaifunData;
    }

    /**
     * Generiert einen neuen Kunden mit einem PremiumCall Auftrag und einer Block-Rufnummer.
     *
     * @param blockSize
     * @return
     */
    public GeneratedTaifunData premiumCallWithBlockDn(Long blockSize) {
        GeneratedTaifunData generatedTaifunData = createKunde(false, null, createKundenBetreuer);
        return premiumCallWithBlockDn(generatedTaifunData.getKunde(), generatedTaifunData.getAddress(),
                generatedTaifunData.getRInfo(), blockSize, null, null, true);
    }

    public GeneratedTaifunData premiumCallWithBlockDns(boolean privatKunde, Long... blockSizes) {
        GeneratedTaifunData generatedTaifunData = createKunde(privatKunde, null, createKundenBetreuer);
        return premiumCallWithBlockDns(generatedTaifunData.getKunde(), generatedTaifunData.getAddress(),
                generatedTaifunData.getRInfo(), null, null, true, blockSizes);
    }

    /**
     * Generiert einen PremiumCall Auftrag mit einer Block-Rufnummer zu dem angegebenen Kunden.
     *
     * @param kunde
     * @param adresse
     * @param rinfo
     * @param blockSize
     * @param onkz
     * @param dnBase
     * @param billingRelevant
     * @return
     */
    public GeneratedTaifunData premiumCallWithBlockDn(Kunde kunde, Adresse adresse, RInfo rinfo, Long blockSize,
            String onkz, String dnBase, boolean billingRelevant) {
        return premiumCallWithBlockDns(kunde, adresse, rinfo, onkz, dnBase, billingRelevant, blockSize);
    }

    public GeneratedTaifunData premiumCallWithBlockDns(Kunde kunde, Adresse adresse, RInfo rinfo,
            String onkz, String dnBase, boolean billingRelevant, Long... blockSizes) {
        GeneratedTaifunData generatedTaifunData = new GeneratedTaifunData(taifunDataHandler, kunde, null, rinfo, adresse);
        BAuftrag auftrag = createAuftrag(generatedTaifunData, 2001L, false, sapId);

        List<BAuftragPosBuilder> auftragPosBuilders =
                AuftragPosObjectMother.auftragPositionenPremiumCall(auftrag);
        for (BAuftragPosBuilder auftragPosBuilder : auftragPosBuilders) {
            Long itemNo = taifunDataHandler.getNextId(BAuftragPos.class);
            BAuftragPos auftragPos = auftragPosBuilder
                    .withItemNo(itemNo).withItemNoOrig(itemNo)
                    .build();
            generatedTaifunData.addAuftragPos(auftragPos);
        }

        List<RufnummerBuilder> rufnummerBuilders =
                new RufnummerObjectMother()
                        .withActCarrier(actCarrier)
                        .withOnkz(this.onkz)
                        .blockDns(onkz, dnBase, blockSizes);
        for (RufnummerBuilder rufnummerBuilder : rufnummerBuilders) {
            Long dnNo = taifunDataHandler.getNextId(Rufnummer.class);
            Rufnummer rufnummer = rufnummerBuilder.withAuftragNoOrig(auftrag.getAuftragNoOrig())
                    .withDnNo(dnNo).withDnNoOrig(dnNo)
                    .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                    .withGueltigBis(DateTools.getBillingEndDate())
                    .withNonBillable(!billingRelevant)
                    .build();
            generatedTaifunData.addDialNumber(rufnummer);
        }

        return generatedTaifunData;
    }

    private BAuftrag createAuftrag(GeneratedTaifunData generatedTaifunData, Long oeNoOrig, boolean cancelled, boolean sapId) {
        Long auftragNo = taifunDataHandler.getNextId(BAuftrag.class);
        BAuftrag auftrag = AuftragObjectMother
                .withKunde(generatedTaifunData.getKunde(), generatedTaifunData.getAddress())
                .withRechInfoNoOrig(generatedTaifunData.getRInfo().getRinfoNo())
                .withAuftragNo(auftragNo)
                .withAuftragNoOrig(auftragNo)
                .withOeNoOrig(oeNoOrig)
                .withHistStatus((cancelled) ? BillingConstants.HIST_STATUS_ALT : BillingConstants.HIST_STATUS_AKT)
                .withGueltigBis((cancelled) ? DateConverterUtils.asDate(LocalDateTime.now().minusDays(10)) : null)
                .withKuendigungsdatum((cancelled) ? DateConverterUtils.asDate(LocalDateTime.now().minusDays(10)) : null)
                .withSAPId(sapId ? "SAPID" + randomString(15) : null)
                .build();

        generatedTaifunData.setBillingAuftrag(auftrag);

        return auftrag;
    }

    public Device createDevice() {
        return DeviceObjectMother.createDefault()
                .withDevNo(taifunDataHandler.getNextId(Device.class))
                .build();
    }

    public TaifunDataFactory withActCarrier(TNB actCarrier) {
        this.actCarrier = actCarrier;
        return this;
    }

    public TaifunDataFactory withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    /**
     * Creates an additional FritzBox device in the billing order, see {@link #createDeviceFritzBox}.
     */
    public TaifunDataFactory withCreateDeviceFritzBox() {
        this.createDeviceFritzBox = true;
        return this;
    }

    /**
     * Creates a random SAP-ID for the {@link BAuftrag#sapId}.
     */
    public TaifunDataFactory withSAPId() {
        this.sapId = true;
        return this;
    }

    /**
     * Creates an additional {@link Person} as Kundenbetreuer to the assigend {@link Kunde}.
     */
    public TaifunDataFactory withCreateKundenBetreuer() {
        this.createKundenBetreuer = true;
        return this;
    }

    public TaifunDataFactory updateAddress(GeneratedTaifunData taifunData, Long geoId) {
        taifunData.getAddress().setGeoId(geoId);
        taifunDataHandler.update(taifunData.getAddress());
        return this;
    }
}
