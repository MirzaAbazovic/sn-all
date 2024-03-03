/*
 * Resource Catalog Management
 * ## TMF API Reference: TMF634 - Resource Catalog Management  ### December 2019  Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.   ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
 *
 * The version of the OpenAPI document: 4.1.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.time.OffsetDateTime;

        /**
* The notification data structure
*/
    @JsonPropertyOrder({
        ResourceCatalogDeleteEvent.JSON_PROPERTY_EVENT,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_EVENT_ID,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_EVENT_TIME,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_EVENT_TYPE,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_CORRELATION_ID,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_DOMAIN,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_TITLE,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_DESCRIPTION,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_PRIORITY,
        ResourceCatalogDeleteEvent.JSON_PROPERTY_TIME_OCURRED
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceCatalogDeleteEvent implements Serializable {
        public static final String JSON_PROPERTY_EVENT = "event";
            private ResourceCatalogDeleteEventPayload event;

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

