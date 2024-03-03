package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * A migration, substitution, dependency or exclusivity relationship between/among resource specifications.
 */

@Schema(name = "ResourceSpecificationRelationship", description = "A migration, substitution, dependency or exclusivity relationship between/among resource specifications.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceSpecificationRelationship {

  private String id;

  private URI href;

  private Integer defaultQuantity;

  private Integer maximumQuantity;

  private Integer minimumQuantity;

  private String name;

  private String relationshipType;

  private String role;

  @Valid
  private List<@Valid ResourceSpecificationCharacteristic> characteristic;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  public ResourceSpecificationRelationship id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of target ResourceSpecification
   * @return id
  */
  
  @Schema(name = "id", description = "Unique identifier of target ResourceSpecification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResourceSpecificationRelationship href(URI href) {
    this.href = href;
    return this;
  }

  /**
   * Reference of the target ResourceSpecification
   * @return href
  */
  @Valid 
  @Schema(name = "href", description = "Reference of the target ResourceSpecification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("href")
  public URI getHref() {
    return href;
  }

  public void setHref(URI href) {
    this.href = href;
  }

  public ResourceSpecificationRelationship defaultQuantity(Integer defaultQuantity) {
    this.defaultQuantity = defaultQuantity;
    return this;
  }

  /**
   * The default number of the related resource that should be instantiated, for example a rack would typically have 4 cards, although it could support more.
   * @return defaultQuantity
  */
  
  @Schema(name = "defaultQuantity", description = "The default number of the related resource that should be instantiated, for example a rack would typically have 4 cards, although it could support more.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("defaultQuantity")
  public Integer getDefaultQuantity() {
    return defaultQuantity;
  }

  public void setDefaultQuantity(Integer defaultQuantity) {
    this.defaultQuantity = defaultQuantity;
  }

  public ResourceSpecificationRelationship maximumQuantity(Integer maximumQuantity) {
    this.maximumQuantity = maximumQuantity;
    return this;
  }

  /**
   * The maximum number of the related resource that should be instantiated, for example a rack supports a maximum of 16 cards
   * @return maximumQuantity
  */
  
  @Schema(name = "maximumQuantity", description = "The maximum number of the related resource that should be instantiated, for example a rack supports a maximum of 16 cards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maximumQuantity")
  public Integer getMaximumQuantity() {
    return maximumQuantity;
  }

  public void setMaximumQuantity(Integer maximumQuantity) {
    this.maximumQuantity = maximumQuantity;
  }

  public ResourceSpecificationRelationship minimumQuantity(Integer minimumQuantity) {
    this.minimumQuantity = minimumQuantity;
    return this;
  }

  /**
   * The minimum number of the related resource that should be instantiated, for example a rack must have at least 1 card
   * @return minimumQuantity
  */
  
  @Schema(name = "minimumQuantity", description = "The minimum number of the related resource that should be instantiated, for example a rack must have at least 1 card", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minimumQuantity")
  public Integer getMinimumQuantity() {
    return minimumQuantity;
  }

  public void setMinimumQuantity(Integer minimumQuantity) {
    this.minimumQuantity = minimumQuantity;
  }

  public ResourceSpecificationRelationship name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name given to the target resource specification instance
   * @return name
  */
  
  @Schema(name = "name", description = "The name given to the target resource specification instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResourceSpecificationRelationship relationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
    return this;
  }

  /**
   * Type of relationship such as migration, substitution, dependency, exclusivity
   * @return relationshipType
  */
  
  @Schema(name = "relationshipType", description = "Type of relationship such as migration, substitution, dependency, exclusivity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("relationshipType")
  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }

  public ResourceSpecificationRelationship role(String role) {
    this.role = role;
    return this;
  }

  /**
   * The association role for this resource specification
   * @return role
  */
  
  @Schema(name = "role", description = "The association role for this resource specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("role")
  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public ResourceSpecificationRelationship characteristic(List<@Valid ResourceSpecificationCharacteristic> characteristic) {
    this.characteristic = characteristic;
    return this;
  }

  public ResourceSpecificationRelationship addCharacteristicItem(ResourceSpecificationCharacteristic characteristicItem) {
    if (this.characteristic == null) {
      this.characteristic = new ArrayList<>();
    }
    this.characteristic.add(characteristicItem);
    return this;
  }

  /**
   * A characteristic that refines the relationship. For example, consider the relationship between a slot and a card. For a half-height card it is important to know the position at which the card is inserted, so a characteristic Position might be defined on the relationship to allow capturing of this in the inventory
   * @return characteristic
  */
  @Valid 
  @Schema(name = "characteristic", description = "A characteristic that refines the relationship. For example, consider the relationship between a slot and a card. For a half-height card it is important to know the position at which the card is inserted, so a characteristic Position might be defined on the relationship to allow capturing of this in the inventory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("characteristic")
  public List<@Valid ResourceSpecificationCharacteristic> getCharacteristic() {
    return characteristic;
  }

  public void setCharacteristic(List<@Valid ResourceSpecificationCharacteristic> characteristic) {
    this.characteristic = characteristic;
  }

  public ResourceSpecificationRelationship validFor(TimePeriod validFor) {
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

  public ResourceSpecificationRelationship atBaseType(String atBaseType) {
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

  public ResourceSpecificationRelationship atSchemaLocation(URI atSchemaLocation) {
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

  public ResourceSpecificationRelationship atType(String atType) {
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
    ResourceSpecificationRelationship resourceSpecificationRelationship = (ResourceSpecificationRelationship) o;
    return Objects.equals(this.id, resourceSpecificationRelationship.id) &&
        Objects.equals(this.href, resourceSpecificationRelationship.href) &&
        Objects.equals(this.defaultQuantity, resourceSpecificationRelationship.defaultQuantity) &&
        Objects.equals(this.maximumQuantity, resourceSpecificationRelationship.maximumQuantity) &&
        Objects.equals(this.minimumQuantity, resourceSpecificationRelationship.minimumQuantity) &&
        Objects.equals(this.name, resourceSpecificationRelationship.name) &&
        Objects.equals(this.relationshipType, resourceSpecificationRelationship.relationshipType) &&
        Objects.equals(this.role, resourceSpecificationRelationship.role) &&
        Objects.equals(this.characteristic, resourceSpecificationRelationship.characteristic) &&
        Objects.equals(this.validFor, resourceSpecificationRelationship.validFor) &&
        Objects.equals(this.atBaseType, resourceSpecificationRelationship.atBaseType) &&
        Objects.equals(this.atSchemaLocation, resourceSpecificationRelationship.atSchemaLocation) &&
        Objects.equals(this.atType, resourceSpecificationRelationship.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, defaultQuantity, maximumQuantity, minimumQuantity, name, relationshipType, role, characteristic, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceSpecificationRelationship {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    defaultQuantity: ").append(toIndentedString(defaultQuantity)).append("\n");
    sb.append("    maximumQuantity: ").append(toIndentedString(maximumQuantity)).append("\n");
    sb.append("    minimumQuantity: ").append(toIndentedString(minimumQuantity)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    relationshipType: ").append(toIndentedString(relationshipType)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    characteristic: ").append(toIndentedString(characteristic)).append("\n");
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

