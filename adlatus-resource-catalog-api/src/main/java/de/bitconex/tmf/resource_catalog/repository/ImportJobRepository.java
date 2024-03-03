package de.bitconex.tmf.resource_catalog.repository;

import de.bitconex.tmf.resource_catalog.model.ImportJob;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRepository extends MongoCollectionRepository<ImportJob> {
}
