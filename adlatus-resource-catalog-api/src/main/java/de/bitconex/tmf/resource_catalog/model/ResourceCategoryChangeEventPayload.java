package de.bitconex.tmf.resource_catalog.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The event data structure
 */

@Schema(name = "ResourceCategoryChangeEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceCategoryChangeEventPayload {

  private ResourceCategory resourceCategory;

  public ResourceCategoryChangeEventPayload resourceCategory(ResourceCategory resourceCategory) {
    this.resourceCategory = resourceCategory;
    return this;
  }

  /**
   * Get resourceCategory
   * @return resourceCategory
  */
  @Valid 
  @Schema(name = "resourceCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceCategory")
  public ResourceCategory getResourceCategory() {
    return resourceCategory;
  }

  public void setResourceCategory(ResourceCategory resourceCategory) {
    this.resourceCategory = resourceCategory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceCategoryChangeEventPayload resourceCategoryChangeEventPayload = (ResourceCategoryChangeEventPayload) o;
    return Objects.equals(this.resourceCategory, resourceCategoryChangeEventPayload.resourceCategory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceCategory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceCategoryChangeEventPayload {\n");
    sb.append("    resourceCategory: ").append(toIndentedString(resourceCategory)).append("\n");
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

