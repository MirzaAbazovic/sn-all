/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungsPositionRUEMVAMeldungTypeTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MeldungsPositionRUEMVAUnmarshallerTest {
    @Mock
    private AdresseAbweichendUnmarshaller adresseAbweichendUnmarshallerMock;
    @InjectMocks
    private MeldungsPositionRUEMVAUnmarshaller testling;
    private Standort apectedStandort;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        apectedStandort = new Standort();
        when(adresseAbweichendUnmarshallerMock.apply(any(AdresseAbweichendType.class))).thenReturn(apectedStandort);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        MeldungsPositionRUEMVAType testdata = new MeldungsPositionRUEMVAMeldungTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        MeldungPositionRueckmeldungVa result = testling.apply(testdata);
        Assert.assertEquals(result.getStandortAbweichend(), apectedStandort);
        Assert.assertEquals(result.getMeldungsText(), testdata.getMeldungstext());
    }
}
