package de.bitconex.adlatus.wholebuy.provision.workflow;

import de.bitconex.adlatus.wholebuy.provision.workflow.action.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.*;
import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates.*;

@Configuration
@EnableStateMachineFactory(contextEvents = false, name = "provisioningStateMachineFactory")
@Slf4j
public class ProvisioningStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderProvisionStates, OrderProvisionEvents> {

    private final StateMachineListener<OrderProvisionStates, OrderProvisionEvents> debugListener;
    private final InitialWitaMessageAction initialWitaMessageAction;
    private final QebMessageReceivedAction qebMessageReceivedAction;
    private final AbmMessageReceivedAction abmMessageReceivedAction;
    private final EntmMessageReceivedAction entmMessageReceivedAction;
    private final ErlmMessageReceivedAction erlmMessageReceivedAction;
    private final OrderCompletedAction orderCompletedAction;
    private final StateMachineRuntimePersister<OrderProvisionStates, OrderProvisionEvents, String> stateMachineRuntimePersister;

    // @formatter:off
    public ProvisioningStateMachineConfig(
            StateMachineListener<OrderProvisionStates, OrderProvisionEvents> debugListener,
            InitialWitaMessageAction initialWitaMessageAction,
            QebMessageReceivedAction qebMessageReceivedAction,
            AbmMessageReceivedAction abmMessageReceivedAction,
            EntmMessageReceivedAction entmMessageReceivedAction,
            ErlmMessageReceivedAction erlmMessageReceivedAction,
            OrderCompletedAction orderCompletedAction,
            StateMachineRuntimePersister<OrderProvisionStates, OrderProvisionEvents, String> stateMachineRuntimePersister) {
        this.debugListener = debugListener;
        this.initialWitaMessageAction = initialWitaMessageAction;
        this.qebMessageReceivedAction = qebMessageReceivedAction;
        this.abmMessageReceivedAction = abmMessageReceivedAction;
        this.entmMessageReceivedAction = entmMessageReceivedAction;
        this.erlmMessageReceivedAction = erlmMessageReceivedAction;
        this.orderCompletedAction = orderCompletedAction;
        this.stateMachineRuntimePersister = stateMachineRuntimePersister;
    }
    // @formatter:on

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderProvisionStates, OrderProvisionEvents> config) throws Exception {
        // @formatter:off
        config
            .withPersistence().runtimePersister(stateMachineRuntimePersister)
            .and()
            .withConfiguration().listener(debugListener)
            .autoStartup(false);
        // @formatter:on
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderProvisionStates, OrderProvisionEvents> states) throws Exception {
        // @formatter:off
        states
            .withStates()
                .initial(INITIAL)
                .state(ACKNOWLEDGED)
                .state(WAITING_QEB)
                .state(WAITING_ABM)
                .fork(FORK)
                .state(ERLM_ENTM_PARENT)
                .join(JOIN)
                .end(COMPLETED)
                .stateEntry(COMPLETED, orderCompletedAction)
            .and()
                .withStates()
                    .parent(ERLM_ENTM_PARENT)
                    .initial(WAITING_ENTM)
                    .end(END_ENTM)
            .and()
                .withStates()
                    .parent(ERLM_ENTM_PARENT)
                    .initial(WAITING_ERLM)
                    .end(END_ERLM);
        // @formatter:on
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<OrderProvisionStates, OrderProvisionEvents> transitions) throws Exception {
        // @formatter:off
        transitions
            .withExternal()
                .source(INITIAL)
                .event(ORDER_RECEIVED)
                .target(ACKNOWLEDGED)
                .action(initialWitaMessageAction)
            .and()
            .withExternal()
                .source(ACKNOWLEDGED)
                .event(ORDER_SENT)
                .target(WAITING_QEB)
            .and()
            .withExternal()
                .source(WAITING_QEB)
                .event(QEB_MESSAGE_RECEIVED)
                .target(WAITING_ABM)
                .action(qebMessageReceivedAction)
            .and()
            .withExternal()
                .source(WAITING_ABM)
                .event(ABM_MESSAGE_RECEIVED)
                .target(FORK)
                .action(abmMessageReceivedAction)
            .and()
            .withFork()
                .source(FORK)
                .target(ERLM_ENTM_PARENT)
            .and()
            .withExternal()
                .source(WAITING_ENTM)
                .event(ENTM_MESSAGE_RECEIVED)
                .target(END_ENTM)
                .action(entmMessageReceivedAction)
            .and()
            .withExternal()
                .source(WAITING_ERLM)
                .event(ERLM_MESSAGE_RECEIVED)
                .target(END_ERLM)
                .action(erlmMessageReceivedAction)
            .and()
            .withJoin()
                .source(ERLM_ENTM_PARENT)
                .target(JOIN)
            .and()
            .withExternal()
                .source(JOIN)
                .target(COMPLETED);
        // @formatter:on
    }

}



