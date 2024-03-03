package de.bitconex.adlatus.wholebuy.provision.tmf.caching.rcm;

import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogCacheServiceImpl;
import de.bitconex.adlatus.wholebuy.provision.adapter.catalog.ResourceCatalogClientService;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class ResourceCatalogCacheServiceImplTest {

    @Mock
    private ResourceCatalogClientService resourceCatalogClientService;

    private ResourceCatalogCacheServiceImpl resourceCatalogCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceCatalogCacheService = new ResourceCatalogCacheServiceImpl(resourceCatalogClientService);
    }

    @Test
    void fetchCatalogTest() {
        List<ResourceSpecification> resourceSpecifications = List.of(ResourceSpecification.builder().build(), ResourceSpecification.builder().build());

        when(resourceCatalogClientService.getAllResourceSpecifications()).thenReturn(resourceSpecifications);

        resourceCatalogCacheService.fetchCatalog();

        verify(resourceCatalogClientService, times(1)).getAllResourceSpecifications();
    }

    @Test
    void evictCatalogTest() {
        resourceCatalogCacheService.evictCatalog();
        // empty test
    }
}