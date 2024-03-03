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
    import com.fasterxml.jackson.annotation.*;

        /**
* Base entity schema for use in TMForum Open-APIs. Property.
*/
    @JsonPropertyOrder({
        Entity.JSON_PROPERTY_AT_TYPE,
        Entity.JSON_PROPERTY_AT_BASE_TYPE,
        Entity.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        Entity.JSON_PROPERTY_HREF,
        Entity.JSON_PROPERTY_ID
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Entity {
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



}

