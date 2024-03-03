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
import java.net.URI;
import java.time.OffsetDateTime;

        /**
* Extra information about a given entity
*/
    @JsonPropertyOrder({
        Note.JSON_PROPERTY_ID,
        Note.JSON_PROPERTY_HREF,
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

        public static final String JSON_PROPERTY_HREF = "href";
            private URI href;

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

