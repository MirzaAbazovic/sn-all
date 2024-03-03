package de.bitconex.adlatus.wholebuy.provision.service.order;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;

/**
 * This is service for processing Order.
 */
public interface OrderProcessingService {

    /**
     * This is function processing Order.
     *
     * @param orderInbox {@link TmfOrderInbox}
     */
    void processOrder(TmfOrderInbox orderInbox);
}
