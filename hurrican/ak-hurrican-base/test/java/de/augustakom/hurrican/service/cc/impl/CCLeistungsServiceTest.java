/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2005 13:55:53
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BAuftragPosBuilder;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.LeistungBuilder;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.view.TechLeistungSynchView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 * JUnit Test-Case fuer <code>CCLeistungsService</code>.
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class CCLeistungsServiceTest extends AbstractHurricanBaseServiceTest {

    private CCLeistungsService leistungsService;

    @BeforeMethod
    public void setup() {
        leistungsService = getCCService(CCLeistungsService.class);
    }

    public void testFindOneTimeTechServices() throws FindException {
        TechLeistung techLs = getBuilder(TechLeistungBuilder.class)
                .withExternLeistungNo(1L)
                .withRandomName()
                .withTyp("SERVICE")
                .build();

        Long auftragNoOrig = 22L;
        BAuftragBuilder auftragBuilder = getBuilder(BAuftragBuilder.class)
                .withAuftragNoOrig(auftragNoOrig);

        Leistung oneTimeService = getBuilder(LeistungBuilder.class)
                .withExternLeistungNo(techLs.getExternLeistungNo())
                .build();
        BAuftragPos oneTimePos = getBuilder(BAuftragPosBuilder.class)
                .withAuftragNoOrig(auftragNoOrig)
                .withChargeFrom(new Date())
                .withChargeTo(new Date())
                .build();

        Leistung repeatableService = getBuilder(LeistungBuilder.class)
                .withExternLeistungNo(techLs.getExternLeistungNo())
                .build();
        BAuftragPos repeatablePos = getBuilder(BAuftragPosBuilder.class)
                .withAuftragNoOrig(auftragNoOrig)
                .withChargeFrom(new Date())
                .withChargeTo(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .build();

        BAuftragLeistungView oneTime = new BAuftragLeistungView(auftragBuilder.get(), new OE(), oneTimePos,
                oneTimeService);
        BAuftragLeistungView repeatableTimes = new BAuftragLeistungView(auftragBuilder.get(), new OE(), repeatablePos,
                repeatableService);

        List<BAuftragLeistungView> auftragLeistungViews = new ArrayList<BAuftragLeistungView>();
        auftragLeistungViews.add(oneTime);
        auftragLeistungViews.add(repeatableTimes);

        BillingAuftragService billingAuftragService = getMockBillingAuftragService();
        when(billingAuftragService.findAuftragLeistungViews4Auftrag(auftragNoOrig, false, true)).thenReturn(
                auftragLeistungViews);

        CCLeistungsServiceImpl service = getCCLeistungsServiceImpl();
        service.setBillingAuftragService(billingAuftragService);
        List<TechLeistungSynchView> result = service.findOneTimeTechServices(auftragNoOrig);
        assertNotEmpty(result, "No one-time services found for order!");
    }

    public void testSynchTechLeistungen4Auftrag() throws StoreException, FindException {
        Long ccAId = 200795L;
        Long aNoOrig = 1051419L;
        BAuftrag ba = new BAuftrag();
        ba.setGueltigVon(new Date());
        BillingAuftragService billingAuftragService = getMockBillingAuftragService();
        when(billingAuftragService.findAuftrag(eq(aNoOrig))).thenReturn(ba);
        CCLeistungsServiceImpl service = getCCLeistungsServiceImpl();
        service.setBillingAuftragService(billingAuftragService);
        service.synchTechLeistungen4Auftrag(
                ccAId, aNoOrig, 450L, null, false, getSessionId());
    }

    public void testIsTechLeistungActive() throws FindException {
        Long extLstNo = 9999999L;
        TechLeistungBuilder techLeistungBuilder = getBuilder(TechLeistungBuilder.class)
                .withExternLeistungNo(extLstNo);

        Auftrag2TechLeistung auftrag2TechLs = getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withTechleistungBuilder(techLeistungBuilder)
                .withAktivVon(DateTools.createDate(2000, 1, 1))
                .build();

        boolean result = leistungsService.isTechLeistungActive(auftrag2TechLs.getAuftragId(), extLstNo, new Date());
        assertTrue(result, "Tech-Leistung is not active on order but should be!");
    }

    public void testIsTechLeistungActiveExpectFalseBecauseOfValidToDate() throws FindException {
        Long extLstNo = 9999999L;
        TechLeistungBuilder techLeistungBuilder = getBuilder(TechLeistungBuilder.class)
                .withExternLeistungNo(extLstNo);

        Auftrag2TechLeistung auftrag2TechLs = getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withTechleistungBuilder(techLeistungBuilder)
                .withAktivVon(DateTools.createDate(2000, 1, 1))
                .withAktivBis(new Date())
                .build();

        boolean result = leistungsService.isTechLeistungActive(auftrag2TechLs.getAuftragId(), extLstNo, new Date());
        assertFalse(result, "Tech-Leistung is active on order but should not be!");
    }

    public void testGetCountEndgeraetPort_WithQuantity() throws FindException {
        Long quantity = 3L;
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withQuantity(quantity)
                .withAuftragBuilder(auftragBuilder)
                .withTechleistungBuilder(getBuilder(TechLeistungBuilder.class).withTyp(TechLeistung.TYP_EG_PORT))
                .withAktivVon(DateTools.createDate(2000, 1, 1))
                .build();

        Integer result = leistungsService.getCountEndgeraetPort(auftragBuilder.get().getAuftragId(), new Date());
        assertNotNull(result);
        assertTrue(NumberTools.equal(result, quantity.intValue()));
    }

    public void testGetCountEndgeraetPort_WithoutQuantity() throws FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withTechleistungBuilder(getBuilder(TechLeistungBuilder.class).withTyp(TechLeistung.TYP_EG_PORT))
                .withAktivVon(DateTools.createDate(2000, 1, 1))
                .build();
        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withTechleistungBuilder(getBuilder(TechLeistungBuilder.class).withTyp(TechLeistung.TYP_EG_PORT))
                .withAktivVon(DateTools.createDate(2100, 1, 1)) // Auftrag2TechLeistung beginn in Zukunft - muss dennoch
                        // beruecksichtigt werden!
                .build();

        Integer result = leistungsService.getCountEndgeraetPort(auftragBuilder.get().getAuftragId(), new Date());
        assertNotNull(result);
        assertTrue(NumberTools.equal(result, 2));
    }

    public void testGetBandwidthString() throws FindException {
        String downStream = "37000 kbit/s";
        TechLeistungBuilder downStreamTechLeistungBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_DOWNSTREAM)
                .withName(downStream);

        Auftrag2TechLeistung auftrag2TechLs = getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withTechleistungBuilder(downStreamTechLeistungBuilder)
                .withAktivVon(DateTools.createDate(2000, 1, 1))
                .build();

        String upStream = "39000 kbit/s";
        TechLeistungBuilder upStreamTechLeistungBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_UPSTREAM)
                .withName(upStream);

        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragId(auftrag2TechLs.getAuftragId())
                .withTechleistungBuilder(upStreamTechLeistungBuilder)
                .withAktivVon(DateTools.createDate(2000, 1, 1))
                .build();

        String result = leistungsService.getBandwidthString(auftrag2TechLs.getAuftragId());
        assertEquals(result, downStream + " / " + upStream);
    }

    private  BillingAuftragService getMockBillingAuftragService (){
        return mock(BillingAuftragService.class);
    }

    private CCLeistungsServiceImpl getCCLeistungsServiceImpl() {
             return (CCLeistungsServiceImpl) getBean(CCLeistungsService.class.getName());
    }

}
