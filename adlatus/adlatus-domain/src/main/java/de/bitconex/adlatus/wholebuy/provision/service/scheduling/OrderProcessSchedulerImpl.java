package de.bitconex.adlatus.wholebuy.provision.service.scheduling;

import de.bitconex.adlatus.wholebuy.provision.service.order.OrderProcessingService;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.wholebuy.provision.service.order.TmfOrderInboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderProcessSchedulerImpl implements OrderProcessScheduler {

    private static final long PROCESSING_INTERVAL_IN_MS = 10000;
    private final TmfOrderInboxService orderInboxService;
    private final OrderProcessingService orderProcessingService;

    public OrderProcessSchedulerImpl(TmfOrderInboxService orderInboxService, OrderProcessingService orderProcessingService) {
        this.orderInboxService = orderInboxService;
        this.orderProcessingService = orderProcessingService;
    }

    @Async
    @Scheduled(fixedRate = PROCESSING_INTERVAL_IN_MS)
    @Override
    @Transactional
    public void processOrder() {
        TmfOrderInbox order = orderInboxService.findFirstByStatus(Status.ACKNOWLEDGED);
        if (order == null)
            return;
        log.debug("Process new orders. Order id: {}", order.getId());
        orderProcessingService.processOrder(order);
        order.setStatus(Status.IN_PROGRESS);
        orderInboxService.save(order);
        log.info("Finished new orders processing");
    }
}

