package de.bitconex.tmf.party.repository;

import de.bitconex.tmf.party.models.EventSubscription;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSubscriptionRepository extends MongoCollectionRepository<EventSubscription> {
}
