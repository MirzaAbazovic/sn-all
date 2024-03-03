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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;
import de.mnet.wbci.marshal.v1.entities.RufnummernportierungMitPortierungskennungMarshaller;
import de.mnet.wbci.marshal.v1.request.AbstactAnfrageMarshallerMock;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class ReineRufnummernportierungGeschaeftsfallMarshallerTest extends
        AbstactAnfrageMarshallerMock {

    @InjectMocks
    private ReineRufnummernportierungGeschaeftsfallMarshaller testling = new ReineRufnummernportierungGeschaeftsfallMarshaller();

    @Mock
    private RufnummernportierungMitPortierungskennungMarshaller rufnummernportierungMitPortierungskennungMarshallerMock;

    private WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp;
    private RufnummernportierungMitPortierungskennungType aspectedRufnummernportierungMitPortierungskennungType;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        wbciGeschaeftsfallRrnp = new WbciGeschaeftsfallRrnpTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_RRNP);
        initMockHandlingVorabstimmung(wbciGeschaeftsfallRrnp);

        aspectedRufnummernportierungMitPortierungskennungType = new RufnummernportierungMitPortierungskennungType();
        when(rufnummernportierungMitPortierungskennungMarshallerMock.apply(wbciGeschaeftsfallRrnp
                .getRufnummernportierung())).thenReturn(aspectedRufnummernportierungMitPortierungskennungType);

    }

    @Test
    public void testApply() throws Exception {
        ReineRufnummernportierungGeschaeftsfallType testlingResult = testling.apply(wbciGeschaeftsfallRrnp);
        Assert.assertNotNull(testlingResult);
        Assert.assertEquals(testlingResult.getRufnummernPortierung(),
                aspectedRufnummernportierungMitPortierungskennungType);

    }
}
