package de.bitconex.adlatus.wholebuy.provision.tmf.service;

import de.bitconex.adlatus.wholebuy.provision.service.notification.OrderEventDispatcherImpl;
import de.bitconex.hub.eventing.NotificationRequest;
import de.bitconex.hub.eventing.Notificator;
import de.bitconex.tmf.rom.model.ResourceOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderEventDispatcherImplTest {
    private Notificator notificator;
    private OrderEventDispatcherImpl orderEventDispatcher;

    @BeforeEach
    void setUp() {
        notificator = mock(Notificator.class);
        orderEventDispatcher = new OrderEventDispatcherImpl(notificator);
    }

    @Test
    void notifyOrderCreateEvent() {
        ResourceOrder resourceOrder = new ResourceOrder();

        orderEventDispatcher.notifyOrderCreateEvent(resourceOrder);

        verify(notificator, times(1)).notifyListener(any(NotificationRequest.class));
    }
}