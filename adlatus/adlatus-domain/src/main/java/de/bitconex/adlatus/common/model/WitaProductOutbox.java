package de.bitconex.adlatus.common.model;

import de.bitconex.adlatus.common.model.converter.WitaStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WitaProductOutbox implements Serializable {
    private String id;

    @NotNull
    private String externalOrderId;

    @Convert(converter = WitaStatusConverter.class) // todo: do we need this
    private Status status;

    private String message; // Storing XML as String for simplicity

    @Column(name = "retries")
    private Integer retries;


    public enum Status {
        CREATED("CREATED"),
        SENT("SENT"),
        ERROR("ERROR");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name();
        }

        public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown enum type " + value + ", Allowed values are " + Arrays.toString(Status.values()));
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WitaProductOutbox that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

