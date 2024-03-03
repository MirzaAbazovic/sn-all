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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.ReineRufnummernportierungGeschaeftsfallTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.RufnummernPortierungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.AnfrageUnmarshallerTest;

@Test(groups = BaseTest.UNIT)
public class ReineRufnummernPortierungGeschaeftsfallUnmarshallerTest
        extends AnfrageUnmarshallerTest<ReineRufnummernportierungGeschaeftsfallType, WbciGeschaeftsfallRrnp, ReineRufnummernPortierungGeschaeftsfallUnmarshaller> {

    @Mock
    protected RufnummernPortierungUnmarshaller rufnummernPortierungUnmarshaller;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        Mockito.when(rufnummernPortierungUnmarshaller.apply(input.getRufnummernPortierung()))
                .thenReturn(new RufnummernportierungAnlage());
    }

    @Test
    public void testApply() {
        WbciGeschaeftsfallRrnp result = testling.apply(input);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getRufnummernportierung());
    }


    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected ReineRufnummernportierungGeschaeftsfallType getInput() {
        return new ReineRufnummernportierungGeschaeftsfallTypeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_RRNP);
    }

    @Override
    protected WbciGeschaeftsfallRrnp getWbciGeschaeftsfall() {
        return new WbciGeschaeftsfallRrnp();
    }

    @Override
    protected ReineRufnummernPortierungGeschaeftsfallUnmarshaller getTestling() {
        return new ReineRufnummernPortierungGeschaeftsfallUnmarshaller();
    }
}
