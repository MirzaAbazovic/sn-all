package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfBaseEntity;
import jakarta.persistence.Entity;
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
public class FeatureConstraint extends TmfBaseEntity {
    private String name;
    private String version;
    @JsonProperty("@referredType")
    private String atReferredType;
}
