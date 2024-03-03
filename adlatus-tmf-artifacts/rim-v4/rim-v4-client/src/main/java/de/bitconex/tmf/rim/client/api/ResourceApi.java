package de.bitconex.tmf.rim.client.api;

import de.bitconex.tmf.rim.client.ApiClient;
import de.bitconex.tmf.rim.client.EncodingUtils;
import de.bitconex.tmf.rim.model.Resource;
import de.bitconex.tmf.rim.model.ResourceCreate;
import de.bitconex.tmf.rim.model.ResourceUpdate;
import feign.*;
import de.bitconex.tmf.rim.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ResourceApi extends ApiClient.Api {


  /**
   * Creates a Resource
   * This operation creates a Resource entity.
   * @param resource The Resource to be created (required)
   * @return Resource
   */
  @RequestLine("POST /resource")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Resource createResource(ResourceCreate resource);

  /**
   * Creates a Resource
   * Similar to <code>createResource</code> but it also returns the http response headers .
   * This operation creates a Resource entity.
   * @param resource The Resource to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /resource")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Resource> createResourceWithHttpInfo(ResourceCreate resource);



  /**
   * Deletes a Resource
   * This operation deletes a Resource entity.
   * @param id Identifier of the Resource (required)
   */
  @RequestLine("DELETE /resource/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteResource(@Param("id") String id);

  /**
   * Deletes a Resource
   * Similar to <code>deleteResource</code> but it also returns the http response headers .
   * This operation deletes a Resource entity.
   * @param id Identifier of the Resource (required)
   */
  @RequestLine("DELETE /resource/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteResourceWithHttpInfo(@Param("id") String id);



  /**
   * List or find Resource objects
   * This operation list or find Resource entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;Resource&gt;
   */
  @RequestLine("GET /resource?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<Resource> listResource(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find Resource objects
   * Similar to <code>listResource</code> but it also returns the http response headers .
   * This operation list or find Resource entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resource?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<Resource>> listResourceWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find Resource objects
   * This operation list or find Resource entities
   * Note, this is equivalent to the other <code>listResource</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListResourceQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;Resource&gt;
   */
  @RequestLine("GET /resource?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<Resource> listResource(@QueryMap(encoded=true) ListResourceQueryParams queryParams);

  /**
  * List or find Resource objects
  * This operation list or find Resource entities
  * Note, this is equivalent to the other <code>listResource</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;Resource&gt;
      */
      @RequestLine("GET /resource?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<Resource>> listResourceWithHttpInfo(@QueryMap(encoded=true) ListResourceQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listResource</code> method in a fluent style.
   */
  public static class ListResourceQueryParams extends HashMap<String, Object> {
    public ListResourceQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a Resource
   * This operation updates partially a Resource entity.
   * @param id Identifier of the Resource (required)
   * @param resource The Resource to be updated (required)
   * @return Resource
   */
  @RequestLine("PATCH /resource/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Resource patchResource(@Param("id") String id, ResourceUpdate resource);

  /**
   * Updates partially a Resource
   * Similar to <code>patchResource</code> but it also returns the http response headers .
   * This operation updates partially a Resource entity.
   * @param id Identifier of the Resource (required)
   * @param resource The Resource to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /resource/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Resource> patchResourceWithHttpInfo(@Param("id") String id, ResourceUpdate resource);



  /**
   * Retrieves a Resource by ID
   * This operation retrieves a Resource entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Resource (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return Resource
   */
  @RequestLine("GET /resource/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  Resource retrieveResource(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a Resource by ID
   * Similar to <code>retrieveResource</code> but it also returns the http response headers .
   * This operation retrieves a Resource entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Resource (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resource/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Resource> retrieveResourceWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a Resource by ID
   * This operation retrieves a Resource entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveResource</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveResourceQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the Resource (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return Resource
   */
  @RequestLine("GET /resource/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  Resource retrieveResource(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceQueryParams queryParams);

  /**
  * Retrieves a Resource by ID
  * This operation retrieves a Resource entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveResource</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the Resource (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return Resource
      */
      @RequestLine("GET /resource/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<Resource> retrieveResourceWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveResource</code> method in a fluent style.
   */
  public static class RetrieveResourceQueryParams extends HashMap<String, Object> {
    public RetrieveResourceQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
