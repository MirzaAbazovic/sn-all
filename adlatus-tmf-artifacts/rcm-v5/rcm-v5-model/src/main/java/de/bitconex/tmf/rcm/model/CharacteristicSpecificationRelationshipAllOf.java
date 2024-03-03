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
    import de.bitconex.tmf.rcm.model.TimePeriod;
    import java.net.URI;
    import com.fasterxml.jackson.annotation.*;

        /**
* An aggregation, migration, substitution, dependency or exclusivity relationship between/among Characteristic specifications. The specification characteristic is embedded within the specification whose ID and href are in this entity, and identified by its ID.
*/
    @JsonPropertyOrder({
        CharacteristicSpecificationRelationshipAllOf.JSON_PROPERTY_RELATIONSHIP_TYPE,
        CharacteristicSpecificationRelationshipAllOf.JSON_PROPERTY_NAME,
        CharacteristicSpecificationRelationshipAllOf.JSON_PROPERTY_CHARACTERISTIC_SPECIFICATION_ID,
        CharacteristicSpecificationRelationshipAllOf.JSON_PROPERTY_PARENT_SPECIFICATION_HREF,
        CharacteristicSpecificationRelationshipAllOf.JSON_PROPERTY_VALID_FOR,
        CharacteristicSpecificationRelationshipAllOf.JSON_PROPERTY_PARENT_SPECIFICATION_ID
    })
            @JsonTypeName("CharacteristicSpecificationRelationship_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class CharacteristicSpecificationRelationshipAllOf {
        public static final String JSON_PROPERTY_RELATIONSHIP_TYPE = "relationshipType";
            private String relationshipType;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_CHARACTERISTIC_SPECIFICATION_ID = "characteristicSpecificationId";
            private String characteristicSpecificationId;

        public static final String JSON_PROPERTY_PARENT_SPECIFICATION_HREF = "parentSpecificationHref";
            private URI parentSpecificationHref;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_PARENT_SPECIFICATION_ID = "parentSpecificationId";
            private String parentSpecificationId;



}

