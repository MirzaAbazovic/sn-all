package de.bitconex.adlatus.wholebuy.provision.persistence;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface ResourceOrderRepository {
    Page<ResourceOrder> findAll(Query query, Pageable pageable);

    ResourceOrder findById(String id);

    ResourceOrder save(ResourceOrder resourceOrder);
}
