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
import { Quantity } from './quantity';
import { TimePeriod } from './timePeriod';


/**
 * Complements the description of an element (for instance a product) through video, pictures...
 */
export interface Attachment { 
    /**
     * Unique identifier for this particular attachment
     */
    id?: string;
    /**
     * URI for this Attachment
     */
    href?: string;
    /**
     * Attachment type such as video, picture
     */
    attachmentType?: string;
    /**
     * The actual contents of the attachment object, if embedded, encoded as base64
     */
    content?: string;
    /**
     * A narrative text describing the content of the attachment
     */
    description?: string;
    /**
     * Attachment mime type such as extension file for video, picture and document
     */
    mimeType?: string;
    /**
     * The name of the attachment
     */
    name?: string;
    /**
     * Uniform Resource Locator, is a web page address (a subset of URI)
     */
    url?: string;
    /**
     * The size of the attachment.
     */
    size?: Quantity;
    /**
     * The period of time for which the attachment is valid
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