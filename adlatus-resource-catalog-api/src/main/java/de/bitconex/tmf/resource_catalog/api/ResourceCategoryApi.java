/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package de.bitconex.tmf.resource_catalog.api;

import de.bitconex.tmf.resource_catalog.model.Error;
import de.bitconex.tmf.resource_catalog.model.ResourceCategory;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryUpdate;
import de.bitconex.tmf.resource_catalog.utility.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Validated
@Tag(name = "resourceCategory", description = "the resourceCategory API")
public interface ResourceCategoryApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /resourceCategory : Creates a ResourceCategory
     * This operation creates a ResourceCategory entity.
     *
     * @param resourceCategory The ResourceCategory to be created (required)
     * @return Created (status code 201)
     * or Bad Request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Method Not allowed (status code 405)
     * or Conflict (status code 409)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "createResourceCategory",
            summary = "Creates a ResourceCategory",
            description = "This operation creates a ResourceCategory entity.",
            tags = {"resourceCategory"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = {
                            @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = ResourceCategory.class))
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
            value = "/resourceCategory",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<ResourceCategory> createResourceCategory(
            @Parameter(name = "resourceCategory", description = "The ResourceCategory to be created", required = true) @Valid @RequestBody ResourceCategoryCreate resourceCategory
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json;charset=utf-8"))) {
                    String exampleString = "{ \"isRoot\" : true, \"lifecycleStatus\" : \"lifecycleStatus\", \"validFor\" : { \"startDateTime\" : \"1985-04-12T23:20:50.52Z\", \"endDateTime\" : \"1985-04-12T23:20:50.52Z\" }, \"@type\" : \"@type\", \"description\" : \"description\", \"relatedParty\" : [ { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" }, { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" } ], \"version\" : \"version\", \"resourceCandidate\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"parentId\" : \"parentId\", \"@baseType\" : \"@baseType\", \"lastUpdate\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"category\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"@schemaLocation\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json;charset=utf-8", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /resourceCategory/{id} : Deletes a ResourceCategory
     * This operation deletes a ResourceCategory entity.
     *
     * @param id Identifier of the ResourceCategory (required)
     * @return Deleted (status code 204)
     * or Bad Request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     * or Method Not allowed (status code 405)
     * or Conflict (status code 409)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "deleteResourceCategory",
            summary = "Deletes a ResourceCategory",
            description = "This operation deletes a ResourceCategory entity.",
            tags = {"resourceCategory"},
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
            value = "/resourceCategory/{id}",
            produces = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<Void> deleteResourceCategory(
            @Parameter(name = "id", description = "Identifier of the ResourceCategory", required = true, in = ParameterIn.PATH) @PathVariable("id") String id
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /resourceCategory : List or find ResourceCategory objects
     * This operation list or find ResourceCategory entities
     *
     * @param fields Comma-separated properties to be provided in response (optional)
     * @param offset Requested index for start of resources to be provided in response (optional)
     * @param limit  Requested number of resources to be provided in response (optional)
     * @return Success (status code 200)
     * or Bad Request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     * or Method Not allowed (status code 405)
     * or Conflict (status code 409)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "listResourceCategory",
            summary = "List or find ResourceCategory objects",
            description = "This operation list or find ResourceCategory entities",
            tags = {"resourceCategory"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=utf-8", array = @ArraySchema(schema = @Schema(implementation = ResourceCategory.class)))
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
            value = "/resourceCategory",
            produces = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<List<ResourceCategory>> listResourceCategory(
            @Parameter(name = "fields", description = "Comma-separated properties to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields,
            @Parameter(name = "offset", description = "Requested index for start of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "offset", required = false) Integer offset,
            @Parameter(name = "limit", description = "Requested number of resources to be provided in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(required = false) Map<String, String> allParams
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json;charset=utf-8"))) {
                    String exampleString = "[ { \"isRoot\" : true, \"lifecycleStatus\" : \"lifecycleStatus\", \"validFor\" : { \"startDateTime\" : \"1985-04-12T23:20:50.52Z\", \"endDateTime\" : \"1985-04-12T23:20:50.52Z\" }, \"@type\" : \"@type\", \"description\" : \"description\", \"relatedParty\" : [ { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" }, { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" } ], \"version\" : \"version\", \"resourceCandidate\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"parentId\" : \"parentId\", \"@baseType\" : \"@baseType\", \"lastUpdate\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"category\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"@schemaLocation\" : \"https://openapi-generator.tech\" }, { \"isRoot\" : true, \"lifecycleStatus\" : \"lifecycleStatus\", \"validFor\" : { \"startDateTime\" : \"1985-04-12T23:20:50.52Z\", \"endDateTime\" : \"1985-04-12T23:20:50.52Z\" }, \"@type\" : \"@type\", \"description\" : \"description\", \"relatedParty\" : [ { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" }, { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" } ], \"version\" : \"version\", \"resourceCandidate\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"parentId\" : \"parentId\", \"@baseType\" : \"@baseType\", \"lastUpdate\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"category\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"@schemaLocation\" : \"https://openapi-generator.tech\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json;charset=utf-8", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PATCH /resourceCategory/{id} : Updates partially a ResourceCategory
     * This operation updates partially a ResourceCategory entity.
     *
     * @param id               Identifier of the ResourceCategory (required)
     * @param resourceCategory The ResourceCategory to be updated (required)
     * @return Updated (status code 200)
     * or Bad Request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     * or Method Not allowed (status code 405)
     * or Conflict (status code 409)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "patchResourceCategory",
            summary = "Updates partially a ResourceCategory",
            description = "This operation updates partially a ResourceCategory entity.",
            tags = {"resourceCategory"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated", content = {
                            @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = ResourceCategory.class))
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
            value = "/resourceCategory/{id}",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<ResourceCategory> patchResourceCategory(
            @Parameter(name = "id", description = "Identifier of the ResourceCategory", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
            @Parameter(name = "resourceCategory", description = "The ResourceCategory to be updated", required = true) @Valid @RequestBody ResourceCategoryUpdate resourceCategory
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json;charset=utf-8"))) {
                    String exampleString = "{ \"isRoot\" : true, \"lifecycleStatus\" : \"lifecycleStatus\", \"validFor\" : { \"startDateTime\" : \"1985-04-12T23:20:50.52Z\", \"endDateTime\" : \"1985-04-12T23:20:50.52Z\" }, \"@type\" : \"@type\", \"description\" : \"description\", \"relatedParty\" : [ { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" }, { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" } ], \"version\" : \"version\", \"resourceCandidate\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"parentId\" : \"parentId\", \"@baseType\" : \"@baseType\", \"lastUpdate\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"category\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"@schemaLocation\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json;charset=utf-8", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /resourceCategory/{id} : Retrieves a ResourceCategory by ID
     * This operation retrieves a ResourceCategory entity. Attribute selection is enabled for all first level attributes.
     *
     * @param id     Identifier of the ResourceCategory (required)
     * @param fields Comma-separated properties to provide in response (optional)
     * @return Success (status code 200)
     * or Bad Request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     * or Method Not allowed (status code 405)
     * or Conflict (status code 409)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "retrieveResourceCategory",
            summary = "Retrieves a ResourceCategory by ID",
            description = "This operation retrieves a ResourceCategory entity. Attribute selection is enabled for all first level attributes.",
            tags = {"resourceCategory"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = ResourceCategory.class))
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
            value = "/resourceCategory/{id}",
            produces = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<ResourceCategory> retrieveResourceCategory(
            @Parameter(name = "id", description = "Identifier of the ResourceCategory", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
            @Parameter(name = "fields", description = "Comma-separated properties to provide in response", in = ParameterIn.QUERY) @Valid @RequestParam(value = "fields", required = false) String fields
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json;charset=utf-8"))) {
                    String exampleString = "{ \"isRoot\" : true, \"lifecycleStatus\" : \"lifecycleStatus\", \"validFor\" : { \"startDateTime\" : \"1985-04-12T23:20:50.52Z\", \"endDateTime\" : \"1985-04-12T23:20:50.52Z\" }, \"@type\" : \"@type\", \"description\" : \"description\", \"relatedParty\" : [ { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" }, { \"@referredType\" : \"@referredType\", \"role\" : \"role\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\" } ], \"version\" : \"version\", \"resourceCandidate\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"parentId\" : \"parentId\", \"@baseType\" : \"@baseType\", \"lastUpdate\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"category\" : [ { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" }, { \"@referredType\" : \"@referredType\", \"@baseType\" : \"@baseType\", \"@type\" : \"@type\", \"name\" : \"name\", \"id\" : \"id\", \"href\" : \"https://openapi-generator.tech\", \"@schemaLocation\" : \"https://openapi-generator.tech\", \"version\" : \"version\" } ], \"@schemaLocation\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json;charset=utf-8", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}