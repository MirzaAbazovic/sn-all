/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import de.augustakom.hurrican.model.cc.view.OltChildKarteImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

/**
 *
 */
public abstract class AbstractOltChildKarteResourceMapper<OLT_CHILD_KARTE extends OltChildKarteImportView> extends AbstractResourceMapper implements ResourceMapper {

    @javax.annotation.Resource(name = "de.augustakom.hurrican.service.cc.FTTXHardwareService")
    protected FTTXHardwareService fttxHardwareService;

    protected abstract String getResourceSpecId();
    protected abstract OLT_CHILD_KARTE createOltChild();
    protected abstract void generateOltChildKarte(final OLT_CHILD_KARTE oltChildPort, final Long sessionId) throws StoreException;

    protected OLT_CHILD_KARTE mapOltChildKarteResource(final Resource in) {
        OLT_CHILD_KARTE out = createOltChild();
        out.setOltChild(in.getParentResource().getId());
        out.setKarte(in.getName());
        return out;
    }

    public void processResource(final Resource ontResource, final Long sessionId) throws ResourceProcessException {
        if (!isResourceSupported(ontResource)) {
            throw new ResourceProcessException("Resource Verarbeitung abgelehnt!");
        }
        try {
            generateOltChildKarte(mapOltChildKarteResource(ontResource), sessionId);
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
