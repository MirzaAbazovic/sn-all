package de.mnet.wita.route.processor.lineorder.in;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.IncomingMessage;
import de.mnet.wita.service.WitaReceiveMessageService;

@Test(groups = BaseTest.UNIT)
public class StoreWitaMessageInProcessorTest extends BaseTest {

    @Mock
    private WitaReceiveMessageService witaReceiveMessageService;
    @Mock
    private ExchangeHelper exchangeHelper;
    @Mock
    private Exchange exchange;
    @Mock
    private IncomingMessage message;

    @InjectMocks
    @Spy
    private StoreWitaMessageInProcessor testling = new StoreWitaMessageInProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldProcessWitaMessageSuccessfully() throws Exception {
        doReturn(message).when(testling).getOriginalMessage(exchange);

        when(witaReceiveMessageService.handleWitaMessage(message)).thenReturn(true);

        testling.process(exchange);

        verify(witaReceiveMessageService).handleWitaMessage(message);
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void shouldProcessWitaMessageWithException() throws Exception {
        doReturn(message).when(testling).getOriginalMessage(exchange);

        when(witaReceiveMessageService.handleWitaMessage(message)).thenReturn(false);

        testling.process(exchange);
    }

}
