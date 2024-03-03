/*
 * Party Management
 * TMF API Reference : TMF 632 - Party  Release: 22.5 The party API provides standardized mechanism for party management such as creation, update, retrieval, deletion, and notification of events. Party can be an individual or an organization that has any kind of relation with the enterprise. Party is created to record individual or organization information before the assignment of any role. For example, within the context of a split billing mechanism, Party API allows creation of the individual or organization that will play the role of 3rd payer for a given offer and, then, allows consultation or update of his information. Resources - Party (abstract base class with concrete subclasses Individual and Organization) Party API performs the following operations: - Retrieve an organization or an individual - Retrieve a collection of organizations or individuals according to given criteria - Create a new organization or a new individual - Update an existing organization or an existing individual - Delete an existing organization or an existing individual - Notify events on organization or individual
 *
 * The version of the OpenAPI document: 5.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.pm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

        /**
* EntityRefFVO
*/
    @JsonPropertyOrder({
        EntityRefFVO.JSON_PROPERTY_AT_TYPE,
        EntityRefFVO.JSON_PROPERTY_AT_BASE_TYPE,
        EntityRefFVO.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        EntityRefFVO.JSON_PROPERTY_ID,
        EntityRefFVO.JSON_PROPERTY_HREF,
        EntityRefFVO.JSON_PROPERTY_NAME,
        EntityRefFVO.JSON_PROPERTY_AT_REFERRED_TYPE
    })
            @JsonTypeName("EntityRef_FVO")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EntityRefFVO {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_AT_REFERRED_TYPE = "@referredType";
            private String atReferredType;



}

