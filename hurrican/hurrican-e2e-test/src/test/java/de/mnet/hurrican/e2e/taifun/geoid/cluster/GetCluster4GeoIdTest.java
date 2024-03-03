/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2012 15:24:30
 */
package de.mnet.hurrican.e2e.taifun.geoid.cluster;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import javax.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdRequest;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdRequestDocument;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdResponseDocument;

@Test(groups = E2E)
public class GetCluster4GeoIdTest extends BaseHurricanE2ETest {

    @Resource(name = "taifunWebServiceTemplate")
    protected WebServiceTemplate taifunWebServiceTemplate;

    @Test
    public void getCluster4GeoIdRequest() throws Exception {
        GetCluster4GeoIdRequestDocument requestDocument = GetCluster4GeoIdRequestDocument.Factory.newInstance();
        GetCluster4GeoIdRequest request = requestDocument.addNewGetCluster4GeoIdRequest();
        request.setGeoId(Long.valueOf(3799176));

        GetCluster4GeoIdResponseDocument responseDocument = (GetCluster4GeoIdResponseDocument) taifunWebServiceTemplate.marshalSendAndReceive(requestDocument);
        assertNotNull(responseDocument);
        assertNotNull(responseDocument.getGetCluster4GeoIdResponse());
        assertTrue(responseDocument.getGetCluster4GeoIdResponse().getAreaNo() > 0);
    }


}
