/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPabgType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPaufType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.marshal.v1.request.storno.StornoAenderungAbgAnfrageMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAenderungAufAnfrageMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAufhebungAbgAnfrageMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAufhebungAufAnfrageMarshaller;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CancelCarrierChangeMarshallerTest extends AbstractWbciMarshallerTest {
    @Mock
    private StornoAenderungAbgAnfrageMarshaller stornoAenderungAbgAnfrageMarshallerMock;

    @Mock
    private StornoAenderungAufAnfrageMarshaller stornoAenderungAufAnfrageMarshallerMock;

    @Mock
    private StornoAufhebungAbgAnfrageMarshaller stornoAufhebungAbgAnfrageMarshallerMock;

    @Mock
    private StornoAufhebungAufAnfrageMarshaller stornoAufhebungAufAnfrageMarshallerMock;

    @InjectMocks
    private CancelCarrierChangeMarshaller testling = new CancelCarrierChangeMarshaller();

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApplyStornoAenderungAbgAnfrage() {
        StornoAenderungAbgAnfrage stornoAnfrage = new StornoAenderungAbgAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        Mockito.when(stornoAenderungAbgAnfrageMarshallerMock.apply(stornoAnfrage)).thenReturn(new StornoAenderungEKPabgType());

        CancelCarrierChange result = testling.apply(stornoAnfrage);
        Assert.assertNotNull(result);

        Mockito.verify(stornoAenderungAbgAnfrageMarshallerMock, times(1)).apply(stornoAnfrage);
    }

    @Test
    public void testApplyStornoAenderungAufAnfrage() {
        StornoAenderungAufAnfrage stornoAnfrage = new StornoAenderungAufAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        Mockito.when(stornoAenderungAufAnfrageMarshallerMock.apply(stornoAnfrage)).thenReturn(new StornoAenderungEKPaufType());

        CancelCarrierChange result = testling.apply(stornoAnfrage);
        Assert.assertNotNull(result);

        Mockito.verify(stornoAenderungAufAnfrageMarshallerMock, times(1)).apply(stornoAnfrage);
    }

    @Test
    public void testApplyStornoAufhebungAbgAnfrage() {
        StornoAufhebungAbgAnfrage stornoAnfrage = new StornoAufhebungAbgAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        Mockito.when(stornoAufhebungAbgAnfrageMarshallerMock.apply(stornoAnfrage)).thenReturn(new StornoAufhebungEKPabgType());

        CancelCarrierChange result = testling.apply(stornoAnfrage);
        Assert.assertNotNull(result);

        Mockito.verify(stornoAufhebungAbgAnfrageMarshallerMock, times(1)).apply(stornoAnfrage);
    }

    @Test
    public void testApplyStornoAufhebungAufAnfrage() {
        StornoAufhebungAufAnfrage stornoAnfrage = new StornoAufhebungAufAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        Mockito.when(stornoAufhebungAufAnfrageMarshallerMock.apply(stornoAnfrage)).thenReturn(new StornoAufhebungEKPaufType());

        CancelCarrierChange result = testling.apply(stornoAnfrage);
        Assert.assertNotNull(result);

        Mockito.verify(stornoAufhebungAufAnfrageMarshallerMock, times(1)).apply(stornoAnfrage);
    }

    @Test
    public void testApplyWithNullArgument() {
        StornoAufhebungAufAnfrage stornoAnfrage = null;
        CancelCarrierChange result = testling.apply(stornoAnfrage);
        Assert.assertNull(result);
    }

}
