/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.02.2015
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class HurricanSipPeeringPartnerTest extends AbstractViewsTest {

    @DataProvider
    public Object[][] hurricanInternalViewNamesDP() {
        return new Object[][] {
                { "V_HURRICAN_SIP_PP_IPS" },
        };
    }

    @Test(dataProvider = "hurricanInternalViewNamesDP")
    public void hurricanInternalViewsValid(String viewName) {
        getJdbcTemplate().queryForObject(createSelectRowCountSQL(viewName), Integer.class);
    }

    @DataProvider
    public Object[][] hurricanInternalViewsColumnCheckDP() {
        return new Object[][] {
                {
                        "V_HURRICAN_SIP_PP_IPS",
                        new String[] {
                                "AUFTRAG_ID", "PEERING_PARTNER_NAME", "GUELTIG_VON",
                                "GUELTIG_BIS", "IP_ADDRESS", "ADDRESS_TYPE",
                        }
                },
        };
    }

    @Test(dataProvider = "hurricanInternalViewsColumnCheckDP")
    public void hurricanInternalViewsColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
