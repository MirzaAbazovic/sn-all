package de.bitconex.hub.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class EventSubscriptionBase implements Serializable {
    private String callback;
    private String query;
}
