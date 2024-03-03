/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class StornoAufhebungAufAnfrageMarshallerTest {

    @Mock
    private EKPGeschaeftsfallMarshaller ekpMarshallerMock;
    @Mock
    private CarrierIdentifikatorMarshaller ciMarshallerMock;

    @InjectMocks
    private StornoAufhebungAufAnfrageMarshaller testling = new StornoAufhebungAufAnfrageMarshaller();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(ekpMarshallerMock.apply(Matchers.any(WbciGeschaeftsfall.class))).thenReturn(new EKPType());
        Mockito.when(ciMarshallerMock.apply(Matchers.any(CarrierCode.class))).thenReturn(new CarrierIdentifikatorType());
    }

    @Test
    public void testApply() throws Exception {
        StornoAufhebungAufAnfrage stornoAnfrage = new StornoAufhebungAufAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        StornoAufhebungEKPaufType result = testling.apply(stornoAnfrage);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getAbsender());
        Assert.assertNotNull(result.getEmpfaenger());
        Assert.assertNotNull(result.getEndkundenvertragspartner());
        Assert.assertEquals(result.getStornoId(), stornoAnfrage.getAenderungsId());
        Assert.assertEquals(result.getVorabstimmungsIdRef(), stornoAnfrage.getVorabstimmungsIdRef());
    }

}
