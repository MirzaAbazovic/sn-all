package de.bitconex.tmf.rom.client.api;

import de.bitconex.tmf.rom.client.ApiClient;
import de.bitconex.tmf.rom.client.EncodingUtils;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;
import de.bitconex.tmf.rom.model.ResourceOrderUpdate;
import feign.*;
import de.bitconex.tmf.rom.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ResourceOrderApi extends ApiClient.Api {


  /**
   * Creates a ResourceOrder
   * This operation creates a ResourceOrder entity.
   * @param resourceOrder The ResourceOrder to be created (required)
   * @return ResourceOrder
   */
  @RequestLine("POST /resourceOrder")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceOrder createResourceOrder(ResourceOrderCreate resourceOrder);

  /**
   * Creates a ResourceOrder
   * Similar to <code>createResourceOrder</code> but it also returns the http response headers .
   * This operation creates a ResourceOrder entity.
   * @param resourceOrder The ResourceOrder to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /resourceOrder")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceOrder> createResourceOrderWithHttpInfo(ResourceOrderCreate resourceOrder);



  /**
   * Deletes a ResourceOrder
   * This operation deletes a ResourceOrder entity.
   * @param id Identifier of the ResourceOrder (required)
   */
  @RequestLine("DELETE /resourceOrder/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteResourceOrder(@Param("id") String id);

  /**
   * Deletes a ResourceOrder
   * Similar to <code>deleteResourceOrder</code> but it also returns the http response headers .
   * This operation deletes a ResourceOrder entity.
   * @param id Identifier of the ResourceOrder (required)
   */
  @RequestLine("DELETE /resourceOrder/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteResourceOrderWithHttpInfo(@Param("id") String id);



  /**
   * List or find ResourceOrder objects
   * This operation list or find ResourceOrder entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ResourceOrder&gt;
   */
  @RequestLine("GET /resourceOrder?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ResourceOrder> listResourceOrder(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ResourceOrder objects
   * Similar to <code>listResourceOrder</code> but it also returns the http response headers .
   * This operation list or find ResourceOrder entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceOrder?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ResourceOrder>> listResourceOrderWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ResourceOrder objects
   * This operation list or find ResourceOrder entities
   * Note, this is equivalent to the other <code>listResourceOrder</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListResourceOrderQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ResourceOrder&gt;
   */
  @RequestLine("GET /resourceOrder?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ResourceOrder> listResourceOrder(@QueryMap(encoded=true) ListResourceOrderQueryParams queryParams);

  /**
  * List or find ResourceOrder objects
  * This operation list or find ResourceOrder entities
  * Note, this is equivalent to the other <code>listResourceOrder</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ResourceOrder&gt;
      */
      @RequestLine("GET /resourceOrder?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ResourceOrder>> listResourceOrderWithHttpInfo(@QueryMap(encoded=true) ListResourceOrderQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listResourceOrder</code> method in a fluent style.
   */
  public static class ListResourceOrderQueryParams extends HashMap<String, Object> {
    public ListResourceOrderQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceOrderQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceOrderQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a ResourceOrder
   * This operation updates partially a ResourceOrder entity.
   * @param id Identifier of the ResourceOrder (required)
   * @param resourceOrder The ResourceOrder to be updated (required)
   * @return ResourceOrder
   */
  @RequestLine("PATCH /resourceOrder/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceOrder patchResourceOrder(@Param("id") String id, ResourceOrderUpdate resourceOrder);

  /**
   * Updates partially a ResourceOrder
   * Similar to <code>patchResourceOrder</code> but it also returns the http response headers .
   * This operation updates partially a ResourceOrder entity.
   * @param id Identifier of the ResourceOrder (required)
   * @param resourceOrder The ResourceOrder to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /resourceOrder/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceOrder> patchResourceOrderWithHttpInfo(@Param("id") String id, ResourceOrderUpdate resourceOrder);



  /**
   * Retrieves a ResourceOrder by ID
   * This operation retrieves a ResourceOrder entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceOrder (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ResourceOrder
   */
  @RequestLine("GET /resourceOrder/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ResourceOrder retrieveResourceOrder(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ResourceOrder by ID
   * Similar to <code>retrieveResourceOrder</code> but it also returns the http response headers .
   * This operation retrieves a ResourceOrder entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceOrder (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceOrder/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceOrder> retrieveResourceOrderWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ResourceOrder by ID
   * This operation retrieves a ResourceOrder entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveResourceOrder</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveResourceOrderQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ResourceOrder (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ResourceOrder
   */
  @RequestLine("GET /resourceOrder/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ResourceOrder retrieveResourceOrder(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceOrderQueryParams queryParams);

  /**
  * Retrieves a ResourceOrder by ID
  * This operation retrieves a ResourceOrder entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveResourceOrder</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ResourceOrder (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ResourceOrder
      */
      @RequestLine("GET /resourceOrder/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ResourceOrder> retrieveResourceOrderWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceOrderQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveResourceOrder</code> method in a fluent style.
   */
  public static class RetrieveResourceOrderQueryParams extends HashMap<String, Object> {
    public RetrieveResourceOrderQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
