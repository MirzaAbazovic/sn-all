package de.mnet.common.route;

import static org.mockito.Mockito.*;

import javax.xml.transform.*;
import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wbci.service.WbciSchemaValidationService;
import de.mnet.wita.WitaMessage;

@Test(groups = BaseTest.UNIT)
public class ConvertCdmToHurricanFormatProcessorTest extends BaseTest {

    @Mock
    private ExchangeHelper exchangeHelper;
    @Mock
    private org.apache.camel.Exchange exchange;
    @Mock
    private Message inMessage;
    @Mock
    private Message outMessage;
    @Mock
    private SoapMessageFactory soapMessageFactory;
    @Mock
    private SoapMessage soapMessage;
    @Mock
    private SoapBody soapBody;
    @Mock
    private Result result;
    @Mock
    private Unmarshaller messageUnmarshaller;

    @Mock
    private WbciSchemaValidationService schemaValidationService;

    @Mock
    private Source sourceMock;

    @Mock
    private WitaMessage messageMock;

    @InjectMocks
    private ConvertCdmToHurricanFormatProcessor cut = mock(ConvertCdmToHurricanFormatProcessor.class);

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testProcess() throws Exception {
        when(cut.getSoapMessageFactory()).thenReturn(soapMessageFactory);
        when(exchangeHelper.getSoapPayloadFromExchange(exchange, soapMessageFactory)).thenReturn(sourceMock);
        when(cut.getSchemaValidationService()).thenReturn(schemaValidationService);
        when(cut.getUnmarshaller()).thenReturn(messageUnmarshaller);
        when(messageUnmarshaller.unmarshal(sourceMock)).thenReturn(messageMock);
        when(exchange.getOut()).thenReturn(outMessage);
        when(exchange.getIn()).thenReturn(inMessage);

        doCallRealMethod().when(cut).process(exchange);
        cut.process(exchange);

        verify(schemaValidationService).validatePayload(sourceMock);
        verify(outMessage, times(1)).copyFrom(inMessage);
        verify(cut, times(1)).setBody(outMessage, messageMock);
    }
}