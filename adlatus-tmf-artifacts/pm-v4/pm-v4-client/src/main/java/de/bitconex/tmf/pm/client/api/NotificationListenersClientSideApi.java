package de.bitconex.tmf.pm.client.api;

import de.bitconex.tmf.pm.client.ApiClient;
import de.bitconex.tmf.pm.model.*;
import feign.*;
import de.bitconex.tmf.pm.client.ApiResponse;


public interface NotificationListenersClientSideApi extends ApiClient.Api {


  /**
   * Client listener for entity IndividualAttributeValueChangeEvent
   * Example of a client listener for receiving the notification IndividualAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/individualAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToIndividualAttributeValueChangeEvent(IndividualAttributeValueChangeEvent data);

  /**
   * Client listener for entity IndividualAttributeValueChangeEvent
   * Similar to <code>listenToIndividualAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification IndividualAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/individualAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToIndividualAttributeValueChangeEventWithHttpInfo(IndividualAttributeValueChangeEvent data);



  /**
   * Client listener for entity IndividualCreateEvent
   * Example of a client listener for receiving the notification IndividualCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/individualCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToIndividualCreateEvent(IndividualCreateEvent data);

  /**
   * Client listener for entity IndividualCreateEvent
   * Similar to <code>listenToIndividualCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification IndividualCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/individualCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToIndividualCreateEventWithHttpInfo(IndividualCreateEvent data);



  /**
   * Client listener for entity IndividualDeleteEvent
   * Example of a client listener for receiving the notification IndividualDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/individualDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToIndividualDeleteEvent(IndividualDeleteEvent data);

  /**
   * Client listener for entity IndividualDeleteEvent
   * Similar to <code>listenToIndividualDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification IndividualDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/individualDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToIndividualDeleteEventWithHttpInfo(IndividualDeleteEvent data);



  /**
   * Client listener for entity IndividualStateChangeEvent
   * Example of a client listener for receiving the notification IndividualStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/individualStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToIndividualStateChangeEvent(IndividualStateChangeEvent data);

  /**
   * Client listener for entity IndividualStateChangeEvent
   * Similar to <code>listenToIndividualStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification IndividualStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/individualStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToIndividualStateChangeEventWithHttpInfo(IndividualStateChangeEvent data);



  /**
   * Client listener for entity OrganizationAttributeValueChangeEvent
   * Example of a client listener for receiving the notification OrganizationAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/organizationAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToOrganizationAttributeValueChangeEvent(OrganizationAttributeValueChangeEvent data);

  /**
   * Client listener for entity OrganizationAttributeValueChangeEvent
   * Similar to <code>listenToOrganizationAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification OrganizationAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/organizationAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToOrganizationAttributeValueChangeEventWithHttpInfo(OrganizationAttributeValueChangeEvent data);



  /**
   * Client listener for entity OrganizationCreateEvent
   * Example of a client listener for receiving the notification OrganizationCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/organizationCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToOrganizationCreateEvent(OrganizationCreateEvent data);

  /**
   * Client listener for entity OrganizationCreateEvent
   * Similar to <code>listenToOrganizationCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification OrganizationCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/organizationCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToOrganizationCreateEventWithHttpInfo(OrganizationCreateEvent data);



  /**
   * Client listener for entity OrganizationDeleteEvent
   * Example of a client listener for receiving the notification OrganizationDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/organizationDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToOrganizationDeleteEvent(OrganizationDeleteEvent data);

  /**
   * Client listener for entity OrganizationDeleteEvent
   * Similar to <code>listenToOrganizationDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification OrganizationDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/organizationDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToOrganizationDeleteEventWithHttpInfo(OrganizationDeleteEvent data);



  /**
   * Client listener for entity OrganizationStateChangeEvent
   * Example of a client listener for receiving the notification OrganizationStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/organizationStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToOrganizationStateChangeEvent(OrganizationStateChangeEvent data);

  /**
   * Client listener for entity OrganizationStateChangeEvent
   * Similar to <code>listenToOrganizationStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification OrganizationStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/organizationStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToOrganizationStateChangeEventWithHttpInfo(OrganizationStateChangeEvent data);


}
