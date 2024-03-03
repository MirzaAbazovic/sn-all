package de.bitconex.adlatus.wholebuy.provision.it.util;

import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rom.model.*;

import java.util.List;

public class ResourceOrderTestUtil {

    public static ResourceOrderCreate createResourceOrderCreate(ResourceSpecification resourceSpecificationRef) {
        final List<ResourceOrderItem> orderItems = List.of(ResourceOrderItem.builder()
                .id("1")
                .action("add")
                .resource(ResourceRefOrValue.builder()
                        .resourceCharacteristic(
                                List.of(
                                        Characteristic.builder()
                                                .name("upstream")
                                                .value("100")
                                                .build(),
                                        Characteristic.builder()
                                                .name("downstream")
                                                .value("50")
                                                .build()
                                )
                        )
                        .resourceSpecification(ResourceSpecificationRef.builder()
                                .id(resourceSpecificationRef.getId())
                                .name(resourceSpecificationRef.getName())
                                .build()
                        ).build()
                ).build());

        return ResourceOrderCreate.builder()
                .name("order-name")
                .orderItem(
                        orderItems
                ).build();
    }
}
