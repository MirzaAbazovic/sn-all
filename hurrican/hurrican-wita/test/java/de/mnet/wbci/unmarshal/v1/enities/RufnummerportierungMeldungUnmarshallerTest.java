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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.PortierungRufnummernMeldungTypeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.PortierungRufnummernbloeckeMeldungMeldungTypeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernportierungMeldungTypeAnlageTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernportierungMeldungTypeBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernportierungMeldungTypeEinzelTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RufnummerportierungMeldungUnmarshallerTest {
    @Mock
    private PortierungRufnummernEinzelnMeldungUnmarshaller portierungRufnummernMeldungUnmarshaller;
    @Mock
    private PortierungRufnummernBloeckeMeldungUnmarshaller portierungRufnummernBloeckeUnmarshaller;
    @InjectMocks
    private RufnummerportierungMeldungUnmarshaller testling;

    private RufnummernportierungEinzeln aspectedRufnummernportierungEinzeln;
    private RufnummernportierungAnlage aspectedRufnummernportierungAnlage;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);

        aspectedRufnummernportierungEinzeln = new RufnummernportierungEinzeln();
        when(portierungRufnummernMeldungUnmarshaller.apply(any(PortierungRufnummernMeldungType.class))).thenReturn(
                aspectedRufnummernportierungEinzeln);

        aspectedRufnummernportierungAnlage = new RufnummernportierungAnlage();
        when(portierungRufnummernBloeckeUnmarshaller.apply(any(PortierungRufnummernbloeckeMeldungType.class)))
                .thenReturn(aspectedRufnummernportierungAnlage);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyEinzel() throws Exception {
        RufnummernportierungMeldungType testdataEinzel = new RufnummernportierungMeldungTypeEinzelTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        Rufnummernportierung result = testling.apply(testdataEinzel);
        Assert.assertEquals(result.getTyp(), RufnummernportierungTyp.EINZEL);
        Assert.assertTrue(result instanceof RufnummernportierungEinzeln);
        Assert.assertEquals(result, aspectedRufnummernportierungEinzeln);
    }

    @Test
    public void testApplyAnlage() throws Exception {
        RufnummernportierungMeldungType testdataAnlage = new RufnummernportierungMeldungTypeAnlageTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        Rufnummernportierung result = testling.apply(testdataAnlage);
        Assert.assertEquals(result.getTyp(), RufnummernportierungTyp.ANLAGE);
        Assert.assertTrue(result instanceof RufnummernportierungAnlage);
        Assert.assertEquals(result, aspectedRufnummernportierungAnlage);
    }

    @Test(expectedExceptions = { AssertionError.class })
    public void testAssert() throws Exception {
        RufnummernportierungMeldungType testBoth = new RufnummernportierungMeldungTypeBuilder()
                .withPortierungRufnummern(
                        new PortierungRufnummernMeldungTypeTestBuilder().buildValid(
                                GeschaeftsfallEnumType.VA_KUE_MRN)
                )
                .withPortierungRufnummernbloecke(
                        new PortierungRufnummernbloeckeMeldungMeldungTypeTestBuilder().buildValid(
                                GeschaeftsfallEnumType.VA_KUE_MRN)
                )
                .build();
        testling.apply(testBoth);
        Assert.assertFalse(true, "No assertion error occurred!");
    }
}
