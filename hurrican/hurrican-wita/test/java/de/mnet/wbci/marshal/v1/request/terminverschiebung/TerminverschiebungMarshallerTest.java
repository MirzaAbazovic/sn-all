/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request.terminverschiebung;

import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.entities.PersonOderFirmaMarshaller;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class TerminverschiebungMarshallerTest {

    @Mock
    private EKPGeschaeftsfallMarshaller ekpMarshaller;
    @Mock
    private CarrierIdentifikatorMarshaller ciMarshaller;
    @Mock
    private PersonOderFirmaMarshaller personOderFirmaMarshaller;

    @InjectMocks
    private TerminverschiebungMarshaller testling = new TerminverschiebungMarshaller();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(ekpMarshaller.apply(Matchers.any(WbciGeschaeftsfall.class))).thenReturn(new EKPType());
        Mockito.when(ciMarshaller.apply(Matchers.any(CarrierCode.class))).thenReturn(new CarrierIdentifikatorType());
        Mockito.when(personOderFirmaMarshaller.apply(Matchers.any(PersonOderFirma.class))).thenReturn(new PersonOderFirmaType());
    }

    public void testApply() {
        TerminverschiebungsAnfrage tvAnfrage =
                new TerminverschiebungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        TerminverschiebungType result = testling.apply(tvAnfrage);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getAbsender());
        Assert.assertNotNull(result.getEmpfaenger());
        Assert.assertNotNull(result.getEndkundenvertragspartner());
        Assert.assertEquals(result.getAenderungsId(), tvAnfrage.getAenderungsId());
        Assert.assertNotNull(result.getName());
        Assert.assertEquals(result.getNeuerKundenwunschtermin(),
                DateConverterUtils.toXmlGregorianCalendar(tvAnfrage.getTvTermin()));
        Assert.assertNotNull(result.getVorabstimmungsIdRef());
    }

}
