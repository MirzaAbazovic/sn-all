/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2006 11:45:08
 */
package de.augustakom.hurrican.service.billing.impl;

import static org.testng.Assert.*;

import java.util.*;
import javax.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.ArchPrintSet;
import de.augustakom.hurrican.model.billing.BLZ;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Service Test-Case fuer 'RechnungsService'.
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class RechnungsServiceTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    @Qualifier("taifunDataSource")
    private DataSource taifunDataSource;
    @Autowired
    private RechnungsService rechnungsService;

    /**
     * Test method for 'de.augustakom.hurrican.rechnungsService.billing.impl.RechnungsServiceImpl.findRInfo()'
     */
    @Test
    public void testFindRInfo() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory().surfAndFonWithDns(1).persist();
        RInfo result = rechnungsService.findRInfo(generatedTaifunData.getRInfo().getRinfoNo());
        assertNotNull(result);
        assertEquals(result.getKundeNo(), generatedTaifunData.getKunde().getKundeNo());
    }

    /**
     * Test method for 'RechnungsService#findRInfo2KundeView4BillId(..)
     */
    @Test(expectedExceptions = FindException.class)
    public void testFindRInfo2KundeView4BillId() throws Exception {
        //test should only test if the SQL query works -> billing data tables are empty at our test systems!
        rechnungsService.findRInfo2KundeView4BillId("200810000473", "2012", "10");
    }

    /**
     * Test method for 'RechnungsService#findRInfo2KundeViews'
     */
    @Test
    public void testFindRInfo2KundeViews() throws Exception {
        //test should only test if the SQL query works -> billing data tables are empty at our test systems!
        List<RInfo2KundeView> result = rechnungsService.findRInfo2KundeViews(0L, "2012", "10");
        assertEmpty(result);
    }

    /**
     * Test method for 'de.augustakom.hurrican.rechnungsService.billing.impl.RechnungsServiceImpl.findRInfos4KundeNoOrig()'
     */
    @Test
    public void testFindRInfos4KundeNoOrig() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory().surfAndFonWithDns(1).persist();
        List<RInfo> result = rechnungsService.findRInfos4KundeNo(generatedTaifunData.getKunde().getKundeNo());
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getKundeNo(), generatedTaifunData.getKunde().getKundeNo());
    }


    /**
     * Test method for 'RechnungsService#findBLZ(Long)'
     */
    @Test
    public void testFindBLZ() throws Exception {
        RechnungsService service = getBillingService(RechnungsService.class);
        BLZ result = service.findBLZ(72050000L);
        assertNotNull(result, "BLZ konnte nicht gefunden werden!");
        assertEquals(result.getBlzNo().longValue(), 72050000L);
    }

    /**
     * Test method for 'RechnungsService#findKundeByRInfoQuery(RInfoQuery)'
     */
    @Test
    public void testFindKundeByRInfoQuery() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory()
                .withCreateKundenBetreuer().surfAndFonWithDns(1).persist();
        RInfoQuery rQ = new RInfoQuery();
        rQ.setKundeNo(generatedTaifunData.getKunde().getKundeNo());
        List<RInfoAdresseView> result = rechnungsService.findKundeByRInfoQuery(rQ);
        assertNotEmpty(result, "RInfoAdresseView konnte nicht gefunden werden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getKundeNo(), generatedTaifunData.getKunde().getKundeNo());
        assertEquals(result.get(0).getKundenbetreuer(), generatedTaifunData.getKundenBetreuer().getName());
        assertEquals(result.get(0).getRInfoNoOrig(), generatedTaifunData.getRInfo().getRinfoNo());
    }

    /**
     * Test method for 'RechnungsService#findKundeByRInfoQuery(RInfoQuery)'
     */
    @Test
    public void testFindInvoiceDate() throws Exception {
        Date result = rechnungsService.findInvoiceDate(125L);
        assertNotNull(result, "Rechnungsdatum konnte nicht gefunden werden!");
    }

    /**
     * Test method for 'RechnungsService#findKundeByRInfoQuery(RInfoQuery)'
     */
    @Test
    public void testfindArchPrintSets() throws Exception {
        List<ArchPrintSet> result = rechnungsService.findArchPrintSets();
        assertNotEmpty(result, "ArchPrintSets konnten nicht gefunden werden!");
    }

}
