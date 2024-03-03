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
public class OrganizationStateChangeEvent {
    @JsonProperty("eventId")
    private String eventId;

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

    @JsonProperty("href")
    private String href;

    @JsonProperty("id")
    private String id;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("event")
    private OrganizationStateChangeEventPayload event;

    public OrganizationStateChangeEvent eventId(String eventId) {
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

    public OrganizationStateChangeEvent eventTime(OffsetDateTime eventTime) {
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

    public OrganizationStateChangeEvent description(String description) {
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

    public OrganizationStateChangeEvent timeOcurred(OffsetDateTime timeOcurred) {
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

    public OrganizationStateChangeEvent title(String title) {
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

    public OrganizationStateChangeEvent eventType(String eventType) {
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

    public OrganizationStateChangeEvent domain(String domain) {
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

    public OrganizationStateChangeEvent priority(String priority) {
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

    public OrganizationStateChangeEvent href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Reference of the ProcessFlow
     *
     * @return href
     */
    @ApiModelProperty(value = "Reference of the ProcessFlow")


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public OrganizationStateChangeEvent id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Identifier of the Process flow
     *
     * @return id
     */
    @ApiModelProperty(value = "Identifier of the Process flow")


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrganizationStateChangeEvent correlationId(String correlationId) {
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

    public OrganizationStateChangeEvent event(OrganizationStateChangeEventPayload event) {
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

    public OrganizationStateChangeEventPayload getEvent() {
        return event;
    }

    public void setEvent(OrganizationStateChangeEventPayload event) {
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
        OrganizationStateChangeEvent organizationStateChangeEvent = (OrganizationStateChangeEvent) o;
        return Objects.equals(this.eventId, organizationStateChangeEvent.eventId) &&
                Objects.equals(this.eventTime, organizationStateChangeEvent.eventTime) &&
                Objects.equals(this.description, organizationStateChangeEvent.description) &&
                Objects.equals(this.timeOcurred, organizationStateChangeEvent.timeOcurred) &&
                Objects.equals(this.title, organizationStateChangeEvent.title) &&
                Objects.equals(this.eventType, organizationStateChangeEvent.eventType) &&
                Objects.equals(this.domain, organizationStateChangeEvent.domain) &&
                Objects.equals(this.priority, organizationStateChangeEvent.priority) &&
                Objects.equals(this.href, organizationStateChangeEvent.href) &&
                Objects.equals(this.id, organizationStateChangeEvent.id) &&
                Objects.equals(this.correlationId, organizationStateChangeEvent.correlationId) &&
                Objects.equals(this.event, organizationStateChangeEvent.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventTime, description, timeOcurred, title, eventType, domain, priority, href, id, correlationId, event);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrganizationStateChangeEvent {\n");

        sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
        sb.append("    eventTime: ").append(toIndentedString(eventTime)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    timeOcurred: ").append(toIndentedString(timeOcurred)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
        sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
        sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

