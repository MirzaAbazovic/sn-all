package de.bitconex.tmf.pm.client;

import de.bitconex.tmf.pm.model.Error;
import de.bitconex.tmf.pm.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * Party Management
 *
 * <p>TMF API Reference : TMF 632 - Party  Release: 22.5 The party API provides standardized mechanism for party management such as creation, update, retrieval, deletion, and notification of events. Party can be an individual or an organization that has any kind of relation with the enterprise. Party is created to record individual or organization information before the assignment of any role. For example, within the context of a split billing mechanism, Party API allows creation of the individual or organization that will play the role of 3rd payer for a given offer and, then, allows consultation or update of his information. Resources - Party (abstract base class with concrete subclasses Individual and Organization) Party API performs the following operations: - Retrieve an organization or an individual - Retrieve a collection of organizations or individuals according to given criteria - Create a new organization or a new individual - Update an existing organization or an existing individual - Delete an existing organization or an existing individual - Notify events on organization or individual
 *
 */
@Path("/listener")
@Api(value = "/", description = "")
public interface NotificationListenerApi  {

    /**
     * Client listener for entity IndividualAttributeValueChangeEvent
     *
     * Example of a client listener for receiving the notification IndividualAttributeValueChangeEvent
     *
     */
    @POST
    @Path("/individualAttributeValueChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity IndividualAttributeValueChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void individualAttributeValueChangeEvent(IndividualAttributeValueChangeEvent individualAttributeValueChangeEvent);

    /**
     * Client listener for entity IndividualCreateEvent
     *
     * Example of a client listener for receiving the notification IndividualCreateEvent
     *
     */
    @POST
    @Path("/individualCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity IndividualCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void individualCreateEvent(IndividualCreateEvent individualCreateEvent);

    /**
     * Client listener for entity IndividualDeleteEvent
     *
     * Example of a client listener for receiving the notification IndividualDeleteEvent
     *
     */
    @POST
    @Path("/individualDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity IndividualDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void individualDeleteEvent(IndividualDeleteEvent individualDeleteEvent);

    /**
     * Client listener for entity IndividualStateChangeEvent
     *
     * Example of a client listener for receiving the notification IndividualStateChangeEvent
     *
     */
    @POST
    @Path("/individualStateChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity IndividualStateChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void individualStateChangeEvent(IndividualStateChangeEvent individualStateChangeEvent);

    /**
     * Client listener for entity OrganizationAttributeValueChangeEvent
     *
     * Example of a client listener for receiving the notification OrganizationAttributeValueChangeEvent
     *
     */
    @POST
    @Path("/organizationAttributeValueChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity OrganizationAttributeValueChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void organizationAttributeValueChangeEvent(OrganizationAttributeValueChangeEvent organizationAttributeValueChangeEvent);

    /**
     * Client listener for entity OrganizationCreateEvent
     *
     * Example of a client listener for receiving the notification OrganizationCreateEvent
     *
     */
    @POST
    @Path("/organizationCreateEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity OrganizationCreateEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void organizationCreateEvent(OrganizationCreateEvent organizationCreateEvent);

    /**
     * Client listener for entity OrganizationDeleteEvent
     *
     * Example of a client listener for receiving the notification OrganizationDeleteEvent
     *
     */
    @POST
    @Path("/organizationDeleteEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity OrganizationDeleteEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void organizationDeleteEvent(OrganizationDeleteEvent organizationDeleteEvent);

    /**
     * Client listener for entity OrganizationStateChangeEvent
     *
     * Example of a client listener for receiving the notification OrganizationStateChangeEvent
     *
     */
    @POST
    @Path("/organizationStateChangeEvent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Client listener for entity OrganizationStateChangeEvent", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Notified"),
        @ApiResponse(code = 200, message = "Error", response = Error.class) })
    public void organizationStateChangeEvent(OrganizationStateChangeEvent organizationStateChangeEvent);
}
