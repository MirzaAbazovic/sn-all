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

import com.fasterxml.jackson.annotation.*;

import java.util.List;

        /**
* PartyRole
*/
    @JsonPropertyOrder({
        PartyRole.JSON_PROPERTY_AT_TYPE,
        PartyRole.JSON_PROPERTY_AT_BASE_TYPE,
        PartyRole.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        PartyRole.JSON_PROPERTY_HREF,
        PartyRole.JSON_PROPERTY_ID,
        PartyRole.JSON_PROPERTY_NAME,
        PartyRole.JSON_PROPERTY_DESCRIPTION,
        PartyRole.JSON_PROPERTY_ROLE,
        PartyRole.JSON_PROPERTY_ENGAGED_PARTY,
        PartyRole.JSON_PROPERTY_PARTY_ROLE_SPECIFICATION,
        PartyRole.JSON_PROPERTY_CHARACTERISTIC,
        PartyRole.JSON_PROPERTY_ACCOUNT,
        PartyRole.JSON_PROPERTY_AGREEMENT,
        PartyRole.JSON_PROPERTY_CONTACT_MEDIUM,
        PartyRole.JSON_PROPERTY_PAYMENT_METHOD,
        PartyRole.JSON_PROPERTY_CREDIT_PROFILE,
        PartyRole.JSON_PROPERTY_RELATED_PARTY,
        PartyRole.JSON_PROPERTY_STATUS,
        PartyRole.JSON_PROPERTY_STATUS_REASON,
        PartyRole.JSON_PROPERTY_VALID_FOR
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(
  value = "@type", // ignore manually set @type, it will be automatically generated by Jackson during serialization
  allowSetters = true // allows the @type to be set during deserialization
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BusinessPartner.class, name = "BusinessPartner"),
  @JsonSubTypes.Type(value = Consumer.class, name = "Consumer"),
  @JsonSubTypes.Type(value = PartyRole.class, name = "PartyRole"),
  @JsonSubTypes.Type(value = Producer.class, name = "Producer"),
  @JsonSubTypes.Type(value = Supplier.class, name = "Supplier"),
})

public class PartyRole {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_ROLE = "role";
            private String role;

        public static final String JSON_PROPERTY_ENGAGED_PARTY = "engagedParty";
            private PartyRef engagedParty;

        public static final String JSON_PROPERTY_PARTY_ROLE_SPECIFICATION = "partyRoleSpecification";
            private PartyRoleSpecificationRef partyRoleSpecification;

        public static final String JSON_PROPERTY_CHARACTERISTIC = "characteristic";
            private List<Characteristic> characteristic;

        public static final String JSON_PROPERTY_ACCOUNT = "account";
            private List<AccountRef> account;

        public static final String JSON_PROPERTY_AGREEMENT = "agreement";
            private List<AgreementRef> agreement;

        public static final String JSON_PROPERTY_CONTACT_MEDIUM = "contactMedium";
            private List<ContactMedium> contactMedium;

        public static final String JSON_PROPERTY_PAYMENT_METHOD = "paymentMethod";
            private List<PaymentMethodRef> paymentMethod;

        public static final String JSON_PROPERTY_CREDIT_PROFILE = "creditProfile";
            private List<CreditProfile> creditProfile;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyOrPartyRole> relatedParty;

        public static final String JSON_PROPERTY_STATUS = "status";
            private String status;

        public static final String JSON_PROPERTY_STATUS_REASON = "statusReason";
            private String statusReason;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;



}

