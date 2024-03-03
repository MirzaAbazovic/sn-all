package de.bitconex.tmf.rcm.client;

import de.bitconex.tmf.rcm.model.Error;
import de.bitconex.tmf.rcm.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * Resource Catalog Management
 *
 * <p>### February 2023 Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.  ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
 *
 */
@Path("/listener")
@Api(value = "/", description = "")
public interface NotificationListenerApi  {

    /**
     * Client listener for entity ExportJobCreateEvent
     *
     * Example of a client listener for receiving the notification ExportJobCreateEvent
     *
     */
    @POST
    @Path("/exportJobCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ExportJobCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void exportJobCreateEvent(ExportJobCreateEvent exportJobCreateEvent);

    /**
     * Client listener for entity ExportJobDeleteEvent
     *
     * Example of a client listener for receiving the notification ExportJobDeleteEvent
     *
     */
    @POST
    @Path("/exportJobDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ExportJobDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void exportJobDeleteEvent(ExportJobDeleteEvent exportJobDeleteEvent);

    /**
     * Client listener for entity ExportJobStateChangeEvent
     *
     * Example of a client listener for receiving the notification ExportJobStateChangeEvent
     *
     */
    @POST
    @Path("/exportJobStateChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ExportJobStateChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void exportJobStateChangeEvent(ExportJobStateChangeEvent exportJobStateChangeEvent);

    /**
     * Client listener for entity ImportJobCreateEvent
     *
     * Example of a client listener for receiving the notification ImportJobCreateEvent
     *
     */
    @POST
    @Path("/importJobCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ImportJobCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void importJobCreateEvent(ImportJobCreateEvent importJobCreateEvent);

    /**
     * Client listener for entity ImportJobDeleteEvent
     *
     * Example of a client listener for receiving the notification ImportJobDeleteEvent
     *
     */
    @POST
    @Path("/importJobDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ImportJobDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void importJobDeleteEvent(ImportJobDeleteEvent importJobDeleteEvent);

    /**
     * Client listener for entity ImportJobStateChangeEvent
     *
     * Example of a client listener for receiving the notification ImportJobStateChangeEvent
     *
     */
    @POST
    @Path("/importJobStateChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ImportJobStateChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void importJobStateChangeEvent(ImportJobStateChangeEvent importJobStateChangeEvent);

    /**
     * Client listener for entity ResourceCandidateAttributeValueChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceCandidateAttributeValueChangeEvent
     *
     */
    @POST
    @Path("/resourceCandidateAttributeValueChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCandidateAttributeValueChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCandidateAttributeValueChangeEvent(ResourceCandidateAttributeValueChangeEvent resourceCandidateAttributeValueChangeEvent);

    /**
     * Client listener for entity ResourceCandidateCreateEvent
     *
     * Example of a client listener for receiving the notification ResourceCandidateCreateEvent
     *
     */
    @POST
    @Path("/resourceCandidateCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCandidateCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCandidateCreateEvent(ResourceCandidateCreateEvent resourceCandidateCreateEvent);

    /**
     * Client listener for entity ResourceCandidateDeleteEvent
     *
     * Example of a client listener for receiving the notification ResourceCandidateDeleteEvent
     *
     */
    @POST
    @Path("/resourceCandidateDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCandidateDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCandidateDeleteEvent(ResourceCandidateDeleteEvent resourceCandidateDeleteEvent);

    /**
     * Client listener for entity ResourceCandidateStatusChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceCandidateStatusChangeEvent
     *
     */
    @POST
    @Path("/resourceCandidateStatusChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCandidateStatusChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCandidateStatusChangeEvent(ResourceCandidateStatusChangeEvent resourceCandidateStatusChangeEvent);

    /**
     * Client listener for entity ResourceCatalogAttributeValueChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceCatalogAttributeValueChangeEvent
     *
     */
    @POST
    @Path("/resourceCatalogAttributeValueChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCatalogAttributeValueChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCatalogAttributeValueChangeEvent(ResourceCatalogAttributeValueChangeEvent resourceCatalogAttributeValueChangeEvent);

    /**
     * Client listener for entity ResourceCatalogCreateEvent
     *
     * Example of a client listener for receiving the notification ResourceCatalogCreateEvent
     *
     */
    @POST
    @Path("/resourceCatalogCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCatalogCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCatalogCreateEvent(ResourceCatalogCreateEvent resourceCatalogCreateEvent);

    /**
     * Client listener for entity ResourceCatalogDeleteEvent
     *
     * Example of a client listener for receiving the notification ResourceCatalogDeleteEvent
     *
     */
    @POST
    @Path("/resourceCatalogDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCatalogDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCatalogDeleteEvent(ResourceCatalogDeleteEvent resourceCatalogDeleteEvent);

    /**
     * Client listener for entity ResourceCatalogStatusChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceCatalogStatusChangeEvent
     *
     */
    @POST
    @Path("/resourceCatalogStatusChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCatalogStatusChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCatalogStatusChangeEvent(ResourceCatalogStatusChangeEvent resourceCatalogStatusChangeEvent);

    /**
     * Client listener for entity ResourceCategoryAttributeValueChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceCategoryAttributeValueChangeEvent
     *
     */
    @POST
    @Path("/resourceCategoryAttributeValueChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCategoryAttributeValueChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCategoryAttributeValueChangeEvent(ResourceCategoryAttributeValueChangeEvent resourceCategoryAttributeValueChangeEvent);

    /**
     * Client listener for entity ResourceCategoryCreateEvent
     *
     * Example of a client listener for receiving the notification ResourceCategoryCreateEvent
     *
     */
    @POST
    @Path("/resourceCategoryCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCategoryCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCategoryCreateEvent(ResourceCategoryCreateEvent resourceCategoryCreateEvent);

    /**
     * Client listener for entity ResourceCategoryDeleteEvent
     *
     * Example of a client listener for receiving the notification ResourceCategoryDeleteEvent
     *
     */
    @POST
    @Path("/resourceCategoryDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCategoryDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCategoryDeleteEvent(ResourceCategoryDeleteEvent resourceCategoryDeleteEvent);

    /**
     * Client listener for entity ResourceCategoryStatusChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceCategoryStatusChangeEvent
     *
     */
    @POST
    @Path("/resourceCategoryStatusChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceCategoryStatusChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceCategoryStatusChangeEvent(ResourceCategoryStatusChangeEvent resourceCategoryStatusChangeEvent);

    /**
     * Client listener for entity ResourceSpecificationAttributeValueChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceSpecificationAttributeValueChangeEvent
     *
     */
    @POST
    @Path("/resourceSpecificationAttributeValueChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceSpecificationAttributeValueChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceSpecificationAttributeValueChangeEvent(ResourceSpecificationAttributeValueChangeEvent resourceSpecificationAttributeValueChangeEvent);

    /**
     * Client listener for entity ResourceSpecificationCreateEvent
     *
     * Example of a client listener for receiving the notification ResourceSpecificationCreateEvent
     *
     */
    @POST
    @Path("/resourceSpecificationCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceSpecificationCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceSpecificationCreateEvent(ResourceSpecificationCreateEvent resourceSpecificationCreateEvent);

    /**
     * Client listener for entity ResourceSpecificationDeleteEvent
     *
     * Example of a client listener for receiving the notification ResourceSpecificationDeleteEvent
     *
     */
    @POST
    @Path("/resourceSpecificationDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceSpecificationDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceSpecificationDeleteEvent(ResourceSpecificationDeleteEvent resourceSpecificationDeleteEvent);

    /**
     * Client listener for entity ResourceSpecificationStatusChangeEvent
     *
     * Example of a client listener for receiving the notification ResourceSpecificationStatusChangeEvent
     *
     */
    @POST
    @Path("/resourceSpecificationStatusChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity ResourceSpecificationStatusChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void resourceSpecificationStatusChangeEvent(ResourceSpecificationStatusChangeEvent resourceSpecificationStatusChangeEvent);
}
