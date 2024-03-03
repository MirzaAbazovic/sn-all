/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2011 14:23:21
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class HurricanInternalViewsTest extends AbstractViewsTest {

    @DataProvider(name = "hurricanInternalViewNamesDP")
    public Object[][] hurricanInternalViewNamesDP() {
        return new Object[][] { { "V_HW_BAUGRUPPEN" }, { "V_HW_BG_PORT2PORT" }, { "V_HW_BG_PORT2PORT_DETAIL" } };
    }

    @Test(dataProvider = "hurricanInternalViewNamesDP")
    public void hurricanInternalViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "hurricanInternalViewsColumnCheckDP")
    public Object[][] hurricanInternalViewsColumnCheckDP() {
        return new Object[][] {
                {
                        "V_HW_BAUGRUPPEN",
                        new String[] { "HVT_ID_STANDORT", "ANLAGENBEZ", "BG_MOD_NUMBER", "EINGEBAUT", "GERAETEBEZ",
                                "HERSTELLER", "HW_BAUGRUPPEN_ID", "HW_BAUGRUPPEN_TYP", "NAME", "RACK_ID", "RACK_TYP",
                                "SUBRACK_MOD_NUMBER", "SUBRACK_TYP" } },
                {
                        "V_HW_BG_PORT2PORT",
                        new String[] { "AUFTRAG_ID", "AUFTRAG_STATUS", "EQ_ID_NEW", "EQ_ID_OLD",
                                "EQ_NEW_MANUAL_CONFIGURATION", "EQ_OLD_MANUAL_CONFIGURATION", "HW_BG_CHANGE_ID",
                                "HW_EQN_NEW", "HW_EQN_OLD", "LAST_SUCCESSFUL_CPS_TX", "PORT_STATE_OLD", "PORT2PORT_ID",
                                "PRODUKT_NAME", "TAIFUN_ORDER_NO", "VPN_NR" } },
                {
                        "V_HW_BG_PORT2PORT_DETAIL",
                        new String[] { "EQ_ID_NEW", "EQ_ID_OLD", "HW_BG_CHANGE_ID", "HW_EQN_NEW", "HW_EQN_OLD",
                                "PORT2PORT_ID", "RANG_BUCHT_NEW", "RANG_BUCHT_OLD", "RANG_LEISTE1_NEW",
                                "RANG_LEISTE1_OLD", "RANG_STIFT1_NEW", "RANG_STIFT1_OLD", "RANG_VERTEILER_NEW",
                                "RANG_VERTEILER_OLD" } } };
    }

    @Test(dataProvider = "hurricanInternalViewsColumnCheckDP")
    public void hurricanInternalViewsColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
