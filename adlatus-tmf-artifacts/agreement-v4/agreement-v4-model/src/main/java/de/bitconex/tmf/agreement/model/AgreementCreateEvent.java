/*
 * Agreement Management
 * This is Swagger UI environment generated for the TMF Agreement Management specification
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.agreement.model;

import java.util.Objects;
import java.util.Arrays;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;
    import de.bitconex.tmf.agreement.model.AgreementCreateEventPayload;
    import java.time.OffsetDateTime;
    import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;

        /**
* The notification data structure
*/
    @JsonPropertyOrder({
        AgreementCreateEvent.JSON_PROPERTY_ID,
        AgreementCreateEvent.JSON_PROPERTY_HREF,
        AgreementCreateEvent.JSON_PROPERTY_EVENT_ID,
        AgreementCreateEvent.JSON_PROPERTY_EVENT_TIME,
        AgreementCreateEvent.JSON_PROPERTY_EVENT_TYPE,
        AgreementCreateEvent.JSON_PROPERTY_CORRELATION_ID,
        AgreementCreateEvent.JSON_PROPERTY_DOMAIN,
        AgreementCreateEvent.JSON_PROPERTY_TITLE,
        AgreementCreateEvent.JSON_PROPERTY_DESCRIPTION,
        AgreementCreateEvent.JSON_PROPERTY_PRIORITY,
        AgreementCreateEvent.JSON_PROPERTY_TIME_OCURRED,
        AgreementCreateEvent.JSON_PROPERTY_EVENT
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AgreementCreateEvent implements Serializable {
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
            private AgreementCreateEventPayload event;



}

