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
import { AgreementSpecCharacteristicValue } from './agreementSpecCharacteristicValue';
import { TimePeriod } from './timePeriod';


/**
 * A characteristic quality or distinctive feature of an agreement.
 */
export interface AgreementSpecCharacteristic { 
    /**
     * If true, the Boolean indicates that the characteristic is configurable
     */
    configurable?: boolean;
    /**
     * A narrative that explains in detail what the characteristic is
     */
    description?: string;
    /**
     * Name of the characteristic being specified.
     */
    name?: string;
    /**
     * A kind of value that the characteristic can take on, such as numeric, text and so forth
     */
    valueType?: string;
    specCharacteristicValue?: Array<AgreementSpecCharacteristicValue>;
    /**
     * The period for which the specification characteristic is valid
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