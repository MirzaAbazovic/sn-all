package de.mnet.hurrican.simulator.handler;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Manager class keeps track of incoming order requests. Manager keeps orders to a maximum number of queue size and is
 * able to identify known order notification messages with same externalOrderId accordingly. Message handler must
 * process intermediate notification messages that belong to existing orders differently than new order requests.
 *
 *
 */
public class SimulatorOrderManager {

    protected int cacheSize = 1000;
    protected Queue<String> orderIds = new LinkedList<String>();

    /**
     * Logger *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorOrderManager.class);

    /**
     * Checks if the incoming order id is already known to the simulator instance. In this case the actual incoming
     * message is a intermediate notification message that belongs to a running use case process in the simulator.
     *
     * @param orderId
     * @return
     */
    public boolean knowsOrderId(String orderId) {
        // Dirty workaround for inbound requests that don't have an orderId set
        if (!StringUtils.hasText(orderId)) {
            LOGGER.debug("Order manager workaround for empty order id");
            return true;
        }

        if (orderIds.contains(orderId)) {
            LOGGER.debug("Identified known order id: '" + orderId + "'");
            return true;
        }

        orderIds.add(orderId);
        while (orderIds.size() > cacheSize) {
            orderIds.remove();
        }

        LOGGER.debug("Introduced new order id: '" + orderId + "'");
        return false;
    }

    /**
     * Gets the maximum cache size.
     *
     * @return
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Sets the maximum cache size.
     *
     * @param cacheSize
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
}
