/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.model.MeldungPositionAbbmRufnummer;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungsPositionABBMTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionABBMUnmarshallerTest {

    @Mock
    private AdresseAbweichendUnmarshaller adresseAbweichendUnmarshallerMock;
    @InjectMocks
    private MeldungsPositionABBMUnmarshaller testling;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        MeldungsPositionABBMType testdata = new MeldungsPositionABBMTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        MeldungPositionAbbruchmeldung result = testling.apply(testdata);
        Assert.assertTrue(result.getRufnummern() != null);
        Assert.assertTrue(result.getRufnummern().size() > 0);
        Assert.assertEquals(result.getRufnummern().size(), testdata.getRufnummer().size());
        for (MeldungPositionAbbmRufnummer rnr : result.getRufnummern()) {
            Assert.assertTrue(testdata.getRufnummer().contains(rnr.getRufnummer()));
        }
        Assert.assertEquals(result.getMeldungsText(), testdata.getMeldungstext());
        Assert.assertEquals(result.getMeldungsCode().getCode(), testdata.getMeldungscode());
    }

}
