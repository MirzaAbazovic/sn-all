package de.bitconex.tmf.rcm.client;

import de.bitconex.tmf.rcm.model.Error;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecificationFVO;
import de.bitconex.tmf.rcm.model.ResourceSpecificationMVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.ws.rs.*;

import java.util.List;

/**
 * Resource Catalog Management
 *
 * <p>### February 2023 Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.  ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
 *
 */
@Path("/resourceSpecification")
@Api(value = "/", description = "")
public interface ResourceSpecificationApi  {

    /**
     * Creates a ResourceSpecification
     *
     * This operation creates a ResourceSpecification entity.
     *
     */
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Creates a ResourceSpecification", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK/Created", response = ResourceSpecification.class),
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
    public ResourceSpecification createResourceSpecification(ResourceSpecificationFVO resourceSpecificationFVO, @QueryParam("fields") String fields);

    /**
     * Deletes a ResourceSpecification
     *
     * This operation deletes a ResourceSpecification entity.
     *
     */
    @DELETE
    @Path("/{id}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Deletes a ResourceSpecification", tags={  })
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
    public void deleteResourceSpecification(@PathParam("id") String id);

    /**
     * List or find ResourceSpecification objects
     *
     * List or find ResourceSpecification objects
     *
     */
    @GET
    
    @Produces({ "application/json" })
    @ApiOperation(value = "List or find ResourceSpecification objects", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ResourceSpecification.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public List<ResourceSpecification> listResourceSpecification(@QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

    /**
     * Updates partially a ResourceSpecification
     *
     * This operation updates partially a ResourceSpecification entity.
     *
     */
    @PATCH
    @Path("/{id}")
    @Consumes({ "application/json", "application/merge-patch+json", "application/json-patch+json", "application/json-patch-query+json" })
    @Produces({ "application/json", "application/merge-patch+json", "application/json-patch+json", "application/json-patch-query+json" })
    @ApiOperation(value = "Updates partially a ResourceSpecification", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ResourceSpecification.class),
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
    public ResourceSpecification patchResourceSpecification(@PathParam("id") String id, ResourceSpecificationMVO resourceSpecificationMVO, @QueryParam("fields") String fields);

    /**
     * Retrieves a ResourceSpecification by ID
     *
     * This operation retrieves a ResourceSpecification entity. Attribute selection enabled for all first level attributes.
     *
     */
    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves a ResourceSpecification by ID", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ResourceSpecification.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public ResourceSpecification retrieveResourceSpecification(@PathParam("id") String id, @QueryParam("fields") String fields);
}
