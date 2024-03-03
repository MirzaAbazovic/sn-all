/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 16:51:55
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteTokenBuilder;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.model.cc.IPSecSite2SiteBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.IPSecService;


/**
 * Test NG Klasse fuer IPSecService
 *
 *
 */
public class IPSecServiceImplTest extends AbstractHurricanBaseServiceTest {
    private IPSecService ipSecService;

    @BeforeMethod(groups = { "service" }, dependsOnMethods = { "beginTransactions" })
    public void initIpSecService() {
        ipSecService = getCCService(IPSecService.class);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindIPSecSiteToSite() throws FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(IPSecSite2SiteBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withWanGateway("10.0.0.0")
                .build();

        IPSecSite2Site result = ipSecService.findIPSecSiteToSite(auftragBuilder.get().getId());
        assertNotNull(result, "IPSecSite2Site Objekt zu Auftrag nicht gefunden!");
        assertNotNull(result.getWanGateway(), "WAN Gateway not defined");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testSaveIPSecSiteToSite() throws StoreException, ValidationException {
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .build();

        IPSecSite2Site toSave = new IPSecSite2Site();
        toSave.setAuftragId(auftrag.getId());
        toSave.setWanGateway("255.255.255.0");

        ipSecService.saveIPSecSiteToSite(toSave);
        flushAndClear();

        assertNotNull(toSave.getId(), "IPSec object is not saved!");
    }

    @Test(groups = BaseTest.SERVICE, expectedExceptions = { ValidationException.class })
    public void testSaveIPSecSiteToSiteExpectValidationException() throws StoreException, ValidationException {
        IPSecSite2Site emptyObject = new IPSecSite2Site();
        ipSecService.saveIPSecSiteToSite(emptyObject);
    }

    @Test(groups = BaseTest.SERVICE, expectedExceptions = { ValidationException.class })
    public void testSaveIPSecSiteToSiteExpectValidationExceptionCertificate() throws StoreException, ValidationException {
        IPSecSite2Site ipsec = new IPSecSite2Site();
        ipsec.setAuftragId(Long.valueOf(0));
        ipsec.setHasCertificate(Boolean.TRUE);
        ipsec.setHasPresharedKey(Boolean.TRUE);
        ipSecService.saveIPSecSiteToSite(ipsec);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testSaveIPSecClient2SiteToken() throws StoreException {
        IPSecClient2SiteTokenBuilder iPSecClient2SiteTokenBuilder = getBuilder(IPSecClient2SiteTokenBuilder.class).setPersist(false);
        IPSecClient2SiteToken token = iPSecClient2SiteTokenBuilder.get();

        ipSecService.saveClient2SiteToken(token);
        flushAndClear();

        assertNotNull(token.getId());
    }

    @Test(groups = BaseTest.SERVICE)
    public void testSaveIPSecClient2SiteTokens() throws StoreException {
        IPSecClient2SiteToken token1 = getBuilder(IPSecClient2SiteTokenBuilder.class).setPersist(false).build();
        IPSecClient2SiteToken token2 = getBuilder(IPSecClient2SiteTokenBuilder.class).setPersist(false).build();

        ipSecService.saveClient2SiteTokens(Arrays.asList(token1, token2));
        flushAndClear();

        assertNotNull(token1.getId());
        assertNotNull(token2.getId());
    }

    @Test(groups = BaseTest.SERVICE)
    public void testDelIPSecClient2SiteToken() throws StoreException, FindException {
        IPSecClient2SiteToken token = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class)).build();
        flushAndClear();

        ipSecService.deleteClient2SiteToken(token);

        List<IPSecClient2SiteToken> foundTokens = ipSecService.findClient2SiteTokens(token.getAuftragId());
        assertEmpty(foundTokens, "Immer noch Tokens auf dem Auftrag obwohl sie geloescht wurden!");
    }


    @Test(groups = BaseTest.SERVICE)
    public void testFindClient2SiteTokens() throws FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        IPSecClient2SiteToken token = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .get();

        List<IPSecClient2SiteToken> foundTokens = ipSecService.findClient2SiteTokens(auftragBuilder.get().getId());

        assertTrue(foundTokens.contains(token));
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindClient2SiteTokensEmptyList() throws FindException {
        List<IPSecClient2SiteToken> foundTokens = ipSecService.findClient2SiteTokens(99999999L);
        assertNotNull(foundTokens);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindAllClient2SiteTokens() throws FindException {
        IPSecClient2SiteToken token = getBuilder(IPSecClient2SiteTokenBuilder.class).get();

        List<IPSecClient2SiteToken> foundTokens = ipSecService.findAllClient2SiteTokens();

        assertTrue(foundTokens.contains(token));
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindFreeClient2SiteTokens() throws FindException {
        IPSecClient2SiteToken freeToken = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(null).get();
        IPSecClient2SiteToken assignedToken = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class)).get();

        List<IPSecClient2SiteToken> foundTokens = ipSecService.findFreeClient2SiteTokens();

        assertTrue(foundTokens.contains(freeToken), "Nicht belegtes Token nicht gefunden!");
        assertFalse(foundTokens.contains(assignedToken), "Belegtes Token gefunden!");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindFreeClient2SiteTokensWildcard() throws FindException {
        IPSecClient2SiteToken freeToken1 = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(null).withSerialNumber("1112").build();
        IPSecClient2SiteToken freeToken2 = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(null).withSerialNumber("1121").build();
        IPSecClient2SiteToken assignedToken = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class)).get();

        List<IPSecClient2SiteToken> foundTokens = ipSecService.findFreeClient2SiteTokens("112*");

        assertFalse(foundTokens.contains(freeToken1), "Token gefunden, obwohl Seriennummer nicht wie gesucht!");
        assertTrue(foundTokens.contains(freeToken2), "Token nicht gefunden, obwohl Seriennummer wie gesucht!!");
        assertFalse(foundTokens.contains(assignedToken), "Belegtes Token gefunden!");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindAllClient2SiteTokensWildcard() throws FindException {
        IPSecClient2SiteToken token1 = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withSerialNumber("1112").build();
        IPSecClient2SiteToken token2 = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withSerialNumber("1121").build();
        IPSecClient2SiteToken assignedToken = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withSerialNumber("11212").get();

        List<IPSecClient2SiteToken> foundTokens = ipSecService.findAllClient2SiteTokens("112*");

        assertFalse(foundTokens.contains(token1), "Token gefunden, obwohl Seriennummer nicht wie gesucht!");
        assertTrue(foundTokens.contains(token2), "Token nicht gefunden, obwohl Seriennummer wie gesucht!!");
        assertTrue(foundTokens.contains(assignedToken), "Belegtes Token nicht gefunden!");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindAllClient2SiteTokensNotNull() throws FindException {
        List<IPSecClient2SiteToken> foundTokens = ipSecService.findAllClient2SiteTokens();
        assertNotNull(foundTokens);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testImportClient2SiteTokens() throws StoreException {
        final String[] header = new String[] {
                IPSecClient2SiteToken.SERIAL_NUMBER,
                IPSecClient2SiteToken.SAP_ORDER_ID,
                IPSecClient2SiteToken.LIEFERDATUM,
                IPSecClient2SiteToken.LAUFZEIT_IN_MONATEN,
                IPSecClient2SiteToken.BATTERIE_ENDE,
                IPSecClient2SiteToken.BATCH };
        Reader reader = new StringReader(
                StringUtils.join(header, ";") + "\n" +
                        "1234567; 0815; 13.12.2009; 12; 14.01.2010;Batch 13\n" +
                        " 1234568; 0815;13.12.2009; 13; 14.01.2010;Batch 14"
        );
        List<IPSecClient2SiteToken> importedClient2SiteTokens =
                ipSecService.importClient2SiteTokens(reader);
        assertEquals(importedClient2SiteTokens.size(), 2);
        IPSecClient2SiteToken firstImportedToken = importedClient2SiteTokens.iterator().next();
        assertEquals(firstImportedToken.getSerialNumber(), "1234567");
        assertEquals(firstImportedToken.getLaufzeitInMonaten(), Integer.valueOf(12));
        assertEquals(firstImportedToken.getBatterieEnde(), DateTools.createDate(2010, 0, 14));
        assertEquals(firstImportedToken.getBatch(), "Batch 13");
        assertEquals(firstImportedToken.getStatusRefId(), IPSecClient2SiteToken.REF_ID_TOKEN_STATUS_ACTIVE);
    }

}


