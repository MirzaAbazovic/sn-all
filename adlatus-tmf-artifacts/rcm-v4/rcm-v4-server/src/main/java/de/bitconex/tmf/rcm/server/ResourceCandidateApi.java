/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package de.bitconex.tmf.rcm.server;

import de.bitconex.tmf.rcm.model.Error;
import de.bitconex.tmf.rcm.model.ResourceCandidate;
import de.bitconex.tmf.rcm.model.ResourceCandidateCreate;
import de.bitconex.tmf.rcm.model.ResourceCandidateUpdate;
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
@Tag(name = "resourceCandidate", description = "the resourceCandidate API")
public interface ResourceCandidateApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /resourceCandidate : Creates a ResourceCandidate
     * This operation creates a ResourceCandidate entity.
     *
     * @param resourceCandidate The ResourceCandidate to be created (required)
     * @return Created (status code 201)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Method Not allowed (status code 405)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "createResourceCandidate",
        summary = "Creates a ResourceCandidate",
        description = "This operation creates a ResourceCandidate entity.",
        tags = { "resourceCandidate" },
        responses = {
            @ApiResponse(responseCode = "201", description = "Created", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = ResourceCandidate.class))
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
        value = "/resourceCandidate",
        produces = { "application/json;charset=utf-8" },
        consumes = { "application/json;charset=utf-8" }
    )
    ResponseEntity<ResourceCandidate> createResourceCandidate(
        @Parameter(name = "resourceCandidate", description = "The ResourceCandidate to be created", required = true) @Valid @RequestBody ResourceCandidateCreate resourceCandidate
    );


    /**
     * DELETE /resourceCandidate/{id} : Deletes a ResourceCandidate
     * This operation deletes a ResourceCandidate entity.
     *
     * @param id Identifier of the ResourceCandidate (required)
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
        operationId = "deleteResourceCandidate",
        summary = "Deletes a ResourceCandidate",
        description = "This operation deletes a ResourceCandidate entity.",
        tags = { "resourceCandidate" },
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
        value = "/resourceCandidate/{id}",
        produces = { "application/json;charset=utf-8" }
    )
    ResponseEntity<Void> deleteResourceCandidate(
        @Parameter(name = "id", description = "Identifier of the ResourceCandidate", required = true, in = ParameterIn.PATH) @PathVariable("id") String id
    );


    /**
     * GET /resourceCandidate : List or find ResourceCandidate objects
     * This operation list or find ResourceCandidate entities
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
        operationId = "listResourceCandidate",
        summary = "List or find ResourceCandidate objects",
        description = "This operation list or find ResourceCandidate entities",
        tags = { "resourceCandidate" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json;charset=utf-8", array = @ArraySchema(schema = @Schema(implementation = ResourceCandidate.class)))
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
        value = "/resourceCandidate",
        produces = { "application/json;charset=utf-8" }
    )
    ResponseEntity<List<ResourceCandidate>> listResourceCandidate(
        @Parameter(name = "fields", description = "Comma-separated properties to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields,
        @Parameter(name = "offset", description = "Requested index for start of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "offset", required = false) Integer offset,
        @Parameter(name = "limit", description = "Requested number of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "limit", required = false) Integer limit
    );


    /**
     * PATCH /resourceCandidate/{id} : Updates partially a ResourceCandidate
     * This operation updates partially a ResourceCandidate entity.
     *
     * @param id Identifier of the ResourceCandidate (required)
     * @param resourceCandidate The ResourceCandidate to be updated (required)
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
        operationId = "patchResourceCandidate",
        summary = "Updates partially a ResourceCandidate",
        description = "This operation updates partially a ResourceCandidate entity.",
        tags = { "resourceCandidate" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Updated", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = ResourceCandidate.class))
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
        value = "/resourceCandidate/{id}",
        produces = { "application/json;charset=utf-8" },
        consumes = { "application/json;charset=utf-8" }
    )
    ResponseEntity<ResourceCandidate> patchResourceCandidate(
        @Parameter(name = "id", description = "Identifier of the ResourceCandidate", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
        @Parameter(name = "resourceCandidate", description = "The ResourceCandidate to be updated", required = true) @Valid @RequestBody ResourceCandidateUpdate resourceCandidate
    );


    /**
     * GET /resourceCandidate/{id} : Retrieves a ResourceCandidate by ID
     * This operation retrieves a ResourceCandidate entity. Attribute selection is enabled for all first level attributes.
     *
     * @param id Identifier of the ResourceCandidate (required)
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
        operationId = "retrieveResourceCandidate",
        summary = "Retrieves a ResourceCandidate by ID",
        description = "This operation retrieves a ResourceCandidate entity. Attribute selection is enabled for all first level attributes.",
        tags = { "resourceCandidate" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = ResourceCandidate.class))
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
        value = "/resourceCandidate/{id}",
        produces = { "application/json;charset=utf-8" }
    )
    ResponseEntity<ResourceCandidate> retrieveResourceCandidate(
        @Parameter(name = "id", description = "Identifier of the ResourceCandidate", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
        @Parameter(name = "fields", description = "Comma-separated properties to provide in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields
    );

}