/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.vorabstimmung;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.KuendigungOhneRNPGeschaeftsfallTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.OnkzRufNrUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.AnfrageUnmarshallerTest;

@Test(groups = BaseTest.UNIT)
public class KuendigungOhneRNPGeschaeftsfallUnmarshallerTest
        extends AnfrageUnmarshallerTest<KuendigungOhneRNPGeschaeftsfallType, WbciGeschaeftsfallKueOrn, KuendigungOhneRNPGeschaeftsfallUnmarshaller> {

    @Mock
    private OnkzRufNrUnmarshaller onkzRufNrUnmarshaller;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        Mockito.when(onkzRufNrUnmarshaller.apply(input.getAnschlussidentifikation()))
                .thenReturn(new RufnummerOnkzTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN));
    }

    @Test
    public void testApply() {
        WbciGeschaeftsfallKueOrn result = testling.apply(input);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getAnschlussIdentifikation());
    }


    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected KuendigungOhneRNPGeschaeftsfallType getInput() {
        return new KuendigungOhneRNPGeschaeftsfallTypeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_ORN);
    }

    @Override
    protected WbciGeschaeftsfallKueOrn getWbciGeschaeftsfall() {
        return new WbciGeschaeftsfallKueOrn();
    }

    @Override
    protected KuendigungOhneRNPGeschaeftsfallUnmarshaller getTestling() {
        return new KuendigungOhneRNPGeschaeftsfallUnmarshaller();
    }
}
