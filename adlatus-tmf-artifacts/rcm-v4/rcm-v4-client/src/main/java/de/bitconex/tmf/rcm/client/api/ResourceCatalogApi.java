package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.EncodingUtils;
import de.bitconex.tmf.rcm.model.ResourceCatalog;
import de.bitconex.tmf.rcm.model.ResourceCatalogCreate;
import de.bitconex.tmf.rcm.model.ResourceCatalogUpdate;
import feign.*;
import de.bitconex.tmf.rcm.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ResourceCatalogApi extends ApiClient.Api {


  /**
   * Creates a ResourceCatalog
   * This operation creates a ResourceCatalog entity.
   * @param resourceCatalog The ResourceCatalog to be created (required)
   * @return ResourceCatalog
   */
  @RequestLine("POST /resourceCatalog")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceCatalog createResourceCatalog(ResourceCatalogCreate resourceCatalog);

  /**
   * Creates a ResourceCatalog
   * Similar to <code>createResourceCatalog</code> but it also returns the http response headers .
   * This operation creates a ResourceCatalog entity.
   * @param resourceCatalog The ResourceCatalog to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /resourceCatalog")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCatalog> createResourceCatalogWithHttpInfo(ResourceCatalogCreate resourceCatalog);



  /**
   * Deletes a ResourceCatalog
   * This operation deletes a ResourceCatalog entity.
   * @param id Identifier of the ResourceCatalog (required)
   */
  @RequestLine("DELETE /resourceCatalog/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteResourceCatalog(@Param("id") String id);

  /**
   * Deletes a ResourceCatalog
   * Similar to <code>deleteResourceCatalog</code> but it also returns the http response headers .
   * This operation deletes a ResourceCatalog entity.
   * @param id Identifier of the ResourceCatalog (required)
   */
  @RequestLine("DELETE /resourceCatalog/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteResourceCatalogWithHttpInfo(@Param("id") String id);



  /**
   * List or find ResourceCatalog objects
   * This operation list or find ResourceCatalog entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ResourceCatalog&gt;
   */
  @RequestLine("GET /resourceCatalog?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ResourceCatalog> listResourceCatalog(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ResourceCatalog objects
   * Similar to <code>listResourceCatalog</code> but it also returns the http response headers .
   * This operation list or find ResourceCatalog entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceCatalog?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ResourceCatalog>> listResourceCatalogWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ResourceCatalog objects
   * This operation list or find ResourceCatalog entities
   * Note, this is equivalent to the other <code>listResourceCatalog</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListResourceCatalogQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ResourceCatalog&gt;
   */
  @RequestLine("GET /resourceCatalog?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ResourceCatalog> listResourceCatalog(@QueryMap(encoded=true) ListResourceCatalogQueryParams queryParams);

  /**
  * List or find ResourceCatalog objects
  * This operation list or find ResourceCatalog entities
  * Note, this is equivalent to the other <code>listResourceCatalog</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ResourceCatalog&gt;
      */
      @RequestLine("GET /resourceCatalog?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ResourceCatalog>> listResourceCatalogWithHttpInfo(@QueryMap(encoded=true) ListResourceCatalogQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listResourceCatalog</code> method in a fluent style.
   */
  public static class ListResourceCatalogQueryParams extends HashMap<String, Object> {
    public ListResourceCatalogQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceCatalogQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceCatalogQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a ResourceCatalog
   * This operation updates partially a ResourceCatalog entity.
   * @param id Identifier of the ResourceCatalog (required)
   * @param resourceCatalog The ResourceCatalog to be updated (required)
   * @return ResourceCatalog
   */
  @RequestLine("PATCH /resourceCatalog/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceCatalog patchResourceCatalog(@Param("id") String id, ResourceCatalogUpdate resourceCatalog);

  /**
   * Updates partially a ResourceCatalog
   * Similar to <code>patchResourceCatalog</code> but it also returns the http response headers .
   * This operation updates partially a ResourceCatalog entity.
   * @param id Identifier of the ResourceCatalog (required)
   * @param resourceCatalog The ResourceCatalog to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /resourceCatalog/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCatalog> patchResourceCatalogWithHttpInfo(@Param("id") String id, ResourceCatalogUpdate resourceCatalog);



  /**
   * Retrieves a ResourceCatalog by ID
   * This operation retrieves a ResourceCatalog entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceCatalog (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ResourceCatalog
   */
  @RequestLine("GET /resourceCatalog/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ResourceCatalog retrieveResourceCatalog(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ResourceCatalog by ID
   * Similar to <code>retrieveResourceCatalog</code> but it also returns the http response headers .
   * This operation retrieves a ResourceCatalog entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceCatalog (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceCatalog/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCatalog> retrieveResourceCatalogWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ResourceCatalog by ID
   * This operation retrieves a ResourceCatalog entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveResourceCatalog</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveResourceCatalogQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ResourceCatalog (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ResourceCatalog
   */
  @RequestLine("GET /resourceCatalog/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ResourceCatalog retrieveResourceCatalog(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceCatalogQueryParams queryParams);

  /**
  * Retrieves a ResourceCatalog by ID
  * This operation retrieves a ResourceCatalog entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveResourceCatalog</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ResourceCatalog (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ResourceCatalog
      */
      @RequestLine("GET /resourceCatalog/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ResourceCatalog> retrieveResourceCatalogWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceCatalogQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveResourceCatalog</code> method in a fluent style.
   */
  public static class RetrieveResourceCatalogQueryParams extends HashMap<String, Object> {
    public RetrieveResourceCatalogQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
