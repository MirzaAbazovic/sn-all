package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import de.telekom.wholesale.oss.v15.message.MeldungstypERLMType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.ERLM_MESSAGE_RECEIVED;

@Component
@Slf4j
public class ERLMHandler implements WitaMessageHandler {
    private final List<String> messageCodes = List.of("0010", "0018");
    private final String successMessage = "0010";
    private final Map<String, List<String>> headersMap = Map.of(
        successMessage, List.of(Variables.COMPLETION_DATE.getVariableName())
    );
    private final SpringStateMachineOrderProvisionWorkflowEngine stateMachine;

    public ERLMHandler(SpringStateMachineOrderProvisionWorkflowEngine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void handle(String orderId, AnnehmenMeldungRequestType request, String witaInboxId) {
        log.info("Handling ERLM message for order: {}", orderId);

        MeldungstypERLMType erlmMessage = (MeldungstypERLMType) request.getMeldung().getMeldungstyp();

        var code = erlmMessage.getMeldungspositionen().getPosition().get(0).getMeldungscode();

        var variables = headersMap.getOrDefault(code, List.of());
        var headers = variables.stream().collect(Collectors.toMap(k -> k, v -> WitaUtil.extractVariable(erlmMessage, v)));
        headers.put(Variables.WITA_PRODUCT_INBOX_ID.getVariableName(), witaInboxId);
        var message = ERLM_MESSAGE_RECEIVED.withHeaders(headers);

        stateMachine.sendMessage(orderId, message);
    }
}
