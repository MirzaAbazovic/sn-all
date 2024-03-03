/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.2015
 */
package de.mnet.hurrican.webservice.resource.serialnumber;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.inventory.service.DpoSerialNumberResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.OntSerialNumberResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.ResourceProviderService;

/**
 * Serial Number Service
 * {@see SerialNumberFFMService}
 *
 */
@CcTxRequired
@Service
public class SerialNumberFFMServiceImpl implements SerialNumberFFMService {
    private static final Logger LOGGER = Logger.getLogger(SerialNumberFFMServiceImpl.class);

    @Autowired
    protected HWService hwService;
    @Autowired
    protected ResourceProviderService resourceProviderService;
    @Autowired
    protected SerialNumberErrorHandler errorHandler;
    @Autowired
    protected SerialNumberCmdSender cmdSender;

    /**
     * {@see SerialNumberFFMService#updateResourceCharacteristics(String, String)}
     */
    @Override
    public void updateResourceCharacteristics(String resourceId, String serialNumber, boolean falseRouting) {
        if (falseRouting) {
            errorHandler.storeErrorLogEntry(resourceId, serialNumber,
                    "UpdateResourceCharacteristics / Nachricht ist nicht an Hurrican gerichtet",
                    "", "");
        }
        else if (StringUtils.isEmpty(resourceId)) {
            errorHandler.storeErrorLogEntry(resourceId, serialNumber, "UpdateResourceCharacteristics / Leere Ressourcen-ID", "", "");
        } else if (StringUtils.isEmpty(serialNumber)) {
            errorHandler.storeErrorLogEntry(resourceId, serialNumber, "UpdateResourceCharacteristics / Leere Seriennummer", "", "");
        } else {
            final HWRack rackEntity = findActiveRackByBezeichnung(resourceId, serialNumber);
            if (rackEntity != null) {
                if (rackEntity instanceof HWOnt || rackEntity instanceof HWDpo) {
                    // need a copy because original rackEntity is managed inside transaction
                    final HWRack rackCopy = (HWRack) SerializationUtils.clone(rackEntity);
                    final HWOltChild modifiedRackCopy = modifyRackSerialNumber(serialNumber, (HWOltChild) rackCopy);
                    final boolean sendSuccessful = cmdSender.sendRackSerialNumberToCmd(modifiedRackCopy);
                    if (sendSuccessful) {
                        // need to do provisionierung first before hardware is stored in the DB
                        // otherwise it does not create CPS
                        provisionierungCps(modifiedRackCopy);
                        // modifies managed entity and stores
                        final HWOltChild modifiedManagedEntity = modifyRackSerialNumber(serialNumber, (HWOltChild) rackEntity);
                        storeRackInDatabase(modifiedManagedEntity);
                    }
                } else {
                    errorHandler.storeErrorLogEntry(resourceId, serialNumber, "UpdateResourceCharacteristics / Falscher Hardwaretyp", "", "");
                }
            } else {
                errorHandler.storeErrorLogEntry(resourceId, serialNumber, "UpdateResourceCharacteristics / Hardware nicht gefunden", "", "");
            }
        }
    }

    /**
     * Updates rack model with serial number
     * @param serialNumber
     * @param rack
     * @return updated model
     */
    private HWOltChild modifyRackSerialNumber(String serialNumber, HWOltChild rack) {
        final HWOltChild updatedRack = rack;
        updatedRack.setSerialNo(serialNumber);
        return updatedRack;
    }

    /**
     * Stores given rack in the database
     * @param rack
     */
    private void storeRackInDatabase(HWOltChild rack) {
        try {
            hwService.saveHWRack(rack);
        }
        catch (StoreException e) {
            LOGGER.error("Exception by storing rack in database", e);
            errorHandler.storeErrorLogEntry(rack.getGeraeteBez(), rack.getSerialNo(), "UpdateResourceCharacteristics / Datenbankfehler bei Speichern",
                    e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
        catch (ValidationException e) {
            LOGGER.error("Exception by validating rack before storing in database", e);
            errorHandler.storeErrorLogEntry(rack.getGeraeteBez(), rack.getSerialNo(), "UpdateResourceCharacteristics / Validierungsfehler bei Speichern",
                    e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private void provisionierungCps(HWOltChild rack) {
        final Long globalSessionId = HurricanScheduler.getSessionId();
        final Resource res = toResource(rack);
        if (res != null) {
            try {
                resourceProviderService.updateResource(res, globalSessionId);
            }
            catch (ResourceProcessException e) {
                LOGGER.error("Error by Provisionierung via CPS ", e);
                errorHandler.storeErrorLogEntry(rack.getGeraeteBez(), rack.getSerialNo(), "UpdateResourceCharacteristics / Provisionierung über CPS Fehler",
                        e.getMessage(), ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private Resource toResource(HWOltChild rack) {
        try {
            if (rack instanceof HWDpo) {
                // need to map serial number here
                return (new DpoSerialNumberResourceMapper()).toResource(rack);
            } else if (rack instanceof HWOnt) {
                // need to map serial number here
                return (new OntSerialNumberResourceMapper()).toResource(rack);
            }
        }
        catch (ResourceProcessException e) {
            LOGGER.error("Exception by mapping resource", e);
            errorHandler.storeErrorLogEntry(rack.getGeraeteBez(), rack.getSerialNo(), "UpdateResourceCharacteristics / Resource mapping error",
                    e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * Retrieves active rack by resource id
     * @param resourceId geraete bez
     * @param serialNumber
     * @return HW Rack model
     */
    private HWRack findActiveRackByBezeichnung(String resourceId, String serialNumber) {
        try {
            return hwService.findActiveRackByBezeichnung(resourceId);
        }
        catch (FindException e) {
            final String message = String.format("Exception by finding rack by bezeichung [%s]", resourceId);
            LOGGER.error(message, e);
            errorHandler.storeErrorLogEntry(resourceId, serialNumber, "UpdateResourceCharacteristics / Fehler beim Laden aus der Datenbank", e.getMessage(), ExceptionUtils.getStackTrace(e));
            return null;
        }
    }


}
