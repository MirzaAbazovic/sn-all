/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.unmarshal.v1;

import static org.mockito.Mockito.*;

import javax.xml.bind.*;
import javax.xml.transform.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.CancelCarrierChangeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RequestCarrierChangeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RescheduleCarrierChangeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.UpdateCarrierChangeTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MessageUnmarshallerTest {
    @Mock
    private Jaxb2Marshaller unmarshaller;
    @Mock
    private UpdateCarrierChangeUnmarshaller updateCarrierChangeUnmarshallerMock;
    @Mock
    private RequestCarrierChangeUnmarshaller requestCarrierChangeUnmarshallerMock;
    @Mock
    private RescheduleCarrierChangeUnmarshaller rescheduleCarrierChangeUnmarshallerMock;
    @Mock
    private CancelCarrierChangeUnmarshaller cancelCarrierChangeUnmarshaller;

    @InjectMocks
    private MessageUnmarshaller testling;

    @Mock
    private Source source;
    @Mock
    private JAXBElement jaxbElement;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(unmarshaller.unmarshal(source)).thenReturn(jaxbElement);
    }

    @Test(expectedExceptions = { UnmarshallingFailureException.class })
    public void testUnmarshalNull() throws Exception {
        testling.unmarshal(null);
    }

    @Test
    public void testUnmarshalUpdateCarrierChange() throws Exception {
        UpdateCarrierChange testObject = new UpdateCarrierChangeTestBuilder().buildValid(MeldungTyp.RUEM_VA,
                GeschaeftsfallEnumType.VA_KUE_MRN);
        when(unmarshaller.unmarshal(source)).thenReturn(testObject);
        testling.unmarshal(source);
        verify(updateCarrierChangeUnmarshallerMock, atLeastOnce()).apply(testObject);
    }

    @Test
    public void testUnmarshalRequestCarrierChange() throws Exception {
        RequestCarrierChange testObject = new RequestCarrierChangeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        when(unmarshaller.unmarshal(source)).thenReturn(testObject);
        testling.unmarshal(source);
        verify(requestCarrierChangeUnmarshallerMock, atLeastOnce()).apply(testObject);
    }

    @Test
    public void testUnmarshalRescheduleCarrierChange() throws Exception {
        RescheduleCarrierChange testObject = new RescheduleCarrierChangeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        when(unmarshaller.unmarshal(source)).thenReturn(testObject);
        testling.unmarshal(source);
        verify(rescheduleCarrierChangeUnmarshallerMock, atLeastOnce()).apply(testObject);
    }

    @Test
    public void testUnmarshalCancelCarrierChange() throws Exception {
        CancelCarrierChange testObject = new CancelCarrierChangeTestBuilder().buildValid(RequestTyp.STR_AUFH_AUF);
        when(unmarshaller.unmarshal(source)).thenReturn(testObject);
        testling.unmarshal(source);
        verify(cancelCarrierChangeUnmarshaller, atLeastOnce()).apply(testObject);
    }
}
