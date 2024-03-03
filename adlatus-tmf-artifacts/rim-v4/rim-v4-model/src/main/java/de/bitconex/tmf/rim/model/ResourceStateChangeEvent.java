/*
 * API Resource Inventory Management
 * ## TMF API Reference: TMF639 - Resource Inventory   ### Release : 19.5 - December 2019  Resource Inventory  API goal is to provide the ability to manage Resources.  ### Operations Resource Inventory API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity (for administration purposes) - Manage notification of events
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rim.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.time.OffsetDateTime;

        /**
* The notification data structure
*/
    @JsonPropertyOrder({
        ResourceStateChangeEvent.JSON_PROPERTY_EVENT,
        ResourceStateChangeEvent.JSON_PROPERTY_EVENT_ID,
        ResourceStateChangeEvent.JSON_PROPERTY_EVENT_TIME,
        ResourceStateChangeEvent.JSON_PROPERTY_EVENT_TYPE,
        ResourceStateChangeEvent.JSON_PROPERTY_CORRELATION_ID,
        ResourceStateChangeEvent.JSON_PROPERTY_DOMAIN,
        ResourceStateChangeEvent.JSON_PROPERTY_TITLE,
        ResourceStateChangeEvent.JSON_PROPERTY_DESCRIPTION,
        ResourceStateChangeEvent.JSON_PROPERTY_PRIORITY,
        ResourceStateChangeEvent.JSON_PROPERTY_TIME_OCURRED
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceStateChangeEvent implements Serializable {
        public static final String JSON_PROPERTY_EVENT = "event";
            private ResourceStateChangeEventPayload event;

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



}

