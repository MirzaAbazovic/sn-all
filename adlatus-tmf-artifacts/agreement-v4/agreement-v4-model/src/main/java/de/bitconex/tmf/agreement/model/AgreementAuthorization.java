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
    import java.time.OffsetDateTime;
    import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;

        /**
* A business participant that is responsible for approving the agreement.
*/
    @JsonPropertyOrder({
        AgreementAuthorization.JSON_PROPERTY_DATE,
        AgreementAuthorization.JSON_PROPERTY_SIGNATURE_REPRESENTATION,
        AgreementAuthorization.JSON_PROPERTY_STATE,
        AgreementAuthorization.JSON_PROPERTY_AT_BASE_TYPE,
        AgreementAuthorization.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        AgreementAuthorization.JSON_PROPERTY_AT_TYPE
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AgreementAuthorization implements Serializable {
        public static final String JSON_PROPERTY_DATE = "date";
            private OffsetDateTime date;

        public static final String JSON_PROPERTY_SIGNATURE_REPRESENTATION = "signatureRepresentation";
            private String signatureRepresentation;

        public static final String JSON_PROPERTY_STATE = "state";
            private String state;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private URI atSchemaLocation;

        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            private String atType;



}
