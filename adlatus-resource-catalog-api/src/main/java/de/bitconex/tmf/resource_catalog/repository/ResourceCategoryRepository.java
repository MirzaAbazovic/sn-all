package de.bitconex.tmf.resource_catalog.repository;

import de.bitconex.tmf.resource_catalog.model.ResourceCategory;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCategoryRepository extends MongoCollectionRepository<ResourceCategory> {
}
