package de.bitconex.adlatus.common.persistence;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TmfOrderInboxRepository extends MongoRepository<TmfOrderInbox, String> {
    Optional<TmfOrderInbox> findFirstByStatus(Status status);

    Optional<TmfOrderInbox> findByOrderId(String id);
}
