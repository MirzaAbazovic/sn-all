/**
 * Resource Ordering Management
 * This is Swagger UI environment generated for the TMF Resource Ordering Management specification
 *
 * OpenAPI spec version: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { ExternalId } from './externalId';
import { Note } from './note';
import { RelatedParty } from './relatedParty';
import { ResourceOrderItem } from './resourceOrderItem';
import { ResourceOrderStateType } from './resourceOrderStateType';

/**
 * A Resource Order is a request to provision a set of Resources (logical and physical) triggered by the request to provision a Service through a Service Order Skipped properties: id,href
 */
export interface ResourceOrderCreate { 
    /**
     * Used to categorize the order from a business perspective that can be useful for the OM system.
     */
    category?: string;
    /**
     * Date when the order was completed
     */
    completionDate?: Date;
    /**
     * free-text description of the Resource Order
     */
    description?: string;
    /**
     * Date when the order was completed
     */
    expectedCompletionDate?: Date;
    /**
     * DEPRECATED: Use externalReference Instead. ID given by the consumer (to facilitate searches afterwards)
     */
    externalId?: string;
    /**
     * A string used to give a name to the Resource Order
     */
    name?: string;
    /**
     * Date when the order was created
     */
    orderDate?: Date;
    /**
     * Name of the Resource Order type
     */
    orderType?: string;
    /**
     * A way that can be used by consumers to prioritize orders in OM system (from 0 to 4 : 0 is the highest priority, and 4 the lowest)
     */
    priority?: number;
    /**
     * Requested delivery date from the requestor perspective
     */
    requestedCompletionDate?: Date;
    /**
     * Order start date wished by the requestor
     */
    requestedStartDate?: Date;
    /**
     * Date when the order was actually started
     */
    startDate?: Date;
    state?: ResourceOrderStateType;
    externalReference?: Array<ExternalId>;
    note?: Array<Note>;
    orderItem?: Array<ResourceOrderItem>;
    relatedParty?: Array<RelatedParty>;
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