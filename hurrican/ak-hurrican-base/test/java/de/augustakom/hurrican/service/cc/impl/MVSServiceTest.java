/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:35:48
 */

package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import javax.annotation.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.cc.AuftragMVSBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterpriseBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.model.cc.AuftragMVSSiteBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.MVSService;

@Test(groups = { BaseTest.SERVICE })
public class MVSServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "de.augustakom.hurrican.service.cc.MVSService")
    private MVSService mvsService;

    @DataProvider
    public Object[][] builderProvider() {
        return new Object[][] { { AuftragMVSEnterpriseBuilder.class }, { AuftragMVSSiteBuilder.class } };
    }

    @Test(dataProvider = "builderProvider")
    public <T extends AuftragMVSBuilder<?, ?>> void testSaveWithBuilder(Class<T> builderClass)
            throws StoreException {
        AuftragMVS toSave = getBuilder(builderClass)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .build();
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragMVS not saved.");
    }

    @Test(dataProvider = "builderProvider")
    public <T extends AuftragMVSBuilder<?, ?>> void testSaveWithService(Class<T> builderClass) throws StoreException {
        AuftragMVS toSave = getBuilder(builderClass).setPersist(false).build();
        toSave.setAuftragId(Long.valueOf(123123));
        mvsService.saveAuftragMvs(toSave);
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragMVS not saved.");
    }

    @Test(dataProvider = "builderProvider", expectedExceptions = StoreException.class)
    public <T extends AuftragMVSBuilder<?, ?>> void auftragWithoutUsernameShouldBeInvalid(Class<T> builderClass)
            throws StoreException {
        AuftragMVS toSave = getBuilder(builderClass).withUserName(null).setPersist(false).build();
        toSave.setAuftragId(Long.valueOf(123123));
        mvsService.saveAuftragMvs(toSave);
        flushAndClear();
    }

    @Test(dataProvider = "builderProvider", expectedExceptions = StoreException.class)
    public <T extends AuftragMVSBuilder<?, ?>> void auftragWithoutPasswordShouldBeInvalid(Class<T> builderClass)
            throws StoreException {
        AuftragMVS toSave = getBuilder(builderClass).withPassword(null).setPersist(false).build();
        toSave.setAuftragId(Long.valueOf(123123));
        mvsService.saveAuftragMvs(toSave);
        flushAndClear();
    }

    public void testFindMvsEnterprise4Auftrag()
            throws FindException, StoreException {
        AuftragMVSEnterprise toSave = getBuilder(AuftragMVSEnterpriseBuilder.class).setPersist(false).build();
        toSave.setAuftragId(123123L);
        mvsService.saveAuftragMvs(toSave);
        flushAndClear();

        AuftragMVSEnterprise auftragMVSEnterprise = mvsService.findMvsEnterprise4Auftrag(123123L);
        assertNotNull(auftragMVSEnterprise);
        assertEquals(auftragMVSEnterprise.getUserName(), toSave.getUserName());
        assertEquals(auftragMVSEnterprise.getMail(), toSave.getMail());
        assertEquals(auftragMVSEnterprise.getDomain(), toSave.getDomain());
        assertEquals(auftragMVSEnterprise.getPassword(), toSave.getPassword());
    }

    public void testFindMvsSite4Auftrag()
            throws StoreException, FindException {
        AuftragMVSSite toSave = getBuilder(AuftragMVSSiteBuilder.class).setPersist(false).build();
        toSave.setAuftragId(123123L);
        mvsService.saveAuftragMvs(toSave);
        flushAndClear();

        AuftragMVSSite auftragMVSSite = mvsService.findMvsSite4Auftrag(123123L, true);
        assertNotNull(auftragMVSSite);
        assertEquals(auftragMVSSite.getUserName(), toSave.getUserName());
        assertEquals(auftragMVSSite.getSubdomain(), toSave.getSubdomain());
        assertEquals(auftragMVSSite.getPassword(), toSave.getPassword());
    }

    public void testFindAllUsedDomains() throws StoreException, FindException {
        AuftragMVSEnterprise toSave1 = getBuilder(AuftragMVSEnterpriseBuilder.class).withDomain("asdf").setPersist(false).build();
        AuftragMVSEnterprise toSave2 = getBuilder(AuftragMVSEnterpriseBuilder.class).withDomain("fdsa").setPersist(false).build();
        mvsService.saveAuftragMvs(toSave1);
        mvsService.saveAuftragMvs(toSave2);
        flushAndClear();

        Collection<String> domains = mvsService.findAllUsedDomains();
        assertTrue(domains.contains(toSave1.getDomain()));
        assertTrue(domains.contains(toSave2.getDomain()));
    }

    public void testFindAuftragIdByEnterpriseDomain() throws StoreException, FindException {
        final String domain = "asdf";
        final Long aIdExp = 123123L;
        //@formatter:off
        getBuilder(AuftragMVSEnterpriseBuilder.class)
                .withDomain(domain)
                .withAuftragId(aIdExp)
                .setPersist(true)
                .build();
        //@formatter:on
        final Long aIdFound = mvsService.findAuftragIdByEnterpriseDomain(domain);
        assertEquals(aIdFound, aIdExp);
    }

    public void testFindAuftragIdByEnterpriseDomain_noAuftragWithDomain() throws StoreException, FindException {
        final Long aIdFound = mvsService.findAuftragIdByEnterpriseDomain("asdf");
        assertNull(aIdFound);
    }

}
