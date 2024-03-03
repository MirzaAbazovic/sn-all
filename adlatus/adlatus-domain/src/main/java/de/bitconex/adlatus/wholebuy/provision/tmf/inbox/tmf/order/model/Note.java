package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Note extends TmfBaseEntity {
    private String author;
    private OffsetDateTime date;
    @NotNull
    private String text;
    @JsonProperty("@referredType")
    private String atReferredType;
}
