package de.bitconex.adlatus.wholebuy.provision.service.notification;

import de.bitconex.hub.eventing.NotificationRequest;
import de.bitconex.hub.eventing.Notificator;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreateEvent;
import de.bitconex.tmf.rom.model.ResourceOrderCreateEventPayload;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class OrderEventDispatcherImpl implements OrderEventDispatcher {

    private final Notificator notificator;

    public OrderEventDispatcherImpl(Notificator notificator) {
        this.notificator = notificator;
    }

    @Override
    public void notifyOrderCreateEvent(ResourceOrder resourceOrder) {
        ResourceOrderCreateEventPayload payload = ResourceOrderCreateEventPayload.builder()
                .resourceOrder(resourceOrder)
                .build();

        ResourceOrderCreateEvent event = ResourceOrderCreateEvent.builder()
                .id(UUID.randomUUID().toString())
                .eventType(ResourceOrderCreateEvent.class.getName())
                .eventTime(OffsetDateTime.now())
                .domain(ResourceOrder.class.getName())
                .title("Resource Order Created")
                .description("New resource order created")
                .event(payload)
                .build();

        NotificationRequest<ResourceOrderCreateEvent> request = new NotificationRequest<>();
        request.setEvent(event);

        notificator.notifyListener(request);
    }
    // todo: add other notifications here
}
