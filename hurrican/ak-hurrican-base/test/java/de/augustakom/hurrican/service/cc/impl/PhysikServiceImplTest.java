/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;


@Test(groups = BaseTest.UNIT)
public class PhysikServiceImplTest extends BaseTest {

    private static final String EQ_OUT_STIFT1 = "12";
    private static final String EXPECTED_PORT_ID = "0012";

    private PhysikServiceImpl sut;
    private RangierungsService rangierungsService;
    private EndstellenService endstellenService;

    @BeforeMethod
    public void setUp() throws FindException {
        endstellenService = mock(EndstellenService.class);
        rangierungsService = mock(RangierungsService.class);

        sut = new PhysikServiceImpl();
        sut.setEndstellenService(endstellenService);
        sut.setRangierungsService(rangierungsService);

        Long rangierId = Long.valueOf(99);
        Endstelle endstelle = new Endstelle();
        endstelle.setRangierId(rangierId);

        Long eqOutId = Long.valueOf(77);
        Rangierung rangierung = new Rangierung();
        rangierung.setEqOutId(eqOutId);

        Equipment equipment = new Equipment();
        equipment.setRangStift1(EQ_OUT_STIFT1);

        when(endstellenService.findEndstelle4Auftrag(any(Long.class), any(String.class))).thenReturn(endstelle);
        when(rangierungsService.findRangierung(rangierId)).thenReturn(rangierung);
        when(rangierungsService.findEquipment(eqOutId)).thenReturn(equipment);
    }

    public void testGetPortId4TAL() {
        String portId = sut.getPortId4TAL(Long.valueOf(1), Endstelle.ENDSTELLEN_TYP_B);
        assertEquals(portId, EXPECTED_PORT_ID, "Generation of Port-ID is invalid!");
    }

}
