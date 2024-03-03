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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.OnkzDurchwahlAbfragestelleTypeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.PortierungRufnummernbloeckeMeldungMeldungTypeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.PortierungRufnummernbloeckeMeldungTypeBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class PortierungRufnummernBloeckeMeldungUnmarshallerTest {
    @Mock
    private RufnummernblockUnmarshaller rufnummernblockUnmarshallerMock;
    @InjectMocks
    private PortierungRufnummernBloeckeMeldungUnmarshaller testling;
    private Rufnummernblock aspectedRufnummernblock;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        aspectedRufnummernblock = new Rufnummernblock();
        when(rufnummernblockUnmarshallerMock.apply(any(RufnummernblockType.class))).thenReturn(aspectedRufnummernblock);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        PortierungRufnummernbloeckeMeldungType testdata = new PortierungRufnummernbloeckeMeldungMeldungTypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        RufnummernportierungAnlage result = testling.apply(testdata);
        checkOnkz(result, testdata);
        checkRufnummerblock(result);
    }

    @Test
    public void testApplyOnlyOnkzDurchwahlAbfragestelle() throws Exception {
        PortierungRufnummernbloeckeMeldungType testdata = new PortierungRufnummernbloeckeMeldungTypeBuilder()
                .withOnkzDurchwahlAbfragestelle(
                        new OnkzDurchwahlAbfragestelleTypeTestBuilder().buildValid(
                                GeschaeftsfallEnumType.VA_KUE_MRN)
                ).build();

        RufnummernportierungAnlage result = testling.apply(testdata);
        checkOnkz(result, testdata);
        Assert.assertNull(result.getRufnummernbloecke());
    }

    @Test
    public void testApplyOnlyRufnummernblock() throws Exception {
        PortierungRufnummernbloeckeMeldungType testdata = new PortierungRufnummernbloeckeMeldungMeldungTypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        testdata.setOnkzDurchwahlAbfragestelle(null);

        RufnummernportierungAnlage result = testling.apply(testdata);
        Assert.assertNull(result.getAbfragestelle());
        Assert.assertNull(result.getDurchwahlnummer());
        Assert.assertNull(result.getOnkz());
        checkRufnummerblock(result);
    }

    private void checkRufnummerblock(RufnummernportierungAnlage result) {
        Assert.assertNotNull(result.getRufnummernbloecke());
        Assert.assertEquals(result.getRufnummernbloecke().get(0), aspectedRufnummernblock);
    }

    private void checkOnkz(RufnummernportierungAnlage result, PortierungRufnummernbloeckeMeldungType testdata) {
        Assert.assertEquals(result.getOnkz(), testdata.getOnkzDurchwahlAbfragestelle().getONKZ());
        Assert.assertEquals(result.getAbfragestelle(), testdata.getOnkzDurchwahlAbfragestelle().getAbfragestelle());
        Assert.assertEquals(result.getDurchwahlnummer(), testdata.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer());
    }

}
