package de.bitconex.adlatus.wholebuy.provision.persistence;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResourceOrderBaseRepository extends MongoRepository<ResourceOrder, String> {
}
