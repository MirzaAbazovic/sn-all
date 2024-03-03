/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungsPositionRUEMVAMeldungTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AbstractMeldungsPositionUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Test
    public void testApply() throws Exception {
        AbstractMeldungsPositionUnmarshaller testling = getMockAbstractMeldungsPositionUnmarshaller();
        MeldungPosition meldungPosition = getMockMeldungPosition();

        MeldungsPositionRUEMVAType testdata = new MeldungsPositionRUEMVAMeldungTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);

        MeldungPosition result = testling.apply(meldungPosition, testdata);
        Assert.assertEquals(result.getMeldungsCode().getCode(), testdata.getMeldungscode());
        Assert.assertEquals(result.getMeldungsText(), testdata.getMeldungstext());
    }

    private AbstractMeldungsPositionUnmarshaller getMockAbstractMeldungsPositionUnmarshaller() {
        AbstractMeldungsPositionUnmarshaller meldungPositionMock = Mockito.mock(AbstractMeldungsPositionUnmarshaller.class, Mockito.CALLS_REAL_METHODS);
        return meldungPositionMock;
    }

    private MeldungPosition getMockMeldungPosition() {
        MeldungPosition meldungPositionMock = Mockito.mock(MeldungPosition.class, Mockito.CALLS_REAL_METHODS);
        return meldungPositionMock;
    }

}
