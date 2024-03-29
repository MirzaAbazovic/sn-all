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
import { RelatedParty } from './relatedParty';
import { ResourceCategoryRef } from './resourceCategoryRef';
import { TimePeriod } from './timePeriod';


/**
 * The root entity for resource catalog management. A resource catalog is a group of resource specifications made available through resource candidates that an organization provides to the consumers (internal consumers like its employees or B2B customers or B2C customers). Skipped properties: id,href
 */
export interface ResourceCatalogUpdate { 
    /**
     * Description of this catalog
     */
    description?: string;
    /**
     * Date and time of the last update
     */
    lastUpdate?: Date;
    /**
     * Used to indicate the current lifecycle status
     */
    lifecycleStatus?: string;
    /**
     * Name of the catalog
     */
    name?: string;
    /**
     * Catalog version
     */
    version?: string;
    /**
     * List of root categories contained in this catalog
     */
    category?: Array<ResourceCategoryRef>;
    /**
     * List of parties involved in this catalog
     */
    relatedParty?: Array<RelatedParty>;
    /**
     * The period for which the catalog is valid
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
