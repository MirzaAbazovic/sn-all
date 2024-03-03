/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2013 15:09:21
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;

/**
 * Modultests fuer {@link RangierungAdminServiceImpl}.
 */
@Test(groups = { BaseTest.UNIT })
public class RangierungAdminServiceImplUnitTest extends BaseTest {

    @Spy
    private RangierungAdminServiceImpl sut = new RangierungAdminServiceImpl();

    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @DataProvider(name = "validateNeededEquipmentsDP")
    public Object[][] validateNeededEquipmentsDP() {
        return new Object[][] {
                // @formatter:off
                // Parent                                  , Child                              , useEWSD, useDSLAM, useCarrier, useSDHPDh
                { PhysikTyp.PHYSIKTYP_CONNECT              , null                               , new boolean[] {false, false, true, true}},

                { PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI       , PhysikTyp.PHYSIKTYP_ADSL_AB_HUAWEI , new boolean[] {true, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI       , PhysikTyp.PHYSIKTYP_ADSL_UK0_HUAWEI, new boolean[] {true, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI        , PhysikTyp.PHYSIKTYP_ADSL_AB_HUAWEI , new boolean[] {true, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI        , PhysikTyp.PHYSIKTYP_ADSL_UK0_HUAWEI, new boolean[] {true, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL2P_MS_HUAWEI     , PhysikTyp.PHYSIKTYP_ADSL_AB_HUAWEI , new boolean[] {true, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL2P_MS_HUAWEI     , PhysikTyp.PHYSIKTYP_ADSL_UK0_HUAWEI, new boolean[] {true, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL2P_ONLY_HUAWEI   , null                               , new boolean[] {false, true, true, false}},
                { PhysikTyp.PHYSIKTYP_ADSL2P_ONLY_MS_HUAWEI, null                               , new boolean[] {false, true, true, false}},
                { PhysikTyp.PHYSIKTYP_SDSL_DA_HUAWEI       , null                               , new boolean[] {false, true, true, false}},
                { PhysikTyp.PHYSIKTYP_SHDSL_HUAWEI         , null                               , new boolean[] {false, true, true, false}},

                { PhysikTyp.PHYSIKTYP_FTTB_VDSL            , null                               , new boolean[] {false, true, false, false}},
                { PhysikTyp.PHYSIKTYP_FTTB_POTS            , null                               , new boolean[] {false, true, false, false}},
                { PhysikTyp.PHYSIKTYP_FTTH                 , null                               , new boolean[] {false, true, false, false}},
                // @formatter:on
        };
    }

    @Test(dataProvider = "validateNeededEquipmentsDP")
    public void testValidateNeededEquipments(Long parentPT, Long childPt, boolean[] expectedFlags) {
        RangierungsAuftrag rangierungsAuftrag = new RangierungsAuftrag();
        rangierungsAuftrag.setPhysiktypParent(parentPT);
        rangierungsAuftrag.setPhysiktypChild(childPt);

        boolean[] result = sut.validateNeededEquipments(rangierungsAuftrag);

        assertNotNull(result);
        assertTrue(result.length == 4);
        assertEquals(expectedFlags[0], result[0]);
        assertEquals(expectedFlags[1], result[1]);
        assertEquals(expectedFlags[2], result[2]);
        assertEquals(expectedFlags[3], result[3]);
    }
}


