/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 10:59:02
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class AmToolVRangierungAkt2AuftragGekViewTest extends AbstractViewsTest {

    @DataProvider(name = "amToolRangierungAkt2AuftragGewViewNamesDP")
    public Object[][] amToolRangierungAkt2AuftragGewViewNamesDP() {
        return new Object[][] { { "V_RANGIERUNG_AKT_2_AUFTRAG_GEK" } };
    }

    @Test(dataProvider = "amToolRangierungAkt2AuftragGewViewNamesDP")
    public void amToolRangierungAkt2AuftragGewViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "rangierungAkt2AuftragGewColumnCheckDP")
    public Object[][] rangierungAkt2AuftragGewColumnCheckDP() {
        return new Object[][] { {
                "V_RANGIERUNG_AKT_2_AUFTRAG_GEK",
                new String[] { "NIEDERLASSUNG", "ONKZ", "ASB", "BEARBEITER", "ORDER__NO", "AUFTRAG_DATUM",
                        "INBETRIEBNAHME", "KUENDIGUNG", "VORGABE_SCV", "BEMERKUNGEN", "BESTELLNR", "ENDSTELLE_ID",
                        "RANGIER_ID", "EQ_IN_ID", "EQ_OUT_ID", "HVT_ID_STANDORT", "GUELTIG_VON", "GUELTIG_BIS",
                        "BEMERKUNG" } } };
    }

    @Test(dataProvider = "rangierungAkt2AuftragGewColumnCheckDP")
    public void rangierungAkt2AuftragGewColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
