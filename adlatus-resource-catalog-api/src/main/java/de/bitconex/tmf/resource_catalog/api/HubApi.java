/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package de.bitconex.tmf.resource_catalog.api;

import de.bitconex.tmf.resource_catalog.model.Error;
import de.bitconex.tmf.resource_catalog.model.EventSubscription;
import de.bitconex.tmf.resource_catalog.model.EventSubscriptionInput;
import de.bitconex.tmf.resource_catalog.utility.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import javax.validation.Valid;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Validated
@Tag(name = "events subscription", description = "the events subscription API")
public interface HubApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /hub : Register a listener
     * Sets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.
     *
     * @param data Data containing the callback endpoint to deliver the information (required)
     * @return Subscribed (status code 201)
     * or Bad Request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     * or Method Not allowed (status code 405)
     * or Conflict (status code 409)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "registerListener",
            summary = "Register a listener",
            description = "Sets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.",
            tags = {"events subscription"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Subscribed", content = {
                            @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = EventSubscription.class))
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
            method = RequestMethod.POST,
            value = "/hub",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<EventSubscription> registerListener(
            @Parameter(name = "data", description = "Data containing the callback endpoint to deliver the information", required = true) @Valid @RequestBody EventSubscriptionInput data
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json;charset=utf-8"))) {
                    String exampleString = "{ \"query\" : \"query\", \"callback\" : \"callback\", \"id\" : \"id\" }";
                    ApiUtil.setExampleResponse(request, "application/json;charset=utf-8", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /hub/{id} : Unregister a listener
     * Resets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.
     *
     * @param id The id of the registered listener (required)
     * @return Deleted (status code 204)
     * or Bad request (status code 400)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     * or Method not allowed (status code 405)
     * or Internal Server Error (status code 500)
     */
    @Operation(
            operationId = "unregisterListener",
            summary = "Unregister a listener",
            description = "Resets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.",
            tags = {"events subscription"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
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
                    @ApiResponse(responseCode = "405", description = "Method not allowed", content = {
                            @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                            @Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/hub/{id}",
            produces = {"application/json;charset=utf-8"}
    )
    default ResponseEntity<Void> unregisterListener(
            @Parameter(name = "id", description = "The id of the registered listener", required = true, in = ParameterIn.PATH) @PathVariable("id") String id
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
