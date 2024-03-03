package de.bitconex.adlatus.inbox.service;

import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;

/**
 * This class validate Order.
 */
public interface OrderValidator {
    /**
     * This function mapping {@link ResourceOrderCreate} to {@link  ResourceOrder}.
     * @param resourceOrderCreate {@link ResourceOrderCreate}
     *
     */
    void validate(ResourceOrderCreate resourceOrderCreate);
}
