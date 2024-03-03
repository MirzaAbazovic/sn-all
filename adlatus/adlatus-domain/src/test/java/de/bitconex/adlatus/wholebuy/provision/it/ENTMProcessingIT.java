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
import java.util.List;
import java.util.Map;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@MockBeans({@MockBean(InitialWitaMessageAction.class), @MockBean(QebMessageReceivedAction.class), @MockBean(AbmMessageReceivedAction.class)})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ENTMProcessingIT extends IntegrationTestBase {
    private static final String BUSINESS_KEY = "order_123456";

    @Autowired
    SpringStateMachineOrderProvisionWorkflowEngine workflowEngine;

    @Qualifier("ENTMHandler")
    @Autowired
    WitaMessageHandler witaMessageHandler;

    @Autowired
    AppointmentClientService appointmentClientService;

    @Autowired
    ResourceOrderService resourceOrderService;

    @Autowired
    CustomOrderMapper customOrderMapper;

    @BeforeAll
    void setUp() {
        this.listenToHub("ResourceOrderAttributeValueChangeEvent");
        ResourceOrder resourceOrder = ResourceOrder.builder()
            .id(BUSINESS_KEY)
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
                            .build()
                    )
                    .build()
            ))
            .build();

        var resourceOrderAdl = customOrderMapper.mapToResourceOrderAdl(resourceOrder);
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
    void testProcessENTMMessage() {
        var body = FileTestUtil.readResourceContent("payloads/wita/v15/entm/H_00854639_message.xml");

        AnnehmenMeldungRequestType annehenMeldung = null;

        annehenMeldung = MarshallUtil.unmarshall(body, AnnehmenMeldungRequestType.class);

        witaMessageHandler.handle(BUSINESS_KEY, annehenMeldung, "entm_inbox_id");

        var appointments = appointmentClientService.getAppointments();

        var appointment = appointments.stream().filter(a ->
            a.getExternalId() != null
                && a.getExternalId().equals(BUSINESS_KEY)
                && a.getCategory() != null
                && a.getCategory().equals("payment")
        ).findFirst().orElse(null);

        assertThat(appointments).isNotNull();
        assertThat(appointment).isNotNull();
        assertThat(appointment.getValidFor().getStartDateTime().toLocalDate()).isEqualTo(LocalDate.of(2023, 9, 29));
    }
}
