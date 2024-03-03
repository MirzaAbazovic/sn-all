package de.bitconex.tmf.resource_catalog.repository;

import de.bitconex.tmf.resource_catalog.model.ExportJob;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportJobRepository extends MongoCollectionRepository<ExportJob> {
}