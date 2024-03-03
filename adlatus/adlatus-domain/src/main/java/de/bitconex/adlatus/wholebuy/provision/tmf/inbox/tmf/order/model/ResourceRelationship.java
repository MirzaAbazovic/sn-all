package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class ResourceRelationship extends TmfBaseEntity {
    private String relationshipType;
}
