package de.bitconex.tmf.rcm.client;

import de.bitconex.tmf.rcm.model.Error;
import de.bitconex.tmf.rcm.model.ExportJob;
import de.bitconex.tmf.rcm.model.ExportJobFVO;
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
@Path("/exportJob")
@Api(value = "/", description = "")
public interface ExportJobApi  {

    /**
     * Creates a ExportJob
     *
     * This operation creates a ExportJob entity.
     *
     */
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Creates a ExportJob", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK/Created", response = ExportJob.class),
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
    public ExportJob createExportJob(ExportJobFVO exportJobFVO, @QueryParam("fields") String fields);

    /**
     * Deletes a ExportJob
     *
     * This operation deletes a ExportJob entity.
     *
     */
    @DELETE
    @Path("/{id}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Deletes a ExportJob", tags={  })
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
    public void deleteExportJob(@PathParam("id") String id);

    /**
     * List or find ExportJob objects
     *
     * List or find ExportJob objects
     *
     */
    @GET
    
    @Produces({ "application/json" })
    @ApiOperation(value = "List or find ExportJob objects", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ExportJob.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public List<ExportJob> listExportJob(@QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

    /**
     * Retrieves a ExportJob by ID
     *
     * This operation retrieves a ExportJob entity. Attribute selection enabled for all first level attributes.
     *
     */
    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves a ExportJob by ID", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ExportJob.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class),
        @ApiResponse(code = 501, message = "Not Implemented", response = Error.class),
        @ApiResponse(code = 503, message = "Service Unavailable", response = Error.class) })
    public ExportJob retrieveExportJob(@PathParam("id") String id, @QueryParam("fields") String fields);
}
