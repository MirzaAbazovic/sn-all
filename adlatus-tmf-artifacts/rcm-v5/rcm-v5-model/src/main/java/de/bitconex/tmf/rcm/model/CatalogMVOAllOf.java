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
    import de.bitconex.tmf.rcm.model.RelatedPartyRefOrPartyRoleRefMVO;
    import de.bitconex.tmf.rcm.model.TimePeriod;
    import java.time.OffsetDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* A collection of Catalog Items
*/
    @JsonPropertyOrder({
        CatalogMVOAllOf.JSON_PROPERTY_DESCRIPTION,
        CatalogMVOAllOf.JSON_PROPERTY_CATALOG_TYPE,
        CatalogMVOAllOf.JSON_PROPERTY_VALID_FOR,
        CatalogMVOAllOf.JSON_PROPERTY_VERSION,
        CatalogMVOAllOf.JSON_PROPERTY_RELATED_PARTY,
        CatalogMVOAllOf.JSON_PROPERTY_LAST_UPDATE,
        CatalogMVOAllOf.JSON_PROPERTY_LIFECYCLE_STATUS,
        CatalogMVOAllOf.JSON_PROPERTY_NAME
    })
            @JsonTypeName("Catalog_MVO_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class CatalogMVOAllOf {
        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_CATALOG_TYPE = "catalogType";
            private String catalogType;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_VERSION = "version";
            private String version;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyRefOrPartyRoleRefMVO> relatedParty;

        public static final String JSON_PROPERTY_LAST_UPDATE = "lastUpdate";
            private OffsetDateTime lastUpdate;

        public static final String JSON_PROPERTY_LIFECYCLE_STATUS = "lifecycleStatus";
            private String lifecycleStatus;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;



}

