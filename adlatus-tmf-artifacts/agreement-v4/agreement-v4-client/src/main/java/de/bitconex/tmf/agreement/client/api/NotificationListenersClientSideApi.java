package de.bitconex.tmf.agreement.client.api;

import de.bitconex.tmf.agreement.client.ApiClient;
import de.bitconex.tmf.agreement.client.ApiResponse;

import de.bitconex.tmf.agreement.model.AgreementAttributeValueChangeEvent;
import de.bitconex.tmf.agreement.model.AgreementCreateEvent;
import de.bitconex.tmf.agreement.model.AgreementDeleteEvent;
import de.bitconex.tmf.agreement.model.AgreementSpecificationAttributeValueChangeEvent;
import de.bitconex.tmf.agreement.model.AgreementSpecificationCreateEvent;
import de.bitconex.tmf.agreement.model.AgreementSpecificationDeleteEvent;
import de.bitconex.tmf.agreement.model.AgreementSpecificationStateChangeEvent;
import de.bitconex.tmf.agreement.model.AgreementStateChangeEvent;
import de.bitconex.tmf.agreement.model.Error;
import de.bitconex.tmf.agreement.model.EventSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface NotificationListenersClientSideApi extends ApiClient.Api {


  /**
   * Client listener for entity AgreementAttributeValueChangeEvent
   * Example of a client listener for receiving the notification AgreementAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementAttributeValueChangeEvent(AgreementAttributeValueChangeEvent data);

  /**
   * Client listener for entity AgreementAttributeValueChangeEvent
   * Similar to <code>listenToAgreementAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementAttributeValueChangeEventWithHttpInfo(AgreementAttributeValueChangeEvent data);



  /**
   * Client listener for entity AgreementCreateEvent
   * Example of a client listener for receiving the notification AgreementCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementCreateEvent(AgreementCreateEvent data);

  /**
   * Client listener for entity AgreementCreateEvent
   * Similar to <code>listenToAgreementCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementCreateEventWithHttpInfo(AgreementCreateEvent data);



  /**
   * Client listener for entity AgreementDeleteEvent
   * Example of a client listener for receiving the notification AgreementDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementDeleteEvent(AgreementDeleteEvent data);

  /**
   * Client listener for entity AgreementDeleteEvent
   * Similar to <code>listenToAgreementDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementDeleteEventWithHttpInfo(AgreementDeleteEvent data);



  /**
   * Client listener for entity AgreementSpecificationAttributeValueChangeEvent
   * Example of a client listener for receiving the notification AgreementSpecificationAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementSpecificationAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementSpecificationAttributeValueChangeEvent(AgreementSpecificationAttributeValueChangeEvent data);

  /**
   * Client listener for entity AgreementSpecificationAttributeValueChangeEvent
   * Similar to <code>listenToAgreementSpecificationAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementSpecificationAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementSpecificationAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementSpecificationAttributeValueChangeEventWithHttpInfo(AgreementSpecificationAttributeValueChangeEvent data);



  /**
   * Client listener for entity AgreementSpecificationCreateEvent
   * Example of a client listener for receiving the notification AgreementSpecificationCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementSpecificationCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementSpecificationCreateEvent(AgreementSpecificationCreateEvent data);

  /**
   * Client listener for entity AgreementSpecificationCreateEvent
   * Similar to <code>listenToAgreementSpecificationCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementSpecificationCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementSpecificationCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementSpecificationCreateEventWithHttpInfo(AgreementSpecificationCreateEvent data);



  /**
   * Client listener for entity AgreementSpecificationDeleteEvent
   * Example of a client listener for receiving the notification AgreementSpecificationDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementSpecificationDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementSpecificationDeleteEvent(AgreementSpecificationDeleteEvent data);

  /**
   * Client listener for entity AgreementSpecificationDeleteEvent
   * Similar to <code>listenToAgreementSpecificationDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementSpecificationDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementSpecificationDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementSpecificationDeleteEventWithHttpInfo(AgreementSpecificationDeleteEvent data);



  /**
   * Client listener for entity AgreementSpecificationStateChangeEvent
   * Example of a client listener for receiving the notification AgreementSpecificationStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementSpecificationStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementSpecificationStateChangeEvent(AgreementSpecificationStateChangeEvent data);

  /**
   * Client listener for entity AgreementSpecificationStateChangeEvent
   * Similar to <code>listenToAgreementSpecificationStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementSpecificationStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementSpecificationStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementSpecificationStateChangeEventWithHttpInfo(AgreementSpecificationStateChangeEvent data);



  /**
   * Client listener for entity AgreementStateChangeEvent
   * Example of a client listener for receiving the notification AgreementStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/agreementStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAgreementStateChangeEvent(AgreementStateChangeEvent data);

  /**
   * Client listener for entity AgreementStateChangeEvent
   * Similar to <code>listenToAgreementStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AgreementStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/agreementStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAgreementStateChangeEventWithHttpInfo(AgreementStateChangeEvent data);


}
