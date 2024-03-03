package de.bitconex.hub.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ConditionalOnProperty(name = "hub.eventRegistrationService", havingValue = "EventRegistrationServiceMongo")
@EnableMongoRepositories(basePackageClasses = { de.bitconex.hub.repository.EventSubscriptionMongoRepository.class })
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class HubMongoConfig {
}

