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
* An Appointment is an arrangement to do something or meet someone at a particular time, at a place (for face to face appointment) or in a contact medium (for phone appointment). Skipped properties: id,href,status,creationDate,lastUpdate
*/
    @JsonPropertyOrder({
        AppointmentCreate.JSON_PROPERTY_CATEGORY,
        AppointmentCreate.JSON_PROPERTY_DESCRIPTION,
        AppointmentCreate.JSON_PROPERTY_EXTERNAL_ID,
        AppointmentCreate.JSON_PROPERTY_ATTACHMENT,
        AppointmentCreate.JSON_PROPERTY_CALENDAR_EVENT,
        AppointmentCreate.JSON_PROPERTY_CONTACT_MEDIUM,
        AppointmentCreate.JSON_PROPERTY_NOTE,
        AppointmentCreate.JSON_PROPERTY_RELATED_ENTITY,
        AppointmentCreate.JSON_PROPERTY_RELATED_PARTY,
        AppointmentCreate.JSON_PROPERTY_RELATED_PLACE,
        AppointmentCreate.JSON_PROPERTY_VALID_FOR,
        AppointmentCreate.JSON_PROPERTY_AT_BASE_TYPE,
        AppointmentCreate.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        AppointmentCreate.JSON_PROPERTY_AT_TYPE
    })
            @JsonTypeName("Appointment_Create")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AppointmentCreate implements Serializable {
        public static final String JSON_PROPERTY_CATEGORY = "category";
            private String category;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_EXTERNAL_ID = "externalId";
            private String externalId;

        public static final String JSON_PROPERTY_ATTACHMENT = "attachment";
            private List<AttachmentRefOrValue> attachment;

        public static final String JSON_PROPERTY_CALENDAR_EVENT = "calendarEvent";
            private CalendarEventRef calendarEvent;

        public static final String JSON_PROPERTY_CONTACT_MEDIUM = "contactMedium";
            private List<ContactMedium> contactMedium;

        public static final String JSON_PROPERTY_NOTE = "note";
            private List<Note> note;

        public static final String JSON_PROPERTY_RELATED_ENTITY = "relatedEntity";
            private List<RelatedEntity> relatedEntity;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedParty> relatedParty;

        public static final String JSON_PROPERTY_RELATED_PLACE = "relatedPlace";
            private RelatedPlaceRefOrValue relatedPlace;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}
