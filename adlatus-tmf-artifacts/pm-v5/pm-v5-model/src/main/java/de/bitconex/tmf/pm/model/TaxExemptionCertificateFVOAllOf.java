/*
 * Party Management
 * TMF API Reference : TMF 632 - Party  Release: 22.5 The party API provides standardized mechanism for party management such as creation, update, retrieval, deletion, and notification of events. Party can be an individual or an organization that has any kind of relation with the enterprise. Party is created to record individual or organization information before the assignment of any role. For example, within the context of a split billing mechanism, Party API allows creation of the individual or organization that will play the role of 3rd payer for a given offer and, then, allows consultation or update of his information. Resources - Party (abstract base class with concrete subclasses Individual and Organization) Party API performs the following operations: - Retrieve an organization or an individual - Retrieve a collection of organizations or individuals according to given criteria - Create a new organization or a new individual - Update an existing organization or an existing individual - Delete an existing organization or an existing individual - Notify events on organization or individual
 *
 * The version of the OpenAPI document: 5.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.pm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

        /**
* A tax exemption certificate represents a tax exemption granted to a party (individual or organization) by a tax jurisdiction which may be a city, state, country,... An exemption has a certificate identifier (received from the jurisdiction that levied the tax) and a validity period. An exemption is per tax types and determines for each type of tax what portion of the tax is exempted (partial by percentage or complete) via the tax definition.
*/
    @JsonPropertyOrder({
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_ID,
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_TAX_DEFINITION,
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_VALID_FOR,
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_CERTIFICATE_NUMBER,
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_ISSUING_JURISDICTION,
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_REASON,
        TaxExemptionCertificateFVOAllOf.JSON_PROPERTY_ATTACHMENT
    })
            @JsonTypeName("TaxExemptionCertificate_FVO_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class TaxExemptionCertificateFVOAllOf {
        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_TAX_DEFINITION = "taxDefinition";
            private List<TaxDefinitionFVO> taxDefinition;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;

        public static final String JSON_PROPERTY_CERTIFICATE_NUMBER = "certificateNumber";
            private String certificateNumber;

        public static final String JSON_PROPERTY_ISSUING_JURISDICTION = "issuingJurisdiction";
            private String issuingJurisdiction;

        public static final String JSON_PROPERTY_REASON = "reason";
            private String reason;

        public static final String JSON_PROPERTY_ATTACHMENT = "attachment";
            private AttachmentRefOrValueFVO attachment;



}

