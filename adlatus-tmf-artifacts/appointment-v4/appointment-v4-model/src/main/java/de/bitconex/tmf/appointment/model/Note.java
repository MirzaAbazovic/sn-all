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
    import java.time.OffsetDateTime;
    import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;

        /**
* Extra information about a given entity
*/
    @JsonPropertyOrder({
        Note.JSON_PROPERTY_ID,
        Note.JSON_PROPERTY_AUTHOR,
        Note.JSON_PROPERTY_DATE,
        Note.JSON_PROPERTY_TEXT,
        Note.JSON_PROPERTY_AT_BASE_TYPE,
        Note.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        Note.JSON_PROPERTY_AT_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Note implements Serializable {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_AUTHOR = "author";
            private String author;

        public static final String JSON_PROPERTY_DATE = "date";
            private OffsetDateTime date;

        public static final String JSON_PROPERTY_TEXT = "text";
            private String text;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}

