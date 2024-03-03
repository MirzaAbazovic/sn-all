package de.bitconex.tmf.appointment.client.api;

import de.bitconex.tmf.appointment.client.ApiClient;
import de.bitconex.tmf.appointment.client.ApiResponse;
import de.bitconex.tmf.appointment.client.EncodingUtils;

import de.bitconex.tmf.appointment.model.Error;
import de.bitconex.tmf.appointment.model.SearchTimeSlot;
import de.bitconex.tmf.appointment.model.SearchTimeSlotCreate;
import de.bitconex.tmf.appointment.model.SearchTimeSlotUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface SearchTimeSlotApi extends ApiClient.Api {


  /**
   * Creates a SearchTimeSlot
   * This operation creates a SearchTimeSlot entity.
   * @param searchTimeSlot The SearchTimeSlot to be created (required)
   * @return SearchTimeSlot
   */
  @RequestLine("POST /searchTimeSlot")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  SearchTimeSlot createSearchTimeSlot(SearchTimeSlotCreate searchTimeSlot);

  /**
   * Creates a SearchTimeSlot
   * Similar to <code>createSearchTimeSlot</code> but it also returns the http response headers .
   * This operation creates a SearchTimeSlot entity.
   * @param searchTimeSlot The SearchTimeSlot to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /searchTimeSlot")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<SearchTimeSlot> createSearchTimeSlotWithHttpInfo(SearchTimeSlotCreate searchTimeSlot);



  /**
   * Deletes a SearchTimeSlot
   * This operation deletes a SearchTimeSlot entity.
   * @param id Identifier of the SearchTimeSlot (required)
   */
  @RequestLine("DELETE /searchTimeSlot/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteSearchTimeSlot(@Param("id") String id);

  /**
   * Deletes a SearchTimeSlot
   * Similar to <code>deleteSearchTimeSlot</code> but it also returns the http response headers .
   * This operation deletes a SearchTimeSlot entity.
   * @param id Identifier of the SearchTimeSlot (required)
   */
  @RequestLine("DELETE /searchTimeSlot/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteSearchTimeSlotWithHttpInfo(@Param("id") String id);



  /**
   * List or find SearchTimeSlot objects
   * This operation list or find SearchTimeSlot entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;SearchTimeSlot&gt;
   */
  @RequestLine("GET /searchTimeSlot?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<SearchTimeSlot> listSearchTimeSlot(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find SearchTimeSlot objects
   * Similar to <code>listSearchTimeSlot</code> but it also returns the http response headers .
   * This operation list or find SearchTimeSlot entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /searchTimeSlot?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<SearchTimeSlot>> listSearchTimeSlotWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find SearchTimeSlot objects
   * This operation list or find SearchTimeSlot entities
   * Note, this is equivalent to the other <code>listSearchTimeSlot</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListSearchTimeSlotQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;SearchTimeSlot&gt;
   */
  @RequestLine("GET /searchTimeSlot?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<SearchTimeSlot> listSearchTimeSlot(@QueryMap(encoded=true) ListSearchTimeSlotQueryParams queryParams);

  /**
  * List or find SearchTimeSlot objects
  * This operation list or find SearchTimeSlot entities
  * Note, this is equivalent to the other <code>listSearchTimeSlot</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;SearchTimeSlot&gt;
      */
      @RequestLine("GET /searchTimeSlot?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<SearchTimeSlot>> listSearchTimeSlotWithHttpInfo(@QueryMap(encoded=true) ListSearchTimeSlotQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listSearchTimeSlot</code> method in a fluent style.
   */
  public static class ListSearchTimeSlotQueryParams extends HashMap<String, Object> {
    public ListSearchTimeSlotQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListSearchTimeSlotQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListSearchTimeSlotQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a SearchTimeSlot
   * This operation updates partially a SearchTimeSlot entity.
   * @param id Identifier of the SearchTimeSlot (required)
   * @param searchTimeSlot The SearchTimeSlot to be updated (required)
   * @return SearchTimeSlot
   */
  @RequestLine("PATCH /searchTimeSlot/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  SearchTimeSlot patchSearchTimeSlot(@Param("id") String id, SearchTimeSlotUpdate searchTimeSlot);

  /**
   * Updates partially a SearchTimeSlot
   * Similar to <code>patchSearchTimeSlot</code> but it also returns the http response headers .
   * This operation updates partially a SearchTimeSlot entity.
   * @param id Identifier of the SearchTimeSlot (required)
   * @param searchTimeSlot The SearchTimeSlot to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /searchTimeSlot/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<SearchTimeSlot> patchSearchTimeSlotWithHttpInfo(@Param("id") String id, SearchTimeSlotUpdate searchTimeSlot);



  /**
   * Retrieves a SearchTimeSlot by ID
   * This operation retrieves a SearchTimeSlot entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the SearchTimeSlot (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return SearchTimeSlot
   */
  @RequestLine("GET /searchTimeSlot/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  SearchTimeSlot retrieveSearchTimeSlot(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a SearchTimeSlot by ID
   * Similar to <code>retrieveSearchTimeSlot</code> but it also returns the http response headers .
   * This operation retrieves a SearchTimeSlot entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the SearchTimeSlot (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /searchTimeSlot/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<SearchTimeSlot> retrieveSearchTimeSlotWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a SearchTimeSlot by ID
   * This operation retrieves a SearchTimeSlot entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveSearchTimeSlot</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveSearchTimeSlotQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the SearchTimeSlot (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return SearchTimeSlot
   */
  @RequestLine("GET /searchTimeSlot/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  SearchTimeSlot retrieveSearchTimeSlot(@Param("id") String id, @QueryMap(encoded=true) RetrieveSearchTimeSlotQueryParams queryParams);

  /**
  * Retrieves a SearchTimeSlot by ID
  * This operation retrieves a SearchTimeSlot entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveSearchTimeSlot</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the SearchTimeSlot (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return SearchTimeSlot
      */
      @RequestLine("GET /searchTimeSlot/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<SearchTimeSlot> retrieveSearchTimeSlotWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveSearchTimeSlotQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveSearchTimeSlot</code> method in a fluent style.
   */
  public static class RetrieveSearchTimeSlotQueryParams extends HashMap<String, Object> {
    public RetrieveSearchTimeSlotQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
