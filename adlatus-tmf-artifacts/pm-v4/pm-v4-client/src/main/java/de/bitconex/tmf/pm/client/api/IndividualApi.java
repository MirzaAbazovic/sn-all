package de.bitconex.tmf.pm.client.api;

import de.bitconex.tmf.pm.client.ApiClient;
import de.bitconex.tmf.pm.client.ApiResponse;
import de.bitconex.tmf.pm.client.EncodingUtils;
import de.bitconex.tmf.pm.model.Individual;
import de.bitconex.tmf.pm.model.IndividualCreate;
import de.bitconex.tmf.pm.model.IndividualUpdate;
import feign.*;

import java.util.HashMap;
import java.util.List;


public interface IndividualApi extends ApiClient.Api {


  /**
   * Creates a Individual
   * This operation creates a Individual entity.
   * @param individual The Individual to be created (required)
   * @return Individual
   */
  @RequestLine("POST /individual")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Individual createIndividual(IndividualCreate individual);

  /**
   * Creates a Individual
   * Similar to <code>createIndividual</code> but it also returns the http response headers .
   * This operation creates a Individual entity.
   * @param individual The Individual to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /individual")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Individual> createIndividualWithHttpInfo(IndividualCreate individual);



  /**
   * Deletes a Individual
   * This operation deletes a Individual entity.
   * @param id Identifier of the Individual (required)
   */
  @RequestLine("DELETE /individual/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteIndividual(@Param("id") String id);

  /**
   * Deletes a Individual
   * Similar to <code>deleteIndividual</code> but it also returns the http response headers .
   * This operation deletes a Individual entity.
   * @param id Identifier of the Individual (required)
   */
  @RequestLine("DELETE /individual/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteIndividualWithHttpInfo(@Param("id") String id);



  /**
   * List or find Individual objects
   * This operation list or find Individual entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;Individual&gt;
   */
  @RequestLine("GET /individual?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<Individual> listIndividual(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find Individual objects
   * Similar to <code>listIndividual</code> but it also returns the http response headers .
   * This operation list or find Individual entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /individual?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<Individual>> listIndividualWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find Individual objects
   * This operation list or find Individual entities
   * Note, this is equivalent to the other <code>listIndividual</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListIndividualQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;Individual&gt;
   */
  @RequestLine("GET /individual?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<Individual> listIndividual(@QueryMap(encoded=true) ListIndividualQueryParams queryParams);

  /**
  * List or find Individual objects
  * This operation list or find Individual entities
  * Note, this is equivalent to the other <code>listIndividual</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;Individual&gt;
      */
      @RequestLine("GET /individual?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<Individual>> listIndividualWithHttpInfo(@QueryMap(encoded=true) ListIndividualQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listIndividual</code> method in a fluent style.
   */
  public static class ListIndividualQueryParams extends HashMap<String, Object> {
    public ListIndividualQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListIndividualQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListIndividualQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Updates partially a Individual
   * This operation updates partially a Individual entity.
   * @param id Identifier of the Individual (required)
   * @param individual The Individual to be updated (required)
   * @return Individual
   */
  @RequestLine("PATCH /individual/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  Individual patchIndividual(@Param("id") String id, IndividualUpdate individual);

  /**
   * Updates partially a Individual
   * Similar to <code>patchIndividual</code> but it also returns the http response headers .
   * This operation updates partially a Individual entity.
   * @param id Identifier of the Individual (required)
   * @param individual The Individual to be updated (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("PATCH /individual/{id}")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Individual> patchIndividualWithHttpInfo(@Param("id") String id, IndividualUpdate individual);



  /**
   * Retrieves a Individual by ID
   * This operation retrieves a Individual entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Individual (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return Individual
   */
  @RequestLine("GET /individual/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  Individual retrieveIndividual(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a Individual by ID
   * Similar to <code>retrieveIndividual</code> but it also returns the http response headers .
   * This operation retrieves a Individual entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the Individual (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /individual/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Individual> retrieveIndividualWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a Individual by ID
   * This operation retrieves a Individual entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveIndividual</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveIndividualQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the Individual (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return Individual
   */
  @RequestLine("GET /individual/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  Individual retrieveIndividual(@Param("id") String id, @QueryMap(encoded=true) RetrieveIndividualQueryParams queryParams);

  /**
  * Retrieves a Individual by ID
  * This operation retrieves a Individual entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveIndividual</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the Individual (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return Individual
      */
      @RequestLine("GET /individual/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<Individual> retrieveIndividualWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveIndividualQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveIndividual</code> method in a fluent style.
   */
  public static class RetrieveIndividualQueryParams extends HashMap<String, Object> {
    public RetrieveIndividualQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
