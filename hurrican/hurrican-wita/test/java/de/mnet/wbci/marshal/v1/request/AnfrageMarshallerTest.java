/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AnfrageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AnfrageMarshallerTest extends AbstactAnfrageMarshallerMock {

    @InjectMocks
    private AnfrageMarshaller<WbciGeschaeftsfall, AnfrageType> testling = new AnfrageMarshaller<WbciGeschaeftsfall, AnfrageType>() {
        public AnfrageType apply(WbciGeschaeftsfall input) {
            return super.apply(new KuendigungMitRNPGeschaeftsfallType(), input);
        }
    };

    private WbciGeschaeftsfall wbciGeschaeftsfall;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        initMockHandling(wbciGeschaeftsfall);
    }

    @Test
    protected void testApply() throws Exception {

        AnfrageType testlingResult = testling.apply(wbciGeschaeftsfall);
        Assert.assertEquals(testlingResult.getAbsender(), aspectedAbsender);
        Assert.assertEquals(testlingResult.getEmpfaenger(), aspectedEmpfaenger);
        Assert.assertEquals(testlingResult.getEndkundenvertragspartner(), aspectedEkp);
    }
}
