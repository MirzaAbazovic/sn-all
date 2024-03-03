/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungABBMTRTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionABBMTRUnmarshaller;

/**
 *
 */
public class AbbruchmeldungTechnRessourceUnmarshallerTest
        extends
        AbstractMeldungUnmarshallerTest<MeldungABBMTRType, AbbruchmeldungTechnRessource, AbbruchmeldungTechnRessourceUnmarshaller> {
    @Mock
    private MeldungsPositionABBMTRUnmarshaller meldungsPositionABBMTRUnmarshallerMock;
    private MeldungPositionAbbruchmeldungTechnRessource meldungPositionAbbruchmeldungTechnRessource;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        meldungPositionAbbruchmeldungTechnRessource = new MeldungPositionAbbruchmeldungTechnRessource();
        when(meldungsPositionABBMTRUnmarshallerMock.apply(any(MeldungsPositionABBMTRType.class))).thenReturn(
                meldungPositionAbbruchmeldungTechnRessource);
    }

    @Test
    public void testApplyAbbruchmeldung() throws Exception {
        AbbruchmeldungTechnRessource result = testling.apply(input);
        Assert.assertNotNull(result.getMeldungsPositionen());
        Assert.assertTrue(result.getMeldungsPositionen().contains(meldungPositionAbbruchmeldungTechnRessource));
    }

    @Override
    protected AbbruchmeldungTechnRessourceUnmarshaller getTestling() {
        return new AbbruchmeldungTechnRessourceUnmarshaller();
    }

    @Override
    protected MeldungABBMTRType getInput() {
        return new MeldungABBMTRTypeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }

}
