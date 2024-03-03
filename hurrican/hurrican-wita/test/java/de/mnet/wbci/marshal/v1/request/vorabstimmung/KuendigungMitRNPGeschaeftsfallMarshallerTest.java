/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request.vorabstimmung;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.marshal.v1.entities.RufnummernportierungMarshaller;
import de.mnet.wbci.marshal.v1.request.AbstactAnfrageMarshallerMock;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class KuendigungMitRNPGeschaeftsfallMarshallerTest extends
        AbstactAnfrageMarshallerMock {

    @InjectMocks
    private KuendigungMitRNPGeschaeftsfallMarshaller testling = new KuendigungMitRNPGeschaeftsfallMarshaller();

    @Mock
    private RufnummernportierungMarshaller rufnummernportierungMarshallerMock;

    private WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn;
    private RufnummernportierungType aspectedRufnummernportierungType;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        initMockHandlingAbstractKuedingungGeschaeftsfall(wbciGeschaeftsfallKueMrn);

        aspectedRufnummernportierungType = new RufnummernportierungType();
        when(rufnummernportierungMarshallerMock.apply(wbciGeschaeftsfallKueMrn
                .getRufnummernportierung())).thenReturn(aspectedRufnummernportierungType);

    }

    @Test
    public void testApply() throws Exception {
        KuendigungMitRNPGeschaeftsfallType testlingResult = testling.apply(wbciGeschaeftsfallKueMrn);
        Assert.assertNotNull(testlingResult);
        Assert.assertEquals(testlingResult.getRufnummernPortierung(),
                aspectedRufnummernportierungType);

    }
}
