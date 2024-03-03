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
import { ResourceCandidate } from './resourceCandidate';


/**
 * The event data structure
 */
export interface ResourceCandidateDeleteEventPayload { 
    /**
     * The involved resource data for the event
     */
    resourceCandidate?: ResourceCandidate;
}
