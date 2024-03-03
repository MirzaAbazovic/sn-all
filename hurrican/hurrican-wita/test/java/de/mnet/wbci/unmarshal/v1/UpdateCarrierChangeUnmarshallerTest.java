/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.UpdateCarrierChangeTestBuilder;
import de.mnet.wbci.unmarshal.v1.meldung.AbbruchmeldungTechnRessourceUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.AbbruchmeldungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.ErledigtmeldungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.RueckmeldungVorabstimmungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.UebernahmeRessourceMeldungUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class UpdateCarrierChangeUnmarshallerTest {

    @Mock
    private RueckmeldungVorabstimmungUnmarshaller rueckmeldungVorabstimmungUnmarshallerMock;
    @Mock
    private AbbruchmeldungUnmarshaller abbruchmeldungUnmarshallerMock;
    @Mock
    private AbbruchmeldungTechnRessourceUnmarshaller abbruchmeldungTechnRessourceUnmarshallerMock;
    @Mock
    private UebernahmeRessourceMeldungUnmarshaller uebernahmeRessourceMeldungUnmarshaller;
    @Mock
    private ErledigtmeldungUnmarshaller erledigtmeldungUnmarshaller;

    @InjectMocks
    private UpdateCarrierChangeUnmarshaller testling;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApplyRuemVa() throws Exception {
        UpdateCarrierChange testdata = new UpdateCarrierChangeTestBuilder().buildValid(MeldungTyp.RUEM_VA,
                GeschaeftsfallEnumType.VA_KUE_MRN);
        RueckmeldungVorabstimmung aspectedRueckmeldungVorabstimmiung = new RueckmeldungVorabstimmung();

        when(rueckmeldungVorabstimmungUnmarshallerMock.apply(any(MeldungRUEMVAType.class))).thenReturn(
                aspectedRueckmeldungVorabstimmiung);

        Assert.assertEquals(testling.apply(testdata), aspectedRueckmeldungVorabstimmiung);
    }

    @Test
    public void testApplyAbbm() throws Exception {
        UpdateCarrierChange testdata = new UpdateCarrierChangeTestBuilder().buildValid(MeldungTyp.ABBM,
                GeschaeftsfallEnumType.VA_KUE_MRN);
        Abbruchmeldung abbruchmeldung = new Abbruchmeldung();

        when(abbruchmeldungUnmarshallerMock.apply(any(MeldungABBMType.class))).thenReturn(abbruchmeldung);

        Assert.assertEquals(testling.apply(testdata), abbruchmeldung);
    }

    @Test
    public void testApplyAbbmTr() throws Exception {
        UpdateCarrierChange testdata = new UpdateCarrierChangeTestBuilder().buildValid(MeldungTyp.ABBM_TR,
                GeschaeftsfallEnumType.VA_KUE_MRN);
        AbbruchmeldungTechnRessource abbruchmeldungTechnRessource = new AbbruchmeldungTechnRessource();

        when(abbruchmeldungTechnRessourceUnmarshallerMock.apply(any(MeldungABBMTRType.class)))
                .thenReturn(abbruchmeldungTechnRessource);

        Assert.assertEquals(testling.apply(testdata), abbruchmeldungTechnRessource);
    }

    @Test
    public void testApplyAkmTr() throws Exception {
        UpdateCarrierChange testdata = new UpdateCarrierChangeTestBuilder().buildValid(MeldungTyp.AKM_TR,
                GeschaeftsfallEnumType.VA_KUE_MRN);
        UebernahmeRessourceMeldung uebernahmeRessourceMeldung = new UebernahmeRessourceMeldung();

        when(uebernahmeRessourceMeldungUnmarshaller.apply(any(MeldungAKMTRType.class)))
                .thenReturn(uebernahmeRessourceMeldung);

        Assert.assertEquals(testling.apply(testdata), uebernahmeRessourceMeldung);
    }

    @Test
    public void testApplyErlm() throws Exception {
        UpdateCarrierChange testdata = new UpdateCarrierChangeTestBuilder().buildValid(MeldungTyp.ERLM,
                GeschaeftsfallEnumType.VA_KUE_MRN);
        Erledigtmeldung erledigtmeldung = new Erledigtmeldung();

        when(erledigtmeldungUnmarshaller.apply(any(MeldungERLMType.class)))
                .thenReturn(erledigtmeldung);

        Assert.assertEquals(testling.apply(testdata), erledigtmeldung);
    }

    @Test
    public void testApplyNull() throws Exception {
        UpdateCarrierChange testdata = null;
        Assert.assertNull(testling.apply(testdata));
    }

}
