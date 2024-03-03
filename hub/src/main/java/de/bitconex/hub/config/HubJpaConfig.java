package de.bitconex.hub.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(name = "hub.eventRegistrationService", havingValue = "EventRegistrationServiceJpa", matchIfMissing = true)
@EnableJpaRepositories(basePackageClasses = { de.bitconex.hub.repository.EventSubscriptionRepositoryJpa.class })
public class HubJpaConfig {
}
