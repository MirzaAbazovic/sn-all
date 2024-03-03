/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 10:33:18
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class HurricanTDABestellungenViewsTest extends AbstractViewsTest {

    @DataProvider(name = "hurricanTdaBestellungViewNamesDP")
    public Object[][] hurricanTdaBestellungViewNamesDP() {
        return new Object[][] { { "VKUP_TDA_BESTELLUNGEN" } };
    }

    @Test(dataProvider = "hurricanTdaBestellungViewNamesDP")
    public void hurricanTdaBestellungViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "hurricanTdaBestellungColumnCheckDP")
    public Object[][] hurricanTdaBestellungColumnCheckDP() {
        return new Object[][] { {
                "VKUP_TDA_BESTELLUNGEN",
                new String[] { "LBZ_COMP", "C2T_ID",
                        "AUFTRAG_ID_HURRICAN", "VERBINDUNGSNUMMER", "ASB", "ONKZ", "BESTELLT_AM", "VORGABEDATUM", "ZURUECK_AM",
                        "BEREITSTELLUNG_AM", "LBZ", "VTRNR", "AQS", "LL", "TPU_UEVT_SCHRANK", "TDA_EV", "TDA_DA", "TDA_DRAHT",
                        "TDA_UEBTRAGVERFAHREN", "TDA_GLASFASER", "TDA_PLZ", "TDA_ORT", "TDA_STRASSE", "TDA_HAUSNR" } } };
    }

    @Test(dataProvider = "hurricanTdaBestellungColumnCheckDP")
    public void hurricanTdaBestellungColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
