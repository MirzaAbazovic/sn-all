package de.bitconex.tmf.appointment.client.api;

import de.bitconex.tmf.appointment.client.ApiClient;
import de.bitconex.tmf.appointment.client.ApiResponse;
import de.bitconex.tmf.appointment.client.EncodingUtils;

import de.bitconex.tmf.appointment.model.Appointment;
import de.bitconex.tmf.appointment.model.AppointmentCreate;
import de.bitconex.tmf.appointment.model.AppointmentUpdate;
import de.bitconex.tmf.appointment.model.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface AppointmentApi extends ApiClient.Api {


  /**
   * Creates a Appointment
   * This operation creates a Appointment entity.
   * @param appointment The Appointment to be created (required)
   * @return Appointment
   */
  @RequestLine("POST /appointment")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Appointment createAppointment(AppointmentCreate appointment);

  /**
   * Creates a Appointment
   * Similar to <code>createAppointment</code> but it also returns the http response headers .
   * This operation creates a Appointment entity.
   * @param appointment The Appointment to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /appointment")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Appointment> createAppointmentWithHttpInfo(AppointmentCreate appointment);



  /**
   * Deletes a Appointment
   * This operation deletes a Appointment entity.
   * @param id Identifier of the Appointment (required)
   */
  @RequestLine("DELETE /appointment/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteAppointment(@Param("id") String id);

  /**
   * Deletes a Appointment
   * Similar to <code>deleteAppointment</code> but it also returns the http response headers .
   * This operation deletes a Appointment entity.
   * @param id Identifier of the Appointment (required)
   */
  @RequestLine("DELETE /appointment/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteAppointmentWithHttpInfo(@Param("id") String id);



  /**
   * List or find Appointment objects
   * This operation list or find Appointment entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;Appointment&gt;
   */
  @RequestLine("GET /appointment?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<Appointment> listAppointment(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find Appointment objects
   * Similar to <code>listAppointment</code> but it also returns the http response headers .
   * This operation list or find Appointment entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /appointment?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<Appointment>> listAppointmentWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find Appointment objects
   * This operation list or find Appointment entities
   * Note, this is equivalent to the other <code>listAppointment</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListAppointmentQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;Appointment&gt;
   */
  @RequestLine("GET /appointment?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<Appointment> listAppointment(@QueryMap(encoded=true) ListAppointmentQueryParams queryParams);

  /**
  * List or find Appointment objects
  * This operation list or find Appointment entities
  * Note, this is equivalent to the other <code>listAppointment</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;Appointment&gt;
      */
      @RequestLine("GET /appointment?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<Appointment>> listAppointmentWithHttpInfo(@QueryMap(encoded=true) ListAppointmentQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listAppointment</code> method in a fluent style.
   */
  public static class ListAppointmentQueryParams extends HashMap<String, Object> {
    public ListAppointmentQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListAppointmentQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListAppointmentQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a Appointment
   * This operation updates partially a Appointment entity.
   * @param id Identifier of the Appointment (required)
   * @param appointment The Appointment to be updated (required)
   * @return Appointment
   */
  @RequestLine("PATCH /appointment/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Appointment patchAppointment(@Param("id") String id, AppointmentUpdate appointment);

  /**
   * Updates partially a Appointment
   * Similar to <code>patchAppointment</code> but it also returns the http response headers .
   * This operation updates partially a Appointment entity.
   * @param id Identifier of the Appointment (required)
   * @param appointment The Appointment to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /appointment/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Appointment> patchAppointmentWithHttpInfo(@Param("id") String id, AppointmentUpdate appointment);



  /**
   * Retrieves a Appointment by ID
   * This operation retrieves a Appointment entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Appointment (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return Appointment
   */
  @RequestLine("GET /appointment/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  Appointment retrieveAppointment(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a Appointment by ID
   * Similar to <code>retrieveAppointment</code> but it also returns the http response headers .
   * This operation retrieves a Appointment entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Appointment (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /appointment/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Appointment> retrieveAppointmentWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a Appointment by ID
   * This operation retrieves a Appointment entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveAppointment</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveAppointmentQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the Appointment (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return Appointment
   */
  @RequestLine("GET /appointment/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  Appointment retrieveAppointment(@Param("id") String id, @QueryMap(encoded=true) RetrieveAppointmentQueryParams queryParams);

  /**
  * Retrieves a Appointment by ID
  * This operation retrieves a Appointment entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveAppointment</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the Appointment (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return Appointment
      */
      @RequestLine("GET /appointment/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<Appointment> retrieveAppointmentWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveAppointmentQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveAppointment</code> method in a fluent style.
   */
  public static class RetrieveAppointmentQueryParams extends HashMap<String, Object> {
    public RetrieveAppointmentQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
