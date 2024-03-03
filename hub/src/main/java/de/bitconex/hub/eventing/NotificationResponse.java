package de.bitconex.hub.eventing;

import java.util.HashMap;
import java.util.Map;

public class NotificationResponse {

    private final Map<String, String> listenerResponse = new HashMap<>();

    public void addListenerResponse(String eventSubscriptionId, String responseCode){
        listenerResponse.put(eventSubscriptionId, responseCode);
    }
}
