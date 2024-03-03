/*
 * Resource Ordering Management
 * This is Swagger UI environment generated for the TMF Resource Ordering Management specification
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.time.OffsetDateTime;

        /**
* The notification data structure
*/
    @JsonPropertyOrder({
        CancelResourceOrderCreateEvent.JSON_PROPERTY_ID,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_HREF,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_EVENT_ID,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_EVENT_TIME,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_EVENT_TYPE,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_CORRELATION_ID,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_DOMAIN,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_TITLE,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_DESCRIPTION,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_PRIORITY,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_TIME_OCURRED,
        CancelResourceOrderCreateEvent.JSON_PROPERTY_EVENT
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class CancelResourceOrderCreateEvent implements Serializable {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_EVENT_ID = "eventId";
            private String eventId;

        public static final String JSON_PROPERTY_EVENT_TIME = "eventTime";
            private OffsetDateTime eventTime;

        public static final String JSON_PROPERTY_EVENT_TYPE = "eventType";
            private String eventType;

        public static final String JSON_PROPERTY_CORRELATION_ID = "correlationId";
            private String correlationId;

        public static final String JSON_PROPERTY_DOMAIN = "domain";
            private String domain;

        public static final String JSON_PROPERTY_TITLE = "title";
            private String title;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_PRIORITY = "priority";
            private String priority;

        public static final String JSON_PROPERTY_TIME_OCURRED = "timeOcurred";
            private OffsetDateTime timeOcurred;

        public static final String JSON_PROPERTY_EVENT = "event";
            private CancelResourceOrderCreateEventPayload event;



}

