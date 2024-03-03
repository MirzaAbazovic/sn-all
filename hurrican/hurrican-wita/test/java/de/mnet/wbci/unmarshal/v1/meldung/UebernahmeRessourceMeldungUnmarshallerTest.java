/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.LeitungTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungAKMTRTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.LeitungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionAKMTRUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class UebernahmeRessourceMeldungUnmarshallerTest extends
        AbstractMeldungUnmarshallerTest<MeldungAKMTRType, UebernahmeRessourceMeldung, UebernahmeRessourceMeldungUnmarshaller> {

    @Mock
    private MeldungsPositionAKMTRUnmarshaller meldungsPositionAKMTRUnmarshaller;
    @Mock
    private LeitungUnmarshaller leitungUnmarshaller;

    private MeldungPositionUebernahmeRessourceMeldung meldungPosition;
    private Leitung leitung;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        meldungPosition = new MeldungPositionUebernahmeRessourceMeldungTestBuilder()
                .withMeldungsCode(MeldungsCode.AKMTR_CODE)
                .withMeldungsText(MeldungsCode.AKMTR_CODE.getStandardText())
                .build();
        when(meldungsPositionAKMTRUnmarshaller.apply(any(MeldungsPositionAKMTRType.class))).thenReturn(
                meldungPosition);

        leitung = new LeitungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(leitungUnmarshaller.apply(any(UebernahmeLeitungType.class))).thenReturn(leitung);
    }

    @Test
    public void testApply() throws Exception {
        UebernahmeRessourceMeldung result = testling.apply(input);
        final Set<MeldungPositionUebernahmeRessourceMeldung> meldungsPositionen = result.getMeldungsPositionen();
        Assert.assertNotNull(meldungsPositionen);
        Assert.assertTrue(meldungsPositionen.contains(meldungPosition));
        Assert.assertEquals(meldungsPositionen.size(), 1);
        final MeldungPosition position = meldungsPositionen.iterator().next();
        Assert.assertNotNull(position.getMeldungsCode());
        Assert.assertNotNull(position.getMeldungsText());

        Assert.assertNotNull(result.getLeitungen());
        Assert.assertTrue(result.getLeitungen().contains(leitung));

        Assert.assertEquals(result.getPortierungskennungPKIauf(), input.getPortierungskennungPKIauf());
        Assert.assertEquals(result.isSichererHafen(), (Boolean) input.isSichererHafen());
        Assert.assertEquals(result.isUebernahme(), (Boolean) input.isRessourcenuebernahme());
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected UebernahmeRessourceMeldungUnmarshaller getTestling() {
        return new UebernahmeRessourceMeldungUnmarshaller();
    }

    @Override
    protected MeldungAKMTRType getInput() {
        return new MeldungAKMTRTypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }

}
