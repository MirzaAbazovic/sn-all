package de.bitconex.adlatus.wholebuy.provision.service.wita;


import de.bitconex.tmf.rom.model.ResourceOrder;

/**
 * Create new message for WITA. Transform one model to WITa model.
 * The interface will be implemented from multiple classes where every class does this generating into different telecom interface (WITA, S/PRI etc.)
 *
 * @see <a href="https://bitconex.atlassian.net/wiki/spaces/ADL/overview">Documentation</a>
 */
public interface MessageGenerator {

    /**
     * Generate message from ResourceOrder.
     *
     * @param order {@link ResourceOrder}
     * @return String
     */
    String generate(ResourceOrder order);
}
