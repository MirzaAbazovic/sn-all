/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2015
 */
package de.mnet.hurrican.webservice.resource.serialnumber;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.hurrican.webservice.resource.inventory.command.CommandResourceInventoryWebserviceClient;

/**
 *
 */
@CcTxRequired
@Service
public class SerialNumberCmdSender implements ICCService {

    private static final Logger LOGGER = Logger.getLogger(SerialNumberFFMServiceImpl.class);

    @Autowired
    protected CommandResourceInventoryWebserviceClient resourceInventoryClient;
    @Autowired
    protected SerialNumberErrorHandler errorHandler;

    /**
     * Sends rack to cmd
     * @param rack
     * @return true if sent successfully
     */
    public boolean sendRackSerialNumberToCmd(HWOltChild rack) {
        try {
            if (rack instanceof HWOnt) {
                resourceInventoryClient.updateOntSerialNumber((HWOnt) rack);
            } else if (rack instanceof HWDpo) {
                resourceInventoryClient.updateDpoSerialNumber((HWDpo) rack);
            }
            return true;
        }
        catch (Exception e) {
            // handle all exceptions from cmd here!
            LOGGER.error("Exception by sending serial number to CMD", e);
            errorHandler.storeErrorLogEntry(rack.getGeraeteBez(), rack.getSerialNo(), "UpdateResourceCharacteristics / Fehler bei CMD", e.getMessage(), ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}
