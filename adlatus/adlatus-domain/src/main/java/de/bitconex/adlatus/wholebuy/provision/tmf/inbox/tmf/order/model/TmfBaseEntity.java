package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder(toBuilder = true)
public class TmfBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    private String id;
    private String href;
    @JsonProperty("@baseType")
    private String atBaseType;
    @JsonProperty("@schemaLocation")
    private String atSchemaLocation;
    @JsonProperty("@type")
    private String atType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TmfBaseEntity that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

