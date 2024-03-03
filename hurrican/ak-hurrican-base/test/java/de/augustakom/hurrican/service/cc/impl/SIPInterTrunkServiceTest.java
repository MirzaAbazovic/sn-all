/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 11:38:59
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunkBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.SIPInterTrunkService;

/**
 * TestNG Klasse fuer {@link SIPInterTrunkService}
 */
@Test(groups = BaseTest.SERVICE)
public class SIPInterTrunkServiceTest extends AbstractHurricanBaseServiceTest {

    private AuftragSIPInterTrunk sipInterTrunk;

    private void buildTestData(boolean persist) {
        HWSwitch hwSwitch = getBuilder(HWSwitchBuilder.class)
                .withType(HWSwitchType.IMS)
                .withName("TEST_SWITCH")
                .build();

        sipInterTrunk = getBuilder(AuftragSIPInterTrunkBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class).withRandomId())
                .withHwSwitch(hwSwitch)
                .withTrunkGroup("trunkGroup")
                .setPersist(persist).build();
    }

    public void testSaveSIPInterTrunk() throws StoreException {
        buildTestData(false);
        SIPInterTrunkService service = getCCService(SIPInterTrunkService.class);
        service.saveSIPInterTrunk(sipInterTrunk, getSessionId());

        assertNotNull(sipInterTrunk.getId());
        assertNotNull(sipInterTrunk.getUserW());
    }

    @Test(expectedExceptions = StoreException.class)
    public void testSaveSIPInterTrunkNoSessionId() throws StoreException {
        buildTestData(false);
        SIPInterTrunkService service = getCCService(SIPInterTrunkService.class);
        service.saveSIPInterTrunk(sipInterTrunk, null);
    }

    public void testFindSIPInterTrunks4Order() throws FindException {
        buildTestData(true);

        SIPInterTrunkService service = getCCService(SIPInterTrunkService.class);
        List<AuftragSIPInterTrunk> result = service.findSIPInterTrunks4Order(sipInterTrunk.getAuftragId());
        assertNotEmpty(result, "Keine SIP InterTrunk Daten gefunden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getTrunkGroup(), sipInterTrunk.getTrunkGroup());
    }

}
