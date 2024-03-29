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
 * A period of time, either as a deadline (endDateTime only) a startDateTime only, or both
 */
export interface TimePeriod { 
    /**
     * End of the time period, using IETC-RFC-3339 format
     */
    endDateTime?: Date;
    /**
     * Start of the time period, using IETC-RFC-3339 format. If you define a start, you must also define an end
     */
    startDateTime?: Date;
}
