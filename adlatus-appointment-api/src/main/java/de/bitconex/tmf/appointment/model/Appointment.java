package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
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
 * An Appointment is an arrangement to do something or meet someone at a particular time, at a place (for face to face appointment) or in a contact medium (for phone appointment).
 */

@Schema(name = "Appointment", description = "An Appointment is an arrangement to do something or meet someone at a particular time, at a place (for face to face appointment) or in a contact medium (for phone appointment).")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
@Document
public class Appointment {
  @Id
  private String id;

  private String href;

  private String category;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime creationDate;

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
   * @deprecated Use {@link Appointment#Appointment(TimePeriod)}
   */
  @Deprecated
  public Appointment() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Appointment(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public Appointment id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of the appointment
   * @return id
  */
  
  @Schema(name = "id", description = "Unique identifier of the appointment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Appointment href(String href) {
    this.href = href;
    return this;
  }

  /**
   * Unique URI used to access to the appointment resource
   * @return href
  */
  
  @Schema(name = "href", description = "Unique URI used to access to the appointment resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("href")
  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public Appointment category(String category) {
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

  public Appointment creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Appointment creation date
   * @return creationDate
  */
  @Valid 
  @Schema(name = "creationDate", description = "Appointment creation date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creationDate")
  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public Appointment description(String description) {
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

  public Appointment externalId(String externalId) {
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

  public Appointment lastUpdate(OffsetDateTime lastUpdate) {
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

  public Appointment attachment(List<@Valid AttachmentRefOrValue> attachment) {
    this.attachment = attachment;
    return this;
  }

  public Appointment addAttachmentItem(AttachmentRefOrValue attachmentItem) {
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

  public Appointment calendarEvent(CalendarEventRef calendarEvent) {
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

  public Appointment contactMedium(List<@Valid ContactMedium> contactMedium) {
    this.contactMedium = contactMedium;
    return this;
  }

  public Appointment addContactMediumItem(ContactMedium contactMediumItem) {
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

  public Appointment note(List<@Valid Note> note) {
    this.note = note;
    return this;
  }

  public Appointment addNoteItem(Note noteItem) {
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

  public Appointment relatedEntity(List<@Valid RelatedEntity> relatedEntity) {
    this.relatedEntity = relatedEntity;
    return this;
  }

  public Appointment addRelatedEntityItem(RelatedEntity relatedEntityItem) {
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

  public Appointment relatedParty(List<@Valid RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
    return this;
  }

  public Appointment addRelatedPartyItem(RelatedParty relatedPartyItem) {
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

  public Appointment relatedPlace(RelatedPlaceRefOrValue relatedPlace) {
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

  public Appointment status(AppointmentStateType status) {
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

  public Appointment validFor(TimePeriod validFor) {
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

  public Appointment atBaseType(String atBaseType) {
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

  public Appointment atSchemaLocation(URI atSchemaLocation) {
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

  public Appointment atType(String atType) {
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
    Appointment appointment = (Appointment) o;
    return Objects.equals(this.id, appointment.id) &&
        Objects.equals(this.href, appointment.href) &&
        Objects.equals(this.category, appointment.category) &&
        Objects.equals(this.creationDate, appointment.creationDate) &&
        Objects.equals(this.description, appointment.description) &&
        Objects.equals(this.externalId, appointment.externalId) &&
        Objects.equals(this.lastUpdate, appointment.lastUpdate) &&
        Objects.equals(this.attachment, appointment.attachment) &&
        Objects.equals(this.calendarEvent, appointment.calendarEvent) &&
        Objects.equals(this.contactMedium, appointment.contactMedium) &&
        Objects.equals(this.note, appointment.note) &&
        Objects.equals(this.relatedEntity, appointment.relatedEntity) &&
        Objects.equals(this.relatedParty, appointment.relatedParty) &&
        Objects.equals(this.relatedPlace, appointment.relatedPlace) &&
        Objects.equals(this.status, appointment.status) &&
        Objects.equals(this.validFor, appointment.validFor) &&
        Objects.equals(this.atBaseType, appointment.atBaseType) &&
        Objects.equals(this.atSchemaLocation, appointment.atSchemaLocation) &&
        Objects.equals(this.atType, appointment.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, category, creationDate, description, externalId, lastUpdate, attachment, calendarEvent, contactMedium, note, relatedEntity, relatedParty, relatedPlace, status, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Appointment {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
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

