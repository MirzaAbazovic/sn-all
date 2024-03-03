package de.bitconex.hub.eventing;

import de.bitconex.hub.model.EventSubscriptionInput;
import de.bitconex.hub.model.EventSubscriptionOutput;

import java.util.List;

public interface EventRegistrationService {

    EventSubscriptionOutput register(EventSubscriptionInput eventSubscriptionInput);

    void unregister(String id);

    List<EventSubscriptionOutput> getAll();

    List<EventSubscriptionOutput> getByEvent(String eventName);
}
