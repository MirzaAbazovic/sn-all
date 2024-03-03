/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.marshal.location;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.xml.transform.StringResult;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.builder.cdm.location.v1.SearchBuildingsTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class LocationServiceMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private LocationServiceMarshaller testling;

    @Test
    public void testMarshal() throws Exception {
        SearchBuildings searchRequest = new SearchBuildingsTestBuilder().build();

        StringResult result = new StringResult();
        testling.marshal(searchRequest, result);

        assertThat(result.toString(), notNullValue());

        assertSchemaValidLocationService(result.toString());
    }
}
