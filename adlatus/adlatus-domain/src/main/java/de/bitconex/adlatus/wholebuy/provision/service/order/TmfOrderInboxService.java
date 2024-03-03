package de.bitconex.adlatus.wholebuy.provision.service.order;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;

import java.util.Optional;

public interface TmfOrderInboxService {
    TmfOrderInbox save(TmfOrderInbox tmfOrderInbox);

    TmfOrderInbox findFirstByStatus(Status status);

    Optional<TmfOrderInbox> findById(String id);
}
