/*
 * Resource Catalog Management
 * ## TMF API Reference: TMF634 - Resource Catalog Management  ### December 2019  Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.   ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
 *
 * The version of the OpenAPI document: 4.1.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

        /**
* The event data structure
*/
    @JsonPropertyOrder({
        ResourceCategoryDeleteEventPayload.JSON_PROPERTY_RESOURCE_CATEGORY
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceCategoryDeleteEventPayload implements Serializable {
        public static final String JSON_PROPERTY_RESOURCE_CATEGORY = "resourceCategory";
            private ResourceCategory resourceCategory;



}

