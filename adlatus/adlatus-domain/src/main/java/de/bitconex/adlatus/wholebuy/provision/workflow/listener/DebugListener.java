package de.bitconex.adlatus.wholebuy.provision.workflow.listener;

import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.inbox.service.WitaInboxService;
import de.bitconex.adlatus.wholebuy.provision.dto.constants.Constants;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DebugListener extends StateMachineListenerAdapter<OrderProvisionStates, OrderProvisionEvents> {

    private final WitaInboxService witaInboxService;

    public DebugListener(WitaInboxService witaInboxService) {
        this.witaInboxService = witaInboxService;
    }

    @Override
    public void stateMachineStarted(StateMachine<OrderProvisionStates, OrderProvisionEvents> stateMachine) {
        super.stateMachineStarted(stateMachine);
        log.debug("State machine started: {}", stateMachine);
    }

    @Override
    public void transition(Transition<OrderProvisionStates, OrderProvisionEvents> transition) {
        super.transition(transition);
        log.debug("Transition: {}", transition);
    }

    @Override
    public void transitionStarted(Transition<OrderProvisionStates, OrderProvisionEvents> transition) {
        super.transitionStarted(transition);
        log.debug("Transition started: {}", transition);
    }

    @Override
    public void transitionEnded(Transition<OrderProvisionStates, OrderProvisionEvents> transition) {
        super.transitionEnded(transition);
        log.debug("Transition ended: {}", transition);
    }


    @Override
    public void stateChanged(State<OrderProvisionStates, OrderProvisionEvents> from, State<OrderProvisionStates, OrderProvisionEvents> to) {
        super.stateChanged(from, to);
        log.debug("State changed :{} to:{}", from, to);
    }

    @Override
    public void stateEntered(State<OrderProvisionStates, OrderProvisionEvents> state) {
        super.stateEntered(state);
        log.debug("State entered: {}", state.getId());
    }

    @Override
    public void stateExited(State<OrderProvisionStates, OrderProvisionEvents> state) {
        super.stateExited(state);
        log.debug("State exited: {}", state.getId());
    }

    @Override
    public void stateMachineError(StateMachine<OrderProvisionStates, OrderProvisionEvents> stateMachine, Exception exception) {
        super.stateMachineError(stateMachine, exception);
        //TODO: RETRY after 3 retries set state to error, return state of WitaProductInbox to acknowledge
        String witaProductInboxId = (String) stateMachine.getExtendedState().getVariables().get(Variables.WITA_PRODUCT_INBOX_ID.getVariableName());
        WitaProductInbox witaProductInbox = witaInboxService.findById(witaProductInboxId);
        if (witaProductInbox == null) {
            log.error("WitaProductInbox with id {} not found", witaProductInboxId);
            return;
        }
        witaProductInbox.setRetries(witaProductInbox.getRetries() + 1);
        if (witaProductInbox.getRetries() >= Constants.MAX_RETRY) {
            witaProductInbox.setStatus(WitaProductInbox.Status.ERROR);
        } else {
            witaProductInbox.setStatus(WitaProductInbox.Status.ACKNOWLEDGED);
        }
        log.error("Error : {} in state machine: {}", exception, stateMachine);
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        super.extendedStateChanged(key, value);
        log.debug("Extended state changed. Key: {}, value: {}", key, value);
    }

    @Override
    public void eventNotAccepted(Message<OrderProvisionEvents> event) {
        super.eventNotAccepted(event);
        // TODO: Retry
        String witaProductInboxId = (String) event.getHeaders().get(Variables.WITA_PRODUCT_INBOX_ID.getVariableName());
        WitaProductInbox witaProductInbox = witaInboxService.findById(witaProductInboxId);
        if (witaProductInbox == null) {
            log.error("WitaProductInbox with id {} not found", witaProductInboxId);
            return;
        }
        witaProductInbox.setRetries(witaProductInbox.getRetries() + 1);
        if (witaProductInbox.getRetries() >= Constants.MAX_RETRY) {
            witaProductInbox.setStatus(WitaProductInbox.Status.ERROR);
        } else {
            witaProductInbox.setStatus(WitaProductInbox.Status.ACKNOWLEDGED);
        }
        witaInboxService.save(witaProductInbox);
        log.error("Event {} not accepted", event);
    }

    @Override
    public void stateMachineStopped(StateMachine<OrderProvisionStates, OrderProvisionEvents> stateMachine) {
        super.stateMachineStopped(stateMachine);
        log.debug("State machine stopped: {}", stateMachine);
    }
}
