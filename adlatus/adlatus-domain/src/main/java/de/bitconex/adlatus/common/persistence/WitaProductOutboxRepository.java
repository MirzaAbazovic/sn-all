package de.bitconex.adlatus.common.persistence;

import de.bitconex.adlatus.common.model.WitaProductOutbox;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WitaProductOutboxRepository extends MongoRepository<WitaProductOutbox, String> {
    Optional<WitaProductOutbox> findFirstByStatusOrderByRetriesAsc(WitaProductOutbox.Status status);

    List<WitaProductOutbox> findByExternalOrderId(String externalOrderId);
}
