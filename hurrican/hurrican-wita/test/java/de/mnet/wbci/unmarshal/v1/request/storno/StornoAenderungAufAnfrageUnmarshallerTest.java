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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPaufType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.StornoAenderungEKPaufTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.PersonOderFirmaUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.StandortUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class StornoAenderungAufAnfrageUnmarshallerTest {

    @Mock
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshallerMock;
    CarrierCode absender;

    @Mock
    private PersonOderFirmaUnmarshaller personOderFirmaUnmarshaller;
    Person personOderFirma;

    @Mock
    private StandortUnmarshaller standortUnmarshaller;
    Standort standort;

    @InjectMocks
    private StornoAenderungAufAnfrageUnmarshaller testling = new StornoAenderungAufAnfrageUnmarshaller();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        personOderFirma = new PersonTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(personOderFirmaUnmarshaller.apply(any(PersonOderFirmaType.class))).thenReturn(personOderFirma);

        standort = new StandortTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(standortUnmarshaller.apply(any(StandortType.class))).thenReturn(standort);

        absender = CarrierCode.DTAG;
        when(carrierIdentifikatorUnmarshallerMock.apply(any(CarrierIdentifikatorType.class))).thenReturn(absender);
    }

    @Test
    public void testApply() throws Exception {
        StornoAenderungEKPaufType input = new StornoAenderungEKPaufTypeTestBuilder().buildValid();
        StornoAenderungAufAnfrage anfrage = testling.apply(input);

        Assert.assertEquals(anfrage.getAenderungsId(), input.getStornoId());
        Assert.assertEquals(anfrage.getVorabstimmungsIdRef(), input.getVorabstimmungsIdRef());
        Assert.assertEquals(anfrage.getTyp(), RequestTyp.STR_AEN_AUF);
        Assert.assertEquals(anfrage.getStandort(), standort);
        Assert.assertEquals(anfrage.getEndkunde(), personOderFirma);
    }
}
