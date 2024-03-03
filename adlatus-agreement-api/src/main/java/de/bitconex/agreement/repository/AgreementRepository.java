package de.bitconex.agreement.repository;

import de.bitconex.agreement.model.Agreement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementRepository extends MongoRepository<Agreement, String> {
}
