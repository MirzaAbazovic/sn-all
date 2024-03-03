/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request.vorabstimmung;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractKuendigungGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.wbci.marshal.v1.request.AbstactAnfrageMarshallerMock;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AbstractKuendigungGeschaeftsfallAnfrageMarshallerTest extends AbstactAnfrageMarshallerMock {
    @InjectMocks
    private AbstractKuendigungGeschaeftsfallMarshaller<WbciGeschaeftsfallKue, AbstractKuendigungGeschaeftsfallType> testling =
            new AbstractKuendigungGeschaeftsfallMarshaller<WbciGeschaeftsfallKue, AbstractKuendigungGeschaeftsfallType>() {
                public AbstractKuendigungGeschaeftsfallType apply(WbciGeschaeftsfallKue input) {
                    return super.apply(new KuendigungMitRNPGeschaeftsfallType(), input);
                }
            };

    WbciGeschaeftsfallKue wbciGeschaeftsfallKue;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        wbciGeschaeftsfallKue = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        initMockHandlingAbstractKuedingungGeschaeftsfall(wbciGeschaeftsfallKue);
    }

    @Test
    public void testApply() throws Exception {
        AbstractKuendigungGeschaeftsfallType testlingResult = testling.apply(wbciGeschaeftsfallKue);
        Assert.assertEquals(testlingResult.getStandort(), aspectedStandort);
    }
}
