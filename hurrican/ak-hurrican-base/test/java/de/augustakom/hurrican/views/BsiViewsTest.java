/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2011 11:15:53
 */
package de.augustakom.hurrican.views;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * Replacement for FitNesse-Tests Tests the BsiViews
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class BsiViewsTest extends AbstractViewsTest {

    @DataProvider(name = "bsiViewNamesDP")
    public Object[][] bsiViewNamesDP() {
        return new Object[][] { { "V_MIG_GETDBLINK4CURRENTUSER" }, { "V_HURRICAN_ACCESSPOINT" },
                { "V_HURRICAN_ACCOUNTS" }, { "V_HURRICAN_ADDRESS" }, { "V_HURRICAN_AQS" },
                { "V_HURRICAN_CARRIER_CONTACT" }, { "V_HURRICAN_CARRIER_KENNUNG" }, { "V_HURRICAN_CARRIER_MAPPING" },
                { "V_HURRICAN_COMBINED_SEARCH" }, { "V_HURRICAN_CONTACT_PERSON" }, { "V_HURRICAN_CROSS_CONNECTION" },
                { "V_HURRICAN_CUSTOMER_LOCKS" }, { "V_HURRICAN_DEVICE_ACL" }, { "V_HURRICAN_DEVICE_CFG_PORT" },
                { "V_HURRICAN_DEVICE_CONFIG" }, { "V_HURRICAN_DEVICE_IP" }, { "V_HURRICAN_DEVICE_ROUTING" },
                { "V_HURRICAN_DEVICE" }, { "V_HURRICAN_DN_SERVICES" }, { "V_HURRICAN_DSLAM_PROFILE" },
                { "V_HURRICAN_GEO_2_ASB" }, { "V_HURRICAN_IP" }, { "V_HURRICAN_PHYSIC_HISTORY" },
                { "V_HURRICAN_PHYSIC_LIST" }, { "V_HURRICAN_PHYSIC" }, { "V_HURRICAN_QOS" },
                { "V_HURRICAN_SERVICE_PROVIDER" }, { "V_HURRICAN_TAL_ORDER_DETAIL" }, { "V_HURRICAN_TAL_ORDER" },
                { "V_HURRICAN_TECH_ORDER_BASE" }, { "V_HURRICAN_TECH_PROCESS_HIST" }, { "V_HURRICAN_TECH_SERVICES" },
                { "V_HURRICAN_VOIP" }, { "V_HURRICAN_VPN" },
                { "V_HURRICAN_VOIP_DN_PLAN" }, { "V_HURRICAN_DTAG_CB" }, { "V_HURRICAN_FTTX_DEVICES" } };
    }

    @Test(dataProvider = "bsiViewNamesDP")
    public void bsiViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "bsiColumnCheckDP")
    public Object[][] bsiColumnCheckDP() {
        return new Object[][] {
                { "V_HURRICAN_GEO_2_ASB", new String[] { "ASB", "GEO_ID", "ONKZ", "CLUSTER_ID", "AREA_NO" } },
                { "V_HURRICAN_VOIP_DN_PLAN",
                        new String[] { "TECH_ORDER_ID", "DN__NO", "GUELTIG_AB", "ANFANG", "ENDE", "ZENTRALE" } },
                { "V_HURRICAN_VOIP",
                        new String[] { "TECH_ORDER_ID", "TAIFUN_DN__NO", "SIP_PASSWORD", "EG_MODE_ID",
                                "EG_MODE", "IS_ACTIVE", "SIP_DOMAIN", "SIP_LOGIN", "SIP_HAUPTRUFNUMMER" } },
                { "V_HURRICAN_DTAG_CB",
                        new String[] { "TAIFUN_ORDER__NO", "DTAG_KUNDE_NR", "DTAG_LEISTUNGS_NR", "BESTELLER_KUNDE_NR",
                                "BESTELLER_LEISTUNGS_NR", "PROD_BEZEICHNER", "VERTRAGSNUMMER", "ENDSTELLE_NAME",
                                "LAGE_TAE", "LBZ" } },
                { "V_HURRICAN_FTTX_DEVICES",
                        new String[] { "NAME", "PRODUCER", "TYPE", "PORT", "PORT_TYPE", "SERIAL_NO", "OLT_PORT",
                                "OLT_NAME", "OLT_PRODUCER", "TECH_ORDER_ID" }
                },
                { "V_HURRICAN_PHYSIC",
                        new String[] { "RANGIER_ID", "ACCESSPOINT_ID", "EQ_IN_ID", "EQ_OUT_ID", "TECH_LOCATION_ID",
                                "RANGIER_VALID_FROM", "RANGIER_VALID_TO", "PHYSIC_TYPE_ID", "PHYSIK_TYPE", "HW_PORT", "HW_EQN",
                                "HW_INTERFACE", "RANG_VERTEILER", "RANG_REIHE", "RANG_BUCHT", "RANG_LEISTE1",
                                "RANG_STIFT1", "RANG_LEISTE2", "RANG_STIFT2", "RANG_SS_TYPE", "UETV", "CARRIER",
                                "V5_PORT", "KVZ_NUMMER", "KVZ_DOPPELADER", "LAYER2_PROTOCOL", "MEDIAGATEWAY", "DSLAM_ID",
                                "DSLAM_NAME", "ERX_LOCATION", "DSLAM_IP", "DSLAM_SLOT_PORT", "HARDWARE_BG_NAME",
                                "HARDWARE_BG_SCHNITTSTELLE", "DSLAM_PRODUCER", "SWITCH", "PSE", "ACTIVE_ON_ORDER",
                                "IS_EQ_IN", "IS_EQ_OUT"
                        }
                },
                { "V_HURRICAN_PHYSIC_LIST",
                        new String[] { "TAIFUN_ORDER__NO", "TECH_ORDER_ID", "TECH_LOCATION_ID", "PORT_ID", "HW_EQN",
                                "DSLAM_NAME", "SWITCH", "PORT_INFO", "LBZ", "VTRNR", "CARRIER_ID", "CARRIER_KENNUNG_ID",
                                "CARRIER_CONTACT_ID", "CARRIER_MAPPING_ID", "ONT_ID", "PSE"
                        }
                },
                { "V_HURRICAN_PHYSIC_HISTORY",
                        new String[] { "RANGIER_ID", "EQ_IN_ID", "EQ_OUT_ID", "TECH_LOCATION_ID", "RANGIER_VALID_FROM",
                                "RANGIER_VALID_TO", "PHYSIC_TYPE_ID", "PHYSIK_TYPE", "HW_PORT", "HW_EQN", "HW_INTERFACE",
                                "RANG_VERTEILER", "RANG_REIHE", "RANG_BUCHT", "RANG_LEISTE1", "RANG_STIFT1", "RANG_LEISTE2",
                                "RANG_STIFT2", "RANG_SS_TYPE", "UETV", "CARRIER", "V5_PORT", "MEDIAGATEWAY", "DSLAM_ID",
                                "DSLAM_NAME", "ERX_LOCATION", "DSLAM_IP", "DSLAM_SLOT_PORT", "DSLAM_PRODUCER", "SWITCH",
                                "PSE", "IS_EQ_IN", "IS_EQ_OUT", "HISTORY_FROM_RID", "HISTORY_COUNT"
                        }
                }
        };
    }

    @Test(dataProvider = "bsiColumnCheckDP")
    public void bsiColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }

    @DataProvider(name = "zeroResultCountQueryDP")
    public Object[][] zeroResultCountQueryDP() {
        return new Object[][] {
                { "select count(*) as VIEW_WORKS from V_HURRICAN_AQS where rownum=1 and GEO_ID=0" },
                { "select count(*) as VIEW_WORKS from V_HURRICAN_GEO_2_ASB where rownum=1 and ASB<0" }
        };
    }

    @Test(dataProvider = "zeroResultCountQueryDP")
    public void zeroResultCountQueryCheck(String sql) {
        int count = getJdbcTemplate().queryForInt(sql);
        Assert.assertEquals(count, 0);
    }
}
