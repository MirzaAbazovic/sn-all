/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.command;

import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

/**
 *
 */
public interface CommandResourceInventoryWebserviceClient {

    void updateResource(HWRack rack) throws ResourceProcessException;

    /**
     * Relevant for serial number update only.
     * Otherwise use {@link CommandResourceInventoryWebserviceClient#updateResource(HWRack)}
     *
     * @param rack ONT or DPO resource
     * @throws ResourceProcessException
     */
    void updateOntSerialNumber(HWOnt ont) throws ResourceProcessException;

    void updateDpoSerialNumber(HWDpo dpo) throws ResourceProcessException;
}
