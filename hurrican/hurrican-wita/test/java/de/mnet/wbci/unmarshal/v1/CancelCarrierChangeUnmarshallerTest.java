/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.CancelCarrierChangeTestBuilder;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAenderungAbgAnfrageUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAenderungAufAnfrageUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAufhebungAbgAnfrageUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAufhebungAufAnfrageUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CancelCarrierChangeUnmarshallerTest {

    @Mock
    private StornoAufhebungAufAnfrageUnmarshaller stornoAufhebungAufAnfrageUnmarshaller;
    @Mock
    private StornoAufhebungAbgAnfrageUnmarshaller stornoAufhebungAbgAnfrageUnmarshaller;
    @Mock
    private StornoAenderungAbgAnfrageUnmarshaller stornoAenderungAbgAnfrageUnmarshaller;
    @Mock
    private StornoAenderungAufAnfrageUnmarshaller stornoAenderungAufAnfrageUnmarshaller;

    @InjectMocks
    private CancelCarrierChangeUnmarshaller testling;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplySTRAUFREC() throws Exception {
        CancelCarrierChange testdata = new CancelCarrierChangeTestBuilder()
                .buildValid(RequestTyp.STR_AUFH_AUF);

        when(stornoAufhebungAufAnfrageUnmarshaller.apply(testdata.getSTRAUFREC())).thenReturn(new StornoAufhebungAufAnfrage());
        StornoAnfrage result = testling.apply(testdata);
        Assert.assertTrue(result instanceof StornoAufhebungAufAnfrage);
    }

    @Test
    public void testApplySTRAUFDON() throws Exception {
        CancelCarrierChange testdata = new CancelCarrierChangeTestBuilder()
                .buildValid(RequestTyp.STR_AUFH_ABG);

        when(stornoAufhebungAbgAnfrageUnmarshaller.apply(testdata.getSTRAUFDON())).thenReturn(new StornoAufhebungAbgAnfrage());
        StornoAnfrage result = testling.apply(testdata);
        Assert.assertTrue(result instanceof StornoAufhebungAbgAnfrage);
    }

    @Test
    public void testApplySTRAENREC() throws Exception {
        CancelCarrierChange testdata = new CancelCarrierChangeTestBuilder()
                .buildValid(RequestTyp.STR_AEN_AUF);

        when(stornoAenderungAufAnfrageUnmarshaller.apply(testdata.getSTRAENREC())).thenReturn(new StornoAenderungAufAnfrage());
        StornoAnfrage result = testling.apply(testdata);
        Assert.assertTrue(result instanceof StornoAenderungAufAnfrage);
    }

    @Test
    public void testApplySTRAENDON() throws Exception {
        CancelCarrierChange testdata = new CancelCarrierChangeTestBuilder()
                .buildValid(RequestTyp.STR_AEN_ABG);

        when(stornoAenderungAbgAnfrageUnmarshaller.apply(testdata.getSTRAENDON())).thenReturn(new StornoAenderungAbgAnfrage());
        StornoAnfrage result = testling.apply(testdata);
        Assert.assertTrue(result instanceof StornoAenderungAbgAnfrage);
    }
}
