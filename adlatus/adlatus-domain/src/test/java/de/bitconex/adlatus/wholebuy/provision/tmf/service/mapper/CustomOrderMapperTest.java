package de.bitconex.adlatus.wholebuy.provision.tmf.service.mapper;

import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.OrderMapper;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.OrderMapperImpl;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.AppointmentRef;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ExternalReference;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomOrderMapperTest {

    // cut
    OrderMapper orderMapper;

    private CustomOrderMapper cut;


    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapperImpl();
        cut = new CustomOrderMapper(orderMapper);
    }

    static Object[][] resourceOrderTmf() {
        // use entity model instead

        var resourceOrderTmf = ResourceOrder.builder()
            .id("1234")
            .href("resourceOrder/1234")
            .atBaseType("OrderBaseType")
            .atSchemaLocation("http://example.com/schema/order")
            .atType("OrderType")
            .category("Test Category")
            .completionDate(OffsetDateTime.now())
            .expectedCompletionDate(OffsetDateTime.now())
            .description("description")
            .externalId("externalId")
            .requestedCompletionDate(OffsetDateTime.now())
            .state(ResourceOrder.ResourceOrderState.IN_PROGRESS)
            .orderDate(OffsetDateTime.now())
            .requestedStartDate(OffsetDateTime.now())
            .name("name")
            .orderDate(OffsetDateTime.now())
            .orderType("orderType")
            .priority(1)
            .notes(
                List.of(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Note.builder()
                    .author("author")
                    .date(OffsetDateTime.now())
                    .text("text")
                    .build())
            )
            .externalReferences(
                List.of(ExternalReference.builder()
                    .id("id")
                    .atBaseType("baseType")
                    .atSchemaLocation("http://example.com/schema/order")
                    .build())
            )
            .relatedParties(
                List.of(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty.builder()
                    .id("id")
                    .href("href")
                    .role("role")
                    .build())
            )
                .resourceOrderItems(
                        List.of(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem.builder()
                            .id("id")
                            .action("action")
                            .quantity(1)
                            .state("state")
                            .resource(
                                    de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource.builder()
                                            .id("id")
                                            .relatedParties(
                                                    List.of(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty.builder()
                                                            .id("id")
                                                            .href("href")
                                                            .role("role")
                                                            .build())
                                            )
                                            .resourceCharacteristic(
                                                    List.of(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceCharacteristic.builder()
                                                            .name("name")
                                                            .value("value")
                                                            .build())
                                            )
                                            .build()
                            )
                            .appointmentRef(
                                    AppointmentRef.builder()
                                            .id("id")
                                            .href("href")
                                            .description("description")
                                            .build()
                            )
                            .action("add")
                            .build()
                        )
                )
            .build();

        return new Object[][]{
            {null, true},
            {resourceOrderTmf, false}
        };
    }

    @ParameterizedTest
    @MethodSource("resourceOrderTmf")
    void mapToResourceOrderTmf(ResourceOrder resourceOrderTmf, boolean isNull) {
        // todo: Dzemil
        de.bitconex.tmf.rom.model.ResourceOrder resourceOrderEntity = cut.mapToResourceOrderTmf(resourceOrderTmf);

        if (isNull) {
            assertNull(resourceOrderEntity);
            return;
        }

        assertNotNull(resourceOrderEntity);
        assertThat(resourceOrderEntity.getId()).isEqualTo(resourceOrderTmf.getId());
        assertThat(resourceOrderEntity.getHref()).isEqualTo(resourceOrderTmf.getHref());
        assertThat(resourceOrderEntity.getAtBaseType()).isEqualTo(resourceOrderTmf.getAtBaseType());
        assertThat(resourceOrderEntity.getAtSchemaLocation()).isEqualTo(URI.create(resourceOrderTmf.getAtSchemaLocation()));
        assertThat(resourceOrderEntity.getAtType()).isEqualTo(resourceOrderTmf.getAtType());
        assertThat(resourceOrderEntity.getCategory()).isEqualTo(resourceOrderTmf.getCategory());
        assertThat(resourceOrderEntity.getCompletionDate()).isEqualTo(resourceOrderTmf.getCompletionDate());
        assertThat(resourceOrderEntity.getExpectedCompletionDate()).isEqualTo(resourceOrderTmf.getExpectedCompletionDate());
        assertThat(resourceOrderEntity.getDescription()).isEqualTo(resourceOrderTmf.getDescription());
        assertThat(resourceOrderEntity.getExternalId()).isEqualTo(resourceOrderTmf.getExternalId());
        assertThat(resourceOrderEntity.getRequestedCompletionDate()).isEqualTo(resourceOrderTmf.getRequestedCompletionDate());
        assertThat(resourceOrderEntity.getState()).isEqualTo(resourceOrderTmf.getState().getValue());
        assertThat(resourceOrderEntity.getOrderDate()).isEqualTo(resourceOrderTmf.getOrderDate());
        assertThat(resourceOrderEntity.getRequestedStartDate()).isEqualTo(resourceOrderTmf.getRequestedStartDate());
        assertThat(resourceOrderEntity.getName()).isEqualTo(resourceOrderTmf.getName());
        assertThat(resourceOrderEntity.getOrderType()).isEqualTo(resourceOrderTmf.getOrderType());
        assertThat(resourceOrderEntity.getPriority()).isEqualTo(resourceOrderTmf.getPriority());
        assertThat(resourceOrderEntity.getNote().size()).isEqualTo(resourceOrderTmf.getNotes().size());
        assertThat(resourceOrderEntity.getNote().get(0).getId()).isEqualTo(resourceOrderTmf.getNotes().get(0).getId());
        assertThat(resourceOrderEntity.getNote().get(0).getText()).isEqualTo(resourceOrderTmf.getNotes().get(0).getText());
        assertThat(resourceOrderEntity.getNote().get(0).getAuthor()).isEqualTo(resourceOrderTmf.getNotes().get(0).getAuthor());
        assertThat(resourceOrderEntity.getNote().get(0).getDate()).isEqualTo(resourceOrderTmf.getNotes().get(0).getDate());
        assertThat(resourceOrderEntity.getExternalReference().size()).isEqualTo(resourceOrderTmf.getExternalReferences().size());
        assertThat(resourceOrderEntity.getExternalReference().get(0).getId()).isEqualTo(resourceOrderTmf.getExternalReferences().get(0).getId());
        assertThat(resourceOrderEntity.getExternalReference().get(0).getAtBaseType()).isEqualTo(resourceOrderTmf.getExternalReferences().get(0).getAtBaseType());
        assertThat(resourceOrderEntity.getExternalReference().get(0).getAtSchemaLocation()).isEqualTo(URI.create(resourceOrderTmf.getExternalReferences().get(0).getAtSchemaLocation()));
        assertThat(resourceOrderEntity.getRelatedParty().size()).isEqualTo(resourceOrderTmf.getRelatedParties().size());
        assertThat(resourceOrderEntity.getRelatedParty().get(0).getId()).isEqualTo(resourceOrderTmf.getRelatedParties().get(0).getId());
        assertThat(resourceOrderEntity.getRelatedParty().get(0).getHref()).isEqualTo(resourceOrderTmf.getRelatedParties().get(0).getHref());
        assertThat(resourceOrderEntity.getRelatedParty().get(0).getRole()).isEqualTo(resourceOrderTmf.getRelatedParties().get(0).getRole());
        assertThat(resourceOrderEntity.getOrderItem().size()).isEqualTo(resourceOrderTmf.getResourceOrderItems().size());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getId()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getId());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getAction()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getAction());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getQuantity()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getQuantity());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getState()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getState());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getId()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getId());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getRelatedParty().size()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getRelatedParties().size());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getRelatedParty().get(0).getId()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getRelatedParties().get(0).getId());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getRelatedParty().get(0).getHref()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getRelatedParties().get(0).getHref());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getRelatedParty().get(0).getRole()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getRelatedParties().get(0).getRole());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getResourceCharacteristic().size()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().size());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getResourceCharacteristic().get(0).getName()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().get(0).getName());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getResource().getResourceCharacteristic().get(0).getValue()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().get(0).getValue());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getAppointment().getId()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getAppointmentRef().getId());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getAppointment().getHref()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getAppointmentRef().getHref());
        assertThat(resourceOrderEntity.getOrderItem().get(0).getAppointment().getDescription()).isEqualTo(resourceOrderTmf.getResourceOrderItems().get(0).getAppointmentRef().getDescription());



    }


    static Object[][] resourceOrderAdl() {
        var resourceOrderAdl = de.bitconex.tmf.rom.model.ResourceOrder.builder()
            .id("1234")
            .href("resourceOrder/1234")
            .atBaseType("OrderBaseType")
            .atSchemaLocation(URI.create("http://example.com/schema/order"))
            .atType("OrderType")
            .category("Test Category")
            .completionDate(OffsetDateTime.now())
            .expectedCompletionDate(OffsetDateTime.now())
            .description("description")
            .externalId("externalId")
            .requestedCompletionDate(OffsetDateTime.now())
            .state("inProgress")
            .orderDate(OffsetDateTime.now())
            .requestedStartDate(OffsetDateTime.now())
            .name("name")
            .orderDate(OffsetDateTime.now())
            .orderType("orderType")
            .priority(1)
                .note(
                    List.of(de.bitconex.tmf.rom.model.Note.builder()
                        .author("author")
                        .date(OffsetDateTime.now())
                        .text("text")
                        .build())
                )
                .externalReference(
                    List.of(de.bitconex.tmf.rom.model.ExternalId.builder()
                        .id("id")
                        .atBaseType("baseType")
                        .atSchemaLocation(URI.create("http://example.com/schema/order"))
                        .build())
                )
                .relatedParty(
                        List.of(de.bitconex.tmf.rom.model.RelatedParty.builder()
                            .id("id")
                            .href("href")
                            .role("role")
                            .build())
                )
                .orderItem(
                        List.of(de.bitconex.tmf.rom.model.ResourceOrderItem.builder()
                            .id("id")
                            .action("action")
                                        .quantity(1)
                                        .state("state")
                                        .resource(
                                                de.bitconex.tmf.rom.model.ResourceRefOrValue.builder()
                                                        .id("id")
                                                        .relatedParty(
                                                                List.of(de.bitconex.tmf.rom.model.RelatedParty.builder()
                                                                        .id("id")
                                                                        .href("href")
                                                                        .role("role")
                                                                        .build())
                                                        )
                                                        .resourceCharacteristic(
                                                                List.of(de.bitconex.tmf.rom.model.Characteristic.builder()
                                                                        .name("name")
                                                                        .value("value")
                                                                        .build())
                                                        )
                                                        .build()
                                        )
                                        .appointment(
                                                de.bitconex.tmf.rom.model.AppointmentRef.builder()
                                                        .id("id")
                                                        .href("href")
                                                        .description("description")
                                                        .build()
                                        )
                                        .action("add")
                            .build()
                        )
                )
            .build();

        return new Object[][]{
            {null, true},
            {resourceOrderAdl, false}
        };
    }

    @ParameterizedTest
    @MethodSource("resourceOrderAdl")
    void mapToResourceOrderAdl(de.bitconex.tmf.rom.model.ResourceOrder resourceOrderAdl, boolean isNull) {
        // todo: Dzemil

        ResourceOrder resourceOrderEntity = cut.mapToResourceOrderAdl(resourceOrderAdl);

        if (isNull) {
            assertNull(resourceOrderEntity);
            return;
        }

        assertNotNull(resourceOrderEntity);
        assertThat(resourceOrderEntity.getId()).isEqualTo(resourceOrderAdl.getId());
        assertThat(resourceOrderEntity.getHref()).isEqualTo(resourceOrderAdl.getHref());
        assertThat(resourceOrderEntity.getAtBaseType()).isEqualTo(resourceOrderAdl.getAtBaseType());
        assertThat(resourceOrderEntity.getAtSchemaLocation()).isEqualTo(resourceOrderAdl.getAtSchemaLocation().toASCIIString());
        assertThat(resourceOrderEntity.getAtType()).isEqualTo(resourceOrderAdl.getAtType());
        assertThat(resourceOrderEntity.getCategory()).isEqualTo(resourceOrderAdl.getCategory());
        assertThat(resourceOrderEntity.getCompletionDate()).isEqualTo(resourceOrderAdl.getCompletionDate());
        assertThat(resourceOrderEntity.getExpectedCompletionDate()).isEqualTo(resourceOrderAdl.getExpectedCompletionDate());
        assertThat(resourceOrderEntity.getDescription()).isEqualTo(resourceOrderAdl.getDescription());
        assertThat(resourceOrderEntity.getExternalId()).isEqualTo(resourceOrderAdl.getExternalId());
        assertThat(resourceOrderEntity.getRequestedCompletionDate()).isEqualTo(resourceOrderAdl.getRequestedCompletionDate());
        assertThat(resourceOrderEntity.getState().getValue()).isEqualTo(resourceOrderAdl.getState());
        assertThat(resourceOrderEntity.getOrderDate()).isEqualTo(resourceOrderAdl.getOrderDate());
        assertThat(resourceOrderEntity.getRequestedStartDate()).isEqualTo(resourceOrderAdl.getRequestedStartDate());
        assertThat(resourceOrderEntity.getName()).isEqualTo(resourceOrderAdl.getName());
        assertThat(resourceOrderEntity.getOrderType()).isEqualTo(resourceOrderAdl.getOrderType());
        assertThat(resourceOrderEntity.getPriority()).isEqualTo(resourceOrderAdl.getPriority());
        assertThat(resourceOrderEntity.getNotes().size()).isEqualTo(resourceOrderAdl.getNote().size());
        assertThat(resourceOrderEntity.getNotes().get(0).getId()).isEqualTo(resourceOrderAdl.getNote().get(0).getId());
        assertThat(resourceOrderEntity.getNotes().get(0).getText()).isEqualTo(resourceOrderAdl.getNote().get(0).getText());
        assertThat(resourceOrderEntity.getNotes().get(0).getAuthor()).isEqualTo(resourceOrderAdl.getNote().get(0).getAuthor());
        assertThat(resourceOrderEntity.getNotes().get(0).getDate()).isEqualTo(resourceOrderAdl.getNote().get(0).getDate());
        assertThat(resourceOrderEntity.getExternalReferences().size()).isEqualTo(resourceOrderAdl.getExternalReference().size());
        assertThat(resourceOrderEntity.getExternalReferences().get(0).getId()).isEqualTo(resourceOrderAdl.getExternalReference().get(0).getId());
        assertThat(resourceOrderEntity.getExternalReferences().get(0).getAtBaseType()).isEqualTo(resourceOrderAdl.getExternalReference().get(0).getAtBaseType());
        assertThat(resourceOrderEntity.getExternalReferences().get(0).getAtSchemaLocation()).isEqualTo(resourceOrderAdl.getExternalReference().get(0).getAtSchemaLocation().toASCIIString());
        assertThat(resourceOrderEntity.getRelatedParties().size()).isEqualTo(resourceOrderAdl.getRelatedParty().size());
        assertThat(resourceOrderEntity.getRelatedParties().get(0).getId()).isEqualTo(resourceOrderAdl.getRelatedParty().get(0).getId());
        assertThat(resourceOrderEntity.getRelatedParties().get(0).getHref()).isEqualTo(resourceOrderAdl.getRelatedParty().get(0).getHref());
        assertThat(resourceOrderEntity.getRelatedParties().get(0).getRole()).isEqualTo(resourceOrderAdl.getRelatedParty().get(0).getRole());
        assertThat(resourceOrderEntity.getResourceOrderItems().size()).isEqualTo(resourceOrderAdl.getOrderItem().size());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getId()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getId());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getAction()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getAction());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getQuantity()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getQuantity());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getState()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getState());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getId()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getId());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getRelatedParties().size()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getRelatedParty().size());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getRelatedParties().get(0).getId()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getRelatedParty().get(0).getId());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getRelatedParties().get(0).getHref()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getRelatedParty().get(0).getHref());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getRelatedParties().get(0).getRole()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getRelatedParty().get(0).getRole());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().size()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getResourceCharacteristic().size());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().get(0).getName()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getResourceCharacteristic().get(0).getName());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().get(0).getValue()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getResource().getResourceCharacteristic().get(0).getValue());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getAppointmentRef().getId()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getAppointment().getId());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getAppointmentRef().getHref()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getAppointment().getHref());
        assertThat(resourceOrderEntity.getResourceOrderItems().get(0).getAppointmentRef().getDescription()).isEqualTo(resourceOrderAdl.getOrderItem().get(0).getAppointment().getDescription());
    }
}