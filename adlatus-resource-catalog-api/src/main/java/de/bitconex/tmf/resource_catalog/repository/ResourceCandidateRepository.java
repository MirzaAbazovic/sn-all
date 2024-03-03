package de.bitconex.tmf.resource_catalog.repository;

import de.bitconex.tmf.resource_catalog.model.ResourceCandidate;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCandidateRepository extends MongoCollectionRepository<ResourceCandidate> {

}
