/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 10:55:31
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = { BaseTest.VIEW }, enabled = true)
public class AccountToOrderViewsTest extends AbstractViewsTest {

    @DataProvider(name = "accToOrderViewNamesDP")
    public Object[][] accToOrderViewNamesDP() {
        return new Object[][] { { "ACCOUNT_TO_ORDER" } };
    }

    @Test(dataProvider = "accToOrderViewNamesDP")
    public void accountToOrderViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "accToOrderColumnCheckDP")
    public Object[][] accToOrderColumnCheckDP() {
        return new Object[][] { {
                "ACCOUNT_TO_ORDER",
                new String[] { "HURRICAN_AUFTRAG_ID", "TAIFUN_ORDER__NO", "AUFTRAG_STATUS_ID", "AUFTRAG_STATUS",
                        "TECH_PRODUKT", "ACCOUNT", "VPN_ID", "VERBINDUNGSBEZ" } } };
    }

    @Test(dataProvider = "accToOrderColumnCheckDP")
    public void accountToOrderColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
