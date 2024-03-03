/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2013 15:48:54
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG Klasse fuer {@link ChangeEqOutPortTool}
 */
@Test(groups = { BaseTest.UNIT })
public class ChangeEqOutPortToolTest extends BaseTest {

    @Spy
    private ChangeEqOutPortTool sut = new ChangeEqOutPortTool();

    @Mock
    private RangierungsService rangierungsServiceMock;

    @Mock
    private HWService hwServiceMock;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        sut.setRangierungsService(rangierungsServiceMock);
        sut.setHwService(hwServiceMock);
    }

    @DataProvider(name = "definePhysikTypesDP")
    public Object[][] definePhysikTypesDP() {
        Uebertragungsverfahren h04 = Uebertragungsverfahren.H04;
        Uebertragungsverfahren h13 = Uebertragungsverfahren.H13;
        Uebertragungsverfahren n01 = Uebertragungsverfahren.N01;
        Long adslH = PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI;
        Long adsl2H = PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI;
        Long adsl2MsH = PhysikTyp.PHYSIKTYP_ADSL2P_MS_HUAWEI;
        Long adslAtmA = PhysikTyp.PHYSIKTYP_ADSL_ATM_ALCATEL;
        Long adsl2AtmA = PhysikTyp.PHYSIKTYP_ADSL2P_ATM_ALCATEL;
        Long hwBgAdbf = HWBaugruppenTyp.HW_ADBF;
        Long hwBgAdbf2 = HWBaugruppenTyp.HW_ADBF2;

        return new Object[][] {
                // @formatter:off
                // uetvToChange, uetvToUse, ptToChange, ptToUse, hwBaugruppenTypToChange, Long hwBaugruppenTypToUse, expPtToChange, expPtToUse
                // Huawei <-> Alcatel
                { h04, h13, adslH, adsl2AtmA, hwBgAdbf, null, adsl2H, adslAtmA},

                // Huawei (ADBF1) <-> Huawei (ADBF1)
                { h04, h13, adslH, adsl2H, hwBgAdbf, hwBgAdbf, adsl2H, adslH},
                { h13, h04, adsl2H, adslH, hwBgAdbf, hwBgAdbf, adslH, adsl2H},

                // Huawei (ADBF1) <-> Huawei (ADBF2)
                { h04, h13, adslH, adsl2MsH, hwBgAdbf, hwBgAdbf2, adsl2H, adslH},
                // Huawei (ADBF2) <-> Huawei (ADBF2)
                { h04, h13, adslH, adsl2MsH, hwBgAdbf2, hwBgAdbf2, adsl2MsH, adslH},
                // Huawei (ADBF2) <-> Huawei (ADBF1)
                { h04, h13, adslH, adsl2H, hwBgAdbf2, hwBgAdbf, adsl2MsH, adslH},

                // Uebertragungverfahren aendern sich nicht
                { h04, h04, adslH, adsl2H, hwBgAdbf, hwBgAdbf, adslH, adsl2H},
                { h13, h13, adslH, adsl2H, hwBgAdbf, hwBgAdbf, adslH, adsl2H},

                // Niederbit <-> Niederbit
                { n01, n01, adslH, adsl2H, hwBgAdbf, hwBgAdbf, adslH, adsl2H},
                // @formatter:on
        };
    }

    @Test(dataProvider = "definePhysikTypesDP")
    public void testDefinePhysikTypes(Uebertragungsverfahren uetvToChange, Uebertragungsverfahren uetvToUse,
            Long ptToChange, Long ptToUse, Long hwBaugruppenTypToChange, Long hwBaugruppenTypToUse,
            Long expectedPtToChange, Long expectedPtToUse) throws FindException {
        Rangierung rangToChange = new Rangierung();
        rangToChange.setPhysikTypId(ptToChange);
        rangToChange.setEqOutId(Long.valueOf(1L));
        rangToChange.setEqInId(Long.valueOf(3L));
        Rangierung rangToUse = new Rangierung();
        rangToUse.setPhysikTypId(ptToUse);
        rangToUse.setEqOutId(Long.valueOf(2L));
        rangToUse.setEqInId(Long.valueOf(4L));

        Equipment eqOutToChange = new Equipment();
        eqOutToChange.setUetv(uetvToChange);
        eqOutToChange.setHwBaugruppenId(Long.valueOf(1L));
        Equipment eqOutToUse = new Equipment();
        eqOutToUse.setUetv(uetvToUse);
        eqOutToUse.setHwBaugruppenId(Long.valueOf(2L));

        when(rangierungsServiceMock.findEquipment(rangToChange.getEqOutId())).thenReturn(eqOutToChange);
        when(rangierungsServiceMock.findEquipment(rangToUse.getEqOutId())).thenReturn(eqOutToUse);
        doReturn(hwBaugruppenTypToChange).when(sut).findHwBaugruppe4EqIn(Long.valueOf(3L));
        doReturn(hwBaugruppenTypToUse).when(sut).findHwBaugruppe4EqIn(Long.valueOf(4L));

        sut.definePhysikTypes(rangToChange, rangToUse);

        assertEquals(rangToChange.getPhysikTypId(), expectedPtToChange);
        assertEquals(rangToUse.getPhysikTypId(), expectedPtToUse);
    }
}


