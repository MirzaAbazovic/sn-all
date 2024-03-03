package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This task resource is used to retrieve available time slots. One of this available time slot is after used to create or reschedule an appointment Skipped properties: id,href,status,searchDate,searchResult,availableTimeSlot
 */

@Schema(name = "SearchTimeSlot_Create", description = "This task resource is used to retrieve available time slots. One of this available time slot is after used to create or reschedule an appointment Skipped properties: id,href,status,searchDate,searchResult,availableTimeSlot")
@JsonTypeName("SearchTimeSlot_Create")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public class SearchTimeSlotCreate {

  @Valid
  private List<@Valid RelatedEntity> relatedEntity;

  private RelatedParty relatedParty;

  private RelatedPlaceRefOrValue relatedPlace;

  @Valid
  private List<@Valid TimeSlot> requestedTimeSlot;

  @JsonProperty("@baseType")
  private String atBaseType;

  @JsonProperty("@schemaLocation")
  private URI atSchemaLocation;

  @JsonProperty("@type")
  private String atType;

  public SearchTimeSlotCreate relatedEntity(List<@Valid RelatedEntity> relatedEntity) {
    this.relatedEntity = relatedEntity;
    return this;
  }

  public SearchTimeSlotCreate addRelatedEntityItem(RelatedEntity relatedEntityItem) {
    if (this.relatedEntity == null) {
      this.relatedEntity = new ArrayList<>();
    }
    this.relatedEntity.add(relatedEntityItem);
    return this;
  }

  /**
   * Get relatedEntity
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

  public SearchTimeSlotCreate relatedParty(RelatedParty relatedParty) {
    this.relatedParty = relatedParty;
    return this;
  }

  /**
   * Get relatedParty
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

  public SearchTimeSlotCreate relatedPlace(RelatedPlaceRefOrValue relatedPlace) {
    this.relatedPlace = relatedPlace;
    return this;
  }

  /**
   * Get relatedPlace
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

  public SearchTimeSlotCreate requestedTimeSlot(List<@Valid TimeSlot> requestedTimeSlot) {
    this.requestedTimeSlot = requestedTimeSlot;
    return this;
  }

  public SearchTimeSlotCreate addRequestedTimeSlotItem(TimeSlot requestedTimeSlotItem) {
    if (this.requestedTimeSlot == null) {
      this.requestedTimeSlot = new ArrayList<>();
    }
    this.requestedTimeSlot.add(requestedTimeSlotItem);
    return this;
  }

  /**
   * Get requestedTimeSlot
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

  public SearchTimeSlotCreate atBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
    return this;
  }

  /**
   * When sub-classing, this defines the super-class
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

  public SearchTimeSlotCreate atSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
    return this;
  }

  /**
   * A URI to a JSON-Schema file that defines additional attributes and relationships
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

  public SearchTimeSlotCreate atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * When sub-classing, this defines the sub-class entity name
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
    SearchTimeSlotCreate searchTimeSlotCreate = (SearchTimeSlotCreate) o;
    return Objects.equals(this.relatedEntity, searchTimeSlotCreate.relatedEntity) &&
        Objects.equals(this.relatedParty, searchTimeSlotCreate.relatedParty) &&
        Objects.equals(this.relatedPlace, searchTimeSlotCreate.relatedPlace) &&
        Objects.equals(this.requestedTimeSlot, searchTimeSlotCreate.requestedTimeSlot) &&
        Objects.equals(this.atBaseType, searchTimeSlotCreate.atBaseType) &&
        Objects.equals(this.atSchemaLocation, searchTimeSlotCreate.atSchemaLocation) &&
        Objects.equals(this.atType, searchTimeSlotCreate.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relatedEntity, relatedParty, relatedPlace, requestedTimeSlot, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchTimeSlotCreate {\n");
    sb.append("    relatedEntity: ").append(toIndentedString(relatedEntity)).append("\n");
    sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
    sb.append("    relatedPlace: ").append(toIndentedString(relatedPlace)).append("\n");
    sb.append("    requestedTimeSlot: ").append(toIndentedString(requestedTimeSlot)).append("\n");
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

