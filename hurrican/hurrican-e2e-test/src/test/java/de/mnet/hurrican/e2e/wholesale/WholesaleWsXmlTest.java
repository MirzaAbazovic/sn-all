/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2012 15:24:30
 */
package de.mnet.hurrican.e2e.wholesale;

import static de.augustakom.common.BaseTest.*;

import java.time.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import com.google.common.collect.ImmutableMap;
import org.springframework.xml.transform.StringSource;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;

@Test(groups = E2E, enabled = false)
public class WholesaleWsXmlTest extends BaseWholesaleXmlE2ETest {

    @DataProvider
    public Object[][] xmlFiles() {
        return new Object[][] {
                { today(), "wholesale/request/getOrderParameters.xml" },
                { today(), "wholesale/request/releasePort.xml" },
                { today(), "wholesale/request/modifyPort.xml" },
                { tomorrow(), "wholesale/request/modifyPortReservationDate.xml" },
        };
    }

    @Test(dataProvider = "xmlFiles", enabled = false)
    public void xmlShouldWork(LocalDate desiredExecutionDate, String fileName) throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        state.reservePort(desiredExecutionDate);

        String xmlString = replaceProperties(fileName,
                ImmutableMap.<String, String>builder().putAll(
                        getProperties(standortData, ekpData))
                        .put("lineId", state.lineId)
                        .put("desiredModifyDate", today().plusDays(3).toString())
                        .put("desiredModifyPortReservationDate", today().plusDays(2).toString())
                        .put("getOrderParamsExecutionDate", today().toString())
                        .build()
        );
        StreamSource source = new StringSource(xmlString);
        DOMResult result = new DOMResult();

        webServiceTemplate.sendSourceAndReceiveToResult(source, result);
    }

    public void reservePortXmlTest() throws Exception {
        String xmlString = replaceProperties("wholesale/request/reservePort.xml", getProperties(standortData, ekpData));

        StreamSource source = new StringSource(xmlString);
        DOMResult result = new DOMResult();

        webServiceTemplate.sendSourceAndReceiveToResult(source, result);
    }

}
