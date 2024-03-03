/*
 * Appointment
 * ## TMF API Reference : TMF 646 - Appointment  An Appointment is an arrangement to do something or meet someone at a particular time and place. It is previously made during an interaction or may be necessary to solve a customer problem or to deliver a product order item. The appointment API goal is to manage an appointment with all the necessary characteristics. First, the API consists in searching free time slots based on given parameters.
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;
import java.util.List;
    import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;

        /**
* This task resource is used to retrieve available time slots. One of this available time slot is after used to create or reschedule an appointment Skipped properties: id,href,status,searchDate,searchResult,availableTimeSlot
*/
    @JsonPropertyOrder({
        SearchTimeSlotCreate.JSON_PROPERTY_RELATED_ENTITY,
        SearchTimeSlotCreate.JSON_PROPERTY_RELATED_PARTY,
        SearchTimeSlotCreate.JSON_PROPERTY_RELATED_PLACE,
        SearchTimeSlotCreate.JSON_PROPERTY_REQUESTED_TIME_SLOT,
        SearchTimeSlotCreate.JSON_PROPERTY_AT_BASE_TYPE,
        SearchTimeSlotCreate.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        SearchTimeSlotCreate.JSON_PROPERTY_AT_TYPE
    })
            @JsonTypeName("SearchTimeSlot_Create")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class SearchTimeSlotCreate implements Serializable {
        public static final String JSON_PROPERTY_RELATED_ENTITY = "relatedEntity";
            private List<RelatedEntity> relatedEntity;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private RelatedParty relatedParty;

        public static final String JSON_PROPERTY_RELATED_PLACE = "relatedPlace";
            private RelatedPlaceRefOrValue relatedPlace;

        public static final String JSON_PROPERTY_REQUESTED_TIME_SLOT = "requestedTimeSlot";
            private List<TimeSlot> requestedTimeSlot;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}

