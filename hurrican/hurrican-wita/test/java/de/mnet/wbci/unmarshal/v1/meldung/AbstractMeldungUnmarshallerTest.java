/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.GeschaeftsfallEnumUnmarshaller;

/**
 *
 */
public abstract class AbstractMeldungUnmarshallerTest<IN extends AbstractMeldungType, OUT extends Meldung, M extends AbstractMeldungUnmarshaller<IN, OUT>> {

    @Mock
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshallerMock;
    @Mock
    private GeschaeftsfallEnumUnmarshaller geschaeftsfallEnumUnmarshallerMock;
    @InjectMocks
    protected M testling;

    private CarrierCode absender = CarrierCode.DTAG;
    private CarrierCode abgebenderEKP = CarrierCode.DTAG;
    private CarrierCode aufnehmenderEKP = CarrierCode.MNET;
    private GeschaeftsfallTyp geschaeftsfallEnumType = GeschaeftsfallTyp.VA_KUE_MRN;

    protected IN input;

    @BeforeClass
    public void init() {
        input = getInput();
        testling = getTestling();

        MockitoAnnotations.initMocks(this);

        when(carrierIdentifikatorUnmarshallerMock.apply(input.getAbsender()))
                .thenReturn(absender);
    }

    /**
     * Implement this method for the other MeldungsUnmarshaller, see {@link RueckmeldungVorabstimmungUnmarshaller}
     */
    protected abstract M getTestling();

    /**
     * Implement this method for the other MeldungsUnmarshaller, see {@link RueckmeldungVorabstimmungUnmarshaller}
     */
    protected abstract IN getInput();

    @Test
    public void testApply() throws Exception {
        Meldung result = testling.apply(input);
        Assert.assertEquals(result.getAbsender(), absender);
        Assert.assertEquals(result.getVorabstimmungsId(), input.getVorabstimmungsIdRef());
        Assert.assertEquals(result.getWbciVersion().getVersion(), String.valueOf(input.getVersion()));
    }
}
