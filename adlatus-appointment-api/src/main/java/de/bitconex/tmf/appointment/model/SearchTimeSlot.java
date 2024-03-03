package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This task resource is used to retrieve available time slots. One of this available time slot is after used to create or reschedule an appointment
 */

@Schema(name = "SearchTimeSlot", description = "This task resource is used to retrieve available time slots. One of this available time slot is after used to create or reschedule an appointment")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
@Document
public class SearchTimeSlot {

    @Id
    private String id;

    private String href;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime searchDate;

    private String searchResult;

    @Valid
    private List<@Valid TimeSlot> availableTimeSlot;

    @Valid
    private List<@Valid RelatedEntity> relatedEntity;

    private RelatedParty relatedParty;

    private RelatedPlaceRefOrValue relatedPlace;

    @Valid
    private List<@Valid TimeSlot> requestedTimeSlot;

    private SearchTimeSlotStateType status;

    @JsonProperty("@baseTyoe")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public SearchTimeSlot id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the search time slot request
     *
     * @return id
     */

    @Schema(name = "id", description = "Unique identifier of the search time slot request", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SearchTimeSlot href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Reference to access the search time slot resource
     *
     * @return href
     */

    @Schema(name = "href", description = "Reference to access the search time slot resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public SearchTimeSlot searchDate(OffsetDateTime searchDate) {
        this.searchDate = searchDate;
        return this;
    }

    /**
     * Date when the search time slot is performed
     *
     * @return searchDate
     */
    @Valid
    @Schema(name = "searchDate", description = "Date when the search time slot is performed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("searchDate")
    public OffsetDateTime getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(OffsetDateTime searchDate) {
        this.searchDate = searchDate;
    }

    public SearchTimeSlot searchResult(String searchResult) {
        this.searchResult = searchResult;
        return this;
    }

    /**
     * Result of the search time slot (success or fail for example)
     *
     * @return searchResult
     */

    @Schema(name = "searchResult", description = "Result of the search time slot (success or fail for example)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("searchResult")
    public String getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(String searchResult) {
        this.searchResult = searchResult;
    }

    public SearchTimeSlot availableTimeSlot(List<@Valid TimeSlot> availableTimeSlot) {
        this.availableTimeSlot = availableTimeSlot;
        return this;
    }

    public SearchTimeSlot addAvailableTimeSlotItem(TimeSlot availableTimeSlotItem) {
        if (this.availableTimeSlot == null) {
            this.availableTimeSlot = new ArrayList<>();
        }
        this.availableTimeSlot.add(availableTimeSlotItem);
        return this;
    }

    /**
     * Get availableTimeSlot
     *
     * @return availableTimeSlot
     */
    @Valid
    @Schema(name = "availableTimeSlot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("availableTimeSlot")
    public List<@Valid TimeSlot> getAvailableTimeSlot() {
        return availableTimeSlot;
    }

    public void setAvailableTimeSlot(List<@Valid TimeSlot> availableTimeSlot) {
        this.availableTimeSlot = availableTimeSlot;
    }

    public SearchTimeSlot relatedEntity(List<@Valid RelatedEntity> relatedEntity) {
        this.relatedEntity = relatedEntity;
        return this;
    }

    public SearchTimeSlot addRelatedEntityItem(RelatedEntity relatedEntityItem) {
        if (this.relatedEntity == null) {
            this.relatedEntity = new ArrayList<>();
        }
        this.relatedEntity.add(relatedEntityItem);
        return this;
    }

    /**
     * Get relatedEntity
     *
     * @return relatedEntity
     */
    @Valid
    @Schema(name = "relatedEntity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedEntity")
    public List<@Valid RelatedEntity> getRelatedEntity() {
        return relatedEntity;
    }

    public void setRelatedEntity(List<@Valid RelatedEntity> relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    public SearchTimeSlot relatedParty(RelatedParty relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    /**
     * Get relatedParty
     *
     * @return relatedParty
     */
    @Valid
    @Schema(name = "relatedParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedParty")
    public RelatedParty getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(RelatedParty relatedParty) {
        this.relatedParty = relatedParty;
    }

    public SearchTimeSlot relatedPlace(RelatedPlaceRefOrValue relatedPlace) {
        this.relatedPlace = relatedPlace;
        return this;
    }

    /**
     * Get relatedPlace
     *
     * @return relatedPlace
     */
    @Valid
    @Schema(name = "relatedPlace", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedPlace")
    public RelatedPlaceRefOrValue getRelatedPlace() {
        return relatedPlace;
    }

    public void setRelatedPlace(RelatedPlaceRefOrValue relatedPlace) {
        this.relatedPlace = relatedPlace;
    }

    public SearchTimeSlot requestedTimeSlot(List<@Valid TimeSlot> requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
        return this;
    }

    public SearchTimeSlot addRequestedTimeSlotItem(TimeSlot requestedTimeSlotItem) {
        if (this.requestedTimeSlot == null) {
            this.requestedTimeSlot = new ArrayList<>();
        }
        this.requestedTimeSlot.add(requestedTimeSlotItem);
        return this;
    }

    /**
     * Get requestedTimeSlot
     *
     * @return requestedTimeSlot
     */
    @Valid
    @Schema(name = "requestedTimeSlot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("requestedTimeSlot")
    public List<@Valid TimeSlot> getRequestedTimeSlot() {
        return requestedTimeSlot;
    }

    public void setRequestedTimeSlot(List<@Valid TimeSlot> requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
    }

    public SearchTimeSlot status(SearchTimeSlotStateType status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     *
     * @return status
     */
    @Valid
    @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("status")
    public SearchTimeSlotStateType getStatus() {
        return status;
    }

    public void setStatus(SearchTimeSlotStateType status) {
        this.status = status;
    }

    public SearchTimeSlot atBaseType(String atBaseType) {
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

    public SearchTimeSlot atSchemaLocation(URI atSchemaLocation) {
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

    public SearchTimeSlot atType(String atType) {
        this.atType = atType;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class entity name
     *
     * @return atType
     */

    @Schema(name = "@type", description = "When sub-classing, this defines the sub-class entity name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
        SearchTimeSlot searchTimeSlot = (SearchTimeSlot) o;
        return Objects.equals(this.id, searchTimeSlot.id) &&
                Objects.equals(this.href, searchTimeSlot.href) &&
                Objects.equals(this.searchDate, searchTimeSlot.searchDate) &&
                Objects.equals(this.searchResult, searchTimeSlot.searchResult) &&
                Objects.equals(this.availableTimeSlot, searchTimeSlot.availableTimeSlot) &&
                Objects.equals(this.relatedEntity, searchTimeSlot.relatedEntity) &&
                Objects.equals(this.relatedParty, searchTimeSlot.relatedParty) &&
                Objects.equals(this.relatedPlace, searchTimeSlot.relatedPlace) &&
                Objects.equals(this.requestedTimeSlot, searchTimeSlot.requestedTimeSlot) &&
                Objects.equals(this.status, searchTimeSlot.status) &&
                Objects.equals(this.atBaseType, searchTimeSlot.atBaseType) &&
                Objects.equals(this.atSchemaLocation, searchTimeSlot.atSchemaLocation) &&
                Objects.equals(this.atType, searchTimeSlot.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, searchDate, searchResult, availableTimeSlot, relatedEntity, relatedParty, relatedPlace, requestedTimeSlot, status, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SearchTimeSlot {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    searchDate: ").append(toIndentedString(searchDate)).append("\n");
        sb.append("    searchResult: ").append(toIndentedString(searchResult)).append("\n");
        sb.append("    availableTimeSlot: ").append(toIndentedString(availableTimeSlot)).append("\n");
        sb.append("    relatedEntity: ").append(toIndentedString(relatedEntity)).append("\n");
        sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
        sb.append("    relatedPlace: ").append(toIndentedString(relatedPlace)).append("\n");
        sb.append("    requestedTimeSlot: ").append(toIndentedString(requestedTimeSlot)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

