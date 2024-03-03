/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.terminverschiebung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.TerminverschiebungTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.PersonOderFirmaUnmarshaller;

@Test(groups = BaseTest.UNIT)
public class TerminverschiebungUnmarshallerTest {

    @Mock
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshallerMock;

    private CarrierCode absender;

    @Mock
    private PersonOderFirmaUnmarshaller personOderFirmaUnmarshallerMock;

    private PersonOderFirma personOderFirmaMock;

    @InjectMocks
    private TerminverschiebungUnmarshaller testling = new TerminverschiebungUnmarshaller();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        personOderFirmaMock = new Person();
        when(personOderFirmaUnmarshallerMock.apply(any(PersonOderFirmaType.class))).thenReturn(personOderFirmaMock);

        absender = CarrierCode.DTAG;
        when(carrierIdentifikatorUnmarshallerMock.apply(any(CarrierIdentifikatorType.class))).thenReturn(absender);
    }

    public void testApply() {
        TerminverschiebungType tv = new TerminverschiebungTypeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        TerminverschiebungsAnfrage result = testling.apply(tv);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getVorabstimmungsIdRef(), tv.getVorabstimmungsIdRef());
        Assert.assertEquals(result.getAenderungsId(), tv.getAenderungsId());
        Assert.assertEquals(result.getTvTermin(), DateConverterUtils.toDateTime(tv.getNeuerKundenwunschtermin()).toLocalDate());
        Assert.assertEquals(result.getEndkunde(), personOderFirmaMock);
        Assert.assertEquals(result.getAbsender(), absender);
    }

}
