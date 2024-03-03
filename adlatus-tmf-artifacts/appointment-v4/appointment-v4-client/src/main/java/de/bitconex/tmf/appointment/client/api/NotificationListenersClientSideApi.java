package de.bitconex.tmf.appointment.client.api;

import de.bitconex.tmf.appointment.client.ApiClient;
import de.bitconex.tmf.appointment.client.ApiResponse;
import de.bitconex.tmf.appointment.client.EncodingUtils;

import de.bitconex.tmf.appointment.model.AppointmentAttributeValueChangeEvent;
import de.bitconex.tmf.appointment.model.AppointmentCreateEvent;
import de.bitconex.tmf.appointment.model.AppointmentDeleteEvent;
import de.bitconex.tmf.appointment.model.AppointmentStateChangeEvent;
import de.bitconex.tmf.appointment.model.Error;
import de.bitconex.tmf.appointment.model.EventSubscription;
import de.bitconex.tmf.appointment.model.SearchTimeSlotAttributeValueChangeEvent;
import de.bitconex.tmf.appointment.model.SearchTimeSlotCreateEvent;
import de.bitconex.tmf.appointment.model.SearchTimeSlotDeleteEvent;
import de.bitconex.tmf.appointment.model.SearchTimeSlotStateChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface NotificationListenersClientSideApi extends ApiClient.Api {


  /**
   * Client listener for entity AppointmentAttributeValueChangeEvent
   * Example of a client listener for receiving the notification AppointmentAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/appointmentAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAppointmentAttributeValueChangeEvent(AppointmentAttributeValueChangeEvent data);

  /**
   * Client listener for entity AppointmentAttributeValueChangeEvent
   * Similar to <code>listenToAppointmentAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AppointmentAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/appointmentAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAppointmentAttributeValueChangeEventWithHttpInfo(AppointmentAttributeValueChangeEvent data);



  /**
   * Client listener for entity AppointmentCreateEvent
   * Example of a client listener for receiving the notification AppointmentCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/appointmentCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAppointmentCreateEvent(AppointmentCreateEvent data);

  /**
   * Client listener for entity AppointmentCreateEvent
   * Similar to <code>listenToAppointmentCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AppointmentCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/appointmentCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAppointmentCreateEventWithHttpInfo(AppointmentCreateEvent data);



  /**
   * Client listener for entity AppointmentDeleteEvent
   * Example of a client listener for receiving the notification AppointmentDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/appointmentDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAppointmentDeleteEvent(AppointmentDeleteEvent data);

  /**
   * Client listener for entity AppointmentDeleteEvent
   * Similar to <code>listenToAppointmentDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AppointmentDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/appointmentDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAppointmentDeleteEventWithHttpInfo(AppointmentDeleteEvent data);



  /**
   * Client listener for entity AppointmentStateChangeEvent
   * Example of a client listener for receiving the notification AppointmentStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/appointmentStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToAppointmentStateChangeEvent(AppointmentStateChangeEvent data);

  /**
   * Client listener for entity AppointmentStateChangeEvent
   * Similar to <code>listenToAppointmentStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification AppointmentStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/appointmentStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToAppointmentStateChangeEventWithHttpInfo(AppointmentStateChangeEvent data);



  /**
   * Client listener for entity SearchTimeSlotAttributeValueChangeEvent
   * Example of a client listener for receiving the notification SearchTimeSlotAttributeValueChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/searchTimeSlotAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToSearchTimeSlotAttributeValueChangeEvent(SearchTimeSlotAttributeValueChangeEvent data);

  /**
   * Client listener for entity SearchTimeSlotAttributeValueChangeEvent
   * Similar to <code>listenToSearchTimeSlotAttributeValueChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification SearchTimeSlotAttributeValueChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/searchTimeSlotAttributeValueChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToSearchTimeSlotAttributeValueChangeEventWithHttpInfo(SearchTimeSlotAttributeValueChangeEvent data);



  /**
   * Client listener for entity SearchTimeSlotCreateEvent
   * Example of a client listener for receiving the notification SearchTimeSlotCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/searchTimeSlotCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToSearchTimeSlotCreateEvent(SearchTimeSlotCreateEvent data);

  /**
   * Client listener for entity SearchTimeSlotCreateEvent
   * Similar to <code>listenToSearchTimeSlotCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification SearchTimeSlotCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/searchTimeSlotCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToSearchTimeSlotCreateEventWithHttpInfo(SearchTimeSlotCreateEvent data);



  /**
   * Client listener for entity SearchTimeSlotDeleteEvent
   * Example of a client listener for receiving the notification SearchTimeSlotDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/searchTimeSlotDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToSearchTimeSlotDeleteEvent(SearchTimeSlotDeleteEvent data);

  /**
   * Client listener for entity SearchTimeSlotDeleteEvent
   * Similar to <code>listenToSearchTimeSlotDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification SearchTimeSlotDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/searchTimeSlotDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToSearchTimeSlotDeleteEventWithHttpInfo(SearchTimeSlotDeleteEvent data);



  /**
   * Client listener for entity SearchTimeSlotStateChangeEvent
   * Example of a client listener for receiving the notification SearchTimeSlotStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/searchTimeSlotStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToSearchTimeSlotStateChangeEvent(SearchTimeSlotStateChangeEvent data);

  /**
   * Client listener for entity SearchTimeSlotStateChangeEvent
   * Similar to <code>listenToSearchTimeSlotStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification SearchTimeSlotStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/searchTimeSlotStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToSearchTimeSlotStateChangeEventWithHttpInfo(SearchTimeSlotStateChangeEvent data);


}
