package de.bitconex.adlatus.wholebuy.provision.service.catalog;

import de.bitconex.tmf.rcm.model.ResourceSpecification;

import java.util.List;

/**
 * OrderValidator validates the resource order itself that arrives at the ROM. So you can see that in the resource order controller we call OrderService, and it calls this validator in one of its methods, so when a new order arrives, the properties must be validated, as well as some other things, in order to continue.
 * @see <a href="https://bitconex.atlassian.net/wiki/spaces/ADL/pages/2247295248/TM+Forum+ROM">ROM documentation</a>
 */
public interface ResourceCatalogCacheService {
    /**
     * Find all Catalog.
     *
     * @return list of {@link  ResourceSpecification}
     */
    List<ResourceSpecification> fetchCatalog();

    /**
     * Evicts resource catalog cache. Cache eviction is invoked after entering the method.
     */
    void evictCatalog();
}
