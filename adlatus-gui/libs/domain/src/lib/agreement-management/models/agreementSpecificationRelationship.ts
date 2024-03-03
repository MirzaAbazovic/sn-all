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
import { TimePeriod } from './timePeriod';


/**
 * A relationship between agreement specifications. Typical relationships are substitution and dependency.
 */
export interface AgreementSpecificationRelationship { 
    /**
     * Unique identifier of a related entity.
     */
    id?: string;
    /**
     * Reference of the related entity.
     */
    href?: string;
    /**
     * Name of the related entity.
     */
    name?: string;
    /**
     * Type of relationship such as, substitution or dependency.
     */
    relationshipType?: string;
    /**
     * The period for which the relationship is valid
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
    /**
     * The actual type of the target instance when needed for disambiguation.
     */
    referredType?: string;
}
