package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.model.*;
import feign.*;
import de.bitconex.tmf.rcm.client.ApiResponse;

public interface NotificationListenersClientSideApi extends ApiClient.Api {


  /**
   * Client listener for entity ExportJobCreateEvent
   * Example of a client listener for receiving the notification ExportJobCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/exportJobCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToExportJobCreateEvent(ExportJobCreateEvent data);

  /**
   * Client listener for entity ExportJobCreateEvent
   * Similar to <code>listenToExportJobCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ExportJobCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/exportJobCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToExportJobCreateEventWithHttpInfo(ExportJobCreateEvent data);



  /**
   * Client listener for entity ExportJobStateChangeEvent
   * Example of a client listener for receiving the notification ExportJobStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/exportJobStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToExportJobStateChangeEvent(ExportJobStateChangeEvent data);

  /**
   * Client listener for entity ExportJobStateChangeEvent
   * Similar to <code>listenToExportJobStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ExportJobStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/exportJobStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToExportJobStateChangeEventWithHttpInfo(ExportJobStateChangeEvent data);



  /**
   * Client listener for entity ImportJobCreateEvent
   * Example of a client listener for receiving the notification ImportJobCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/importJobCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToImportJobCreateEvent(ImportJobCreateEvent data);

  /**
   * Client listener for entity ImportJobCreateEvent
   * Similar to <code>listenToImportJobCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ImportJobCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/importJobCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToImportJobCreateEventWithHttpInfo(ImportJobCreateEvent data);



  /**
   * Client listener for entity ImportJobStateChangeEvent
   * Example of a client listener for receiving the notification ImportJobStateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/importJobStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToImportJobStateChangeEvent(ImportJobStateChangeEvent data);

  /**
   * Client listener for entity ImportJobStateChangeEvent
   * Similar to <code>listenToImportJobStateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ImportJobStateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/importJobStateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToImportJobStateChangeEventWithHttpInfo(ImportJobStateChangeEvent data);



  /**
   * Client listener for entity ResourceCandidateChangeEvent
   * Example of a client listener for receiving the notification ResourceCandidateChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCandidateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCandidateChangeEvent(ResourceCandidateChangeEvent data);

  /**
   * Client listener for entity ResourceCandidateChangeEvent
   * Similar to <code>listenToResourceCandidateChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCandidateChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCandidateChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCandidateChangeEventWithHttpInfo(ResourceCandidateChangeEvent data);



  /**
   * Client listener for entity ResourceCandidateCreateEvent
   * Example of a client listener for receiving the notification ResourceCandidateCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCandidateCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCandidateCreateEvent(ResourceCandidateCreateEvent data);

  /**
   * Client listener for entity ResourceCandidateCreateEvent
   * Similar to <code>listenToResourceCandidateCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCandidateCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCandidateCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCandidateCreateEventWithHttpInfo(ResourceCandidateCreateEvent data);



  /**
   * Client listener for entity ResourceCandidateDeleteEvent
   * Example of a client listener for receiving the notification ResourceCandidateDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCandidateDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCandidateDeleteEvent(ResourceCandidateDeleteEvent data);

  /**
   * Client listener for entity ResourceCandidateDeleteEvent
   * Similar to <code>listenToResourceCandidateDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCandidateDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCandidateDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCandidateDeleteEventWithHttpInfo(ResourceCandidateDeleteEvent data);



  /**
   * Client listener for entity ResourceCatalogChangeEvent
   * Example of a client listener for receiving the notification ResourceCatalogChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCatalogChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCatalogChangeEvent(ResourceCatalogChangeEvent data);

  /**
   * Client listener for entity ResourceCatalogChangeEvent
   * Similar to <code>listenToResourceCatalogChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCatalogChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCatalogChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCatalogChangeEventWithHttpInfo(ResourceCatalogChangeEvent data);



  /**
   * Client listener for entity ResourceCatalogCreateEvent
   * Example of a client listener for receiving the notification ResourceCatalogCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCatalogCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCatalogCreateEvent(ResourceCatalogCreateEvent data);

  /**
   * Client listener for entity ResourceCatalogCreateEvent
   * Similar to <code>listenToResourceCatalogCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCatalogCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCatalogCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCatalogCreateEventWithHttpInfo(ResourceCatalogCreateEvent data);



  /**
   * Client listener for entity ResourceCatalogDeleteEvent
   * Example of a client listener for receiving the notification ResourceCatalogDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCatalogDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCatalogDeleteEvent(ResourceCatalogDeleteEvent data);

  /**
   * Client listener for entity ResourceCatalogDeleteEvent
   * Similar to <code>listenToResourceCatalogDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCatalogDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCatalogDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCatalogDeleteEventWithHttpInfo(ResourceCatalogDeleteEvent data);



  /**
   * Client listener for entity ResourceCategoryChangeEvent
   * Example of a client listener for receiving the notification ResourceCategoryChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCategoryChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCategoryChangeEvent(ResourceCategoryChangeEvent data);

  /**
   * Client listener for entity ResourceCategoryChangeEvent
   * Similar to <code>listenToResourceCategoryChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCategoryChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCategoryChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCategoryChangeEventWithHttpInfo(ResourceCategoryChangeEvent data);



  /**
   * Client listener for entity ResourceCategoryCreateEvent
   * Example of a client listener for receiving the notification ResourceCategoryCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCategoryCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCategoryCreateEvent(ResourceCategoryCreateEvent data);

  /**
   * Client listener for entity ResourceCategoryCreateEvent
   * Similar to <code>listenToResourceCategoryCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCategoryCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCategoryCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCategoryCreateEventWithHttpInfo(ResourceCategoryCreateEvent data);



  /**
   * Client listener for entity ResourceCategoryDeleteEvent
   * Example of a client listener for receiving the notification ResourceCategoryDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceCategoryDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceCategoryDeleteEvent(ResourceCategoryDeleteEvent data);

  /**
   * Client listener for entity ResourceCategoryDeleteEvent
   * Similar to <code>listenToResourceCategoryDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceCategoryDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceCategoryDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceCategoryDeleteEventWithHttpInfo(ResourceCategoryDeleteEvent data);



  /**
   * Client listener for entity ResourceSpecificationChangeEvent
   * Example of a client listener for receiving the notification ResourceSpecificationChangeEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceSpecificationChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceSpecificationChangeEvent(ResourceSpecificationChangeEvent data);

  /**
   * Client listener for entity ResourceSpecificationChangeEvent
   * Similar to <code>listenToResourceSpecificationChangeEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceSpecificationChangeEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceSpecificationChangeEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceSpecificationChangeEventWithHttpInfo(ResourceSpecificationChangeEvent data);



  /**
   * Client listener for entity ResourceSpecificationCreateEvent
   * Example of a client listener for receiving the notification ResourceSpecificationCreateEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceSpecificationCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceSpecificationCreateEvent(ResourceSpecificationCreateEvent data);

  /**
   * Client listener for entity ResourceSpecificationCreateEvent
   * Similar to <code>listenToResourceSpecificationCreateEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceSpecificationCreateEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceSpecificationCreateEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceSpecificationCreateEventWithHttpInfo(ResourceSpecificationCreateEvent data);



  /**
   * Client listener for entity ResourceSpecificationDeleteEvent
   * Example of a client listener for receiving the notification ResourceSpecificationDeleteEvent
   * @param data The event data (required)
   * @return EventSubscription
   */
  @RequestLine("POST /listener/resourceSpecificationDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  EventSubscription listenToResourceSpecificationDeleteEvent(ResourceSpecificationDeleteEvent data);

  /**
   * Client listener for entity ResourceSpecificationDeleteEvent
   * Similar to <code>listenToResourceSpecificationDeleteEvent</code> but it also returns the http response headers .
   * Example of a client listener for receiving the notification ResourceSpecificationDeleteEvent
   * @param data The event data (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /listener/resourceSpecificationDeleteEvent")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<EventSubscription> listenToResourceSpecificationDeleteEventWithHttpInfo(ResourceSpecificationDeleteEvent data);


}
