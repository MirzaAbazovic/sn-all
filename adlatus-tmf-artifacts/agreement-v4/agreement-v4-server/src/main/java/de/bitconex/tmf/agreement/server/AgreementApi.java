/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package de.bitconex.tmf.agreement.server;

import de.bitconex.tmf.agreement.model.Agreement;
import de.bitconex.tmf.agreement.model.AgreementCreate;
import de.bitconex.tmf.agreement.model.AgreementUpdate;
import de.bitconex.tmf.agreement.model.Error;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Validated
@Tag(name = "agreement", description = "the agreement API")
public interface AgreementApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /agreement : Creates a Agreement
     * This operation creates a Agreement entity.
     *
     * @param agreement The Agreement to be created (required)
     * @return Created (status code 201)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "createAgreement",
        summary = "Creates a Agreement",
        description = "This operation creates a Agreement entity.",
        tags = { "agreement" },
        responses = {
            @ApiResponse(responseCode = "201", description = "Created", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Agreement.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/agreement",
        produces = { "application/json;charset=utf-8" },
        consumes = { "application/json;charset=utf-8" }
    )
    ResponseEntity<Agreement> createAgreement(
        @Parameter(name = "agreement", description = "The Agreement to be created", required = true) @Valid @RequestBody AgreementCreate agreement
    );


    /**
     * DELETE /agreement/{id} : Deletes a Agreement
     * This operation deletes a Agreement entity.
     *
     * @param id Identifier of the Agreement (required)
     * @return Deleted (status code 204)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "deleteAgreement",
        summary = "Deletes a Agreement",
        description = "This operation deletes a Agreement entity.",
        tags = { "agreement" },
        responses = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/agreement/{id}",
        produces = { "application/json;charset=utf-8" }
    )
    ResponseEntity<Void> deleteAgreement(
        @Parameter(name = "id", description = "Identifier of the Agreement", required = true, in = ParameterIn.PATH) @PathVariable("id") String id
    );


    /**
     * GET /agreement : List or find Agreement objects
     * This operation list or find Agreement entities
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
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "listAgreement",
        summary = "List or find Agreement objects",
        description = "This operation list or find Agreement entities",
        tags = { "agreement" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json;charset=utf-8", array = @ArraySchema(schema = @Schema(implementation = Agreement.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/agreement",
        produces = { "application/json;charset=utf-8" }
    )
    ResponseEntity<List<Agreement>> listAgreement(
        @Parameter(name = "fields", description = "Comma-separated properties to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields,
        @Parameter(name = "offset", description = "Requested index for start of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "offset", required = false) Integer offset,
        @Parameter(name = "limit", description = "Requested number of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "limit", required = false) Integer limit
    );


    /**
     * PATCH /agreement/{id} : Updates partially a Agreement
     * This operation updates partially a Agreement entity.
     *
     * @param id Identifier of the Agreement (required)
     * @param agreement The Agreement to be updated (required)
     * @return Updated (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "patchAgreement",
        summary = "Updates partially a Agreement",
        description = "This operation updates partially a Agreement entity.",
        tags = { "agreement" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Updated", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Agreement.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.PATCH,
        value = "/agreement/{id}",
        produces = { "application/json;charset=utf-8" },
        consumes = { "application/json;charset=utf-8" }
    )
    ResponseEntity<Agreement> patchAgreement(
        @Parameter(name = "id", description = "Identifier of the Agreement", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
        @Parameter(name = "agreement", description = "The Agreement to be updated", required = true) @Valid @RequestBody AgreementUpdate agreement
    );


    /**
     * GET /agreement/{id} : Retrieves a Agreement by ID
     * This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.
     *
     * @param id Identifier of the Agreement (required)
     * @param fields Comma-separated properties to provide in response (optional)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "retrieveAgreement",
        summary = "Retrieves a Agreement by ID",
        description = "This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.",
        tags = { "agreement" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Agreement.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "405", description = "Method Not allowed", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/agreement/{id}",
        produces = { "application/json;charset=utf-8" }
    )
    ResponseEntity<Agreement> retrieveAgreement(
        @Parameter(name = "id", description = "Identifier of the Agreement", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
        @Parameter(name = "fields", description = "Comma-separated properties to provide in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields
    );

}