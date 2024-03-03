/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.CarrierRole.*;
import static de.mnet.wbci.model.Severity.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class WbciRequestStatusTest {

    @DataProvider
    public Object[][] expectedRequestSeverities() {
        return new Object[][] {
                // CARRIER ROLE, REQUEST_STATUS , EXPECTED SEVERITY
                { AUFNEHMEND, ABBM_TR_VERSENDET, LEVEL_10 },
                { AUFNEHMEND, AKM_TR_EMPFANGEN, null },
                { AUFNEHMEND, ABBM_VERSENDET, LEVEL_20 },
                { AUFNEHMEND, RUEM_VA_VERSENDET, LEVEL_0 },
                { AUFNEHMEND, VA_EMPFANGEN, null },

                { AUFNEHMEND, ABBM_TR_EMPFANGEN, LEVEL_10 },
                { AUFNEHMEND, AKM_TR_VERSENDET, null },
                { AUFNEHMEND, ABBM_EMPFANGEN, LEVEL_20 },
                { AUFNEHMEND, RUEM_VA_EMPFANGEN, LEVEL_0 },
                { AUFNEHMEND, VA_VERSENDET, null },
                { AUFNEHMEND, VA_VORGEHALTEN, null },

                { AUFNEHMEND, TV_ERLM_VERSENDET, LEVEL_0 },
                { AUFNEHMEND, TV_ABBM_VERSENDET, LEVEL_10 },
                { AUFNEHMEND, TV_EMPFANGEN, null },

                { AUFNEHMEND, TV_ERLM_EMPFANGEN, LEVEL_0 },
                { AUFNEHMEND, TV_ABBM_EMPFANGEN, LEVEL_10 },
                { AUFNEHMEND, TV_VERSENDET, null },
                { AUFNEHMEND, TV_VORGEHALTEN, null },

                { AUFNEHMEND, STORNO_ERLM_EMPFANGEN, LEVEL_0 },
                { AUFNEHMEND, STORNO_ABBM_EMPFANGEN, LEVEL_20 },
                { AUFNEHMEND, STORNO_VERSENDET, null },
                { AUFNEHMEND, STORNO_VORGEHALTEN, null },

                { AUFNEHMEND, STORNO_ERLM_VERSENDET, LEVEL_0 },
                { AUFNEHMEND, STORNO_ABBM_VERSENDET, null },
                { AUFNEHMEND, STORNO_EMPFANGEN, LEVEL_10 },

                { ABGEBEND, STORNO_ERLM_EMPFANGEN, LEVEL_0 },
                { ABGEBEND, STORNO_ABBM_EMPFANGEN, LEVEL_10 },
                { ABGEBEND, STORNO_VERSENDET, null },
                { ABGEBEND, STORNO_VORGEHALTEN, null },

                { ABGEBEND, STORNO_ERLM_VERSENDET, LEVEL_0 },
                { ABGEBEND, STORNO_ABBM_VERSENDET, LEVEL_10 },
                { ABGEBEND, STORNO_EMPFANGEN, null },
        };
    }

    @Test
    public void testGetVaRequestStatuses() throws Exception {
        Assert.assertEquals(WbciRequestStatus.getVaRequestStatuses().size(), 11);
        Assert.assertTrue(WbciRequestStatus.getVaRequestStatuses().contains(WbciRequestStatus.ABBM_EMPFANGEN));
    }

    @Test
    public void testGetStornoRequestStatuses() throws Exception {
        Assert.assertEquals(WbciRequestStatus.getStornoRequestStatuses().size(), 7);
        Assert.assertTrue(WbciRequestStatus.getStornoRequestStatuses().contains(WbciRequestStatus.STORNO_ABBM_EMPFANGEN));
    }

    @Test
    public void testGetTvRequestStatuses() throws Exception {
        Assert.assertEquals(WbciRequestStatus.getTvRequestStatuses().size(), 7);
        Assert.assertTrue(WbciRequestStatus.getTvRequestStatuses().contains(WbciRequestStatus.TV_ABBM_EMPFANGEN));
    }

    @Test
    public void testIsVaRequestStatus() throws Exception {
        Assert.assertTrue(WbciRequestStatus.RUEM_VA_EMPFANGEN.isVaRequestStatus());
        Assert.assertFalse(WbciRequestStatus.TV_ABBM_EMPFANGEN.isVaRequestStatus());
    }

    @Test
    public void testIsTvRequestStatus() throws Exception {
        Assert.assertTrue(WbciRequestStatus.TV_EMPFANGEN.isTvRequestStatus());
        Assert.assertFalse(WbciRequestStatus.STORNO_EMPFANGEN.isTvRequestStatus());
    }

    @Test
    public void testIsActiveTvRequestStatus() throws Exception {
        Assert.assertTrue(WbciRequestStatus.TV_VORGEHALTEN.isActiveTvRequestStatus());
        Assert.assertTrue(WbciRequestStatus.TV_VERSENDET.isActiveTvRequestStatus());
        Assert.assertTrue(WbciRequestStatus.TV_EMPFANGEN.isActiveTvRequestStatus());
        Assert.assertFalse(WbciRequestStatus.TV_ABBM_EMPFANGEN.isActiveTvRequestStatus());
        Assert.assertFalse(WbciRequestStatus.TV_ERLM_EMPFANGEN.isActiveTvRequestStatus());
        Assert.assertFalse(WbciRequestStatus.TV_ABBM_VERSENDET.isActiveTvRequestStatus());
        Assert.assertFalse(WbciRequestStatus.TV_ERLM_VERSENDET.isActiveTvRequestStatus());
    }

    @Test
    public void testIsStornoRequestStatus() throws Exception {
        Assert.assertTrue(WbciRequestStatus.STORNO_EMPFANGEN.isStornoRequestStatus());
        Assert.assertFalse(WbciRequestStatus.TV_ABBM_EMPFANGEN.isStornoRequestStatus());
    }

    @Test
    public void testIsStornoErledigtRequestStatus() throws Exception {
        Assert.assertTrue(WbciRequestStatus.STORNO_ERLM_EMPFANGEN.isStornoErledigtRequestStatus());
        Assert.assertTrue(WbciRequestStatus.STORNO_ERLM_VERSENDET.isStornoErledigtRequestStatus());
        Assert.assertFalse(WbciRequestStatus.STORNO_VERSENDET.isStornoErledigtRequestStatus());
    }

    @Test
    public void testIsActiveStornoRequestStatus() throws Exception {
        Assert.assertTrue(WbciRequestStatus.STORNO_VORGEHALTEN.isActiveStornoRequestStatus());
        Assert.assertTrue(WbciRequestStatus.STORNO_VERSENDET.isActiveStornoRequestStatus());
        Assert.assertTrue(WbciRequestStatus.STORNO_EMPFANGEN.isActiveStornoRequestStatus());
        Assert.assertFalse(WbciRequestStatus.STORNO_ABBM_EMPFANGEN.isActiveStornoRequestStatus());
        Assert.assertFalse(WbciRequestStatus.STORNO_ERLM_EMPFANGEN.isActiveStornoRequestStatus());
        Assert.assertFalse(WbciRequestStatus.STORNO_ABBM_VERSENDET.isActiveStornoRequestStatus());
        Assert.assertFalse(WbciRequestStatus.STORNO_ERLM_VERSENDET.isActiveStornoRequestStatus());
    }

    @Test(dataProvider = "expectedRequestSeverities")
    public void testGetSeverity(CarrierRole mnetCarrierRole, WbciRequestStatus status, Severity expectedSeverity) {
        Assert.assertEquals(status.getSeverity(mnetCarrierRole), expectedSeverity);
    }

    @Test
    public void testIsInbound() {
        for (WbciRequestStatus wbciRequestStatus : WbciRequestStatus.values()) {
            if (wbciRequestStatus.name().endsWith("VERSENDET") || wbciRequestStatus.name().endsWith("VORGEHALTEN")) {
                Assert.assertFalse(wbciRequestStatus.isInbound());
            }
            else {
                Assert.assertTrue(wbciRequestStatus.isInbound());
            }
        }
    }

    @DataProvider
    public Object[][] activeChangeStatuses() {
        return new Object[][] {
                // status, isActive
                { TV_ERLM_VERSENDET, false},
                { TV_ABBM_VERSENDET, false},
                { TV_EMPFANGEN, true},

                { TV_ERLM_EMPFANGEN, false},
                { TV_ABBM_EMPFANGEN, false},
                { TV_VERSENDET, true},
                { TV_VORGEHALTEN, true},

                { STORNO_ERLM_EMPFANGEN, false},
                { STORNO_ABBM_EMPFANGEN, false},
                { STORNO_VERSENDET, true },
                { STORNO_VORGEHALTEN, true},

                { STORNO_ERLM_VERSENDET, false},
                { STORNO_ABBM_VERSENDET, false},
                { STORNO_EMPFANGEN, true},

                { VA_VORGEHALTEN, false},
        };
    }

    @Test(dataProvider = "activeChangeStatuses")
    public void testGetActiveChangeStatuses(WbciRequestStatus requestStatus, boolean expectedResult) {
        Assert.assertEquals(WbciRequestStatus.getActiveChangeRequestStatuses().contains(requestStatus), expectedResult);
    }
}
