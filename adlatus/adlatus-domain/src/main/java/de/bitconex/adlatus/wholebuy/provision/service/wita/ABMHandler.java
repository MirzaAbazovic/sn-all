package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import de.telekom.wholesale.oss.v15.message.MeldungstypABMType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.ABM_MESSAGE_RECEIVED;

@Component
@Slf4j
public class ABMHandler implements WitaMessageHandler {
    private final List<String> messageCodes = List.of("0000", "0002", "0003", "0005", "0006", "0011", "5002", "6012");
    private final String successMessage = "0000";

    private final Map<String, List<String>> headersMap = Map.of(
        successMessage, List.of(Variables.LINE_ID.getVariableName(), Variables.BINDING_DELIVERY_DATE.getVariableName())
        // other mappings
    );

    private final SpringStateMachineOrderProvisionWorkflowEngine stateMachine;

    public ABMHandler(SpringStateMachineOrderProvisionWorkflowEngine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void handle(String orderId, AnnehmenMeldungRequestType request, String witaInboxId) {
        log.info("Handling ABM message for order: {}", orderId);
        var abmMessage = (MeldungstypABMType) request.getMeldung().getMeldungstyp();
        var code = abmMessage.getMeldungspositionen().getPosition().get(0).getMeldungscode();

        var variables = headersMap.getOrDefault(code, List.of());

        var headers = variables.stream().collect(Collectors.toMap(v -> v, v -> WitaUtil.extractVariable(abmMessage, v)));
        headers.put(Variables.WITA_PRODUCT_INBOX_ID.getVariableName(), witaInboxId);
        var message = ABM_MESSAGE_RECEIVED.withHeaders(headers);
        stateMachine.sendMessage(orderId, message);
    }
}
