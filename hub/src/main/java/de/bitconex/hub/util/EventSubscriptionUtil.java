package de.bitconex.hub.util;

import de.bitconex.hub.model.EventSubscription;
import de.bitconex.hub.model.EventSubscriptionOutput;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventSubscriptionUtil {

    public static <T extends EventSubscription> EventSubscriptionOutput mapToOutput(T eventSubscription) {
        return EventSubscriptionOutput.builder()
                .id(eventSubscription.getId())
                .callback(eventSubscription.getCallback())
                .query(eventSubscription.getQuery())
                .build();
    }
}
