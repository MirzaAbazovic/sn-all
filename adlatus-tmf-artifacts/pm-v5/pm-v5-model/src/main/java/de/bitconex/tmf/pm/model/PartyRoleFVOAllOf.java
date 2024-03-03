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
* The part played by a party in a given context.
*/
    @JsonPropertyOrder({
        PartyRoleFVOAllOf.JSON_PROPERTY_NAME,
        PartyRoleFVOAllOf.JSON_PROPERTY_DESCRIPTION,
        PartyRoleFVOAllOf.JSON_PROPERTY_ROLE,
        PartyRoleFVOAllOf.JSON_PROPERTY_ENGAGED_PARTY,
        PartyRoleFVOAllOf.JSON_PROPERTY_PARTY_ROLE_SPECIFICATION,
        PartyRoleFVOAllOf.JSON_PROPERTY_CHARACTERISTIC,
        PartyRoleFVOAllOf.JSON_PROPERTY_ACCOUNT,
        PartyRoleFVOAllOf.JSON_PROPERTY_AGREEMENT,
        PartyRoleFVOAllOf.JSON_PROPERTY_CONTACT_MEDIUM,
        PartyRoleFVOAllOf.JSON_PROPERTY_PAYMENT_METHOD,
        PartyRoleFVOAllOf.JSON_PROPERTY_CREDIT_PROFILE,
        PartyRoleFVOAllOf.JSON_PROPERTY_RELATED_PARTY,
        PartyRoleFVOAllOf.JSON_PROPERTY_STATUS,
        PartyRoleFVOAllOf.JSON_PROPERTY_STATUS_REASON,
        PartyRoleFVOAllOf.JSON_PROPERTY_VALID_FOR
    })
            @JsonTypeName("PartyRole_FVO_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PartyRoleFVOAllOf {
        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_DESCRIPTION = "description";
            private String description;

        public static final String JSON_PROPERTY_ROLE = "role";
            private String role;

        public static final String JSON_PROPERTY_ENGAGED_PARTY = "engagedParty";
            private PartyRefFVO engagedParty;

        public static final String JSON_PROPERTY_PARTY_ROLE_SPECIFICATION = "partyRoleSpecification";
            private PartyRoleSpecificationRefFVO partyRoleSpecification;

        public static final String JSON_PROPERTY_CHARACTERISTIC = "characteristic";
            private List<CharacteristicFVO> characteristic;

        public static final String JSON_PROPERTY_ACCOUNT = "account";
            private List<AccountRefFVO> account;

        public static final String JSON_PROPERTY_AGREEMENT = "agreement";
            private List<AgreementRefFVO> agreement;

        public static final String JSON_PROPERTY_CONTACT_MEDIUM = "contactMedium";
            private List<ContactMediumFVO> contactMedium;

        public static final String JSON_PROPERTY_PAYMENT_METHOD = "paymentMethod";
            private List<PaymentMethodRefFVO> paymentMethod;

        public static final String JSON_PROPERTY_CREDIT_PROFILE = "creditProfile";
            private List<CreditProfileFVO> creditProfile;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyOrPartyRoleFVO> relatedParty;

        public static final String JSON_PROPERTY_STATUS = "status";
            private String status;

        public static final String JSON_PROPERTY_STATUS_REASON = "statusReason";
            private String statusReason;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;



}
