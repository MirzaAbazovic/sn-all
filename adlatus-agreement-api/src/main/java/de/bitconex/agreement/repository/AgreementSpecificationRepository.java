package de.bitconex.agreement.repository;

import de.bitconex.agreement.model.AgreementSpecification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementSpecificationRepository extends MongoRepository<AgreementSpecification, String> {
}
