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

/**
 * Attachment reference. An attachment complements the description of an element (for instance a product) through video, pictures
 */
export interface AttachmentRef { 
    /**
     * Unique-Identifier for this attachment
     */
    id: string;
    /**
     * URL serving as reference for the attachment resource
     */
    href?: string;
    /**
     * A narrative text describing the content of the attachment
     */
    description?: string;
    /**
     * Name of the related entity.
     */
    name?: string;
    /**
     * Link to the attachment media/content
     */
    url?: string;
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