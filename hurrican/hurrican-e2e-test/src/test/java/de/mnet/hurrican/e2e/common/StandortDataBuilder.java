/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 13:07:35
 */
package de.mnet.hurrican.e2e.common;

import java.util.*;
import javax.inject.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.UEVTBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

public class StandortDataBuilder {
    private static final Logger LOGGER = Logger.getLogger(StandortDataBuilder.class);

    private static final Long SESSION_ID = Long.valueOf(42);

    public static class StandortData {
        public final HWOlt hwOlt;
        public final HWOltChild oltChild;
        public final HVTGruppe oltChildGruppe;
        public final HVTStandort oltChildStandort;
        public final GeoId geoId;
        public final GeoId2TechLocation geoId2TechLocation;
        public final HVTGruppe betriebsraumGruppe;
        public final HVTGruppe funktionsraumGruppe;

        public StandortData(HVTGruppe oltChildGruppe, HWOltChild oltChild, HWOlt hwOlt, HVTStandort oltChildStandort,
                GeoId geoId,
                GeoId2TechLocation geoId2TechLocation, HVTGruppe betriebsraumGruppe, HVTGruppe funktionsraumGruppe) {
            this.hwOlt = hwOlt;
            this.oltChildGruppe = oltChildGruppe;
            this.oltChild = oltChild;
            this.oltChildStandort = oltChildStandort;
            this.geoId = geoId;
            this.geoId2TechLocation = geoId2TechLocation;
            this.betriebsraumGruppe = betriebsraumGruppe;
            this.funktionsraumGruppe = funktionsraumGruppe;
        }
    }

    private String onkz = "8433";
    private int asb = 1;
    private Long carrierKennungId = CarrierKennung.ID_MNET_MUENCHEN;

    private String plz = "86571";
    private String ort = "Langenmosen";

    private String hvtStrasse = "Amselweg";
    private String hvtHausNr = "1";

    private String kundeStrasse = "Columbusstr.";
    private String kundeHausNr = "8";

    private String ipNetzvon = "172.31.32.3";

    private Long standortTypeRefId = HVTStandort.HVT_STANDORT_TYP_FTTB;
    private String oltChildRackTyp = HWRack.RACK_TYPE_MDU;
    private Long physikTypId = PhysikTyp.PHYSIKTYP_FTTB_VDSL;
    public static final int RANGIERUNGS_COUNT = 4;

    @Inject
    private HVTService hvtService;
    @Inject
    private AvailabilityService availabilityService;
    @Inject
    private RangierungsService rangierungsService;
    @Inject
    private HWService hwService;
    @Inject
    private DSLAMService dslamService;

    private StandortData standortData;

    // -1 = unlimited
    private int hwBaugruppeMaxBandwidth = -1;

    // -1 = zweite Baugruppe existiert nicht
    private int zweiteHwBaugruppeMaxBandwidth = -1;

    private int rangierungsCount = RANGIERUNGS_COUNT;

    public synchronized StandortData getStandortData() throws Exception {
        if (standortData == null) {
            standortData = build();
        }
        return standortData;
    }

    public synchronized StandortDataBuilder reset() {
        standortData = null;
        return this;
    }

    public StandortDataBuilder withStandortTypeRefId(Long standortTypeRefId) {
        this.standortTypeRefId = standortTypeRefId;
        return this;
    }

