/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.14
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceBuilder;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.model.billing.PurchaseOrderBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.CommandBaseTest;

@Test(groups = { BaseTest.UNIT })
public class CheckPortCpeCombinationFttxTelefonCommandTest extends CommandBaseTest {

    @Mock
    private EndstellenService endstellenService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private DeviceService deviceService;
    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    @Spy
    CheckPortCpeCombinationFttxTelefonCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new CheckPortCpeCombinationFttxTelefonCommand();
        initMocks(this);
    }

    public void noEndstelle() throws Exception {
        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(null);
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains("Endstelle B des Auftrags konnte nicht ermittelt werden"));
    }

    public void noRangierung() throws Exception {
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains("Der Endstelle B des Auftrags ist keine Rangierung zugeordnet"));
    }

    public void rangierungNotFound() throws Exception {
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId().setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withRangierungBuilder(rangierungBuilder)
                .setPersist(false).build();
        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        when(rangierungsService.findRangierung(rangierungBuilder.get().getId())).thenReturn(null);
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains(
                String.format("Rangierung mit Id %s wurde nicht gefunden", rangierungBuilder.get().getId())));
    }

    public void noEquipmentId() throws Exception {
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId().setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withRangierungBuilder(rangierungBuilder)
                .setPersist(false).build();
        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        when(rangierungsService.findRangierung(rangierungBuilder.get().getId())).thenReturn(rangierungBuilder.get());
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains("Rangierung besitzt keinen EQ-In Port"));
    }


    public void potsButFritzBoxAssigned() throws Exception {
        returnValidEquipment(HWBaugruppenTyp.HW_SCHNITTSTELLE_POTS);
        doReturn(true).when(testling).isFritzBoxAssigned(anyListOf(Device.class));
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains("Dem Auftrag ist eine FritzBox zugeordnet"));
    }

    public void vdslButNoFritzBox() throws Exception {
        returnValidEquipment(HWBaugruppenTyp.HW_SCHNITTSTELLE_VDSL2);
        doReturn(false).when(testling).isFritzBoxAssigned(anyListOf(Device.class));
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains("Dem Auftrag ist keine FritzBox zugeordnet"));

        verify(testling, times(0)).isFritzBoxPurchaseOrderActivated(anyListOf(Device.class));
    }

    public void vdslButFritzBoxOrderNotActivated() throws Exception {
        returnValidEquipment(HWBaugruppenTyp.HW_SCHNITTSTELLE_VDSL2);
        doReturn(true).when(testling).isFritzBoxAssigned(anyListOf(Device.class));
        doReturn(false).when(testling).isFritzBoxPurchaseOrderActivated(anyListOf(Device.class));
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertTrue(result.getMessage().contains("die Lieferung ist noch nicht aktiviert"));

        verify(testling).isFritzBoxPurchaseOrderActivated(anyListOf(Device.class));
    }

    public void vdslAndFritzBoxAndOrderActivated() throws Exception {
        returnValidEquipment(HWBaugruppenTyp.HW_SCHNITTSTELLE_VDSL2);
        doReturn(true).when(testling).isFritzBoxAssigned(anyListOf(Device.class));
        doReturn(true).when(testling).isFritzBoxPurchaseOrderActivated(anyListOf(Device.class));
        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
        assertNull(result.getMessage());

        verify(testling).isFritzBoxPurchaseOrderActivated(anyListOf(Device.class));
    }

    private void returnValidEquipment(String hwSchnittstelle) throws FindException {
        EquipmentBuilder equipmentBuilder = new EquipmentBuilder()
                .withRandomId()
                .withHwSchnittstelle(hwSchnittstelle)
                .setPersist(false);

        RangierungBuilder rangierungBuilder = new RangierungBuilder()
                .withRandomId()
                .withEqInBuilder(equipmentBuilder)
                .setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withRangierungBuilder(rangierungBuilder)
                .setPersist(false).build();
        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        when(rangierungsService.findRangierung(rangierungBuilder.get().getId())).thenReturn(rangierungBuilder.get());
        when(rangierungsService.findEquipment(rangierungBuilder.get().getEqInId())).thenReturn(equipmentBuilder.get());
        doReturn(new BAuftrag()).when(testling).getBillingAuftrag();
    }


    @DataProvider(name = "isFritzBoxOrderActivatedDP")
    public Object[][] isFritzBoxOrderActivatedDP() {
        Device deviceWithoutPurchaseOrder = new DeviceBuilder().setPersist(false).build();
        Device deviceWithPurchaseOrder = new DeviceBuilder().withPurchaseOrderNo(1L).setPersist(false).build();
        PurchaseOrder purchaseOrderNew = new PurchaseOrderBuilder().withStatus(PurchaseOrder.STATUS_NEW).setPersist(false).build();
        PurchaseOrder purchaseOrderPlanned = new PurchaseOrderBuilder().withStatus(PurchaseOrder.STATUS_PLANNED).setPersist(false).build();
        PurchaseOrder purchaseOrderCreated = new PurchaseOrderBuilder().withStatus(PurchaseOrder.STATUS_CREATED).setPersist(false).build();
        PurchaseOrder purchaseOrderPartial = new PurchaseOrderBuilder().withStatus(PurchaseOrder.STATUS_PARTIAL).setPersist(false).build();
        PurchaseOrder purchaseOrderClosed = new PurchaseOrderBuilder().withStatus(PurchaseOrder.STATUS_CLOSED).setPersist(false).build();

        return new Object[][] {
                { Arrays.asList(deviceWithoutPurchaseOrder), null, false },
                { Arrays.asList(deviceWithPurchaseOrder), purchaseOrderPlanned, false },
                { Arrays.asList(deviceWithPurchaseOrder), purchaseOrderNew, true },
                { Arrays.asList(deviceWithPurchaseOrder), purchaseOrderCreated, true },
                { Arrays.asList(deviceWithPurchaseOrder), purchaseOrderPartial, true },
                { Arrays.asList(deviceWithPurchaseOrder), purchaseOrderClosed, true },
                { Arrays.asList(deviceWithoutPurchaseOrder, deviceWithPurchaseOrder), purchaseOrderClosed, true },
        };
    }

    @Test(dataProvider = "isFritzBoxOrderActivatedDP")
    public void isFritzBoxOrderActivated(List<Device> devices, PurchaseOrder purchaseOrder, boolean expectedResult) throws FindException {
        when(purchaseOrderService.findPurchaseOrder(anyLong())).thenReturn(purchaseOrder);
        assertEquals(testling.isFritzBoxPurchaseOrderActivated(devices), expectedResult);
    }

}
