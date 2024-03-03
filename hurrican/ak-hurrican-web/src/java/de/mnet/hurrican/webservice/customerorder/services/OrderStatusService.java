package de.mnet.hurrican.webservice.customerorder.services;

import de.augustakom.hurrican.service.cc.ICCService;

/**
 * Service zu Status abfragen für einem Auftrag
 *
 */
public interface OrderStatusService extends ICCService {

    /**
     * Retrieves public order status by customer order id
     *
     * @param customerOrderId customer order id
     * @return public order status as enum {@link PublicOrderStatus}
     */
    PublicOrderStatus getPublicOrderStatus(String customerOrderId);

}
