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
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

        /**
* ResourceCandidate is an entity that makes a resource specification available to a catalog. A ResourceCandidate and its associated resource specification may be published - made visible - in any number of resource catalogs, or in none. Skipped properties: id,href
*/
    @JsonPropertyOrder({
        ResourceCandidateCreate.JSON_PROPERTY_DESCRIPTION,
        ResourceCandidateCreate.JSON_PROPERTY_LAST_UPDATE,
        ResourceCandidateCreate.JSON_PROPERTY_LIFECYCLE_STATUS,
        ResourceCandidateCreate.JSON_PROPERTY_NAME,
        ResourceCandidateCreate.JSON_PROPERTY_VERSION,
        ResourceCandidateCreate.JSON_PROPERTY_CATEGORY,
        ResourceCandidateCreate.JSON_PROPERTY_RESOURCE_SPECIFICATION,
        ResourceCandidateCreate.JSON_PROPERTY_VALID_FOR,
        ResourceCandidateCreate.JSON_PROPERTY_AT_BASE_TYPE,
        ResourceCandidateCreate.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ResourceCandidateCreate.JSON_PROPERTY_AT_TYPE
    })
            @JsonTypeName("ResourceCandidate_Create")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceCandidateCreate implements Serializable {
        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_LAST_UPDATE = "lastUpdate";
            private OffsetDateTime lastUpdate;

        public static final String JSON_PROPERTY_LIFECYCLE_STATUS = "lifecycleStatus";
            private String lifecycleStatus;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_VERSION = "version";
            private String version;

        public static final String JSON_PROPERTY_CATEGORY = "category";
            private List<ResourceCategoryRef> category;

        public static final String JSON_PROPERTY_RESOURCE_SPECIFICATION = "resourceSpecification";
            private ResourceSpecificationRef resourceSpecification;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}
