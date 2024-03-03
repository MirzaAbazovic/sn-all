package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
public class ResourceAttachment extends TmfBaseEntity {
    private String attachmentType;
    private String content;
    private String description;
    private String mimeType;
    private String name;
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(insertable = false, updatable = false, name = "size"))
    @AttributeOverride(name = "units", column = @Column(insertable = false, updatable = false, name = "size"))
    private Quantity size;
    private String url;
    @Embedded
    @AttributeOverride(name = "startDateTime", column = @Column(insertable = false, updatable = false, name = "validFor"))
    @AttributeOverride(name = "endDateTime", column = @Column(insertable = false, updatable = false, name = "validFor"))
    private TimePeriod validFor;
    @NotNull
    @JsonProperty("@referredType")
    private String atReferredType;
}
