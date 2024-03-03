/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2015
 */
package de.mnet.hurrican.webservice.resource.characteristics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mnet.esb.cdm.resource.resourcecharacteristicsservice.v1.ResourceCharacteristicsService;
import de.mnet.esb.cdm.resource.resourcecharacteristicsservice.v1.UpdateResourceCharacteristics;
import de.mnet.hurrican.webservice.resource.serialnumber.SerialNumberFFMService;

/**
 * Umsetzung von {@link de.mnet.esb.cdm.resource.resourcecharacteristicsservice.v1.ResourceCharacteristicsService} endpoint interface
 *
 */
@Service
public class ResourceCharacteristicsServiceImpl implements ResourceCharacteristicsService {

    @Autowired
    private SerialNumberFFMService serialNumberFFMService;

    @Override
    public void updateResourceCharacteristics(UpdateResourceCharacteristics in) {
        serialNumberFFMService.updateResourceCharacteristics(in.getResourceId(), in.getSerialNumber(),
                in.getInventory() != UpdateResourceCharacteristics.Inventory.HURRICAN);
    }

}
