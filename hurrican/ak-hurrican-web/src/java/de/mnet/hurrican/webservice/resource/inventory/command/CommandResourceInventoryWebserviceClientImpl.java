/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2014 14:36:27
 */
package de.mnet.hurrican.webservice.resource.inventory.command;

import java.util.*;

import javax.annotation.*;
import javax.inject.*;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.impl.AbstractHurricanWebServiceClient;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResource;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.inventory.service.DpoSerialNumberResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.OntSerialNumberResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.RackResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.ResourceMapper;

/**
 * Command um Aufrufe an den Command ResourceInventory WS abzuschicken.
 *
 *
 */
@Component("commandResourceInventoryWebserviceClient")
@CcTxRequired
public class CommandResourceInventoryWebserviceClientImpl extends AbstractHurricanWebServiceClient implements CommandResourceInventoryWebserviceClient {

    @javax.annotation.Resource(name = "command.resourceinventory.WebServiceTemplate")
    private MnetWebServiceTemplate commandResourceInventoryWebServiceTemplate;

    @Inject
    private List<RackResourceMapper> resourceMappers;

    @PostConstruct
    protected WebServiceTemplate configureAndGetWSTemplate() throws ServiceNotFoundException {
        return configureAndGetWsTemplateForConfig(WebServiceConfig.WS_CFG_COMMAND_RESOURCE_INVENTORY,
                commandResourceInventoryWebServiceTemplate);
    }

    /**
     * "updateResource" absenden. Fuer die Konvertierung von Rack -> Resource wird der konfigurierte {@link
     * ResourceMapper} verwendet.
     *
     * @param rack
     * @throws ResourceProcessException
     */
    @Override
    public void updateResource(HWRack rack) throws ResourceProcessException {
        try {
            for (RackResourceMapper mapper : resourceMappers) {
                if (mapper.isRackSupported(rack)) {
                    updateResourceUsingMapper(rack, mapper);
                    return;
                }
            }
            throw new ResourceProcessException("kein Mapper gefunden, rackTyp=" + rack.getRackTyp() + " id="
                    + rack.getId());
        }
        catch (ResourceProcessException r) {
            throw r;
        }
        catch (Exception e) {
            throw new ResourceProcessException("updateResource failed", e);
        }
    }

    private void updateResourceUsingMapper(HWRack rack, RackResourceMapper mapper) throws ResourceProcessException {
        UpdateResource updateResource = new UpdateResource();
        updateResource.getResource().add(mapper.toResource(rack));
        commandResourceInventoryWebServiceTemplate.marshalSendAndReceive(updateResource);
    }

    /**
     * {@see CommandResourceInventoryWebserviceClient#updateOntSerialNumber()}
     */
    @Override
    public void updateOntSerialNumber(HWOnt ont) throws ResourceProcessException {
        try {
            updateResourceUsingMapper(ont, new OntSerialNumberResourceMapper());
        }
        catch (ResourceProcessException r) {
            throw r;
        }
        catch (Exception e) {
            throw new ResourceProcessException("updateResource failed", e);
        }
    }

    /**
     * {@see CommandResourceInventoryWebserviceClient#updateDpoSerialNumber()}
     */
    @Override
    public void updateDpoSerialNumber(HWDpo dpo) throws ResourceProcessException {
        try {
            updateResourceUsingMapper(dpo, new DpoSerialNumberResourceMapper());
        }
        catch (ResourceProcessException r) {
            throw r;
        }
        catch (Exception e) {
            throw new ResourceProcessException("updateResource failed", e);
        }

    }
}
