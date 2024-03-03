package de.bitconex.adlatus.wholebuy.provision.tmf.service;

import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogCacheService;
import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogServiceImpl;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ResourceCatalogServiceImplTest {

    @Mock
    private ResourceCatalogCacheService catalogCacheService;

    private ResourceCatalogServiceImpl resourceCatalogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceCatalogService = new ResourceCatalogServiceImpl(catalogCacheService);
    }

    @Test
    void fetchCatalogReturnsExpectedResources() {
        List<ResourceSpecification> expectedResourceSpecifications = List.of(
            ResourceSpecification.builder()
                .id("1")
                .name("name")
                .build(),
            ResourceSpecification.builder()
                .id("2")
                .build()
        );

        when(catalogCacheService.fetchCatalog()).thenReturn(expectedResourceSpecifications);

        ResourceSpecification resourceSpecification = resourceCatalogService.retrieveSpecification("1");

        assertThat(resourceSpecification.getId()).isEqualTo(expectedResourceSpecifications.get(0).getId());
        verify(catalogCacheService, times(1)).fetchCatalog();
    }

    @Test
    void fetchCatalogReturnsEmptyListWhenNoResources() {
        when(catalogCacheService.fetchCatalog()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> resourceCatalogService.retrieveSpecification("1"))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("No value present");
    }

    @Test
    void fetchCatalogReturnsEmptyListWhenNoPresent() {
        List<ResourceSpecification> expectedResourceSpecifications = List.of(
            ResourceSpecification.builder()
                .id("1")
                .name("name")
                .build(),
            ResourceSpecification.builder()
                .id("2")
                .build()
        );
        when(catalogCacheService.fetchCatalog()).thenReturn(expectedResourceSpecifications);

        assertThatThrownBy(() -> resourceCatalogService.retrieveSpecification("3"))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("No value present");

    }
}