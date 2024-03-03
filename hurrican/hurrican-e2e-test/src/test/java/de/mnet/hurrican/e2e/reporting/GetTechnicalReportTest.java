/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.14 12:43
 */
package de.mnet.hurrican.e2e.reporting;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNot.*;
import static org.mockito.Matchers.*;

import javax.annotation.*;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.GetTechnicalReportRequest;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.GetTechnicalReportResponse;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;

/**
 *
 */
@Test(groups = E2E)
public class GetTechnicalReportTest extends BaseHurricanE2ETest {

    @Resource(name = "resourceReportingWebServiceTemplate")
    protected WebServiceTemplate webServiceTemplate;

    public void testGetTechnicalReport() throws Exception {
        final GetTechnicalReportRequest request = new GetTechnicalReportRequest();
        request.setTechnicalOrderId(1L);

        final GetTechnicalReportResponse response =
                (GetTechnicalReportResponse) webServiceTemplate.marshalSendAndReceive(request);

        assertThat(response.getReport().length, not(eq(0)));
    }

    public void testGetTechnicalReportWithUnknownTechOrderId() throws Exception {
        final GetTechnicalReportRequest request = new GetTechnicalReportRequest();
        request.setTechnicalOrderId(-1L);

        final GetTechnicalReportResponse response =
                (GetTechnicalReportResponse) webServiceTemplate.marshalSendAndReceive(request);

        assertThat(response.getReport().length, equalTo(0));
    }

}
