package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * The notification data structure
 */
@ApiModel(description = "The notification data structure")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class OrganizationAttributeValueChangeEvent {
    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("fieldPath")
    private String fieldPath;

    @JsonProperty("eventTime")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime eventTime;

    @JsonProperty("description")
    private String description;

    @JsonProperty("timeOcurred")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime timeOcurred;

    @JsonProperty("title")
    private String title;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("event")
    private OrganizationAttributeValueChangeEventPayload event;

    public OrganizationAttributeValueChangeEvent eventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    /**
     * The identifier of the notification.
     *
     * @return eventId
     */
    @ApiModelProperty(value = "The identifier of the notification.")


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public OrganizationAttributeValueChangeEvent fieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
        return this;
    }

    /**
     * The path identifying the object field concerned by this notification.
     *
     * @return fieldPath
     */
    @ApiModelProperty(value = "The path identifying the object field concerned by this notification.")


    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public OrganizationAttributeValueChangeEvent eventTime(OffsetDateTime eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    /**
     * Time of the event occurrence.
     *
     * @return eventTime
     */
    @ApiModelProperty(value = "Time of the event occurrence.")

    @Valid

    public OffsetDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(OffsetDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public OrganizationAttributeValueChangeEvent description(String description) {
        this.description = description;
        return this;
    }

    /**
     * An explnatory of the event.
     *
     * @return description
     */
    @ApiModelProperty(value = "An explnatory of the event.")


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrganizationAttributeValueChangeEvent timeOcurred(OffsetDateTime timeOcurred) {
        this.timeOcurred = timeOcurred;
        return this;
    }

    /**
     * The time the event occured.
     *
     * @return timeOcurred
     */
    @ApiModelProperty(value = "The time the event occured.")

    @Valid

    public OffsetDateTime getTimeOcurred() {
        return timeOcurred;
    }

    public void setTimeOcurred(OffsetDateTime timeOcurred) {
        this.timeOcurred = timeOcurred;
    }

    public OrganizationAttributeValueChangeEvent title(String title) {
        this.title = title;
        return this;
    }

    /**
     * The title of the event.
     *
     * @return title
     */
    @ApiModelProperty(value = "The title of the event.")


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OrganizationAttributeValueChangeEvent eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * The type of the notification.
     *
     * @return eventType
     */
    @ApiModelProperty(value = "The type of the notification.")


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public OrganizationAttributeValueChangeEvent domain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * The domain of the event.
     *
     * @return domain
     */
    @ApiModelProperty(value = "The domain of the event.")


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public OrganizationAttributeValueChangeEvent priority(String priority) {
        this.priority = priority;
        return this;
    }

    /**
     * A priority.
     *
     * @return priority
     */
    @ApiModelProperty(value = "A priority.")


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public OrganizationAttributeValueChangeEvent correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    /**
     * The correlation id for this event.
     *
     * @return correlationId
     */
    @ApiModelProperty(value = "The correlation id for this event.")


    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public OrganizationAttributeValueChangeEvent event(OrganizationAttributeValueChangeEventPayload event) {
        this.event = event;
        return this;
    }

    /**
     * Get event
     *
     * @return event
     */
    @ApiModelProperty(value = "")

    @Valid

    public OrganizationAttributeValueChangeEventPayload getEvent() {
        return event;
    }

    public void setEvent(OrganizationAttributeValueChangeEventPayload event) {
        this.event = event;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrganizationAttributeValueChangeEvent organizationAttributeValueChangeEvent = (OrganizationAttributeValueChangeEvent) o;
        return Objects.equals(this.eventId, organizationAttributeValueChangeEvent.eventId) &&
                Objects.equals(this.fieldPath, organizationAttributeValueChangeEvent.fieldPath) &&
                Objects.equals(this.eventTime, organizationAttributeValueChangeEvent.eventTime) &&
                Objects.equals(this.description, organizationAttributeValueChangeEvent.description) &&
                Objects.equals(this.timeOcurred, organizationAttributeValueChangeEvent.timeOcurred) &&
                Objects.equals(this.title, organizationAttributeValueChangeEvent.title) &&
                Objects.equals(this.eventType, organizationAttributeValueChangeEvent.eventType) &&
                Objects.equals(this.domain, organizationAttributeValueChangeEvent.domain) &&
                Objects.equals(this.priority, organizationAttributeValueChangeEvent.priority) &&
                Objects.equals(this.correlationId, organizationAttributeValueChangeEvent.correlationId) &&
                Objects.equals(this.event, organizationAttributeValueChangeEvent.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, fieldPath, eventTime, description, timeOcurred, title, eventType, domain, priority, correlationId, event);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrganizationAttributeValueChangeEvent {\n");

        sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
        sb.append("    fieldPath: ").append(toIndentedString(fieldPath)).append("\n");
        sb.append("    eventTime: ").append(toIndentedString(eventTime)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    timeOcurred: ").append(toIndentedString(timeOcurred)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
        sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
        sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
        sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
        sb.append("    event: ").append(toIndentedString(event)).append("\n");
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

