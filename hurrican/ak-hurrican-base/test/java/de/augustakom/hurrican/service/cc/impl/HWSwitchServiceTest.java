/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 16:00:46
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWSwitchService;

/**
 * Servicetest fuer {@link HWSwitchServiceImpl}.
 *
 *
 * @since Release 10
 */
@Test(groups = { BaseTest.SERVICE })
public class HWSwitchServiceTest extends AbstractHurricanBaseServiceTest {

    private HWSwitchService sut;

    @BeforeMethod
    @SuppressWarnings("unused")
    private void makeInstanceOfSystemUnderTest() {
        sut = getCCService(HWSwitchService.class);
    }

    /**
     * Testmethode fuer {@link HWSwitchService#findSwitchByName(String)}. Liefert ein Null-Objekt, wenn fuer den Namen
     * eine Nullreferenz verwendet wird.
     */
    @Test(groups = { BaseTest.SERVICE })
    public void findSwitchByName_NoNameGiven() throws FindException {
        assertNull(sut.findSwitchByName(null));
    }

    /**
     * Testemethode fuer {@link HWSwitchService#findSwitchByName(String)}. Liefert ein Null-Objekt fuer einen Namen, der
     * nicht bekannt ist.
     */
    @Test(groups = { BaseTest.SERVICE })
    public void findSwitchByName_NoProperNameGiven() throws FindException {
        assertNull(sut.findSwitchByName("OINK"));
    }

    /**
     * Testmethode fuer {@link HWSwitchService#findSwitchByName(String)}. Liefert einen {@link HWSwitch} mit richtigen
     * Daten inkl. Namen.
     */
    @Test(groups = { BaseTest.SERVICE })
    public void findSwitchByName_ProperNameGiven() throws FindException {
        final String properName = "MUC06";
        HWSwitch result = sut.findSwitchByName(properName);
        assertNotNull(result);
        assertEquals(result.getName(), properName);
    }

}
