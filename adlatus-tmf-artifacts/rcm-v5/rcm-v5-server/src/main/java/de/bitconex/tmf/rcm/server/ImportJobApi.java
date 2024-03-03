/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package de.bitconex.tmf.rcm.server;

import de.bitconex.tmf.rcm.model.Error;
import de.bitconex.tmf.rcm.model.ImportJob;
import de.bitconex.tmf.rcm.model.ImportJobFVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;


@Validated
@Tag(name = "importJob", description = "Operations for ImportJob Resource")
public interface ImportJobApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /importJob : Creates a ImportJob
     * This operation creates a ImportJob entity.
     *
     * @param importJobFVO The ImportJob to be created (required)
     * @param fields Comma-separated properties to be provided in response (optional)
     * @return OK/Created (status code 201)
     *         or Accepted (status code 202)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     *         or Not Implemented (status code 501)
     *         or Service Unavailable (status code 503)
     */
    @Operation(
        operationId = "createImportJob",
        summary = "Creates a ImportJob",
        description = "This operation creates a ImportJob entity.",
        tags = { "importJob" },
        responses = {
            @ApiResponse(responseCode = "201", description = "OK/Created", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ImportJob.class))
            }),
            @ApiResponse(responseCode = "202", description = "Accepted"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "501", description = "Not Implemented", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "503", description = "Service Unavailable", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/importJob",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    ResponseEntity<ImportJob> createImportJob(
        @Parameter(name = "ImportJobFVO", description = "The ImportJob to be created", required = true) @Valid @RequestBody ImportJobFVO importJobFVO,
        @Parameter(name = "fields", description = "Comma-separated properties to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields
    );


    /**
     * DELETE /importJob/{id} : Deletes a ImportJob
     * This operation deletes a ImportJob entity.
     *
     * @param id Identifier of the Resource (required)
     * @return Accepted (status code 202)
     *         or Deleted (status code 204)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     *         or Not Implemented (status code 501)
     *         or Service Unavailable (status code 503)
     */
    @Operation(
        operationId = "deleteImportJob",
        summary = "Deletes a ImportJob",
        description = "This operation deletes a ImportJob entity.",
        tags = { "importJob" },
        responses = {
            @ApiResponse(responseCode = "202", description = "Accepted"),
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "501", description = "Not Implemented", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "503", description = "Service Unavailable", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/importJob/{id}",
        produces = { "application/json" }
    )
    ResponseEntity<Void> deleteImportJob(
        @Parameter(name = "id", description = "Identifier of the Resource", required = true, in = ParameterIn.PATH) @PathVariable("id") String id
    );


    /**
     * GET /importJob : List or find ImportJob objects
     * List or find ImportJob objects
     *
     * @param fields Comma-separated properties to be provided in response (optional)
     * @param offset Requested index for start of resources to be provided in response (optional)
     * @param limit Requested number of resources to be provided in response (optional)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Internal Server Error (status code 500)
     *         or Not Implemented (status code 501)
     *         or Service Unavailable (status code 503)
     */
    @Operation(
        operationId = "listImportJob",
        summary = "List or find ImportJob objects",
        description = "List or find ImportJob objects",
        tags = { "importJob" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ImportJob.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "501", description = "Not Implemented", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "503", description = "Service Unavailable", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/importJob",
        produces = { "application/json" }
    )
    ResponseEntity<List<ImportJob>> listImportJob(
        @Parameter(name = "fields", description = "Comma-separated properties to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields,
        @Parameter(name = "offset", description = "Requested index for start of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "offset", required = false) Integer offset,
        @Parameter(name = "limit", description = "Requested number of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "limit", required = false) Integer limit
    );


    /**
     * GET /importJob/{id} : Retrieves a ImportJob by ID
     * This operation retrieves a ImportJob entity. Attribute selection enabled for all first level attributes.
     *
     * @param id Identifier of the Resource (required)
     * @param fields Comma-separated properties to be provided in response (optional)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Internal Server Error (status code 500)
     *         or Not Implemented (status code 501)
     *         or Service Unavailable (status code 503)
     */
    @Operation(
        operationId = "retrieveImportJob",
        summary = "Retrieves a ImportJob by ID",
        description = "This operation retrieves a ImportJob entity. Attribute selection enabled for all first level attributes.",
        tags = { "importJob" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ImportJob.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "501", description = "Not Implemented", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "503", description = "Service Unavailable", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/importJob/{id}",
        produces = { "application/json" }
    )
    ResponseEntity<ImportJob> retrieveImportJob(
        @Parameter(name = "id", description = "Identifier of the Resource", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
        @Parameter(name = "fields", description = "Comma-separated properties to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields
    );

}
