package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.EncodingUtils;
import de.bitconex.tmf.rcm.model.ResourceCategory;
import de.bitconex.tmf.rcm.model.ResourceCategoryCreate;
import de.bitconex.tmf.rcm.model.ResourceCategoryUpdate;
import feign.*;
import de.bitconex.tmf.rcm.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ResourceCategoryApi extends ApiClient.Api {


  /**
   * Creates a ResourceCategory
   * This operation creates a ResourceCategory entity.
   * @param resourceCategory The ResourceCategory to be created (required)
   * @return ResourceCategory
   */
  @RequestLine("POST /resourceCategory")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceCategory createResourceCategory(ResourceCategoryCreate resourceCategory);

  /**
   * Creates a ResourceCategory
   * Similar to <code>createResourceCategory</code> but it also returns the http response headers .
   * This operation creates a ResourceCategory entity.
   * @param resourceCategory The ResourceCategory to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /resourceCategory")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCategory> createResourceCategoryWithHttpInfo(ResourceCategoryCreate resourceCategory);



  /**
   * Deletes a ResourceCategory
   * This operation deletes a ResourceCategory entity.
   * @param id Identifier of the ResourceCategory (required)
   */
  @RequestLine("DELETE /resourceCategory/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteResourceCategory(@Param("id") String id);

  /**
   * Deletes a ResourceCategory
   * Similar to <code>deleteResourceCategory</code> but it also returns the http response headers .
   * This operation deletes a ResourceCategory entity.
   * @param id Identifier of the ResourceCategory (required)
   */
  @RequestLine("DELETE /resourceCategory/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteResourceCategoryWithHttpInfo(@Param("id") String id);



  /**
   * List or find ResourceCategory objects
   * This operation list or find ResourceCategory entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ResourceCategory&gt;
   */
  @RequestLine("GET /resourceCategory?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ResourceCategory> listResourceCategory(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ResourceCategory objects
   * Similar to <code>listResourceCategory</code> but it also returns the http response headers .
   * This operation list or find ResourceCategory entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceCategory?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ResourceCategory>> listResourceCategoryWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ResourceCategory objects
   * This operation list or find ResourceCategory entities
   * Note, this is equivalent to the other <code>listResourceCategory</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListResourceCategoryQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ResourceCategory&gt;
   */
  @RequestLine("GET /resourceCategory?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ResourceCategory> listResourceCategory(@QueryMap(encoded=true) ListResourceCategoryQueryParams queryParams);

  /**
  * List or find ResourceCategory objects
  * This operation list or find ResourceCategory entities
  * Note, this is equivalent to the other <code>listResourceCategory</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ResourceCategory&gt;
      */
      @RequestLine("GET /resourceCategory?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ResourceCategory>> listResourceCategoryWithHttpInfo(@QueryMap(encoded=true) ListResourceCategoryQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listResourceCategory</code> method in a fluent style.
   */
  public static class ListResourceCategoryQueryParams extends HashMap<String, Object> {
    public ListResourceCategoryQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceCategoryQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceCategoryQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a ResourceCategory
   * This operation updates partially a ResourceCategory entity.
   * @param id Identifier of the ResourceCategory (required)
   * @param resourceCategory The ResourceCategory to be updated (required)
   * @return ResourceCategory
   */
  @RequestLine("PATCH /resourceCategory/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceCategory patchResourceCategory(@Param("id") String id, ResourceCategoryUpdate resourceCategory);

  /**
   * Updates partially a ResourceCategory
   * Similar to <code>patchResourceCategory</code> but it also returns the http response headers .
   * This operation updates partially a ResourceCategory entity.
   * @param id Identifier of the ResourceCategory (required)
   * @param resourceCategory The ResourceCategory to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /resourceCategory/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCategory> patchResourceCategoryWithHttpInfo(@Param("id") String id, ResourceCategoryUpdate resourceCategory);



  /**
   * Retrieves a ResourceCategory by ID
   * This operation retrieves a ResourceCategory entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceCategory (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ResourceCategory
   */
  @RequestLine("GET /resourceCategory/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ResourceCategory retrieveResourceCategory(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ResourceCategory by ID
   * Similar to <code>retrieveResourceCategory</code> but it also returns the http response headers .
   * This operation retrieves a ResourceCategory entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceCategory (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceCategory/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCategory> retrieveResourceCategoryWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ResourceCategory by ID
   * This operation retrieves a ResourceCategory entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveResourceCategory</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveResourceCategoryQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ResourceCategory (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ResourceCategory
   */
  @RequestLine("GET /resourceCategory/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ResourceCategory retrieveResourceCategory(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceCategoryQueryParams queryParams);

  /**
  * Retrieves a ResourceCategory by ID
  * This operation retrieves a ResourceCategory entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveResourceCategory</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ResourceCategory (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ResourceCategory
      */
      @RequestLine("GET /resourceCategory/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ResourceCategory> retrieveResourceCategoryWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceCategoryQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveResourceCategory</code> method in a fluent style.
   */
  public static class RetrieveResourceCategoryQueryParams extends HashMap<String, Object> {
    public RetrieveResourceCategoryQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
