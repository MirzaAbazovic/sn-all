package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ACKNOWLEDGED("acknowledged"),
    IN_PROGRESS("inProgress"),
    FINISHED("finished");

    private final String value;

    Status(String value) {
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
    public static Status fromValue(String status) {
        for (Status b : Status.values()) {
            if (b.value.equals(status)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + status + "'");
    }
}
