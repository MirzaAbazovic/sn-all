/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.mnet.hurrican.acceptance.builder;

import com.consol.citrus.context.TestContext;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2Endstelle;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdStreetSectionBuilder;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class TvTestBuilder {

    private CCAuftragDAO ccAuftragDAO;
    private Long prodId;
    private Long standortTypRefId;
    private boolean withRangierung = false;

    public TvTestBuilder configure(CCAuftragDAO ccAuftragDAO) {
        this.ccAuftragDAO = ccAuftragDAO;
        return this;
    }

    public void bundleAuftraege(TestContext testContext, AuftragDaten... auftraege) {
        Integer buendelNummer = EntityBuilder.randomInt(99999999, 999999999);
        for (AuftragDaten auftragDaten : auftraege) {
            auftragDaten.setBuendelNr(buendelNummer);
            auftragDaten.setBuendelNrHerkunft(AuftragDaten.BUENDEL_HERKUNFT_HURRICAN);
            ccAuftragDAO.store(auftragDaten);
        }
        testContext.setVariable(VariableNames.BUENDEL_NUMMER, buendelNummer.toString());
    }

    public AuftragDaten buildTvAuftrag(TestContext testContext) {
        GeneratedTaifunData generatedTaifunData = testContext.getApplicationContext().getBean(TaifunDataFactory.class)
                .surfAndFonWithDns(1).persist();

        AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withKundeNo(generatedTaifunData.getKunde().getKundeNo())
                .setPersist(false);
        Auftrag auftrag = ccAuftragDAO.store(auftragBuilder.build());

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(prodId)
                .withAuftragNoOrig(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig())
                .setPersist(false)
                .build();
        auftragDaten.setAuftragId(auftrag.getAuftragId());
        ccAuftragDAO.store(auftragDaten);

        AuftragTechnik2Endstelle at2Es = new AuftragTechnik2EndstelleBuilder().setPersist(false).build();
        ccAuftragDAO.store(at2Es);

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .setPersist(false).build();
        auftragTechnik.setAuftragId(auftrag.getAuftragId());
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        ccAuftragDAO.store(auftragTechnik);

        HVTGruppe hvtGruppe = new HVTGruppeBuilder().setPersist(false).build();
        ccAuftragDAO.store(hvtGruppe);

        testContext.setVariable(VariableNames.ORTSTEIL, hvtGruppe.getOrtsteil());

        HVTStandort hvtStandort = new HVTStandortBuilder()
                .withStandortTypRefId(standortTypRefId)
                .setPersist(false).build();
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        ccAuftragDAO.store(hvtStandort);

        CCAddress address = new CCAddressBuilder()
                .setPersist(false).build();
        address.setHandy("+49 176 123456");
        address.setTelefon("+49 89 555222");
        ccAuftragDAO.store(address);

        Ansprechpartner ansprechpartner = new AnsprechpartnerBuilder()
                .withType(Ansprechpartner.Typ.ENDSTELLE_B)
                .setPersist(false).build();
        ansprechpartner.setAddress(address);
        ansprechpartner.setAuftragId(auftrag.getId());
        ccAuftragDAO.store(ansprechpartner);

        Pair<Rangierung, Rangierung> createdRangierungen = null;
        if(withRangierung) {
            createdRangierungen = buildHardware(standortTypRefId, hvtStandort, ccAuftragDAO);
        }

        GeoIdStreetSection streetSection = new GeoIdStreetSectionBuilder().setPersist(false).build();
        GeoIdZipCode zipCode = streetSection.getZipCode();
        GeoIdCity city = zipCode.getCity();
        GeoIdCountry country = city.getCountry();

        ccAuftragDAO.store(country);
        ccAuftragDAO.store(city);
        ccAuftragDAO.store(zipCode);
        streetSection = ccAuftragDAO.store(streetSection);

        GeoId geoId = new GeoIdBuilder().setPersist(false).build();
        geoId.setStreetSection(streetSection);

        ccAuftragDAO.store(geoId);
        testContext.setVariable(VariableNames.GEO_ID, geoId.getId().toString());

        Endstelle endstelle = new EndstelleBuilder()
                .setPersist(false).build();
        endstelle.setEndstelleGruppeId(at2Es.getId());
        endstelle.setHvtIdStandort(hvtStandort.getId());
        endstelle.setAddressId(address.getId());
        endstelle.setGeoId(geoId.getId());
        if (createdRangierungen != null && createdRangierungen.getFirst() != null) {
            endstelle.setRangierId(createdRangierungen.getFirst().getId());
        }
        if (createdRangierungen != null && createdRangierungen.getSecond() != null) {
            endstelle.setRangierIdAdditional(createdRangierungen.getSecond().getId());
        }
        ccAuftragDAO.store(endstelle);

        return auftragDaten;
    }

    private Pair<Rangierung, Rangierung> buildHardware(Long standortTypRefId, HVTStandort hvtStandort, CCAuftragDAO ccAuftragDAO) {
        HVTTechnik hvtTechnik = new HVTTechnikBuilder().withHersteller("hersteller")
                .setPersist(false).build();
        ccAuftragDAO.store(hvtTechnik);

        HWBaugruppenTypBuilder hwBgTypBuilder = new HWBaugruppenTypBuilder().setPersist(false);
        ccAuftragDAO.store(hwBgTypBuilder.get());

        boolean createEqOut = false;

        HWRack rack;
        Long physikTypId;
        if (HVTStandort.HVT_STANDORT_TYP_FTTH.equals(standortTypRefId)) {
            physikTypId = PhysikTyp.PHYSIKTYP_FTTH;

            rack = new HWOntBuilder().withSerialNo("serial-no").setPersist(false).build();
            rack.setRackTyp(HWRack.RACK_TYPE_ONT);
        }
        else if (HVTStandort.HVT_STANDORT_TYP_FTTB.equals(standortTypRefId)) {
            physikTypId = PhysikTyp.PHYSIKTYP_FTTB_VDSL;

            HWOlt olt = new HWOltBuilder().setPersist(false).build();
            olt.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            olt.setRackTyp(HWRack.RACK_TYPE_OLT);
            olt.setHwProducer(hvtTechnik.getId());
            ccAuftragDAO.store(olt);

            rack = new HWMduBuilder().withSerialNo("serial-no").setPersist(false).build();
            rack.setRackTyp(HWRack.RACK_TYPE_MDU);
            ((HWMdu) rack).setOltRackId(olt.getId());
        }
        else {
            physikTypId = PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI;

            rack = new HWDslamBuilder().withIpAddress("0.0.0.0").setPersist(false).build();
            rack.setRackTyp(HWRack.RACK_TYPE_DSLAM);
            createEqOut = true;
        }
        rack.setHwProducer(hvtTechnik.getId());
        rack.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        ccAuftragDAO.store(rack);

        HWBaugruppe hwBaugruppe = new HWBaugruppeBuilder()
                .withBaugruppenTypBuilder(hwBgTypBuilder)
                .setPersist(false).build();
        hwBaugruppe.setRackId(rack.getId());
        ccAuftragDAO.store(hwBaugruppe);

        Equipment eqIn = new EquipmentBuilder()
                .withRangLeiste1("L1").withRangStift1("R1")
                .setPersist(false).build();
        eqIn.setHwBaugruppenId(hwBaugruppe.getId());
        eqIn.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        ccAuftragDAO.store(eqIn);

        Equipment eqOut = null;
        if (createEqOut) {
            eqOut = new EquipmentBuilder()
                    .withRangLeiste1("0204")
                    .withRangLeiste1("01").withRangStift1("56")
                    .withCarrier("DTAG")
                    .withUETV(Uebertragungsverfahren.H13)
                    .setPersist(false).build();
            if (HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ.equals(standortTypRefId)) {
                eqOut.setKvzDoppelader("0056");
                eqOut.setKvzNummer("A023");
            }
            eqOut.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            ccAuftragDAO.store(eqOut);
        }

        Rangierung rangierung = new RangierungBuilder().setPersist(false).build();
        rangierung.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        rangierung.setPhysikTypId(physikTypId);
        rangierung.setEqInId(eqIn.getId());
        rangierung.setEqOutId((eqOut != null) ? eqOut.getId() : null);
        ccAuftragDAO.store(rangierung);

        return Pair.create(rangierung, null);
    }

    public TvTestBuilder withProdId(Long prodId) {
        this.prodId = prodId;
        return this;
    }

    public TvTestBuilder withStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }

    public TvTestBuilder withRangierung(boolean rangierung) {
        this.withRangierung = rangierung;
        return this;
    }

}
