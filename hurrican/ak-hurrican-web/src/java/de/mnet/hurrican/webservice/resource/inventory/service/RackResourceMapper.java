package de.mnet.hurrican.webservice.resource.inventory.service;

import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

/**
 *
 */
public interface RackResourceMapper extends ResourceMapper {
    boolean isRackSupported(HWRack rack);

    Resource toResource(HWRack rack) throws ResourceProcessException;
}
