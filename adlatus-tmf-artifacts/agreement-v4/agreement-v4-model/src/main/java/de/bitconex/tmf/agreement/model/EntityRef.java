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
* Entity reference schema to be use for all entityRef class.
*/
    @JsonPropertyOrder({
        EntityRef.JSON_PROPERTY_ID,
        EntityRef.JSON_PROPERTY_HREF,
        EntityRef.JSON_PROPERTY_NAME,
        EntityRef.JSON_PROPERTY_AT_BASE_TYPE,
        EntityRef.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        EntityRef.JSON_PROPERTY_AT_TYPE,
        EntityRef.JSON_PROPERTY_AT_REFERRED_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EntityRef implements Serializable {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;

        public static final String JSON_PROPERTY_AT_REFERRED_TYPE = "@referredType";
            private String atReferredType;



}

