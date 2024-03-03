package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceOrderPersistenceIT extends IntegrationTestBase {
    @Autowired
    private ResourceOrderService resourceOrderService;

    @Test
    void testOrderPersistence() {
        OffsetDateTime now = OffsetDateTime.now();

        ResourceOrder resourceOrder = ResourceOrder.builder()
            .id("12345652")
            .href("http://example.com/order1")
            .atBaseType("OrderBaseType")
            .atSchemaLocation("http://example.com/schema/order")
            .atType("OrderType")
            .category("Test Category")
            .completionDate(now)
            .description("Test Order Description")
            .expectedCompletionDate(now.plusDays(7))
            .externalId("EXT123")
            .name("Test Order")
            .orderDate(now)
            .orderType("Standard")
            .priority(1)
            .requestedCompletionDate(now.plusDays(5))
            .requestedStartDate(now.plusDays(2))
            .startDate(now)
            .state(ResourceOrder.ResourceOrderState.ACKNOWLEDGED)
            .build();

        ResourceOrderItem resourceOrderItem = ResourceOrderItem.builder()
            .href("http://example.com/item1")
            .atBaseType("ItemBaseType")
            .atSchemaLocation("http://example.com/schema/item")
            .atType("ItemType")
            .action("add")
            .quantity(2)
            .state("pending")
            .build();

        Resource resource = Resource.builder()
            .href("http://example.com/resource1")
            .atBaseType("ResourceBaseType")
            .atSchemaLocation("http://example.com/schema/resource")
            .atType("ResourceType")
            .atReferredType("ResourceReferredType")
            .category("Test Resource Category")
            .description("Test Resource Description")
            .endOperatingDate(now.plusYears(1))
            .name("Test Resource")
            .build();
        resourceOrderItem.setResource(resource);

        resourceOrder.setResourceOrderItems(List.of(resourceOrderItem));

        ResourceOrder savedOrder = resourceOrderService.save(resourceOrder);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo("12345652");
        assertThat(savedOrder.getName()).isEqualTo("Test Order");
        assertThat(savedOrder.getResourceOrderItems()).isNotEmpty();

    }
}

