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
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The (resource) category resource is used to group resource candidates in logical containers. Categories can contain other categories.
 */

@Schema(name = "ResourceCategory", description = "The (resource) category resource is used to group resource candidates in logical containers. Categories can contain other categories.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Document(collection = CollectionNames.RESOURCE_CATEGORY)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ResourceCategory {

    @Id
    private String id;

    private URI href;

    private String description;

    private Boolean isRoot;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastUpdate;

    private String lifecycleStatus;

    private String name;

    private String parentId;

    private String version;

    @Valid
    @DocumentReference(collection = CollectionNames.RESOURCE_CATEGORY)
    private List<@Valid ResourceCategoryRef> category;

    @Valid
    private List<@Valid RelatedParty> relatedParty;

    @Valid
    @DocumentReference(collection = CollectionNames.RESOURCE_CANDIDATE)
    private List<@Valid ResourceCandidateRef> resourceCandidate;

    private TimePeriod validFor;

    private String atBaseType;

    private URI atSchemaLocation;

    private String atType;

    public ResourceCategory id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the category
     *
     * @return id
     */

    @Schema(name = "id", description = "Unique identifier of the category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceCategory href(URI href) {
        this.href = href;
        return this;
    }

    /**
     * Hyperlink reference to the category
     *
     * @return href
     */
    @Valid
    @Schema(name = "href", description = "Hyperlink reference to the category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("href")
    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public ResourceCategory description(String description) {
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

    public ResourceCategory isRoot(Boolean isRoot) {
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

    public ResourceCategory lastUpdate(OffsetDateTime lastUpdate) {
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

    public ResourceCategory lifecycleStatus(String lifecycleStatus) {
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

    public ResourceCategory name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the category
     *
     * @return name
     */

    @Schema(name = "name", description = "Name of the category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceCategory parentId(String parentId) {
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

    public ResourceCategory version(String version) {
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

    public ResourceCategory category(List<@Valid ResourceCategoryRef> category) {
        this.category = category;
        return this;
    }

    public ResourceCategory addCategoryItem(ResourceCategoryRef categoryItem) {
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

    public ResourceCategory relatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    public ResourceCategory addRelatedPartyItem(RelatedParty relatedPartyItem) {
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

    public ResourceCategory resourceCandidate(List<@Valid ResourceCandidateRef> resourceCandidate) {
        this.resourceCandidate = resourceCandidate;
        return this;
    }

    public ResourceCategory addResourceCandidateItem(ResourceCandidateRef resourceCandidateItem) {
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

    public ResourceCategory validFor(TimePeriod validFor) {
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

    public ResourceCategory atBaseType(String atBaseType) {
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

    public ResourceCategory atSchemaLocation(URI atSchemaLocation) {
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

    public ResourceCategory atType(String atType) {
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
        ResourceCategory resourceCategory = (ResourceCategory) o;
        return Objects.equals(this.id, resourceCategory.id) &&
                Objects.equals(this.href, resourceCategory.href) &&
                Objects.equals(this.description, resourceCategory.description) &&
                Objects.equals(this.isRoot, resourceCategory.isRoot) &&
                Objects.equals(this.lastUpdate, resourceCategory.lastUpdate) &&
                Objects.equals(this.lifecycleStatus, resourceCategory.lifecycleStatus) &&
                Objects.equals(this.name, resourceCategory.name) &&
                Objects.equals(this.parentId, resourceCategory.parentId) &&
                Objects.equals(this.version, resourceCategory.version) &&
                Objects.equals(this.category, resourceCategory.category) &&
                Objects.equals(this.relatedParty, resourceCategory.relatedParty) &&
                Objects.equals(this.resourceCandidate, resourceCategory.resourceCandidate) &&
                Objects.equals(this.validFor, resourceCategory.validFor) &&
                Objects.equals(this.atBaseType, resourceCategory.atBaseType) &&
                Objects.equals(this.atSchemaLocation, resourceCategory.atSchemaLocation) &&
                Objects.equals(this.atType, resourceCategory.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, description, isRoot, lastUpdate, lifecycleStatus, name, parentId, version, category, relatedParty, resourceCandidate, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceCategory {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
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

