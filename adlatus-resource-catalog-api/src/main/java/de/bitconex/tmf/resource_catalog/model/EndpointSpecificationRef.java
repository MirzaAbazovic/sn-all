package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * A specification for a vertex in a resource graph.
 */

@Schema(name = "EndpointSpecificationRef", description = "A specification for a vertex in a resource graph.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class EndpointSpecificationRef {

  private String id;

  private URI href;

  private Boolean isRoot = true;

  private String name;

  private String role;

  private ConnectionPointSpecificationRef connectionPointSpecification;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  private String atReferredType;

  /**
   * Default constructor
   * @deprecated Use {@link EndpointSpecificationRef#EndpointSpecificationRef(String)}
   */
  @Deprecated
  public EndpointSpecificationRef() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EndpointSpecificationRef(String id) {
    this.id = id;
  }

  public EndpointSpecificationRef id(String id) {
    this.id = id;
    return this;
  }

  /**
   * unique identifier
   * @return id
  */
  @NotNull 
  @Schema(name = "id", description = "unique identifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public EndpointSpecificationRef href(URI href) {
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

  public EndpointSpecificationRef isRoot(Boolean isRoot) {
    this.isRoot = isRoot;
    return this;
  }

  /**
   * Directionality: true when endpoint is a source, false when a sink. If true for all endpoints connectivity is bidirectional. Default is true.
   * @return isRoot
  */
  
  @Schema(name = "isRoot", description = "Directionality: true when endpoint is a source, false when a sink. If true for all endpoints connectivity is bidirectional. Default is true.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isRoot")
  public Boolean getIsRoot() {
    return isRoot;
  }

  public void setIsRoot(Boolean isRoot) {
    this.isRoot = isRoot;
  }

  public EndpointSpecificationRef name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the related entity.
   * @return name
  */
  
  @Schema(name = "name", description = "Name of the related entity.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EndpointSpecificationRef role(String role) {
    this.role = role;
    return this;
  }

  /**
   * Role of the Resource Function.
   * @return role
  */
  
  @Schema(name = "role", description = "Role of the Resource Function.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("role")
  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public EndpointSpecificationRef connectionPointSpecification(ConnectionPointSpecificationRef connectionPointSpecification) {
    this.connectionPointSpecification = connectionPointSpecification;
    return this;
  }

  /**
   * Get connectionPointSpecification
   * @return connectionPointSpecification
  */
  @Valid 
  @Schema(name = "connectionPointSpecification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("connectionPointSpecification")
  public ConnectionPointSpecificationRef getConnectionPointSpecification() {
    return connectionPointSpecification;
  }

  public void setConnectionPointSpecification(ConnectionPointSpecificationRef connectionPointSpecification) {
    this.connectionPointSpecification = connectionPointSpecification;
  }

  public EndpointSpecificationRef atBaseType(String atBaseType) {
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

  public EndpointSpecificationRef atSchemaLocation(URI atSchemaLocation) {
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

  public EndpointSpecificationRef atType(String atType) {
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

  public EndpointSpecificationRef atReferredType(String atReferredType) {
    this.atReferredType = atReferredType;
    return this;
  }

  /**
   * The actual type of the target instance when needed for disambiguation.
   * @return atReferredType
  */
  
  @Schema(name = "@referredType", description = "The actual type of the target instance when needed for disambiguation.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@referredType")
  public String getAtReferredType() {
    return atReferredType;
  }

  public void setAtReferredType(String atReferredType) {
    this.atReferredType = atReferredType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EndpointSpecificationRef endpointSpecificationRef = (EndpointSpecificationRef) o;
    return Objects.equals(this.id, endpointSpecificationRef.id) &&
        Objects.equals(this.href, endpointSpecificationRef.href) &&
        Objects.equals(this.isRoot, endpointSpecificationRef.isRoot) &&
        Objects.equals(this.name, endpointSpecificationRef.name) &&
        Objects.equals(this.role, endpointSpecificationRef.role) &&
        Objects.equals(this.connectionPointSpecification, endpointSpecificationRef.connectionPointSpecification) &&
        Objects.equals(this.atBaseType, endpointSpecificationRef.atBaseType) &&
        Objects.equals(this.atSchemaLocation, endpointSpecificationRef.atSchemaLocation) &&
        Objects.equals(this.atType, endpointSpecificationRef.atType) &&
        Objects.equals(this.atReferredType, endpointSpecificationRef.atReferredType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, isRoot, name, role, connectionPointSpecification, atBaseType, atSchemaLocation, atType, atReferredType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EndpointSpecificationRef {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    isRoot: ").append(toIndentedString(isRoot)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    connectionPointSpecification: ").append(toIndentedString(connectionPointSpecification)).append("\n");
    sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
    sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
    sb.append("    atReferredType: ").append(toIndentedString(atReferredType)).append("\n");
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

