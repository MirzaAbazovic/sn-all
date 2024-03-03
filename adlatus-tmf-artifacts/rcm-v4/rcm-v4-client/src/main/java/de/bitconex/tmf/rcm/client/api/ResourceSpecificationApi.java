package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.EncodingUtils;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecificationCreate;
import de.bitconex.tmf.rcm.model.ResourceSpecificationUpdate;
import feign.*;
import de.bitconex.tmf.rcm.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ResourceSpecificationApi extends ApiClient.Api {


  /**
   * Creates a ResourceSpecification
   * This operation creates a ResourceSpecification entity.
   * @param resourceSpecification The ResourceSpecification to be created (required)
   * @return ResourceSpecification
   */
  @RequestLine("POST /resourceSpecification")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceSpecification createResourceSpecification(ResourceSpecificationCreate resourceSpecification);

  /**
   * Creates a ResourceSpecification
   * Similar to <code>createResourceSpecification</code> but it also returns the http response headers .
   * This operation creates a ResourceSpecification entity.
   * @param resourceSpecification The ResourceSpecification to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /resourceSpecification")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceSpecification> createResourceSpecificationWithHttpInfo(ResourceSpecificationCreate resourceSpecification);



  /**
   * Deletes a ResourceSpecification
   * This operation deletes a ResourceSpecification entity.
   * @param id Identifier of the ResourceSpecification (required)
   */
  @RequestLine("DELETE /resourceSpecification/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteResourceSpecification(@Param("id") String id);

  /**
   * Deletes a ResourceSpecification
   * Similar to <code>deleteResourceSpecification</code> but it also returns the http response headers .
   * This operation deletes a ResourceSpecification entity.
   * @param id Identifier of the ResourceSpecification (required)
   */
  @RequestLine("DELETE /resourceSpecification/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteResourceSpecificationWithHttpInfo(@Param("id") String id);



  /**
   * List or find ResourceSpecification objects
   * This operation list or find ResourceSpecification entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ResourceSpecification&gt;
   */
  @RequestLine("GET /resourceSpecification?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ResourceSpecification> listResourceSpecification(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ResourceSpecification objects
   * Similar to <code>listResourceSpecification</code> but it also returns the http response headers .
   * This operation list or find ResourceSpecification entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceSpecification?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ResourceSpecification>> listResourceSpecificationWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ResourceSpecification objects
   * This operation list or find ResourceSpecification entities
   * Note, this is equivalent to the other <code>listResourceSpecification</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListResourceSpecificationQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ResourceSpecification&gt;
   */
  @RequestLine("GET /resourceSpecification?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ResourceSpecification> listResourceSpecification(@QueryMap(encoded=true) ListResourceSpecificationQueryParams queryParams);

  /**
  * List or find ResourceSpecification objects
  * This operation list or find ResourceSpecification entities
  * Note, this is equivalent to the other <code>listResourceSpecification</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ResourceSpecification&gt;
      */
      @RequestLine("GET /resourceSpecification?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ResourceSpecification>> listResourceSpecificationWithHttpInfo(@QueryMap(encoded=true) ListResourceSpecificationQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listResourceSpecification</code> method in a fluent style.
   */
  public static class ListResourceSpecificationQueryParams extends HashMap<String, Object> {
    public ListResourceSpecificationQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceSpecificationQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListResourceSpecificationQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a ResourceSpecification
   * This operation updates partially a ResourceSpecification entity.
   * @param id Identifier of the ResourceSpecification (required)
   * @param resourceSpecification The ResourceSpecification to be updated (required)
   * @return ResourceSpecification
   */
  @RequestLine("PATCH /resourceSpecification/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ResourceSpecification patchResourceSpecification(@Param("id") String id, ResourceSpecificationUpdate resourceSpecification);

  /**
   * Updates partially a ResourceSpecification
   * Similar to <code>patchResourceSpecification</code> but it also returns the http response headers .
   * This operation updates partially a ResourceSpecification entity.
   * @param id Identifier of the ResourceSpecification (required)
   * @param resourceSpecification The ResourceSpecification to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /resourceSpecification/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceSpecification> patchResourceSpecificationWithHttpInfo(@Param("id") String id, ResourceSpecificationUpdate resourceSpecification);



  /**
   * Retrieves a ResourceSpecification by ID
   * This operation retrieves a ResourceSpecification entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceSpecification (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ResourceSpecification
   */
  @RequestLine("GET /resourceSpecification/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ResourceSpecification retrieveResourceSpecification(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ResourceSpecification by ID
   * Similar to <code>retrieveResourceSpecification</code> but it also returns the http response headers .
   * This operation retrieves a ResourceSpecification entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ResourceSpecification (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /resourceSpecification/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ResourceSpecification> retrieveResourceSpecificationWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ResourceSpecification by ID
   * This operation retrieves a ResourceSpecification entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveResourceSpecification</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveResourceSpecificationQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ResourceSpecification (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ResourceSpecification
   */
  @RequestLine("GET /resourceSpecification/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ResourceSpecification retrieveResourceSpecification(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceSpecificationQueryParams queryParams);

  /**
  * Retrieves a ResourceSpecification by ID
  * This operation retrieves a ResourceSpecification entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveResourceSpecification</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ResourceSpecification (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ResourceSpecification
      */
      @RequestLine("GET /resourceSpecification/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ResourceSpecification> retrieveResourceSpecificationWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveResourceSpecificationQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveResourceSpecification</code> method in a fluent style.
   */
  public static class RetrieveResourceSpecificationQueryParams extends HashMap<String, Object> {
    public RetrieveResourceSpecificationQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
