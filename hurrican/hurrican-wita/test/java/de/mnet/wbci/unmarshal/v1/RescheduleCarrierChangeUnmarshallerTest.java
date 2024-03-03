/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.unmarshal.v1;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RescheduleCarrierChangeTestBuilder;
import de.mnet.wbci.unmarshal.v1.request.terminverschiebung.TerminverschiebungUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RescheduleCarrierChangeUnmarshallerTest {

    @Mock
    private TerminverschiebungUnmarshaller terminverschiebungUnmarshallerMock;

    @InjectMocks
    private RescheduleCarrierChangeUnmarshaller testling;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        RescheduleCarrierChange testdata = new RescheduleCarrierChangeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.TVS_VA);
        TerminverschiebungsAnfrage aspectedTV = new TerminverschiebungsAnfrage();
        when(terminverschiebungUnmarshallerMock.apply(testdata.getTVSVA())).thenReturn(aspectedTV);
        Assert.assertEquals(testling.apply(testdata), aspectedTV);
    }
}
