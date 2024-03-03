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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

        /**
* Organization
*/
    @JsonPropertyOrder({
        Organization.JSON_PROPERTY_IS_LEGAL_ENTITY,
        Organization.JSON_PROPERTY_IS_HEAD_OFFICE,
        Organization.JSON_PROPERTY_ORGANIZATION_TYPE,
        Organization.JSON_PROPERTY_EXISTS_DURING,
        Organization.JSON_PROPERTY_NAME,
        Organization.JSON_PROPERTY_NAME_TYPE,
        Organization.JSON_PROPERTY_STATUS,
        Organization.JSON_PROPERTY_OTHER_NAME,
        Organization.JSON_PROPERTY_ORGANIZATION_IDENTIFICATION,
        Organization.JSON_PROPERTY_ORGANIZATION_CHILD_RELATIONSHIP,
        Organization.JSON_PROPERTY_ORGANIZATION_PARENT_RELATIONSHIP,
        Organization.JSON_PROPERTY_TRADING_NAME
    })
@lombok.Data
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(
  value = "@type", // ignore manually set @type, it will be automatically generated by Jackson during serialization
  allowSetters = true // allows the @type to be set during deserialization
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)

public class Organization extends Party {
        public static final String JSON_PROPERTY_IS_LEGAL_ENTITY = "isLegalEntity";
            private Boolean isLegalEntity;

        public static final String JSON_PROPERTY_IS_HEAD_OFFICE = "isHeadOffice";
            private Boolean isHeadOffice;

        public static final String JSON_PROPERTY_ORGANIZATION_TYPE = "organizationType";
            private String organizationType;

        public static final String JSON_PROPERTY_EXISTS_DURING = "existsDuring";
            private TimePeriod existsDuring;

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_NAME_TYPE = "nameType";
            private String nameType;

        public static final String JSON_PROPERTY_STATUS = "status";
            private OrganizationStateType status;

        public static final String JSON_PROPERTY_OTHER_NAME = "otherName";
            private List<OtherNameOrganization> otherName;

        public static final String JSON_PROPERTY_ORGANIZATION_IDENTIFICATION = "organizationIdentification";
            private List<OrganizationIdentification> organizationIdentification;

        public static final String JSON_PROPERTY_ORGANIZATION_CHILD_RELATIONSHIP = "organizationChildRelationship";
            private List<OrganizationChildRelationship> organizationChildRelationship;

        public static final String JSON_PROPERTY_ORGANIZATION_PARENT_RELATIONSHIP = "organizationParentRelationship";
            private OrganizationParentRelationship organizationParentRelationship;

        public static final String JSON_PROPERTY_TRADING_NAME = "tradingName";
            private String tradingName;



}

