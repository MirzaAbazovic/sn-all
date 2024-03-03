package de.bitconex.hub.repository;

import de.bitconex.hub.model.EventSubscriptionDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "hub.eventRegistrationService", havingValue = "EventRegistrationServiceMongo")
public interface EventSubscriptionMongoRepository extends MongoRepository<EventSubscriptionDocument, String> {

    @Query("{ $or: [ { 'query' : { $regex: ?0, $options: 'i' } }, { 'query' : null } ] }")
    List<EventSubscriptionDocument> findByEvent(String query);
}

