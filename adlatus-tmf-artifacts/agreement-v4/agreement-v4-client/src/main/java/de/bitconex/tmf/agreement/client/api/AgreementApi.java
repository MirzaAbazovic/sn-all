package de.bitconex.tmf.agreement.client.api;

import de.bitconex.tmf.agreement.client.ApiClient;
import de.bitconex.tmf.agreement.client.ApiResponse;
import de.bitconex.tmf.agreement.client.EncodingUtils;

import de.bitconex.tmf.agreement.model.Agreement;
import de.bitconex.tmf.agreement.model.AgreementCreate;
import de.bitconex.tmf.agreement.model.AgreementUpdate;
import de.bitconex.tmf.agreement.model.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface AgreementApi extends ApiClient.Api {


  /**
   * Creates a Agreement
   * This operation creates a Agreement entity.
   * @param agreement The Agreement to be created (required)
   * @return Agreement
   */
  @RequestLine("POST /agreement")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Agreement createAgreement(AgreementCreate agreement);

  /**
   * Creates a Agreement
   * Similar to <code>createAgreement</code> but it also returns the http response headers .
   * This operation creates a Agreement entity.
   * @param agreement The Agreement to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /agreement")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Agreement> createAgreementWithHttpInfo(AgreementCreate agreement);



  /**
   * Deletes a Agreement
   * This operation deletes a Agreement entity.
   * @param id Identifier of the Agreement (required)
   */
  @RequestLine("DELETE /agreement/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteAgreement(@Param("id") String id);

  /**
   * Deletes a Agreement
   * Similar to <code>deleteAgreement</code> but it also returns the http response headers .
   * This operation deletes a Agreement entity.
   * @param id Identifier of the Agreement (required)
   */
  @RequestLine("DELETE /agreement/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteAgreementWithHttpInfo(@Param("id") String id);



  /**
   * List or find Agreement objects
   * This operation list or find Agreement entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;Agreement&gt;
   */
  @RequestLine("GET /agreement?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<Agreement> listAgreement(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find Agreement objects
   * Similar to <code>listAgreement</code> but it also returns the http response headers .
   * This operation list or find Agreement entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /agreement?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<Agreement>> listAgreementWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find Agreement objects
   * This operation list or find Agreement entities
   * Note, this is equivalent to the other <code>listAgreement</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListAgreementQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;Agreement&gt;
   */
  @RequestLine("GET /agreement?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<Agreement> listAgreement(@QueryMap(encoded=true) ListAgreementQueryParams queryParams);

  /**
  * List or find Agreement objects
  * This operation list or find Agreement entities
  * Note, this is equivalent to the other <code>listAgreement</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;Agreement&gt;
      */
      @RequestLine("GET /agreement?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<Agreement>> listAgreementWithHttpInfo(@QueryMap(encoded=true) ListAgreementQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listAgreement</code> method in a fluent style.
   */
  public static class ListAgreementQueryParams extends HashMap<String, Object> {
    public ListAgreementQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListAgreementQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListAgreementQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a Agreement
   * This operation updates partially a Agreement entity.
   * @param id Identifier of the Agreement (required)
   * @param agreement The Agreement to be updated (required)
   * @return Agreement
   */
  @RequestLine("PATCH /agreement/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Agreement patchAgreement(@Param("id") String id, AgreementUpdate agreement);

  /**
   * Updates partially a Agreement
   * Similar to <code>patchAgreement</code> but it also returns the http response headers .
   * This operation updates partially a Agreement entity.
   * @param id Identifier of the Agreement (required)
   * @param agreement The Agreement to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /agreement/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Agreement> patchAgreementWithHttpInfo(@Param("id") String id, AgreementUpdate agreement);



  /**
   * Retrieves a Agreement by ID
   * This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Agreement (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return Agreement
   */
  @RequestLine("GET /agreement/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  Agreement retrieveAgreement(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a Agreement by ID
   * Similar to <code>retrieveAgreement</code> but it also returns the http response headers .
   * This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Agreement (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /agreement/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Agreement> retrieveAgreementWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a Agreement by ID
   * This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveAgreement</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveAgreementQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the Agreement (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return Agreement
   */
  @RequestLine("GET /agreement/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  Agreement retrieveAgreement(@Param("id") String id, @QueryMap(encoded=true) RetrieveAgreementQueryParams queryParams);

  /**
  * Retrieves a Agreement by ID
  * This operation retrieves a Agreement entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveAgreement</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the Agreement (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return Agreement
      */
      @RequestLine("GET /agreement/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<Agreement> retrieveAgreementWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveAgreementQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveAgreement</code> method in a fluent style.
   */
  public static class RetrieveAgreementQueryParams extends HashMap<String, Object> {
    public RetrieveAgreementQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
