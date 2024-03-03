/**
 * Agreement Management
 * This is Swagger UI environment generated for the TMF Agreement Management specification
 *
 * OpenAPI spec version: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


/**
 * A business participant that is responsible for approving the agreement.
 */
export interface AgreementAuthorization { 
    /**
     * The date associated with the authorization state.
     */
    date?: Date;
    /**
     * Indication that represents whether the signature is a physical paper signature or a digital signature.
     */
    signatureRepresentation?: string;
    /**
     * Current status of the authorization, for example in process, approved, rejected.
     */
    state?: string;
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
