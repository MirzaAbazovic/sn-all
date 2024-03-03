/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 08:04:37
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class ServiceportalViewsTest extends AbstractViewsTest {

    @DataProvider(name = "servicePortalViewNamesDP")
    public Object[][] servicePortalViewNamesDP() {
        return new Object[][] { { "V_ASB_PRO_DSLAM_EWSD_MDU" }, { "V_HARDWARE_PRO_ASB" }, { "V_KUNDE_PRO_BAUGRUPPE" },
                { "V_KUNDE_PRO_OLTPORT_MDU" }, { "V_KUNDE_PRO_OLTPORT" }, { "V_OLT_U_PORTS" } };
    }

    @Test(dataProvider = "servicePortalViewNamesDP")
    public void servicePortalViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "accToOrderColumnCheckDP")
    public Object[][] accToOrderColumnCheckDP() {
        return new Object[][] {
                {
                        "V_ASB_PRO_DSLAM_EWSD_MDU",
                        new String[] { "ONKZ", "ASB", "HVT_ID_STANDORT", "GERAETEBEZ", "MANAGEMENTBEZ", "RACK_TYP",
                                "DLU_NUMBER", "SWITCH" } },
                {
                        "V_KUNDE_PRO_BAUGRUPPE",
                        new String[] { "TECH_STATUS", "TAIFUN_ORDER__NO", "PLANNED_START", "REAL_DATE", "CANCEL_DATE",
                                "ANSCHLUSSART", "PROD_ID", "RACK_ID", "BAUGRUPPE_ID", "VERBINDUNGSBEZEICHNUNG" } },
                {
                        "V_HARDWARE_PRO_ASB",
                        new String[] { "ID", "RACK_TYP", "ANLAGENBEZ", "GERAETEBEZ", "HVT_ID_STANDORT", "HVT_RAUM_ID",
                                "HW_PRODUCER", "GUELTIG_VON", "GUELTIG_BIS", "MANAGEMENTBEZ", "VERSION", "SWITCH",
                                "DLU_NUMBER", "BG_ID", "MOD_NUMBER", "BG_NAME" } },
                {
                        "V_KUNDE_PRO_OLTPORT_MDU",
                        new String[] { "TECH_STATUS", "TAIFUN_ORDER__NO", "PLANNED_START", "REAL_DATE", "CANCEL_DATE",
                                "ANSCHLUSSART", "PROD_ID", "OLT_FRAME", "OLT_SHELF", "OLT_GPON_PORT", "GERAETEBEZ",
                                "VERBINDUNGSBEZEICHNUNG" } },
                {
                        "V_KUNDE_PRO_OLTPORT",
                        new String[] { "TECH_STATUS", "TAIFUN_ORDER__NO", "PLANNED_START", "REAL_DATE", "CANCEL_DATE",
                                "ANSCHLUSSART", "PROD_ID", "HW_EQN", "GERAETEBEZ", "VERBINDUNGSBEZEICHNUNG" } },
                { "V_OLT_U_PORTS",
                        new String[] { "GERAETEBEZ", "PORT" } } };
    }

    @Test(dataProvider = "accToOrderColumnCheckDP")
    public void accountToOrderColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
