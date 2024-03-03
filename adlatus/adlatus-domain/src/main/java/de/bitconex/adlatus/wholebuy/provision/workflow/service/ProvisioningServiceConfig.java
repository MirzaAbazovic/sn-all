package de.bitconex.adlatus.wholebuy.provision.workflow.service;

import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class ProvisioningServiceConfig {

    @Bean
    public StateMachineService<OrderProvisionStates, OrderProvisionEvents> provisioningStateMachineService(
        StateMachineFactory<OrderProvisionStates, OrderProvisionEvents> stateMachineFactory,
        StateMachineRuntimePersister<OrderProvisionStates, OrderProvisionEvents, String> stateMachineRuntimePersister
    ) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }
}
