/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2012 08:14:14
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.dao.cc.AuftragMVSDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterpriseBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.MVSService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MVSServiceUnitTest extends BaseTest {

    @InjectMocks
    private MVSService sut;

    @Mock
    private KundenService kundenService;
    @Mock
    private CCKundenService ccKundenService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private AuftragMVSDAO auftragMVSDAO;

    @BeforeMethod
    public void setUp() {
        sut = new MVSServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = { NoDataFoundException.class })
    public void testFindEnterpriseForSiteAuftragIdWithoutAuftrag() throws Exception {
        Auftrag siteAuftrag = new AuftragBuilder().withRandomId().build();
        when(auftragService.findAuftragById(eq(siteAuftrag.getAuftragId()))).thenReturn(null);
        sut.findEnterpriseForSiteAuftragId(siteAuftrag.getAuftragId());
    }

    public void testFindEnterpriseForSiteAuftragId() throws Exception {
        Auftrag siteAuftrag = new AuftragBuilder().withRandomId().build();
        AuftragMVSEnterprise auftragMvsEnterpriseExpected = new AuftragMVSEnterpriseBuilder()
                .withRandomId()
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .build();
        when(auftragService.findAuftragById(eq(siteAuftrag.getAuftragId()))).thenReturn(siteAuftrag);
        when(ccKundenService.findActiveAuftragInProdGruppe(eq(siteAuftrag.getKundeNo()),
                eq(Arrays.asList(ProduktGruppe.MVS_ENTERPRISE)))).thenReturn(
                Arrays.asList(auftragMvsEnterpriseExpected.getAuftragId()));
        when(auftragMVSDAO.find4Auftrag(eq(auftragMvsEnterpriseExpected.getAuftragId()),
                eq(AuftragMVSEnterprise.class)))
                .thenReturn(auftragMvsEnterpriseExpected);
        AuftragMVSEnterprise auftragMvsEnterpriseFound = sut.findEnterpriseForSiteAuftragId(siteAuftrag.getAuftragId());
        assertEquals(auftragMvsEnterpriseFound, auftragMvsEnterpriseExpected);
    }

    public void testFindEnterpriseForSiteAuftragIdUeberHauptkunde() throws Exception {
        Auftrag siteAuftrag = new AuftragBuilder().withRandomId().build();
        AuftragMVSEnterprise auftragMvsEnterpriseExpected = new AuftragMVSEnterpriseBuilder()
                .withRandomId()
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .build();
        Kunde kunde = new KundeBuilder().withHauptKundeNo(RandomTools.createLong()).build();
        when(kundenService.findKunde(eq(siteAuftrag.getKundeNo()))).thenReturn(kunde);
        when(auftragService.findAuftragById(eq(siteAuftrag.getAuftragId()))).thenReturn(siteAuftrag);
        when(ccKundenService.findActiveAuftragInProdGruppe(eq(siteAuftrag.getKundeNo()),
                eq(Arrays.asList(ProduktGruppe.MVS_ENTERPRISE)))).thenReturn(Collections.<Long>emptyList());
        when(ccKundenService.findActiveAuftragInProdGruppe(eq(kunde.getHauptKundenNo()),
                eq(Arrays.asList(ProduktGruppe.MVS_ENTERPRISE)))).thenReturn(
                Arrays.asList(auftragMvsEnterpriseExpected.getAuftragId()));
        when(auftragMVSDAO.find4Auftrag(eq(auftragMvsEnterpriseExpected.getAuftragId()),
                eq(AuftragMVSEnterprise.class)))
                .thenReturn(auftragMvsEnterpriseExpected);
        AuftragMVSEnterprise auftragMvsEnterpriseFound = sut.findEnterpriseForSiteAuftragId(siteAuftrag.getAuftragId());
        assertEquals(auftragMvsEnterpriseFound, auftragMvsEnterpriseExpected);
    }

    @DataProvider
    public Object[][] dataProviderGetQualifiedDomain() {
        // @formatter:off
        return new Object[][] {
                // subdomain,          domain,        expectedResult,
                //                                          exceptionExpected
                {       null,            null,                  null,    true },
                {       null, "Kunde.mpbx.de",                  null,    true },
                { "Standort",            null,                  null,    true },
                {         "", "Kunde.mpbx.de",                  null,    true },
                { "Standort",              "",                  null,    true },
                { "Standort",    "Kunde.mpbx", "Standort.Kunde.mpbx",    false },
                { "Standort", "Kunde.mpbx.de", "Standort.Kunde.mpbx.de", false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetQualifiedDomain")
    public void testGetQualifiedDomain(String subdomain, String domain, String expectedResult, boolean exceptionExpected) {
        AuftragMVSEnterprise auftragMVSEnterprise = new AuftragMVSEnterprise();
        auftragMVSEnterprise.setDomain(domain);
        AuftragMVSSite auftragMVSSite = new AuftragMVSSite();
        auftragMVSSite.setSubdomain(subdomain);
        String result = null;
        try {
            result = sut.getQualifiedDomain(auftragMVSEnterprise, auftragMVSSite);
        }
        catch (Exception e) {
            if (!exceptionExpected) {
                fail();
            }
            return;
        }
        if (exceptionExpected) {
            fail();
        }
        assertEquals(result, expectedResult);
    }

}


