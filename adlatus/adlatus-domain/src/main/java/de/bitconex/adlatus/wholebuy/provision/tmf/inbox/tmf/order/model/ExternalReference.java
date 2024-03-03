package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfBaseEntity;
import jakarta.persistence.Entity;
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
public class ExternalReference extends TmfBaseEntity {
    @NotNull
    private String name;
    private String externalReferenceType;
}
