package de.bitconex.hub.eventing;

import de.bitconex.hub.model.EventSubscriptionEntity;
import de.bitconex.hub.model.EventSubscriptionInput;
import de.bitconex.hub.model.EventSubscriptionOutput;
import de.bitconex.hub.repository.EventSubscriptionRepositoryJpa;
import de.bitconex.hub.util.EventSubscriptionUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "hub.eventRegistrationService", havingValue = "EventRegistrationServiceJpa")
public class EventRegistrationServiceJpa implements EventRegistrationService {

    private final EventSubscriptionRepositoryJpa repository;

    public EventRegistrationServiceJpa(EventSubscriptionRepositoryJpa repository) {
        this.repository = repository;
    }

    @Override
    public EventSubscriptionOutput register(EventSubscriptionInput eventSubscriptionInput) {
        EventSubscriptionEntity eventSubscription = repository.save(EventSubscriptionEntity.builder()
                .id(UUID.randomUUID().toString())
                .callback(eventSubscriptionInput.getCallback())
                .query(eventSubscriptionInput.getQuery()).build());

        return EventSubscriptionUtil.mapToOutput(eventSubscription);
    }

    @Override
    public void unregister(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<EventSubscriptionOutput> getAll() {
        return repository.findAll().stream()
                .map(EventSubscriptionUtil::mapToOutput)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventSubscriptionOutput> getByEvent(final String eventName) {
        return repository.findByEvent(eventName)
                .stream()
                .map(EventSubscriptionUtil::mapToOutput)
                .collect(Collectors.toList());
    }
}
