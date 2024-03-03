/*
 * API Resource Inventory Management
 * ## TMF API Reference: TMF639 - Resource Inventory   ### Release : 19.5 - December 2019  Resource Inventory  API goal is to provide the ability to manage Resources.  ### Operations Resource Inventory API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity (for administration purposes) - Manage notification of events
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rim.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.net.URI;

        /**
* Base schema for adressable entities
*/
    @JsonPropertyOrder({
        Addressable.JSON_PROPERTY_ID,
        Addressable.JSON_PROPERTY_HREF
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Addressable implements Serializable {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private URI href;



}

