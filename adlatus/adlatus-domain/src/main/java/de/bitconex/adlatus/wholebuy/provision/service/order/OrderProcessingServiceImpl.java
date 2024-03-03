package de.bitconex.adlatus.wholebuy.provision.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.adlatus.common.infrastructure.exception.OrderInvalidException;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.common.util.json.JsonUtil;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.bitconex.tmf.rom.model.ResourceOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class OrderProcessingServiceImpl implements OrderProcessingService {
    private final OrderProvisionWorkflowEngine provisionWorkflowEngine;
    private final ResourceOrderService resourceOrderService;
    private final CustomOrderMapper customOrderMapper;

    public OrderProcessingServiceImpl(OrderProvisionWorkflowEngine provisionWorkflowEngine,
                                      CustomOrderMapper customOrderMapper,
                                      ResourceOrderService resourceOrderService) {
        this.provisionWorkflowEngine = provisionWorkflowEngine;
        this.customOrderMapper = customOrderMapper;
        this.resourceOrderService = resourceOrderService;
    }

    @Override
    public void processOrder(TmfOrderInbox orderInbox) {
        String orderId = orderInbox.getOrderId();
        if (orderId == null) {
            log.error("Invalid order for processing with id {}. Error: {}", orderInbox.getId(), "Order id is null");
            throw new OrderInvalidException("Order id is null");
        }
        log.debug("Processing order with id {}. Order: {}", orderId, orderInbox.getMessage());
        ObjectMapper objectMapper = JsonUtil.createObjectMapper();
        ResourceOrder resourceOrderDTO;
        try {
            resourceOrderDTO = objectMapper.readValue(orderInbox.getMessage(), ResourceOrder.class);
        } catch (JsonProcessingException e) {
            log.error("Invalid order for processing with id {}. Error: {}", orderInbox.getId(), e.getMessage());
            throw new OrderInvalidException(e);
        }
        // TODO fix mapping and saving of order
        var resourceOrder = customOrderMapper.mapToResourceOrderAdl(resourceOrderDTO);
        if (resourceOrder != null) {
            resourceOrderService.save(resourceOrder);
        }
        log.debug("Saved order start process with id {} ...", orderId);
        provisionWorkflowEngine.startProcess(orderId);
//        // TODO send our ResourceOrder model
        provisionWorkflowEngine.sendMessage(
            orderId,
            OrderProvisionEvents.ORDER_RECEIVED.withHeaders(Map.of(Variables.RESOURCE_ORDER_ID.getVariableName(), orderId))
            // TODO: We should propagate Resource Order ID and then wherever we use resource order, we read it from adl db
        );
    }
}