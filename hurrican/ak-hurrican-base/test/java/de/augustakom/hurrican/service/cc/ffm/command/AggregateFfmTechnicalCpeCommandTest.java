/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.ACLBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetAclBuilder;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalCpeCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private DeviceService deviceService;
    @Mock
    private EndgeraeteService endgeraeteService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalCpeCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalCpeCommand();
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

        Device device = new DeviceBuilder().withDevType("AVM Fritz!Box FON WLAN 7360").setPersist(false).build();
        when(deviceService.findDevices4Auftrag(anyLong(), isNull(String.class), isNull(String.class)))
                .thenReturn(Arrays.asList(device));

        EG2AuftragView eg2AuftragView = new EG2AuftragView();
        eg2AuftragView.setEg2AuftragId(1L);
        eg2AuftragView.setEgName("SET8 Turbolink AR871");
        eg2AuftragView.setSeriennummer("serial-no");
        when(endgeraeteService.findEG2AuftragViews(anyLong())).thenReturn(Arrays.asList(eg2AuftragView));
        String aclName = "BEVV3";
        String routerTyp = "Thomson STP605s";
        when(endgeraeteService.findEGConfig(eg2AuftragView.getEg2AuftragId()))
                .thenReturn(
                        new EGConfigBuilder()
                                .withEndgeraetAcl(
                                        new EndgeraetAclBuilder()
                                                .withName(aclName)
                                                .withRouterTyp(routerTyp)
                                                .setPersist(false)
                                                .build()
                                )
                                .setPersist(false)
                                .build()
                );

        doReturn(new AuftragDatenBuilder().withAuftragNoOrig(1L).build()).when(testling).getAuftragDaten();

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.CPE> cpes = workforceOrder.getDescription().getTechParams().getCPE();
        assertNotNull(cpes);
        assertTrue(cpes.size() == 2);
        assertTrue(cpes.get(0).getACL().size() == 0);
        List<OrderTechnicalParams.CPE.ACL> acls = cpes.get(1).getACL();
        assertTrue(acls.size() == 1);
        assertEquals(acls.get(0).getName(), aclName);
        assertEquals(acls.get(0).getRouterType(), routerTyp);

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
        verify(deviceService).findDevices4Auftrag(anyLong(), isNull(String.class), isNull(String.class));
        verify(endgeraeteService).findEG2AuftragViews(anyLong());
        verify(endgeraeteService).findEGConfig(eg2AuftragView.getEg2AuftragId());
    }

    @DataProvider
    private Object[][] convertDataProvider() {
        return new Object[][] {
                {null, null},
                {Collections.EMPTY_SET, Collections.EMPTY_LIST},
                {new HashSet<>(Arrays.asList(createEndgeraetAcl("TRUV1", "Thomson STP520s"), createEndgeraetAcl("BEVcisco", "Cisco Router"))),
                        Arrays.asList(createACL("BEVcisco", "Cisco Router"), createACL("TRUV1", "Thomson STP520s"))},
                {new HashSet<>(Arrays.asList(createEndgeraetAcl("TRUV1", "Thomson STP520s"), createEndgeraetAcl("BEVcisco", "Cisco Router"))),
                        Arrays.asList(createACL("TRUV1", "Thomson STP520s"), createACL("BEVcisco", "Cisco Router"))},
        };
    }

    @Test(dataProvider = "convertDataProvider")
    public void testConvert(Set<EndgeraetAcl> endgeraetAcls, List<OrderTechnicalParams.CPE.ACL> expectedAcls) {
        List<OrderTechnicalParams.CPE.ACL> convert = testling.convert(endgeraetAcls);
        verifyAclList(convert, expectedAcls);
    }

    private void verifyAclList(List<OrderTechnicalParams.CPE.ACL> actual, List<OrderTechnicalParams.CPE.ACL> expected) {
        if (CollectionUtils.isNotEmpty(expected)) {
            assertEquals(actual.size(), expected.size());
            for (OrderTechnicalParams.CPE.ACL acl : expected) {
                verifyAclInList(actual, acl);
            }
            for (OrderTechnicalParams.CPE.ACL acl : actual) {
                verifyAclInList(expected, acl);
            }
        }
        else {
            assertEquals(actual, expected);
        }
    }

    private void verifyAclInList(List<OrderTechnicalParams.CPE.ACL> actual, OrderTechnicalParams.CPE.ACL expected) {
        boolean found = false;
        for (OrderTechnicalParams.CPE.ACL acl : actual) {
            if (acl.getName().equals(expected.getName()) && acl.getRouterType().equals(expected.getRouterType())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    private EndgeraetAcl createEndgeraetAcl(String name, String routerTyp) {
        return new EndgeraetAclBuilder()
                .withName(name)
                .withRouterTyp(routerTyp)
                .setPersist(false)
                .build();
    }

    private OrderTechnicalParams.CPE.ACL createACL(String name, String routerTyp) {
        return new ACLBuilder()
                .withName(name)
                .withRouterType(routerTyp)
                .build();
    }

}
