package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.adapter.appointment.AppointmentClientService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaMessageHandler;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.AbmMessageReceivedAction;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.InitialWitaMessageAction;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.QebMessageReceivedAction;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.bitconex.tmf.appointment.model.Appointment;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@MockBeans({@MockBean(InitialWitaMessageAction.class), @MockBean(QebMessageReceivedAction.class), @MockBean(AbmMessageReceivedAction.class)})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ERLMProcessingIT extends IntegrationTestBase {
    private static final String BUSINESS_KEY = "erlm_1";

    @Autowired
    SpringStateMachineOrderProvisionWorkflowEngine workflowEngine;

    @Qualifier("ERLMHandler")
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
            .id(BUSINESS_KEY)
            .state("inProgress")
            .category("category")
            .description("description")
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

        workflowEngine.startProcess(BUSINESS_KEY);
        workflowEngine.sendMessage(BUSINESS_KEY,
            ORDER_RECEIVED.withHeaders(Map.of(Variables.RESOURCE_ORDER_ID.getVariableName(), resourceOrder.getId())));
        workflowEngine.sendMessage(BUSINESS_KEY, ORDER_SENT.getMessage());
        workflowEngine.sendMessage(BUSINESS_KEY,
            QEB_MESSAGE_RECEIVED.getMessage());
        workflowEngine.sendMessage(BUSINESS_KEY,
            ABM_MESSAGE_RECEIVED.getMessage());
    }

    @AfterAll
    void tearDown() {
        this.verifyHub("resourceOrderAttributeValueChangeEvent");
        this.destroyHub();
    }

    @Test
    @SneakyThrows
    void testProcessERLMMessage() {
        String message = FileTestUtil.readResourceContent("payloads/wita/v15/erlm/ERLM_test_message.xml");

        AnnehmenMeldungRequestType annehmenMeldungRequestType = MarshallUtil.unmarshall(message, AnnehmenMeldungRequestType.class);

        witaMessageHandler.handle(BUSINESS_KEY, annehmenMeldungRequestType, "erlm_inbox_id");

        List<Appointment> appointmentList = appointmentClientService.getAppointments();
        assertThat(appointmentList).isNotNull();

        Appointment appointment = appointmentList.stream()
            .filter(a -> a.getExternalId() != null && a.getCategory() != null && a.getCategory().equals("completionDate"))
            .findFirst()
            .orElseThrow();

        var resourceOrder = resourceOrderService.findById(BUSINESS_KEY);

        assertThat(appointment).isNotNull();
        assertThat(appointment.getExternalId()).isEqualTo(BUSINESS_KEY);
        assertThat(appointment.getCategory()).isEqualTo("completionDate");

        assertThat(resourceOrder.getCompletionDate()).isEqualTo(appointment.getValidFor().getStartDateTime());
        assertThat(resourceOrder.getResourceOrderItems().get(0).getResource().getResourceCharacteristic().stream().filter(c -> Objects.equals(c.getName(), "completionDate")).findFirst().get().getValue()).contains(appointment.getId());

    }
}
