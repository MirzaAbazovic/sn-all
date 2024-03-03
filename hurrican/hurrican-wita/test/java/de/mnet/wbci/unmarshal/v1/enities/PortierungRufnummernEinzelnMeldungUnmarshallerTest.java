/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.PortierungRufnummernMeldungTypeTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class PortierungRufnummernEinzelnMeldungUnmarshallerTest {
    @Mock
    private OnkzRufNrUnmarshaller onkzRufNrUnmarshallerMock;

    @InjectMocks
    private PortierungRufnummernEinzelnMeldungUnmarshaller testling;

    private RufnummerOnkz aspectedOnkzPortierung;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        aspectedOnkzPortierung = new RufnummerOnkz();
        when(onkzRufNrUnmarshallerMock.apply(any(OnkzRufNrPortierungskennerType
                .class))).thenReturn(aspectedOnkzPortierung);
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyEinzel() throws Exception {
        RufnummernportierungEinzeln result = testling.apply(new PortierungRufnummernMeldungTypeTestBuilder()
                .buildValid(
                        GeschaeftsfallEnumType.VA_KUE_MRN));
        Assert.assertNotNull(result.getRufnummernOnkz());
        Assert.assertEquals(result.getRufnummernOnkz().get(0), aspectedOnkzPortierung);
        Assert.assertNull(result.getAlleRufnummernPortieren());
    }

}
