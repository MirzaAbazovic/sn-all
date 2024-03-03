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
* An identification of an entity that is owned by or originates in a software system different from the current system, for example a ProductOrder handed off from a commerce platform into an order handling system. The structure identifies the system itself, the nature of the entity within the system (e.g. class name) and the unique ID of the entity within the system. It is anticipated that multiple external IDs can be held for a single entity, e.g. if the entity passed through multiple systems on the way to the current system. In this case the consumer is expected to sequence the IDs in the array in reverse order of provenance, i.e. most recent system first in the list.
*/
    @JsonPropertyOrder({
        ExternalIdentifierAllOf.JSON_PROPERTY_OWNER,
        ExternalIdentifierAllOf.JSON_PROPERTY_EXTERNAL_IDENTIFIER_TYPE,
        ExternalIdentifierAllOf.JSON_PROPERTY_ID
    })
            @JsonTypeName("ExternalIdentifier_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExternalIdentifierAllOf {
        public static final String JSON_PROPERTY_OWNER = "owner";
            private String owner;

        public static final String JSON_PROPERTY_EXTERNAL_IDENTIFIER_TYPE = "externalIdentifierType";
            private String externalIdentifierType;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;



}
