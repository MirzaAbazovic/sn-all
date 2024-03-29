/**
 * Resource Catalog Management
 * ## TMF API Reference: TMF634 - Resource Catalog Management  ### Release : 19.0 - June 2019  Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.   ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
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
 * An aggregation, migration, substitution, dependency or exclusivity relationship between/among FeatureSpecificationCharacteristics.
 */
export interface FeatureSpecificationCharacteristicRelationship { 
    /**
     * Unique identifier of the characteristic within the the target feature specification
     */
    characteristicId?: string;
    /**
     * Unique identifier of the target feature specification within the resource specification.
     */
    featureId?: string;
    /**
     * Name of the target characteristic
     */
    name?: string;
    /**
     * Type of relationship such as aggregation, migration, substitution, dependency, exclusivity
     */
    relationshipType?: string;
    /**
     * Hyperlink reference to the resource specification containing the target feature and feature characteristic
     */
    resourceSpecificationHref?: string;
    /**
     * Unique identifier of the resource specification containing the target feature and feature characteristic
     */
    resourceSpecificationId?: string;
    /**
     * The period for which the object is valid
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
