package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Describes link between resource graph specifications.
 */

@Schema(name = "ResourceGraphSpecificationRelationship", description = "Describes link between resource graph specifications.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceGraphSpecificationRelationship {

  private String id;

  private URI href;

  private String relationshipType;

  private ResourceGraphSpecificationRef resourceGraph;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  public ResourceGraphSpecificationRelationship id(String id) {
    this.id = id;
    return this;
  }

  /**
   * unique identifier
   * @return id
  */
  
  @Schema(name = "id", description = "unique identifier", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResourceGraphSpecificationRelationship href(URI href) {
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

  public ResourceGraphSpecificationRelationship relationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
    return this;
  }

  /**
   * Semantic of the relationship.
   * @return relationshipType
  */
  
  @Schema(name = "relationshipType", description = "Semantic of the relationship.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("relationshipType")
  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }

  public ResourceGraphSpecificationRelationship resourceGraph(ResourceGraphSpecificationRef resourceGraph) {
    this.resourceGraph = resourceGraph;
    return this;
  }

  /**
   * Get resourceGraph
   * @return resourceGraph
  */
  @Valid 
  @Schema(name = "resourceGraph", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceGraph")
  public ResourceGraphSpecificationRef getResourceGraph() {
    return resourceGraph;
  }

  public void setResourceGraph(ResourceGraphSpecificationRef resourceGraph) {
    this.resourceGraph = resourceGraph;
  }

  public ResourceGraphSpecificationRelationship atBaseType(String atBaseType) {
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

  public ResourceGraphSpecificationRelationship atSchemaLocation(URI atSchemaLocation) {
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

  public ResourceGraphSpecificationRelationship atType(String atType) {
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
    ResourceGraphSpecificationRelationship resourceGraphSpecificationRelationship = (ResourceGraphSpecificationRelationship) o;
    return Objects.equals(this.id, resourceGraphSpecificationRelationship.id) &&
        Objects.equals(this.href, resourceGraphSpecificationRelationship.href) &&
        Objects.equals(this.relationshipType, resourceGraphSpecificationRelationship.relationshipType) &&
        Objects.equals(this.resourceGraph, resourceGraphSpecificationRelationship.resourceGraph) &&
        Objects.equals(this.atBaseType, resourceGraphSpecificationRelationship.atBaseType) &&
        Objects.equals(this.atSchemaLocation, resourceGraphSpecificationRelationship.atSchemaLocation) &&
        Objects.equals(this.atType, resourceGraphSpecificationRelationship.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, relationshipType, resourceGraph, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceGraphSpecificationRelationship {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    relationshipType: ").append(toIndentedString(relationshipType)).append("\n");
    sb.append("    resourceGraph: ").append(toIndentedString(resourceGraph)).append("\n");
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

