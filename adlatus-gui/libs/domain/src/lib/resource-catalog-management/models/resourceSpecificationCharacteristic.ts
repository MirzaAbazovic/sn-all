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
import { ResourceSpecificationCharacteristicRelationship } from './resourceSpecificationCharacteristicRelationship';
import { ResourceSpecificationCharacteristicValue } from './resourceSpecificationCharacteristicValue';
import { TimePeriod } from './timePeriod';


/**
 * This class defines the characteristic features of a resource specification. Every ResourceSpecification has a variety of important attributes, methods, constraints, and relationships, which distinguish a resource specification from other resource specifications.
 */
export interface ResourceSpecificationCharacteristic { 
    /**
     * Unique ID for the characteristic
     */
    id?: string;
    /**
     * If true, the Boolean indicates that the ResourceSpecCharacteristic is configurable
     */
    configurable?: boolean;
    /**
     * A narrative that explains the CharacteristicSpecification.
     */
    description?: string;
    /**
     * An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.
     */
    extensible?: boolean;
    /**
     * An indicator that specifies if a value is unique for the specification. Possible values are; \"unique while value is in effect\" and \"unique whether value is in effect or not\"
     */
    isUnique?: boolean;
    /**
     * The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.
     */
    maxCardinality?: number;
    /**
     * The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.
     */
    minCardinality?: number;
    /**
     * A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.
     */
    name?: string;
    /**
     * A rule or principle represented in regular expression used to derive the value of a characteristic value.
     */
    regex?: string;
    /**
     * A kind of value that the characteristic can take on, such as numeric, text and so forth
     */
    valueType?: string;
    /**
     * An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.
     */
    resourceSpecCharRelationship?: Array<ResourceSpecificationCharacteristicRelationship>;
    /**
     * A ResourceSpecificationCharacteristicValue object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a ResourceSpecificationCharacteristic object. The values of the attributes in the ResourceSpecificationCharacteristicValue object describe the values of the attributes that a corresponding ResourceSpecificationCharacteristic object can take on.
     */
    resourceSpecCharacteristicValue?: Array<ResourceSpecificationCharacteristicValue>;
    /**
     * The period of time for which a characteristic is applicable.
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
     * This (optional) field provides a link to the schema describing the value type.
     */
    valueSchemaLocation?: string;
}
