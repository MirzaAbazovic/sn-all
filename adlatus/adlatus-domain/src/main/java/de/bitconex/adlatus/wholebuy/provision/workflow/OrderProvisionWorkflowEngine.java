package de.bitconex.adlatus.wholebuy.provision.workflow;

import org.springframework.messaging.Message;

import java.util.Map;

/**
 * Abstraction of state machine for process of provisioning order in Adlatus.
 */
public interface OrderProvisionWorkflowEngine {
    /**
     * This function start process in state machine with business key.
     *
     * @param businessKey String value representing business key
     */
    void startProcess(String businessKey);

    /**
     * This function send broadcast message.
     *
     * @param businessKey String value representing business key
     * @param message     {@link Message<OrderProvisionEvents>} representing message carrying specified event.
     */
    void sendMessage(String businessKey, Message<OrderProvisionEvents> message);

}