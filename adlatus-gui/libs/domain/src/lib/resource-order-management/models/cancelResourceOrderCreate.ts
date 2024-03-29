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
import { ResourceOrderRef } from './resourceOrderRef';

/**
 * Request for cancellation an existing resource order Skipped properties: id,href,state,effectiveCancellationDate
 */
export interface CancelResourceOrderCreate { 
    /**
     * Reason why the order is cancelled.
     */
    cancellationReason?: string;
    /**
     * Date when the submitter wants the order to be cancelled
     */
    requestedCancellationDate?: Date;
    resourceOrder: ResourceOrderRef;
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