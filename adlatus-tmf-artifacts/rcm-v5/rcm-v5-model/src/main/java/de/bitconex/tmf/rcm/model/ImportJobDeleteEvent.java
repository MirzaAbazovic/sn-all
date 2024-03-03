/*
 * Resource Catalog Management
 * ### February 2023 Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.  ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
 *
 * The version of the OpenAPI document: 5.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rcm.model;

import java.util.Objects;
import java.util.Arrays;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonSubTypes;
    import com.fasterxml.jackson.annotation.JsonTypeInfo;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;
    import de.bitconex.tmf.rcm.model.Characteristic;
    import de.bitconex.tmf.rcm.model.EntityRef;
    import de.bitconex.tmf.rcm.model.ImportJobDeleteEventPayload;
    import de.bitconex.tmf.rcm.model.RelatedPartyRefOrPartyRoleRef;
    import java.time.OffsetDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* ImportJobDeleteEvent
*/
    @JsonPropertyOrder({
        ImportJobDeleteEvent.JSON_PROPERTY_AT_TYPE,
        ImportJobDeleteEvent.JSON_PROPERTY_AT_BASE_TYPE,
        ImportJobDeleteEvent.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ImportJobDeleteEvent.JSON_PROPERTY_HREF,
        ImportJobDeleteEvent.JSON_PROPERTY_ID,
        ImportJobDeleteEvent.JSON_PROPERTY_CORRELATION_ID,
        ImportJobDeleteEvent.JSON_PROPERTY_DOMAIN,
        ImportJobDeleteEvent.JSON_PROPERTY_TITLE,
        ImportJobDeleteEvent.JSON_PROPERTY_DESCRIPTION,
        ImportJobDeleteEvent.JSON_PROPERTY_PRIORITY,
        ImportJobDeleteEvent.JSON_PROPERTY_TIME_OCCURRED,
        ImportJobDeleteEvent.JSON_PROPERTY_SOURCE,
        ImportJobDeleteEvent.JSON_PROPERTY_REPORTING_SYSTEM,
        ImportJobDeleteEvent.JSON_PROPERTY_RELATED_PARTY,
        ImportJobDeleteEvent.JSON_PROPERTY_ANALYTIC_CHARACTERISTIC,
        ImportJobDeleteEvent.JSON_PROPERTY_EVENT_ID,
        ImportJobDeleteEvent.JSON_PROPERTY_EVENT_TIME,
        ImportJobDeleteEvent.JSON_PROPERTY_EVENT_TYPE,
        ImportJobDeleteEvent.JSON_PROPERTY_EVENT
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(
  value = "@type", // ignore manually set @type, it will be automatically generated by Jackson during serialization
  allowSetters = true // allows the @type to be set during deserialization
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)

public class ImportJobDeleteEvent {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

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

        public static final String JSON_PROPERTY_TIME_OCCURRED = "timeOccurred";
            private OffsetDateTime timeOccurred;

        public static final String JSON_PROPERTY_SOURCE = "source";
            private EntityRef source;

        public static final String JSON_PROPERTY_REPORTING_SYSTEM = "reportingSystem";
            private EntityRef reportingSystem;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyRefOrPartyRoleRef> relatedParty;

        public static final String JSON_PROPERTY_ANALYTIC_CHARACTERISTIC = "analyticCharacteristic";
            private List<Characteristic> analyticCharacteristic;

        public static final String JSON_PROPERTY_EVENT_ID = "eventId";
            private String eventId;

        public static final String JSON_PROPERTY_EVENT_TIME = "eventTime";
            private OffsetDateTime eventTime;

        public static final String JSON_PROPERTY_EVENT_TYPE = "eventType";
            private String eventType;

        public static final String JSON_PROPERTY_EVENT = "event";
            private ImportJobDeleteEventPayload event;



}

