package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import de.telekom.wholesale.oss.v15.message.MeldungstypENTMType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.ENTM_MESSAGE_RECEIVED;

@Component
@Slf4j
public class ENTMHandler implements WitaMessageHandler {
    private final SpringStateMachineOrderProvisionWorkflowEngine stateMachine;

    private final List<String> messageCodes = List.of("0010");

    public ENTMHandler(SpringStateMachineOrderProvisionWorkflowEngine stateMachine) {
        this.stateMachine = stateMachine;
    }

    private final Map<String, List<String>> headersMap = Map.of(
        "0010", List.of(Variables.PAYMENT_DATE.getVariableName())
    );

    @Override
    public void handle(String orderId, AnnehmenMeldungRequestType request, String witaInboxId) {
        log.info("Handling ENTM message for order: {}", orderId);

        MeldungstypENTMType entmMessage = (MeldungstypENTMType) request.getMeldung().getMeldungstyp();

        var code = entmMessage.getMeldungspositionen().getPosition().get(0).getMeldungscode();

        var variables = headersMap.getOrDefault(code, List.of());

        var headers = variables.stream().collect(Collectors.toMap(v -> v, v -> WitaUtil.extractVariable(entmMessage, v)));
        headers.put(Variables.WITA_PRODUCT_INBOX_ID.getVariableName(), witaInboxId);

        var message = ENTM_MESSAGE_RECEIVED.withHeaders(headers);

        stateMachine.sendMessage(orderId, message);
    }
}
