package de.bitconex.tmf.pm.client.api;

import de.bitconex.tmf.pm.client.ApiClient;
import de.bitconex.tmf.pm.client.EncodingUtils;
import de.bitconex.tmf.pm.model.Organization;
import de.bitconex.tmf.pm.model.OrganizationCreate;
import de.bitconex.tmf.pm.model.OrganizationUpdate;
import feign.*;
import de.bitconex.tmf.pm.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface OrganizationApi extends ApiClient.Api {


  /**
   * Creates a Organization
   * This operation creates a Organization entity.
   * @param organization The Organization to be created (required)
   * @return Organization
   */
  @RequestLine("POST /organization")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Organization createOrganization(OrganizationCreate organization);

  /**
   * Creates a Organization
   * Similar to <code>createOrganization</code> but it also returns the http response headers .
   * This operation creates a Organization entity.
   * @param organization The Organization to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /organization")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Organization> createOrganizationWithHttpInfo(OrganizationCreate organization);



  /**
   * Deletes a Organization
   * This operation deletes a Organization entity.
   * @param id Identifier of the Organization (required)
   */
  @RequestLine("DELETE /organization/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteOrganization(@Param("id") String id);

  /**
   * Deletes a Organization
   * Similar to <code>deleteOrganization</code> but it also returns the http response headers .
   * This operation deletes a Organization entity.
   * @param id Identifier of the Organization (required)
   */
  @RequestLine("DELETE /organization/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteOrganizationWithHttpInfo(@Param("id") String id);



  /**
   * List or find Organization objects
   * This operation list or find Organization entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;Organization&gt;
   */
  @RequestLine("GET /organization?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<Organization> listOrganization(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find Organization objects
   * Similar to <code>listOrganization</code> but it also returns the http response headers .
   * This operation list or find Organization entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /organization?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<Organization>> listOrganizationWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find Organization objects
   * This operation list or find Organization entities
   * Note, this is equivalent to the other <code>listOrganization</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListOrganizationQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;Organization&gt;
   */
  @RequestLine("GET /organization?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<Organization> listOrganization(@QueryMap(encoded=true) ListOrganizationQueryParams queryParams);

  /**
  * List or find Organization objects
  * This operation list or find Organization entities
  * Note, this is equivalent to the other <code>listOrganization</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;Organization&gt;
      */
      @RequestLine("GET /organization?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<Organization>> listOrganizationWithHttpInfo(@QueryMap(encoded=true) ListOrganizationQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listOrganization</code> method in a fluent style.
   */
  public static class ListOrganizationQueryParams extends HashMap<String, Object> {
    public ListOrganizationQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListOrganizationQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListOrganizationQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a Organization
   * This operation updates partially a Organization entity.
   * @param id Identifier of the Organization (required)
   * @param organization The Organization to be updated (required)
   * @return Organization
   */
  @RequestLine("PATCH /organization/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Organization patchOrganization(@Param("id") String id, OrganizationUpdate organization);

  /**
   * Updates partially a Organization
   * Similar to <code>patchOrganization</code> but it also returns the http response headers .
   * This operation updates partially a Organization entity.
   * @param id Identifier of the Organization (required)
   * @param organization The Organization to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /organization/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Organization> patchOrganizationWithHttpInfo(@Param("id") String id, OrganizationUpdate organization);



  /**
   * Retrieves a Organization by ID
   * This operation retrieves a Organization entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Organization (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return Organization
   */
  @RequestLine("GET /organization/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  Organization retrieveOrganization(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a Organization by ID
   * Similar to <code>retrieveOrganization</code> but it also returns the http response headers .
   * This operation retrieves a Organization entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Organization (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /organization/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Organization> retrieveOrganizationWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a Organization by ID
   * This operation retrieves a Organization entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveOrganization</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveOrganizationQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the Organization (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return Organization
   */
  @RequestLine("GET /organization/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  Organization retrieveOrganization(@Param("id") String id, @QueryMap(encoded=true) RetrieveOrganizationQueryParams queryParams);

  /**
  * Retrieves a Organization by ID
  * This operation retrieves a Organization entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveOrganization</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the Organization (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return Organization
      */
      @RequestLine("GET /organization/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<Organization> retrieveOrganizationWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveOrganizationQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveOrganization</code> method in a fluent style.
   */
  public static class RetrieveOrganizationQueryParams extends HashMap<String, Object> {
    public RetrieveOrganizationQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
