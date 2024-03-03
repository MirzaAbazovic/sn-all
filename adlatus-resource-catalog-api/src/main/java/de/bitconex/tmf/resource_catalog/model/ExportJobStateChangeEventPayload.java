package de.bitconex.tmf.resource_catalog.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The event data structure
 */

@Schema(name = "ExportJobStateChangeEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ExportJobStateChangeEventPayload {

  private ExportJob exportJob;

  public ExportJobStateChangeEventPayload exportJob(ExportJob exportJob) {
    this.exportJob = exportJob;
    return this;
  }

  /**
   * Get exportJob
   * @return exportJob
  */
  @Valid 
  @Schema(name = "exportJob", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exportJob")
  public ExportJob getExportJob() {
    return exportJob;
  }

  public void setExportJob(ExportJob exportJob) {
    this.exportJob = exportJob;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExportJobStateChangeEventPayload exportJobStateChangeEventPayload = (ExportJobStateChangeEventPayload) o;
    return Objects.equals(this.exportJob, exportJobStateChangeEventPayload.exportJob);
  }

  @Override
  public int hashCode() {
    return Objects.hash(exportJob);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExportJobStateChangeEventPayload {\n");
    sb.append("    exportJob: ").append(toIndentedString(exportJob)).append("\n");
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

