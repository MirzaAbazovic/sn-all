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
import java.net.URI;

        /**
* External reference of the individual or reference in other system
*/
    @JsonPropertyOrder({
        ExternalReference.JSON_PROPERTY_EXTERNAL_REFERENCE_TYPE,
        ExternalReference.JSON_PROPERTY_NAME,
        ExternalReference.JSON_PROPERTY_AT_BASE_TYPE,
        ExternalReference.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ExternalReference.JSON_PROPERTY_AT_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExternalReference implements Serializable {
        public static final String JSON_PROPERTY_EXTERNAL_REFERENCE_TYPE = "externalReferenceType";
            private String externalReferenceType;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}

