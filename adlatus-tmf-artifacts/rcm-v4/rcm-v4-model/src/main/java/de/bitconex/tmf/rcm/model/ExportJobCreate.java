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

        /**
* Represents a task used to export resources to a file Skipped properties: id,href
*/
    @JsonPropertyOrder({
        ExportJobCreate.JSON_PROPERTY_COMPLETION_DATE,
        ExportJobCreate.JSON_PROPERTY_CONTENT_TYPE,
        ExportJobCreate.JSON_PROPERTY_CREATION_DATE,
        ExportJobCreate.JSON_PROPERTY_ERROR_LOG,
        ExportJobCreate.JSON_PROPERTY_PATH,
        ExportJobCreate.JSON_PROPERTY_QUERY,
        ExportJobCreate.JSON_PROPERTY_URL,
        ExportJobCreate.JSON_PROPERTY_STATUS,
        ExportJobCreate.JSON_PROPERTY_AT_BASE_TYPE,
        ExportJobCreate.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        ExportJobCreate.JSON_PROPERTY_AT_TYPE
    })
            @JsonTypeName("ExportJob_Create")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExportJobCreate implements Serializable {
        public static final String JSON_PROPERTY_COMPLETION_DATE = "completionDate";
            private OffsetDateTime completionDate;

        public static final String JSON_PROPERTY_CONTENT_TYPE = "contentType";
            private String contentType;

        public static final String JSON_PROPERTY_CREATION_DATE = "creationDate";
            private OffsetDateTime creationDate;

        public static final String JSON_PROPERTY_ERROR_LOG = "errorLog";
            private String errorLog;

        public static final String JSON_PROPERTY_PATH = "path";
            private String path;

        public static final String JSON_PROPERTY_QUERY = "query";
            private String query;

        public static final String JSON_PROPERTY_URL = "url";
            private URI url;

        public static final String JSON_PROPERTY_STATUS = "status";
            private JobStateType status;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}
