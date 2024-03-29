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
    import de.bitconex.tmf.rcm.model.CharacteristicSpecificationFVO;
    import de.bitconex.tmf.rcm.model.TimePeriod;
    import java.net.URI;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* ResourceSpecificationRelationshipFVO
*/
    @JsonPropertyOrder({
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_AT_TYPE,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_AT_BASE_TYPE,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_RELATIONSHIP_TYPE,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_ROLE,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_ID,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_HREF,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_NAME,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_DEFAULT_QUANTITY,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_MAXIMUM_QUANTITY,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_MINIMUM_QUANTITY,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_CHARACTERISTIC,
        ResourceSpecificationRelationshipFVO.JSON_PROPERTY_VALID_FOR
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

public class ResourceSpecificationRelationshipFVO {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_RELATIONSHIP_TYPE = "relationshipType";
            private String relationshipType;

        public static final String JSON_PROPERTY_ROLE = "role";
            private String role;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private URI href;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_DEFAULT_QUANTITY = "defaultQuantity";
            private Integer defaultQuantity;

        public static final String JSON_PROPERTY_MAXIMUM_QUANTITY = "maximumQuantity";
            private Integer maximumQuantity;

        public static final String JSON_PROPERTY_MINIMUM_QUANTITY = "minimumQuantity";
            private Integer minimumQuantity;

        public static final String JSON_PROPERTY_CHARACTERISTIC = "characteristic";
            private List<CharacteristicSpecificationFVO> characteristic;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;



}

