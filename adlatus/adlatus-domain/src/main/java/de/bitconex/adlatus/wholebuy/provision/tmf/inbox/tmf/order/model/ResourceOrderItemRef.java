package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ResourceOrderItemRef {
    @Id
    private String id;
    @NotNull
    private String itemId;
    private String resourceOrderHref;
    private String resourceOrderId;
    @JsonProperty("@referredType")
    private String atReferredType;
    @JsonProperty("@baseType")
    private String atBaseType;
    @JsonProperty("@schemaLocation")
    private String atSchemaLocation;
    @JsonProperty("@type")
    private String atType;

    @OneToOne(mappedBy = "resourceOrderItemRef", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private ResourceOrderItemRelationship resourceOrderItemRelationship;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceOrderItemRef that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
