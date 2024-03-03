package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ResourceFeatureRelationship {
    @Id
    private String id;
    private String name;
    private String relationshipType;
    private TimePeriod validFor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceFeatureRelationship that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
