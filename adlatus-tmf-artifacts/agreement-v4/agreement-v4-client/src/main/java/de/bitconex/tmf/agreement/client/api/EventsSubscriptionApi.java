package de.bitconex.tmf.agreement.client.api;

import de.bitconex.tmf.agreement.client.ApiClient;
import de.bitconex.tmf.agreement.client.ApiResponse;

import de.bitconex.tmf.agreement.model.EventSubscription;
import de.bitconex.tmf.agreement.model.EventSubscriptionInput;

import feign.*;


public interface EventsSubscriptionApi extends ApiClient.Api {


  /**
   * Register a listener
   * Sets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.
   * @param data Data containing the callback endpoint to deliver the information (required)
   * @return EventSubscription
   */
  @RequestLine("POST /hub")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription registerListener(EventSubscriptionInput data);

  /**
   * Register a listener
   * Similar to <code>registerListener</code> but it also returns the http response headers .
   * Sets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.
   * @param data Data containing the callback endpoint to deliver the information (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /hub")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> registerListenerWithHttpInfo(EventSubscriptionInput data);



  /**
   * Unregister a listener
   * Resets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.
   * @param id The id of the registered listener (required)
   */
  @RequestLine("DELETE /hub/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void unregisterListener(@Param("id") String id);

  /**
   * Unregister a listener
   * Similar to <code>unregisterListener</code> but it also returns the http response headers .
   * Resets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.
   * @param id The id of the registered listener (required)
   */
  @RequestLine("DELETE /hub/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> unregisterListenerWithHttpInfo(@Param("id") String id);


}
