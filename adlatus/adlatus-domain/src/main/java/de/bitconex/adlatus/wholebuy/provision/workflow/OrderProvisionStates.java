package de.bitconex.adlatus.wholebuy.provision.workflow;

public enum OrderProvisionStates {
    /**
     * Starting state of state machine
     */
    INITIAL,
    /**
     * Resource Order received
     */
    ACKNOWLEDGED,
    /**
     * Provision of product started. Create order message is sent.
     * Waiting to receive QEB message
     */
    WAITING_QEB,
    /**
     * QEB (Qualified confirmation of receipt) message is received.
     * Waiting for ABM message
     */
    WAITING_ABM,
    /**
     * ABM (Order confirmation message) is received.
     * Pseudo state for parallel tasks
     */
    FORK,
    /**
     * Parent state for ENTM and ERLM
     * Allowing them to be executed in parallel
     */
    ERLM_ENTM_PARENT,
    /**
     * Waiting for ENTM message for payment notification.
     */
    WAITING_ENTM,
    /**
     * ENTM message received
     */
    END_ENTM,
    /**
     * Waiting for ERLM message (order completed message)
     */
    WAITING_ERLM,
    /**
     * ERLM message received
     */
    END_ERLM,
    /**
     * Pseudo state for joining parallel tasks
     */
    JOIN,
    /**
     * Final state, Order finalized
     */
    COMPLETED
}