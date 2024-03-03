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
* ResourceSpecificationFVO
*/
    @JsonPropertyOrder({
        ResourceSpecificationFVO.JSON_PROPERTY_AT_TYPE,
        ResourceSpecificationFVO.JSON_PROPERTY_AT_BASE_TYPE,
        ResourceSpecificationFVO.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ResourceSpecificationFVO.JSON_PROPERTY_ID,
        ResourceSpecificationFVO.JSON_PROPERTY_DESCRIPTION,
        ResourceSpecificationFVO.JSON_PROPERTY_VERSION,
        ResourceSpecificationFVO.JSON_PROPERTY_VALID_FOR,
        ResourceSpecificationFVO.JSON_PROPERTY_IS_BUNDLE,
        ResourceSpecificationFVO.JSON_PROPERTY_LAST_UPDATE,
        ResourceSpecificationFVO.JSON_PROPERTY_LIFECYCLE_STATUS,
        ResourceSpecificationFVO.JSON_PROPERTY_NAME,
        ResourceSpecificationFVO.JSON_PROPERTY_CATEGORY,
        ResourceSpecificationFVO.JSON_PROPERTY_TARGET_RESOURCE_SCHEMA,
        ResourceSpecificationFVO.JSON_PROPERTY_FEATURE_SPECIFICATION,
        ResourceSpecificationFVO.JSON_PROPERTY_ATTACHMENT,
        ResourceSpecificationFVO.JSON_PROPERTY_RELATED_PARTY,
        ResourceSpecificationFVO.JSON_PROPERTY_RESOURCE_SPEC_CHARACTERISTIC,
        ResourceSpecificationFVO.JSON_PROPERTY_RESOURCE_SPEC_RELATIONSHIP,
        ResourceSpecificationFVO.JSON_PROPERTY_INTENT_SPECIFICATION,
        ResourceSpecificationFVO.JSON_PROPERTY_EXTERNAL_IDENTIFIER
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

public class ResourceSpecificationFVO {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

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
