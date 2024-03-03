/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2011 14:38:29
 */
package de.mnet.wita.servicetest.service;

import static org.testng.Assert.*;

import javax.annotation.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.cc.ESAATalOrderService;
import de.augustakom.hurrican.service.cc.TALOrderService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Service, der sicherstellt, dass die TAL-Order-Services im WITA-Modus richtig konfiguriert wurden.
 */
@Test(groups = BaseTest.SERVICE)
public class TalOrderServicesAvailable extends AbstractServiceTest {

    @Resource(name = "de.mnet.wita.service.WitaTalOrderService")
    private WitaTalOrderService witaTalOrderService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ESAATalOrderService")
    private ESAATalOrderService esaaTalOrderService;

    @Resource(name = "de.augustakom.hurrican.service.cc.TALOrderService")
    private TALOrderService talOrderService;

    public void checkTalOrderServicesAreCorrectInWitaMode() {
        assertNotNull(witaTalOrderService);
        assertNotNull(esaaTalOrderService);
        assertNotNull(talOrderService);
        assertTrue(talOrderService instanceof WitaTalOrderService);
    }

}
