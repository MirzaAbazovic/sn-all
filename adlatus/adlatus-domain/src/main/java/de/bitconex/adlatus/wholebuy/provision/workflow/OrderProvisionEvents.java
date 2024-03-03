package de.bitconex.adlatus.wholebuy.provision.workflow;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

public enum OrderProvisionEvents {
    /**
     * TMF create order received from TMF inbox
     */
    ORDER_RECEIVED,
    /**
     * Initial message for provisioning of order sent to WITA outbox
     */
    ORDER_SENT,
    /**
     * QEB Qualifizierte EingangsBestätigung - Qualified confirmation of receipt received in WITA inbox
     */
    QEB_MESSAGE_RECEIVED,
    /**
     * ABM Auftragsbestätigungsmeldung - Order confirmation message received
     */
    ABM_MESSAGE_RECEIVED,
    /**
     * ENTM Entgeltmeldung - Payment notification message received
     */
    ENTM_MESSAGE_RECEIVED,
    /**
     * ERLM Erledigungsmeldung - Completion notification message received
     */
    ERLM_MESSAGE_RECEIVED;

    public Message<OrderProvisionEvents> getMessage() {
        return MessageBuilder
            .withPayload(this)
            .build();
    }

    public Message<OrderProvisionEvents> withHeaders(Map<String, Object> headers) {
        return MessageBuilder
            .withPayload(this)
            .copyHeaders(headers)
            .build();
    }

    public Mono<Message<OrderProvisionEvents>> getMessageMono() {
        return Mono.just(getMessage());
    }

}
