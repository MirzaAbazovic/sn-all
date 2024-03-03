package de.bitconex.adlatus.wholebuy.provision.workflow.guard;

import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class QebMessageReceivedGuard implements Guard<OrderProvisionStates, OrderProvisionEvents> {
    @Override
    public boolean evaluate(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        //TODO Logic for the guard (or some condition) when QEB message is received
        return true;
    }
}