/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 14:37:54
 */
package de.augustakom.hurrican.service.billing.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.billing.LeistungDAO;
import de.augustakom.hurrican.dao.billing.ServiceValueDAO;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BAuftragPosBuilder;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.LeistungBuilder;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.ServiceBlockPrice;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.model.billing.query.KundeLeistungQuery;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.KundeLeistungView;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * UnitTest fuer die Service-Implementierung von Objekten des Typs 'Leistung'.
 *
 *
 */
@Test(groups = { BaseTest.UNIT })
public class LeistungServiceTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(LeistungServiceTest.class);

    @InjectMocks
    @Spy
    private LeistungServiceImpl testling;

    @Mock
    CCAuftragService ccAuftragService;
    @Mock
    ProduktService produktService;
    @Mock
    private BillingAuftragService billingAuftragService;
    @Mock
    private LeistungDAO leistungDAO;
    @Mock
    private ServiceValueDAO serviceValueDAO;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for LeistungService#findLeistung(Long)
     */
    @Test
    public void testFindLeistung() throws Exception {
        Long leistungNoOrig = 20291L;
        Leistung leistung = new LeistungBuilder().withLeistungNoOrig(20291L).build();
        when(leistungDAO.findLeistungByNoOrig(anyLong())).thenReturn(leistung);
        Leistung result = testling.findLeistung(leistungNoOrig);
        assertEquals(result.getLeistungNoOrig(),leistungNoOrig);
    }

    /**
     * Test method for LeistungService#findLeistungen4Kunde(Long)
     */
    @Test
    public void testFindLeistungen4Kunde() throws Exception{
        Long auftragNoOrig = 98782L;
        Long leistungNoOrig = 95052L;
        Long kundeNo =  200000407L;
        List<Long> oeNos = new ArrayList<>();
        oeNos.add(OE.OE__NO_AKEMAIL);
        oeNos.add(OE.OE__NO_AKEMAIL_K);
        oeNos.add(OE.OE__NO_AKFLAT);
        oeNos.add(OE.OE__NO_AKFLAT_K);
        oeNos.add(OE.OE__NO_AKHOSTING);
        oeNos.add(OE.OE__NO_AKHOSTING_K);

        KundeLeistungQuery query = new KundeLeistungQuery();
        query.setKundeNo(kundeNo);
        query.setOeNoOrigs(oeNos);
        
        BAuftragPos bAuftragPos = new BAuftragPosBuilder()
                .withAuftragNoOrig(auftragNoOrig)
                .withLeistungNoOrig(leistungNoOrig)
                .withChargeTo(DateTools.getBillingEndDate())
                .build();

        Leistung leistung = new LeistungBuilder()
                .withLeistungNoOrig(leistungNoOrig)
                .build();
        BAuftragLeistungView bAuftragLeistungView = BAuftragLeistungView.createBAuftragLeistungView(bAuftragPos,leistung);
        bAuftragLeistungView.setOeNoOrig(OE.OE__NO_AKEMAIL_K);

        doReturn(billingAuftragService).when(testling).getBillingService(BillingAuftragService.class);
        when(billingAuftragService.findAuftragLeistungViews(anyLong(), anyBoolean())).thenReturn(Collections.singletonList(bAuftragLeistungView));
        when(leistungDAO.findLeistungByNoOrig(anyLong())).thenReturn(leistung);
        when(leistungDAO.findRechnungstext(anyLong(), anyString(), anyString())).thenReturn("Rechnungstext");

        List<KundeLeistungView>  result= testling.findLeistungen4Kunde(query);
        assertEquals(result.get(0).getKundeNo(),kundeNo);
        assertEquals(result.get(0).getLeistungRechnungstext(),"Rechnungstext");
    }

    /**
     * Test method for LeistungService#findLeistungen4Auftrag(java.lang.Long)
     */
    @Test
    public void testFindLeistungen4Auftrag() throws Exception {
        Long auftragNoOrig = 98782L;
        Long leistungNoOrig1 = 95052L;
        Long leistungNoOrig2 = 95053L;
        BAuftragPos bAuftragPos1 = new BAuftragPosBuilder().withAuftragNoOrig(auftragNoOrig).withLeistungNoOrig(leistungNoOrig1).build();
        BAuftragPos bAuftragPos2 = new BAuftragPosBuilder().withAuftragNoOrig(auftragNoOrig).withLeistungNoOrig(leistungNoOrig2).build();

        List<BAuftragPos> positionen = Arrays.asList(bAuftragPos1,bAuftragPos2);
        Leistung leistung1 = new LeistungBuilder().withLeistungNoOrig(leistungNoOrig1).build();
        Leistung leistung2 = new LeistungBuilder().withLeistungNoOrig(leistungNoOrig2).build();
        doReturn(billingAuftragService).when(testling).getBillingService(BillingAuftragService.class);
        when(billingAuftragService.findAuftragPositionen(auftragNoOrig, true)).thenReturn(positionen);
        when(leistungDAO.findLeistungen(Collections.singletonList(anyLong()))).thenReturn(Arrays.asList(leistung1,leistung2));

        List<Leistung> result = testling.findLeistungen4Auftrag(auftragNoOrig);
        assertTrue(result.size()==2);
        assertEquals(result.get(0).getLeistungNoOrig(),leistungNoOrig1);
        assertEquals(result.get(1).getLeistungNoOrig(),leistungNoOrig2);
    }

    /**
     * Test method for LeistungService#findLeistungen4Auftrag(java.lang.Long)
     */
    @Test
    public void testFindLeistungen4AuftragWithServiceValue() throws Exception{
        Long auftragNoOrig = 98782L;
        Long leistungNoOrig = 94052L;
        Long externLeistungNo = 60004L;

        List<BAuftragPos> positionen = Collections.singletonList(new BAuftragPosBuilder().withAuftragNoOrig(auftragNoOrig).withLeistungNoOrig(leistungNoOrig).withParameter("CLIP no screening").build());
        Leistung leistung = new LeistungBuilder().withLeistungNoOrig(leistungNoOrig).build();
        ServiceValue serviceValue = new ServiceValue();
        serviceValue.setLeistungNo(leistungNoOrig);
        serviceValue.setValue("CLIP no screening");
        serviceValue.setExternLeistungNo(externLeistungNo);
        doReturn(billingAuftragService).when(testling).getBillingService(BillingAuftragService.class);
        when(billingAuftragService.findAuftragPositionen(auftragNoOrig, true)).thenReturn(positionen);
        when(leistungDAO.findLeistungen(Collections.singletonList(anyLong()))).thenReturn(Collections.singletonList(leistung));

        doReturn(serviceValue).when(serviceValueDAO).findServiceValue(anyLong(),anyString());
        doReturn(leistung).when(leistungDAO).findLeistungByNoOrigWithoutLang(anyLong());
        List<Leistung> result = testling.findLeistungen4Auftrag(auftragNoOrig);
        assertTrue(result.size()==1);
        assertEquals(result.get(0).getName(),"CLIP no screening");
        assertEquals(result.get(0).getExternLeistungNo(),externLeistungNo);
        assertEquals(result.get(0).getLeistungNoOrig(),leistungNoOrig);
    }

    /**
     * Test method for LeistungService#findServiceValue(Long, String)
     */
    @Test
    public void testFindServiceValue() throws Exception {
        Long leistungNoOrig = 94052L;
        String serviceVal = "ISDN";
        Leistung leistung = new LeistungBuilder().withLeistungNoOrig(leistungNoOrig).build();
        ServiceValue serviceValue = new ServiceValue();
        serviceValue.setLeistungNo(leistungNoOrig);
        serviceValue.setValue("ISDN");
        when(leistungDAO.findLeistungByNoOrigWithoutLang(leistungNoOrig)).thenReturn(leistung);
        doReturn(serviceValue).when(serviceValueDAO).findServiceValue(anyLong(),anyString());
        ServiceValue result = testling.findServiceValue(leistungNoOrig, serviceVal);
        assertEquals(result.getLeistungNo(),leistungNoOrig);
        assertEquals(result.getValue(),serviceVal);

    }

    /**
     * Test method for LeistungService#findProductLeistung4Auftrag(java.lang.Long)
     */
    @Test
    public void testFindProductLeistung4Auftrag() throws Exception {
        Long auftragId = 167492L; // 136386); //147915);
        Long auftragNoOrig = 233453L;
        Long leistungNoOrig = 94052L;
        Long externProdNo = 540L;
        AuftragDaten ad = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrig)
                .withStatusId(6000L)
                .withProdId(540L)
                .build();
        BAuftragPos bAuftragPos = new BAuftragPosBuilder()
                .withAuftragNoOrig(auftragNoOrig)
                .withLeistungNoOrig(leistungNoOrig)
                .withChargeTo(DateTools.getBillingEndDate())
                .build();

        Leistung leistung = new LeistungBuilder()
                .withLeistungNoOrig(leistungNoOrig)
                .withExternProduktNo(externProdNo)
                .build();
        BAuftragLeistungView bAuftragLeistungView = BAuftragLeistungView.createBAuftragLeistungView(bAuftragPos,leistung);
        when(ccAuftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(ad);
        doReturn(billingAuftragService).when(testling).getBillingService(BillingAuftragService.class);
        when(billingAuftragService.findAuftragLeistungViews4Auftrag(anyLong(), anyBoolean(), anyBoolean()))
                .thenReturn(Collections.singletonList(bAuftragLeistungView));
        when(produktService.findExtProdNos(anyLong(),anyString())).thenReturn(Collections.singletonList(540L));
        when(leistungDAO.findLeistungByNoOrig(anyLong())).thenReturn(leistung);
        Leistung result = testling.findProductLeistung4Auftrag(
                auftragId, ProduktMapping.MAPPING_PART_TYPE_PHONE);
        assertEquals(result.getLeistungNoOrig(), leistungNoOrig);
    }

    /**
     * Test method for LeistungService#findRechnungstext(Long, String, String)
     */
    @Test
    public void testFindRechnungstext() throws Exception {
        Long leistungNoOrig = 94052L;
        Leistung leistung = new LeistungBuilder().withLeistungNoOrig(leistungNoOrig).build();
        when(leistungDAO.findLeistungByNoOrig(anyLong())).thenReturn(leistung);
        when(leistungDAO.findRechnungstext(anyLong(), anyString(), anyString())).thenReturn("Rechnungstext");
        String result = testling.findRechnungstext(5720L, null, "german");
        assertEquals(result,"Rechnungstext");
    }

    /**
     * Test method for LeistungService#findLeistungen4OE(Long, boolean)
     */
    @Test
    public void testFindLeistungen4OE() throws Exception {
        Long oeNoOrig = 209L;
        Leistung leistung = new Leistung();
        leistung.setHistStatus(BillingConstants.HIST_STATUS_AKT);
        leistung.setOeNoOrig(oeNoOrig);
        leistung.setExternProduktNo(540L);
        when(leistungDAO.queryByExample(anyObject(), any(Class.class))).thenReturn(Collections.singletonList(leistung));
        List<Leistung> result = testling.findLeistungen4OE(209L, true);
        assertTrue(result.size() == 1);
    }

    /**
     * Test method for LeistungService#getUDRTarifType4Auftrag(Long, java.util.Date)
     */
    @Test
    public void testGetUDRTarifType4Auftrag() throws Exception {
        Long auftragId = 890779L;
        Long leistungNoOrig = 94052L;
        Date date = DateTools.createDate(2009, 4, 10);
        Leistung leistung = new LeistungBuilder().withLeistungNoOrig(leistungNoOrig).build();
        leistung.setBillingCode("UDR.M");
        ServiceBlockPrice serviceBlockPrice = new ServiceBlockPrice();
        serviceBlockPrice.setLeistungNo(leistungNoOrig);
        serviceBlockPrice.setBlockPrice(3.99F);
        when(leistungDAO.findUDRServices4Order(anyLong(), anyObject())).thenReturn(Collections.singletonList(leistung));
        when(leistungDAO.findServiceBlockPrices(anyLong())).thenReturn(Collections.singletonList(serviceBlockPrice));
        int result = testling.getUDRTarifType4Auftrag(auftragId, date);
        assertTrue(result == 2);
    }
}


