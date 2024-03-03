package de.bitconex.adlatus.wholebuy.provision.service.catalog;

import de.bitconex.adlatus.wholebuy.provision.adapter.catalog.ResourceCatalogClientService;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ResourceCatalogCacheServiceImpl implements ResourceCatalogCacheService {
    private final ResourceCatalogClientService resourceSpecificationClient;

    public ResourceCatalogCacheServiceImpl(ResourceCatalogClientService resourceSpecificationClient) {
        this.resourceSpecificationClient = resourceSpecificationClient;
    }

    @Override
    @Cacheable("resource-catalog")
    public List<ResourceSpecification> fetchCatalog() {
        return resourceSpecificationClient.getAllResourceSpecifications();
    }

    @Override
    @CacheEvict(value = "resource-catalog", allEntries = true)
    public void evictCatalog() {
        log.info("Evicting resource catalog cache");
    }
}
