package de.bitconex.tmf.agreement.client.api;

import de.bitconex.tmf.agreement.client.ApiClient;
import de.bitconex.tmf.agreement.client.ApiResponse;
import de.bitconex.tmf.agreement.client.EncodingUtils;

import de.bitconex.tmf.agreement.model.AgreementSpecification;
import de.bitconex.tmf.agreement.model.AgreementSpecificationCreate;
import de.bitconex.tmf.agreement.model.AgreementSpecificationUpdate;
import de.bitconex.tmf.agreement.model.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface AgreementSpecificationApi extends ApiClient.Api {


  /**
   * Creates a AgreementSpecification
   * This operation creates a AgreementSpecification entity.
   * @param agreementSpecification The AgreementSpecification to be created (required)
   * @return AgreementSpecification
   */
  @RequestLine("POST /agreementSpecification")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  AgreementSpecification createAgreementSpecification(AgreementSpecificationCreate agreementSpecification);

  /**
   * Creates a AgreementSpecification
   * Similar to <code>createAgreementSpecification</code> but it also returns the http response headers .
   * This operation creates a AgreementSpecification entity.
   * @param agreementSpecification The AgreementSpecification to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /agreementSpecification")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<AgreementSpecification> createAgreementSpecificationWithHttpInfo(AgreementSpecificationCreate agreementSpecification);



  /**
   * Deletes a AgreementSpecification
   * This operation deletes a AgreementSpecification entity.
   * @param id Identifier of the AgreementSpecification (required)
   */
  @RequestLine("DELETE /agreementSpecification/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteAgreementSpecification(@Param("id") String id);

  /**
   * Deletes a AgreementSpecification
   * Similar to <code>deleteAgreementSpecification</code> but it also returns the http response headers .
   * This operation deletes a AgreementSpecification entity.
   * @param id Identifier of the AgreementSpecification (required)
   */
  @RequestLine("DELETE /agreementSpecification/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteAgreementSpecificationWithHttpInfo(@Param("id") String id);



  /**
   * List or find AgreementSpecification objects
   * This operation list or find AgreementSpecification entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;AgreementSpecification&gt;
   */
  @RequestLine("GET /agreementSpecification?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<AgreementSpecification> listAgreementSpecification(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find AgreementSpecification objects
   * Similar to <code>listAgreementSpecification</code> but it also returns the http response headers .
   * This operation list or find AgreementSpecification entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /agreementSpecification?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<AgreementSpecification>> listAgreementSpecificationWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find AgreementSpecification objects
   * This operation list or find AgreementSpecification entities
   * Note, this is equivalent to the other <code>listAgreementSpecification</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListAgreementSpecificationQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;AgreementSpecification&gt;
   */
  @RequestLine("GET /agreementSpecification?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<AgreementSpecification> listAgreementSpecification(@QueryMap(encoded=true) ListAgreementSpecificationQueryParams queryParams);

  /**
  * List or find AgreementSpecification objects
  * This operation list or find AgreementSpecification entities
  * Note, this is equivalent to the other <code>listAgreementSpecification</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;AgreementSpecification&gt;
      */
      @RequestLine("GET /agreementSpecification?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<AgreementSpecification>> listAgreementSpecificationWithHttpInfo(@QueryMap(encoded=true) ListAgreementSpecificationQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listAgreementSpecification</code> method in a fluent style.
   */
  public static class ListAgreementSpecificationQueryParams extends HashMap<String, Object> {
    public ListAgreementSpecificationQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListAgreementSpecificationQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListAgreementSpecificationQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a AgreementSpecification
   * This operation updates partially a AgreementSpecification entity.
   * @param id Identifier of the AgreementSpecification (required)
   * @param agreementSpecification The AgreementSpecification to be updated (required)
   * @return AgreementSpecification
   */
  @RequestLine("PATCH /agreementSpecification/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  AgreementSpecification patchAgreementSpecification(@Param("id") String id, AgreementSpecificationUpdate agreementSpecification);

  /**
   * Updates partially a AgreementSpecification
   * Similar to <code>patchAgreementSpecification</code> but it also returns the http response headers .
   * This operation updates partially a AgreementSpecification entity.
   * @param id Identifier of the AgreementSpecification (required)
   * @param agreementSpecification The AgreementSpecification to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /agreementSpecification/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<AgreementSpecification> patchAgreementSpecificationWithHttpInfo(@Param("id") String id, AgreementSpecificationUpdate agreementSpecification);



  /**
   * Retrieves a AgreementSpecification by ID
   * This operation retrieves a AgreementSpecification entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the AgreementSpecification (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return AgreementSpecification
   */
  @RequestLine("GET /agreementSpecification/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  AgreementSpecification retrieveAgreementSpecification(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a AgreementSpecification by ID
   * Similar to <code>retrieveAgreementSpecification</code> but it also returns the http response headers .
   * This operation retrieves a AgreementSpecification entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the AgreementSpecification (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /agreementSpecification/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<AgreementSpecification> retrieveAgreementSpecificationWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a AgreementSpecification by ID
   * This operation retrieves a AgreementSpecification entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveAgreementSpecification</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveAgreementSpecificationQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the AgreementSpecification (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return AgreementSpecification
   */
  @RequestLine("GET /agreementSpecification/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  AgreementSpecification retrieveAgreementSpecification(@Param("id") String id, @QueryMap(encoded=true) RetrieveAgreementSpecificationQueryParams queryParams);

  /**
  * Retrieves a AgreementSpecification by ID
  * This operation retrieves a AgreementSpecification entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveAgreementSpecification</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the AgreementSpecification (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return AgreementSpecification
      */
      @RequestLine("GET /agreementSpecification/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<AgreementSpecification> retrieveAgreementSpecificationWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveAgreementSpecificationQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveAgreementSpecification</code> method in a fluent style.
   */
  public static class RetrieveAgreementSpecificationQueryParams extends HashMap<String, Object> {
    public RetrieveAgreementSpecificationQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
