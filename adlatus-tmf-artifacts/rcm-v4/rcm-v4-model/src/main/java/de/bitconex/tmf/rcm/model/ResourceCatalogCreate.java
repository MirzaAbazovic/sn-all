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
* The root entity for resource catalog management. A resource catalog is a group of resource specifications made available through resource candidates that an organization provides to the consumers (internal consumers like its employees or B2B customers or B2C customers). Skipped properties: id,href
*/
    @JsonPropertyOrder({
        ResourceCatalogCreate.JSON_PROPERTY_DESCRIPTION,
        ResourceCatalogCreate.JSON_PROPERTY_LAST_UPDATE,
        ResourceCatalogCreate.JSON_PROPERTY_LIFECYCLE_STATUS,
        ResourceCatalogCreate.JSON_PROPERTY_NAME,
        ResourceCatalogCreate.JSON_PROPERTY_VERSION,
        ResourceCatalogCreate.JSON_PROPERTY_CATEGORY,
        ResourceCatalogCreate.JSON_PROPERTY_RELATED_PARTY,
        ResourceCatalogCreate.JSON_PROPERTY_VALID_FOR,
        ResourceCatalogCreate.JSON_PROPERTY_AT_BASE_TYPE,
        ResourceCatalogCreate.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ResourceCatalogCreate.JSON_PROPERTY_AT_TYPE
    })
            @JsonTypeName("ResourceCatalog_Create")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceCatalogCreate implements Serializable {
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

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedParty> relatedParty;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}

