package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * An aggregation, migration, substitution, dependency or exclusivity relationship between/among FeatureSpecificationCharacteristics.
 */

@Schema(name = "FeatureSpecificationCharacteristicRelationship", description = "An aggregation, migration, substitution, dependency or exclusivity relationship between/among FeatureSpecificationCharacteristics.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class FeatureSpecificationCharacteristicRelationship {

  private String id;

  private URI href;

  private String characteristicId;

  private String featureId;

  private String name;

  private String relationshipType;

  private URI resourceSpecificationHref;

  private String resourceSpecificationId;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  public FeatureSpecificationCharacteristicRelationship id(String id) {
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

  public FeatureSpecificationCharacteristicRelationship href(URI href) {
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

  public FeatureSpecificationCharacteristicRelationship characteristicId(String characteristicId) {
    this.characteristicId = characteristicId;
    return this;
  }

  /**
   * Unique identifier of the characteristic within the the target feature specification
   * @return characteristicId
  */
  
  @Schema(name = "characteristicId", description = "Unique identifier of the characteristic within the the target feature specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("characteristicId")
  public String getCharacteristicId() {
    return characteristicId;
  }

  public void setCharacteristicId(String characteristicId) {
    this.characteristicId = characteristicId;
  }

  public FeatureSpecificationCharacteristicRelationship featureId(String featureId) {
    this.featureId = featureId;
    return this;
  }

  /**
   * Unique identifier of the target feature specification within the resource specification.
   * @return featureId
  */
  
  @Schema(name = "featureId", description = "Unique identifier of the target feature specification within the resource specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("featureId")
  public String getFeatureId() {
    return featureId;
  }

  public void setFeatureId(String featureId) {
    this.featureId = featureId;
  }

  public FeatureSpecificationCharacteristicRelationship name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the target characteristic
   * @return name
  */
  
  @Schema(name = "name", description = "Name of the target characteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FeatureSpecificationCharacteristicRelationship relationshipType(String relationshipType) {
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

  public FeatureSpecificationCharacteristicRelationship resourceSpecificationHref(URI resourceSpecificationHref) {
    this.resourceSpecificationHref = resourceSpecificationHref;
    return this;
  }

  /**
   * Hyperlink reference to the resource specification containing the target feature and feature characteristic
   * @return resourceSpecificationHref
  */
  @Valid 
  @Schema(name = "resourceSpecificationHref", description = "Hyperlink reference to the resource specification containing the target feature and feature characteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecificationHref")
  public URI getResourceSpecificationHref() {
    return resourceSpecificationHref;
  }

  public void setResourceSpecificationHref(URI resourceSpecificationHref) {
    this.resourceSpecificationHref = resourceSpecificationHref;
  }

  public FeatureSpecificationCharacteristicRelationship resourceSpecificationId(String resourceSpecificationId) {
    this.resourceSpecificationId = resourceSpecificationId;
    return this;
  }

  /**
   * Unique identifier of the resource specification containing the target feature and feature characteristic
   * @return resourceSpecificationId
  */
  
  @Schema(name = "resourceSpecificationId", description = "Unique identifier of the resource specification containing the target feature and feature characteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecificationId")
  public String getResourceSpecificationId() {
    return resourceSpecificationId;
  }

  public void setResourceSpecificationId(String resourceSpecificationId) {
    this.resourceSpecificationId = resourceSpecificationId;
  }

  public FeatureSpecificationCharacteristicRelationship validFor(TimePeriod validFor) {
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

  public FeatureSpecificationCharacteristicRelationship atBaseType(String atBaseType) {
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

  public FeatureSpecificationCharacteristicRelationship atSchemaLocation(URI atSchemaLocation) {
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

  public FeatureSpecificationCharacteristicRelationship atType(String atType) {
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
    FeatureSpecificationCharacteristicRelationship featureSpecificationCharacteristicRelationship = (FeatureSpecificationCharacteristicRelationship) o;
    return Objects.equals(this.id, featureSpecificationCharacteristicRelationship.id) &&
        Objects.equals(this.href, featureSpecificationCharacteristicRelationship.href) &&
        Objects.equals(this.characteristicId, featureSpecificationCharacteristicRelationship.characteristicId) &&
        Objects.equals(this.featureId, featureSpecificationCharacteristicRelationship.featureId) &&
        Objects.equals(this.name, featureSpecificationCharacteristicRelationship.name) &&
        Objects.equals(this.relationshipType, featureSpecificationCharacteristicRelationship.relationshipType) &&
        Objects.equals(this.resourceSpecificationHref, featureSpecificationCharacteristicRelationship.resourceSpecificationHref) &&
        Objects.equals(this.resourceSpecificationId, featureSpecificationCharacteristicRelationship.resourceSpecificationId) &&
        Objects.equals(this.validFor, featureSpecificationCharacteristicRelationship.validFor) &&
        Objects.equals(this.atBaseType, featureSpecificationCharacteristicRelationship.atBaseType) &&
        Objects.equals(this.atSchemaLocation, featureSpecificationCharacteristicRelationship.atSchemaLocation) &&
        Objects.equals(this.atType, featureSpecificationCharacteristicRelationship.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, characteristicId, featureId, name, relationshipType, resourceSpecificationHref, resourceSpecificationId, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureSpecificationCharacteristicRelationship {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    characteristicId: ").append(toIndentedString(characteristicId)).append("\n");
    sb.append("    featureId: ").append(toIndentedString(featureId)).append("\n");
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

