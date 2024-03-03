package de.bitconex.hub.eventing;

import de.bitconex.hub.eventing.util.EventSubscriptionTestUtil;
import de.bitconex.hub.model.EventSubscription;
import de.bitconex.hub.model.EventSubscriptionEntity;
import de.bitconex.hub.model.EventSubscriptionInput;
import de.bitconex.hub.model.EventSubscriptionOutput;
import de.bitconex.hub.repository.EventSubscriptionRepositoryJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRegistrationServiceJpaTest {

    @Mock
    private EventSubscriptionRepositoryJpa eventSubscriptionRepository;

    @InjectMocks
    private EventRegistrationServiceJpa eventRegistrationService;


    @DisplayName("Register event subscription")
    @Test
    void register() {
        EventSubscriptionInput eventSubscriptionInput = EventSubscriptionTestUtil.createEventSubscriptionInput();

        EventSubscriptionEntity eventSubscription = createEventSubscriptionEntity(
                eventSubscriptionInput.getCallback(), eventSubscriptionInput.getQuery()
        );

        when(eventSubscriptionRepository.save(any(EventSubscriptionEntity.class)))
                .thenReturn(eventSubscription);

        EventSubscriptionOutput newEventSubscription = eventRegistrationService.register(eventSubscriptionInput);

        assertThat(newEventSubscription).isNotNull();
        assertThat(newEventSubscription.getCallback()).isEqualTo(eventSubscriptionInput.getCallback());
    }

    @DisplayName("Unregister event subscription")
    @Test
    void unregister() {
        EventSubscription eventSubscription = EventSubscriptionTestUtil.createEventSubscription();

        doNothing().when(eventSubscriptionRepository).deleteById(eventSubscription.getId());

        eventRegistrationService.unregister(eventSubscription.getId());

        verify(eventSubscriptionRepository, times(1)).deleteById(eventSubscription.getId());
    }

    @DisplayName("Get all event subscriptions")
    @Test
    void getAll() {
        when(eventSubscriptionRepository.findAll()).thenReturn(Collections.emptyList());

        List<EventSubscriptionOutput> allEventSubscriptions = eventRegistrationService.getAll();

        assertThat(allEventSubscriptions).isEmpty();
        verify(eventSubscriptionRepository, times(1)).findAll();
    }

    @DisplayName("Get all event subscriptions by event")
    @Test
    void getByEvent() {
        String query = "ResourceOrderCreateEvent";

        EventSubscriptionEntity eventSubscription = createEventSubscriptionEntity("host:port", query);

        when(eventSubscriptionRepository.findByEvent(query)).thenReturn(Collections.singletonList(eventSubscription));

        List<EventSubscriptionOutput> subscriptionsByEvent = eventRegistrationService.getByEvent(query);

        assertThat(subscriptionsByEvent).hasSize(1);
        assertThat(subscriptionsByEvent.get(0).getQuery()).isEqualTo(query);
        verify(eventSubscriptionRepository, times(1)).findByEvent(query);
    }

    private EventSubscriptionEntity createEventSubscriptionEntity(String callback, String query) {
        return EventSubscriptionEntity.builder()
                .id(UUID.randomUUID().toString())
                .callback(callback)
                .query(query)
                .build();
    }
}