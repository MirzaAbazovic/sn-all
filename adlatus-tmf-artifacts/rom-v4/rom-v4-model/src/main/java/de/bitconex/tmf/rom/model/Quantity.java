/*
 * Resource Ordering Management
 * This is Swagger UI environment generated for the TMF Resource Ordering Management specification
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

        /**
* An amount in a given unit
*/
    @JsonPropertyOrder({
        Quantity.JSON_PROPERTY_AMOUNT,
        Quantity.JSON_PROPERTY_UNITS
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Quantity implements Serializable {
        public static final String JSON_PROPERTY_AMOUNT = "amount";
            private Float amount = 1.0f;

        public static final String JSON_PROPERTY_UNITS = "units";
            private String units;



}

