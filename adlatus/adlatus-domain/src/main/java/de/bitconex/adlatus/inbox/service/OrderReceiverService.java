package de.bitconex.adlatus.inbox.service;

import de.bitconex.adlatus.common.persistence.TmfOrderInboxRepository;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;

/**
 * This service handle resource order.
 */
public interface OrderReceiverService {

    /**
     * Its purpose is saving Order to inbox and return this object {@link ResourceOrder}
     *
     * @param resourceOrderCreate {@link ResourceOrderCreate} object to save to our {@link TmfOrderInboxRepository}.
     * @return ResourceOrder
     */
    ResourceOrder saveOrderToInbox(ResourceOrderCreate resourceOrderCreate);
}
