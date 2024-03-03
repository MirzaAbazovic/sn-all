package de.mnet.hurrican.atlas.simulator.wita;

import static org.mockito.Mockito.*;

import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.atlas.simulator.wita.builder.MockTestBuilder;
import de.mnet.hurrican.simulator.helper.XPathHelper;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WitaLineOrderSimulatorEndpointAdapterTest extends AbstractSimulatorBaseTest {

    @Autowired
    private WitaLineOrderSimulatorEndpointAdapter testling;

    private EndpointAdapter endpointAdapter = Mockito.mock(EndpointAdapter.class);

    private XPathHelper xPathHelperMock = Mockito.mock(XPathHelper.class);

    @Autowired
    @Qualifier("TEST_BUILDER_A")
    private MockTestBuilder testBuilderA;

    @Autowired
    @Qualifier("TEST_BUILDER_B")
    private MockTestBuilder testBuilderB;

    @Autowired
    @Qualifier("DEFAULT_BUILDER")
    private MockTestBuilder defaultBuilder;

    @BeforeClass
    public void setupTest() {
        testling.setResponseEndpointAdapter(endpointAdapter);
        testling.setXPathHelper(xPathHelperMock);
    }

    @BeforeMethod
    public void resetVersions() {
        testBuilderA.setServiceVersion(null);
        testBuilderB.setServiceVersion(null);
    }

    @Test
    public void testInterfaceVersion() throws Exception {
        reset(endpointAdapter);

        when(xPathHelperMock.evaluateAsString((Message) anyObject(), (String) anyObject())).thenReturn("22222222");

        when(endpointAdapter.handleMessage((Message) anyObject())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Message request = (Message) invocationOnMock.getArguments()[0];

                Assert.assertTrue(request.copyHeaders().containsKey(WitaLineOrderMessageHeaders.INTERFACE_VERSION));
                Assert.assertEquals(request.getHeader(WitaLineOrderMessageHeaders.INTERFACE_VERSION), WitaLineOrderServiceVersion.V1);
                Assert.assertTrue(request.copyHeaders().containsKey(WitaLineOrderMessageHeaders.INTERFACE_NAMESPACE));
                Assert.assertEquals(request.getHeader(WitaLineOrderMessageHeaders.INTERFACE_NAMESPACE), WitaLineOrderServiceVersion.V1.getNamespace());

                return new DefaultMessage("OK");
            }
        });

        Assert.assertNull(testBuilderA.getServiceVersion());
        testling.handleMessage(buildTestRequest(WitaLineOrderServiceVersion.V1, "TEST_BUILDER_A"));
        Assert.assertEquals(testBuilderA.getServiceVersion(), WitaLineOrderServiceVersion.V1);
    }

    @Test
    public void testDefaultBehavior() throws Exception {
        reset(endpointAdapter);

        when(xPathHelperMock.evaluateAsString((Message) anyObject(), (String) anyObject())).thenReturn("33333333");

        when(endpointAdapter.handleMessage((Message) anyObject())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new DefaultMessage("OK");
            }
        });

        Assert.assertNull(defaultBuilder.getServiceVersion());
        testling.handleMessage(buildTestRequest(WitaLineOrderServiceVersion.V1, "UNKNOWN"));
        Assert.assertEquals(defaultBuilder.getServiceVersion(), WitaLineOrderServiceVersion.V1);
    }

    private Message buildTestRequest(WitaLineOrderServiceVersion version, String builderName) {
        return new DefaultMessage("<vX:annehmenAuftragRequest xmlns:vX=\"" + version.getNamespace() + "\">" +
                "<test>" + builderName + "</test>" +
                "</vX:annehmenAuftragRequest>");
    }
}
