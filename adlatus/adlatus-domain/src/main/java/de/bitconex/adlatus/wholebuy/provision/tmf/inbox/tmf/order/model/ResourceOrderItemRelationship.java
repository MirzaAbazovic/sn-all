package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class ResourceOrderItemRelationship extends TmfBaseEntity {
    private String relationshipType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_order_item_ref_id", referencedColumnName = "id")
    @JsonBackReference
    private ResourceOrderItemRef resourceOrderItemRef;
}
