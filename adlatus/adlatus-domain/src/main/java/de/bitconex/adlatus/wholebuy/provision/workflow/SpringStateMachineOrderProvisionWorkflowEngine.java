package de.bitconex.adlatus.wholebuy.provision.workflow;

import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpringStateMachineOrderProvisionWorkflowEngine implements OrderProvisionWorkflowEngine {
    private final StateMachineService<OrderProvisionStates, OrderProvisionEvents> stateMachineService;
    private StateMachine<OrderProvisionStates, OrderProvisionEvents> currentStateMachine;

    public SpringStateMachineOrderProvisionWorkflowEngine(StateMachineService<OrderProvisionStates, OrderProvisionEvents> stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    @Override
    public void startProcess(String businessKey) {
        //todo: remove this, its not needed
    }

    @Override
    public void sendMessage(String businessKey, Message<OrderProvisionEvents> message) {
        StateMachine<OrderProvisionStates, OrderProvisionEvents> stateMachine = getStateMachine(businessKey);

        Map<String, Object> businessHeaders = message.getHeaders().entrySet()
            .stream()
            .filter(entry -> Variables.isEnumConstant(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        stateMachine.getExtendedState().getVariables().putAll(businessHeaders);

        if (!stateMachine
            .sendEvent(message))
            throw new RuntimeException("Failed to send message to state machine");

        log.info("Sent message: {} to process: {}", message, businessKey);
    }

    private StateMachine<OrderProvisionStates, OrderProvisionEvents> getStateMachine(String businessKey) {
        if (currentStateMachine == null) {
            currentStateMachine = stateMachineService.acquireStateMachine(businessKey);
            currentStateMachine.startReactively().block();
        } else if (!ObjectUtils.nullSafeEquals(currentStateMachine.getId(), businessKey)) {
            stateMachineService.releaseStateMachine(currentStateMachine.getId());
            currentStateMachine.stopReactively().subscribe();
            currentStateMachine = stateMachineService.acquireStateMachine(businessKey);
            currentStateMachine.startReactively().block();
        }
        return currentStateMachine;
    }

    // TODO should be visible for testing only
    OrderProvisionStates getProcessState(String businessKey) {

        return getStateMachine(businessKey).getState().getId();
    }

    // TODO should be visible for testing only
    List<OrderProvisionStates> getProcessStates(String businessKey) {
        return getStateMachine(businessKey).getState().getIds().stream().toList();
    }


    Map<Object, Object> getVariables(String businessKey) {
        return getStateMachine(businessKey).getExtendedState().getVariables();
    }
}

