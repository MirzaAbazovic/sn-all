/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.time.*;
import java.time.format.*;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungERLMTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionERLMUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class ErledigtmeldungUnmarshallerTest extends
        AbstractMeldungUnmarshallerTest<MeldungERLMType, Erledigtmeldung, ErledigtmeldungUnmarshaller> {

    @Mock
    private MeldungsPositionERLMUnmarshaller meldungsPositionUnmarshaller;

    private MeldungPositionErledigtmeldung meldungPositionErledigtmeldung;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        meldungPositionErledigtmeldung = new MeldungPositionErledigtmeldung();
        when(meldungsPositionUnmarshaller.apply(any(MeldungsPositionERLMType.class))).thenReturn(
                meldungPositionErledigtmeldung);
    }

    @Test
    public void testApplyErledigtmeldung() throws Exception {
        Erledigtmeldung result = testling.apply(input);
        Assert.assertNotNull(result.getMeldungsPositionen());
        Assert.assertTrue(result.getMeldungsPositionen().contains(meldungPositionErledigtmeldung));
        Assert.assertEquals(result.getAenderungsIdRef(), input.getAenderungsIdRef());
        Assert.assertEquals(result.getStornoIdRef(), input.getStornoIdRef());
        Assert.assertEquals(result.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), input.getWechseltermin().toXMLFormat());
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected ErledigtmeldungUnmarshaller getTestling() {
        return new ErledigtmeldungUnmarshaller();
    }

    @Override
    protected MeldungERLMType getInput() {
        return new MeldungERLMTypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }
}
