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
* Organization represents a group of people identified by shared interests or purpose. Examples include business, department and enterprise. Because of the complex nature of many businesses, both organizations and organization units are represented by the same data.
*/
    @JsonPropertyOrder({
        OrganizationFVOAllOf.JSON_PROPERTY_IS_LEGAL_ENTITY,
        OrganizationFVOAllOf.JSON_PROPERTY_IS_HEAD_OFFICE,
        OrganizationFVOAllOf.JSON_PROPERTY_ORGANIZATION_TYPE,
        OrganizationFVOAllOf.JSON_PROPERTY_EXISTS_DURING,
        OrganizationFVOAllOf.JSON_PROPERTY_NAME,
        OrganizationFVOAllOf.JSON_PROPERTY_NAME_TYPE,
        OrganizationFVOAllOf.JSON_PROPERTY_STATUS,
        OrganizationFVOAllOf.JSON_PROPERTY_OTHER_NAME,
        OrganizationFVOAllOf.JSON_PROPERTY_ORGANIZATION_IDENTIFICATION,
        OrganizationFVOAllOf.JSON_PROPERTY_ORGANIZATION_CHILD_RELATIONSHIP,
        OrganizationFVOAllOf.JSON_PROPERTY_ORGANIZATION_PARENT_RELATIONSHIP,
        OrganizationFVOAllOf.JSON_PROPERTY_TRADING_NAME
    })
            @JsonTypeName("Organization_FVO_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class OrganizationFVOAllOf {
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
            private List<OtherNameOrganizationFVO> otherName;

        public static final String JSON_PROPERTY_ORGANIZATION_IDENTIFICATION = "organizationIdentification";
            private List<OrganizationIdentificationFVO> organizationIdentification;

        public static final String JSON_PROPERTY_ORGANIZATION_CHILD_RELATIONSHIP = "organizationChildRelationship";
            private List<OrganizationChildRelationshipFVO> organizationChildRelationship;

        public static final String JSON_PROPERTY_ORGANIZATION_PARENT_RELATIONSHIP = "organizationParentRelationship";
            private OrganizationParentRelationshipFVO organizationParentRelationship;

        public static final String JSON_PROPERTY_TRADING_NAME = "tradingName";
            private String tradingName;



}

