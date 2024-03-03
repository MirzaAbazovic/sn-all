package de.bitconex.adlatus.common.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WitaProductInbox implements Serializable {

    private String id;

    @NotNull
    @Column(name = "external_order_id")
    private String externalOrderId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "retries")
    private Integer retries;

    @Column(name = "message", length = 65535)
    private String message;// Storing XML as String for simplicity

    public enum Status {
        ACKNOWLEDGED,
        PROCESSED,
        ERROR,
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WitaProductInbox that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
