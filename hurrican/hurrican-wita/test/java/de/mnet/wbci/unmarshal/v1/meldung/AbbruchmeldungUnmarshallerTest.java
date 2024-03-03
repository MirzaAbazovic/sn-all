/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungABBMTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionABBMUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AbbruchmeldungUnmarshallerTest extends
        AbstractMeldungUnmarshallerTest<MeldungABBMType, Abbruchmeldung, AbbruchmeldungUnmarshaller> {

    @Mock
    private MeldungsPositionABBMUnmarshaller meldungsPositionABBMUnmarshaller;

    private MeldungPositionAbbruchmeldung meldungPositionAbbruchmeldung;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        meldungPositionAbbruchmeldung = new MeldungPositionAbbruchmeldung();
        when(meldungsPositionABBMUnmarshaller.apply(any(MeldungsPositionABBMType.class))).thenReturn(
                meldungPositionAbbruchmeldung);
    }

    @Test
    public void testApplyAbbruchmeldung() throws Exception {
        Abbruchmeldung result = testling.apply(input);
        Assert.assertNotNull(result.getMeldungsPositionen());
        Assert.assertTrue(result.getMeldungsPositionen().contains(meldungPositionAbbruchmeldung));
        Assert.assertEquals(result.getAenderungsIdRef(), input.getAenderungsIdRef());
        Assert.assertEquals(result.getStornoIdRef(), input.getStornoIdRef());
        Assert.assertEquals(result.getBegruendung(), input.getBegruendung());
        Assert.assertEquals(result.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), input.getWechseltermin().toXMLFormat());
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected AbbruchmeldungUnmarshaller getTestling() {
        return new AbbruchmeldungUnmarshaller();
    }

    @Override
    protected MeldungABBMType getInput() {
        return new MeldungABBMTypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }

}
