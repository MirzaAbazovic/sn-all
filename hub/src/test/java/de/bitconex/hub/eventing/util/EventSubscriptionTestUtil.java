package de.bitconex.hub.eventing.util;


import de.bitconex.hub.model.EventSubscription;
import de.bitconex.hub.model.EventSubscriptionInput;

import java.util.UUID;

public class EventSubscriptionTestUtil {

    public static EventSubscriptionInput createEventSubscriptionInput() {
        return EventSubscriptionInput.builder()
                .callback("http:/host:port")
                .query(null)
                .build();
    }

    public static EventSubscription createEventSubscription() {
        return EventSubscription.builder()
                .id(UUID.randomUUID().toString())
                .callback("host:port")
                .query(null)
                .build();
    }
}
