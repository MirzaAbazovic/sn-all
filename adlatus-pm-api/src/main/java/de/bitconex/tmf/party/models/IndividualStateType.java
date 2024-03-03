package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valid values for the lifecycle state of the individual
 */
public enum IndividualStateType {

    INITIALIZED("initialized"),

    VALIDATED("validated"),

    DECEADED("deceaded");

    private String value;

    IndividualStateType(String value) {
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
    public static IndividualStateType fromValue(String value) {
        for (IndividualStateType b : IndividualStateType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