    public StandortDataBuilder withPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
        return this;
    }

    public StandortDataBuilder withOltChildRackType(String rackType) {
        this.oltChildRackTyp = rackType;
        return this;
    }

    public StandortData build() throws Exception {
        HVTGruppe betriebsraumGruppe = new HVTGruppeBuilder().get();
        betriebsraumGruppe.setOrtsteil("betriebsraum-" + betriebsraumGruppe.getOrtsteil());
        betriebsraumGruppe = hvtService.saveHVTGruppe(betriebsraumGruppe);

        HVTStandort betriebsraumStandort = new HVTStandortBuilder().get();
        betriebsraumStandort.setHvtGruppeId(betriebsraumGruppe.getId());
        betriebsraumStandort = hvtService.saveHVTStandort(betriebsraumStandort);

        HVTGruppe funktionsraumGruppe = new HVTGruppeBuilder().get();
        funktionsraumGruppe.setOrtsteil("funktionsraum-" + funktionsraumGruppe.getOrtsteil());
        funktionsraumGruppe = hvtService.saveHVTGruppe(funktionsraumGruppe);

        HVTStandort funktionsraumStandort = new HVTStandortBuilder().get();
        funktionsraumStandort.setHvtGruppeId(funktionsraumGruppe.getId());
        funktionsraumStandort = hvtService.saveHVTStandort(funktionsraumStandort);

        HVTGruppeBuilder hvtGruppeBuilder = (new HVTGruppeBuilder())
                .withStrasse(hvtStrasse)
                .withHausNr(hvtHausNr)
                .withOnkz(onkz)
                .withPlz(plz)
                .withOrt(ort);
        HVTStandortBuilder hvtStandortBuilder = (new HVTStandortBuilder())
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withCarrierKennungId(carrierKennungId)
                .withAsb(asb)
                .withStandortTypRefId(standortTypeRefId)
                .withActivation();

        // Achtung: Standortadresse fuer StandortA wird aus Taifun ermittelt
        // (bei gewuenschter Aenderung des Kundenstandorts muss also auch das SQL-File angepasst werden!)
        GeoIdBuilder geoIdBuilder = (new GeoIdBuilder()).withZipCode(plz).withCity(ort).withStreet(kundeStrasse)
                .withHouseNum(kundeHausNr);

        GeoId2TechLocationBuilder geoId2TechLocationBuilder = (new GeoId2TechLocationBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withGeoIdBuilder(geoIdBuilder);

        HVTGruppe oltChildHvtGruppe = geoId2TechLocationBuilder
                .getHvtStandortBuilder()
                .getHvtGruppeBuilder()
                .setPersist(false)
                .get();
        oltChildHvtGruppe.setOrtsteil(oltChildRackTyp + "-" + oltChildHvtGruppe.getOrtsteil());
        oltChildHvtGruppe = hvtService.saveHVTGruppe(oltChildHvtGruppe);

        HVTStandort hvtStandort = geoId2TechLocationBuilder.getHvtStandortBuilder().setPersist(false).get();
        hvtStandort.setBetriebsraumId(betriebsraumStandort.getId());
        hvtStandort.setFcRaumId(funktionsraumStandort.getId());
        hvtStandort.setHvtGruppeId(oltChildHvtGruppe.getId());
        hvtStandort = hvtService.saveHVTStandort(hvtStandort);

        HVTTechnik hvtTechnik = new HVTTechnikBuilder().build();
        hvtTechnik.setCpsName("testManufacturer");
        hvtTechnik = hvtService.saveHVTTechnik(hvtTechnik);

        HWBaugruppenTyp hwBaugruppenTyp = new HWBaugruppenTypBuilder().build();
        hwBaugruppenTyp.setHvtTechnik(hvtTechnik);
        if (hwBaugruppeMaxBandwidth != -1) {
            hwBaugruppenTyp.setMaxBandwidth(Bandwidth.create(hwBaugruppeMaxBandwidth));
        }
        hwBaugruppenTyp = hwService.saveHWBaugruppenTyp(hwBaugruppenTyp);
        buildDslamProfile(hwBaugruppenTyp, hwBaugruppeMaxBandwidth);

        HWOlt hwOlt = new HWOltBuilder()
                .withRackTyp(HWRack.RACK_TYPE_OLT)
                .withIpNetzVon(ipNetzvon)
                .withVlanAktivAb(DateTools.minusWorkDays(100)).build();
        hwOlt.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        hwOlt.setHwProducer(hvtTechnik.getId());
        hwOlt = hwService.saveHWRack(hwOlt);

        HWBaugruppe hwBaugruppeOlt = new HWBaugruppeBuilder().withModNumber("modNumber").build();
        hwBaugruppeOlt.setHwBaugruppenTyp(hwBaugruppenTyp);
        hwBaugruppeOlt.setRackId(hwOlt.getId());
        hwBaugruppeOlt = hwService.saveHWBaugruppe(hwBaugruppeOlt);

        UEVT uevt = new UEVTBuilder().withHvtIdStandort(hvtStandort.getId()).withUevt("0000").setPersist(false).get();
        hvtService.saveUEVT(uevt);
        List<UEVT> uevts = hvtService.findUEVTs4HVTStandort(hvtStandort.getId());

        List<Rangierungsmatrix> matrix = rangierungsService.createMatrix(
                SESSION_ID,
                Arrays.asList(uevts.get(0).getId()),
                Arrays.asList(WholesaleProductName.FTTB_50.hurricanProduktId,
                        WholesaleProductName.FTTB_100.hurricanProduktId),
                Arrays.asList(physikTypId)
        );
        if (matrix.isEmpty()) {
            throw new RuntimeException(String.format(
                    "Keine Rangierungsmatrix erzeugt am Standort mit StandortId %d, physikTyp %d",
                    hvtStandort.getId(), physikTypId));
        }

        HWOltChild oltChild = null;
        if (HWRack.RACK_TYPE_MDU.equals(oltChildRackTyp)) {
            oltChild = createHwMdu(hvtStandort, hvtTechnik, hwBaugruppenTyp, hwOlt);
        } if (HWRack.RACK_TYPE_ONT.equals(oltChildRackTyp)) {
            oltChild = createHwOnt(hvtStandort, hvtTechnik, hwOlt);
        }
        GeoId geoId = geoId2TechLocationBuilder.getGeoIdBuilder().setPersist(false).get();
        GeoIdStreetSection geoIdStreetSection = geoId.getStreetSection();
        GeoIdZipCode geoIdZipCode = geoIdStreetSection.getZipCode();
        GeoIdCity geoIdCity = geoIdZipCode.getCity();
        GeoIdCountry geoIdCountry = geoIdCity.getCountry();
        availabilityService.save(geoIdCountry);
        availabilityService.save(geoIdCity);
        availabilityService.save(geoIdZipCode);
        availabilityService.save(geoIdStreetSection);
        availabilityService.save(geoId);

        GeoId2TechLocation geoId2TechLocation = geoId2TechLocationBuilder.setPersist(false).get();
        geoId2TechLocation.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        geoId2TechLocation.setGeoId(geoId.getId());
        // use random sessionId due to
        // DefaultHurricanService.getUserNameAndFirstNameSilent(Long)
        geoId2TechLocation = availabilityService.saveGeoId2TechLocation(geoId2TechLocation, SESSION_ID);
        return new StandortData(oltChildHvtGruppe, oltChild, hwOlt, hvtStandort, geoId, geoId2TechLocation, betriebsraumGruppe,
                funktionsraumGruppe);
    }

    private HWOnt createHwOnt(HVTStandort hvtStandort, HVTTechnik hvtTechnik, HWOlt hwOlt) throws Exception {
        HWOnt hwOnt = new HWOntBuilder().withHWRackOltBuilder(new HWOltBuilder()).build();
        hwOnt.setHvtIdStandort(hvtStandort.getId());
        hwOnt.setOltRackId(hwOlt.getId());
        hwOnt.setOltSlot("01");
        hwOnt.setRackTyp(HWRack.RACK_TYPE_ONT);
        hwOnt.setHwProducer(hvtTechnik.getId());
        hwOnt.setOltGPONId("123");
        hwOnt = hwService.saveHWRack(hwOnt);
        return hwOnt;
    }

    private HWMdu createHwMdu(HVTStandort hvtStandort, HVTTechnik hvtTechnik, HWBaugruppenTyp hwBaugruppenTyp, HWOlt hwOlt) throws StoreException, de.augustakom.common.tools.validation.ValidationException {
        HWMdu hwMdu = new HWMduBuilder().withHWRackOltBuilder(new HWOltBuilder()).build();
        hwMdu.setHvtIdStandort(hvtStandort.getId());
        hwMdu.setOltRackId(hwOlt.getId());
        hwMdu.setOltSlot("01");
        hwMdu.setRackTyp(HWRack.RACK_TYPE_MDU);
        hwMdu.setHwProducer(hvtTechnik.getId());
        hwMdu.setOltGPONId("123");
        hwMdu = hwService.saveHWRack(hwMdu);

        HWBaugruppe hwBaugruppeMdu = new HWBaugruppeBuilder().withModNumber("modNumber").build();
        hwBaugruppeMdu.setHwBaugruppenTyp(hwBaugruppenTyp);
        hwBaugruppeMdu.setRackId(hwMdu.getId());
        hwBaugruppeMdu = hwService.saveHWBaugruppe(hwBaugruppeMdu);

        if (zweiteHwBaugruppeMaxBandwidth != -1) {
            HWMdu hwMdu2 = new HWMduBuilder().withHWRackOltBuilder(new HWOltBuilder()).build();
            hwMdu2.setHvtIdStandort(hvtStandort.getId());
            hwMdu2.setOltRackId(hwOlt.getId());
            hwMdu2.setOltSlot("02");
            hwMdu2.setRackTyp(HWRack.RACK_TYPE_MDU);
            hwMdu2.setHwProducer(hvtTechnik.getId());
            hwMdu2.setOltGPONId("234");
            hwMdu2 = hwService.saveHWRack(hwMdu2);

            HWBaugruppenTyp hwBaugruppenTyp2 = new HWBaugruppenTypBuilder().build();
            hwBaugruppenTyp2.setHvtTechnik(hvtTechnik);
            hwBaugruppenTyp2.setMaxBandwidth(Bandwidth.create(zweiteHwBaugruppeMaxBandwidth));
            hwBaugruppenTyp2 = hwService.saveHWBaugruppenTyp(hwBaugruppenTyp2);
            HWBaugruppe hwBaugruppeMdu2 = new HWBaugruppeBuilder().withModNumber("modNumber").build();
            hwBaugruppeMdu2.setHwBaugruppenTyp(hwBaugruppenTyp2);
            hwBaugruppeMdu2.setRackId(hwMdu2.getId());
            hwBaugruppeMdu2 = hwService.saveHWBaugruppe(hwBaugruppeMdu2);

            buildDslamProfile(hwBaugruppenTyp, zweiteHwBaugruppeMaxBandwidth);
            buildEquipmentsAndRangierungen(hvtStandort, hwBaugruppeMdu2);
        }
        buildEquipmentsAndRangierungen(hvtStandort, hwBaugruppeMdu);
        return hwMdu;
    }

    private void buildDslamProfile(HWBaugruppenTyp hwBaugruppenTyp, int baugruppeMaxBandwidth) {
        int maxBandwith = baugruppeMaxBandwidth == -1 ? 100000 : baugruppeMaxBandwidth;
        DSLAMProfile profil1 = new DSLAMProfileBuilder().withBaugruppenTypId(hwBaugruppenTyp.getId())
                .withBandwidth(maxBandwith, maxBandwith / 5).build();
        dslamService.saveDSLAMProfile(profil1);
        DSLAMProfile profil11 = new DSLAMProfileBuilder().withBaugruppenTypId(hwBaugruppenTyp.getId())
                .withBandwidth(maxBandwith, maxBandwith / 20).build();
        dslamService.saveDSLAMProfile(profil11);
        DSLAMProfile profil2 = new DSLAMProfileBuilder().withBaugruppenTypId(hwBaugruppenTyp.getId())
                .withBandwidth(maxBandwith / 2, maxBandwith / 10).build();
        dslamService.saveDSLAMProfile(profil2);
        DSLAMProfile profil22 = new DSLAMProfileBuilder().withBaugruppenTypId(hwBaugruppenTyp.getId())
                .withBandwidth(maxBandwith / 2, maxBandwith / 20).build();
        dslamService.saveDSLAMProfile(profil22);
        DSLAMProfile profil24 = new DSLAMProfileBuilder().withBaugruppenTypId(hwBaugruppenTyp.getId())
                .withBandwidth(maxBandwith / 4, maxBandwith / 40).build();
        dslamService.saveDSLAMProfile(profil24);
    }

    private void buildEquipmentsAndRangierungen(HVTStandort hvtStandort, HWBaugruppe hwBaugruppe) throws StoreException {
        for (int i = 0; i < rangierungsCount; i++) {
            Equipment equipment = createEquipment(String.format("0-1-%s", i));
            equipment.setHvtIdStandort(hvtStandort.getId());
            equipment.setHwBaugruppenId(hwBaugruppe.getId());
            Equipment savedEquipment = rangierungsService.saveEquipment(equipment);

            Rangierung rangierung = createRangierung(savedEquipment);
            rangierung.setHvtIdStandort(hvtStandort.getId());
            Rangierung saved = rangierungsService.saveRangierung(rangierung, false);
            LOGGER.info(">>>>>>>>>>>>>>>>>> Rangierung: " + saved.getId());
        }
    }

    public StandortDataBuilder rangierungsCount(int rangierungsCount) {
        this.rangierungsCount = rangierungsCount;
        return this;
    }

    private Equipment createEquipment(String hwEqn) {
        // TODO Equipment mit Baugruppe+Baugruppentyp versehen, wenn die Baugruppe
        // zur Unterscheidung der Ports verwendet werden soll (wg. FTTB 50/100 Baugruppen)

        // @formatter:off
        return new EquipmentBuilder()
            .withHwEQN(hwEqn)
            .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2)
            .setPersist(false)
            .build();
        // @formatter:on
    }

    private Rangierung createRangierung(Equipment eqIn) {
        // @formatter:off
        Rangierung rangierung = new RangierungBuilder()
            .withPhysikTypId(physikTypId)
            .setPersist(false)
            .build();
        rangierung.setEqInId(eqIn.getId());
        return rangierung;
        // @formatter:on
    }

    public StandortDataBuilder withHwBaugruppeMaxBandwidth(int maxBandwidth) {
        this.hwBaugruppeMaxBandwidth = maxBandwidth;
        return this;
    }

    public StandortDataBuilder withZweiteHwBaugruppeMaxBandwidth(int maxBandwidth) {
        this.zweiteHwBaugruppeMaxBandwidth = maxBandwidth;
        return this;
    }

}
