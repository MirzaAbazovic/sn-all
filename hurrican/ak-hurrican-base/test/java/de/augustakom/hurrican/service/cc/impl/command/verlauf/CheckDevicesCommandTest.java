package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CheckDevicesCommandTest {

    @Mock
    private DeviceService deviceService;
    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    @Spy
    CheckDevicesCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new CheckDevicesCommand() {
            @Override
            protected BAuftrag getBillingAuftrag() {
                final BAuftrag bAuftrag = new BAuftrag();
                bAuftrag.setAuftragNo(10L);
                return bAuftrag;
            }
        };
        initMocks(this);
    }

    @Test
    public void testOneActiveFritzBox() throws Exception {
        final Device device = validDeviceMock();
        when(deviceService.findDevices4Auftrag(anyLong(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(device));
        final ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNull(testling.getWarnings());
    }

    @Test
    public void testNoActiveFritzBox() throws Exception {
        when(deviceService.findDevices4Auftrag(anyLong(), anyString(), anyString()))
                .thenReturn(Collections.EMPTY_LIST);
        final ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(testling.getWarnings());
        Assert.assertTrue(testling.getWarnings().isNotEmpty());
    }

    @Test
    public void testSeveralActiveFritzBoxes() throws Exception {
        final List<Device> devicesList = Arrays.asList(validDeviceMock(), validDeviceMock());
        when(deviceService.findDevices4Auftrag(anyLong(), anyString(), anyString()))
                .thenReturn(devicesList);
        final ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(testling.getWarnings());
        Assert.assertTrue(testling.getWarnings().isNotEmpty());
    }

    @Test
    public void testPurchasedFritzBox() throws Exception {
        final Device device = spy(new Device());
        final Long orderNo = 10L;
        when(device.getPurchaseOrderNo()).thenReturn(orderNo);
        when(deviceService.findOrderedDevices4Auftrag(anyLong(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(device));
        final PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setStatus(PurchaseOrder.STATUS_CLOSED);  //done
        when(purchaseOrderService.findPurchaseOrder(orderNo)).thenReturn(purchaseOrder);

        final ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertTrue(result.isOk());
        Assert.assertNull(testling.getWarnings());
    }

    private Device validDeviceMock() {
        final Device validDevice = spy(new Device());
        when(validDevice.isValid(anyObject())).thenReturn(true);
        return validDevice;
    }

}
