package de.mnet.wita.route.processor.lineorder.out;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.jms.JmsConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.route.WitaCamelConstants;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.UNIT)
public class EvaluateCdmVersionProcessorTest extends BaseTest implements WitaCamelConstants {

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
        cut.lineOrderServiceOut = "version.depend.url.v%s.destination";
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.V2);
        when(exchange.getIn()).thenReturn(inMessage);
        cut.process(exchange);
        verify(inMessage).setHeader(JmsConstants.JMS_DESTINATION_NAME, "version.depend.url.v2.destination");
    }

}
