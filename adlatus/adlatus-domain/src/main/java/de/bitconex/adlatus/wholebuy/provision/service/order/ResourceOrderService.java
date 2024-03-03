package de.bitconex.adlatus.wholebuy.provision.service.order;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface ResourceOrderService {
    Page<ResourceOrder> findAll(Query query, Pageable pageable);

    ResourceOrder findById(String id);

    ResourceOrder save(ResourceOrder resourceOrder);
}
