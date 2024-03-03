package de.bitconex.hub;

import de.bitconex.hub.controller.HubController;
import de.bitconex.hub.eventing.EventRegistrationService;
import de.bitconex.hub.eventing.EventRegistrationServiceJpa;
import de.bitconex.hub.eventing.EventRegistrationServiceMongo;
import de.bitconex.hub.model.EventSubscription;
import de.bitconex.hub.model.EventSubscriptionOutput;
import de.bitconex.hub.util.EventSubscriptionUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("jpa")
class HubAppTests {

    @Autowired
    private EventRegistrationService eventRegistrationService;

    @Autowired
    private HubController hubController;

    @Value("${hub.eventRegistrationService}")
    private String eventRegistrationServiceProperty;

    @SpringBootConfiguration
    @ComponentScan(basePackages = {"de.bitconex.hub"})
    @ConfigurationPropertiesScan(basePackages = {"de.bitconex.hub.config"})
    @EnableAutoConfiguration
    public static class TestConfig {
    }

    @Test
    @DisplayName("Application context load")
    void contextLoads() {
        assertThat(hubController).isNotNull();
    }

    @Test
    @DisplayName("Assert event registration service implementation class")
    void testEventRegistrationServiceImpl() {
         if (EventRegistrationServiceType.MONGO.getValue().equals(eventRegistrationServiceProperty)) {
            assertThat(eventRegistrationService.getClass()).isEqualTo(EventRegistrationServiceMongo.class);
        } else if (EventRegistrationServiceType.JPA.getValue().equals(eventRegistrationServiceProperty)) {
            assertThat(eventRegistrationService.getClass()).isEqualTo(EventRegistrationServiceJpa.class);
        }
    }

    @DisplayName("Test map event subscription to event subscription output")
    @Test
    void testEventSubscriptionUtil() {
        EventSubscription eventSubscription = EventSubscription.builder()
                .id(UUID.randomUUID().toString())
                .query("ResourceOrderCreateEvent,ResourceOrderAttributeChangeEvent")
                .callback("http://host:port")
                .build();

        EventSubscriptionOutput eventSubscriptionOutput = EventSubscriptionUtil.mapToOutput(eventSubscription);

        assertThat(eventSubscriptionOutput).isNotNull();
        assertThat(eventSubscriptionOutput.getId()).isEqualTo(eventSubscription.getId());
    }
}