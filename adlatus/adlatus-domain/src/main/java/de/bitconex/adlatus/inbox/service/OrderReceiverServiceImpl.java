package de.bitconex.adlatus.inbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bitconex.adlatus.wholebuy.provision.dto.constants.Constants;
import de.bitconex.adlatus.wholebuy.provision.service.notification.OrderEventDispatcher;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.wholebuy.provision.service.order.TmfOrderInboxService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.OrderMapper;
import de.bitconex.adlatus.common.util.json.JsonUtil;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
public class OrderReceiverServiceImpl implements OrderReceiverService { // todo: Extract common InboxService interface, which has multiple impls.
    // Tmfimpl, WitaImpl, etc.?

    private final TmfOrderInboxService inboxService;
    private final OrderMapper orderMapper;
    private final OrderValidator orderValidator;
    private final OrderEventDispatcher orderEventDispatcher;

    public OrderReceiverServiceImpl(TmfOrderInboxService inboxService, OrderMapper orderMapper, OrderValidator orderValidator, OrderEventDispatcher orderEventDispatcher) {
        this.inboxService = inboxService;
        this.orderMapper = orderMapper;
        this.orderValidator = orderValidator;
        this.orderEventDispatcher = orderEventDispatcher;
    }

    @SneakyThrows(JsonProcessingException.class)
    @Override
    public ResourceOrder saveOrderToInbox(ResourceOrderCreate resourceOrderCreate) {
        orderValidator.validate(resourceOrderCreate);
        ResourceOrder resourceOrder = orderMapper.mapToResourceOrder(resourceOrderCreate);
        //TODO this generation ofd id and href is repeated in multiple places. If later we want to add full url (with host) to do change in one place
        String orderId = UUID.randomUUID().toString();
        resourceOrder.setId(orderId);
        resourceOrder.setHref(String.format("%s/%s", Constants.TMF_RESOURCE_ORDER_URL_PREFIX, orderId));
        resourceOrder.setState("acknowledged");
        resourceOrder.setOrderDate(OffsetDateTime.now());
        TmfOrderInbox build = TmfOrderInbox.builder()
            .id(UUID.randomUUID().toString())
            .orderId(orderId)
            .status(Status.ACKNOWLEDGED)
            .message(JsonUtil.createObjectMapper().writeValueAsString(resourceOrder))
            .build();
        inboxService.save(build);
        orderEventDispatcher.notifyOrderCreateEvent(resourceOrder);
        return resourceOrder;
    }
}
