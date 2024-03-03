package de.mnet.hurrican.webservice.resource.inventory.service;

import java.util.*;
import javax.inject.*;
import org.springframework.stereotype.Service;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;


@Named
@Service
public class ResourceProviderService {

    @Inject
    private List<ResourceMapper> resourceMappers;

    public void updateResource(Resource resource, Long sessionId) throws ResourceProcessException {
        boolean resourceProcessed = false;
        for (final ResourceMapper resourceMapper : resourceMappers) {
            if (resourceMapper.isResourceSupported(resource)) {
                resourceMapper.processResource(resource, sessionId);
                resourceProcessed = true;
            }
        }

        if (!resourceProcessed) {
            throw new ResourceProcessException(String.format("UpdateResource fuer Resource [id=%s, inventory=%s, name=%s] " +
                            "ist nicht unterstuetzt!", resource.getId(), resource.getInventory(), resource.getName()));
        }
    }

    void setResourceMappers(List<ResourceMapper> resourceMappers) {
        this.resourceMappers = resourceMappers;
    }

}
