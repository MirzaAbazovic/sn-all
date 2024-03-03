package de.bitconex.adlatus.wholebuy.provision.service.scheduling;

import de.bitconex.adlatus.common.model.WitaProductOutbox;

import java.util.List;

/**
 * Working with WITA Outbox. For more @see <a href="https://bitconex.atlassian.net/wiki/spaces/ADL/pages/2245099527/Interfaces">WITA</a>
 */
public interface WitaOutboxService {

    /**
     * Save WITA msg.
     *
     * @param orderId order ID
     * @param request String value representing request
     */
    void save(String orderId, String request);

    /**
     * Find all wita msg where status is @see {@link WitaProductOutbox.Status#CREATED} (A change is possible soon.).
     */
    void check();

    /**
     * Try to send WITA and change status to @see {@link WitaProductOutbox.Status#SENT} (A change is possible soon).
     *
     * @param productOutbox {@link WitaProductOutbox}
     */
    void process(WitaProductOutbox productOutbox);

    List<WitaProductOutbox> findByExternalOrderId(String id);
}
