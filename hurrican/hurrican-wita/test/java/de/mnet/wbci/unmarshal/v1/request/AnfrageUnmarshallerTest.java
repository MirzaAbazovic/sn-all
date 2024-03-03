/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.request;

import javax.annotation.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AnfrageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.KuendigungMitRNPGeschaeftsfallTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.PersonOderFirmaUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.ProjektUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.StandortUnmarshaller;

@Test(groups = BaseTest.UNIT)
public class AnfrageUnmarshallerTest<IN extends AnfrageType, OUT extends WbciGeschaeftsfall, M extends AnfrageUnmarshaller<IN, OUT>> {

    @Mock
    protected CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshaller;
    @Mock
    protected PersonOderFirmaUnmarshaller personOderFirmaUnmarshaller;
    @Mock
    protected ProjektUnmarshaller projektUnmarshaller;
    @Mock
    protected StandortUnmarshaller standortUnmarshaller;

    @InjectMocks
    protected M testling;

    protected IN input;
    protected OUT wbciGeschaeftsfall;

    @BeforeClass
    public void init() {
        wbciGeschaeftsfall = getWbciGeschaeftsfall();
        input = getInput();
        testling = getTestling();

        MockitoAnnotations.initMocks(this);

        Mockito.when(carrierIdentifikatorUnmarshaller.apply(input.getAbsender())).thenReturn(CarrierCode.DTAG);
        Mockito.when(carrierIdentifikatorUnmarshaller.apply(input.getEmpfaenger())).thenReturn(CarrierCode.MNET);
        Mockito.when(carrierIdentifikatorUnmarshaller.apply(input.getEndkundenvertragspartner().getEKPauf())).thenReturn(CarrierCode.DTAG);
    }

    public void testApply() {
        WbciGeschaeftsfall result = testling.apply(input);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getWbciVersion(), WbciVersion.V2);
        Assert.assertEquals(result.getAbsender(), CarrierCode.DTAG);
        Assert.assertEquals(result.getAufnehmenderEKP(), CarrierCode.DTAG);
        Assert.assertEquals(result.getAbgebenderEKP(), CarrierCode.MNET);
    }

    /**
     * Override this method for concrete AnfrageUnmarshaller
     */
    protected M getTestling() {
        return (M) new AnfrageUnmarshaller<IN, OUT>() {
            @Nullable
            @Override
            public OUT apply(@Nullable IN input) {
                return super.apply(wbciGeschaeftsfall, input);
            }
        };
    }

    /**
     * Override this method for concrete AnfrageUnmarshaller
     */
    protected OUT getWbciGeschaeftsfall() {
        return (OUT) new WbciGeschaeftsfallKueMrn();
    }

    /**
     * Override this method for concrete AnfrageUnmarshaller
     */
    protected IN getInput() {
        return (IN) new KuendigungMitRNPGeschaeftsfallTypeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }

}
