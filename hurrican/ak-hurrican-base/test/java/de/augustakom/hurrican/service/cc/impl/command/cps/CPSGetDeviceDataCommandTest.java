/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceBuilder;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.model.billing.DeviceFritzBoxBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSIADData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;


/**
 * Unit Test for {@link CPSGetDeviceDataCommand}
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class CPSGetDeviceDataCommandTest extends AbstractHurricanBaseServiceTest {

    private static final Long TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String DEVICE_TECH_NAME = "AVM_FritzBox 7150";
    private static final String DEVICE_TECH_NAME_EXPECTED = "AVM_FritzBox7150";
    private static final String DEVICE_FB_CWMPID = "00040E-0024FE2EB3FA";
    private static final String DEVICE_FB_PASSPHRASE = "9rz4WbZRtgbW";

    private DeviceService deviceService;
    private Device device;
    private DeviceFritzBox deviceFB;
    private AuftragDaten auftragDaten;
    private CPSTransaction cpsTx;


    /**
     * Initialize the tests
     *
     * @throws FindException
     */
    @SuppressWarnings("unused")
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withAnschlussart("test")
                .withMaxDnCount(10)
                .withDNBlock(Boolean.FALSE);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        auftragDaten = auftragBuilder.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(TAIFUN_ORDER__NO)
                .withProdBuilder(produktBuilder)
                .build();

        auftragDaten.setProdId(Produkt.PROD_ID_MAXI_KOMPLETT_DELUXE);

        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withOrderNoOrig(TAIFUN_ORDER__NO)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        final EndstelleBuilder esBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(
                        getBuilder(AuftragTechnikBuilder.class)
                                .withAuftragBuilder(auftragBuilder)
                )
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        final HWBaugruppeBuilder bgBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(getBuilder(HWOntBuilder.class));

        final EquipmentBuilder eqBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(bgBuilder);

        getBuilder(RangierungBuilder.class)
                .withEndstelleBuilder(esBuilder)
                .withEqInBuilder(eqBuilder)
                .build();

        buildDeviceMocks();
    }


    private void buildDeviceMocks() throws FindException {
        Date validFrom = DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1);
        device = getBuilder(DeviceBuilder.class)
                .withTechName(DEVICE_TECH_NAME)
                .withValidFrom(validFrom)
                .withDeviceExtension(Device.DEVICE_EXTENSION_FRITZBOX)
                .withRandomId()
                .build();

        List<Device> devices = new ArrayList<Device>();
        devices.add(device);

        deviceFB = getBuilder(DeviceFritzBoxBuilder.class)
                .withCwmpId(DEVICE_FB_CWMPID)
                .withDeviceNo(device.getDevNo())
                .withPassphrase(DEVICE_FB_PASSPHRASE)
                .build();

        deviceService = mock(DeviceService.class);
        when(deviceService.findDevices4Auftrag(
                TAIFUN_ORDER__NO,
                Device.PROV_SYSTEM_HURRICAN,
                Device.DEVICE_CLASS_IAD))
                .thenReturn(devices);

        when(deviceService.findDeviceFritzBox(
                device.getDevNo()))
                .thenReturn(deviceFB);
    }

    @Test
    public void testExecute() throws Exception {
        CPSGetDeviceDataCommand dataCommand = (CPSGetDeviceDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDeviceDataCommand");
        dataCommand.setDeviceService(deviceService);

        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        dataCommand.prepare(CPSGetDSLDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        dataCommand.prepare(CPSGetDeviceDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        dataCommand.prepare(CPSGetDSLDataCommand.KEY_AUFTRAG_DATEN, auftragDaten);

        Object result = dataCommand.execute();

        assertTrue((result instanceof ServiceCommandResult),
                "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK,
                "ServiceCommand not successful!");

        assertNotNull(serviceOrderData.getIad(), "IAD not defined!");

        CPSIADData iad = serviceOrderData.getIad();
        assertEquals(iad.getManufacturer(), device.getManufacturer(), "Device manufacturer not as expected");
        assertEquals(iad.getType(), DEVICE_TECH_NAME_EXPECTED, "Device type not as expected");
        assertEquals(iad.getCwmpId(), DEVICE_FB_CWMPID, "CWMPID not as expected");
    }

}


