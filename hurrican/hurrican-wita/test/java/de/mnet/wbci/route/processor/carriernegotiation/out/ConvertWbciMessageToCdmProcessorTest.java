/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import static org.mockito.Mockito.*;

import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.xml.transform.StringResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wbci.marshal.MessageMarshallerDelegate;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.service.WbciSchemaValidationService;

@Test(groups = BaseTest.UNIT)
public class ConvertWbciMessageToCdmProcessorTest extends BaseTest {

    @Mock
    private Exchange exchange;
    @Mock
    private Message message;
    @Mock
    private MessageMarshallerDelegate messageMarshallerDelegate;
    @Mock
    private SoapMessageFactory soapMessageFactory;
    @Mock
    private SoapMessage soapMessage;
    @Mock
    private SoapBody soapBody;
    @Mock
    private WbciSchemaValidationService schemaValidationService;

    @InjectMocks
    @Spy
    private ConvertWbciMessageToCdmProcessor cut = new ConvertWbciMessageToCdmProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // set dummy soap action for requestCarrierChange
        cut.setSoapActionRequestCarrierChange("soapActionRequestCarrierChange");
        cut.setSoapActionUpdateCarrierChange("soapActionUpdateCarrierChange");
        cut.setSoapActionRescheduleCarrierChange("soapActionRescheduleCarrierChange");
        cut.setSoapActionCancelCarrierChange("soapActionCancelCarrierChange");
    }

    public void testProcessorWithWbciVorabstimmung() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        WbciRequest request = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        prepareCamelMocks(new VorabstimmungsAnfrageBuilder().build(), soapBodyTransformationResult);
        Mockito.doReturn(request).when(cut).getOriginalMessage(Matchers.any(Exchange.class));

        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(AtlasEsbConstants.CDM_VERSION_KEY)).thenReturn(WbciCdmVersion.V1);

        cut.process(exchange);

        verify(messageMarshallerDelegate).marshal(any(WbciRequest.class), any(Result.class), eq(WbciCdmVersion.V1));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionRequestCarrierChange");
        verify(message).removeHeader(AtlasEsbConstants.CDM_VERSION_KEY);
        verify(schemaValidationService).validatePayload(any(Source.class));
    }

    public void testProcessorWithMeldung() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        prepareCamelMocks(meldung, soapBodyTransformationResult);
        Mockito.doReturn(meldung).when(cut).getOriginalMessage(Matchers.any(Exchange.class));

        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(AtlasEsbConstants.CDM_VERSION_KEY)).thenReturn(WbciCdmVersion.V1);

        cut.process(exchange);

        verify(messageMarshallerDelegate).marshal(any(Meldung.class), any(Result.class), eq(WbciCdmVersion.V1));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionUpdateCarrierChange");
        verify(message).removeHeader(AtlasEsbConstants.CDM_VERSION_KEY);
        verify(schemaValidationService).validatePayload(any(Source.class));
    }

    public void testProcessorWithStorno() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        StornoAnfrage<?> request = new StornoAenderungAufAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        prepareCamelMocks(new VorabstimmungsAnfrageBuilder().build(), soapBodyTransformationResult);
        Mockito.doReturn(request).when(cut).getOriginalMessage(Matchers.any(Exchange.class));

        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(AtlasEsbConstants.CDM_VERSION_KEY)).thenReturn(WbciCdmVersion.V1);

        cut.process(exchange);

        verify(messageMarshallerDelegate).marshal(any(WbciRequest.class), any(Result.class), eq(WbciCdmVersion.V1));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionCancelCarrierChange");
        verify(message).removeHeader(AtlasEsbConstants.CDM_VERSION_KEY);
        verify(schemaValidationService).validatePayload(any(Source.class));
    }

    private void prepareCamelMocks(WbciMessage originalMessage, StringResult payloadResult) {
        reset(exchange, soapMessageFactory, soapMessage, soapBody);

        when(exchange.getOut()).thenReturn(message);
        when(soapMessageFactory.createWebServiceMessage()).thenReturn(soapMessage);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        when(soapBody.getPayloadResult()).thenReturn(payloadResult);
        when(message.getBody()).thenReturn(originalMessage);
    }

}
