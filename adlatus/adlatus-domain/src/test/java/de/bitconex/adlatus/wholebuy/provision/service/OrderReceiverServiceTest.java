package de.bitconex.adlatus.wholebuy.provision.service;

import de.bitconex.adlatus.inbox.service.OrderReceiverService;
import de.bitconex.adlatus.inbox.service.OrderReceiverServiceImpl;
import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogService;
import de.bitconex.adlatus.wholebuy.provision.service.notification.OrderEventDispatcher;
import de.bitconex.adlatus.wholebuy.provision.service.order.TmfOrderInboxService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.OrderMapper;
import de.bitconex.adlatus.inbox.service.OrderValidator;
import de.bitconex.adlatus.inbox.service.OrderValidatorImpl;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.OrderMapperImpl;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecificationCharacteristic;
import de.bitconex.tmf.rom.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderReceiverServiceTest {

    private TmfOrderInboxService orderInboxService;
    private OrderMapper orderMapper;
    private OrderValidator orderValidator;
    private OrderEventDispatcher orderEventDispatcher;
    private OrderReceiverService orderReceiverService;

    private ResourceCatalogService resourceCatalogService;

    @BeforeEach
    void setUp() {
        orderInboxService = Mockito.mock(TmfOrderInboxService.class);
        resourceCatalogService = Mockito.mock(ResourceCatalogService.class);
        orderEventDispatcher = Mockito.mock(OrderEventDispatcher.class);
        orderMapper = new OrderMapperImpl();
        orderValidator = new OrderValidatorImpl(resourceCatalogService);
        orderReceiverService = new OrderReceiverServiceImpl(orderInboxService, orderMapper, orderValidator, orderEventDispatcher);
    }

    @Test
    void shouldSaveOrderToInbox() {
        ResourceOrderCreate resourceOrderCreate = ResourceOrderCreate.builder()
            .orderItem(
                List.of(ResourceOrderItem.builder()
                    .id("1")
                    .action("add")
                    .resource(ResourceRefOrValue.builder()
                        .resourceSpecification(ResourceSpecificationRef.builder()
                            .id("1")
                            .build())
                        .resourceCharacteristic(List.of(Characteristic.builder()
                            .name("name")
                            .build()))
                        .build())
                    .build())
            )
            .build();

        when(resourceCatalogService.retrieveSpecification("1")).thenReturn(ResourceSpecification.builder()
            .id("1")
            .name("name")
            .resourceSpecCharacteristic(List.of(ResourceSpecificationCharacteristic.builder()
                .name("name")
                .build()))
            .build());


        ResourceOrder result = orderReceiverService.saveOrderToInbox(resourceOrderCreate);

        assertThat(result.getId()).isEqualTo(result.getId());
        verify(orderInboxService).save(any());
        verify(orderEventDispatcher).notifyOrderCreateEvent(result);
    }

    @Test
    void shouldThrowExceptionWhenValidationFails() {
        ResourceOrderCreate resourceOrderCreate = new ResourceOrderCreate();

        assertThatThrownBy(() -> orderReceiverService.saveOrderToInbox(resourceOrderCreate))
            .isInstanceOf(RuntimeException.class);

        verify(orderInboxService, never()).save(any());
        verify(orderEventDispatcher, never()).notifyOrderCreateEvent(any());
    }
}