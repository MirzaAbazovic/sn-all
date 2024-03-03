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
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.VPNKonfigurationBuilder;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalVpnCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private ReferenceService referenceService;
    @Mock
    private VPNService vpnService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalVpnCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalVpnCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }


    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        Reference ref = new ReferenceBuilder().withRandomId().withStrValue("vpn type ref").setPersist(false).build();

        VPN vpn = new VPNBuilder()
                .withEinwahl("0123456")
                .withVpnType(ref.getId())
                .withVpnName("vpn name")
                .setPersist(false).build();
        when(vpnService.findVPNByAuftragId(anyLong())).thenReturn(vpn);

        VPNKonfiguration vpnKonf = new VPNKonfigurationBuilder().withVplsId("vpls id").setPersist(false).build();
        when(vpnService.findVPNKonfiguration4Auftrag(anyLong())).thenReturn(vpnKonf);

        when(referenceService.findReference(ref.getId())).thenReturn(ref);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        OrderTechnicalParams.VPN cdmVpn = workforceOrder.getDescription().getTechParams().getVPN();
        Assert.assertNotNull(cdmVpn);
        Assert.assertEquals(cdmVpn.getId(), String.format("%s", vpn.getVpnNr()));
        Assert.assertEquals(cdmVpn.getDialUp(), vpn.getEinwahl());
        Assert.assertEquals(cdmVpn.getName(), vpn.getVpnName());
        Assert.assertEquals(cdmVpn.getVPLSId(), vpnKonf.getVplsId());
        Assert.assertEquals(cdmVpn.getVpnType(), ref.getStrValue());
    }

}
