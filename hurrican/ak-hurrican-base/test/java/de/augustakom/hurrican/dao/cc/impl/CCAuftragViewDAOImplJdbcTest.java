package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import java.util.stream.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
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
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierQuery;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungQuery;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungView;
import de.augustakom.hurrican.model.shared.view.AuftragVorlaufView;
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

@Test(groups = BaseTest.SLOW)
public class CCAuftragViewDAOImplJdbcTest extends AbstractHurricanBaseServiceTest {

    public static final long PROD_ID = 513L;
    public static final long KUNDE_NO = 9080706050L;
    public static final String ORT = "HierOderDort";

    @Autowired
    private CCAuftragViewDAOImplJdbc daoImplJdbc;

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    @Test
    public void testFindAuftragDatenViewsNoResult() {
        AuftragDatenQuery query = new AuftragDatenQuery();
        query.setAuftragId(0L);
        List<AuftragDatenView> views = daoImplJdbc.findAuftragDatenViews(query);
        Assert.assertTrue(views.isEmpty());
    }

    @Test
    public void testFindAuftragDatenViews() {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class)
                        .withProdId(Produkt.PROD_ID_SDSL_10000)
                        .withStatusId(AuftragStatus.IN_BETRIEB))
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class))
                .build();

        flushAndClear();

        AuftragDatenQuery query = new AuftragDatenQuery();
        query.setAuftragId(auftrag.getAuftragId());
        List<AuftragDatenView> views = daoImplJdbc.findAuftragDatenViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftrag.getAuftragId());
        Assert.assertNotNull(views.get(0).getAuftragNoOrig());
        Assert.assertNotNull(views.get(0).getAuftragStatusId());
        Assert.assertNotNull(views.get(0).getBestellNr());
        Assert.assertNotNull(views.get(0).getProdId());
        Assert.assertNotNull(views.get(0).getAnschlussart());
    }

    @Test
    public void testFindAuftragDatenViewsWithIntAccount() {
        IntAccountBuilder intAccountBuilder = getBuilder(IntAccountBuilder.class)
                .withRandomAccount();
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withProdId(Produkt.PROD_ID_DSL_VOIP);
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class)
                        .withIntAccountBuilder(intAccountBuilder))
                .build();

        flushAndClear();

        AuftragDatenQuery query = new AuftragDatenQuery();
        query.setAuftragNoOrig(auftragDatenBuilder.get().getAuftragNoOrig());
        query.setIntAccount(intAccountBuilder.get().getAccount());

        List<AuftragDatenView> views = daoImplJdbc.findAuftragDatenViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftrag.getAuftragId());
        Assert.assertNotNull(views.get(0).getAuftragNoOrig());
        Assert.assertNotNull(views.get(0).getAuftragStatusId());
        Assert.assertNotNull(views.get(0).getBestellNr());
        Assert.assertNotNull(views.get(0).getProdId());
        Assert.assertNotNull(views.get(0).getAnschlussart());
        Assert.assertEquals(views.get(0).getIntAccount(), query.getIntAccount());
    }

    @Test
    public void testFindRealisierungViewsInbetriebnahme() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE);
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class))
                .build();

        flushAndClear();

        AuftragRealisierungQuery query = new AuftragRealisierungQuery();
        query.setInbetriebnahme(true);
        query.setKundeNo(auftrag.getKundeNo());
        query.setInbetriebnahmeFrom(auftragDatenBuilder.get().getInbetriebnahme());
        query.setInbetriebnahmeTo(auftragDatenBuilder.get().getInbetriebnahme());

        List<AuftragRealisierungView> views = daoImplJdbc.findRealisierungViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftrag.getAuftragId());
        Assert.assertNotNull(views.get(0).getAuftragNoOrig());
        Assert.assertNotNull(views.get(0).getAuftragStatusId());
        Assert.assertNotNull(views.get(0).getProdId());
        Assert.assertNotNull(views.get(0).getAnschlussart());
        Assert.assertNotNull(views.get(0).getInbetriebnahme());
    }

    @Test
    public void testFindRealisierungViewsKuendigung() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .withKuendigung(new Date());
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class))
                .build();

        flushAndClear();

        AuftragRealisierungQuery query = new AuftragRealisierungQuery();
        query.setKuendigung(true);
        query.setKundeNo(auftrag.getKundeNo());
        query.setInbetriebnahmeFrom(auftragDatenBuilder.get().getKuendigung());
        query.setInbetriebnahmeTo(auftragDatenBuilder.get().getKuendigung());

        List<AuftragRealisierungView> views = daoImplJdbc.findRealisierungViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftrag.getAuftragId());
        Assert.assertNotNull(views.get(0).getAuftragNoOrig());
        Assert.assertNotNull(views.get(0).getAuftragStatusId());
        Assert.assertNotNull(views.get(0).getProdId());
        Assert.assertNotNull(views.get(0).getAnschlussart());
        Assert.assertNotNull(views.get(0).getKuendigung());
    }

    @Test
    public void testFindRealisierungViewsRealisierung() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class));

        Verlauf verlauf = getBuilder(VerlaufBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .build();

        flushAndClear();

        AuftragRealisierungQuery query = new AuftragRealisierungQuery();
        query.setRealisierung(true);
        query.setKundeNo(auftragBuilder.get().getKundeNo());
        query.setInbetriebnahmeFrom(verlauf.getRealisierungstermin());
        query.setInbetriebnahmeTo(verlauf.getRealisierungstermin());

        List<AuftragRealisierungView> views = daoImplJdbc.findRealisierungViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
        Assert.assertEquals(views.get(0).getRealisierungstermin().getTime(), verlauf.getRealisierungstermin().getTime());
    }

    @Test
    public void testFindAuftragCarrierViews() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        Carrierbestellung2EndstelleBuilder carrierbestellung2EndstelleBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);

        getBuilder(EndstelleBuilder.class)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        Carrierbestellung carrierbestellung = getBuilder(CarrierbestellungBuilder.class)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withVtrNr("1212121212")
                .withLbz("999999991")
                .build();

        flushAndClear();

        AuftragCarrierQuery query = new AuftragCarrierQuery();
        query.setLbz(carrierbestellung.getLbz());

        List<AuftragCarrierView> views = daoImplJdbc.findAuftragCarrierViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
        Assert.assertEquals(views.get(0).getLbz(), carrierbestellung.getLbz());

        query = new AuftragCarrierQuery();
        query.setVtrNr(carrierbestellung.getVtrNr());

        views = daoImplJdbc.findAuftragCarrierViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
        Assert.assertEquals(views.get(0).getVtrNr(), carrierbestellung.getVtrNr());
    }

    @Test
    public void testFindWbciRequestCarrierViews() {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(
                WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        sessionFactory.getCurrentSession().save(vorabstimmungsAnfrage);
        sessionFactory.getCurrentSession().flush();

        List<WbciRequestCarrierView> views = daoImplJdbc.findWbciRequestCarrierViews(vorabstimmungsAnfrage.getVorabstimmungsId());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), wbciGeschaeftsfallKueMrn.getAuftragId());
    }

    @Test
    public void testFindAuftragEndstelleViews() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);

        Endstelle endstelle = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        flushAndClear();

        AuftragEndstelleQuery query = new AuftragEndstelleQuery();
        query.setEndstelle(endstelle.getEndstelle());
        query.setEndstelleOrt(endstelle.getOrt());
        query.setEndstelleTyp(endstelle.getEndstelleTyp());

        List<AuftragEndstelleView> views = daoImplJdbc.findAuftragEndstelleViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
        Assert.assertEquals(views.get(0).getEndstelleTyp(), endstelle.getEndstelleTyp());
        Assert.assertEquals(views.get(0).getEndstelle(), endstelle.getEndstelle());
        Assert.assertEquals(views.get(0).getEndstelleOrt(), endstelle.getOrt());
    }

    @Test
    public void testFindAuftragEndstelleViews4VPN() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        VPNBuilder vpnBuilder = getBuilder(VPNBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class))
                .withVPNBuilder(vpnBuilder);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withKundeNo(KUNDE_NO)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);

        Endstelle endstelle = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        flushAndClear();

        List<AuftragEndstelleView> views = daoImplJdbc.findAuftragEndstelleViews4VPN(vpnBuilder.get().getId(), Collections.singletonList(auftragBuilder.getKundeNo()));
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
        Assert.assertEquals(views.get(0).getEndstelleTyp(), endstelle.getEndstelleTyp());
        Assert.assertEquals(views.get(0).getEndstelle(), endstelle.getEndstelle());
        Assert.assertEquals(views.get(0).getEndstelleOrt(), endstelle.getOrt());
    }

    @Test
    public void testFindAuftragEquipmentViews() {
        AuftragEquipmentQuery query = new AuftragEquipmentQuery();
        query.setHvtIdStandort(29556L);

        List<AuftragEquipmentView> views = daoImplJdbc.findAuftragEquipmentViews(query);
        Assert.assertFalse(views.isEmpty());
    }

    @Test
    public void testFindAESViews4Uebernahme() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withProdId(PROD_ID);
        HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
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
                .withRangBucht("01.-001")
                .withHvtStandortBuilder(ontStandortBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(ontEquipmentBuilder);
        EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder)
                .withGeoIdBuilder(getBuilder(GeoIdBuilder.class))
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);

        auftragDatenBuilder.build();
        endstelleBuilder.build();
        eqVlanBuilder.build();

        rangierungBuilder.get().setEsId(-1L);

        flushAndClear();

        List<AuftragEndstelleView> views = daoImplJdbc.findAESViews4Uebernahme(endstelleBuilder.get().getGeoId(), Arrays.asList(800L));
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAESViews4KundeAndPG() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withProdId(PROD_ID);
        HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
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
                .withRangBucht("01.-001")
                .withHvtStandortBuilder(ontStandortBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(ontEquipmentBuilder);
        EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder)
                .withGeoIdBuilder(getBuilder(GeoIdBuilder.class))
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);

        auftragDatenBuilder.build();
        endstelleBuilder.build();
        eqVlanBuilder.build();

        rangierungBuilder.get().setEsId(-1L);

        flushAndClear();

        List<AuftragEndstelleView> views = daoImplJdbc.findAESViews4KundeAndPG(auftragBuilder.getKundeNo(), ProduktGruppe.MAXI_DELUXE, false);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAESViews4Wandel() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withProdId(PROD_ID);
        HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
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
                .withRangBucht("01.-001")
                .withHvtStandortBuilder(ontStandortBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(ontEquipmentBuilder);
        EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder)
                .withGeoIdBuilder(getBuilder(GeoIdBuilder.class))
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);

        auftragDatenBuilder.build();
        endstelleBuilder.build();
        eqVlanBuilder.build();

        flushAndClear();

        List<AuftragEndstelleView> views = daoImplJdbc.findAESViews4Wandel(endstelleBuilder.get().getGeoId(), auftragBuilder.getKundeNo(), null);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAESViews4TalPortAenderung() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withProdId(PROD_ID);
        HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
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
                .withRangBucht("01.-001")
                .withHvtStandortBuilder(ontStandortBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(ontEquipmentBuilder);
        EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);

        Carrierbestellung2EndstelleBuilder carrierbestellung2EndstelleBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withGeoIdBuilder(getBuilder(GeoIdBuilder.class))
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);

        CarrierbestellungBuilder carrierbestellungBuilder = getBuilder(CarrierbestellungBuilder.class)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withCarrier(Carrier.ID_DTAG)
                .withVtrNr("1212121212")
                .withLbz("999999991");

        auftragDatenBuilder.build();
        carrierbestellungBuilder.build();
        eqVlanBuilder.build();

        flushAndClear();

        List<AuftragEndstelleView> views = daoImplJdbc.findAESViews4TalPortAenderung(endstelleBuilder.get().getGeoId(), endstelleBuilder.get().getEndstelleTyp());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAuftragProduktVbzViews() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withKundeNo(KUNDE_NO);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withProdId(PROD_ID);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("#VBZ#"))
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        flushAndClear();

        CCAuftragProduktVbzQuery query = new CCAuftragProduktVbzQuery();
        query.setKundeNo(auftragBuilder.getKundeNo());
        query.setProduktId(auftragDatenBuilder.get().getProdId());

        List<CCAuftragProduktVbzView> views = daoImplJdbc.findAuftragProduktVbzViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAuftragAccountViews() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withKundeNo(KUNDE_NO);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL)
                .withProdId(PROD_ID);

        IntAccountBuilder intAccountBuilder = getBuilder(IntAccountBuilder.class).withLiNr(9900);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withIntAccountBuilder(intAccountBuilder)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("#VBZ#"))
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        flushAndClear();

        AuftragIntAccountQuery query = new AuftragIntAccountQuery();
        query.setKundeNo(auftragBuilder.getKundeNo());
        query.setProduktGruppeId(ProduktGruppe.MAXI_DELUXE);
        query.setIntAccountTyp(intAccountBuilder.get().getLiNr());

        List<AuftragIntAccountView> views = daoImplJdbc.findAuftragAccountViews(query);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAufragIdAndVbz4AuftragNoOrig() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(100000001L)
                .withProdId(PROD_ID);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        flushAndClear();

        List<CCAuftragIDsView> views = daoImplJdbc.findAufragIdAndVbz4AuftragNoOrig(auftragDatenBuilder.getAuftragNoOrig());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAuftragIdAndVbz4BuendelNr() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withBuendelNr(1001)
                .withBuendelNrHerkunft("BuendelNrHerkunft")
                .withProdId(PROD_ID);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        flushAndClear();

        List<CCAuftragIDsView> views = daoImplJdbc.findAuftragIdAndVbz4BuendelNr(auftragDatenBuilder.get().getBuendelNr(),
                auftragDatenBuilder.get().getBuendelNrHerkunft());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAuftragViews4Kunde() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(PROD_ID);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withKundeNo(KUNDE_NO);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class))
                .withAuftragBuilder(auftragBuilder);

        EndstelleBuilder endstelleABuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        endstelleABuilder.build();
        endstelleBBuilder.build();

        flushAndClear();

        List<CCKundeAuftragView> views = daoImplJdbc.findAuftragViews4Kunde(auftragBuilder.getKundeNo(), true, true, true);
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindActiveAuftraege4AM() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(PROD_ID);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withKundeNo(KUNDE_NO);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class))
                .withAuftragBuilder(auftragBuilder);

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        endstelleBuilder.build();

        flushAndClear();

        List<Map<String, Object>> views = daoImplJdbc.findActiveAuftraege4AM(auftragBuilder.getKundeNo(), auftragDatenBuilder.getAuftragNoOrig());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).get(CCAuftragViewDAO.AM_KEY_CC_AUFTRAG_ID), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAuftragsVorlauf() {
        List<AuftragVorlaufView> views = daoImplJdbc.findAuftragsVorlauf();
        Assert.assertFalse(views.isEmpty());
    }

    @Test
    public void testFindIncomplete() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withGueltigVon(new Date())
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withProdId(PROD_ID);
        HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
                .withHvtGruppeBuilder(ontGruppeBuilder);
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
                .withRangBucht("01.-001")
                .withHvtStandortBuilder(ontStandortBuilder);
        EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);

        Carrierbestellung2EndstelleBuilder carrierbestellung2EndstelleBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);

        CarrierbestellungBuilder carrierbestellungBuilder = getBuilder(CarrierbestellungBuilder.class)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withCarrier(Carrier.ID_DTAG)
                .withVtrNr("1212121212")
                .withLbz("999999991");

        auftragDatenBuilder.build();
        carrierbestellungBuilder.build();
        eqVlanBuilder.build();

        flushAndClear();

        List<IncompleteAuftragView> views = daoImplJdbc.findIncomplete(auftragDatenBuilder.get().getGueltigVon());
        Assert.assertFalse(views.isEmpty());
        List<Long> auftragIds = views.stream().map(view -> view.getAuftragId()).collect(Collectors.toList());
        Assert.assertTrue(auftragIds.contains(auftragBuilder.get().getAuftragId()));
    }

    @Test
    public void testFindKundeAuftragViews4Address() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(PROD_ID);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withKundeNo(KUNDE_NO);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class))
                .withAuftragBuilder(auftragBuilder);

        EndstelleBuilder endstelleABuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        CCAddressBuilder addressBuilder = getBuilder(CCAddressBuilder.class);
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withAddressBuilder(addressBuilder)
                .withAuftragBuilder(auftragBuilder);

        endstelleABuilder.build();
        endstelleBBuilder.build();

        ansprechpartnerBuilder.build();

        flushAndClear();

        List<CCKundeAuftragView> views = daoImplJdbc.findKundeAuftragViews4Address(addressBuilder.get().getId());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

    @Test
    public void testFindAufragIdAndVbz4AuftragIds() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(PROD_ID);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withKundeNo(KUNDE_NO);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class))
                .withAuftragBuilder(auftragBuilder);

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withOrt(ORT)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        endstelleBuilder.build();

        flushAndClear();

        List<CCAuftragIDsView> views = daoImplJdbc.findAufragIdAndVbz4AuftragIds(Collections.singletonList(auftragBuilder.get().getAuftragId()));
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());
    }

}
