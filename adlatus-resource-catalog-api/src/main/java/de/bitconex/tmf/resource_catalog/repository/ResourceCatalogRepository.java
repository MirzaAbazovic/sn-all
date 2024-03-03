package de.bitconex.tmf.resource_catalog.repository;

import de.bitconex.tmf.resource_catalog.model.ResourceCatalog;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCatalogRepository extends MongoCollectionRepository<ResourceCatalog> {
}
