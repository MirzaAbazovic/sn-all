package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * A specification for an edge in a resource graph.
 */

@Schema(name = "ConnectionSpecification", description = "A specification for an edge in a resource graph.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ConnectionSpecification {

  private String id;

  private URI href;

  private String associationType;

  private String name;

  @Valid
  private List<@Valid EndpointSpecificationRef> endpointSpecification = new ArrayList<>();

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  /**
   * Default constructor
   * @deprecated Use {@link ConnectionSpecification#ConnectionSpecification(String, List<@Valid EndpointSpecificationRef>)}
   */
  @Deprecated
  public ConnectionSpecification() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ConnectionSpecification(String associationType, List<@Valid EndpointSpecificationRef> endpointSpecification) {
    this.associationType = associationType;
    this.endpointSpecification = endpointSpecification;
  }

  public ConnectionSpecification id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for graph edge specification.
   * @return id
  */
  
  @Schema(name = "id", description = "Unique identifier for graph edge specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ConnectionSpecification href(URI href) {
    this.href = href;
    return this;
  }

  /**
   * Hyperlink reference
   * @return href
  */
  @Valid 
  @Schema(name = "href", description = "Hyperlink reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("href")
  public URI getHref() {
    return href;
  }

  public void setHref(URI href) {
    this.href = href;
  }

  public ConnectionSpecification associationType(String associationType) {
    this.associationType = associationType;
    return this;
  }

  /**
   * Association type.
   * @return associationType
  */
  @NotNull 
  @Schema(name = "associationType", description = "Association type.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("associationType")
  public String getAssociationType() {
    return associationType;
  }

  public void setAssociationType(String associationType) {
    this.associationType = associationType;
  }

  public ConnectionSpecification name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Descriptive name for graph edge specification.
   * @return name
  */
  
  @Schema(name = "name", description = "Descriptive name for graph edge specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ConnectionSpecification endpointSpecification(List<@Valid EndpointSpecificationRef> endpointSpecification) {
    this.endpointSpecification = endpointSpecification;
    return this;
  }

  public ConnectionSpecification addEndpointSpecificationItem(EndpointSpecificationRef endpointSpecificationItem) {
    if (this.endpointSpecification == null) {
      this.endpointSpecification = new ArrayList<>();
    }
    this.endpointSpecification.add(endpointSpecificationItem);
    return this;
  }

  /**
   * Specifications for resource graph vertices connected by this edge.
   * @return endpointSpecification
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "endpointSpecification", description = "Specifications for resource graph vertices connected by this edge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endpointSpecification")
  public List<@Valid EndpointSpecificationRef> getEndpointSpecification() {
    return endpointSpecification;
  }

  public void setEndpointSpecification(List<@Valid EndpointSpecificationRef> endpointSpecification) {
    this.endpointSpecification = endpointSpecification;
  }

  public ConnectionSpecification atBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
    return this;
  }

  /**
   * When sub-classing, this defines the super-class
   * @return atBaseType
  */
  
  @Schema(name = "@baseType", description = "When sub-classing, this defines the super-class", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@baseType")
  public String getAtBaseType() {
    return atBaseType;
  }

  public void setAtBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
  }

  public ConnectionSpecification atSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
    return this;
  }

  /**
   * A URI to a JSON-Schema file that defines additional attributes and relationships
   * @return atSchemaLocation
  */
  @Valid 
  @Schema(name = "@schemaLocation", description = "A URI to a JSON-Schema file that defines additional attributes and relationships", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@schemaLocation")
  public URI getAtSchemaLocation() {
    return atSchemaLocation;
  }

  public void setAtSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
  }

  public ConnectionSpecification atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * When sub-classing, this defines the sub-class Extensible name
   * @return atType
  */
  
  @Schema(name = "@type", description = "When sub-classing, this defines the sub-class Extensible name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    ConnectionSpecification connectionSpecification = (ConnectionSpecification) o;
    return Objects.equals(this.id, connectionSpecification.id) &&
        Objects.equals(this.href, connectionSpecification.href) &&
        Objects.equals(this.associationType, connectionSpecification.associationType) &&
        Objects.equals(this.name, connectionSpecification.name) &&
        Objects.equals(this.endpointSpecification, connectionSpecification.endpointSpecification) &&
        Objects.equals(this.atBaseType, connectionSpecification.atBaseType) &&
        Objects.equals(this.atSchemaLocation, connectionSpecification.atSchemaLocation) &&
        Objects.equals(this.atType, connectionSpecification.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, associationType, name, endpointSpecification, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectionSpecification {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    associationType: ").append(toIndentedString(associationType)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    endpointSpecification: ").append(toIndentedString(endpointSpecification)).append("\n");
    sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
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

