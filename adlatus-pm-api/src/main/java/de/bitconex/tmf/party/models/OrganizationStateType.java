package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valid values for the lifecycle state of the organization
 */
public enum OrganizationStateType {

    INITIALIZED("initialized"),

    VALIDATED("validated"),

    CLOSED("closed");

    private String value;

    OrganizationStateType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static OrganizationStateType fromValue(String value) {
        for (OrganizationStateType b : OrganizationStateType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

