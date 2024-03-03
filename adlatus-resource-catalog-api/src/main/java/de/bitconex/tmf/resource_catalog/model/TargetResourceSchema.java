package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * The reference object to the schema and type of target resource which is described by resource specification
 */

@Schema(name = "TargetResourceSchema", description = "The reference object to the schema and type of target resource which is described by resource specification")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class TargetResourceSchema {

  private URI atSchemaLocation;

  private String atType;

  /**
   * Default constructor
   * @deprecated Use {@link TargetResourceSchema#TargetResourceSchema(URI, String)}
   */
  @Deprecated
  public TargetResourceSchema() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TargetResourceSchema(URI atSchemaLocation, String atType) {
    this.atSchemaLocation = atSchemaLocation;
    this.atType = atType;
  }

  public TargetResourceSchema atSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
    return this;
  }

  /**
   * This field provides a link to the schema describing the target resource
   * @return atSchemaLocation
  */
  @NotNull @Valid 
  @Schema(name = "@schemaLocation", description = "This field provides a link to the schema describing the target resource", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("@schemaLocation")
  public URI getAtSchemaLocation() {
    return atSchemaLocation;
  }

  public void setAtSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
  }

  public TargetResourceSchema atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * Class type of the target resource
   * @return atType
  */
  @NotNull 
  @Schema(name = "@type", description = "Class type of the target resource", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("@type")
  public String getAtType() {
    return atType;
  }

  public void setAtType(String atType) {
    this.atType = atType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TargetResourceSchema targetResourceSchema = (TargetResourceSchema) o;
    return Objects.equals(this.atSchemaLocation, targetResourceSchema.atSchemaLocation) &&
        Objects.equals(this.atType, targetResourceSchema.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TargetResourceSchema {\n");
    sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
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

