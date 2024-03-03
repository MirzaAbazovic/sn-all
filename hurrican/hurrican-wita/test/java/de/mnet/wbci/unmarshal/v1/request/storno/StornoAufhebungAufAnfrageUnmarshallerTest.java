/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.StornoAufhebungEKPaufTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class StornoAufhebungAufAnfrageUnmarshallerTest {

    @Mock
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshallerMock;
    private CarrierCode absender;

    @InjectMocks
    private StornoAufhebungAufAnfrageUnmarshaller testling = new StornoAufhebungAufAnfrageUnmarshaller();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        absender = CarrierCode.DTAG;
        when(carrierIdentifikatorUnmarshallerMock.apply(any(CarrierIdentifikatorType.class))).thenReturn(absender);
    }

    @Test
    public void testApply() throws Exception {
        StornoAufhebungEKPaufType input = new StornoAufhebungEKPaufTypeTestBuilder().buildValid();
        StornoAufhebungAufAnfrage anfrage = testling.apply(input);

        Assert.assertEquals(anfrage.getAenderungsId(), input.getStornoId());
        Assert.assertEquals(anfrage.getVorabstimmungsIdRef(), input.getVorabstimmungsIdRef());
        Assert.assertEquals(anfrage.getTyp(), RequestTyp.STR_AUFH_AUF);
    }
}
