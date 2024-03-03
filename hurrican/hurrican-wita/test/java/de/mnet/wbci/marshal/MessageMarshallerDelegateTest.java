/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.13
 */
package de.mnet.wbci.marshal;

import static org.mockito.Mockito.*;

import java.util.*;
import javax.xml.transform.dom.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.marshal.v1.MessageMarshaller;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wita.service.WitaConfigService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MessageMarshallerDelegateTest extends AbstractWbciMarshallerTest {

    @Autowired
    private WitaConfigService witaConfigServiceMock;
    @Mock
    private MessageMarshaller messageMarshallerMock;

    @Autowired
    @InjectMocks
    private MessageMarshallerDelegate testling;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(witaConfigServiceMock.getWbciCdmVersion(CarrierCode.DTAG)).thenReturn(WbciCdmVersion.V1);
        when(witaConfigServiceMock.getWbciCdmVersion(CarrierCode.VODAFONE)).thenReturn(WbciCdmVersion.V1);
    }

    @Test
    public void testMarshalRequest() throws Exception {
        for (CarrierCode carrier : Arrays.asList(CarrierCode.DTAG, CarrierCode.VODAFONE)) {
            WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                    GeschaeftsfallTyp.VA_KUE_MRN);
            wbciRequest.getWbciGeschaeftsfall().setAbgebenderEKP(carrier);

            DOMResult expectedResult = new DOMResult();
            // if no exception happend test is valid
            testling.marshal(wbciRequest, expectedResult, WbciCdmVersion.V1);
            Assert.assertNotNull(expectedResult);
            verify(messageMarshallerMock).marshal(wbciRequest, expectedResult);
        }
    }

    @Test
    public void testMarshalMeldung() throws Exception {
        for (CarrierCode carrier : Arrays.asList(CarrierCode.DTAG, CarrierCode.VODAFONE)) {
            final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn =
                    new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
            wbciGeschaeftsfallKueMrn.setAbgebenderEKP(carrier);
            Meldung meldung = new UebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
            meldung.setWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn);

            DOMResult expectedResult = new DOMResult();
            // if no exception happend test is valid
            testling.marshal(meldung, expectedResult, WbciCdmVersion.V1);
            Assert.assertNotNull(expectedResult);
            verify(messageMarshallerMock).marshal(meldung, expectedResult);
        }
    }
}
