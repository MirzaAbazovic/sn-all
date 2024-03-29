/*
 * Resource Catalog Management
 * ### February 2023 Resource Catalog API is one of Catalog Management API Family. Resource Catalog API goal is to provide a catalog of resources.  ### Operations Resource Catalog API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity - Manage notification of events
 *
 * The version of the OpenAPI document: 5.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rcm.model;

import java.util.Objects;
import java.util.Arrays;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonTypeName;
    import com.fasterxml.jackson.annotation.JsonValue;
    import de.bitconex.tmf.rcm.model.EndpointSpecificationRefMVO;
    import java.util.ArrayList;
    import java.util.List;
    import com.fasterxml.jackson.annotation.*;

        /**
* A specification for an edge in a resource graph.
*/
    @JsonPropertyOrder({
        ConnectionSpecificationMVOAllOf.JSON_PROPERTY_NAME,
        ConnectionSpecificationMVOAllOf.JSON_PROPERTY_ASSOCIATION_TYPE,
        ConnectionSpecificationMVOAllOf.JSON_PROPERTY_ENDPOINT_SPECIFICATION
    })
            @JsonTypeName("ConnectionSpecification_MVO_allOf")
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ConnectionSpecificationMVOAllOf {
        public static final String JSON_PROPERTY_NAME = "name";
            private String name;

              /**
   * Association type.
   */
  public enum AssociationTypeEnum {
    POINTTOPOINT("pointtoPoint"),
    
    POINTTOMULTIPOINT("pointtoMultipoint");

    private String value;

    AssociationTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AssociationTypeEnum fromValue(String value) {
      for (AssociationTypeEnum b : AssociationTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

        public static final String JSON_PROPERTY_ASSOCIATION_TYPE = "associationType";
            private AssociationTypeEnum associationType;

        public static final String JSON_PROPERTY_ENDPOINT_SPECIFICATION = "endpointSpecification";
            private List<EndpointSpecificationRefMVO> endpointSpecification = new ArrayList<>();



}

