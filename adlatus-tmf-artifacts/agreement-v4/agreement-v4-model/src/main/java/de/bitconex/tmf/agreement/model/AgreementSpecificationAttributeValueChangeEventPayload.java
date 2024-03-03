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
    import de.bitconex.tmf.agreement.model.AgreementSpecification;
    import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;

        /**
* The event data structure
*/
    @JsonPropertyOrder({
        AgreementSpecificationAttributeValueChangeEventPayload.JSON_PROPERTY_AGREEMENT_SPECIFICATION
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AgreementSpecificationAttributeValueChangeEventPayload implements Serializable {
        public static final String JSON_PROPERTY_AGREEMENT_SPECIFICATION = "agreementSpecification";
            private AgreementSpecification agreementSpecification;



}

