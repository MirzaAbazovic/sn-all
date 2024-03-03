package de.bitconex.tmf.rcm.client.api;

import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.ApiResponse;
import de.bitconex.tmf.rcm.client.EncodingUtils;
import de.bitconex.tmf.rcm.model.ExportJob;
import de.bitconex.tmf.rcm.model.ExportJobCreate;
import feign.*;

import java.util.HashMap;
import java.util.List;


public interface ExportJobApi extends ApiClient.Api {


  /**
   * Creates a ExportJob
   * This operation creates a ExportJob entity.
   * @param exportJob The ExportJob to be created (required)
   * @return ExportJob
   */
  @RequestLine("POST /exportJob")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ExportJob createExportJob(ExportJobCreate exportJob);

  /**
   * Creates a ExportJob
   * Similar to <code>createExportJob</code> but it also returns the http response headers .
   * This operation creates a ExportJob entity.
   * @param exportJob The ExportJob to be created (required)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("POST /exportJob")
  @Headers({
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ExportJob> createExportJobWithHttpInfo(ExportJobCreate exportJob);



  /**
   * Deletes a ExportJob
   * This operation deletes a ExportJob entity.
   * @param id Identifier of the ExportJob (required)
   */
  @RequestLine("DELETE /exportJob/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  void deleteExportJob(@Param("id") String id);

  /**
   * Deletes a ExportJob
   * Similar to <code>deleteExportJob</code> but it also returns the http response headers .
   * This operation deletes a ExportJob entity.
   * @param id Identifier of the ExportJob (required)
   */
  @RequestLine("DELETE /exportJob/{id}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<Void> deleteExportJobWithHttpInfo(@Param("id") String id);



  /**
   * List or find ExportJob objects
   * This operation list or find ExportJob entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return List&lt;ExportJob&gt;
   */
  @RequestLine("GET /exportJob?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  List<ExportJob> listExportJob(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);

  /**
   * List or find ExportJob objects
   * Similar to <code>listExportJob</code> but it also returns the http response headers .
   * This operation list or find ExportJob entities
   * @param fields Comma-separated properties to be provided in response (optional)
   * @param offset Requested index for start of resources to be provided in response (optional)
   * @param limit Requested number of resources to be provided in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /exportJob?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<List<ExportJob>> listExportJobWithHttpInfo(@Param("fields") String fields, @Param("offset") Integer offset, @Param("limit") Integer limit);


  /**
   * List or find ExportJob objects
   * This operation list or find ExportJob entities
   * Note, this is equivalent to the other <code>listExportJob</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link ListExportJobQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
   *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
   *   <li>limit - Requested number of resources to be provided in response (optional)</li>
   *   </ul>
   * @return List&lt;ExportJob&gt;
   */
  @RequestLine("GET /exportJob?fields={fields}&offset={offset}&limit={limit}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  List<ExportJob> listExportJob(@QueryMap(encoded=true) ListExportJobQueryParams queryParams);

  /**
  * List or find ExportJob objects
  * This operation list or find ExportJob entities
  * Note, this is equivalent to the other <code>listExportJob</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to be provided in response (optional)</li>
          *   <li>offset - Requested index for start of resources to be provided in response (optional)</li>
          *   <li>limit - Requested number of resources to be provided in response (optional)</li>
      *   </ul>
          * @return List&lt;ExportJob&gt;
      */
      @RequestLine("GET /exportJob?fields={fields}&offset={offset}&limit={limit}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<List<ExportJob>> listExportJobWithHttpInfo(@QueryMap(encoded=true) ListExportJobQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>listExportJob</code> method in a fluent style.
   */
  public static class ListExportJobQueryParams extends HashMap<String, Object> {
    public ListExportJobQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
    public ListExportJobQueryParams offset(final Integer value) {
      put("offset", EncodingUtils.encode(value));
      return this;
    }
    public ListExportJobQueryParams limit(final Integer value) {
      put("limit", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Retrieves a ExportJob by ID
   * This operation retrieves a ExportJob entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ExportJob (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return ExportJob
   */
  @RequestLine("GET /exportJob/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ExportJob retrieveExportJob(@Param("id") String id, @Param("fields") String fields);

  /**
   * Retrieves a ExportJob by ID
   * Similar to <code>retrieveExportJob</code> but it also returns the http response headers .
   * This operation retrieves a ExportJob entity. Attribute selection is enabled for all first level attributes.
   * @param id Identifier of the ExportJob (required)
   * @param fields Comma-separated properties to provide in response (optional)
   * @return A ApiResponse that wraps the response boyd and the http headers.
   */
  @RequestLine("GET /exportJob/{id}?fields={fields}")
  @Headers({
    "Accept: application/json;charset=utf-8",
  })
  ApiResponse<ExportJob> retrieveExportJobWithHttpInfo(@Param("id") String id, @Param("fields") String fields);


  /**
   * Retrieves a ExportJob by ID
   * This operation retrieves a ExportJob entity. Attribute selection is enabled for all first level attributes.
   * Note, this is equivalent to the other <code>retrieveExportJob</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link RetrieveExportJobQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param id Identifier of the ExportJob (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>fields - Comma-separated properties to provide in response (optional)</li>
   *   </ul>
   * @return ExportJob
   */
  @RequestLine("GET /exportJob/{id}?fields={fields}")
  @Headers({
  "Accept: application/json;charset=utf-8",
  })
  ExportJob retrieveExportJob(@Param("id") String id, @QueryMap(encoded=true) RetrieveExportJobQueryParams queryParams);

  /**
  * Retrieves a ExportJob by ID
  * This operation retrieves a ExportJob entity. Attribute selection is enabled for all first level attributes.
  * Note, this is equivalent to the other <code>retrieveExportJob</code> that receives the query parameters as a map,
  * but this one also exposes the Http response headers
              * @param id Identifier of the ExportJob (required)
      * @param queryParams Map of query parameters as name-value pairs
      *   <p>The following elements may be specified in the query map:</p>
      *   <ul>
          *   <li>fields - Comma-separated properties to provide in response (optional)</li>
      *   </ul>
          * @return ExportJob
      */
      @RequestLine("GET /exportJob/{id}?fields={fields}")
      @Headers({
    "Accept: application/json;charset=utf-8",
      })
   ApiResponse<ExportJob> retrieveExportJobWithHttpInfo(@Param("id") String id, @QueryMap(encoded=true) RetrieveExportJobQueryParams queryParams);


   /**
   * A convenience class for generating query parameters for the
   * <code>retrieveExportJob</code> method in a fluent style.
   */
  public static class RetrieveExportJobQueryParams extends HashMap<String, Object> {
    public RetrieveExportJobQueryParams fields(final String value) {
      put("fields", EncodingUtils.encode(value));
      return this;
    }
  }
}
