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
import { Any } from './any';


/**
 * Describes a given characteristic of an object or entity through a name/value pair.
 */
export interface Characteristic { 
    /**
     * Name of the characteristic
     */
    name: string;
    /**
     * Data type of the value of the characteristic
     */
    valueType?: string;
    /**
     * The value of the characteristic
     */
    value: Any;
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