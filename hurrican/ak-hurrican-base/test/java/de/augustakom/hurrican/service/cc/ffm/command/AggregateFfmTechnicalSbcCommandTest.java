package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSetBuilder;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalSbcCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private SipPeeringPartnerService sipPeeringPartnerService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalSbcCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalSbcCommand();
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

        IPAddress ipAddress1 = new IPAddressBuilder().setPersist(false).build();
        IPAddress ipAddress2 = new IPAddressBuilder().setPersist(false).build();
        IPAddress ipAddress3 = new IPAddressBuilder().setPersist(false).build();

        SipSbcIpSet currentSet = new SipSbcIpSetBuilder()
                .withGueltigAb(Date.from(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant()))
                .withSbcIps(Arrays.asList(ipAddress1, ipAddress2))
                .build();
        SipSbcIpSet futureSet = new SipSbcIpSetBuilder()
                .withGueltigAb(Date.from(LocalDateTime.now().plusDays(50).atZone(ZoneId.systemDefault()).toInstant()))
                .withSbcIps(Arrays.asList(ipAddress3))
                .build();

        SipPeeringPartner peeringPartner = new SipPeeringPartnerBuilder()
                .withSbcIpSets(Arrays.asList(currentSet, futureSet))
                .build();

        when(sipPeeringPartnerService.findPeeringPartner4Auftrag(anyLong(), any(Date.class))).thenReturn(peeringPartner);

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.SBCAddress> sbcAddresses = workforceOrder.getDescription().getTechParams().getSBCAddress();
        assertNotEmpty(sbcAddresses);
        assertEquals(sbcAddresses.size(), 2);

    }

}
