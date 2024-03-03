package de.bitconex.adlatus.wholebuy.provision.persistence;


import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResourceOrderRepositoryImpl implements ResourceOrderRepository {
    private final ResourceOrderBaseRepository resourceOrderBaseRepository;
    private final MongoTemplate mongoTemplate;

    public ResourceOrderRepositoryImpl(ResourceOrderBaseRepository resourceOrderBaseRepository, MongoTemplate mongoTemplate) {
        this.resourceOrderBaseRepository = resourceOrderBaseRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<ResourceOrder> findAll(Query query, Pageable pageable) {
        query.with(pageable);
        List<ResourceOrder> resourceOrderList = mongoTemplate.find(query, ResourceOrder.class);
        long total = mongoTemplate.count(query, ResourceOrder.class);
        return new PageImpl<>(resourceOrderList, pageable, total);
    }

    @Override
    public ResourceOrder findById(String id) {
        return resourceOrderBaseRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public ResourceOrder save(ResourceOrder resourceOrder) {
        return resourceOrderBaseRepository.save(resourceOrder);
    }
}
