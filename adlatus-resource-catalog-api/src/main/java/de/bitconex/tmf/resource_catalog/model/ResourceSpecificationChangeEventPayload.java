package de.bitconex.tmf.resource_catalog.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The event data structure
 */

@Schema(name = "ResourceSpecificationChangeEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceSpecificationChangeEventPayload {

  private ResourceSpecification resourceSpecification;

  public ResourceSpecificationChangeEventPayload resourceSpecification(ResourceSpecification resourceSpecification) {
    this.resourceSpecification = resourceSpecification;
    return this;
  }

  /**
   * Get resourceSpecification
   * @return resourceSpecification
  */
  @Valid 
  @Schema(name = "resourceSpecification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecification")
  public ResourceSpecification getResourceSpecification() {
    return resourceSpecification;
  }

  public void setResourceSpecification(ResourceSpecification resourceSpecification) {
    this.resourceSpecification = resourceSpecification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceSpecificationChangeEventPayload resourceSpecificationChangeEventPayload = (ResourceSpecificationChangeEventPayload) o;
    return Objects.equals(this.resourceSpecification, resourceSpecificationChangeEventPayload.resourceSpecification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceSpecification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceSpecificationChangeEventPayload {\n");
    sb.append("    resourceSpecification: ").append(toIndentedString(resourceSpecification)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

