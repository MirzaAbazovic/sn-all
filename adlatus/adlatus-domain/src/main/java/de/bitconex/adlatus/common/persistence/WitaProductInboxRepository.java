package de.bitconex.adlatus.common.persistence;

import de.bitconex.adlatus.common.model.WitaProductInbox;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WitaProductInboxRepository extends MongoRepository<WitaProductInbox, String> {
    Optional<WitaProductInbox> findFirstByStatusOrderByRetriesAsc(WitaProductInbox.Status status);

    List<WitaProductInbox> findByExternalOrderId(String externalOrderId);
}
