package de.bitconex.adlatus.wholebuy.provision.adapter.catalog;

import de.bitconex.tmf.rcm.model.ResourceSpecification;

import java.util.List;

/**
 * Used for API Calls to Resource Catalog Management.
 */
public interface ResourceCatalogClientService {

    /**
     * Receiving information for client catalog.
     *
     * @return list of {@link ResourceSpecification}
     */
    List<ResourceSpecification> getAllResourceSpecifications();
}
