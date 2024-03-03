package de.bitconex.adlatus.wholebuy.provision.service.order;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.wholebuy.provision.persistence.ResourceOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ResourceOrderServiceImpl implements ResourceOrderService { // todo: this is only interface towards repository, think about removing it or renaming it
    private final ResourceOrderRepository resourceOrderRepository;

    public ResourceOrderServiceImpl(ResourceOrderRepository resourceOrderRepository) {
        this.resourceOrderRepository = resourceOrderRepository;
    }

    @Override
    public Page<ResourceOrder> findAll(Query query, Pageable pageable) {
        return resourceOrderRepository.findAll(query, pageable);
    }

    @Override
    public ResourceOrder findById(String id) {
        return resourceOrderRepository.findById(id);
    }

    @Override
    public ResourceOrder save(ResourceOrder resourceOrder) {
        return resourceOrderRepository.save(resourceOrder);
    }
}
