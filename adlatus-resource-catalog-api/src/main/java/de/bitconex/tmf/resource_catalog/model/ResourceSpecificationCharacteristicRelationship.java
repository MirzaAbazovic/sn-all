package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * An aggregation, migration, substitution, dependency or exclusivity relationship between/among ResourceSpecificationCharacteristics. The specification characteristic is embedded within the specification whose ID and href are in this entity, and identified by its ID.
 */

@Schema(name = "ResourceSpecificationCharacteristicRelationship", description = "An aggregation, migration, substitution, dependency or exclusivity relationship between/among ResourceSpecificationCharacteristics. The specification characteristic is embedded within the specification whose ID and href are in this entity, and identified by its ID.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceSpecificationCharacteristicRelationship {

  private String characteristicSpecificationId;

  private String name;

  private String relationshipType;

  private URI resourceSpecificationHref;

  private String resourceSpecificationId;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  public ResourceSpecificationCharacteristicRelationship characteristicSpecificationId(String characteristicSpecificationId) {
    this.characteristicSpecificationId = characteristicSpecificationId;
    return this;
  }

  /**
   * Unique identifier of the characteristic within the specification
   * @return characteristicSpecificationId
  */
  
  @Schema(name = "characteristicSpecificationId", description = "Unique identifier of the characteristic within the specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("characteristicSpecificationId")
  public String getCharacteristicSpecificationId() {
    return characteristicSpecificationId;
  }

  public void setCharacteristicSpecificationId(String characteristicSpecificationId) {
    this.characteristicSpecificationId = characteristicSpecificationId;
  }

  public ResourceSpecificationCharacteristicRelationship name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the target characteristic within the specification
   * @return name
  */
  
  @Schema(name = "name", description = "Name of the target characteristic within the specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResourceSpecificationCharacteristicRelationship relationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
    return this;
  }

  /**
   * Type of relationship such as aggregation, migration, substitution, dependency, exclusivity
   * @return relationshipType
  */
  
  @Schema(name = "relationshipType", description = "Type of relationship such as aggregation, migration, substitution, dependency, exclusivity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("relationshipType")
  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }

  public ResourceSpecificationCharacteristicRelationship resourceSpecificationHref(URI resourceSpecificationHref) {
    this.resourceSpecificationHref = resourceSpecificationHref;
    return this;
  }

  /**
   * Hyperlink reference to the resource specification containing the target characteristic
   * @return resourceSpecificationHref
  */
  @Valid 
  @Schema(name = "resourceSpecificationHref", description = "Hyperlink reference to the resource specification containing the target characteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecificationHref")
  public URI getResourceSpecificationHref() {
    return resourceSpecificationHref;
  }

  public void setResourceSpecificationHref(URI resourceSpecificationHref) {
    this.resourceSpecificationHref = resourceSpecificationHref;
  }

  public ResourceSpecificationCharacteristicRelationship resourceSpecificationId(String resourceSpecificationId) {
    this.resourceSpecificationId = resourceSpecificationId;
    return this;
  }

  /**
   * Unique identifier of the resource specification containing the target characteristic
   * @return resourceSpecificationId
  */
  
  @Schema(name = "resourceSpecificationId", description = "Unique identifier of the resource specification containing the target characteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecificationId")
  public String getResourceSpecificationId() {
    return resourceSpecificationId;
  }

  public void setResourceSpecificationId(String resourceSpecificationId) {
    this.resourceSpecificationId = resourceSpecificationId;
  }

  public ResourceSpecificationCharacteristicRelationship validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * Get validFor
   * @return validFor
  */
  @Valid 
  @Schema(name = "validFor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("validFor")
  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public ResourceSpecificationCharacteristicRelationship atBaseType(String atBaseType) {
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

  public ResourceSpecificationCharacteristicRelationship atSchemaLocation(URI atSchemaLocation) {
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

  public ResourceSpecificationCharacteristicRelationship atType(String atType) {
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
    ResourceSpecificationCharacteristicRelationship resourceSpecificationCharacteristicRelationship = (ResourceSpecificationCharacteristicRelationship) o;
    return Objects.equals(this.characteristicSpecificationId, resourceSpecificationCharacteristicRelationship.characteristicSpecificationId) &&
        Objects.equals(this.name, resourceSpecificationCharacteristicRelationship.name) &&
        Objects.equals(this.relationshipType, resourceSpecificationCharacteristicRelationship.relationshipType) &&
        Objects.equals(this.resourceSpecificationHref, resourceSpecificationCharacteristicRelationship.resourceSpecificationHref) &&
        Objects.equals(this.resourceSpecificationId, resourceSpecificationCharacteristicRelationship.resourceSpecificationId) &&
        Objects.equals(this.validFor, resourceSpecificationCharacteristicRelationship.validFor) &&
        Objects.equals(this.atBaseType, resourceSpecificationCharacteristicRelationship.atBaseType) &&
        Objects.equals(this.atSchemaLocation, resourceSpecificationCharacteristicRelationship.atSchemaLocation) &&
        Objects.equals(this.atType, resourceSpecificationCharacteristicRelationship.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(characteristicSpecificationId, name, relationshipType, resourceSpecificationHref, resourceSpecificationId, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceSpecificationCharacteristicRelationship {\n");
    sb.append("    characteristicSpecificationId: ").append(toIndentedString(characteristicSpecificationId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    relationshipType: ").append(toIndentedString(relationshipType)).append("\n");
    sb.append("    resourceSpecificationHref: ").append(toIndentedString(resourceSpecificationHref)).append("\n");
    sb.append("    resourceSpecificationId: ").append(toIndentedString(resourceSpecificationId)).append("\n");
    sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
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

