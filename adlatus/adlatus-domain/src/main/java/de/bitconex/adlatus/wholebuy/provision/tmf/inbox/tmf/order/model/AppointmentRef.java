package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
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
public class AppointmentRef extends TmfBaseEntity {
    private String description;
    @JsonProperty("@referredType")
    private String atReferredType;

    @OneToOne(mappedBy = "appointmentRef", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ResourceOrderItem resourceOrderItem;
}
