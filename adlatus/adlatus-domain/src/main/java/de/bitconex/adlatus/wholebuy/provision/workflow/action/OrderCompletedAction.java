package de.bitconex.adlatus.wholebuy.provision.workflow.action;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class OrderCompletedAction implements Action<OrderProvisionStates, OrderProvisionEvents> {
    private final ResourceOrderService resourceOrderService;

    public OrderCompletedAction(ResourceOrderService resourceOrderService) {
        this.resourceOrderService = resourceOrderService;
    }

    @Override
    public void execute(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        var stateVariables = context.getStateMachine().getExtendedState().getVariables();
        String id = (String) stateVariables.get(Variables.RESOURCE_ORDER_ID.getVariableName());
        var resourceOrder = resourceOrderService.findById(id);
        resourceOrder.setState(ResourceOrder.ResourceOrderState.COMPLETED);

        resourceOrderService.save(resourceOrder);
    }
}
