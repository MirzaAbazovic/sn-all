/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2009 09:57:50
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSIADData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.DeviceService;

/**
 * Command-Klasse, um Endgeraete-Daten fuer einen Auftrag zu ermitteln. <br> Die ermittelten Daten werden von dem
 * Command in einem XML-Element in der vom CPS erwarteten Struktur aufbereitet.
 *
 *
 */
public class CPSGetDeviceDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetDeviceDataCommand.class);

    // Anzahl der erwarteten Endgeraete mit PROV_SYSTEM=HURRICAN
    private static final int EXPECTED_DEVICE_COUNT = 1;

    @Resource(name = "de.augustakom.hurrican.service.billing.DeviceService")
    private DeviceService deviceService;

    /**
     * called by Spring
     */
    @Override
    public void init() throws ServiceNotFoundException {
    }

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            AuftragDaten ad = getAuftragDaten();

            if (deviceNecessary()) {
                // nur IADs ermitteln
                List<Device> devices = deviceService.findDevices4Auftrag(ad.getAuftragNoOrig(),
                        Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD);

                // ungueltige Devices ausfiltern
                CollectionUtils.filter(devices, obj -> ((Device) obj).isValid(getCPSTransaction().getEstimatedExecTime()));

                if (CollectionTools.isEmpty(devices)) {
                    // kein fehler mehr, nur eine warning
                    addWarning(this, "Dem Auftrag ist keine FritzBox zugeordnet ");
                }
                else {
                    return createDeviceData(devices);
                }
            }
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass(),
                    null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading IAD-Data: " + e.getMessage(), this.getClass());
        }
    }

    ServiceCommandResult createDeviceData(List<Device> devices) throws HurricanServiceCommandException, FindException {
        // ungueltige Devices ausfiltern
        CollectionUtils.filter(devices, obj -> ((Device) obj).isValid(getCPSTransaction().getEstimatedExecTime()));

        if (devices.size() != EXPECTED_DEVICE_COUNT) {
            throw new HurricanServiceCommandException(
                    "Count of devices for provisioning system {0} is invalid! Expected: {1};  Found: {2}",
                    new Object[] { Device.PROV_SYSTEM_HURRICAN, EXPECTED_DEVICE_COUNT, "" + devices.size() });
        }
        else {
            Device dev = devices.get(0);

            // IAD Daten generieren
            CPSIADData iadData = new CPSIADData();
            String deviceName = (StringUtils.isNotBlank(dev.getTechName())) ? dev.getTechName() : dev.getDevType();
            iadData.setType(StringUtils.deleteWhitespace(deviceName));
            iadData.setManufacturer(StringUtils.deleteWhitespace(dev.getManufacturer()));

            // Sonderfall FritzBox
            if (StringUtils.equals(dev.getDeviceExtension(), Device.DEVICE_EXTENSION_FRITZBOX)) {
                DeviceFritzBox fritzBox = deviceService.findDeviceFritzBox(dev.getDevNo());
                if (fritzBox != null) {
                    iadData.setCwmpId(StringUtils.deleteWhitespace(fritzBox.getCwmpId()));

                    if (StringUtils.isBlank(iadData.getCwmpId())) {
                        throw new HurricanServiceCommandException(
                                "CWMP-ID of Device is not defined (in Taifun)!");
                    }
                }
            }

            // IAD den ServiceOrderData zuordnen
            getServiceOrderData().setIad(iadData);

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass(),
                    null);
        }
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

}
