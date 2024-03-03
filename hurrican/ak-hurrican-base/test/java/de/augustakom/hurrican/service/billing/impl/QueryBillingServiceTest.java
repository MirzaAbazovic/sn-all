/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 14:04:19
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.billing.QueryBillingService;


/**
 * Service Test for QueryBillingService
 */
@Test(groups = BaseTest.SERVICE)
public class QueryBillingServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(QueryBillingServiceTest.class);

    /**
     * Test method for 'de.augustakom.hurrican.service.billing.impl.QueryBillingServiceImpl.query(String, Object[])'
     */
    @Test
    public void testQuerySql() throws Exception {
        String sql = "SELECT d.ORDER__NO, d.DN_NO, d.DN__NO, d.VALID_FROM, d.VALID_TO, d.HIST_CNT, "
                + "d.HIST_LAST, d.BLOCK__NO, d.ONKZ, d.DN_BASE, d.DIRECT_DIAL, d.RANGE_FROM, d.RANGE_TO, "
                + "d.STATE, d.OE__NO, o.PRODUKTCODE, d.PORT_MODE, d.REMARKS, d.USERW, d.DATEW "
                + "FROM DN d, OE o "
                + "WHERE d.OE__NO=o.OE__NO "
                + "AND d.ORDER__NO IS NULL "
                + "AND d.VALID_FROM <= sysdate AND d.VALID_TO > sysdate "
                + "AND d.OE__NO=65";
        QueryBillingService service = getBillingService(QueryBillingService.class);
        List<Map<String, Object>> result = service.query(sql, new String[] { "ORDER__NO", "DN_NO", "DN__NO", "VALID_FROM",
                "VALID_TO", "HIST_CNT", "HIST_LAST", "BLOCK__NO", "ONKZ", "DN_BASE", "DIRECT_DIAL", "RANGE_FROM", "RANGE_TO",
                "STATE", "OE__NO", "PRODUKTCODE", "PORT_MODE", "REMARKS", "USERW", "DATEW" }, null);
        assertNotEmpty(result, "Kein RESULT!");
        LOGGER.debug("<<<<< result-size: " + result.size());

    }
}


