package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTBMduImportView;
import de.augustakom.hurrican.model.cc.view.FTTBMduPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.model.tools.AuftragFttb_hSuFAsFttbBuilder;
import de.augustakom.hurrican.model.tools.AuftragFtthSuFBuilder;
import de.augustakom.hurrican.model.tools.StandortFttb_hBuilder;
import de.augustakom.hurrican.model.tools.StandortFtthBuilder;
import de.augustakom.hurrican.model.tools.StandortOntOnlyBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

public class FTTXHardwareServiceTest extends AbstractHurricanBaseServiceTest {

    private static final String VALID_HUAWEI_DSLAM_TYPE = "MA5603T";

    private FTTXHardwareService fttxHardwareService;
    private HWService hwService;
    private HVTService hvtService;
    private RangierungsService rangierungsService;

    @BeforeMethod(groups = { "service" })
    public void initService() {
        fttxHardwareService = getCCService(FTTXHardwareService.class);
        hwService = getCCService(HWService.class);
        rangierungsService = getCCService(RangierungsService.class);
        hvtService = getCCService(HVTService.class);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testUpdateMdu() throws Exception {
        HWMduBuilder hwMduBuilder = getBuilder(HWMduBuilder.class).withSerialNo("abc");
        HWMdu hwMdu = hwMduBuilder.get();
        fttxHardwareService.updateMDU(hwMdu.getGeraeteBez(), "xyz", -1L);
        hwMdu = (HWMdu) hwService.findRackById(hwMdu.getId());
        assertEquals(hwMdu.getSerialNo(), "xyz");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testGenerateFTTBMdu() throws Exception {
        final String ortsteil = "OrtsteilExample";
        final String herstellerHuawei = "Huawei";
        final String herstellerAlcatel = "Alcatel-Lucent";
        final String raum = "RaumExample";

        HVTStandortBuilder standortBuilder = getBuilder(HVTStandortBuilder.class);
        HVTStandort hvtStandor = standortBuilder.withHvtGruppeBuilder(getBuilder(HVTGruppeBuilder.class).withOrtsteil(ortsteil)).build();

        HVTTechnikBuilder huaweiTechnikBuilder = getBuilder(HVTTechnikBuilder.class);
        huaweiTechnikBuilder.withId(HVTTechnik.HUAWEI).withHersteller(herstellerHuawei).setPersist(false);

        HVTTechnikBuilder alcatelTechnikBuilder = getBuilder(HVTTechnikBuilder.class);
        alcatelTechnikBuilder.withId(HVTTechnik.ALCATEL).withHersteller(herstellerAlcatel).setPersist(false);

        HWOlt huaweiOlt = getBuilder(HWOltBuilder.class)
                .withHvtStandortBuilder(standortBuilder)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withIpNetzVon("172.31.32.3")
                .build();
        HWOlt alcatelOlt = getBuilder(HWOltBuilder.class)
                .withHvtStandortBuilder(standortBuilder)
                .withHwProducerBuilder(alcatelTechnikBuilder)
                .build();

        // Standort schlaegt fehl
        FTTBMduImportView view1 = new FTTBMduImportView();
        view1.setBezeichnung("MDU-000001");
        view1.setHersteller(herstellerHuawei);
        view1.setSeriennummer("485754438BD0XY01");
        view1.setModellnummer("MA5652G");
        view1.setOlt(huaweiOlt.getGeraeteBez());
        view1.setOltRack("0");
        view1.setOltSlot("1");
        view1.setOltPort("0");
        view1.setGponId("000");
        view1.setCatv("false");
        view1.setStandort("OrtsteilExistiertNicht");
        view1.setRaum(raum);

        // Hersteller schlaegt fehl
        FTTBMduImportView view2 = new FTTBMduImportView();
        view2.setBezeichnung("MDU-000002");
        view2.setHersteller("HerstellerExistiertNicht");
        view2.setSeriennummer("485754438BD0XY02");
        view2.setModellnummer("MA5652G");
        view2.setOlt(huaweiOlt.getGeraeteBez());
        view2.setOltRack("0");
        view2.setOltSlot("1");
        view2.setOltPort("0");
        view2.setGponId("001");
        view2.setCatv("false");
        view2.setStandort(ortsteil);
        view2.setRaum(raum);

        // Olt schlaegt fehl
        FTTBMduImportView view3 = new FTTBMduImportView();
        view3.setBezeichnung("MDU-000003");
        view3.setHersteller(herstellerHuawei);
        view3.setSeriennummer("485754438BD0XY03");
        view3.setModellnummer("MA5652G");
        view3.setOlt("BezeichnungExistiertNicht");
        view3.setOltRack("0");
        view3.setOltSlot("1");
        view3.setOltPort("0");
        view3.setGponId("002");
        view3.setCatv("false");
        view3.setStandort(ortsteil);
        view3.setRaum(raum);

        // MA5652G (Huawei) MDU anlegen
        FTTBMduImportView view4 = new FTTBMduImportView();
        view4.setBezeichnung("MDU-000004");
        view4.setHersteller(herstellerHuawei);
        view4.setSeriennummer("485754438BD0XY04");
        view4.setModellnummer("MA5652G");
        view4.setOlt(huaweiOlt.getGeraeteBez());
        view4.setOltRack("0");
        view4.setOltSlot("1");
        view4.setOltPort("0");
        view4.setGponId("003");
        view4.setCatv("false");
        view4.setStandort(ortsteil);
        view4.setRaum(raum);

        // MA5652G (Huawei) MDU nochmal anlegen - schlaegt fehl
        FTTBMduImportView view5 = new FTTBMduImportView();
        view5.setBezeichnung("MDU-000004");
        view5.setHersteller(herstellerHuawei);
        view5.setSeriennummer("485754438BD0XY04");
        view5.setModellnummer("MA5652G");
        view5.setOlt(huaweiOlt.getGeraeteBez());
        view5.setOltRack("0");
        view5.setOltSlot("1");
        view5.setOltPort("0");
        view5.setGponId("003");
        view5.setCatv("false");
        view5.setStandort(ortsteil);
        view5.setRaum(raum);

        // MA5616 (Huawei) MDU anlegen
        FTTBMduImportView view6 = new FTTBMduImportView();
        view6.setBezeichnung("MDU-000005");
        view6.setHersteller(herstellerHuawei);
        view6.setSeriennummer("485754438BD0XY05");
        view6.setModellnummer("MA5616");
        view6.setOlt(huaweiOlt.getGeraeteBez());
        view6.setOltRack("0");
        view6.setOltSlot("1");
        view6.setOltPort("0");
        view6.setGponId("004");
        view6.setCatv("false");
        view6.setStandort(ortsteil);
        view6.setRaum(raum);

        // MA5616 (Huawei) MDU nochmal anlegen - schlaegt fehl
        FTTBMduImportView view7 = new FTTBMduImportView();
        view7.setBezeichnung("MDU-000005");
        view7.setHersteller(herstellerHuawei);
        view7.setSeriennummer("485754438BD0XY05");
        view7.setModellnummer("MA5616");
        view7.setOlt(huaweiOlt.getGeraeteBez());
        view7.setOltRack("0");
        view7.setOltSlot("1");
        view7.setOltPort("0");
        view7.setGponId("004");
        view7.setCatv("false");
        view7.setStandort(ortsteil);
        view7.setRaum(raum);

        // O-881V-P (Alcatel) MDU anlegen
        FTTBMduImportView view8 = new FTTBMduImportView();
        view8.setBezeichnung("MDU-000006");
        view8.setHersteller(herstellerAlcatel);
        view8.setSeriennummer("485754438BD0XY06");
        view8.setModellnummer("MA5616");
        view8.setOlt(alcatelOlt.getGeraeteBez());
        view8.setOltRack("0");
        view8.setOltSlot("1");
        view8.setOltPort("0");
        view8.setGponId("000");
        view8.setCatv("false");
        view8.setStandort(ortsteil);
        view8.setRaum(raum);

        // O-881V-P (Alcatel) MDU nochmal anlegen - schlaegt fehl
        FTTBMduImportView view9 = new FTTBMduImportView();
        view9.setBezeichnung("MDU-000006");
        view9.setHersteller(herstellerAlcatel);
        view9.setSeriennummer("485754438BD0XY06");
        view9.setModellnummer("MA5616");
        view9.setOlt(alcatelOlt.getGeraeteBez());
        view9.setOltRack("0");
        view9.setOltSlot("1");
        view9.setOltPort("0");
        view9.setGponId("000");
        view9.setCatv("false");
        view9.setStandort(ortsteil);
        view9.setRaum(raum);

        callGenerateFTTBMdu(view1, hvtStandor.getId(), true);
        callGenerateFTTBMdu(view2, hvtStandor.getId(), true);
        callGenerateFTTBMdu(view3, hvtStandor.getId(), true);
        callGenerateFTTBMdu(view4, hvtStandor.getId(), false);
        callGenerateFTTBMdu(view5, hvtStandor.getId(), true);
        callGenerateFTTBMdu(view6, hvtStandor.getId(), false);
        callGenerateFTTBMdu(view7, hvtStandor.getId(), true);
        callGenerateFTTBMdu(view8, hvtStandor.getId(), false);
        callGenerateFTTBMdu(view9, hvtStandor.getId(), true);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testGenerateFTTBMduPorts() throws Exception {
        String typName = "TESTMDU";

        HWMduBuilder mduBuilder = getBuilder(HWMduBuilder.class);
        HWMdu mdu = mduBuilder.build();

        getBuilder(HWBaugruppenTypBuilder.class).withName(typName + "_POTS").withPortCount(1).build();

        getBuilder(HWBaugruppeBuilder.class).withModNumber("0-1").withRackBuilder(mduBuilder).withBaugruppenTypBuilder(
                getBuilder(HWBaugruppenTypBuilder.class).withName(typName).withPortCount(1)).build();

        getBuilder(HWBaugruppeBuilder.class).withModNumber("0-2").withRackBuilder(mduBuilder).withBaugruppenTypBuilder(
                getBuilder(HWBaugruppenTypBuilder.class).withName("TESTMDU2_VDSL2").withPortCount(1)).build();

        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift1").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_MNET).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift2").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_MNET).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift3").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_MNET).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift4").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_MNET).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift5").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_MNET).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift6").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_LZV).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withRangLeiste1("Leiste1").withRangStift1("Stift7").withHwEQN(null)
                .withRangSchnittstelle(RangSchnittstelle.FTTB_LVT).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();
        getBuilder(EquipmentBuilder.class).withHwEQN(null).withHvtStandortBuilder(mduBuilder.getHvtStandortBuilder()).build();

        FTTBMduPortImportView view1 = new FTTBMduPortImportView();
        view1.setBgTyp(typName);
        view1.setModulNummer("0-1");
        view1.setMdu(mdu.getGeraeteBez());
        view1.setSchnittstelle("VDSL2");
        view1.setLeiste("Leiste1");
        view1.setStift("Stift1");
        view1.setPort("1");

        FTTBMduPortImportView view2 = new FTTBMduPortImportView();
        view2.setBgTyp("TESTMDU2");
        view2.setModulNummer("0-2");
        view2.setMdu(mdu.getGeraeteBez());
        view2.setSchnittstelle("VDSL2");
        view2.setLeiste("Leiste1");
        view2.setStift("Stift2");
        view2.setPort("2");

        FTTBMduPortImportView view3 = new FTTBMduPortImportView();
        view3.setBgTyp(typName);
        view3.setModulNummer("0-2");
        view3.setMdu(mdu.getGeraeteBez());
        view3.setSchnittstelle("POTS");
        view3.setLeiste("Leiste1");
        view3.setStift("Stift3");
        view3.setPort("3");

        FTTBMduPortImportView view4 = new FTTBMduPortImportView();
        view4.setBgTyp(typName);
        view4.setModulNummer("0-3");
        view4.setMdu(mdu.getGeraeteBez());
        view4.setSchnittstelle("POTS");
        view4.setLeiste("Leiste1");
        view4.setStift("Stift4");
        view4.setPort("4");

        FTTBMduPortImportView view5 = new FTTBMduPortImportView();
        view5.setBgTyp(typName);
        view5.setModulNummer("0-3");
        view5.setMdu(mdu.getGeraeteBez());
        view5.setSchnittstelle("POTS");
        view5.setLeiste("Leiste1");
        view5.setStift("Stift5");
        view5.setPort("5");

        FTTBMduPortImportView view6 = new FTTBMduPortImportView();
        view6.setBgTyp(typName);
        view6.setModulNummer("0-4");
        view6.setMdu(mdu.getGeraeteBez());
        view6.setSchnittstelle("VDSL2");
        view6.setLeiste("Leiste1");
        view6.setStift("Stift6");
        view6.setPort("6");

        FTTBMduPortImportView view7 = new FTTBMduPortImportView();
        view7.setBgTyp(typName);
        view7.setModulNummer("0-5");
        view7.setMdu(mdu.getGeraeteBez());
        view7.setSchnittstelle("VDSL2");
        view7.setLeiste("Leiste1");
        view7.setStift("Stift7");
        view7.setPort("7");

        FTTBMduPortImportView view8 = new FTTBMduPortImportView();
        view8.setBgTyp(typName);
        view8.setModulNummer("0-5");
        view8.setMdu(mdu.getGeraeteBez());
        view8.setSchnittstelle("VDSL2");
        view8.setLeiste("Leiste1");
        view8.setStift("Stift8");
        view8.setPort("8");

        FTTBMduPortImportView view9 = new FTTBMduPortImportView();
        view9.setBgTyp(typName);
        view9.setModulNummer("0-6");
        view9.setMdu(mdu.getGeraeteBez());
        view9.setSchnittstelle("RF");
        view9.setPort("3-1");

        callGenerateFTTBMduPort(view1, mdu, false); // alles matched
        callGenerateFTTBMduPort(view2, mdu, false); // Baugruppentypname muss zusammengesetzt werden
        callGenerateFTTBMduPort(view3, mdu, true);  // Fehlschlag, da Baugruppe bereits existiert, aber anderen Typ hat
        callGenerateFTTBMduPort(view4, mdu, false); // neue Baugruppe wird angelegt
        callGenerateFTTBMduPort(view5, mdu, true);  // Fehlschlag, da bereits zu viele Ports
        callGenerateFTTBMduPort(view6, mdu, false); // alles matched (mit LZV Schnittstelle)
        callGenerateFTTBMduPort(view7, mdu, false); // alles matched (mit LVT Schnittstelle)
        callGenerateFTTBMduPort(view8, mdu, true);  // alles matched, aber Fehlschlag, da Anzahl Ports auf Baugruppe ueberschritten
        callGenerateFTTBMduPort(view9, mdu, false); // alles matched (fuer RF Port)
    }

    private void callGenerateFTTBMduPort(FTTBMduPortImportView view, HWMdu mdu, boolean shouldFail) throws Exception {
        try {
            fttxHardwareService.generateFTTBMduPort(view, getSessionId());
            if (shouldFail) {
                fail(view.getStift() + ": Port creation succeeded when it should have failed!");
            }

            Equipment equipment = rangierungsService.findEQByLeisteStift(mdu.getHvtIdStandort(), view.getLeiste(), view.getStift());
            if (equipment == null) {
                Equipment example = new Equipment();
                example.setHwEQN(view.getPort());
                example.setHvtIdStandort(mdu.getHvtIdStandort());
                example.setHwSchnittstelle(view.getSchnittstelle());
                List<Equipment> eqs = rangierungsService.findEquipments(example, new String[] { });
                equipment = (CollectionTools.hasExpectedSize(eqs, 1)) ? eqs.get(0) : null;
            }
            assertNotNull(equipment);
            assertEquals(equipment.getHwEQN(), view.getPort());
            assertEquals(equipment.getStatus(), EqStatus.rang);
            assertNotNull(equipment.getHwBaugruppenId());
            assertEquals(equipment.getHwSchnittstelle(), view.getSchnittstelle());

            // Rangierung ermitteln u. pruefen
            Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId(), true);
            assertNotNull(rangierung, "No Rangierung found for equipment!");
        }
        catch (StoreException ex) {
            if (!shouldFail) {
                fail(view.getStift() + ": Port creation should have succeeded, but faild with error: " + ex.getMessage(), ex);
            }
        }
    }

    private void callGenerateFTTBMdu(FTTBMduImportView view, Long hvtStandortId, boolean shouldFail) throws Exception {
        try {
            flushAndClear();

            fttxHardwareService.generateFTTBMdu(view, getSessionId());
            if (shouldFail) {
                fail(view.getBezeichnung() + ": MDU creation succeeded when it should have failed!");
            }

            HWRack rack = hwService.findRackByBezeichnung(view.getBezeichnung());
            assertNotNull(rack, "Consistency check failed. Rack '" + view.getBezeichnung() + "' couldn't be found.");
            HWMdu mdu = (HWMdu) hwService.findRackById(rack.getId());
            assertNotNull(mdu, "Consistency check failed. MDU Rack Id'" + rack.getId().toString() + "' couldn't be found.");
            HVTRaum mduRaum = hvtService.findHVTRaumByName(hvtStandortId, view.getRaum());
            assertNotNull(mduRaum, "Consistency check failed. MDU Raum '" + view.getRaum() + "' couldn't be found.");
        }
        catch (StoreException ex) {
            if (!shouldFail) {
                fail(view.getBezeichnung() + ": MDU creation should have succeeded, but faild with error: " + ex.getMessage(), ex);
            }
        }
    }

    @Test(groups = BaseTest.SERVICE)
    public void testGenerateFTTHOnt() throws Exception {
        final String ortsteil = "OrtsteilExample";
        final String herstellerHuawei = "Huawei";
        final String raum = "RaumExample";
        final String altGslamBez = "OLT-123456";

        HVTStandortBuilder standortBuilder = getBuilder(HVTStandortBuilder.class);
        standortBuilder.withHvtGruppeBuilder(getBuilder(HVTGruppeBuilder.class).withOrtsteil(ortsteil)).build();
        HVTTechnikBuilder huaweiTechnikBuilder = getBuilder(HVTTechnikBuilder.class);
        huaweiTechnikBuilder.withId(HVTTechnik.HUAWEI).withHersteller(herstellerHuawei).setPersist(false);

        HWOlt huaweiOlt = getBuilder(HWOltBuilder.class)
                .withHvtStandortBuilder(standortBuilder)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withIpNetzVon("172.31.32.3")
                .build();

        HWDslam huaweiGslam = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(standortBuilder)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withAltGslamBez(altGslamBez)
                .withDslamType(VALID_HUAWEI_DSLAM_TYPE)
                .build();

        HWDslam huaweiGslamDouble = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(standortBuilder)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withAltGslamBez(huaweiGslam.getGeraeteBez()) // identischer Bezeichner
                .withDslamType(VALID_HUAWEI_DSLAM_TYPE)
                .build();

        // Huawei ONT anlegen: Standort schlaegt fehl
        FTTHOntImportView view1 = new FTTHOntImportView();
        view1.setBezeichnung("ONT-000001");
        view1.setHersteller("Huawei");
        view1.setSeriennummer(null);
        view1.setModellnummer("O-123-T");
        view1.setOlt(huaweiOlt.getGeraeteBez());
        view1.setOltRack(1L);
        view1.setOltSubrack(10L);
        view1.setOltSlot(2L);
        view1.setOltPort(3L);
        view1.setGponId(4L);
        view1.setStandort("OrtsteilExistiertNicht");
        view1.setRaumbezeichung(raum);

        // Huawei ONT anlegen: Hersteller schlaegt fehl
        FTTHOntImportView view2 = new FTTHOntImportView();
        view2.setBezeichnung("ONT-000002");
        view2.setHersteller("huawei");
        view2.setSeriennummer(null);
        view2.setModellnummer("O-123-T");
        view2.setOlt(huaweiOlt.getGeraeteBez());
        view2.setOltRack(1L);
        view2.setOltSubrack(10L);
        view2.setOltSlot(2L);
        view2.setOltPort(3L);
        view2.setGponId(4L);
        view2.setStandort(ortsteil);
        view2.setRaumbezeichung(raum);

        // Olt schlaegt fehl
        FTTHOntImportView view3 = new FTTHOntImportView();
        view3.setBezeichnung("ONT-000003");
        view3.setHersteller("Huawei");
        view3.setSeriennummer(null);
        view3.setModellnummer("O-123-T");
        view3.setOlt("BezeichnungExistiertNicht");
        view3.setOltRack(1L);
        view3.setOltSubrack(10L);
        view3.setOltSlot(2L);
        view3.setOltPort(3L);
        view3.setGponId(4L);
        view3.setStandort(ortsteil);
        view3.setRaumbezeichung(raum);

        // Huawei ONT anlegen
        FTTHOntImportView view4 = new FTTHOntImportView();
        view4.setBezeichnung("ONT-000004");
        view4.setHersteller("Huawei");
        view4.setSeriennummer(null);
        view4.setModellnummer("O-123-T");
        view4.setOlt(huaweiOlt.getGeraeteBez());
        view4.setOltRack(1L);
        view4.setOltSubrack(10L);
        view4.setOltSlot(2L);
        view4.setOltPort(3L);
        view4.setGponId(4L);
        view4.setStandort(ortsteil);
        view4.setRaumbezeichung(raum);

        // Huawei ONT nochmal anlegen - schlaegt fehl
        FTTHOntImportView view5 = new FTTHOntImportView();
        view5.setBezeichnung("ONT-000004");
        view5.setHersteller("Huawei");
        view5.setSeriennummer(null);
        view5.setModellnummer("O-123-T");
        view5.setOlt(huaweiOlt.getGeraeteBez());
        view5.setOltRack(1L);
        view5.setOltSubrack(10L);
        view5.setOltSlot(2L);
        view5.setOltPort(3L);
        view5.setGponId(4L);
        view5.setStandort(ortsteil);
        view5.setRaumbezeichung(raum);

        // Huawei ONT anlegen: GSLAM/OLT Bezeichner doppelt
        FTTHOntImportView view6 = new FTTHOntImportView();
        view6.setBezeichnung("ONT-000005");
        view6.setHersteller("Huawei");
        view6.setSeriennummer(null);
        view6.setModellnummer("O-123-T");
        view6.setOlt(huaweiGslamDouble.getAltGslamBez());
        view6.setOltRack(1L);
        view6.setOltSubrack(10L);
        view6.setOltSlot(2L);
        view6.setOltPort(3L);
        view6.setGponId(4L);
        view6.setStandort(ortsteil);
        view6.setRaumbezeichung(raum);

        // Huawei ONT anlegen: GSLAM
        FTTHOntImportView view7 = new FTTHOntImportView();
        view7.setBezeichnung("ONT-000005");
        view7.setHersteller("Huawei");
        view7.setSeriennummer(null);
        view7.setModellnummer("O-123-T");
        view7.setOlt(huaweiGslam.getAltGslamBez());
        view7.setOltRack(1L);
        view7.setOltSubrack(10L);
        view7.setOltSlot(2L);
        view7.setOltPort(3L);
        view7.setGponId(4L);
        view7.setStandort(ortsteil);
        view7.setRaumbezeichung(raum);

        callGenerateFTTHOnt(view1, true);  // Standort falsch
        callGenerateFTTHOnt(view2, true);  // Hersteller falsch
        callGenerateFTTHOnt(view3, true);  // OLT falsch
        callGenerateFTTHOnt(view4, false); // Anlage einer ONT -> richtig
        callGenerateFTTHOnt(view5, true);  // doppele Anlage einer ONT -> falsch
        callGenerateFTTHOnt(view6, true);  // GSLAM hat alternativen Bezeichner der OLT -> falsch
        callGenerateFTTHOnt(view7, false); // OLT Anlage an einem GSLAM -> richtig
    }

    private void callGenerateFTTHOnt(FTTHOntImportView view, boolean shouldFail) throws Exception {
        try {
            flushAndClear();

            fttxHardwareService.generateFTTHOnt(view, getSessionId());
            if (shouldFail) {
                fail(view.getBezeichnung() + ": ONT creation succeeded when it should have failed!");
            }

            HVTTechnik hvtTechnik = hvtService.findHVTTechnikByHersteller(view.getHersteller());
            assertNotNull(hvtTechnik, "Consistency check failed. hvtTechnik mit Hersteller '" + view.getHersteller() + "' couldn't be found.");

            HWRack oltOrGslam = hwService.findRackByBezeichnung(view.getOlt());
            if (oltOrGslam == null) {
                oltOrGslam = hwService.findGslamByAltBez(view.getOlt());
            }
            assertNotNull(oltOrGslam, "Consistency check failed. Oltrack '" + view.getOlt() + "' couldn't be found.");

            List<HWOnt> ontList = hwService.findHWOltChildByOlt(oltOrGslam.getId(), HWOnt.class);
            assertNotNull(ontList, "Consistency check failed. ONT Rack Id'" + oltOrGslam.getId().toString() + "' couldn't be found.");
            for (HWOnt ont : ontList) {
                assertEquals(ont.getSerialNo(), view.getSeriennummer());
                assertEquals(ont.getOntType(), view.getModellnummer());
                assertEquals(ont.getOltRackId(), oltOrGslam.getId());
                assertEquals(ont.getOltSubrack(), view.getOltSubrack().toString());
                assertEquals(ont.getOltGPONPort(), view.getOltPort().toString());
                assertEquals(ont.getOltGPONId(), view.getGponId().toString());
                assertNull(ont.getFreigabe());
            }

            HVTStandort hvtStandort = hvtService.findHVTStandortByBezeichnung(view.getStandort());
            assertNotNull(hvtStandort, "Consistency check failed. StandortHvt " + view.getStandort() + "' couldn't be found.");

            HVTRaum ontRaum = hvtService.findHVTRaumByName(hvtStandort.getId(), view.getRaumbezeichung());
            assertNotNull(ontRaum, "Consistency check failed. ONT Raum '" + view.getRaumbezeichung() + "' couldn't be found.");

        }
        catch (StoreException ex) {
            if (!shouldFail) {
                fail(view.getBezeichnung() + ": ONT creation should have succeeded, but faild with error: " + ex.getMessage(), ex);
            }
        }
    }

    @Test(groups = BaseTest.SERVICE)
    public void testOntConnectedToAuftragAndDetermineOntForAUFTRAG_GEKUENDIGT() throws Exception {

        final String ontSerialNo = "ABCDEFG0123456789";
        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder()
                .withOntSerialNo(ontSerialNo);
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);

        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(this, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(this, standortFtth);
        auftragFtthSuFBuilder.build(auftragFtthSuF);

        String auftragId = fttxHardwareService.checkHwOltChildForActiveAuftraege(standortFtth.hwOntBuilder.get());
        assertNull(auftragId);

        fttxHardwareService.deleteHwOltChild(standortFtth.hwOntBuilder.get());

        assertEquals(DateTools.formatDate(standortFtth.hwOntBuilder.get().getGueltigBis(), "dd-MM-yyyy"), DateTools.formatDate(new Date(), "dd-MM-yyyy"));
        assertEquals(DateTools.formatDate(standortFtth.ontEthPorts[0].get().getGueltigBis(), "dd-MM-yyyy"), DateTools.formatDate(new Date(), "dd-MM-yyyy"));
        assertEquals(DateTools.formatDate(auftragFtthSuF.rangierungBuilder.get().getGueltigBis(), "dd-MM-yyyy"), DateTools.formatDate(new Date(), "dd-MM-yyyy"));
    }

    @Test(groups = BaseTest.SERVICE)
    public void testDpoConnectedToAuftragAndDetermineDpoForAUFTRAG_GEKUENDIGT() throws Exception {

        final String dpoSerialNo = "ABCDEFG0123456789";
        final StandortFttb_hBuilder standortFttb_hBuilder = new StandortFttb_hBuilder()
                .withDpoSerialNo(dpoSerialNo);
        final AuftragFttb_hSuFAsFttbBuilder auftragFttb_hSuFAsFttbBuilder = new AuftragFttb_hSuFAsFttbBuilder()
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);

        final StandortFttb_hBuilder.StandortFttb_h standortFttb_h = standortFttb_hBuilder.prepare(this, null);
        final AuftragFttb_hSuFAsFttbBuilder.AuftragFttbSuF auftragFttbSuF = auftragFttb_hSuFAsFttbBuilder.prepare(this,
                standortFttb_h);
        auftragFttb_hSuFAsFttbBuilder.build(auftragFttbSuF);

        String auftragId = fttxHardwareService.checkHwOltChildForActiveAuftraege(standortFttb_h.hwOntBuilder.get());
        assertNull(auftragId);

        fttxHardwareService.deleteHwOltChild(standortFttb_h.hwDpoBuilder.get());

        assertEquals(DateTools.formatDate(standortFttb_h.hwDpoBuilder.get().getGueltigBis(), "dd-MM-yyyy"), DateTools.formatDate(new Date(), "dd-MM-yyyy"));
        assertEquals(DateTools.formatDate(auftragFttbSuF.rangierungBuilder.get().getGueltigBis(), "dd-MM-yyyy"), DateTools.formatDate(new Date(), "dd-MM-yyyy"));
        assertEquals(DateTools.formatDate(standortFttb_h.dpoVdslPorts[0].get().getGueltigBis(), "dd-MM-yyyy"), DateTools.formatDate(DateTools.getHurricanEndDate(), "dd-MM-yyyy"));
        assertTrue(standortFttb_h.dpoVdslPorts[0].get().getHwEQN() == null);
        assertTrue(standortFttb_h.dpoVdslPorts[0].get().getHwSchnittstelle() == null);
        assertTrue(standortFttb_h.dpoVdslPorts[0].get().getStatus() == EqStatus.frei);
        assertTrue(standortFttb_h.dpoVdslPorts[0].get().getHwBaugruppenId() == null);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testOntIdInRangierungSet() throws StoreException, FindException {
        final StandortOntOnlyBuilder standortOntOnlyBuilder = new StandortOntOnlyBuilder();
        final StandortOntOnlyBuilder.StandortOntOnly standortOntOnly = standortOntOnlyBuilder.prepare(this, null);
        standortOntOnlyBuilder.build(standortOntOnly);

        FTTHOntPortImportView view = new FTTHOntPortImportView();
        view.setOltChild(standortOntOnly.hwOntBuilder.get().getGeraeteBez());
        view.setPort("1-1");
        view.setSchnittstelle("ETH");
        fttxHardwareService.generateFTTHOntPort(view, getSessionId());
        HWBaugruppe baugruppe = hwService.findBaugruppe(standortOntOnly.hwOntBuilder.get().getId(), null, "0-1");
        assertNotNull(baugruppe);
        List<Equipment> ports = rangierungsService.findEquipments4HWBaugruppe(baugruppe.getId());
        assertNotNull(ports);
        assertEquals(ports.size(), 1);
        Rangierung rangierung = rangierungsService.findRangierung4Equipment(ports.get(0).getId());
        assertNotNull(rangierung);
        assertNotNull(rangierung.getOntId());
    }

}
