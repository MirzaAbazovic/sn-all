package de.bitconex.tmf.party.service;

import de.bitconex.tmf.party.models.EventSubscription;

public interface SubscriptionService {
    EventSubscription subscribe(EventSubscription subscription);

    void unsubscribe(String id);

    <T> void notify(Class<T> eventType, T eventData);
}
