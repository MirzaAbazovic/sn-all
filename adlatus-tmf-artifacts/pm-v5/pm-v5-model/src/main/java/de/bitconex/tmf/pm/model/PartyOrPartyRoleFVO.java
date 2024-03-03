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

import java.time.OffsetDateTime;
import java.util.List;

        /**
* 
*/
    @JsonPropertyOrder({
        PartyOrPartyRoleFVO.JSON_PROPERTY_AT_TYPE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_AT_BASE_TYPE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_AT_SCHEMA_LOCATION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ID,
        PartyOrPartyRoleFVO.JSON_PROPERTY_HREF,
        PartyOrPartyRoleFVO.JSON_PROPERTY_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_AT_REFERRED_TYPE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PARTY_ID,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PARTY_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_EXTERNAL_REFERENCE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PARTY_CHARACTERISTIC,
        PartyOrPartyRoleFVO.JSON_PROPERTY_TAX_EXEMPTION_CERTIFICATE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_CREDIT_RATING,
        PartyOrPartyRoleFVO.JSON_PROPERTY_RELATED_PARTY,
        PartyOrPartyRoleFVO.JSON_PROPERTY_CONTACT_MEDIUM,
        PartyOrPartyRoleFVO.JSON_PROPERTY_GENDER,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PLACE_OF_BIRTH,
        PartyOrPartyRoleFVO.JSON_PROPERTY_COUNTRY_OF_BIRTH,
        PartyOrPartyRoleFVO.JSON_PROPERTY_NATIONALITY,
        PartyOrPartyRoleFVO.JSON_PROPERTY_MARITAL_STATUS,
        PartyOrPartyRoleFVO.JSON_PROPERTY_BIRTH_DATE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_DEATH_DATE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_TITLE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ARISTOCRATIC_TITLE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_GENERATION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PREFERRED_GIVEN_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_FAMILY_NAME_PREFIX,
        PartyOrPartyRoleFVO.JSON_PROPERTY_LEGAL_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_MIDDLE_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_FORMATTED_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_LOCATION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_STATUS,
        PartyOrPartyRoleFVO.JSON_PROPERTY_OTHER_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_INDIVIDUAL_IDENTIFICATION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_DISABILITY,
        PartyOrPartyRoleFVO.JSON_PROPERTY_LANGUAGE_ABILITY,
        PartyOrPartyRoleFVO.JSON_PROPERTY_SKILL,
        PartyOrPartyRoleFVO.JSON_PROPERTY_FAMILY_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_GIVEN_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_IS_LEGAL_ENTITY,
        PartyOrPartyRoleFVO.JSON_PROPERTY_IS_HEAD_OFFICE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ORGANIZATION_TYPE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_EXISTS_DURING,
        PartyOrPartyRoleFVO.JSON_PROPERTY_NAME_TYPE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ORGANIZATION_IDENTIFICATION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ORGANIZATION_CHILD_RELATIONSHIP,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ORGANIZATION_PARENT_RELATIONSHIP,
        PartyOrPartyRoleFVO.JSON_PROPERTY_TRADING_NAME,
        PartyOrPartyRoleFVO.JSON_PROPERTY_DESCRIPTION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ROLE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ENGAGED_PARTY,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PARTY_ROLE_SPECIFICATION,
        PartyOrPartyRoleFVO.JSON_PROPERTY_CHARACTERISTIC,
        PartyOrPartyRoleFVO.JSON_PROPERTY_ACCOUNT,
        PartyOrPartyRoleFVO.JSON_PROPERTY_AGREEMENT,
        PartyOrPartyRoleFVO.JSON_PROPERTY_PAYMENT_METHOD,
        PartyOrPartyRoleFVO.JSON_PROPERTY_CREDIT_PROFILE,
        PartyOrPartyRoleFVO.JSON_PROPERTY_STATUS_REASON,
        PartyOrPartyRoleFVO.JSON_PROPERTY_VALID_FOR
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(
  value = "@type", // ignore manually set @type, it will be automatically generated by Jackson during serialization
  allowSetters = true // allows the @type to be set during deserialization
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BusinessPartnerFVO.class, name = "BusinessPartner"),
  @JsonSubTypes.Type(value = BusinessPartnerFVO.class, name = "BusinessPartner_FVO"),
  @JsonSubTypes.Type(value = ConsumerFVO.class, name = "Consumer"),
  @JsonSubTypes.Type(value = ConsumerFVO.class, name = "Consumer_FVO"),
  @JsonSubTypes.Type(value = IndividualFVO.class, name = "Individual"),
  @JsonSubTypes.Type(value = IndividualFVO.class, name = "Individual_FVO"),
  @JsonSubTypes.Type(value = OrganizationFVO.class, name = "Organization"),
  @JsonSubTypes.Type(value = OrganizationFVO.class, name = "Organization_FVO"),
  @JsonSubTypes.Type(value = PartyRefFVO.class, name = "PartyRef"),
  @JsonSubTypes.Type(value = PartyRefFVO.class, name = "PartyRef_FVO"),
  @JsonSubTypes.Type(value = PartyRoleFVO.class, name = "PartyRole"),
  @JsonSubTypes.Type(value = PartyRoleRefFVO.class, name = "PartyRoleRef"),
  @JsonSubTypes.Type(value = PartyRoleRefFVO.class, name = "PartyRoleRef_FVO"),
  @JsonSubTypes.Type(value = PartyRoleFVO.class, name = "PartyRole_FVO"),
  @JsonSubTypes.Type(value = ProducerFVO.class, name = "Producer"),
  @JsonSubTypes.Type(value = ProducerFVO.class, name = "Producer_FVO"),
  @JsonSubTypes.Type(value = SupplierFVO.class, name = "Supplier"),
  @JsonSubTypes.Type(value = SupplierFVO.class, name = "Supplier_FVO"),
})

