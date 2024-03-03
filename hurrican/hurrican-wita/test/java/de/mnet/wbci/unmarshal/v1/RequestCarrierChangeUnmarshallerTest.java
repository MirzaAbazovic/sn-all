/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.unmarshal.v1;

import static org.mockito.Mockito.*;

import java.time.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RequestCarrierChangeTestBuilder;
import de.mnet.wbci.unmarshal.v1.request.vorabstimmung.KuendigungMitRNPGeschaeftsfallUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.vorabstimmung.KuendigungOhneRNPGeschaeftsfallUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.vorabstimmung.ReineRufnummernPortierungGeschaeftsfallUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RequestCarrierChangeUnmarshallerTest {

    @Mock
    private KuendigungMitRNPGeschaeftsfallUnmarshaller kueMrnUnmarshallerMock;
    @Mock
    private KuendigungOhneRNPGeschaeftsfallUnmarshaller kueOrnUnmarshallerMock;
    @Mock
    private ReineRufnummernPortierungGeschaeftsfallUnmarshaller rrnpUnmarshallerMock;
    @InjectMocks
    private RequestCarrierChangeUnmarshaller testling;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyVAKUEMRN() throws Exception {
        RequestCarrierChange testdata = new RequestCarrierChangeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        WbciGeschaeftsfallKueMrn expectedGF = new WbciGeschaeftsfallKueMrn();
        LocalDate expectedVaKwt = LocalDate.now();
        expectedGF.setKundenwunschtermin(expectedVaKwt);
        when(kueMrnUnmarshallerMock.apply(testdata.getVAKUEMRN())).thenReturn(expectedGF);
        WbciRequest result = testling.apply(testdata);
        verifyFields(result, expectedGF, expectedVaKwt);
    }

    @Test
    public void testApplyVAKUEORN() throws Exception {
        RequestCarrierChange testdata = new RequestCarrierChangeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_ORN);
        WbciGeschaeftsfallKueOrn expectedGF = new WbciGeschaeftsfallKueOrn();
        LocalDate expectedVaKwt = LocalDate.now();
        expectedGF.setKundenwunschtermin(expectedVaKwt);
        when(kueOrnUnmarshallerMock.apply(testdata.getVAKUEORN())).thenReturn(expectedGF);
        WbciRequest result = testling.apply(testdata);
        verifyFields(result, expectedGF, expectedVaKwt);

    }

    @Test
    public void testApplyVARRNP() throws Exception {
        RequestCarrierChange testdata = new RequestCarrierChangeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_RRNP);
        WbciGeschaeftsfallRrnp expectedGF = new WbciGeschaeftsfallRrnp();
        LocalDate expectedVaKwt = LocalDate.now();
        expectedGF.setKundenwunschtermin(expectedVaKwt);
        when(rrnpUnmarshallerMock.apply(testdata.getVARRNP())).thenReturn(expectedGF);
        WbciRequest result = testling.apply(testdata);
        verifyFields(result, expectedGF, expectedVaKwt);
    }

    private void verifyFields(WbciRequest result, WbciGeschaeftsfall expectedGF, LocalDate expectedKundenwunschtermin) {
        Assert.assertTrue(result instanceof VorabstimmungsAnfrage);
        Assert.assertEquals(result.getWbciGeschaeftsfall(), expectedGF);
        Assert.assertEquals(((VorabstimmungsAnfrage) result).getVaKundenwunschtermin(), expectedKundenwunschtermin);
    }
}
