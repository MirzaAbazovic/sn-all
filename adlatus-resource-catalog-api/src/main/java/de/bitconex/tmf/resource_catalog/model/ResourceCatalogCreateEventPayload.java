package de.bitconex.tmf.resource_catalog.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The event data structure
 */

@Schema(name = "ResourceCatalogCreateEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceCatalogCreateEventPayload {

  private ResourceCatalog resourceCatalog;

  public ResourceCatalogCreateEventPayload resourceCatalog(ResourceCatalog resourceCatalog) {
    this.resourceCatalog = resourceCatalog;
    return this;
  }

  /**
   * Get resourceCatalog
   * @return resourceCatalog
  */
  @Valid 
  @Schema(name = "resourceCatalog", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceCatalog")
  public ResourceCatalog getResourceCatalog() {
    return resourceCatalog;
  }

  public void setResourceCatalog(ResourceCatalog resourceCatalog) {
    this.resourceCatalog = resourceCatalog;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceCatalogCreateEventPayload resourceCatalogCreateEventPayload = (ResourceCatalogCreateEventPayload) o;
    return Objects.equals(this.resourceCatalog, resourceCatalogCreateEventPayload.resourceCatalog);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceCatalog);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceCatalogCreateEventPayload {\n");
    sb.append("    resourceCatalog: ").append(toIndentedString(resourceCatalog)).append("\n");
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

