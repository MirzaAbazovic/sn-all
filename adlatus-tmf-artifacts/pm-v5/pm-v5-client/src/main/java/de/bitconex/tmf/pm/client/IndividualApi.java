package de.bitconex.tmf.pm.client;

import de.bitconex.tmf.pm.model.Error;
import de.bitconex.tmf.pm.model.Individual;
import de.bitconex.tmf.pm.model.IndividualFVO;
import de.bitconex.tmf.pm.model.IndividualMVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.ws.rs.*;

import java.util.List;

/**
 * Party Management
 *
 * <p>TMF API Reference : TMF 632 - Party  Release: 22.5 The party API provides standardized mechanism for party management such as creation, update, retrieval, deletion, and notification of events. Party can be an individual or an organization that has any kind of relation with the enterprise. Party is created to record individual or organization information before the assignment of any role. For example, within the context of a split billing mechanism, Party API allows creation of the individual or organization that will play the role of 3rd payer for a given offer and, then, allows consultation or update of his information. Resources - Party (abstract base class with concrete subclasses Individual and Organization) Party API performs the following operations: - Retrieve an organization or an individual - Retrieve a collection of organizations or individuals according to given criteria - Create a new organization or a new individual - Update an existing organization or an existing individual - Delete an existing organization or an existing individual - Notify events on organization or individual
 *
 */
@Path("/individual")
@Api(value = "/", description = "")
public interface IndividualApi  {

    /**
     * Creates a Individual
     *
     * This operation creates a Individual entity.
     *
     */
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Creates a Individual", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK/Created", response = Individual.class),
        @ApiResponse(code = 202, message = "Accepted"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public Individual createIndividual(IndividualFVO individualFVO, @QueryParam("fields") String fields);

    /**
     * Deletes a Individual
     *
     * This operation deletes a Individual entity.
     *
     */
    @DELETE
    @Path("/{id}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Deletes a Individual", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 202, message = "Accepted"),
        @ApiResponse(code = 204, message = "Deleted"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public void deleteIndividual(@PathParam("id") String id);

    /**
     * List or find Individual objects
     *
     * List or find Individual objects
     *
     */
    @GET
    
    @Produces({ "application/json" })
    @ApiOperation(value = "List or find Individual objects", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Individual.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public List<Individual> listIndividual(@QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

    /**
     * Updates partially a Individual
     *
     * This operation updates partially a Individual entity.
     *
     */
    @PATCH
    @Path("/{id}")
    @Consumes({ "application/json", "application/merge-patch+json", "application/json-patch+json", "application/json-patch-query+json" })
    @Produces({ "application/json", "application/merge-patch+json", "application/json-patch+json", "application/json-patch-query+json" })
    @ApiOperation(value = "Updates partially a Individual", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Individual.class),
        @ApiResponse(code = 202, message = "Accepted"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public Individual patchIndividual(@PathParam("id") String id, IndividualMVO individualMVO, @QueryParam("fields") String fields);

    /**
     * Retrieves a Individual by ID
     *
     * This operation retrieves a Individual entity. Attribute selection enabled for all first level attributes.
     *
     */
    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves a Individual by ID", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Individual.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public Individual retrieveIndividual(@PathParam("id") String id, @QueryParam("fields") String fields);
}
