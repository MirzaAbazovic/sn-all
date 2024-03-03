/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import javax.annotation.*;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RufnummernportierungBaseMarshallerTest {

    @InjectMocks
    private RufnummernportierungBaseMarshaller<RufnummernportierungType> testling = new RufnummernportierungBaseMarshaller<RufnummernportierungType>() {
        @Nullable
        @Override
        public RufnummernportierungType apply(Rufnummernportierung input) {
            return super.apply(new RufnummernportierungType(), input);
        }
    };

    @Mock
    private PortierungDurchwahlanlageMarshaller durchwahlanlageMarshallerMock;

    @Mock
    private PortierungEinzelanschlussMarshaller einzelanschlussMarshallerMock;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMapDurchwahlanlage() throws Exception {
        RufnummernportierungAnlage input = new RufnummernportierungAnlageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        when(durchwahlanlageMarshallerMock.apply(input)).thenReturn(null);

        RufnummernportierungType output = testling.apply(input);

        assertEquals(output.getPortierungszeitfenster().value(), input.getPortierungszeitfenster().name());

        verify(durchwahlanlageMarshallerMock, atLeastOnce()).apply(input);
        verify(einzelanschlussMarshallerMock, never()).apply(Matchers.any());
    }

    @Test
    public void testMap() throws Exception {
        RufnummernportierungEinzeln input = new RufnummernportierungEinzelnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        when(einzelanschlussMarshallerMock.apply(input)).thenReturn(null);

        RufnummernportierungType output = testling.apply(input);

        assertEquals(output.getPortierungszeitfenster().value(), input.getPortierungszeitfenster().name());

        verify(einzelanschlussMarshallerMock, atLeastOnce()).apply(input);
        verify(durchwahlanlageMarshallerMock, never()).apply(Matchers.any());
    }
}
