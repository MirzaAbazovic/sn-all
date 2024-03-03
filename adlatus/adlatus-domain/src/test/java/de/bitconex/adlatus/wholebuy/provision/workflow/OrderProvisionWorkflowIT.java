package de.bitconex.adlatus.wholebuy.provision.workflow;

import de.bitconex.adlatus.wholebuy.provision.it.IntegrationTestBase;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.*;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Map;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.*;
import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MockBeans({
    @MockBean(InitialWitaMessageAction.class),
    @MockBean(QebMessageReceivedAction.class),
    @MockBean(AbmMessageReceivedAction.class),
    @MockBean(EntmMessageReceivedAction.class),
    @MockBean(ErlmMessageReceivedAction.class)
})
class OrderProvisionWorkflowIT extends IntegrationTestBase {
    private static final LocalDate BINDING_DELIVERY_DATE = LocalDate.of(2023, 1, 20);

    @Autowired
    SpringStateMachineOrderProvisionWorkflowEngine cut;

    static Object[][] conditions() {
        return new Object[][]{
            {true, "provision1"},
            {false, "provision2"}
        };
    }

    @ParameterizedTest
    @MethodSource("conditions")
    void testHappyPathWorkflow(boolean isEntmFirst, String businessKey) {
        ResourceOrder resourceOrder = ResourceOrder.builder()
            .id(businessKey)
            .category("category")
            .description("description")
            .build();

        cut.startProcess(businessKey);
        assertThat(cut.getProcessState(businessKey)).isEqualTo(INITIAL);
        assertThat(cut.getVariables(businessKey)).isEmpty();

        cut.sendMessage(businessKey,
            ORDER_RECEIVED.withHeaders(Map.of(Variables.RESOURCE_ORDER_ID.getVariableName(), resourceOrder.getId())));
        assertThat(cut.getProcessState(businessKey)).isEqualTo(ACKNOWLEDGED);
        assertThat(cut.getVariables(businessKey))
            .hasSize(1)
            .containsExactlyEntriesOf(Map.of(Variables.RESOURCE_ORDER_ID.getVariableName(), resourceOrder.getId()));

        cut.sendMessage(businessKey, ORDER_SENT.getMessage());

        assertThat(cut.getProcessState(businessKey)).isEqualTo(WAITING_QEB);

        cut.sendMessage(businessKey,
            QEB_MESSAGE_RECEIVED.withHeaders(Map.of(Variables.BINDING_DELIVERY_DATE.getVariableName(), BINDING_DELIVERY_DATE)));
        assertThat(cut.getProcessState(businessKey)).isEqualTo(WAITING_ABM);
        assertThat(cut.getVariables(businessKey))
            .hasSize(2)
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                Variables.RESOURCE_ORDER_ID.getVariableName(), resourceOrder.getId(),
                Variables.BINDING_DELIVERY_DATE.getVariableName(), BINDING_DELIVERY_DATE));

        cut.sendMessage(businessKey, ABM_MESSAGE_RECEIVED.getMessage());
        assertThat(cut.getProcessState(businessKey)).isEqualTo(ERLM_ENTM_PARENT);
        if (isEntmFirst) {
            cut.sendMessage(businessKey, ENTM_MESSAGE_RECEIVED.getMessage());
            assertThat(cut.getProcessStates(businessKey)).containsExactlyInAnyOrder(ERLM_ENTM_PARENT, END_ENTM, WAITING_ERLM);
            cut.sendMessage(businessKey, ERLM_MESSAGE_RECEIVED.getMessage());
            assertThat(cut.getProcessState(businessKey)).isEqualTo(COMPLETED);
        } else {
            cut.sendMessage(businessKey, ERLM_MESSAGE_RECEIVED.getMessage());
            assertThat(cut.getProcessStates(businessKey)).containsExactlyInAnyOrder(ERLM_ENTM_PARENT, END_ERLM, WAITING_ENTM);
            cut.sendMessage(businessKey, ENTM_MESSAGE_RECEIVED.getMessage());
            assertThat(cut.getProcessState(businessKey)).isEqualTo(COMPLETED);
        }
    }
}