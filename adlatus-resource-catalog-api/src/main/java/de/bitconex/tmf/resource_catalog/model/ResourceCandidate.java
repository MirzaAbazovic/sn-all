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
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.format.annotation.DateTimeFormat;


import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * ResourceCandidate is an entity that makes a resource specification available to a catalog. A ResourceCandidate and its associated resource specification may be published - made visible - in any number of resource catalogs, or in none.
 */

@Schema(name = "ResourceCandidate", description = "ResourceCandidate is an entity that makes a resource specification available to a catalog. A ResourceCandidate and its associated resource specification may be published - made visible - in any number of resource catalogs, or in none.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Document(collection = CollectionNames.RESOURCE_CANDIDATE)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ResourceCandidate {
    @Id
    private String id;

    public URI href;

    public String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastUpdate;

    private String lifecycleStatus;

    private String name;

    private String version;

    @Valid
    @DocumentReference(collection = CollectionNames.RESOURCE_CATEGORY)
    private List<@Valid ResourceCategoryRef> category;

    @DocumentReference(collection = CollectionNames.RESOURCE_SPECIFICATION)
    private ResourceSpecificationRef resourceSpecification;

    private TimePeriod validFor;

    private String atBaseType;

    private URI atSchemaLocation;

    private String atType;

    public ResourceCandidate id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of this REST resource
     *
     * @return id
     */

    @Schema(name = "id", description = "Unique identifier of this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceCandidate href(URI href) {
        this.href = href;
        return this;
    }

    /**
     * Hyperlink reference to this REST resource
     *
     * @return href
     */
    @Valid
    @Schema(name = "href", description = "Hyperlink reference to this REST resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("href")
    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public ResourceCandidate description(String description) {
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

    public ResourceCandidate lastUpdate(OffsetDateTime lastUpdate) {
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

    public ResourceCandidate lifecycleStatus(String lifecycleStatus) {
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

    public ResourceCandidate name(String name) {
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

    public ResourceCandidate version(String version) {
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

    public ResourceCandidate category(List<@Valid ResourceCategoryRef> category) {
        this.category = category;
        return this;
    }

    public ResourceCandidate addCategoryItem(ResourceCategoryRef categoryItem) {
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

    public ResourceCandidate resourceSpecification(ResourceSpecificationRef resourceSpecification) {
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

    public ResourceCandidate validFor(TimePeriod validFor) {
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

    public ResourceCandidate atBaseType(String atBaseType) {
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

    public ResourceCandidate atSchemaLocation(URI atSchemaLocation) {
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

    public ResourceCandidate atType(String atType) {
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
        ResourceCandidate resourceCandidate = (ResourceCandidate) o;
        return Objects.equals(this.id, resourceCandidate.id) &&
                Objects.equals(this.href, resourceCandidate.href) &&
                Objects.equals(this.description, resourceCandidate.description) &&
                Objects.equals(this.lastUpdate, resourceCandidate.lastUpdate) &&
                Objects.equals(this.lifecycleStatus, resourceCandidate.lifecycleStatus) &&
                Objects.equals(this.name, resourceCandidate.name) &&
                Objects.equals(this.version, resourceCandidate.version) &&
                Objects.equals(this.category, resourceCandidate.category) &&
                Objects.equals(this.resourceSpecification, resourceCandidate.resourceSpecification) &&
                Objects.equals(this.validFor, resourceCandidate.validFor) &&
                Objects.equals(this.atBaseType, resourceCandidate.atBaseType) &&
                Objects.equals(this.atSchemaLocation, resourceCandidate.atSchemaLocation) &&
                Objects.equals(this.atType, resourceCandidate.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, description, lastUpdate, lifecycleStatus, name, version, category, resourceSpecification, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceCandidate {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
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

