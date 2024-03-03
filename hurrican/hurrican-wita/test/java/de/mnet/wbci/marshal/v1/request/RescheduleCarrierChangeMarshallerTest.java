/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.marshal.v1.request.terminverschiebung.TerminverschiebungMarshaller;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RescheduleCarrierChangeMarshallerTest extends AbstractWbciMarshallerTest {

    @Mock
    private TerminverschiebungMarshaller terminverschiebungMarshallerMock;

    @InjectMocks
    private RescheduleCarrierChangeMarshaller testling = new RescheduleCarrierChangeMarshaller();

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    public void testApply() {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        Mockito.when(terminverschiebungMarshallerMock.apply(tv)).thenReturn(new TerminverschiebungType());

        RescheduleCarrierChange result = testling.apply(tv);
        Assert.assertNotNull(result);

        Mockito.verify(terminverschiebungMarshallerMock, times(1)).apply(tv);
    }

}
