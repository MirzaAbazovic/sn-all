package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.EncodingUtils;
import de.bitconex.tmf.rcm.model.ImportJob;
import de.bitconex.tmf.rcm.model.ImportJobCreate;
import feign.*;
import de.bitconex.tmf.rcm.client.ApiResponse;

import java.util.HashMap;
import java.util.List;


public interface ImportJobApi extends ApiClient.Api {


  /**
   * Creates a ImportJob
   * This operation creates a ImportJob entity.
   * @param importJob The ImportJob to be created (required)
   * @return ImportJob
   */
  @RequestLine("POST /importJob")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ImportJob createImportJob(ImportJobCreate importJob);

  /**
   * Creates a ImportJob
   * Similar to <code>createImportJob</code> but it also returns the http response headers .
   * This operation creates a ImportJob entity.
   * @param importJob The ImportJob to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /importJob")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ImportJob> createImportJobWithHttpInfo(ImportJobCreate importJob);



  /**
   * Deletes a ImportJob
   * This operation deletes a ImportJob entity.
   * @param id Identifier of the ImportJob (required)
   */
  @RequestLine("DELETE /importJob/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteImportJob(@Param("id") String id);

  /**
   * Deletes a ImportJob
   * Similar to <code>deleteImportJob</code> but it also returns the http response headers .
   * This operation deletes a ImportJob entity.
   * @param id Identifier of the ImportJob (required)
   */
  @RequestLine("DELETE /importJob/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteImportJobWithHttpInfo(@Param("id") String id);



  /**
   * List or find ImportJob objects
   * This operation list or find ImportJob entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ImportJob&gt;
   */
  @RequestLine("GET /importJob?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ImportJob> listImportJob(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ImportJob objects
   * Similar to <code>listImportJob</code> but it also returns the http response headers .
   * This operation list or find ImportJob entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /importJob?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ImportJob>> listImportJobWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ImportJob objects
   * This operation list or find ImportJob entities
   * Note, this is equivalent to the other <code>listImportJob</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListImportJobQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ImportJob&gt;
   */
  @RequestLine("GET /importJob?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ImportJob> listImportJob(@QueryMap(encoded=true) ListImportJobQueryParams queryParams);

  /**
  * List or find ImportJob objects
  * This operation list or find ImportJob entities
  * Note, this is equivalent to the other <code>listImportJob</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ImportJob&gt;
      */
      @RequestLine("GET /importJob?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ImportJob>> listImportJobWithHttpInfo(@QueryMap(encoded=true) ListImportJobQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listImportJob</code> method in a fluent style.
   */
  public static class ListImportJobQueryParams extends HashMap<String, Object> {
    public ListImportJobQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListImportJobQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListImportJobQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Retrieves a ImportJob by ID
   * This operation retrieves a ImportJob entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ImportJob (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ImportJob
   */
  @RequestLine("GET /importJob/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ImportJob retrieveImportJob(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ImportJob by ID
   * Similar to <code>retrieveImportJob</code> but it also returns the http response headers .
   * This operation retrieves a ImportJob entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ImportJob (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /importJob/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ImportJob> retrieveImportJobWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ImportJob by ID
   * This operation retrieves a ImportJob entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveImportJob</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveImportJobQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ImportJob (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ImportJob
   */
  @RequestLine("GET /importJob/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ImportJob retrieveImportJob(@Param("id") String id, @QueryMap(encoded=true) RetrieveImportJobQueryParams queryParams);

  /**
  * Retrieves a ImportJob by ID
  * This operation retrieves a ImportJob entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveImportJob</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ImportJob (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ImportJob
      */
      @RequestLine("GET /importJob/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ImportJob> retrieveImportJobWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveImportJobQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveImportJob</code> method in a fluent style.
   */
  public static class RetrieveImportJobQueryParams extends HashMap<String, Object> {
    public RetrieveImportJobQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
