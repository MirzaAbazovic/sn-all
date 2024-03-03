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

        /**
* Ability of an individual to understand or converse in a language.
*/
    @JsonPropertyOrder({
        LanguageAbility.JSON_PROPERTY_LANGUAGE_CODE,
        LanguageAbility.JSON_PROPERTY_LANGUAGE_NAME,
        LanguageAbility.JSON_PROPERTY_IS_FAVOURITE_LANGUAGE,
        LanguageAbility.JSON_PROPERTY_WRITING_PROFICIENCY,
        LanguageAbility.JSON_PROPERTY_READING_PROFICIENCY,
        LanguageAbility.JSON_PROPERTY_SPEAKING_PROFICIENCY,
        LanguageAbility.JSON_PROPERTY_LISTENING_PROFICIENCY,
        LanguageAbility.JSON_PROPERTY_VALID_FOR
    })
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class LanguageAbility {
        public static final String JSON_PROPERTY_LANGUAGE_CODE = "languageCode";
            private String languageCode;

        public static final String JSON_PROPERTY_LANGUAGE_NAME = "languageName";
            private String languageName;

        public static final String JSON_PROPERTY_IS_FAVOURITE_LANGUAGE = "isFavouriteLanguage";
            private Boolean isFavouriteLanguage;

        public static final String JSON_PROPERTY_WRITING_PROFICIENCY = "writingProficiency";
            private String writingProficiency;

        public static final String JSON_PROPERTY_READING_PROFICIENCY = "readingProficiency";
            private String readingProficiency;

        public static final String JSON_PROPERTY_SPEAKING_PROFICIENCY = "speakingProficiency";
            private String speakingProficiency;

        public static final String JSON_PROPERTY_LISTENING_PROFICIENCY = "listeningProficiency";
            private String listeningProficiency;

        public static final String JSON_PROPERTY_VALID_FOR = "validFor";
            private TimePeriod validFor;



}
