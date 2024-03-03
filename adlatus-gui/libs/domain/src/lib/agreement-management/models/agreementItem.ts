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
import { AgreementTermOrCondition } from './agreementTermOrCondition';
import { ProductOfferingRef } from './productOfferingRef';
import { ProductRef } from './productRef';


/**
 * A part of the agreement expressed in terms of a product offering and possibly including specific terms and conditions.
 */
export interface AgreementItem { 
    /**
     * The list of products indirectly referred by this agreement item (since an agreement item refers primarily to product offerings)
     */
    product?: Array<ProductRef>;
    /**
     * The list of product offerings referred by this agreement item
     */
    productOffering?: Array<ProductOfferingRef>;
    termOrCondition?: Array<AgreementTermOrCondition>;
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