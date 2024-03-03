package de.bitconex.adlatus.wholebuy.provision.workflow.action;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.service.wita.OutgoingMessageGeneratorService;
import de.bitconex.adlatus.wholebuy.provision.service.scheduling.WitaOutboxService;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitialWitaMessageAction implements Action<OrderProvisionStates, OrderProvisionEvents> {
    private final OutgoingMessageGeneratorService outgoingMessageGeneratorService;
    private final WitaOutboxService witaOutboxService;
    private final ResourceOrderService resourceOrderService;

    CustomOrderMapper customOrderMapper;

    public InitialWitaMessageAction(OutgoingMessageGeneratorService outgoingMessageGeneratorService, WitaOutboxService witaOutboxService, ResourceOrderService resourceOrderService, CustomOrderMapper customOrderMapper) {
        this.outgoingMessageGeneratorService = outgoingMessageGeneratorService;
        this.witaOutboxService = witaOutboxService;
        this.resourceOrderService = resourceOrderService;
        this.customOrderMapper = customOrderMapper;
    }

    @Override
    public void execute(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        var stateVariables = context.getStateMachine().getExtendedState().getVariables();
        log.info("New TMF message received. Process vars: {}", stateVariables);
        String id = (String) stateVariables.get(Variables.RESOURCE_ORDER_ID.getVariableName());
        var resourceOrder = resourceOrderService.findById(id);

        createOutgoingMessage(customOrderMapper.mapToResourceOrderTmf(resourceOrder));
        resourceOrder.setState(ResourceOrder.ResourceOrderState.IN_PROGRESS);

        resourceOrderService.save(resourceOrder);
    }

    private void createOutgoingMessage(de.bitconex.tmf.rom.model.ResourceOrder resourceOrder) {
        // TODO support creating of messages for other interfaces
        log.info("Creating outgoing WITA message for order {}", resourceOrder);
        // TODO call generic generator service which will resolve the correct generator based on the interface type which will be resolved from the order
        var createOrderMessage = outgoingMessageGeneratorService.generate(resourceOrder);  // TODO return interface type from generator
        log.info("Created outgoing WITA message: {}", createOrderMessage);
        persistOutgoingMessage(resourceOrder.getId(), createOrderMessage, TelecomInterfaceType.WITA15);
    }

    private void persistOutgoingMessage(String orderId, String message, TelecomInterfaceType telecomInterfaceType) {
        log.info("Persisting outgoing message: {}", message);
        witaOutboxService.save(orderId, message);
    }
}

