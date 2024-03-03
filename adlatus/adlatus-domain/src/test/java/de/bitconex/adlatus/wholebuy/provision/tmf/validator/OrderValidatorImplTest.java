package de.bitconex.adlatus.wholebuy.provision.tmf.validator;

import de.bitconex.adlatus.common.infrastructure.exception.CreateOrderInvalidException;
import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogService;
import de.bitconex.adlatus.inbox.service.OrderValidatorImpl;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecificationCharacteristic;
import de.bitconex.tmf.rom.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OrderValidatorImplTest {

    @Mock
    ResourceCatalogService resourceCatalogService;

    private OrderValidatorImpl orderValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderValidator = new OrderValidatorImpl(resourceCatalogService);
    }

    @Test
    void validatesOrderSuccessfully() {
        ResourceOrderCreate order = ResourceOrderCreate.builder()
            .orderItem(List.of(
                    ResourceOrderItem.builder()
                        .id("1")
                        .action("add")
                        .resource(ResourceRefOrValue.builder()
                            .resourceSpecification(ResourceSpecificationRef.builder()
                                .id("1")
                                .name("name")
                                .build())
                            .resourceCharacteristic(List.of(Characteristic.builder()
                                .name("name")
                                .build()))
                            .build())
                        .build()
                )
            )
            .build();

        when(resourceCatalogService.retrieveSpecification("1")).thenReturn(
            ResourceSpecification.builder()
                .id("1")
                .name("name")
                .resourceSpecCharacteristic(
                    List.of(
                        ResourceSpecificationCharacteristic.builder().name("name").build()
                    )
                )
                .build());

        orderValidator.validate(order);
    }

    @Test
    void failsToValidateOrderWithMissingFields() {
        ResourceOrderCreate order = ResourceOrderCreate.builder().build();

        assertThrows(CreateOrderInvalidException.class, () -> {
            orderValidator.validate(order);
        });
    }

    @Test
    void failsToValidateNullOrder() {
        assertThrows(NullPointerException.class, () -> {
            orderValidator.validate(null);
        });
    }
}