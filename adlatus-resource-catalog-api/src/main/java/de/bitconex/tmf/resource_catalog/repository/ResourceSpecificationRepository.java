package de.bitconex.tmf.resource_catalog.repository;

import de.bitconex.tmf.resource_catalog.model.ResourceSpecification;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceSpecificationRepository extends MongoCollectionRepository<ResourceSpecification> {
}
