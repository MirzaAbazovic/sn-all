/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalIpAddressCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private IPAddressService ipAddressService;

    @Mock
    private EndgeraeteService egService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalIpAddressCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalIpAddressCommand();
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


    @DataProvider(name = "ipDataProvider")
    public Object[][] ipDataProvider() {
        IPAddress ipV4 = new IPAddressBuilder().withAddressType(AddressTypeEnum.IPV4).withAddress("10.10.10.10")
                .withGueltigVon(DateConverterUtils.asDate(LocalDateTime.now().minusYears(110)))
                .withGueltigBis(DateConverterUtils.asDate(LocalDateTime.now().plusYears(110)))
                .setPersist(false).build();
        IPAddress ipV6 = new IPAddressBuilder().setPersist(false)
                .withGueltigVon(DateConverterUtils.asDate(LocalDateTime.now().minusYears(110)))
                .withGueltigBis(DateConverterUtils.asDate(LocalDateTime.now().plusYears(110))).build();
        IPAddress ipValidToBeforeBauauftragRealDate = new IPAddressBuilder()
                .withGueltigBis(DateConverterUtils.asDate(LocalDateTime.now().minusYears(110)))
                .withGueltigBis(DateConverterUtils.asDate(LocalDateTime.now().minusYears(100)))
                .setPersist(false).build();
        IPAddress ipValidAfterBauauftragRealDate = new IPAddressBuilder()
                .withGueltigVon(DateConverterUtils.asDate(LocalDateTime.now().plusYears(100)))
                .setPersist(false).build();

        return new Object[][]{
                { ipV4, true },
                { ipV6, true },
                { ipValidToBeforeBauauftragRealDate, false },
                { ipValidAfterBauauftragRealDate, false }
        };
    }

    @Test(dataProvider = "ipDataProvider")
    public void testLoadIPsFromAuftrag(IPAddress ip, boolean resultExpected) throws Exception {
        prepareFfmCommand(testling, true);

        IPAddressPanelView view = new IPAddressPanelView();
        view.setIpAddress(ip);
        when(ipAddressService.findAllIPAddressPanelViews(anyLong())).thenReturn(Arrays.asList(view));

        doReturn(new AuftragDatenBuilder().withAuftragNoOrig(1L).build()).when(testling).getAuftragDaten();

        testling.loadIPsFromAuftrag();

        List<OrderTechnicalParams.IPAddress> ipAddresses = workforceOrder.getDescription().getTechParams().getIPAddress();
        Assert.assertNotNull(ipAddresses);
        Assert.assertTrue(ipAddresses.size() == ((resultExpected) ? 1 : 0));

        if (resultExpected) {
            Assert.assertEquals(ipAddresses.get(0).getAddress(), ip.getAddress());
            Assert.assertEquals(ipAddresses.get(0).getVersion(), (ip.isIPV4()) ? "v4" : "v6");
            Assert.assertNotNull(ipAddresses.get(0).getType());
        }
    }



    public void testLoadIPsFromEgConfig() throws FindException {
        prepareFfmCommand(testling, true);

        EndgeraetIp egIpWan = new EndgeraetIp();
        egIpWan.setIpAddressRef(new IPAddressBuilder().setPersist(false).build());
        egIpWan.setAddressType(EndgeraetIp.AddressType.WAN);

        EndgeraetIp egIpLan = new EndgeraetIp();
        egIpLan.setIpAddressRef(new IPAddressBuilder().withAddressType(AddressTypeEnum.IPV4).setPersist(false).build());
        egIpLan.setAddressType(EndgeraetIp.AddressType.LAN);

        Set<EndgeraetIp> endgeraetIps = new HashSet<>();
        endgeraetIps.add(egIpWan);
        endgeraetIps.add(egIpLan);

        EG2Auftrag eg2a = new EG2AuftragBuilder()
                .withEndgeraetIps(endgeraetIps)
                .setPersist(false).build();

        EGConfig egConfig = new EGConfigBuilder()
                .withWanIpFest(Boolean.TRUE)
                .setPersist(false).build();

        when(egService.findEGs4Auftrag(anyLong())).thenReturn(Arrays.asList(eg2a));
        when(egService.findEGConfig(anyLong())).thenReturn(egConfig);

        testling.loadIPsFromEgConfig();

        List<OrderTechnicalParams.IPAddress> ipAddresses = workforceOrder.getDescription().getTechParams().getIPAddress();
        Assert.assertNotNull(ipAddresses);
        Assert.assertTrue(ipAddresses.size() == 2);
        for (OrderTechnicalParams.IPAddress ip : ipAddresses) {
            if (ip.getType().contains("WAN")) {
                Assert.assertEquals(ip.getVersion(), "v6");
                Assert.assertEquals(ip.getAddress(), egIpWan.getIpAddressRef().getAddress());
                Assert.assertEquals(ip.getType(), "M-net Endgerät WAN (V6) fest konfiguriert");
            }
            else {
                Assert.assertEquals(ip.getVersion(), "v4");
                Assert.assertEquals(ip.getAddress(), egIpWan.getIpAddressRef().getAddress());
                Assert.assertEquals(ip.getType(), "M-net Endgerät LAN (V4)");
            }
        }
    }


    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);
        doNothing().when(testling).loadIPsFromAuftrag();
        doNothing().when(testling).loadIPsFromEgConfig();

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
        verify(testling).loadIPsFromAuftrag();
        verify(testling).loadIPsFromEgConfig();
    }

}