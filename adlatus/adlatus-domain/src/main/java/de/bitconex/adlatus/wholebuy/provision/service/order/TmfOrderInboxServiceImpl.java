package de.bitconex.adlatus.wholebuy.provision.service.order;

import de.bitconex.adlatus.common.persistence.TmfOrderInboxRepository;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TmfOrderInboxServiceImpl implements TmfOrderInboxService {
    private final TmfOrderInboxRepository tmfOrderInboxRepository;

    public TmfOrderInboxServiceImpl(TmfOrderInboxRepository tmfOrderInboxRepository) {
        this.tmfOrderInboxRepository = tmfOrderInboxRepository;
    }

    @Override
    public TmfOrderInbox save(TmfOrderInbox tmfOrderInbox) {
        return tmfOrderInboxRepository.save(tmfOrderInbox);
    }

    @Override
    public TmfOrderInbox findFirstByStatus(Status status) {
        return tmfOrderInboxRepository.findFirstByStatus(status).orElse(null);
    }

    @Override
    public Optional<TmfOrderInbox> findById(String id) {
        return tmfOrderInboxRepository.findById(id);
    }
}
