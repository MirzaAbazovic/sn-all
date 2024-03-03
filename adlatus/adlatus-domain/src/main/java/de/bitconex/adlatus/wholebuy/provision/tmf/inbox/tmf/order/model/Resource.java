package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
public class Resource extends TmfBaseEntity {
    private String administrativeState;
    private String category;
    private String description;
    private OffsetDateTime endOperatingDate;
    private String name;
    private String operationalState;
    private String resourceStatus;
    private String resourceVersion;
    private OffsetDateTime startOperatingDate;
    private String usageState;

    @JsonProperty("@referredType")
    private String atReferredType;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_specification_id", referencedColumnName = "id")
    @JsonBackReference
    private ResourceSpecification resourceSpecification;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @JsonBackReference
    private List<RelatedParty> relatedParties;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @JsonBackReference
    private List<ResourceRelationship> resourceRelationships;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @JsonBackReference
    private List<ResourceCharacteristic> resourceCharacteristic;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @JsonBackReference
    private List<ResourceFeature> resourceFeatures;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_place_id", referencedColumnName = "id")
    @JsonBackReference
    private RelatedPlace relatedPlace;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @JsonBackReference
    private List<ResourceAttachment> attachments;

    public void addRelatedParty(RelatedParty relatedParty) {
        if (this.relatedParties == null)
            this.relatedParties = new ArrayList<>();
        this.relatedParties.add(relatedParty);
    }

    public void addResourceRelationship(ResourceRelationship resourceRelationship) {
        if (this.resourceRelationships == null)
            this.resourceRelationships = new ArrayList<>();
        this.resourceRelationships.add(resourceRelationship);
    }

    public void addResourceCharacteristic(ResourceCharacteristic resourceCharacteristic) {
        if (this.resourceCharacteristic == null)
            this.resourceCharacteristic = new ArrayList<>();
        this.resourceCharacteristic.add(resourceCharacteristic);
    }

    public void addResourceFeature(ResourceFeature resourceFeature) {
        if (this.resourceFeatures == null)
            this.resourceFeatures = new ArrayList<>();
        this.resourceFeatures.add(resourceFeature);
    }

    public void addAttachment(ResourceAttachment resourceAttachment) {
        if (this.attachments == null)
            this.attachments = new ArrayList<>();
        this.attachments.add(resourceAttachment);
    }
}

