package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An Appointment is an arrangement to do something or meet someone at a particular time, at a place (for face to face appointment) or in a contact medium (for phone appointment). Skipped properties: id,href,creationDate
 */

@Schema(name = "Appointment_Update", description = "An Appointment is an arrangement to do something or meet someone at a particular time, at a place (for face to face appointment) or in a contact medium (for phone appointment). Skipped properties: id,href,creationDate")
@JsonTypeName("Appointment_Update")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public class AppointmentUpdate {

  private String category;

  private String description;

  private String externalId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastUpdate;

  @Valid
  private List<@Valid AttachmentRefOrValue> attachment;

  private CalendarEventRef calendarEvent;

  @Valid
  private List<@Valid ContactMedium> contactMedium;

  @Valid
  private List<@Valid Note> note;

  @Valid
  private List<@Valid RelatedEntity> relatedEntity;

  @Valid
  private List<@Valid RelatedParty> relatedParty;

  private RelatedPlaceRefOrValue relatedPlace;

  private AppointmentStateType status;

  private TimePeriod validFor;

  @JsonProperty("@baseType")
  private String atBaseType;

  @JsonProperty("@schemaLocation")
  private URI atSchemaLocation;

  @JsonProperty("@type")
  private String atType;

  /**
   * Default constructor
   * @deprecated Use {@link AppointmentUpdate#AppointmentUpdate(TimePeriod)}
   */
  @Deprecated
  public AppointmentUpdate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AppointmentUpdate(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public AppointmentUpdate category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Business category : intervention for example or to be more precise after SalesIntervention, orderDeliveryIntervention,...
   * @return category
  */
  
  @Schema(name = "category", description = "Business category : intervention for example or to be more precise after SalesIntervention, orderDeliveryIntervention,...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public AppointmentUpdate description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Short free text describing the appointment
   * @return description
  */
  
  @Schema(name = "description", description = "Short free text describing the appointment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AppointmentUpdate externalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

  /**
   * External reference known by the customer
   * @return externalId
  */
  
  @Schema(name = "externalId", description = "External reference known by the customer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalId")
  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public AppointmentUpdate lastUpdate(OffsetDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  /**
   * Date of last appointment update
   * @return lastUpdate
  */
  @Valid 
  @Schema(name = "lastUpdate", description = "Date of last appointment update", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastUpdate")
  public OffsetDateTime getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(OffsetDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public AppointmentUpdate attachment(List<@Valid AttachmentRefOrValue> attachment) {
    this.attachment = attachment;
    return this;
  }

  public AppointmentUpdate addAttachmentItem(AttachmentRefOrValue attachmentItem) {
    if (this.attachment == null) {
      this.attachment = new ArrayList<>();
    }
    this.attachment.add(attachmentItem);
    return this;
  }

  /**
   * Get attachment
   * @return attachment
  */
  @Valid 
  @Schema(name = "attachment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("attachment")
  public List<@Valid AttachmentRefOrValue> getAttachment() {
    return attachment;
  }

  public void setAttachment(List<@Valid AttachmentRefOrValue> attachment) {
    this.attachment = attachment;
  }

  public AppointmentUpdate calendarEvent(CalendarEventRef calendarEvent) {
    this.calendarEvent = calendarEvent;
    return this;
  }

  /**
   * Get calendarEvent
   * @return calendarEvent
  */
  @Valid 
  @Schema(name = "calendarEvent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("calendarEvent")
  public CalendarEventRef getCalendarEvent() {
    return calendarEvent;
  }

  public void setCalendarEvent(CalendarEventRef calendarEvent) {
    this.calendarEvent = calendarEvent;
  }

  public AppointmentUpdate contactMedium(List<@Valid ContactMedium> contactMedium) {
    this.contactMedium = contactMedium;
    return this;
  }

  public AppointmentUpdate addContactMediumItem(ContactMedium contactMediumItem) {
    if (this.contactMedium == null) {
      this.contactMedium = new ArrayList<>();
    }
    this.contactMedium.add(contactMediumItem);
    return this;
  }

  /**
   * Get contactMedium
   * @return contactMedium
  */
  @Valid 
  @Schema(name = "contactMedium", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactMedium")
  public List<@Valid ContactMedium> getContactMedium() {
    return contactMedium;
  }

  public void setContactMedium(List<@Valid ContactMedium> contactMedium) {
    this.contactMedium = contactMedium;
  }

  public AppointmentUpdate note(List<@Valid Note> note) {
    this.note = note;
    return this;
  }

  public AppointmentUpdate addNoteItem(Note noteItem) {
    if (this.note == null) {
      this.note = new ArrayList<>();
    }
    this.note.add(noteItem);
    return this;
  }

  /**
   * Get note
   * @return note
  */
  @Valid 
  @Schema(name = "note", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("note")
  public List<@Valid Note> getNote() {
    return note;
  }

  public void setNote(List<@Valid Note> note) {
    this.note = note;
  }

  public AppointmentUpdate relatedEntity(List<@Valid RelatedEntity> relatedEntity) {
    this.relatedEntity = relatedEntity;
    return this;
  }

  public AppointmentUpdate addRelatedEntityItem(RelatedEntity relatedEntityItem) {
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

  public AppointmentUpdate relatedParty(List<@Valid RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
    return this;
  }

  public AppointmentUpdate addRelatedPartyItem(RelatedParty relatedPartyItem) {
    if (this.relatedParty == null) {
      this.relatedParty = new ArrayList<>();
    }
    this.relatedParty.add(relatedPartyItem);
    return this;
  }

  /**
   * Get relatedParty
   * @return relatedParty
  */
  @Valid 
  @Schema(name = "relatedParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("relatedParty")
  public List<@Valid RelatedParty> getRelatedParty() {
    return relatedParty;
  }

  public void setRelatedParty(List<@Valid RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
  }

  public AppointmentUpdate relatedPlace(RelatedPlaceRefOrValue relatedPlace) {
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

  public AppointmentUpdate status(AppointmentStateType status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public AppointmentStateType getStatus() {
    return status;
  }

  public void setStatus(AppointmentStateType status) {
    this.status = status;
  }

  public AppointmentUpdate validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * Get validFor
   * @return validFor
  */
  @NotNull @Valid 
  @Schema(name = "validFor", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("validFor")
  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public AppointmentUpdate atBaseType(String atBaseType) {
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

  public AppointmentUpdate atSchemaLocation(URI atSchemaLocation) {
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

  public AppointmentUpdate atType(String atType) {
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
    AppointmentUpdate appointmentUpdate = (AppointmentUpdate) o;
    return Objects.equals(this.category, appointmentUpdate.category) &&
        Objects.equals(this.description, appointmentUpdate.description) &&
        Objects.equals(this.externalId, appointmentUpdate.externalId) &&
        Objects.equals(this.lastUpdate, appointmentUpdate.lastUpdate) &&
        Objects.equals(this.attachment, appointmentUpdate.attachment) &&
        Objects.equals(this.calendarEvent, appointmentUpdate.calendarEvent) &&
        Objects.equals(this.contactMedium, appointmentUpdate.contactMedium) &&
        Objects.equals(this.note, appointmentUpdate.note) &&
        Objects.equals(this.relatedEntity, appointmentUpdate.relatedEntity) &&
        Objects.equals(this.relatedParty, appointmentUpdate.relatedParty) &&
        Objects.equals(this.relatedPlace, appointmentUpdate.relatedPlace) &&
        Objects.equals(this.status, appointmentUpdate.status) &&
        Objects.equals(this.validFor, appointmentUpdate.validFor) &&
        Objects.equals(this.atBaseType, appointmentUpdate.atBaseType) &&
        Objects.equals(this.atSchemaLocation, appointmentUpdate.atSchemaLocation) &&
        Objects.equals(this.atType, appointmentUpdate.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, description, externalId, lastUpdate, attachment, calendarEvent, contactMedium, note, relatedEntity, relatedParty, relatedPlace, status, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppointmentUpdate {\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    externalId: ").append(toIndentedString(externalId)).append("\n");
    sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
    sb.append("    attachment: ").append(toIndentedString(attachment)).append("\n");
    sb.append("    calendarEvent: ").append(toIndentedString(calendarEvent)).append("\n");
    sb.append("    contactMedium: ").append(toIndentedString(contactMedium)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    relatedEntity: ").append(toIndentedString(relatedEntity)).append("\n");
    sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
    sb.append("    relatedPlace: ").append(toIndentedString(relatedPlace)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

