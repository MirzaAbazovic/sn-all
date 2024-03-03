/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import de.augustakom.hurrican.model.cc.view.OltChildPortImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

/**
 *
 */
public abstract class AbstractOltChildPortResourceMapper<OLT_CHILD_PORT extends OltChildPortImportView> extends AbstractResourceMapper implements ResourceMapper {

    public static final String CHARACTERISTIC_SCHNITTSTELLE = "schnittstelle";

    @javax.annotation.Resource(name = "de.augustakom.hurrican.service.cc.FTTXHardwareService")
    protected FTTXHardwareService fttxHardwareService;

    protected abstract String getResourceSpecId();
    protected abstract OLT_CHILD_PORT createOltChild();
    protected abstract void generateOltChildPort(final OLT_CHILD_PORT oltChildPort, final Long sessionId) throws StoreException;

    protected OLT_CHILD_PORT mapOltChildPortResource(final Resource in) {
        OLT_CHILD_PORT out = createOltChild();
        out.setOltChild(in.getParentResource().getId());
        out.setPort(in.getName());
        for (ResourceCharacteristic characteristic : in.getCharacteristic()) {
            switch (characteristic.getName().toLowerCase()) {
                case CHARACTERISTIC_SCHNITTSTELLE:
                    out.setSchnittstelle(extractString(characteristic.getValue(), 0));
                    break;
                default:
                    break;
            }
        }
        return out;
    }

    public void processResource(final Resource ontResource, final Long sessionId) throws ResourceProcessException {
        if (!isResourceSupported(ontResource)) {
            throw new ResourceProcessException("Resource Verarbeitung abgelehnt!");
        }
        try {
            generateOltChildPort(mapOltChildPortResource(ontResource), sessionId);
        }
        catch (StoreException e) {
            throw new ResourceProcessException(e.getMessage(), e);
        }
    }

    public boolean isResourceSupported(final Resource resource) {
        if (resource.getResourceSpec() == null
                || resource.getResourceSpec().getId() == null
                || resource.getResourceSpec().getInventory() == null) {
            return false;
        }
        return getResourceSpecId().equals(resource.getResourceSpec().getId())
                && COMMAND_INVENTORY.equals(resource.getResourceSpec().getInventory());
    }

}
