package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.adapter.appointment.AppointmentClientService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaMessageHandler;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.InitialWitaMessageAction;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.QebMessageReceivedAction;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderItem;
import de.bitconex.tmf.rom.model.ResourceRefOrValue;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@MockBeans({@MockBean(InitialWitaMessageAction.class), @MockBean(QebMessageReceivedAction.class)})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ABMProcessingIT extends IntegrationTestBase {

    @Autowired
    SpringStateMachineOrderProvisionWorkflowEngine workflowEngine;

    @Qualifier("ABMHandler")
    @Autowired
    WitaMessageHandler witaMessageHandler;

    @Autowired
    AppointmentClientService appointmentClientService;

    @Autowired
    ResourceOrderService resourceOrderService;

    @Autowired
    CustomOrderMapper orderMapper;

    @BeforeAll
    void setUp() {
        this.listenToHub("ResourceOrderAttributeValueChangeEvent");
        ResourceOrder resourceOrder = ResourceOrder.builder()
            .id("order_12345")
            .category("category")
            .description("description")
            .state("inProgress")
            .orderItem(List.of(
                ResourceOrderItem.builder()
                    .id("order_item_1")
                    .action("add")
                    .resource(
                        ResourceRefOrValue.builder()
                            .id("resource")
                            .resourceCharacteristic(new ArrayList<>())
                            .build()
                    )
                    .build()
            ))
            .build();

        var resourceOrderAdl = orderMapper.mapToResourceOrderAdl(resourceOrder);
        resourceOrderService.save(resourceOrderAdl);

        workflowEngine.startProcess("order_12345");
        workflowEngine.sendMessage("order_12345",
            ORDER_RECEIVED.withHeaders(Map.of(Variables.RESOURCE_ORDER_ID.getVariableName(), resourceOrder.getId())));
        workflowEngine.sendMessage("order_12345", ORDER_SENT.getMessage());
        workflowEngine.sendMessage("order_12345",
            QEB_MESSAGE_RECEIVED.getMessage());
    }

    @AfterAll
    void tearDown() {
        this.verifyHub("resourceOrderAttributeValueChangeEvent");
        this.destroyHub();
    }

    @Test
    @SneakyThrows
    void testProcessABMMessage() {
        String message = FileTestUtil.readResourceContent("payloads/wita/v15/abm/order_12345_message.xml");

        AnnehmenMeldungRequestType annehenMeldung = MarshallUtil.unmarshall(message, AnnehmenMeldungRequestType.class);

        witaMessageHandler.handle("order_12345", annehenMeldung, "abm_inbox_id");

        var appointments = appointmentClientService.getAppointments();

        var appointment = appointments.stream().filter(a ->
            a.getExternalId() != null
                && a.getExternalId().equals("order_12345")
                && a.getCategory() != null
                && a.getCategory().equals("delivery")
        ).findFirst().orElse(null);

        var ro = resourceOrderService.findById("order_12345");

        assertThat(appointment).isNotNull();
        assertThat(appointment.getValidFor().getStartDateTime().toLocalDate()).isEqualTo(LocalDate.of(2023, 12, 6));
        assertThat(appointment.getValidFor().getEndDateTime().toLocalDate()).isEqualTo(LocalDate.of(2023, 12, 6));

        assertThat(ro.getExpectedCompletionDate()).isEqualTo(LocalDate.parse(LocalDate.of(2023, 12, 6).toString()).atStartOfDay().atOffset(ZoneOffset.UTC));
        assertThat(ro.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().stream().filter(c -> Objects.equals(c.getName(), "lineId")).findFirst().get().getValue()).isEqualTo("DEU.DTAG.VWJLR");
    }
}
