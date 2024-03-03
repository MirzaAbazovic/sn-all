package de.bitconex.adlatus.common.controller;

import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cache/resourceCatalog") // todo: should be /internal/cache/resourceCatalog, we should implement this in catalog, to call this endpoint when catalog is updated
public class ResourceCatalogCacheController {

    private final ResourceCatalogCacheService catalogCacheService;

    public ResourceCatalogCacheController(ResourceCatalogCacheService catalogCacheService) {
        this.catalogCacheService = catalogCacheService;
    }

    @PostMapping("/evict")
    public void evictCatalog() {
        catalogCacheService.evictCatalog();
    }
}
