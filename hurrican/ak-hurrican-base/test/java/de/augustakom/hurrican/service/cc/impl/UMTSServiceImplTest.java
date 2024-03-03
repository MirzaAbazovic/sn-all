/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2010 11:31:49
 */

package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragUMTS;
import de.augustakom.hurrican.model.cc.AuftragUMTSBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.UMTSService;


/**
 *
 */
@Test(groups = BaseTest.SERVICE)
public class UMTSServiceImplTest extends AbstractHurricanBaseServiceTest {

    private UMTSService umtsService;

    @BeforeMethod(groups = BaseTest.SERVICE, dependsOnMethods = { "beginTransactions" })
    public void initUmtsService() {
        umtsService = getCCService(UMTSService.class);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindAuftragUMTS() throws FindException, StoreException {
        //original AuftragUMTS
        //create and persist record #1
        AuftragUMTS toBeFound1 = getBuilder(AuftragUMTSBuilder.class).withAuftragBuilder(getBuilder(AuftragBuilder.class)).build();
        //then find persisted record in db
        AuftragUMTS toFind1 = umtsService.findAuftragUMTS(toBeFound1.getAuftragId());
        assertNotNull(toFind1, "AuftragUMTS not found.");
        assertTrue(toFind1.getId().equals(toBeFound1.getId()), "Original and returned AuftragUMTS must be equal.");

        //audited AuftragUMTS
        //first persist record #2
        AuftragUMTS toBeFound2 = umtsService.saveAuftragUMTS(toBeFound1, getSessionId());
        //then find persisted record in db
        AuftragUMTS toFind2 = umtsService.findAuftragUMTS(toBeFound2.getAuftragId());
        assertNotNull(toFind2, "Audited AuftragUMTS not found.");
        assertTrue(toFind2.getId().equals(toBeFound2.getId()), "Original and returned AuftragUMTS must be equal.");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testSaveAuftragUMTSWithBuilder() throws StoreException {
        //this is just a builder test - create class tree and persist in db
        AuftragUMTS toSave = getBuilder(AuftragUMTSBuilder.class).withAuftragBuilder(getBuilder(AuftragBuilder.class)).build();
        assertNotNull(toSave.getId(), "AuftragUMTS not saved.");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testSaveAuftragUMTSWithService() throws StoreException {
        //create persisted Auftrag
        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();

        //original AuftragUMTS
        //create none persisted UMTS data and persist it via service
        AuftragUMTS toSave1 = getBuilder(AuftragUMTSBuilder.class).setPersist(false).build();
        toSave1.setAuftragId(auftrag.getId());
        umtsService.saveAuftragUMTS(toSave1, getSessionId());
        assertNotNull(toSave1.getId(), "AuftragUMTS not saved.");

        //audited AuftragUMTS
        //persist UMTS data a second time -> #2 records (audited (#1) and current(#2))
        flushAndClear(); //write data to persistence store, because we want to update 4 history
        AuftragUMTS toSave2 = umtsService.saveAuftragUMTS(toSave1, getSessionId());
        assertNotNull(toSave2.getId(), "Audited AuftragUMTS not saved.");
        assertFalse(toSave1.getId().equals(toSave2.getId()), "Original and audited AuftragUMTS must be different.");
    }

}
