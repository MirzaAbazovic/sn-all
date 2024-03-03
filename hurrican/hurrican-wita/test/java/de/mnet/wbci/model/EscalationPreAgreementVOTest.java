/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2014
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.model.EscalationPreAgreementVO.*;
import static org.testng.Assert.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class EscalationPreAgreementVOTest {

    @DataProvider(name = "escalationTypesExternal")
    public static Object[][] escalationTypesExternal() {
        return new Object[][] {
                { EscalationType.VA_VERSENDET, null, null },
                { EscalationType.VA_VERSENDET, 1, null },
                { EscalationType.VA_VERSENDET, 0, null },
                { EscalationType.VA_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.VA_VERSENDET, -3, EscalationLevel.LEVEL_1 },
                { EscalationType.VA_VERSENDET, -4, EscalationLevel.LEVEL_2 },
                { EscalationType.VA_VERSENDET, -6, EscalationLevel.LEVEL_2 },
                { EscalationType.VA_VERSENDET, -7, EscalationLevel.LEVEL_3 },
                { EscalationType.RUEM_VA_VERSENDET, 1, null },
                { EscalationType.RUEM_VA_VERSENDET, 0, null },
                { EscalationType.RUEM_VA_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.RUEM_VA_VERSENDET, -4, EscalationLevel.LEVEL_1 },
                { EscalationType.RUEM_VA_VERSENDET, -5, EscalationLevel.LEVEL_2 },
                { EscalationType.RUEM_VA_VERSENDET, -6, EscalationLevel.LEVEL_2 },
                { EscalationType.RUEM_VA_VERSENDET, -7, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ABG_VERSENDET, 1, null },
                { EscalationType.STORNO_AEN_ABG_VERSENDET, 0, null },
                { EscalationType.STORNO_AEN_ABG_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ABG_VERSENDET, -2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ABG_VERSENDET, -3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ABG_VERSENDET, -4, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_AUF_VERSENDET, 1, null },
                { EscalationType.STORNO_AEN_AUF_VERSENDET, 0, null },
                { EscalationType.STORNO_AEN_AUF_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_AUF_VERSENDET, -2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_AUF_VERSENDET, -3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_AUF_VERSENDET, -4, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AUFH_ABG_VERSENDET, 1, null },
                { EscalationType.STORNO_AUFH_ABG_VERSENDET, 0, null },
                { EscalationType.STORNO_AUFH_ABG_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AUFH_ABG_VERSENDET, -2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AUFH_ABG_VERSENDET, -3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AUFH_ABG_VERSENDET, -4, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AUFH_AUF_VERSENDET, 1, null },
                { EscalationType.STORNO_AUFH_AUF_VERSENDET, 0, null },
                { EscalationType.STORNO_AUFH_AUF_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AUFH_AUF_VERSENDET, -2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AUFH_AUF_VERSENDET, -3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AUFH_AUF_VERSENDET, -4, EscalationLevel.LEVEL_3 },
                { EscalationType.TV_VERSENDET, 1, null },
                { EscalationType.TV_VERSENDET, 0, null },
                { EscalationType.TV_VERSENDET, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.TV_VERSENDET, -2, EscalationLevel.LEVEL_2 },
                { EscalationType.TV_VERSENDET, -3, EscalationLevel.LEVEL_2 },
                { EscalationType.TV_VERSENDET, -4, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 6, null },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, -1, null },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 6, null },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, -1, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 6, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, -1, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 6, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, -1, null },
        };
    }

    @DataProvider(name = "escalationTypesInternal")
    public static Object[][] escalationTypesInternal() {
        return new Object[][] {
                { EscalationType.VA_EMPFANGEN, null, null },
                { EscalationType.VA_EMPFANGEN, 1, null },
                { EscalationType.VA_EMPFANGEN, 0, null },
                { EscalationType.VA_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.VA_EMPFANGEN, -7, EscalationLevel.LEVEL_1 },
                { EscalationType.RUEM_VA_EMPFANGEN, 1, null },
                { EscalationType.RUEM_VA_EMPFANGEN, 0, null },
                { EscalationType.RUEM_VA_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.RUEM_VA_EMPFANGEN, -7, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ABG_EMPFANGEN, 1, null },
                { EscalationType.STORNO_AEN_ABG_EMPFANGEN, 0, null },
                { EscalationType.STORNO_AEN_ABG_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ABG_EMPFANGEN, -4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_AUF_EMPFANGEN, 1, null },
                { EscalationType.STORNO_AEN_AUF_EMPFANGEN, 0, null },
                { EscalationType.STORNO_AEN_AUF_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_AUF_EMPFANGEN, -4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AUFH_ABG_EMPFANGEN, 1, null },
                { EscalationType.STORNO_AUFH_ABG_EMPFANGEN, 0, null },
                { EscalationType.STORNO_AUFH_ABG_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AUFH_ABG_EMPFANGEN, -4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AUFH_AUF_EMPFANGEN, 1, null },
                { EscalationType.STORNO_AUFH_AUF_EMPFANGEN, 0, null },
                { EscalationType.STORNO_AUFH_AUF_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AUFH_AUF_EMPFANGEN, -4, EscalationLevel.LEVEL_1 },
                { EscalationType.TV_EMPFANGEN, 1, null },
                { EscalationType.TV_EMPFANGEN, 0, null },
                { EscalationType.TV_EMPFANGEN, -1, EscalationLevel.LEVEL_1 },
                { EscalationType.TV_EMPFANGEN, -4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 6, null },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, -1, null },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 6, null },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF, -1, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 6, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, 1, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG, -1, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 6, null },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 5, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 4, EscalationLevel.LEVEL_1 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 3, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 2, EscalationLevel.LEVEL_2 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, 0, EscalationLevel.LEVEL_3 },
                { EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF, -1, null },
                { EscalationType.NEW_VA_EXPIRED, -1, EscalationLevel.INFO },
                { EscalationType.NEW_VA_EXPIRED, 0, EscalationLevel.INFO },
                { EscalationType.NEW_VA_EXPIRED, 1, EscalationLevel.INFO },
                { EscalationType.NEW_VA_EXPIRED, null, EscalationLevel.INFO },
        };
    }

    @Test(dataProvider = "escalationTypesExternal")
    public void testGetEscalationLevelForExternalDeadline(EscalationType escalationType, Integer daysUntilDeadline, EscalationLevel expectedEscalationLevel) throws Exception {
        assertEquals(escalationType.getEscalationLevelForDeadline(daysUntilDeadline), expectedEscalationLevel);
        assertTrue(escalationType.isExternal());

    }

    @Test(dataProvider = "escalationTypesInternal")
    public void testGetEscalationLevelForInternalDeadline(EscalationType escalationType, Integer daysUntilDeadline, EscalationLevel expectedEscalationLevel) throws Exception {
        assertEquals(escalationType.getEscalationLevelForDeadline(daysUntilDeadline), expectedEscalationLevel);
        assertTrue(escalationType.isInternal());
    }

    @Test
    public void testGetExpectedMeldungTypAsString() throws Exception {
        boolean v1 = EscalationType.VA_VERSENDET.getExpectedAction().equals(
                MeldungTyp.RUEM_VA.getShortName() + ", " + MeldungTyp.ABBM.getShortName());
        boolean v2 = EscalationType.VA_VERSENDET.getExpectedAction().equals(
                MeldungTyp.ABBM.getShortName() + ", " + MeldungTyp.RUEM_VA.getShortName());
        Assert.assertTrue(v1 || v2);
    }


}
