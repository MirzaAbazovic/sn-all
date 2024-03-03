package de.mnet.wita.route.processor.lineorder.in;

import static org.mockito.Mockito.*;

import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.WitaMessage;

@Test(groups = BaseTest.UNIT)
public class ConvertCdmV1ToWitaFormatProcessorTest extends BaseTest {

    @Mock
    private Message outMessage;
    @Mock
    private WitaMessage messageMock;

    @InjectMocks
    private ConvertCdmV1ToWitaFormatProcessor cut = new ConvertCdmV1ToWitaFormatProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testSetBody() throws Exception {
        cut.setBody(outMessage, messageMock);
        verify(outMessage).setBody(messageMock, WitaMessage.class);
    }
}
