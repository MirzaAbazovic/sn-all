/**
 * Agreement Management
 * This is Swagger UI environment generated for the TMF Agreement Management specification
 *
 * OpenAPI spec version: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { AgreementSpecCharacteristic } from './agreementSpecCharacteristic';
import { AgreementSpecificationRelationship } from './agreementSpecificationRelationship';
import { AttachmentRefOrValue } from './attachmentRefOrValue';
import { CategoryRef } from './categoryRef';
import { RelatedParty } from './relatedParty';
import { TimePeriod } from './timePeriod';


/**
 * A template of an agreement that can be used when establishing partnerships Skipped properties: id,href
 */
export interface AgreementSpecificationCreate { 
    /**
     * A narrative that explains in detail what the agreement specification is about
     */
    description?: string;
    /**
     * If true, this agreement specification is a grouping of other agreement specifications. The list of bundled agreement specifications is provided by the specificationRelationship property
     */
    isBundle?: boolean;
    /**
     * Date and time of the last update
     */
    lastUpdate?: Date;
    /**
     * Indicates the current lifecycle status
     */
    lifecycleStatus?: string;
    /**
     * Name of the agreement specification
     */
    name: string;
    /**
     * Agreement specification version
     */
    version?: string;
    attachment: Array<AttachmentRefOrValue>;
    relatedParty?: Array<RelatedParty>;
    serviceCategory?: CategoryRef;
    specificationCharacteristic?: Array<AgreementSpecCharacteristic>;
    specificationRelationship?: Array<AgreementSpecificationRelationship>;
    /**
     * The period for which the agreement specification is valid
     */
    validFor?: TimePeriod;
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
