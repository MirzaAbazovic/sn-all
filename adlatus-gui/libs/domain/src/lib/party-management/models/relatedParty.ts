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


/**
 * Related Entity reference. A related party defines party or party role linked to a specific entity.
 */
export interface RelatedParty { 
    /**
     * Unique identifier of a related entity.
     */
    id: string;
    /**
     * Reference of the related entity.
     */
    href?: string;
    /**
     * Name of the related entity.
     */
    name?: string;
    /**
     * Role played by the related party
     */
    role?: string;
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
    referredType: string;
}
