package de.mnet.hurrican.webservice.resource.inventory.service;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

/**
 * Mapper Interface das eine Resource basierend auf der Endpoint Operation in ein Hurrican Datenmodell mappt und den
 * zuständigen Hurrican Service auslöst.
 */
public interface ResourceMapper {

    public boolean isResourceSupported(Resource resource);

    void processResource(Resource resource, Long sessionId) throws ResourceProcessException;
}
