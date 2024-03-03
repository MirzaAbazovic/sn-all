package de.bitconex.adlatus.wholebuy.provision.tmf.service;

import de.bitconex.adlatus.wholebuy.provision.service.order.OrderProcessingService;
import de.bitconex.adlatus.wholebuy.provision.service.order.TmfOrderInboxService;
import de.bitconex.adlatus.wholebuy.provision.service.scheduling.OrderProcessSchedulerImpl;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class OrderProcessSchedulerImplTest {

    @Test
    void processOrders() {
        TmfOrderInboxService orderInboxService = mock(TmfOrderInboxService.class);
        OrderProcessingService orderProcessingService = mock(OrderProcessingService.class);
        OrderProcessSchedulerImpl orderProcessScheduler = new OrderProcessSchedulerImpl(orderInboxService, orderProcessingService);

        TmfOrderInbox order = new TmfOrderInbox();

        when(orderInboxService.findFirstByStatus(Status.ACKNOWLEDGED)).thenReturn(order);

        orderProcessScheduler.processOrder();

        verify(orderProcessingService, times(1)).processOrder(any(TmfOrderInbox.class));
        verify(orderInboxService, times(1)).save(any(TmfOrderInbox.class));
    }

}