package de.bitconex.tmf.party.service.impl;

import de.bitconex.tmf.party.api.client.APIClient;
import de.bitconex.tmf.party.models.EventSubscription;
import de.bitconex.tmf.party.repository.EventSubscriptionRepository;
import de.bitconex.tmf.party.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final EventSubscriptionRepository eventSubscriptionRepository;
    private final APIClient apiClient;

    @Autowired
    public SubscriptionServiceImpl(EventSubscriptionRepository eventSubscriptionRepository, APIClient apiClient) {
        this.eventSubscriptionRepository = eventSubscriptionRepository;
        this.apiClient = apiClient;
    }

    @Override
    public EventSubscription subscribe(EventSubscription subscription) {
        return eventSubscriptionRepository.save(subscription);
    }

    @Override
    public void unsubscribe(String id) {
        eventSubscriptionRepository.deleteById(id);
    }

    @Override
    public <T> void notify(Class<T> eventType, T eventData) {
        List<EventSubscription> subscriptions = eventSubscriptionRepository.findAll();
        for (EventSubscription subscription : subscriptions) {
            String callbackUrl = subscription.getCallback();
            String query = subscription.getQuery();

            // no need to fetch subscription by event type, just post to host/query (http://example.com/listener?eventType=individualCreateEvent
            // listener basically have post method that receive this
            apiClient.post(String.format("%s/listener%s", callbackUrl, query), eventData);
        }
    }
}
