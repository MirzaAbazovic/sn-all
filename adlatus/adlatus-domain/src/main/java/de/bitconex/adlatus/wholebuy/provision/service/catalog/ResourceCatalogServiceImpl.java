package de.bitconex.adlatus.wholebuy.provision.service.catalog;

import de.bitconex.tmf.rcm.model.ResourceSpecification;
import org.springframework.stereotype.Service;

@Service
public class ResourceCatalogServiceImpl implements ResourceCatalogService {
    private final ResourceCatalogCacheService catalogCacheService;

    public ResourceCatalogServiceImpl(ResourceCatalogCacheService catalogCacheService) {
        this.catalogCacheService = catalogCacheService;
    }

    @Override
    public ResourceSpecification retrieveSpecification(String id) {
        var resourceSpecifications = catalogCacheService.fetchCatalog();
        return resourceSpecifications.stream()
                .filter(resourceSpecification -> resourceSpecification.getId().equals(id))
                .findAny().orElseThrow(); // todo: propagate this to validateOrderItems in OrderValidatorImpl
    }
}