public class PartyOrPartyRoleFVO {
        public static final String JSON_PROPERTY_AT_TYPE = "@type";
            protected String atType;

        public static final String JSON_PROPERTY_AT_BASE_TYPE = "@baseType";
            private String atBaseType;

        public static final String JSON_PROPERTY_AT_SCHEMA_LOCATION = "@schemaLocation";
            private String atSchemaLocation;

        public static final String JSON_PROPERTY_ID = "id";
            private String id;

        public static final String JSON_PROPERTY_HREF = "href";
            private String href;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_AT_REFERRED_TYPE = "@referredType";
            private String atReferredType;

        public static final String JSON_PROPERTY_PARTY_ID = "partyId";
            private String partyId;

        public static final String JSON_PROPERTY_PARTY_NAME = "partyName";
            private String partyName;

        public static final String JSON_PROPERTY_EXTERNAL_REFERENCE = "externalReference";
            private List<ExternalIdentifierFVO> externalReference;

        public static final String JSON_PROPERTY_PARTY_CHARACTERISTIC = "partyCharacteristic";
            private List<CharacteristicFVO> partyCharacteristic;

        public static final String JSON_PROPERTY_TAX_EXEMPTION_CERTIFICATE = "taxExemptionCertificate";
            private List<TaxExemptionCertificateFVO> taxExemptionCertificate;

        public static final String JSON_PROPERTY_CREDIT_RATING = "creditRating";
            private List<PartyCreditProfileFVO> creditRating;

        public static final String JSON_PROPERTY_RELATED_PARTY = "relatedParty";
            private List<RelatedPartyOrPartyRoleFVO> relatedParty;

        public static final String JSON_PROPERTY_CONTACT_MEDIUM = "contactMedium";
            private List<ContactMediumFVO> contactMedium;

        public static final String JSON_PROPERTY_GENDER = "gender";
            private String gender;

        public static final String JSON_PROPERTY_PLACE_OF_BIRTH = "placeOfBirth";
            private String placeOfBirth;

        public static final String JSON_PROPERTY_COUNTRY_OF_BIRTH = "countryOfBirth";
            private String countryOfBirth;

        public static final String JSON_PROPERTY_NATIONALITY = "nationality";
            private String nationality;

