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
import { CancelResourceOrderInformationRequiredEventPayload } from './cancelResourceOrderInformationRequiredEventPayload';

/**
 * The notification data structure
 */
export interface CancelResourceOrderInformationRequiredEvent { 
    /**
     * The identifier of the notification.
     */
    eventId?: string;
    /**
     * Time of the event occurrence.
     */
    eventTime?: Date;
    /**
     * The type of the notification.
     */
    eventType?: string;
    /**
     * The correlation id for this event.
     */
    correlationId?: string;
    /**
     * The domain of the event.
     */
    domain?: string;
    /**
     * The title of the event.
     */
    title?: string;
    /**
     * An explnatory of the event.
     */
    description?: string;
    /**
     * A priority.
     */
    priority?: string;
    /**
     * The time the event occured.
     */
    timeOcurred?: Date;
    /**
     * The path identifying the object field concerned by this notification.
     */
    fieldPath?: string;
    event?: CancelResourceOrderInformationRequiredEventPayload;
}