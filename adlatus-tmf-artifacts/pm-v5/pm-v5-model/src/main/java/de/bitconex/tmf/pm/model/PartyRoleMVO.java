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
* PartyRoleMVO
*/
    @JsonPropertyOrder({
        PartyRoleMVO.JSON_PROPERTY_AT_TYPE,
        PartyRoleMVO.JSON_PROPERTY_AT_BASE_TYPE,
        PartyRoleMVO.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        PartyRoleMVO.JSON_PROPERTY_NAME,
        PartyRoleMVO.JSON_PROPERTY_DESCRIPTION,
        PartyRoleMVO.JSON_PROPERTY_ROLE,
        PartyRoleMVO.JSON_PROPERTY_ENGAGED_PARTY,
        PartyRoleMVO.JSON_PROPERTY_PARTY_ROLE_SPECIFICATION,
        PartyRoleMVO.JSON_PROPERTY_CHARACTERISTIC,
        PartyRoleMVO.JSON_PROPERTY_ACCOUNT,
        PartyRoleMVO.JSON_PROPERTY_AGREEMENT,
        PartyRoleMVO.JSON_PROPERTY_CONTACT_MEDIUM,
        PartyRoleMVO.JSON_PROPERTY_PAYMENT_METHOD,
        PartyRoleMVO.JSON_PROPERTY_CREDIT_PROFILE,
        PartyRoleMVO.JSON_PROPERTY_RELATED_PARTY,
        PartyRoleMVO.JSON_PROPERTY_STATUS,
        PartyRoleMVO.JSON_PROPERTY_STATUS_REASON,
        PartyRoleMVO.JSON_PROPERTY_VALID_FOR
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
  @JsonSubTypes.Type(value = BusinessPartnerMVO.class, name = "BusinessPartner"),
  @JsonSubTypes.Type(value = ConsumerMVO.class, name = "Consumer"),
  @JsonSubTypes.Type(value = PartyRoleMVO.class, name = "PartyRole"),
  @JsonSubTypes.Type(value = ProducerMVO.class, name = "Producer"),
  @JsonSubTypes.Type(value = SupplierMVO.class, name = "Supplier"),
})

public class PartyRoleMVO {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_ROLE = "role";
            private String role;

        public static final String JSON_PROPERTY_ENGAGED_PARTY = "engagedParty";
            private PartyRefMVO engagedParty;

        public static final String JSON_PROPERTY_PARTY_ROLE_SPECIFICATION = "partyRoleSpecification";
            private PartyRoleSpecificationRefMVO partyRoleSpecification;

        public static final String JSON_PROPERTY_CHARACTERISTIC = "characteristic";
            private List<CharacteristicMVO> characteristic;

        public static final String JSON_PROPERTY_ACCOUNT = "account";
            private List<AccountRefMVO> account;

        public static final String JSON_PROPERTY_AGREEMENT = "agreement";
            private List<AgreementRefMVO> agreement;

        public static final String JSON_PROPERTY_CONTACT_MEDIUM = "contactMedium";
            private List<ContactMediumMVO> contactMedium;

        public static final String JSON_PROPERTY_PAYMENT_METHOD = "paymentMethod";
            private List<PaymentMethodRefMVO> paymentMethod;

        public static final String JSON_PROPERTY_CREDIT_PROFILE = "creditProfile";
            private List<CreditProfileMVO> creditProfile;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyOrPartyRoleMVO> relatedParty;

        public static final String JSON_PROPERTY_STATUS = "status";
            private String status;

        public static final String JSON_PROPERTY_STATUS_REASON = "statusReason";
            private String statusReason;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;



}
