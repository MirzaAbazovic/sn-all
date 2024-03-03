/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 15:40:53
 */
package de.mnet.hurrican.e2e.wholesale;

import static de.augustakom.common.BaseTest.*;

import java.util.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import com.google.common.collect.ImmutableMap;
import org.springframework.xml.transform.StringSource;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;

@Test(groups = E2E, enabled = false)
public class WholesaleFaultClearanceXmlTest extends BaseWholesaleXmlE2ETest {
    @DataProvider
    public Object[][] xmlFiles() {
        return new Object[][] {
                { "wholesale/request/getAvailablePorts.xml" },
                { "wholesale/request/getVdslProfiles.xml" },
        };
    }

    @Test(dataProvider = "xmlFiles", enabled = false)
    public void xmlShouldWork(String fileName) throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        state.reservePort(today());

        String xmlString = replaceProperties(fileName,
                ImmutableMap.<String, String>builder().putAll(
                        getProperties(standortData, ekpData))
                        .put("lineId", state.lineId)
                        .put("validFrom", DateTools.formatDate(new Date(), "yyyy-MM-dd"))
                        .build()
        );
        StreamSource source = new StringSource(xmlString);
        DOMResult result = new DOMResult();

        webServiceTemplate.sendSourceAndReceiveToResult(source, result);
    }

}


