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
    import de.bitconex.tmf.rcm.model.AttachmentRefOrValueFVO;
    import de.bitconex.tmf.rcm.model.CharacteristicSpecificationFVO;
    import de.bitconex.tmf.rcm.model.ExternalIdentifierFVO;
    import de.bitconex.tmf.rcm.model.FeatureSpecificationFVO;
    import de.bitconex.tmf.rcm.model.IntentSpecificationRefFVO;
    import de.bitconex.tmf.rcm.model.RelatedPartyRefOrPartyRoleRefFVO;
    import de.bitconex.tmf.rcm.model.ResourceSpecificationRelationshipFVO;
    import de.bitconex.tmf.rcm.model.TargetResourceSchemaFVO;
    import de.bitconex.tmf.rcm.model.TimePeriod;
    import java.time.OffsetDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* Resources are physical or non-physical components (or some combination of these) within an enterprise&#39;s infrastructure or inventory. They are typically consumed or used by services (for example a physical port assigned to a service) or contribute to the realization of a Product (for example, a SIM card). They can be drawn from the Application, Computing and Network domains, and include, for example, Network Elements, software, IT systems, content and information, and technology components. A ResourceSpecification is a base class that represents a generic means for implementing a particular type of Resource. In essence, a ResourceSpecification defines the common attributes and relationships of a set of related Resources, while Resource defines a specific instance that is based on a particular ResourceSpecification.
*/
    @JsonPropertyOrder({
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_DESCRIPTION,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_VERSION,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_VALID_FOR,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_IS_BUNDLE,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_LAST_UPDATE,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_LIFECYCLE_STATUS,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_NAME,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_CATEGORY,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_TARGET_RESOURCE_SCHEMA,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_FEATURE_SPECIFICATION,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_ATTACHMENT,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_RELATED_PARTY,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_RESOURCE_SPEC_CHARACTERISTIC,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_RESOURCE_SPEC_RELATIONSHIP,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_INTENT_SPECIFICATION,
        ResourceSpecificationFVOAllOf.JSON_PROPERTY_EXTERNAL_IDENTIFIER
    })
            @JsonTypeName("ResourceSpecification_FVO_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceSpecificationFVOAllOf {
        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_VERSION = "version";
            private String version;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_IS_BUNDLE = "isBundle";
            private Boolean isBundle;

        public static final String JSON_PROPERTY_LAST_UPDATE = "lastUpdate";
            private OffsetDateTime lastUpdate;

        public static final String JSON_PROPERTY_LIFECYCLE_STATUS = "lifecycleStatus";
            private String lifecycleStatus;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_CATEGORY = "category";
            private String category;

        public static final String JSON_PROPERTY_TARGET_RESOURCE_SCHEMA = "targetResourceSchema";
            private TargetResourceSchemaFVO targetResourceSchema;

        public static final String JSON_PROPERTY_FEATURE_SPECIFICATION = "featureSpecification";
            private List<FeatureSpecificationFVO> featureSpecification;

        public static final String JSON_PROPERTY_ATTACHMENT = "attachment";
            private List<AttachmentRefOrValueFVO> attachment;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyRefOrPartyRoleRefFVO> relatedParty;

        public static final String JSON_PROPERTY_RESOURCE_SPEC_CHARACTERISTIC = "resourceSpecCharacteristic";
            private List<CharacteristicSpecificationFVO> resourceSpecCharacteristic;

        public static final String JSON_PROPERTY_RESOURCE_SPEC_RELATIONSHIP = "resourceSpecRelationship";
            private List<ResourceSpecificationRelationshipFVO> resourceSpecRelationship;

        public static final String JSON_PROPERTY_INTENT_SPECIFICATION = "intentSpecification";
            private IntentSpecificationRefFVO intentSpecification;

        public static final String JSON_PROPERTY_EXTERNAL_IDENTIFIER = "externalIdentifier";
            private List<ExternalIdentifierFVO> externalIdentifier;



}
