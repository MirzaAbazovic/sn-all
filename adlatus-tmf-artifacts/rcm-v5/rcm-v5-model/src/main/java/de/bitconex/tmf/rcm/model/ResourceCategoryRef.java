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
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonSubTypes;
    import com.fasterxml.jackson.annotation.JsonTypeInfo;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;
    import com.fasterxml.jackson.annotation.*;

        /**
* ResourceCategoryRef
*/
    @JsonPropertyOrder({
        ResourceCategoryRef.JSON_PROPERTY_AT_TYPE,
        ResourceCategoryRef.JSON_PROPERTY_AT_BASE_TYPE,
        ResourceCategoryRef.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ResourceCategoryRef.JSON_PROPERTY_HREF,
        ResourceCategoryRef.JSON_PROPERTY_ID,
        ResourceCategoryRef.JSON_PROPERTY_NAME,
        ResourceCategoryRef.JSON_PROPERTY_AT_REFERRED_TYPE,
        ResourceCategoryRef.JSON_PROPERTY_VERSION
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(
  value = "@type", // ignore manually set @type, it will be automatically generated by Jackson during serialization
  allowSetters = true // allows the @type to be set during deserialization
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)

public class ResourceCategoryRef {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_AT_REFERRED_TYPE = "@referredType";
            private String atReferredType;

        public static final String JSON_PROPERTY_VERSION = "version";
            private String version;



}

