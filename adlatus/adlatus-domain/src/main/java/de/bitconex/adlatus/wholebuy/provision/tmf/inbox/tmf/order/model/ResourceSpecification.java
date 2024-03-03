package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ResourceSpecification extends TmfBaseEntity {
    private String name;
    private String version;
    @JsonProperty("@referredType")
    private String atReferredType;

    @OneToOne(mappedBy = "resourceSpecification", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Resource resource;

    @OneToOne(mappedBy = "resourceSpecification", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ResourceOrderItem resourceOrderItem;
}
