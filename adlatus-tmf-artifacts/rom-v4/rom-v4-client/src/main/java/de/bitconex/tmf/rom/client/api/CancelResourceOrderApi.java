package de.bitconex.tmf.rom.client.api;

import de.bitconex.tmf.rom.client.ApiClient;
import de.bitconex.tmf.rom.client.ApiResponse;
import de.bitconex.tmf.rom.client.EncodingUtils;
import de.bitconex.tmf.rom.model.CancelResourceOrder;
import de.bitconex.tmf.rom.model.CancelResourceOrderCreate;
import feign.*;

import java.util.HashMap;
import java.util.List;


public interface CancelResourceOrderApi extends ApiClient.Api {


  /**
   * Creates a CancelResourceOrder
   * This operation creates a CancelResourceOrder entity.
   * @param cancelResourceOrder The CancelResourceOrder to be created (required)
   * @return CancelResourceOrder
   */
  @RequestLine("POST /cancelResourceOrder")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  CancelResourceOrder createCancelResourceOrder(CancelResourceOrderCreate cancelResourceOrder);

  /**
   * Creates a CancelResourceOrder
   * Similar to <code>createCancelResourceOrder</code> but it also returns the http response headers .
   * This operation creates a CancelResourceOrder entity.
   * @param cancelResourceOrder The CancelResourceOrder to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /cancelResourceOrder")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<CancelResourceOrder> createCancelResourceOrderWithHttpInfo(CancelResourceOrderCreate cancelResourceOrder);



  /**
   * List or find CancelResourceOrder objects
   * This operation list or find CancelResourceOrder entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;CancelResourceOrder&gt;
   */
  @RequestLine("GET /cancelResourceOrder?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<CancelResourceOrder> listCancelResourceOrder(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find CancelResourceOrder objects
   * Similar to <code>listCancelResourceOrder</code> but it also returns the http response headers .
   * This operation list or find CancelResourceOrder entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /cancelResourceOrder?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<CancelResourceOrder>> listCancelResourceOrderWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find CancelResourceOrder objects
   * This operation list or find CancelResourceOrder entities
   * Note, this is equivalent to the other <code>listCancelResourceOrder</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListCancelResourceOrderQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;CancelResourceOrder&gt;
   */
  @RequestLine("GET /cancelResourceOrder?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<CancelResourceOrder> listCancelResourceOrder(@QueryMap(encoded=true) ListCancelResourceOrderQueryParams queryParams);

  /**
  * List or find CancelResourceOrder objects
  * This operation list or find CancelResourceOrder entities
  * Note, this is equivalent to the other <code>listCancelResourceOrder</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;CancelResourceOrder&gt;
      */
      @RequestLine("GET /cancelResourceOrder?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<CancelResourceOrder>> listCancelResourceOrderWithHttpInfo(@QueryMap(encoded=true) ListCancelResourceOrderQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listCancelResourceOrder</code> method in a fluent style.
   */
  public static class ListCancelResourceOrderQueryParams extends HashMap<String, Object> {
    public ListCancelResourceOrderQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListCancelResourceOrderQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListCancelResourceOrderQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Retrieves a CancelResourceOrder by ID
   * This operation retrieves a CancelResourceOrder entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the CancelResourceOrder (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return CancelResourceOrder
   */
  @RequestLine("GET /cancelResourceOrder/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  CancelResourceOrder retrieveCancelResourceOrder(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a CancelResourceOrder by ID
   * Similar to <code>retrieveCancelResourceOrder</code> but it also returns the http response headers .
   * This operation retrieves a CancelResourceOrder entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the CancelResourceOrder (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /cancelResourceOrder/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<CancelResourceOrder> retrieveCancelResourceOrderWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a CancelResourceOrder by ID
   * This operation retrieves a CancelResourceOrder entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveCancelResourceOrder</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveCancelResourceOrderQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the CancelResourceOrder (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return CancelResourceOrder
   */
  @RequestLine("GET /cancelResourceOrder/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  CancelResourceOrder retrieveCancelResourceOrder(@Param("id") String id, @QueryMap(encoded=true) RetrieveCancelResourceOrderQueryParams queryParams);

  /**
  * Retrieves a CancelResourceOrder by ID
  * This operation retrieves a CancelResourceOrder entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveCancelResourceOrder</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the CancelResourceOrder (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return CancelResourceOrder
      */
      @RequestLine("GET /cancelResourceOrder/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<CancelResourceOrder> retrieveCancelResourceOrderWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveCancelResourceOrderQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveCancelResourceOrder</code> method in a fluent style.
   */
  public static class RetrieveCancelResourceOrderQueryParams extends HashMap<String, Object> {
    public RetrieveCancelResourceOrderQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
