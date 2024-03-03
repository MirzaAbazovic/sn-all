package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;


import javax.annotation.Generated;

/**
 * ResourceCandidate is an entity that makes a resource specification available to a catalog. A ResourceCandidate and its associated resource specification may be published - made visible - in any number of resource catalogs, or in none. Skipped properties: id,href
 */

@Schema(name = "ResourceCandidate_Create", description = "ResourceCandidate is an entity that makes a resource specification available to a catalog. A ResourceCandidate and its associated resource specification may be published - made visible - in any number of resource catalogs, or in none. Skipped properties: id,href")
@JsonTypeName("ResourceCandidate_Create")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceCandidateCreate {

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastUpdate;

    private String lifecycleStatus;

    @NotNull(message = "name cannot be null")
    private String name;

    private String version;

    @Valid
    private List<@Valid ResourceCategoryRef> category;

    private ResourceSpecificationRef resourceSpecification;

    private TimePeriod validFor;

    private String atBaseType;

    private URI atSchemaLocation;

    private String atType;

    /**
     * Default constructor
     *
     * @deprecated Use {@link ResourceCandidateCreate#ResourceCandidateCreate(String)}
     */
    @Deprecated
    public ResourceCandidateCreate() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public ResourceCandidateCreate(String name) {
        this.name = name;
    }

    public ResourceCandidateCreate description(String description) {
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

    public ResourceCandidateCreate lastUpdate(OffsetDateTime lastUpdate) {
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

    public ResourceCandidateCreate lifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
        return this;
    }

    /**
     * Used to indicate the current lifecycle status of the resource candidate.
     *
     * @return lifecycleStatus
     */

    @Schema(name = "lifecycleStatus", description = "Used to indicate the current lifecycle status of the resource candidate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("lifecycleStatus")
    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public ResourceCandidateCreate name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name given to this REST resource
     *
     * @return name
     */
    @NotNull
    @Schema(name = "name", description = "Name given to this REST resource", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceCandidateCreate version(String version) {
        this.version = version;
        return this;
    }

    /**
     * the version of resource candidate
     *
     * @return version
     */

    @Schema(name = "version", description = "the version of resource candidate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ResourceCandidateCreate category(List<@Valid ResourceCategoryRef> category) {
        this.category = category;
        return this;
    }

    public ResourceCandidateCreate addCategoryItem(ResourceCategoryRef categoryItem) {
        if (this.category == null) {
            this.category = new ArrayList<>();
        }
        this.category.add(categoryItem);
        return this;
    }

    /**
     * The categories in which this candidate is exposed
     *
     * @return category
     */
    @Valid
    @Schema(name = "category", description = "The categories in which this candidate is exposed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("category")
    public List<@Valid ResourceCategoryRef> getCategory() {
        return category;
    }

    public void setCategory(List<@Valid ResourceCategoryRef> category) {
        this.category = category;
    }

    public ResourceCandidateCreate resourceSpecification(ResourceSpecificationRef resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
        return this;
    }

    /**
     * Get resourceSpecification
     *
     * @return resourceSpecification
     */
    @Valid
    @Schema(name = "resourceSpecification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("resourceSpecification")
    public ResourceSpecificationRef getResourceSpecification() {
        return resourceSpecification;
    }

    public void setResourceSpecification(ResourceSpecificationRef resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
    }

    public ResourceCandidateCreate validFor(TimePeriod validFor) {
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

    public ResourceCandidateCreate atBaseType(String atBaseType) {
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

    public ResourceCandidateCreate atSchemaLocation(URI atSchemaLocation) {
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

    public ResourceCandidateCreate atType(String atType) {
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
        ResourceCandidateCreate resourceCandidateCreate = (ResourceCandidateCreate) o;
        return Objects.equals(this.description, resourceCandidateCreate.description) &&
                Objects.equals(this.lastUpdate, resourceCandidateCreate.lastUpdate) &&
                Objects.equals(this.lifecycleStatus, resourceCandidateCreate.lifecycleStatus) &&
                Objects.equals(this.name, resourceCandidateCreate.name) &&
                Objects.equals(this.version, resourceCandidateCreate.version) &&
                Objects.equals(this.category, resourceCandidateCreate.category) &&
                Objects.equals(this.resourceSpecification, resourceCandidateCreate.resourceSpecification) &&
                Objects.equals(this.validFor, resourceCandidateCreate.validFor) &&
                Objects.equals(this.atBaseType, resourceCandidateCreate.atBaseType) &&
                Objects.equals(this.atSchemaLocation, resourceCandidateCreate.atSchemaLocation) &&
                Objects.equals(this.atType, resourceCandidateCreate.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, lastUpdate, lifecycleStatus, name, version, category, resourceSpecification, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceCandidateCreate {\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
        sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("    resourceSpecification: ").append(toIndentedString(resourceSpecification)).append("\n");
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

