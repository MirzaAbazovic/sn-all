package de.bitconex.adlatus.wholebuy.provision.workflow;

import de.bitconex.adlatus.wholebuy.provision.it.IntegrationTestBase;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.*;
import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates.*;

@MockBeans({
    @MockBean(InitialWitaMessageAction.class),
    @MockBean(QebMessageReceivedAction.class),
    @MockBean(AbmMessageReceivedAction.class),
    @MockBean(EntmMessageReceivedAction.class),
    @MockBean(ErlmMessageReceivedAction.class)
})
class ProvisioningStateMachineConfigIT extends IntegrationTestBase {

    private StateMachine<OrderProvisionStates, OrderProvisionEvents> stateMachine;

    @Autowired
    StateMachineService<OrderProvisionStates, OrderProvisionEvents> stateMachineService;

    @BeforeEach
    void setUp() {
        stateMachine = stateMachineService.acquireStateMachine("machine");
        stateMachine.start();
    }

    static Object[][] plans() {
        StateMachineTestPlanBuilder<OrderProvisionStates, OrderProvisionEvents> entmFirstPlan = getBasePlan()
            .step()
            .sendEvent(ENTM_MESSAGE_RECEIVED)
            .expectStates(ERLM_ENTM_PARENT, WAITING_ERLM, END_ENTM)
            .and()
            .step()
            .sendEvent(ERLM_MESSAGE_RECEIVED)
            .expectState(COMPLETED)
            .and();

        StateMachineTestPlanBuilder<OrderProvisionStates, OrderProvisionEvents> erlmFirstPlan = getBasePlan()
            .step()
            .sendEvent(ERLM_MESSAGE_RECEIVED)
            .expectStates(ERLM_ENTM_PARENT, WAITING_ENTM, END_ERLM)
            .and()
            .step()
            .sendEvent(ENTM_MESSAGE_RECEIVED)
            .expectState(COMPLETED)
            .and();


        return new Object[][]{
            {entmFirstPlan},
            {erlmFirstPlan}
        };
    }

    @ParameterizedTest
    @MethodSource("plans")
    void testStateMachineProvisionHappyPath(StateMachineTestPlanBuilder<OrderProvisionStates, OrderProvisionEvents> plan) throws Exception {
        plan.stateMachine(stateMachine).build().test();
    }

    static StateMachineTestPlanBuilder<OrderProvisionStates, OrderProvisionEvents> getBasePlan() {
        return StateMachineTestPlanBuilder.<OrderProvisionStates, OrderProvisionEvents>builder()
            .defaultAwaitTime(2)
            .step()
            .expectState(INITIAL)
            .and()
            .step()
            .sendEvent(ORDER_RECEIVED)
            .expectState(ACKNOWLEDGED)
            .and()
            .step()
            .sendEvent(ORDER_SENT)
            .expectState(WAITING_QEB)
            .and()
            .step()
            .sendEvent(QEB_MESSAGE_RECEIVED)
            .expectState(WAITING_ABM)
            .and()
            .step()
            .sendEvent(ABM_MESSAGE_RECEIVED)
            .expectStates(ERLM_ENTM_PARENT, WAITING_ENTM, WAITING_ERLM).and();
    }
}