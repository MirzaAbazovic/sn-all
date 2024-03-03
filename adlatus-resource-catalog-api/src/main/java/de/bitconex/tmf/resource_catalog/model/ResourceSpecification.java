package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import de.bitconex.tmf.resource_catalog.utility.CollectionNames;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Resources are physical or non-physical components (or some combination of these) within an enterprise&#39;s infrastructure or inventory. They are typically consumed or used by services (for example a physical port assigned to a service) or contribute to the realization of a Product (for example, a SIM card). They can be drawn from the Application, Computing and Network domains, and include, for example, Network Elements, software, IT systems, content and information, and technology components. A ResourceSpecification is a base class that represents a generic means for implementing a particular type of Resource. In essence, a ResourceSpecification defines the common attributes and relationships of a set of related Resources, while Resource defines a specific instance that is based on a particular ResourceSpecification.
 */

@Schema(name = "ResourceSpecification", description = "Resources are physical or non-physical components (or some combination of these) within an enterprise's infrastructure or inventory. They are typically consumed or used by services (for example a physical port assigned to a service) or contribute to the realization of a Product (for example, a SIM card). They can be drawn from the Application, Computing and Network domains, and include, for example, Network Elements, software, IT systems, content and information, and technology components. A ResourceSpecification is a base class that represents a generic means for implementing a particular type of Resource. In essence, a ResourceSpecification defines the common attributes and relationships of a set of related Resources, while Resource defines a specific instance that is based on a particular ResourceSpecification.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Document(collection = CollectionNames.RESOURCE_SPECIFICATION)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ResourceSpecification {
    @Id
    private String id;

    private URI href;

    private String category;

    private String description;

    private Boolean isBundle;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastUpdate;

    private String lifecycleStatus;

    private String name;

    private String version;

    @Valid
    private List<@Valid AttachmentRefOrValue> attachment;

    @Valid
    private List<@Valid FeatureSpecification> featureSpecification;

    @Valid
    private List<@Valid RelatedParty> relatedParty;

    @Valid
    private List<@Valid ResourceSpecificationCharacteristic> resourceSpecCharacteristic;

    @Valid
    private List<@Valid ResourceSpecificationRelationship> resourceSpecRelationship;

    private TargetResourceSchema targetResourceSchema;

    private TimePeriod validFor;

    private String atBaseType;

    private URI atSchemaLocation;

    private String atType;

    public ResourceSpecification id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of this REST resource
     *
     * @return id
     */

    @Schema(name = "id", example = "6c088cf7f1bc", description = "Unique identifier of this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceSpecification href(URI href) {
        this.href = href;
        return this;
    }

    /**
     * Hyperlink reference to this REST resource
     *
     * @return href
     */
    @Valid
    @Schema(name = "href", example = "http://host/resourceSpecification/6c088cf7f1bc", description = "Hyperlink reference to this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("href")
    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public ResourceSpecification category(String category) {
        this.category = category;
        return this;
    }

    /**
     * Category of the target resource like NetworkConnectivity, PhysicalLinks, Generic, L2Network and so on.
     *
     * @return category
     */

    @Schema(name = "category", description = "Category of the target resource like NetworkConnectivity, PhysicalLinks, Generic, L2Network and so on.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ResourceSpecification description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Description of this REST resource
     *
     * @return description
     */

    @Schema(name = "description", description = "Description of this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceSpecification isBundle(Boolean isBundle) {
        this.isBundle = isBundle;
        return this;
    }

    /**
     * A flag indicates that if this resource specification is a bundled specification (true) or single (false).
     *
     * @return isBundle
     */

    @Schema(name = "isBundle", description = "A flag indicates that if this resource specification is a bundled specification (true) or single (false).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("isBundle")
    public Boolean getIsBundle() {
        return isBundle;
    }

    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    public ResourceSpecification lastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    /**
     * Date and time of the last update of this REST resource
     *
     * @return lastUpdate
     */
    @Valid
    @Schema(name = "lastUpdate", description = "Date and time of the last update of this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("lastUpdate")
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ResourceSpecification lifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
        return this;
    }

    /**
     * Used to indicate the current lifecycle status of the resource specification
     *
     * @return lifecycleStatus
     */

    @Schema(name = "lifecycleStatus", description = "Used to indicate the current lifecycle status of the resource specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("lifecycleStatus")
    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public ResourceSpecification name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name given to this REST resource
     *
     * @return name
     */

    @Schema(name = "name", description = "Name given to this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceSpecification version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Resource Specification version
     *
     * @return version
     */

    @Schema(name = "version", description = "Resource Specification version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ResourceSpecification attachment(List<@Valid AttachmentRefOrValue> attachment) {
        this.attachment = attachment;
        return this;
    }

    public ResourceSpecification addAttachmentItem(AttachmentRefOrValue attachmentItem) {
        if (this.attachment == null) {
            this.attachment = new ArrayList<>();
        }
        this.attachment.add(attachmentItem);
        return this;
    }

    /**
     * Complements the description of an element (for instance a resource) through video, pictures ...
     *
     * @return attachment
     */
    @Valid
    @Schema(name = "attachment", description = "Complements the description of an element (for instance a resource) through video, pictures ...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("attachment")
    public List<@Valid AttachmentRefOrValue> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<@Valid AttachmentRefOrValue> attachment) {
        this.attachment = attachment;
    }

    public ResourceSpecification featureSpecification(List<@Valid FeatureSpecification> featureSpecification) {
        this.featureSpecification = featureSpecification;
        return this;
    }

    public ResourceSpecification addFeatureSpecificationItem(FeatureSpecification featureSpecificationItem) {
        if (this.featureSpecification == null) {
            this.featureSpecification = new ArrayList<>();
        }
        this.featureSpecification.add(featureSpecificationItem);
        return this;
    }

    /**
     * A list of Features for this specification.
     *
     * @return featureSpecification
     */
    @Valid
    @Schema(name = "featureSpecification", description = "A list of Features for this specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("featureSpecification")
    public List<@Valid FeatureSpecification> getFeatureSpecification() {
        return featureSpecification;
    }

    public void setFeatureSpecification(List<@Valid FeatureSpecification> featureSpecification) {
        this.featureSpecification = featureSpecification;
    }

    public ResourceSpecification relatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    public ResourceSpecification addRelatedPartyItem(RelatedParty relatedPartyItem) {
        if (this.relatedParty == null) {
            this.relatedParty = new ArrayList<>();
        }
        this.relatedParty.add(relatedPartyItem);
        return this;
    }

    /**
     * A related party defines party or party role linked to a specific entity.
     *
     * @return relatedParty
     */
    @Valid
    @Schema(name = "relatedParty", description = "A related party defines party or party role linked to a specific entity.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedParty")
    public List<@Valid RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public ResourceSpecification resourceSpecCharacteristic(List<@Valid ResourceSpecificationCharacteristic> resourceSpecCharacteristic) {
        this.resourceSpecCharacteristic = resourceSpecCharacteristic;
        return this;
    }

    public ResourceSpecification addResourceSpecCharacteristicItem(ResourceSpecificationCharacteristic resourceSpecCharacteristicItem) {
        if (this.resourceSpecCharacteristic == null) {
            this.resourceSpecCharacteristic = new ArrayList<>();
        }
        this.resourceSpecCharacteristic.add(resourceSpecCharacteristicItem);
        return this;
    }

    /**
     * A characteristic quality or distinctive feature of a ResourceSpecification.  The characteristic can be take on a discrete value, such as color, can take on a range of values, (for example, sensitivity of 100-240 mV), or can be derived from a formula (for example, usage time (hrs) = 30 - talk time *3). Certain characteristics, such as color, may be configured during the ordering or some other process.
     *
     * @return resourceSpecCharacteristic
     */
    @Valid
    @Schema(name = "resourceSpecCharacteristic", description = "A characteristic quality or distinctive feature of a ResourceSpecification.  The characteristic can be take on a discrete value, such as color, can take on a range of values, (for example, sensitivity of 100-240 mV), or can be derived from a formula (for example, usage time (hrs) = 30 - talk time *3). Certain characteristics, such as color, may be configured during the ordering or some other process.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("resourceSpecCharacteristic")
    public List<@Valid ResourceSpecificationCharacteristic> getResourceSpecCharacteristic() {
        return resourceSpecCharacteristic;
    }

    public void setResourceSpecCharacteristic(List<@Valid ResourceSpecificationCharacteristic> resourceSpecCharacteristic) {
        this.resourceSpecCharacteristic = resourceSpecCharacteristic;
    }

    public ResourceSpecification resourceSpecRelationship(List<@Valid ResourceSpecificationRelationship> resourceSpecRelationship) {
        this.resourceSpecRelationship = resourceSpecRelationship;
        return this;
    }

    public ResourceSpecification addResourceSpecRelationshipItem(ResourceSpecificationRelationship resourceSpecRelationshipItem) {
        if (this.resourceSpecRelationship == null) {
            this.resourceSpecRelationship = new ArrayList<>();
        }
        this.resourceSpecRelationship.add(resourceSpecRelationshipItem);
        return this;
    }

    /**
     * A migration, substitution, dependency or exclusivity relationship between/among resource specifications.
     *
     * @return resourceSpecRelationship
     */
    @Valid
    @Schema(name = "resourceSpecRelationship", description = "A migration, substitution, dependency or exclusivity relationship between/among resource specifications.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("resourceSpecRelationship")
    public List<@Valid ResourceSpecificationRelationship> getResourceSpecRelationship() {
        return resourceSpecRelationship;
    }

    public void setResourceSpecRelationship(List<@Valid ResourceSpecificationRelationship> resourceSpecRelationship) {
        this.resourceSpecRelationship = resourceSpecRelationship;
    }

    public ResourceSpecification targetResourceSchema(TargetResourceSchema targetResourceSchema) {
        this.targetResourceSchema = targetResourceSchema;
        return this;
    }

    /**
     * Get targetResourceSchema
     *
     * @return targetResourceSchema
     */
    @Valid
    @Schema(name = "targetResourceSchema", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("targetResourceSchema")
    public TargetResourceSchema getTargetResourceSchema() {
        return targetResourceSchema;
    }

    public void setTargetResourceSchema(TargetResourceSchema targetResourceSchema) {
        this.targetResourceSchema = targetResourceSchema;
    }

    public ResourceSpecification validFor(TimePeriod validFor) {
        this.validFor = validFor;
        return this;
    }

    /**
     * Get validFor
     *
     * @return validFor
     */
    @Valid
    @Schema(name = "validFor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("validFor")
    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public ResourceSpecification atBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
        return this;
    }

    /**
     * When sub-classing, this defines the super-class
     *
     * @return atBaseType
     */

    @Schema(name = "@baseType", description = "When sub-classing, this defines the super-class", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("@baseType")
    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }

    public ResourceSpecification atSchemaLocation(URI atSchemaLocation) {
        this.atSchemaLocation = atSchemaLocation;
        return this;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     *
     * @return atSchemaLocation
     */
    @Valid
    @Schema(name = "@schemaLocation", description = "A URI to a JSON-Schema file that defines additional attributes and relationships", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("@schemaLocation")
    public URI getAtSchemaLocation() {
        return atSchemaLocation;
    }

    public void setAtSchemaLocation(URI atSchemaLocation) {
        this.atSchemaLocation = atSchemaLocation;
    }

    public ResourceSpecification atType(String atType) {
        this.atType = atType;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class Extensible name
     *
     * @return atType
     */

    @Schema(name = "@type", description = "When sub-classing, this defines the sub-class Extensible name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("@type")
    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceSpecification resourceSpecification = (ResourceSpecification) o;
        return Objects.equals(this.id, resourceSpecification.id) &&
                Objects.equals(this.href, resourceSpecification.href) &&
                Objects.equals(this.category, resourceSpecification.category) &&
                Objects.equals(this.description, resourceSpecification.description) &&
                Objects.equals(this.isBundle, resourceSpecification.isBundle) &&
                Objects.equals(this.lastUpdate, resourceSpecification.lastUpdate) &&
                Objects.equals(this.lifecycleStatus, resourceSpecification.lifecycleStatus) &&
                Objects.equals(this.name, resourceSpecification.name) &&
                Objects.equals(this.version, resourceSpecification.version) &&
                Objects.equals(this.attachment, resourceSpecification.attachment) &&
                Objects.equals(this.featureSpecification, resourceSpecification.featureSpecification) &&
                Objects.equals(this.relatedParty, resourceSpecification.relatedParty) &&
                Objects.equals(this.resourceSpecCharacteristic, resourceSpecification.resourceSpecCharacteristic) &&
                Objects.equals(this.resourceSpecRelationship, resourceSpecification.resourceSpecRelationship) &&
                Objects.equals(this.targetResourceSchema, resourceSpecification.targetResourceSchema) &&
                Objects.equals(this.validFor, resourceSpecification.validFor) &&
                Objects.equals(this.atBaseType, resourceSpecification.atBaseType) &&
                Objects.equals(this.atSchemaLocation, resourceSpecification.atSchemaLocation) &&
                Objects.equals(this.atType, resourceSpecification.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, category, description, isBundle, lastUpdate, lifecycleStatus, name, version, attachment, featureSpecification, relatedParty, resourceSpecCharacteristic, resourceSpecRelationship, targetResourceSchema, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceSpecification {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    isBundle: ").append(toIndentedString(isBundle)).append("\n");
        sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
        sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    attachment: ").append(toIndentedString(attachment)).append("\n");
        sb.append("    featureSpecification: ").append(toIndentedString(featureSpecification)).append("\n");
        sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
        sb.append("    resourceSpecCharacteristic: ").append(toIndentedString(resourceSpecCharacteristic)).append("\n");
        sb.append("    resourceSpecRelationship: ").append(toIndentedString(resourceSpecRelationship)).append("\n");
        sb.append("    targetResourceSchema: ").append(toIndentedString(targetResourceSchema)).append("\n");
        sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

