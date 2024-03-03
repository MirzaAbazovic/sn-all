package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.model.cc.IPSecSite2SiteBuilder;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalIPSecS2SCommandTest  extends AbstractAggregateFfmCommandTest {

    @Mock
    private IPSecService ipSecService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalIPSecS2SCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalIPSecS2SCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertFalse(((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }


    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        IPSecSite2Site ipSecSite2Site = new IPSecSite2SiteBuilder().setPersist(false).build();
        when(ipSecService.findIPSecSiteToSite(anyLong())).thenReturn(ipSecSite2Site);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        OrderTechnicalParams.IPSecS2S ipSecS2S = workforceOrder.getDescription().getTechParams().getIPSecS2S();
        Assert.assertNotNull(ipSecS2S);
        Assert.assertEquals(ipSecS2S.getHostName(), ipSecSite2Site.getHostname());
        Assert.assertEquals(ipSecS2S.getWANSubnet(), ipSecSite2Site.getVirtualWanSubmask());
        Assert.assertEquals(ipSecS2S.getVLANIp(), ipSecSite2Site.getVirtualLanIp());
    }

}