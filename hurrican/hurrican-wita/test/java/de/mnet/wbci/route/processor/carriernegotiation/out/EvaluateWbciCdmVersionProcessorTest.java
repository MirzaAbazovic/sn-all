/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.jms.JmsConstants;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.exceptions.WitaConfigException;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.UNIT)
public class EvaluateWbciCdmVersionProcessorTest extends BaseTest implements WbciCamelConstants {

    @Mock
    private Exchange exchange;
    @Mock
    private Message inMessage;
    @Mock
    private WitaConfigService witaConfigService;

    @InjectMocks
    @Spy
    private EvaluateCdmVersionProcessor cut = new EvaluateCdmVersionProcessor();

    @BeforeMethod
    public void setUp() {
        cut.carrierNegotiationServiceOut = "version.depend.url.v%s.destination";
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessWithWbciRequest() throws Exception {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(
                WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        WbciRequest<WbciGeschaeftsfallKueMrn> request = new VorabstimmungsAnfrageBuilder<WbciGeschaeftsfallKueMrn>()
                .withIoType(IOType.OUT)
                .withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn).build();

        when(witaConfigService.getWbciCdmVersion(request.getEKPPartner())).thenReturn(WbciCdmVersion.V1);
        when(exchange.getIn()).thenReturn(inMessage);

        Mockito.doReturn(request).when(cut).getOriginalMessage(Matchers.any(Exchange.class));
        cut.process(exchange);

        verify(inMessage).setHeader(JmsConstants.JMS_DESTINATION_NAME, "version.depend.url.v1.destination");
        verify(inMessage).setHeader(AtlasEsbConstants.CDM_VERSION_KEY, WbciCdmVersion.V1);
    }

    @Test
    public void testProcessWithMeldung() throws Exception {
        RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(witaConfigService.getWbciCdmVersion(meldung.getEKPPartner())).thenReturn(WbciCdmVersion.V1);
        when(exchange.getIn()).thenReturn(inMessage);

        Mockito.doReturn(meldung).when(cut).getOriginalMessage(Matchers.any(Exchange.class));
        cut.process(exchange);

        verify(inMessage).setHeader(JmsConstants.JMS_DESTINATION_NAME, "version.depend.url.v1.destination");
        verify(inMessage).setHeader(AtlasEsbConstants.CDM_VERSION_KEY, WbciCdmVersion.V1);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testNoCarrier() throws Exception {
        WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        wbciRequest.getWbciGeschaeftsfall().setAbgebenderEKP(null);

        when(exchange.getIn()).thenReturn(inMessage);

        Mockito.doReturn(wbciRequest).when(cut).getOriginalMessage(Matchers.any(Exchange.class));
        cut.process(exchange);

        Assert.fail("Missing IllegalArgumentException");
    }

    @Test(expectedExceptions = { WitaConfigException.class })
    public void testNoConfigedCarrier() throws Exception {
        WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        wbciRequest.getWbciGeschaeftsfall().setAbgebenderEKP(CarrierCode.MNET);

        when(witaConfigService.getWbciCdmVersion(CarrierCode.MNET)).thenThrow(new WitaConfigException());
        when(exchange.getIn()).thenReturn(inMessage);

        Mockito.doReturn(wbciRequest).when(cut).getOriginalMessage(Matchers.any(Exchange.class));
        cut.process(exchange);

        Assert.fail("Missing WitaConfigException");
    }

}
