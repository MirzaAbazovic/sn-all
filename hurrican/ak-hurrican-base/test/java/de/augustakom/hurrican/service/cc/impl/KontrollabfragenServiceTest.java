/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2010 12:00:00
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.DBQueryDef;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.QueryBillingService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ScvViewService;

/**
 * Test-Case fuer <code>ScvViewService</code>.
 *
 *
 */
@Test(groups = { "nightly" })
public class KontrollabfragenServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(KontrollabfragenServiceTest.class);

    /**
     * Führt alle SQLs zu Kontrollabfragen aus und testet jeweils, ob das Statement fehlerfrei ist.
     *
     * @throws FindException
     */
    public void testRunAMKontrollabfragenSQL() throws FindException {
        ScvViewService service = getScvViewService();
        List<DBQueryDef> queryDefs = service.findDBQueryDefs();
        Collection<DBQueryDef> queriesToCheck = Collections2.filter(queryDefs, new com.google.common.base.Predicate<DBQueryDef>() {
            @Override
            public boolean apply(DBQueryDef input) {
                return !BooleanTools.nullToFalse(input.getNotForTest());
            }
        });

        int i = 0;
        for (DBQueryDef query : queriesToCheck) {
            assertTrue(query.isCCQuery() || query.isBillingQuery(),
                    "Es werden z.Z. nur Abfragen für das Billing- und CC-System unterstuetzt.");
            Object[] queryParams = createDummyQueryParams(query);
            LOGGER.info("Abfrage " + ++i + " von " + queryDefs.size() + ": "
                    + query.getDescription());
            StringBuilder sql = new StringBuilder();
            // Nur testen, ob SQL korrekt ist, Result irrelevant!
            if (query.getSqlQuery().toLowerCase().indexOf("order by") > 0) {
                // Funktioniert mit ORDER BY, ist aber langsamer
                sql.append("SELECT DISTINCT(1) FROM (");
            }
            else {
                // Funktioniert mit ORDER BY nicht
                sql.append("SELECT 1 FROM DUAL WHERE EXISTS (");
            }
            sql.append(query.getSqlQuery());
            sql.append(")");
            if (query.isBillingQuery()) {
                QueryBillingService qs = getBillingService(QueryBillingService.class);
                qs.query(sql.toString(), new String[] {"COL1"}, queryParams);
            }
            else if (query.isCCQuery()) {
                QueryCCService qs = getCCService(QueryCCService.class);
                qs.query(sql.toString(), new String[] {"COL1"}, queryParams);
            }
        }
    }

    /**
     * Erzeugt Dummy-Parameter für die SQL-Abfrage: "" für Strings, 0 für Integer, (heute) für Dates
     *
     * @param query {@link DBQueryDef}
     * @return Dummy-Parameter
     */
    private Object[] createDummyQueryParams(final DBQueryDef query) {
        if (StringUtils.isNotBlank(query.getParams())) {
            final String[] paramDefs = StringUtils.split(query.getParams(),
                    DBQueryDef.PARAM_SEPARATOR);
            final Object[] queryParams = new Object[paramDefs.length];
            int i = 0;
            for (String s : paramDefs) {
                String[] nameType = StringUtils.split(s, DBQueryDef.PARAM_NAME_TYPE_SEPARATOR);
                // String paramName = nameType[0];
                String paramType = nameType[1];
                if (StringUtils.equalsIgnoreCase(paramType, DBQueryDef.PARAM_TYPE_STRING)) {
                    queryParams[i++] = "";
                }
                else if (StringUtils.equalsIgnoreCase(paramType, DBQueryDef.PARAM_TYPE_DATE)) {
                    queryParams[i++] = new Date();
                }
                else if (StringUtils.equalsIgnoreCase(paramType, DBQueryDef.PARAM_TYPE_INTEGER)) {
                    queryParams[i++] = Integer.valueOf(0);
                }
            }
            return queryParams;
        }
        else {
            return null;
        }
    }

    /* Gibt eine Instanz des ScvViewService zurueck. */
    private ScvViewService getScvViewService() {
        return getCCService(ScvViewService.class);
    }
}
