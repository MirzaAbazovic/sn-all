package de.bitconex.adlatus.wholebuy.provision.workflow.persistence;

import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class ProvisioningMongoPersisterConfig {

    @Bean
    public StateMachineRuntimePersister<OrderProvisionStates, OrderProvisionEvents, String> stateMachineRuntimePersister(
        MongoDbStateMachineRepository mongoDbStateMachineRepository) {
        return new MongoDbPersistingStateMachineInterceptor<>(mongoDbStateMachineRepository);
    }
}
