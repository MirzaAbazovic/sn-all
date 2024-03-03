package de.bitconex.tmf.rom.client.api;

import de.bitconex.tmf.rom.client.ApiClient;
import de.bitconex.tmf.rom.model.*;
import feign.*;
import de.bitconex.tmf.rom.client.ApiResponse;


public interface NotificationListenersClientSideApi extends ApiClient.Api {


  /**
   * Client listener for entity CancelResourceOrderCreateEvent
   * Example of a client listener for receiving the notification CancelResourceOrderCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/cancelResourceOrderCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToCancelResourceOrderCreateEvent(CancelResourceOrderCreateEvent data);

  /**
   * Client listener for entity CancelResourceOrderCreateEvent
   * Similar to <code>listenToCancelResourceOrderCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification CancelResourceOrderCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/cancelResourceOrderCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToCancelResourceOrderCreateEventWithHttpInfo(CancelResourceOrderCreateEvent data);



  /**
   * Client listener for entity CancelResourceOrderInformationRequiredEvent
   * Example of a client listener for receiving the notification CancelResourceOrderInformationRequiredEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/cancelResourceOrderInformationRequiredEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToCancelResourceOrderInformationRequiredEvent(CancelResourceOrderInformationRequiredEvent data);

  /**
   * Client listener for entity CancelResourceOrderInformationRequiredEvent
   * Similar to <code>listenToCancelResourceOrderInformationRequiredEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification CancelResourceOrderInformationRequiredEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/cancelResourceOrderInformationRequiredEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToCancelResourceOrderInformationRequiredEventWithHttpInfo(CancelResourceOrderInformationRequiredEvent data);



  /**
   * Client listener for entity CancelResourceOrderStateChangeEvent
   * Example of a client listener for receiving the notification CancelResourceOrderStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/cancelResourceOrderStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToCancelResourceOrderStateChangeEvent(CancelResourceOrderStateChangeEvent data);

  /**
   * Client listener for entity CancelResourceOrderStateChangeEvent
   * Similar to <code>listenToCancelResourceOrderStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification CancelResourceOrderStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/cancelResourceOrderStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToCancelResourceOrderStateChangeEventWithHttpInfo(CancelResourceOrderStateChangeEvent data);



  /**
   * Client listener for entity ResourceOrderAttributeValueChangeEvent
   * Example of a client listener for receiving the notification ResourceOrderAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceOrderAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceOrderAttributeValueChangeEvent(ResourceOrderAttributeValueChangeEvent data);

  /**
   * Client listener for entity ResourceOrderAttributeValueChangeEvent
   * Similar to <code>listenToResourceOrderAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceOrderAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceOrderAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceOrderAttributeValueChangeEventWithHttpInfo(ResourceOrderAttributeValueChangeEvent data);



  /**
   * Client listener for entity ResourceOrderCreateEvent
   * Example of a client listener for receiving the notification ResourceOrderCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceOrderCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceOrderCreateEvent(ResourceOrderCreateEvent data);

  /**
   * Client listener for entity ResourceOrderCreateEvent
   * Similar to <code>listenToResourceOrderCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceOrderCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceOrderCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceOrderCreateEventWithHttpInfo(ResourceOrderCreateEvent data);



  /**
   * Client listener for entity ResourceOrderDeleteEvent
   * Example of a client listener for receiving the notification ResourceOrderDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceOrderDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceOrderDeleteEvent(ResourceOrderDeleteEvent data);

  /**
   * Client listener for entity ResourceOrderDeleteEvent
   * Similar to <code>listenToResourceOrderDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceOrderDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceOrderDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceOrderDeleteEventWithHttpInfo(ResourceOrderDeleteEvent data);



  /**
   * Client listener for entity ResourceOrderInformationRequiredEvent
   * Example of a client listener for receiving the notification ResourceOrderInformationRequiredEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceOrderInformationRequiredEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceOrderInformationRequiredEvent(ResourceOrderInformationRequiredEvent data);

  /**
   * Client listener for entity ResourceOrderInformationRequiredEvent
   * Similar to <code>listenToResourceOrderInformationRequiredEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceOrderInformationRequiredEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceOrderInformationRequiredEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceOrderInformationRequiredEventWithHttpInfo(ResourceOrderInformationRequiredEvent data);



  /**
   * Client listener for entity ResourceOrderStateChangeEvent
   * Example of a client listener for receiving the notification ResourceOrderStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceOrderStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceOrderStateChangeEvent(ResourceOrderStateChangeEvent data);

  /**
   * Client listener for entity ResourceOrderStateChangeEvent
   * Similar to <code>listenToResourceOrderStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceOrderStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceOrderStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceOrderStateChangeEventWithHttpInfo(ResourceOrderStateChangeEvent data);


}
