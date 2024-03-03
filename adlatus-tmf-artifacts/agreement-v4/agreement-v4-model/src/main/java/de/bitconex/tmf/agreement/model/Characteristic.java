/*
 * Agreement Management
 * This is Swagger UI environment generated for the TMF Agreement Management specification
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.agreement.model;

import java.util.Objects;
import java.util.Arrays;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;
    import java.net.URI;
    import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;

        /**
* Describes a given characteristic of an object or entity through a name/value pair.
*/
    @JsonPropertyOrder({
        Characteristic.JSON_PROPERTY_NAME,
        Characteristic.JSON_PROPERTY_VALUE_TYPE,
        Characteristic.JSON_PROPERTY_VALUE,
        Characteristic.JSON_PROPERTY_AT_BASE_TYPE,
        Characteristic.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        Characteristic.JSON_PROPERTY_AT_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Characteristic implements Serializable {
        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_VALUE_TYPE = "valueType";
            private String valueType;

        public static final String JSON_PROPERTY_VALUE = "value";
            private Object value;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}

