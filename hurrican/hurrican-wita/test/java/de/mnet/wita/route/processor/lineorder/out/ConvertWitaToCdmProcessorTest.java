package de.mnet.wita.route.processor.lineorder.out;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.marshal.MessageMarshallerDelegate;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaSchemaValidationService;

@Test(groups = BaseTest.UNIT)
public class ConvertWitaToCdmProcessorTest extends BaseTest {

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
    private WitaSchemaValidationService schemaValidationService;
    @Mock
    private WitaConfigService witaConfigService;

    @InjectMocks
    @Spy
    private ConvertWitaToCdmProcessor cut = new ConvertWitaToCdmProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // set dummy soap action for requestCarrierChange
        cut.setSoapActionCreateOrder("soapActionCreateOrder");
        cut.setSoapActionUpdateOrder("soapActionUpdateOrder");
        cut.setSoapActionRescheduleOrder("soapActionRescheduleOrder");
        cut.setSoapActionCancelOrder("soapActionCancelOrder");

        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.V2);
    }

    public void testProcessorWithWbciVorabstimmung() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1).buildValid();
        prepareCamelMocks(auftrag, soapBodyTransformationResult);
        Mockito.doReturn(auftrag).when(cut).getOriginalMessage(Matchers.any(Exchange.class));

        when(exchange.getIn()).thenReturn(message);

        cut.process(exchange);

        verify(messageMarshallerDelegate).marshal(any(Auftrag.class), any(Result.class), eq(WitaCdmVersion.V2));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionCreateOrder");
        verify(schemaValidationService).validatePayload(any(Source.class));
    }

    public void testProcessorWithMeldung() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        AuftragsBestaetigungsMeldung meldung = new AuftragsBestaetigungsMeldungBuilder().build();

        prepareCamelMocks(meldung, soapBodyTransformationResult);
        Mockito.doReturn(meldung).when(cut).getOriginalMessage(Matchers.any(Exchange.class));

        when(exchange.getIn()).thenReturn(message);

        cut.process(exchange);

        verify(messageMarshallerDelegate).marshal(any(Meldung.class), any(Result.class), eq(WitaCdmVersion.V2));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionUpdateOrder");
        verify(schemaValidationService).validatePayload(any(Source.class));
    }

    public void testProcessorWithStorno() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        Storno request = new StornoBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V2).buildValid();

        prepareCamelMocks(request, soapBodyTransformationResult);
        Mockito.doReturn(request).when(cut).getOriginalMessage(Matchers.any(Exchange.class));

        when(exchange.getIn()).thenReturn(message);
        cut.process(exchange);
        verify(messageMarshallerDelegate).marshal(any(Storno.class), any(Result.class), eq(WitaCdmVersion.V2));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionCancelOrder");
        verify(schemaValidationService).validatePayload(any(Source.class));
    }

    private void prepareCamelMocks(WitaMessage originalMessage, StringResult payloadResult) {
        reset(exchange, soapMessageFactory, soapMessage, soapBody);

        when(exchange.getOut()).thenReturn(message);
        when(soapMessageFactory.createWebServiceMessage()).thenReturn(soapMessage);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        when(soapBody.getPayloadResult()).thenReturn(payloadResult);
        when(message.getBody()).thenReturn(originalMessage);
    }

}