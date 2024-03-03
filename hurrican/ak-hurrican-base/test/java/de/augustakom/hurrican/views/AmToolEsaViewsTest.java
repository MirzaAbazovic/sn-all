/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 10:39:16
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class AmToolEsaViewsTest extends AbstractViewsTest {

    @DataProvider(name = "amToolEsaViewNamesDP")
    public Object[][] amToolEsaViewNamesDP() {
        return new Object[][] { { "V_KONTR_ESAA_RM_OK" }, { "V_KONTR_ESAA_RM_ERR_O_KUEND" }, { "V_KONTR_ESAA_RM_FEHLT" },
                { "V_KONTR_ESAA_TAL_UEBERSICHT" } };
    }

    @Test(dataProvider = "amToolEsaViewNamesDP")
    public void amToolEsaViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "amToolEsaColumnCheckDP")
    public Object[][] amToolEsaColumnCheckDP() {
        return new Object[][] {
                {
                        "V_KONTR_ESAA_RM_OK",
                        new String[] { "TBS_TS", "B005_2", "B005_3", "B005_4", "TBS_ID", "B017_2", "TBS_TS_SND",
                                "TFE_KLASSE", "B002_2", "B002_4", "B001_2", "B001_4", "B001_5", "B001_6", "B015_2_SND",
                                "B015_3_SND", "ANZ_ARBEITSTAGE" } },
                {
                        "V_KONTR_ESAA_RM_ERR_O_KUEND",
                        new String[] { "TBS_TS", "B005_2", "B005_3", "B005_4", "TBS_ID", "B017_2", "TBS_TS_SND",
                                "TFE_KLASSE", "B002_2", "B002_4", "B001_2", "B001_4", "B001_5", "B001_6", "B015_2_SND",
                                "B015_3_SND", "B001_2_RM", "ANZ_ARBEITSTAGE" } },
                {
                        "V_KONTR_ESAA_RM_FEHLT",
                        new String[] { "CB_ID", "LBZ", "ESAA_VORGANG", "TS_DATUM", "TS_ZEIT", "TBS_LEVEL", "TBS_ID",
                                "TBS_TBV_ID", "TBS_AUF_ID", "TBS_LTG_ID", "TBS_SRC_ID", "TBS_TBS_ID",
                                "TBS_TBS_FIRST_ID", "TBS_STATUS", "TBS_DATEI", "TBS_SENDER", "TBS_RECIPIENT",
                                "TBS_SENDEVERSUCH", "B001_ID", "B001_2", "B001_4", "B001_5", "B001_6", "B005_ID",
                                "B005_2", "B005_3", "B005_4", "TFE_ID", "B002_ID", "B002_2" } },
                {
                        "V_KONTR_ESAA_TAL_UEBERSICHT",
                        new String[] { "TBS_ID", "TS_DATUM", "TS_ZEIT", "B001_2", "B001_4", "B001_5", "B001_6",
                                "B005_2", "B005_3", "B005_4", "TFE_KLASSE", "B002_2", "B009_3", "B009_5" } }
        };
    }

    @Test(dataProvider = "amToolEsaColumnCheckDP")
    public void amToolEsaColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
