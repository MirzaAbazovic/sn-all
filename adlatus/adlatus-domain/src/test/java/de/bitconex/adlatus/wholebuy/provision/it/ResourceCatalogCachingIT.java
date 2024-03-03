package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogCacheService;
import de.bitconex.adlatus.wholebuy.provision.adapter.catalog.ResourceCatalogClientService;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ResourceCatalogCachingIT extends IntegrationTestBase {

    @Autowired
    private ResourceCatalogCacheService resourceCatalogCacheService;

    @MockBean
    private ResourceCatalogClientService resourceCatalogClientService;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void testFetchCatalogCaching() {
        ResourceSpecification resourceSpecification1 = new ResourceSpecification();
        ResourceSpecification resourceSpecification2 = new ResourceSpecification();
        List<ResourceSpecification> resourceSpecifications = Arrays.asList(resourceSpecification1, resourceSpecification2);

        when(resourceCatalogClientService.getAllResourceSpecifications()).thenReturn(resourceSpecifications);

        // First call to fetchCatalog, should hit the API
        List<ResourceSpecification> firstCall = resourceCatalogCacheService.fetchCatalog();
        assertEquals(resourceSpecifications, firstCall);

        // Second call to fetchCatalog, should return cached data
        List<ResourceSpecification> secondCall = resourceCatalogCacheService.fetchCatalog();
        assertEquals(resourceSpecifications, secondCall);

        // Verify that the API was hit only once
        verify(resourceCatalogClientService, times(1)).getAllResourceSpecifications();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void testEvictCatalog() {
        ResourceSpecification resourceSpecification1 = new ResourceSpecification();
        ResourceSpecification resourceSpecification2 = new ResourceSpecification();
        List<ResourceSpecification> resourceSpecifications = Arrays.asList(resourceSpecification1, resourceSpecification2);

        when(resourceCatalogClientService.getAllResourceSpecifications()).thenReturn(resourceSpecifications);

        // First call to fetchCatalog, should hit the API
        List<ResourceSpecification> firstCall = resourceCatalogCacheService.fetchCatalog();
        assertEquals(resourceSpecifications, firstCall);

        // Call to evictCatalog, should clear the cache
        resourceCatalogCacheService.evictCatalog();

        // Second call to fetchCatalog, should hit the API again because the cache was cleared
        List<ResourceSpecification> secondCall = resourceCatalogCacheService.fetchCatalog();
        assertEquals(resourceSpecifications, secondCall);

        // Verify that the API was hit twice
        verify(resourceCatalogClientService, times(2)).getAllResourceSpecifications();
    }
}
