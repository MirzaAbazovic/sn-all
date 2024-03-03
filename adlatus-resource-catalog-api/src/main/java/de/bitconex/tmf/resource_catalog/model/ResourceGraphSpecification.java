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
 * Resource graph specification.
 */

@Schema(name = "ResourceGraphSpecification", description = "Resource graph specification.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceGraphSpecification {

  private String id;

  private URI href;

  private String description;

  private String name;

  @Valid
  private List<@Valid ConnectionSpecification> connectionSpecification = new ArrayList<>();

  @Valid
  private List<@Valid ResourceGraphSpecificationRelationship> graphSpecificationRelationship;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  /**
   * Default constructor
   * @deprecated Use {@link ResourceGraphSpecification#ResourceGraphSpecification(List<@Valid ConnectionSpecification>)}
   */
  @Deprecated
  public ResourceGraphSpecification() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ResourceGraphSpecification(List<@Valid ConnectionSpecification> connectionSpecification) {
    this.connectionSpecification = connectionSpecification;
  }

  public ResourceGraphSpecification id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of the resource graph specification.
   * @return id
  */
  
  @Schema(name = "id", description = "Unique identifier of the resource graph specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResourceGraphSpecification href(URI href) {
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

  public ResourceGraphSpecification description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the resource graph specification.
   * @return description
  */
  
  @Schema(name = "description", description = "Description of the resource graph specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ResourceGraphSpecification name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Descriptive name for the resource graph specification.
   * @return name
  */
  
  @Schema(name = "name", description = "Descriptive name for the resource graph specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResourceGraphSpecification connectionSpecification(List<@Valid ConnectionSpecification> connectionSpecification) {
    this.connectionSpecification = connectionSpecification;
    return this;
  }

  public ResourceGraphSpecification addConnectionSpecificationItem(ConnectionSpecification connectionSpecificationItem) {
    if (this.connectionSpecification == null) {
      this.connectionSpecification = new ArrayList<>();
    }
    this.connectionSpecification.add(connectionSpecificationItem);
    return this;
  }

  /**
   * Resource graph edge specifications.
   * @return connectionSpecification
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "connectionSpecification", description = "Resource graph edge specifications.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("connectionSpecification")
  public List<@Valid ConnectionSpecification> getConnectionSpecification() {
    return connectionSpecification;
  }

  public void setConnectionSpecification(List<@Valid ConnectionSpecification> connectionSpecification) {
    this.connectionSpecification = connectionSpecification;
  }

  public ResourceGraphSpecification graphSpecificationRelationship(List<@Valid ResourceGraphSpecificationRelationship> graphSpecificationRelationship) {
    this.graphSpecificationRelationship = graphSpecificationRelationship;
    return this;
  }

  public ResourceGraphSpecification addGraphSpecificationRelationshipItem(ResourceGraphSpecificationRelationship graphSpecificationRelationshipItem) {
    if (this.graphSpecificationRelationship == null) {
      this.graphSpecificationRelationship = new ArrayList<>();
    }
    this.graphSpecificationRelationship.add(graphSpecificationRelationshipItem);
    return this;
  }

  /**
   * Relationships to other resource graph specifications.
   * @return graphSpecificationRelationship
  */
  @Valid 
  @Schema(name = "graphSpecificationRelationship", description = "Relationships to other resource graph specifications.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("graphSpecificationRelationship")
  public List<@Valid ResourceGraphSpecificationRelationship> getGraphSpecificationRelationship() {
    return graphSpecificationRelationship;
  }

  public void setGraphSpecificationRelationship(List<@Valid ResourceGraphSpecificationRelationship> graphSpecificationRelationship) {
    this.graphSpecificationRelationship = graphSpecificationRelationship;
  }

  public ResourceGraphSpecification atBaseType(String atBaseType) {
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

  public ResourceGraphSpecification atSchemaLocation(URI atSchemaLocation) {
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

  public ResourceGraphSpecification atType(String atType) {
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
    ResourceGraphSpecification resourceGraphSpecification = (ResourceGraphSpecification) o;
    return Objects.equals(this.id, resourceGraphSpecification.id) &&
        Objects.equals(this.href, resourceGraphSpecification.href) &&
        Objects.equals(this.description, resourceGraphSpecification.description) &&
        Objects.equals(this.name, resourceGraphSpecification.name) &&
        Objects.equals(this.connectionSpecification, resourceGraphSpecification.connectionSpecification) &&
        Objects.equals(this.graphSpecificationRelationship, resourceGraphSpecification.graphSpecificationRelationship) &&
        Objects.equals(this.atBaseType, resourceGraphSpecification.atBaseType) &&
        Objects.equals(this.atSchemaLocation, resourceGraphSpecification.atSchemaLocation) &&
        Objects.equals(this.atType, resourceGraphSpecification.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, description, name, connectionSpecification, graphSpecificationRelationship, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceGraphSpecification {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    connectionSpecification: ").append(toIndentedString(connectionSpecification)).append("\n");
    sb.append("    graphSpecificationRelationship: ").append(toIndentedString(graphSpecificationRelationship)).append("\n");
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

