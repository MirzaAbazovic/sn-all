package de.bitconex.adlatus.wholebuy.provision.service.notification;

import de.bitconex.tmf.rom.model.ResourceOrder;

/**
 * Used to send notifications. It uses the notifier from hub (it's our second library, you have the code under another repository: hub).
 */
public interface OrderEventDispatcher {
    /**
     * Used for notifying and send {@link ResourceOrder}.
     *
     * @param resourceOrder {@link ResourceOrder}
     */
    void notifyOrderCreateEvent(ResourceOrder resourceOrder);
}
