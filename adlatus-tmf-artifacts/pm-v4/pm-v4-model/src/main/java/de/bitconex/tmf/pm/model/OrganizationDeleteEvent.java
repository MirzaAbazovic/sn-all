/*
 * API Party
 * ## TMF API Reference : TMF 632 - Party   ### Release : 19.0   The party API provides standardized mechanism for party management such as creation, update, retrieval, deletion and notification of events. Party can be an individual or an organization that has any kind of relation with the enterprise. Party is created to record individual or organization information before the assignment of any role. For example, within the context of a split billing mechanism, Party API allows creation of the individual or organization that will play the role of 3 rd payer for a given offer and, then, allows consultation or update of his information.  ### Resources - Organization - Individual - Hub  Party API performs the following operations : - Retrieve an organization or an individual - Retrieve a collection of organizations or individuals according to given criteria - Create a new organization or a new individual - Update an existing organization or an existing individual - Delete an existing organization or an existing individual - Notify events on organizatin or individual
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.pm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.time.OffsetDateTime;

        /**
* The notification data structure
*/
    @JsonPropertyOrder({
        OrganizationDeleteEvent.JSON_PROPERTY_EVENT_ID,
        OrganizationDeleteEvent.JSON_PROPERTY_EVENT_TIME,
        OrganizationDeleteEvent.JSON_PROPERTY_DESCRIPTION,
        OrganizationDeleteEvent.JSON_PROPERTY_TIME_OCURRED,
        OrganizationDeleteEvent.JSON_PROPERTY_TITLE,
        OrganizationDeleteEvent.JSON_PROPERTY_EVENT_TYPE,
        OrganizationDeleteEvent.JSON_PROPERTY_DOMAIN,
        OrganizationDeleteEvent.JSON_PROPERTY_PRIORITY,
        OrganizationDeleteEvent.JSON_PROPERTY_HREF,
        OrganizationDeleteEvent.JSON_PROPERTY_ID,
        OrganizationDeleteEvent.JSON_PROPERTY_CORRELATION_ID,
        OrganizationDeleteEvent.JSON_PROPERTY_EVENT
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class OrganizationDeleteEvent implements Serializable {
        public static final String JSON_PROPERTY_EVENT_ID = "eventId";
            private String eventId;

        public static final String JSON_PROPERTY_EVENT_TIME = "eventTime";
            private OffsetDateTime eventTime;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_TIME_OCURRED = "timeOcurred";
            private OffsetDateTime timeOcurred;

        public static final String JSON_PROPERTY_TITLE = "title";
            private String title;

        public static final String JSON_PROPERTY_EVENT_TYPE = "eventType";
            private String eventType;

        public static final String JSON_PROPERTY_DOMAIN = "domain";
            private String domain;

        public static final String JSON_PROPERTY_PRIORITY = "priority";
            private String priority;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_CORRELATION_ID = "correlationId";
            private String correlationId;

        public static final String JSON_PROPERTY_EVENT = "event";
            private OrganizationDeleteEventPayload event;



}

