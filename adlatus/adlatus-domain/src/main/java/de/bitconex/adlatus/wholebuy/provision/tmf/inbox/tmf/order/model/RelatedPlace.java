package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
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
public class RelatedPlace extends TmfBaseEntity {
    private String name;

    @NotNull
    private String role;

    @NotNull
    @JsonProperty("@referredType")
    private String atReferredType;

    @OneToOne(mappedBy = "relatedPlace", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Resource resource;
}
