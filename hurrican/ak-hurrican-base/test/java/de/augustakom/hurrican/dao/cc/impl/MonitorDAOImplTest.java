package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.RsmRangCountBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.model.cc.view.HVTBelegungView;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class MonitorDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private MonitorDAOImpl daoImpl;

    @Test
    public void testFindRsmRangCount() throws Exception {
        Niederlassung niederlassung = setupTestData();

        ResourcenMonitorQuery query = new ResourcenMonitorQuery();
        query.setNiederlassungId(niederlassung.getId());

        List<RsmRangCountView> views = daoImpl.findRsmRangCount(query);
        Assert.assertEquals(views.size(), 3L);
        assertEquals(views.get(0).getNiederlassung(), niederlassung.getName());

        query.addStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        views = daoImpl.findRsmRangCount(query);
        Assert.assertEquals(views.size(), 1L);
        assertEquals(views.get(0).getNiederlassung(), niederlassung.getName());
    }

    @Test
    public void testFindUevtCuDAViews() throws Exception {
        List<UevtCuDAView> result = daoImpl.findUevtCuDAViews();
        assertNotEmpty(result, "collection should not be empty");

        ResourcenMonitorQuery query1 = new ResourcenMonitorQuery();
        query1.setNiederlassungId(Niederlassung.ID_AUGSBURG);
        List<UevtCuDAView> result1 = daoImpl.findUevtCuDAViews(query1);
        assertTrue(result1.size() < result.size(), "does not filter by niederlassung");

        ResourcenMonitorQuery query2 = new ResourcenMonitorQuery();
        query2.addStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        List<UevtCuDAView> result2 = daoImpl.findUevtCuDAViews(query2);
        assertTrue(result2.size() < result.size(), "does not filter by standortType");

        ResourcenMonitorQuery query3 = new ResourcenMonitorQuery();
        query3.setNiederlassungId(Niederlassung.ID_AUGSBURG);
        query3.addStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        List<UevtCuDAView> result3 = daoImpl.findUevtCuDAViews(query3);
        assertTrue(result3.size() < result1.size(), "does not filter by niederlassung and standortType");
        assertTrue(result3.size() < result2.size(), "does not filter by niederlassung and standortType");
    }

    @Test
    public void testFindHVTBelegungGrouped() throws Exception {
        List<HVTBelegungView> views = new ArrayList<>();

        HVTBelegungView view = new HVTBelegungView();
        view.setUevt("0815");
        view.setHvtIdStandort(HVTStandort.HVT_STANDORT_TYP_HVT);
        view.setBelegt(1);
        view.setCudaPhysik("H");
        view.setRangLeiste1("10");
        view.setRangSSType("2H");
        view.setFrei(0);
        views.add(view);

        daoImpl.store(views);

        views = daoImpl.findHVTBelegungGrouped("0815", HVTStandort.HVT_STANDORT_TYP_HVT);
        Assert.assertFalse(views.isEmpty());
    }

    @Test
    public void testFindViewsGroupedByRangSSType() throws Exception {
        List<UevtCuDAView> views = new ArrayList<>();

        UevtCuDAView view = new UevtCuDAView();
        view.setHvtIdStandort(HVTStandort.HVT_STANDORT_TYP_HVT);
        view.setUevtId(1L);
        view.setUevt("0815");
        view.setCudaPhysik("N");
        view.setCarrier("DTAG");
        view.setCudaFrei(0);
        view.setCudaVorbereitet(0);
        view.setCudaRangiert(0);
        view.setHvtName("Hier");
        view.setOnkz("89");
        view.setAsb(0);
        view.setRangSSType("2H");

        views.add(view);

        daoImpl.storeUevtCuDAViews(views);

        views = daoImpl.findViewsGroupedByRangSSType("0815", "N", HVTStandort.HVT_STANDORT_TYP_HVT);
        Assert.assertFalse(views.isEmpty());
    }

    @Test
    public void testSumPortUsage() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withInbetriebnahme(new Date())
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(513L);
        HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withHvtGruppeBuilder(ontGruppeBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("#VBZ#"))
                .withAuftragBuilder(auftragBuilder);
        HVTTechnikBuilder hvtTechnikBuilder = getBuilder(HVTTechnikBuilder.class)
                .withCpsName("ALCATEL_LUCENT")
                .withHersteller("Alcatel");
        HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class)
                .withHwProducerBuilder(hvtTechnikBuilder)
                .withGeraeteBez("OLT-400003");
        HWOntBuilder ontBuilder = getBuilder(HWOntBuilder.class)
                .withHWRackOltBuilder(oltBuilder)
                .withHvtStandortBuilder(ontStandortBuilder)
                .withSerialNo("A-1-B2-C3-0815")
                .withGeraeteBez("ONT-404453");
        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(ontBuilder);
        EquipmentBuilder ontEquipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withKvzNummer("KVZ10")
                .withRangBucht("01.-001")
                .withHvtStandortBuilder(ontStandortBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI)
                .withHvtStandortBuilder(ontStandortBuilder)
                .withEqOutBuilder(ontEquipmentBuilder);
        RangierungBuilder rangierungAdditionalBuilder = getBuilder(RangierungBuilder.class)
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_ADSL_UK0_HUAWEI)
                .withHvtStandortBuilder(ontStandortBuilder)
                .withEqOutBuilder(ontEquipmentBuilder);
        EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder)
                .withRangierungAdditionalBuilder(rangierungAdditionalBuilder)
                .withGeoIdBuilder(getBuilder(GeoIdBuilder.class))
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);

        auftragDatenBuilder.build();
        endstelleBuilder.build();
        eqVlanBuilder.build();

        flushAndClear();

        int sum = daoImpl.sumPortUsage(DateTools.createDate(2009, 0, 1),
                DateTools.getHurricanEndDate(),
                ontStandortBuilder.get().getId(),
                ontEquipmentBuilder.get().getKvzNummer(),
                rangierungBuilder.get().getPhysikTypId(),
                rangierungAdditionalBuilder.get().getPhysikTypId(),
                false);

        Assert.assertTrue(sum > 0);
    }

    private Niederlassung setupTestData() {
        // @formatter:off
        Niederlassung niederlassung = getBuilder(NiederlassungBuilder.class)
                .withRandomId()
                .build();
        HVTGruppeBuilder hvtGruppeBuilder1 = getBuilder(HVTGruppeBuilder.class)
                .withRandomId()
                .withNiederlassungId(niederlassung.getId());
        HVTStandortBuilder hvtStandortBuilder1 = getBuilder(HVTStandortBuilder.class)
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder1)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT);
        HVTStandortBuilder hvtStandortBuilder2 = getBuilder(HVTStandortBuilder.class)
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder1)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_KVZ)
                .withClusterId("TEST_CLUSTER");
        HVTStandortBuilder hvtStandortBuilder3 = getBuilder(HVTStandortBuilder.class)
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder1)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withClusterId("TEST_CLUSTER");
        RsmRangCountBuilder rsmRangCountBuilder1 = getBuilder(RsmRangCountBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder1)
                .withPhysiktyp(PhysikTyp.PHYSIKTYP_UK0)
                .withVorhanden(1);
        RsmRangCountBuilder rsmRangCountBuilder2 = getBuilder(RsmRangCountBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder2)
                .withPhysiktyp(PhysikTyp.PHYSIKTYP_UK0)
                .withVorhanden(1);
        RsmRangCountBuilder rsmRangCountBuilder3 = getBuilder(RsmRangCountBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder3)
                .withPhysiktyp(PhysikTyp.PHYSIKTYP_UK0)
                .withVorhanden(1);

        rsmRangCountBuilder1.build();
        rsmRangCountBuilder2.build();
        rsmRangCountBuilder3.build();

        return niederlassung;
        // @formatter:on
    }

    @Test
    public void testStoreHVTBelegungView() throws Exception {
        List<HVTBelegungView> views = new ArrayList<>();

        daoImpl.store(views);

        HVTBelegungView view = new HVTBelegungView();
        view.setUevt("0815");
        view.setHvtIdStandort(HVTStandort.HVT_STANDORT_TYP_HVT);
        view.setBelegt(1);
        view.setCudaPhysik("H");
        view.setRangLeiste1("10");
        view.setRangSSType("2H");
        view.setFrei(0);
        views.add(view);

        daoImpl.store(views);
    }
}