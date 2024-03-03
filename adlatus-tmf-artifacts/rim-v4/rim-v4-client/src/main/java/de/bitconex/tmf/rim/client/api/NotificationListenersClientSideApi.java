package de.bitconex.tmf.rim.client.api;

import de.bitconex.tmf.rim.client.ApiClient;
import de.bitconex.tmf.rim.client.ApiResponse;
import de.bitconex.tmf.rim.model.*;
import feign.*;


public interface NotificationListenersClientSideApi extends ApiClient.Api {


  /**
   * Client listener for entity ResourceAttributeValueChangeEvent
   * Example of a client listener for receiving the notification ResourceAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceAttributeValueChangeEvent(ResourceAttributeValueChangeEvent data);

  /**
   * Client listener for entity ResourceAttributeValueChangeEvent
   * Similar to <code>listenToResourceAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceAttributeValueChangeEventWithHttpInfo(ResourceAttributeValueChangeEvent data);



  /**
   * Client listener for entity ResourceCreateEvent
   * Example of a client listener for receiving the notification ResourceCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCreateEvent(ResourceCreateEvent data);

  /**
   * Client listener for entity ResourceCreateEvent
   * Similar to <code>listenToResourceCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCreateEventWithHttpInfo(ResourceCreateEvent data);



  /**
   * Client listener for entity ResourceDeleteEvent
   * Example of a client listener for receiving the notification ResourceDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceDeleteEvent(ResourceDeleteEvent data);

  /**
   * Client listener for entity ResourceDeleteEvent
   * Similar to <code>listenToResourceDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceDeleteEventWithHttpInfo(ResourceDeleteEvent data);



  /**
   * Client listener for entity ResourceStateChangeEvent
   * Example of a client listener for receiving the notification ResourceStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceStateChangeEvent(ResourceStateChangeEvent data);

  /**
   * Client listener for entity ResourceStateChangeEvent
   * Similar to <code>listenToResourceStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceStateChangeEventWithHttpInfo(ResourceStateChangeEvent data);


}
