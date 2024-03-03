package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.EncodingUtils;
import de.bitconex.tmf.rcm.model.ResourceCandidate;
import de.bitconex.tmf.rcm.model.ResourceCandidateCreate;
import de.bitconex.tmf.rcm.model.ResourceCandidateUpdate;
import feign.*;
import de.bitconex.tmf.rcm.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ResourceCandidateApi extends ApiClient.Api {


  /**
   * Creates a ResourceCandidate
   * This operation creates a ResourceCandidate entity.
   * @param resourceCandidate The ResourceCandidate to be created (required)
   * @return ResourceCandidate
   */
  @RequestLine("POST /resourceCandidate")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceCandidate createResourceCandidate(ResourceCandidateCreate resourceCandidate);

  /**
   * Creates a ResourceCandidate
   * Similar to <code>createResourceCandidate</code> but it also returns the http response headers .
   * This operation creates a ResourceCandidate entity.
   * @param resourceCandidate The ResourceCandidate to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /resourceCandidate")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCandidate> createResourceCandidateWithHttpInfo(ResourceCandidateCreate resourceCandidate);



  /**
   * Deletes a ResourceCandidate
   * This operation deletes a ResourceCandidate entity.
   * @param id Identifier of the ResourceCandidate (required)
   */
  @RequestLine("DELETE /resourceCandidate/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteResourceCandidate(@Param("id") String id);

  /**
   * Deletes a ResourceCandidate
   * Similar to <code>deleteResourceCandidate</code> but it also returns the http response headers .
   * This operation deletes a ResourceCandidate entity.
   * @param id Identifier of the ResourceCandidate (required)
   */
  @RequestLine("DELETE /resourceCandidate/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteResourceCandidateWithHttpInfo(@Param("id") String id);



  /**
   * List or find ResourceCandidate objects
   * This operation list or find ResourceCandidate entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ResourceCandidate&gt;
   */
  @RequestLine("GET /resourceCandidate?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ResourceCandidate> listResourceCandidate(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ResourceCandidate objects
   * Similar to <code>listResourceCandidate</code> but it also returns the http response headers .
   * This operation list or find ResourceCandidate entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceCandidate?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ResourceCandidate>> listResourceCandidateWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ResourceCandidate objects
   * This operation list or find ResourceCandidate entities
   * Note, this is equivalent to the other <code>listResourceCandidate</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListResourceCandidateQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ResourceCandidate&gt;
   */
  @RequestLine("GET /resourceCandidate?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ResourceCandidate> listResourceCandidate(@QueryMap(encoded=true) ListResourceCandidateQueryParams queryParams);

  /**
  * List or find ResourceCandidate objects
  * This operation list or find ResourceCandidate entities
  * Note, this is equivalent to the other <code>listResourceCandidate</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ResourceCandidate&gt;
      */
      @RequestLine("GET /resourceCandidate?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ResourceCandidate>> listResourceCandidateWithHttpInfo(@QueryMap(encoded=true) ListResourceCandidateQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listResourceCandidate</code> method in a fluent style.
   */
  public static class ListResourceCandidateQueryParams extends HashMap<String, Object> {
    public ListResourceCandidateQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceCandidateQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceCandidateQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a ResourceCandidate
   * This operation updates partially a ResourceCandidate entity.
   * @param id Identifier of the ResourceCandidate (required)
   * @param resourceCandidate The ResourceCandidate to be updated (required)
   * @return ResourceCandidate
   */
  @RequestLine("PATCH /resourceCandidate/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceCandidate patchResourceCandidate(@Param("id") String id, ResourceCandidateUpdate resourceCandidate);

  /**
   * Updates partially a ResourceCandidate
   * Similar to <code>patchResourceCandidate</code> but it also returns the http response headers .
   * This operation updates partially a ResourceCandidate entity.
   * @param id Identifier of the ResourceCandidate (required)
   * @param resourceCandidate The ResourceCandidate to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /resourceCandidate/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCandidate> patchResourceCandidateWithHttpInfo(@Param("id") String id, ResourceCandidateUpdate resourceCandidate);



  /**
   * Retrieves a ResourceCandidate by ID
   * This operation retrieves a ResourceCandidate entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceCandidate (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ResourceCandidate
   */
  @RequestLine("GET /resourceCandidate/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ResourceCandidate retrieveResourceCandidate(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ResourceCandidate by ID
   * Similar to <code>retrieveResourceCandidate</code> but it also returns the http response headers .
   * This operation retrieves a ResourceCandidate entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceCandidate (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceCandidate/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceCandidate> retrieveResourceCandidateWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ResourceCandidate by ID
   * This operation retrieves a ResourceCandidate entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveResourceCandidate</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveResourceCandidateQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ResourceCandidate (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ResourceCandidate
   */
  @RequestLine("GET /resourceCandidate/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ResourceCandidate retrieveResourceCandidate(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceCandidateQueryParams queryParams);

  /**
  * Retrieves a ResourceCandidate by ID
  * This operation retrieves a ResourceCandidate entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveResourceCandidate</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ResourceCandidate (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ResourceCandidate
      */
      @RequestLine("GET /resourceCandidate/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ResourceCandidate> retrieveResourceCandidateWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceCandidateQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveResourceCandidate</code> method in a fluent style.
   */
  public static class RetrieveResourceCandidateQueryParams extends HashMap<String, Object> {
    public RetrieveResourceCandidateQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
