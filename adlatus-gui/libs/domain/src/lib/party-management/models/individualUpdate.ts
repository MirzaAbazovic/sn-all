/**
 * API Party
 * ## TMF API Reference : TMF 632 - Party   ### Release : 19.0   The party API provides standardized mechanism for party management such as creation, update, retrieval, deletion and notification of events. Party can be an individual or an organization that has any kind of relation with the enterprise. Party is created to record individual or organization information before the assignment of any role. For example, within the context of a split billing mechanism, Party API allows creation of the individual or organization that will play the role of 3 rd payer for a given offer and, then, allows consultation or update of his information.  ### Resources - Organization - Individual - Hub  Party API performs the following operations : - Retrieve an organization or an individual - Retrieve a collection of organizations or individuals according to given criteria - Create a new organization or a new individual - Update an existing organization or an existing individual - Delete an existing organization or an existing individual - Notify events on organizatin or individual
 *
 * OpenAPI spec version: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { Characteristic } from './characteristic';
import { ContactMedium } from './contactMedium';
import { Disability } from './disability';
import { ExternalReference } from './externalReference';
import { IndividualIdentification } from './individualIdentification';
import { IndividualStateType } from './individualStateType';
import { LanguageAbility } from './languageAbility';
import { OtherNameIndividual } from './otherNameIndividual';
import { PartyCreditProfile } from './partyCreditProfile';
import { RelatedParty } from './relatedParty';
import { Skill } from './skill';
import { TaxExemptionCertificate } from './taxExemptionCertificate';


/**
 * Individual represents a single human being (a man, woman or child). The individual can be a customer, an employee or any other person that the organization needs to store information about. Skipped properties: id,href
 */
export interface IndividualUpdate { 
    /**
     * e.g. Baron, Graf, Earl,…
     */
    aristocraticTitle?: string;
    /**
     * Birth date
     */
    birthDate?: Date;
    /**
     * Country where the individual was born
     */
    countryOfBirth?: string;
    /**
     * Date of death
     */
    deathDate?: Date;
    /**
     * Contains the non-chosen or inherited name. Also known as last name in the Western context
     */
    familyName?: string;
    /**
     * Family name prefix
     */
    familyNamePrefix?: string;
    /**
     * A fully formatted name in one string with all of its pieces in their proper place and all of the necessary punctuation. Useful for specific contexts (Chinese, Japanese, Korean,…)
     */
    formattedName?: string;
    /**
     * Full name flatten (first, middle, and last names)
     */
    fullName?: string;
    /**
     * Gender
     */
    gender?: string;
    /**
     * e.g.. Sr, Jr, III (the third),…
     */
    generation?: string;
    /**
     * First name of the individual
     */
    givenName?: string;
    /**
     * Legal name or birth name (name one has for official purposes)
     */
    legalName?: string;
    /**
     * Temporary current location od the individual (may be used if the individual has approved its sharing)
     */
    location?: string;
    /**
     * Marital status (married, divorced, widow ...)
     */
    maritalStatus?: string;
    /**
     * Middles name or initial
     */
    middleName?: string;
    /**
     * Nationality
     */
    nationality?: string;
    /**
     * Reference to the place where the individual was born
     */
    placeOfBirth?: string;
    /**
     * Contains the chosen name by which the individual prefers to be addressed. Note: This name may be a name other than a given name, such as a nickname
     */
    preferredGivenName?: string;
    /**
     * Useful for titles (aristocratic, social,...) Pr, Dr, Sir, ...
     */
    title?: string;
    contactMedium?: Array<ContactMedium>;
    creditRating?: Array<PartyCreditProfile>;
    disability?: Array<Disability>;
    externalReference?: Array<ExternalReference>;
    individualIdentification?: Array<IndividualIdentification>;
    languageAbility?: Array<LanguageAbility>;
    otherName?: Array<OtherNameIndividual>;
    partyCharacteristic?: Array<Characteristic>;
    relatedParty?: Array<RelatedParty>;
    skill?: Array<Skill>;
    /**
     * Status of the individual
     */
    status?: IndividualStateType;
    taxExemptionCertificate?: Array<TaxExemptionCertificate>;
    /**
     * When sub-classing, this defines the super-class
     */
    baseType?: string;
    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     */
    schemaLocation?: string;
    /**
     * When sub-classing, this defines the sub-class entity name
     */
    type?: string;
}
