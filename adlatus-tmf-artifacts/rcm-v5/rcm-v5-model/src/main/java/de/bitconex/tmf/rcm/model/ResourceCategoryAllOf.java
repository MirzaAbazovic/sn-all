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
    import de.bitconex.tmf.rcm.model.ExternalIdentifier;
    import de.bitconex.tmf.rcm.model.RelatedPartyRefOrPartyRoleRef;
    import de.bitconex.tmf.rcm.model.ResourceCandidateRef;
    import de.bitconex.tmf.rcm.model.ResourceCategoryRef;
    import de.bitconex.tmf.rcm.model.ResourceSpecificationRef;
    import de.bitconex.tmf.rcm.model.TimePeriod;
    import java.time.OffsetDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* The (resource) category resource is used to group resource candidates in logical containers. Categories can contain other categories.
*/
    @JsonPropertyOrder({
        ResourceCategoryAllOf.JSON_PROPERTY_DESCRIPTION,
        ResourceCategoryAllOf.JSON_PROPERTY_NAME,
        ResourceCategoryAllOf.JSON_PROPERTY_VERSION,
        ResourceCategoryAllOf.JSON_PROPERTY_VALID_FOR,
        ResourceCategoryAllOf.JSON_PROPERTY_LIFECYCLE_STATUS,
        ResourceCategoryAllOf.JSON_PROPERTY_LAST_UPDATE,
        ResourceCategoryAllOf.JSON_PROPERTY_PARENT_ID,
        ResourceCategoryAllOf.JSON_PROPERTY_IS_ROOT,
        ResourceCategoryAllOf.JSON_PROPERTY_CATEGORY,
        ResourceCategoryAllOf.JSON_PROPERTY_RESOURCE_SPECIFICATION,
        ResourceCategoryAllOf.JSON_PROPERTY_RESOURCE_CANDIDATE,
        ResourceCategoryAllOf.JSON_PROPERTY_RELATED_PARTY,
        ResourceCategoryAllOf.JSON_PROPERTY_EXTERNAL_IDENTIFIER
    })
            @JsonTypeName("ResourceCategory_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResourceCategoryAllOf {
        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_VERSION = "version";
            private String version;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_LIFECYCLE_STATUS = "lifecycleStatus";
            private String lifecycleStatus;

        public static final String JSON_PROPERTY_LAST_UPDATE = "lastUpdate";
            private OffsetDateTime lastUpdate;

        public static final String JSON_PROPERTY_PARENT_ID = "parentId";
            private String parentId;

        public static final String JSON_PROPERTY_IS_ROOT = "isRoot";
            private Boolean isRoot;

        public static final String JSON_PROPERTY_CATEGORY = "category";
            private List<ResourceCategoryRef> category;

        public static final String JSON_PROPERTY_RESOURCE_SPECIFICATION = "resourceSpecification";
            private List<ResourceSpecificationRef> resourceSpecification;

        public static final String JSON_PROPERTY_RESOURCE_CANDIDATE = "resourceCandidate";
            private List<ResourceCandidateRef> resourceCandidate;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyRefOrPartyRoleRef> relatedParty;

        public static final String JSON_PROPERTY_EXTERNAL_IDENTIFIER = "externalIdentifier";
            private List<ExternalIdentifier> externalIdentifier;



}

