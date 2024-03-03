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

        /**
* Complements the description of an element (for instance a product) through video, pictures...
*/
    @JsonPropertyOrder({
        Attachment.JSON_PROPERTY_ID,
        Attachment.JSON_PROPERTY_HREF,
        Attachment.JSON_PROPERTY_ATTACHMENT_TYPE,
        Attachment.JSON_PROPERTY_CONTENT,
        Attachment.JSON_PROPERTY_DESCRIPTION,
        Attachment.JSON_PROPERTY_MIME_TYPE,
        Attachment.JSON_PROPERTY_NAME,
        Attachment.JSON_PROPERTY_URL,
        Attachment.JSON_PROPERTY_SIZE,
        Attachment.JSON_PROPERTY_VALID_FOR,
        Attachment.JSON_PROPERTY_AT_BASE_TYPE,
        Attachment.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        Attachment.JSON_PROPERTY_AT_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Attachment implements Serializable {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private URI href;

        public static final String JSON_PROPERTY_ATTACHMENT_TYPE = "attachmentType";
            private String attachmentType;

        public static final String JSON_PROPERTY_CONTENT = "content";
            private String content;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_MIME_TYPE = "mimeType";
            private String mimeType;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_URL = "url";
            private URI url;

        public static final String JSON_PROPERTY_SIZE = "size";
            private Quantity size;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}