        public static final String JSON_PROPERTY_MARITAL_STATUS = "maritalStatus";
            private String maritalStatus;

        public static final String JSON_PROPERTY_BIRTH_DATE = "birthDate";
            private OffsetDateTime birthDate;

        public static final String JSON_PROPERTY_DEATH_DATE = "deathDate";
            private OffsetDateTime deathDate;

        public static final String JSON_PROPERTY_TITLE = "title";
            private String title;

        public static final String JSON_PROPERTY_ARISTOCRATIC_TITLE = "aristocraticTitle";
            private String aristocraticTitle;

        public static final String JSON_PROPERTY_GENERATION = "generation";
            private String generation;

        public static final String JSON_PROPERTY_PREFERRED_GIVEN_NAME = "preferredGivenName";
            private String preferredGivenName;

        public static final String JSON_PROPERTY_FAMILY_NAME_PREFIX = "familyNamePrefix";
            private String familyNamePrefix;

        public static final String JSON_PROPERTY_LEGAL_NAME = "legalName";
            private String legalName;

        public static final String JSON_PROPERTY_MIDDLE_NAME = "middleName";
            private String middleName;

        public static final String JSON_PROPERTY_FORMATTED_NAME = "formattedName";
            private String formattedName;

        public static final String JSON_PROPERTY_LOCATION = "location";
            private String location;

        public static final String JSON_PROPERTY_STATUS = "status";
            private String status;

        public static final String JSON_PROPERTY_OTHER_NAME = "otherName";
            private List<OtherNameOrganizationFVO> otherName;

        public static final String JSON_PROPERTY_INDIVIDUAL_IDENTIFICATION = "individualIdentification";
            private List<IndividualIdentificationFVO> individualIdentification;

        public static final String JSON_PROPERTY_DISABILITY = "disability";
            private List<Disability> disability;

        public static final String JSON_PROPERTY_LANGUAGE_ABILITY = "languageAbility";
            private List<LanguageAbility> languageAbility;

        public static final String JSON_PROPERTY_SKILL = "skill";
            private List<Skill> skill;

        public static final String JSON_PROPERTY_FAMILY_NAME = "familyName";
            private String familyName;

        public static final String JSON_PROPERTY_GIVEN_NAME = "givenName";
            private String givenName;

        public static final String JSON_PROPERTY_IS_LEGAL_ENTITY = "isLegalEntity";
            private Boolean isLegalEntity;

        public static final String JSON_PROPERTY_IS_HEAD_OFFICE = "isHeadOffice";
            private Boolean isHeadOffice;

        public static final String JSON_PROPERTY_ORGANIZATION_TYPE = "organizationType";
            private String organizationType;

        public static final String JSON_PROPERTY_EXISTS_DURING = "existsDuring";
            private TimePeriod existsDuring;

        public static final String JSON_PROPERTY_NAME_TYPE = "nameType";
            private String nameType;

        public static final String JSON_PROPERTY_ORGANIZATION_IDENTIFICATION = "organizationIdentification";
            private List<OrganizationIdentificationFVO> organizationIdentification;

        public static final String JSON_PROPERTY_ORGANIZATION_CHILD_RELATIONSHIP = "organizationChildRelationship";
            private List<OrganizationChildRelationshipFVO> organizationChildRelationship;

        public static final String JSON_PROPERTY_ORGANIZATION_PARENT_RELATIONSHIP = "organizationParentRelationship";
            private OrganizationParentRelationshipFVO organizationParentRelationship;

        public static final String JSON_PROPERTY_TRADING_NAME = "tradingName";
            private String tradingName;

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

        public static final String JSON_PROPERTY_PAYMENT_METHOD = "paymentMethod";
            private List<PaymentMethodRefFVO> paymentMethod;

        public static final String JSON_PROPERTY_CREDIT_PROFILE = "creditProfile";
            private List<CreditProfileFVO> creditProfile;

        public static final String JSON_PROPERTY_STATUS_REASON = "statusReason";
            private String statusReason;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;



}

