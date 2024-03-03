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

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The (resource) category resource is used to group resource candidates in logical containers. Categories can contain other categories. Skipped properties: id,href
 */

@Schema(name = "ResourceCategory_Create", description = "The (resource) category resource is used to group resource candidates in logical containers. Categories can contain other categories. Skipped properties: id,href")
@JsonTypeName("ResourceCategory_Create")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceCategoryCreate {

    private String description;

    private Boolean isRoot;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastUpdate;

    private String lifecycleStatus;


    private String name;

    private String parentId;

    private String version;

    @Valid
    private List<@Valid ResourceCategoryRef> category;

    @Valid
    private List<@Valid RelatedParty> relatedParty;

    @Valid
    private List<@Valid ResourceCandidateRef> resourceCandidate;

    private TimePeriod validFor;

    private String atBaseType;

    private URI atSchemaLocation;

    private String atType;

    /**
     * Default constructor
     *
     * @deprecated Use {@link ResourceCategoryCreate#ResourceCategoryCreate(String)}
     */
    @Deprecated
    public ResourceCategoryCreate() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public ResourceCategoryCreate(String name) {
        this.name = name;
    }

    public ResourceCategoryCreate description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Description of the category
     *
     * @return description
     */

    @Schema(name = "description", description = "Description of the category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceCategoryCreate isRoot(Boolean isRoot) {
        this.isRoot = isRoot;
        return this;
    }

    /**
     * If true, this Boolean indicates that the category is a root of categories
     *
     * @return isRoot
     */

    @Schema(name = "isRoot", description = "If true, this Boolean indicates that the category is a root of categories", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("isRoot")
    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean isRoot) {
        this.isRoot = isRoot;
    }

    public ResourceCategoryCreate lastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    /**
     * Date and time of the last update
     *
     * @return lastUpdate
     */
    @Valid
    @Schema(name = "lastUpdate", description = "Date and time of the last update", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("lastUpdate")
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ResourceCategoryCreate lifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
        return this;
    }

    /**
     * Used to indicate the current lifecycle status
     *
     * @return lifecycleStatus
     */

    @Schema(name = "lifecycleStatus", description = "Used to indicate the current lifecycle status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("lifecycleStatus")
    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public ResourceCategoryCreate name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the category
     *
     * @return name
     */
    @NotNull
    @Schema(name = "name", description = "Name of the category", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceCategoryCreate parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * Unique identifier of the parent category
     *
     * @return parentId
     */

    @Schema(name = "parentId", description = "Unique identifier of the parent category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("parentId")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ResourceCategoryCreate version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Category version
     *
     * @return version
     */

    @Schema(name = "version", description = "Category version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ResourceCategoryCreate category(List<@Valid ResourceCategoryRef> category) {
        this.category = category;
        return this;
    }

    public ResourceCategoryCreate addCategoryItem(ResourceCategoryRef categoryItem) {
        if (this.category == null) {
            this.category = new ArrayList<>();
        }
        this.category.add(categoryItem);
        return this;
    }

    /**
     * The category resource is used to group product offerings, service and resource candidates in logical containers. Categories can contain other (sub-)categories and/or product offerings.
     *
     * @return category
     */
    @Valid
    @Schema(name = "category", description = "The category resource is used to group product offerings, service and resource candidates in logical containers. Categories can contain other (sub-)categories and/or product offerings.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("category")
    public List<@Valid ResourceCategoryRef> getCategory() {
        return category;
    }

    public void setCategory(List<@Valid ResourceCategoryRef> category) {
        this.category = category;
    }

    public ResourceCategoryCreate relatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    public ResourceCategoryCreate addRelatedPartyItem(RelatedParty relatedPartyItem) {
        if (this.relatedParty == null) {
            this.relatedParty = new ArrayList<>();
        }
        this.relatedParty.add(relatedPartyItem);
        return this;
    }

    /**
     * List of parties involved in this category
     *
     * @return relatedParty
     */
    @Valid
    @Schema(name = "relatedParty", description = "List of parties involved in this category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedParty")
    public List<@Valid RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public ResourceCategoryCreate resourceCandidate(List<@Valid ResourceCandidateRef> resourceCandidate) {
        this.resourceCandidate = resourceCandidate;
        return this;
    }

    public ResourceCategoryCreate addResourceCandidateItem(ResourceCandidateRef resourceCandidateItem) {
        if (this.resourceCandidate == null) {
            this.resourceCandidate = new ArrayList<>();
        }
        this.resourceCandidate.add(resourceCandidateItem);
        return this;
    }

    /**
     * List of resource candidates accessible via this category
     *
     * @return resourceCandidate
     */
    @Valid
    @Schema(name = "resourceCandidate", description = "List of resource candidates accessible via this category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("resourceCandidate")
    public List<@Valid ResourceCandidateRef> getResourceCandidate() {
        return resourceCandidate;
    }

    public void setResourceCandidate(List<@Valid ResourceCandidateRef> resourceCandidate) {
        this.resourceCandidate = resourceCandidate;
    }

    public ResourceCategoryCreate validFor(TimePeriod validFor) {
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

    public ResourceCategoryCreate atBaseType(String atBaseType) {
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

    public ResourceCategoryCreate atSchemaLocation(URI atSchemaLocation) {
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

    public ResourceCategoryCreate atType(String atType) {
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
        ResourceCategoryCreate resourceCategoryCreate = (ResourceCategoryCreate) o;
        return Objects.equals(this.description, resourceCategoryCreate.description) &&
                Objects.equals(this.isRoot, resourceCategoryCreate.isRoot) &&
                Objects.equals(this.lastUpdate, resourceCategoryCreate.lastUpdate) &&
                Objects.equals(this.lifecycleStatus, resourceCategoryCreate.lifecycleStatus) &&
                Objects.equals(this.name, resourceCategoryCreate.name) &&
                Objects.equals(this.parentId, resourceCategoryCreate.parentId) &&
                Objects.equals(this.version, resourceCategoryCreate.version) &&
                Objects.equals(this.category, resourceCategoryCreate.category) &&
                Objects.equals(this.relatedParty, resourceCategoryCreate.relatedParty) &&
                Objects.equals(this.resourceCandidate, resourceCategoryCreate.resourceCandidate) &&
                Objects.equals(this.validFor, resourceCategoryCreate.validFor) &&
                Objects.equals(this.atBaseType, resourceCategoryCreate.atBaseType) &&
                Objects.equals(this.atSchemaLocation, resourceCategoryCreate.atSchemaLocation) &&
                Objects.equals(this.atType, resourceCategoryCreate.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, isRoot, lastUpdate, lifecycleStatus, name, parentId, version, category, relatedParty, resourceCandidate, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceCategoryCreate {\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    isRoot: ").append(toIndentedString(isRoot)).append("\n");
        sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
        sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
        sb.append("    resourceCandidate: ").append(toIndentedString(resourceCandidate)).append("\n");
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

