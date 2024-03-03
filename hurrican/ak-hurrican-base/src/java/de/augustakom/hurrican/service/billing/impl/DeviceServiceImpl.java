/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 09:08:00
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.dao.billing.DeviceDAO;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;


/**
 * Service-Implementierung von <code>DeviceService</code>
 *
 *
 */
@BillingTx
public class DeviceServiceImpl extends DefaultBillingService implements DeviceService {

    private static final Logger LOGGER = Logger.getLogger(DeviceServiceImpl.class);

    @Override
    public DeviceFritzBox findDeviceFritzBox(Long devNo) throws FindException {
        if (devNo == null) { return null; }
        try {
            return ((DeviceDAO) getDAO()).findById(devNo, DeviceFritzBox.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<Device> findDevices4Auftrag(Long auftragNoOrig, String provisioningSystem,
            String deviceClass) throws FindException {
        if (auftragNoOrig == null) { return null; }
        try {
            return ((DeviceDAO) getDAO()).findDevices4Auftrag(auftragNoOrig, provisioningSystem, deviceClass);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Device> findOrderedDevices4Auftrag(Long auftragNoOrig, String provisioningSystem,
            String deviceClass) throws FindException {
        if (auftragNoOrig == null) { return null; }
        try {
            return ((DeviceDAO) getDAO()).findOrderedDevices4Auftrag(auftragNoOrig, provisioningSystem, deviceClass);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}


