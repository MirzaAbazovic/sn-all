package de.bitconex.hub;

import lombok.Getter;

@Getter
public enum EventRegistrationServiceType {
    JPA("EventRegistrationServiceJpa"),
    MONGO("EventRegistrationServiceMongo");

    private final String value;

    EventRegistrationServiceType(String eventRegistrationServiceType) {
        value = eventRegistrationServiceType;
    }
}
