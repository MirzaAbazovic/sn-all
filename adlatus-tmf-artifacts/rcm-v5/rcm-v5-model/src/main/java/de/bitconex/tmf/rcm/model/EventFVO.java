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
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;
    import de.bitconex.tmf.rcm.model.CharacteristicFVO;
    import de.bitconex.tmf.rcm.model.EntityRefFVO;
    import de.bitconex.tmf.rcm.model.RelatedPartyRefOrPartyRoleRefFVO;
    import java.time.OffsetDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* EventFVO
*/
    @JsonPropertyOrder({
        EventFVO.JSON_PROPERTY_AT_TYPE,
        EventFVO.JSON_PROPERTY_AT_BASE_TYPE,
        EventFVO.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        EventFVO.JSON_PROPERTY_HREF,
        EventFVO.JSON_PROPERTY_ID,
        EventFVO.JSON_PROPERTY_CORRELATION_ID,
        EventFVO.JSON_PROPERTY_DOMAIN,
        EventFVO.JSON_PROPERTY_TITLE,
        EventFVO.JSON_PROPERTY_DESCRIPTION,
        EventFVO.JSON_PROPERTY_PRIORITY,
        EventFVO.JSON_PROPERTY_TIME_OCCURRED,
        EventFVO.JSON_PROPERTY_SOURCE,
        EventFVO.JSON_PROPERTY_REPORTING_SYSTEM,
        EventFVO.JSON_PROPERTY_RELATED_PARTY,
        EventFVO.JSON_PROPERTY_ANALYTIC_CHARACTERISTIC,
        EventFVO.JSON_PROPERTY_EVENT_ID,
        EventFVO.JSON_PROPERTY_EVENT_TIME,
        EventFVO.JSON_PROPERTY_EVENT_TYPE,
        EventFVO.JSON_PROPERTY_EVENT
    })
            @JsonTypeName("Event_FVO")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EventFVO {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;

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
            private EntityRefFVO source;

        public static final String JSON_PROPERTY_REPORTING_SYSTEM = "reportingSystem";
            private EntityRefFVO reportingSystem;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyRefOrPartyRoleRefFVO> relatedParty;

        public static final String JSON_PROPERTY_ANALYTIC_CHARACTERISTIC = "analyticCharacteristic";
            private List<CharacteristicFVO> analyticCharacteristic;

        public static final String JSON_PROPERTY_EVENT_ID = "eventId";
            private String eventId;

        public static final String JSON_PROPERTY_EVENT_TIME = "eventTime";
            private OffsetDateTime eventTime;

        public static final String JSON_PROPERTY_EVENT_TYPE = "eventType";
            private String eventType;

        public static final String JSON_PROPERTY_EVENT = "event";
            private Object event;



}

