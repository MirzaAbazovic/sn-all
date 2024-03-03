package de.bitconex.adlatus.wholebuy.provision.service.catalog;

import de.bitconex.tmf.rcm.model.ResourceSpecification;

/**
 * The cache service uses it as a dependency, so it is an abstraction of this cache service.
 */
public interface ResourceCatalogService {
    /**
     * Retrieves Resource Specification by id
     *
     * @param id String representing our ID of a specific resource specification
     * @return {@link ResourceSpecification}
     */
    ResourceSpecification retrieveSpecification(String id);
}
