/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.33).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package de.bitconex.agreement.api;

import de.bitconex.agreement.model.Agreement;
import de.bitconex.agreement.model.AgreementCreate;
import de.bitconex.agreement.model.AgreementUpdate;
import de.bitconex.agreement.model.Error;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")
@Validated
@Api(value = "agreement", description = "the agreement API")
@RequestMapping(value = "/tmf-api/agreementManagement/v4/")
public interface AgreementApi {

    @ApiOperation(value = "Creates a Agreement", nickname = "createAgreement", notes = "This operation creates a Agreement entity.", response = Agreement.class, tags={ "agreement", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Created", response = Agreement.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/agreement",
        produces = { "application/json;charset=utf-8" }, 
        consumes = { "application/json;charset=utf-8" },
        method = RequestMethod.POST)
    ResponseEntity<Agreement> createAgreement(@ApiParam(value = "The Agreement to be created" ,required=true )  @Valid @RequestBody AgreementCreate agreement);


    @ApiOperation(value = "Deletes a Agreement", nickname = "deleteAgreement", notes = "This operation deletes a Agreement entity.", tags={ "agreement", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Deleted"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/agreement/{id}",
        produces = { "application/json;charset=utf-8" }, 
        consumes = { "application/json;charset=utf-8" },
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteAgreement(@ApiParam(value = "Identifier of the Agreement",required=true) @PathVariable("id") String id);


    @ApiOperation(value = "List or find Agreement objects", nickname = "listAgreement", notes = "This operation list or find Agreement entities", response = Agreement.class, responseContainer = "List", tags={ "agreement", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Agreement.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/agreement",
        produces = { "application/json;charset=utf-8" }, 
        consumes = { "application/json;charset=utf-8" },
        method = RequestMethod.GET)
    ResponseEntity<List<Agreement>> listAgreement(@ApiParam(value = "Comma-separated properties to be provided in response") @Valid @RequestParam(value = "fields", required = false) String fields,@ApiParam(value = "Requested index for start of resources to be provided in response") @Valid @RequestParam(value = "offset", required = false) Integer offset,@ApiParam(value = "Requested number of resources to be provided in response") @Valid @RequestParam(value = "limit", required = false) Integer limit);


    @ApiOperation(value = "Updates partially a Agreement", nickname = "patchAgreement", notes = "This operation updates partially a Agreement entity.", response = Agreement.class, tags={ "agreement", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Updated", response = Agreement.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/agreement/{id}",
        produces = { "application/json;charset=utf-8" }, 
        consumes = { "application/json;charset=utf-8" },
        method = RequestMethod.PATCH)
    ResponseEntity<Agreement> patchAgreement(@ApiParam(value = "Identifier of the Agreement",required=true) @PathVariable("id") String id,@ApiParam(value = "The Agreement to be updated" ,required=true )  @Valid @RequestBody AgreementUpdate agreement);


    @ApiOperation(value = "Retrieves a Agreement by ID", nickname = "retrieveAgreement", notes = "This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.", response = Agreement.class, tags={ "agreement", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Agreement.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/agreement/{id}",
        produces = { "application/json;charset=utf-8" }, 
        consumes = { "application/json;charset=utf-8" },
        method = RequestMethod.GET)
    ResponseEntity<Agreement> retrieveAgreement(@ApiParam(value = "Identifier of the Agreement",required=true) @PathVariable("id") String id,@ApiParam(value = "Comma-separated properties to provide in response") @Valid @RequestParam(value = "fields", required = false) String fields);

}
