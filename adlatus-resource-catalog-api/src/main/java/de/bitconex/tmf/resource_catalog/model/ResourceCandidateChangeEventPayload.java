package de.bitconex.tmf.resource_catalog.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The event data structure
 */

@Schema(name = "ResourceCandidateChangeEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceCandidateChangeEventPayload {

  private ResourceCandidate resourceCandidate;

  public ResourceCandidateChangeEventPayload resourceCandidate(ResourceCandidate resourceCandidate) {
    this.resourceCandidate = resourceCandidate;
    return this;
  }

  /**
   * Get resourceCandidate
   * @return resourceCandidate
  */
  @Valid 
  @Schema(name = "resourceCandidate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceCandidate")
  public ResourceCandidate getResourceCandidate() {
    return resourceCandidate;
  }

  public void setResourceCandidate(ResourceCandidate resourceCandidate) {
    this.resourceCandidate = resourceCandidate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceCandidateChangeEventPayload resourceCandidateChangeEventPayload = (ResourceCandidateChangeEventPayload) o;
    return Objects.equals(this.resourceCandidate, resourceCandidateChangeEventPayload.resourceCandidate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceCandidate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceCandidateChangeEventPayload {\n");
    sb.append("    resourceCandidate: ").append(toIndentedString(resourceCandidate)).append("\n");
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

