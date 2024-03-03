package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.service.cc.impl.helper.VoIpDataHelper;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class AggregateFfmTechnicalDialUpAccessVoipCommandTest extends AbstractAggregateFfmCommandTest {

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalDialUpAccessVoipCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalDialUpAccessVoipCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(!((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }

    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        OrderTechnicalParams.DialUpAccessVoIP dialUpAccessVoIP = workforceOrder.getDescription().getTechParams().getDialUpAccessVoIP();
        Assert.assertNotNull(dialUpAccessVoIP);
        Assert.assertEquals(dialUpAccessVoIP.getAccount(), auftragDaten.getAuftragNoOrig() + "-VOIP");
        Assert.assertEquals(dialUpAccessVoIP.getPassword(), VoIpDataHelper.PPPOE_PW);

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }

}
