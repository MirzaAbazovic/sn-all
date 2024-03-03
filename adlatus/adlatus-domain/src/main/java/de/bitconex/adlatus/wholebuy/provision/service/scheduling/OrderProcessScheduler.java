package de.bitconex.adlatus.wholebuy.provision.service.scheduling;

/**
 * This is a scheduler for processing orders from the TMF Inbox (pull all received messages from the left TMF side and starts their processing with the Workflow engine).
 *
 * @see <a href="https://bitconex.atlassian.net/wiki/spaces/ADL/pages/2247295248/TM+Forum+ROM">More about TMF</a>
 */
public interface OrderProcessScheduler {

    /**
     * This function find all Orders by criteria.
     */
    void processOrder();
}
