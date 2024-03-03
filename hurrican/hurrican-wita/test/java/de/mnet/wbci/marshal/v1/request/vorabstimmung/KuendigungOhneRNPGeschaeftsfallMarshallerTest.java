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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.wbci.marshal.v1.entities.RufnummerOnkzMarshaller;
import de.mnet.wbci.marshal.v1.request.AbstactAnfrageMarshallerMock;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class KuendigungOhneRNPGeschaeftsfallMarshallerTest extends
        AbstactAnfrageMarshallerMock {
    @InjectMocks
    private KuendigungOhneRNPGeschaeftsfallMarshaller testling = new KuendigungOhneRNPGeschaeftsfallMarshaller();

    @Mock
    private RufnummerOnkzMarshaller rufnummerOnkzMarshallermock;

    private WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn;
    private OnkzRufNrType aspectedOnkzRufNrType;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_ORN);
        initMockHandlingAbstractKuedingungGeschaeftsfall(wbciGeschaeftsfallKueOrn);

        aspectedOnkzRufNrType = new OnkzRufNrType();
        when(rufnummerOnkzMarshallermock.apply(wbciGeschaeftsfallKueOrn.getAnschlussIdentifikation())).thenReturn(
                aspectedOnkzRufNrType);
    }

    @Test
    public void testApply() throws Exception {
        KuendigungOhneRNPGeschaeftsfallType testlingResult = testling.apply(wbciGeschaeftsfallKueOrn);
        Assert.assertNotNull(testlingResult);
        Assert.assertEquals(testlingResult.getAnschlussidentifikation(), aspectedOnkzRufNrType);
    }
}
