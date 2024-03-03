package de.bitconex.tmf.resource_catalog.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The event data structure
 */

@Schema(name = "ImportJobStateChangeEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ImportJobStateChangeEventPayload {

  private ImportJob importJob;

  public ImportJobStateChangeEventPayload importJob(ImportJob importJob) {
    this.importJob = importJob;
    return this;
  }

  /**
   * Get importJob
   * @return importJob
  */
  @Valid 
  @Schema(name = "importJob", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("importJob")
  public ImportJob getImportJob() {
    return importJob;
  }

  public void setImportJob(ImportJob importJob) {
    this.importJob = importJob;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImportJobStateChangeEventPayload importJobStateChangeEventPayload = (ImportJobStateChangeEventPayload) o;
    return Objects.equals(this.importJob, importJobStateChangeEventPayload.importJob);
  }

  @Override
  public int hashCode() {
    return Objects.hash(importJob);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImportJobStateChangeEventPayload {\n");
    sb.append("    importJob: ").append(toIndentedString(importJob)).append("\n");
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

