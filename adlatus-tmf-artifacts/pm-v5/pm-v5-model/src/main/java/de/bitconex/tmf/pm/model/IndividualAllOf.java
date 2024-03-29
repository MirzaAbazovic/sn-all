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

import java.time.OffsetDateTime;
import java.util.List;

        /**
* Individual represents a single human being (a man, woman or child). The individual can be a customer, an employee or any other person that the organization needs to store information about.
*/
    @JsonPropertyOrder({
        IndividualAllOf.JSON_PROPERTY_GENDER,
        IndividualAllOf.JSON_PROPERTY_PLACE_OF_BIRTH,
        IndividualAllOf.JSON_PROPERTY_COUNTRY_OF_BIRTH,
        IndividualAllOf.JSON_PROPERTY_NATIONALITY,
        IndividualAllOf.JSON_PROPERTY_MARITAL_STATUS,
        IndividualAllOf.JSON_PROPERTY_BIRTH_DATE,
        IndividualAllOf.JSON_PROPERTY_DEATH_DATE,
        IndividualAllOf.JSON_PROPERTY_TITLE,
        IndividualAllOf.JSON_PROPERTY_ARISTOCRATIC_TITLE,
        IndividualAllOf.JSON_PROPERTY_GENERATION,
        IndividualAllOf.JSON_PROPERTY_PREFERRED_GIVEN_NAME,
        IndividualAllOf.JSON_PROPERTY_FAMILY_NAME_PREFIX,
        IndividualAllOf.JSON_PROPERTY_LEGAL_NAME,
        IndividualAllOf.JSON_PROPERTY_MIDDLE_NAME,
        IndividualAllOf.JSON_PROPERTY_NAME,
        IndividualAllOf.JSON_PROPERTY_FORMATTED_NAME,
        IndividualAllOf.JSON_PROPERTY_LOCATION,
        IndividualAllOf.JSON_PROPERTY_STATUS,
        IndividualAllOf.JSON_PROPERTY_OTHER_NAME,
        IndividualAllOf.JSON_PROPERTY_INDIVIDUAL_IDENTIFICATION,
        IndividualAllOf.JSON_PROPERTY_DISABILITY,
        IndividualAllOf.JSON_PROPERTY_LANGUAGE_ABILITY,
        IndividualAllOf.JSON_PROPERTY_SKILL,
        IndividualAllOf.JSON_PROPERTY_FAMILY_NAME,
        IndividualAllOf.JSON_PROPERTY_GIVEN_NAME
    })
            @JsonTypeName("Individual_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class IndividualAllOf {
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

        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

        public static final String JSON_PROPERTY_FORMATTED_NAME = "formattedName";
            private String formattedName;

        public static final String JSON_PROPERTY_LOCATION = "location";
            private String location;

        public static final String JSON_PROPERTY_STATUS = "status";
            private IndividualStateType status;

        public static final String JSON_PROPERTY_OTHER_NAME = "otherName";
            private List<OtherNameIndividual> otherName;

        public static final String JSON_PROPERTY_INDIVIDUAL_IDENTIFICATION = "individualIdentification";
            private List<IndividualIdentification> individualIdentification;

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



}

