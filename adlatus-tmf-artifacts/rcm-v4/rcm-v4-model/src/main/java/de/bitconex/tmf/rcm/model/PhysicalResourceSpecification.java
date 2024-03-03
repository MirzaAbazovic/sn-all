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
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

        /**
* This is an example of a derived class of ResourceSpecification, and is used to define the invariant characteristics and behavior (attributes, methods, constraints, and relationships) of a PhysicalResource.
*/
    @JsonPropertyOrder({
        PhysicalResourceSpecification.JSON_PROPERTY_ID,
        PhysicalResourceSpecification.JSON_PROPERTY_HREF,
        PhysicalResourceSpecification.JSON_PROPERTY_CATEGORY,
        PhysicalResourceSpecification.JSON_PROPERTY_DESCRIPTION,
        PhysicalResourceSpecification.JSON_PROPERTY_IS_BUNDLE,
        PhysicalResourceSpecification.JSON_PROPERTY_LAST_UPDATE,
        PhysicalResourceSpecification.JSON_PROPERTY_LIFECYCLE_STATUS,
        PhysicalResourceSpecification.JSON_PROPERTY_MODEL,
        PhysicalResourceSpecification.JSON_PROPERTY_NAME,
        PhysicalResourceSpecification.JSON_PROPERTY_PART,
        PhysicalResourceSpecification.JSON_PROPERTY_SKU,
        PhysicalResourceSpecification.JSON_PROPERTY_VENDOR,
        PhysicalResourceSpecification.JSON_PROPERTY_VERSION,
        PhysicalResourceSpecification.JSON_PROPERTY_ATTACHMENT,
        PhysicalResourceSpecification.JSON_PROPERTY_FEATURE_SPECIFICATION,
        PhysicalResourceSpecification.JSON_PROPERTY_RELATED_PARTY,
        PhysicalResourceSpecification.JSON_PROPERTY_RESOURCE_SPEC_CHARACTERISTIC,
        PhysicalResourceSpecification.JSON_PROPERTY_RESOURCE_SPEC_RELATIONSHIP,
        PhysicalResourceSpecification.JSON_PROPERTY_TARGET_RESOURCE_SCHEMA,
        PhysicalResourceSpecification.JSON_PROPERTY_VALID_FOR,
        PhysicalResourceSpecification.JSON_PROPERTY_AT_BASE_TYPE,
        PhysicalResourceSpecification.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        PhysicalResourceSpecification.JSON_PROPERTY_AT_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PhysicalResourceSpecification implements Serializable {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private URI href;

        public static final String JSON_PROPERTY_CATEGORY = "category";
            private String category;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_IS_BUNDLE = "isBundle";
            private Boolean isBundle;

        public static final String JSON_PROPERTY_LAST_UPDATE = "lastUpdate";
            private OffsetDateTime lastUpdate;

        public static final String JSON_PROPERTY_LIFECYCLE_STATUS = "lifecycleStatus";
            private String lifecycleStatus;

        public static final String JSON_PROPERTY_MODEL = "model";
            private String model;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_PART = "part";
            private String part;

        public static final String JSON_PROPERTY_SKU = "sku";
            private String sku;

        public static final String JSON_PROPERTY_VENDOR = "vendor";
            private String vendor;

        public static final String JSON_PROPERTY_VERSION = "version";
            private String version;

        public static final String JSON_PROPERTY_ATTACHMENT = "attachment";
            private List<AttachmentRefOrValue> attachment;

        public static final String JSON_PROPERTY_FEATURE_SPECIFICATION = "featureSpecification";
            private List<FeatureSpecification> featureSpecification;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedParty> relatedParty;

        public static final String JSON_PROPERTY_RESOURCE_SPEC_CHARACTERISTIC = "resourceSpecCharacteristic";
            private List<ResourceSpecificationCharacteristic> resourceSpecCharacteristic;

        public static final String JSON_PROPERTY_RESOURCE_SPEC_RELATIONSHIP = "resourceSpecRelationship";
            private List<ResourceSpecificationRelationship> resourceSpecRelationship;

        public static final String JSON_PROPERTY_TARGET_RESOURCE_SCHEMA = "targetResourceSchema";
            private TargetResourceSchema targetResourceSchema;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}
