package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import de.telekom.wholesale.oss.v15.message.MeldungstypQEBType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.QEB_MESSAGE_RECEIVED;

@Component
@Slf4j
public class QEBHandler implements WitaMessageHandler {
    private final String successMessage = "0000";
    private final SpringStateMachineOrderProvisionWorkflowEngine stateMachine;
    private final Map<String, List<String>> headersMap = Map.of(
        successMessage, List.of(Variables.CONTACT_PERSON.getVariableName())
        // other mappings
    );

    public QEBHandler(SpringStateMachineOrderProvisionWorkflowEngine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void handle(String orderId, AnnehmenMeldungRequestType request, String witaInboxId) {
        log.info("Handling QEB message for order: {}", orderId);
        var qebMessage = (MeldungstypQEBType) request.getMeldung().getMeldungstyp();
        var meldungscode = qebMessage.getMeldungspositionen().getPosition().get(0).getMeldungscode();

        var variables = headersMap.getOrDefault(meldungscode, List.of());

        var headers = variables.stream().collect(Collectors.toMap(v -> v, v -> WitaUtil.extractVariable(qebMessage, v)));
        headers.put(Variables.WITA_PRODUCT_INBOX_ID.getVariableName(), witaInboxId);
        var message = QEB_MESSAGE_RECEIVED.withHeaders(headers);

        stateMachine.sendMessage(orderId, message);
    }
}
