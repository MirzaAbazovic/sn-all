package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class ResourceOrderItem extends TmfBaseEntity {
    @NotNull
    private String action;
    private Integer quantity;
    private String state;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_ref_id")
    @JsonManagedReference
    private AppointmentRef appointmentRef;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_specification_id")
    @JsonBackReference
    private ResourceSpecification resourceSpecification;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_order_item_id")
    @JsonBackReference
    private List<ResourceOrderItemRelationship> resourceOrderItemRelationships;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    @JoinColumn(name = "resource_id")
    private Resource resource;

    public void addResourceOrderItemRelationship(ResourceOrderItemRelationship roir) {
        if (this.resourceOrderItemRelationships == null)
            this.resourceOrderItemRelationships = new ArrayList<>();
        this.resourceOrderItemRelationships.add(roir);
    }
}
