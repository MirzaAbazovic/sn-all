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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPabgType;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.entities.PersonOderFirmaMarshaller;
import de.mnet.wbci.marshal.v1.entities.StandortMarshaller;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class StornoAenderungAbgAnfrageMarshallerTest {

    @Mock
    private EKPGeschaeftsfallMarshaller ekpMarshallerMock;
    @Mock
    private CarrierIdentifikatorMarshaller ciMarshallerMock;
    @Mock
    private PersonOderFirmaMarshaller personOderFirmaMarshallerMock;
    @Mock
    private StandortMarshaller standortMarshallerMock;

    @InjectMocks
    private StornoAenderungAbgAnfrageMarshaller testling = new StornoAenderungAbgAnfrageMarshaller();


    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(ekpMarshallerMock.apply(Matchers.any(WbciGeschaeftsfall.class))).thenReturn(new EKPType());
        Mockito.when(ciMarshallerMock.apply(Matchers.any(CarrierCode.class))).thenReturn(new CarrierIdentifikatorType());
        Mockito.when(personOderFirmaMarshallerMock.apply(Matchers.any(PersonOderFirma.class))).thenReturn(new PersonOderFirmaType());
        Mockito.when(standortMarshallerMock.apply(Matchers.any(Standort.class))).thenReturn(new StandortType());
    }

    @Test
    public void testApply() throws Exception {
        StornoAenderungAbgAnfrage stornoAnfrage = new StornoAenderungAbgAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        StornoAenderungEKPabgType result = testling.apply(stornoAnfrage);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getAbsender());
        Assert.assertNotNull(result.getEmpfaenger());
        Assert.assertNotNull(result.getEndkundenvertragspartner());
        Assert.assertNotNull(result.getName());
        Assert.assertNotNull(result.getStandort());
        Assert.assertEquals(result.getStornogrund(), stornoAnfrage.getStornoGrund());
        Assert.assertEquals(result.getStornoId(), stornoAnfrage.getAenderungsId());
        Assert.assertEquals(result.getVorabstimmungsIdRef(), stornoAnfrage.getVorabstimmungsIdRef());
    }
}
