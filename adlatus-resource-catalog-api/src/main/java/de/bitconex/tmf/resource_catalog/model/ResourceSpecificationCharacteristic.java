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
 * This class defines the characteristic features of a resource specification. Every ResourceSpecification has a variety of important attributes, methods, constraints, and relationships, which distinguish a resource specification from other resource specifications.
 */

@Schema(name = "ResourceSpecificationCharacteristic", description = "This class defines the characteristic features of a resource specification. Every ResourceSpecification has a variety of important attributes, methods, constraints, and relationships, which distinguish a resource specification from other resource specifications.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class ResourceSpecificationCharacteristic {

  private String id;

  private Boolean configurable;

  private String description;

  private Boolean extensible;

  private Boolean isUnique;

  private Integer maxCardinality;

  private Integer minCardinality;

  private String name;

  private String regex;

  private String valueType;

  @Valid
  private List<@Valid ResourceSpecificationCharacteristicRelationship> resourceSpecCharRelationship;

  @Valid
  private List<@Valid CharacteristicValueSpecification> resourceSpecCharacteristicValue;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  private String atValueSchemaLocation;

  public ResourceSpecificationCharacteristic id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique ID for the characteristic
   * @return id
  */
  
  @Schema(name = "id", description = "Unique ID for the characteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResourceSpecificationCharacteristic configurable(Boolean configurable) {
    this.configurable = configurable;
    return this;
  }

  /**
   * If true, the Boolean indicates that the target Characteristic is configurable
   * @return configurable
  */
  
  @Schema(name = "configurable", description = "If true, the Boolean indicates that the target Characteristic is configurable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("configurable")
  public Boolean getConfigurable() {
    return configurable;
  }

  public void setConfigurable(Boolean configurable) {
    this.configurable = configurable;
  }

  public ResourceSpecificationCharacteristic description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A narrative that explains the CharacteristicSpecification.
   * @return description
  */
  
  @Schema(name = "description", description = "A narrative that explains the CharacteristicSpecification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ResourceSpecificationCharacteristic extensible(Boolean extensible) {
    this.extensible = extensible;
    return this;
  }

  /**
   * An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.
   * @return extensible
  */
  
  @Schema(name = "extensible", description = "An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("extensible")
  public Boolean getExtensible() {
    return extensible;
  }

  public void setExtensible(Boolean extensible) {
    this.extensible = extensible;
  }

  public ResourceSpecificationCharacteristic isUnique(Boolean isUnique) {
    this.isUnique = isUnique;
    return this;
  }

  /**
   * An indicator that specifies if a value is unique for the specification. Possible values are; \"unique while value is in effect\" and \"unique whether value is in effect or not\"
   * @return isUnique
  */
  
  @Schema(name = "isUnique", description = "An indicator that specifies if a value is unique for the specification. Possible values are; \"unique while value is in effect\" and \"unique whether value is in effect or not\"", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isUnique")
  public Boolean getIsUnique() {
    return isUnique;
  }

  public void setIsUnique(Boolean isUnique) {
    this.isUnique = isUnique;
  }

  public ResourceSpecificationCharacteristic maxCardinality(Integer maxCardinality) {
    this.maxCardinality = maxCardinality;
    return this;
  }

  /**
   * The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.
   * @return maxCardinality
  */
  
  @Schema(name = "maxCardinality", description = "The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxCardinality")
  public Integer getMaxCardinality() {
    return maxCardinality;
  }

  public void setMaxCardinality(Integer maxCardinality) {
    this.maxCardinality = maxCardinality;
  }

  public ResourceSpecificationCharacteristic minCardinality(Integer minCardinality) {
    this.minCardinality = minCardinality;
    return this;
  }

  /**
   * The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.
   * @return minCardinality
  */
  
  @Schema(name = "minCardinality", description = "The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minCardinality")
  public Integer getMinCardinality() {
    return minCardinality;
  }

  public void setMinCardinality(Integer minCardinality) {
    this.minCardinality = minCardinality;
  }

  public ResourceSpecificationCharacteristic name(String name) {
    this.name = name;
    return this;
  }

  /**
   * A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.
   * @return name
  */
  
  @Schema(name = "name", description = "A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResourceSpecificationCharacteristic regex(String regex) {
    this.regex = regex;
    return this;
  }

  /**
   * A rule or principle represented in regular expression used to derive the value of a characteristic value.
   * @return regex
  */
  
  @Schema(name = "regex", description = "A rule or principle represented in regular expression used to derive the value of a characteristic value.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("regex")
  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public ResourceSpecificationCharacteristic valueType(String valueType) {
    this.valueType = valueType;
    return this;
  }

  /**
   * A kind of value that the characteristic can take on, such as numeric, text and so forth
   * @return valueType
  */
  
  @Schema(name = "valueType", description = "A kind of value that the characteristic can take on, such as numeric, text and so forth", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valueType")
  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public ResourceSpecificationCharacteristic resourceSpecCharRelationship(List<@Valid ResourceSpecificationCharacteristicRelationship> resourceSpecCharRelationship) {
    this.resourceSpecCharRelationship = resourceSpecCharRelationship;
    return this;
  }

  public ResourceSpecificationCharacteristic addResourceSpecCharRelationshipItem(ResourceSpecificationCharacteristicRelationship resourceSpecCharRelationshipItem) {
    if (this.resourceSpecCharRelationship == null) {
      this.resourceSpecCharRelationship = new ArrayList<>();
    }
    this.resourceSpecCharRelationship.add(resourceSpecCharRelationshipItem);
    return this;
  }

  /**
   * An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.
   * @return resourceSpecCharRelationship
  */
  @Valid 
  @Schema(name = "resourceSpecCharRelationship", description = "An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecCharRelationship")
  public List<@Valid ResourceSpecificationCharacteristicRelationship> getResourceSpecCharRelationship() {
    return resourceSpecCharRelationship;
  }

  public void setResourceSpecCharRelationship(List<@Valid ResourceSpecificationCharacteristicRelationship> resourceSpecCharRelationship) {
    this.resourceSpecCharRelationship = resourceSpecCharRelationship;
  }

  public ResourceSpecificationCharacteristic resourceSpecCharacteristicValue(List<@Valid CharacteristicValueSpecification> resourceSpecCharacteristicValue) {
    this.resourceSpecCharacteristicValue = resourceSpecCharacteristicValue;
    return this;
  }

  public ResourceSpecificationCharacteristic addResourceSpecCharacteristicValueItem(CharacteristicValueSpecification resourceSpecCharacteristicValueItem) {
    if (this.resourceSpecCharacteristicValue == null) {
      this.resourceSpecCharacteristicValue = new ArrayList<>();
    }
    this.resourceSpecCharacteristicValue.add(resourceSpecCharacteristicValueItem);
    return this;
  }

  /**
   * A CharacteristicValueSpecification object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a ResourceSpecificationCharacteristic object. The values of the attributes in the CharacteristicValueSpecification object describe the values of the attributes that a corresponding ResourceSpecificationCharacteristic object can take on.
   * @return resourceSpecCharacteristicValue
  */
  @Valid 
  @Schema(name = "resourceSpecCharacteristicValue", description = "A CharacteristicValueSpecification object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a ResourceSpecificationCharacteristic object. The values of the attributes in the CharacteristicValueSpecification object describe the values of the attributes that a corresponding ResourceSpecificationCharacteristic object can take on.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceSpecCharacteristicValue")
  public List<@Valid CharacteristicValueSpecification> getResourceSpecCharacteristicValue() {
    return resourceSpecCharacteristicValue;
  }

  public void setResourceSpecCharacteristicValue(List<@Valid CharacteristicValueSpecification> resourceSpecCharacteristicValue) {
    this.resourceSpecCharacteristicValue = resourceSpecCharacteristicValue;
  }

  public ResourceSpecificationCharacteristic validFor(TimePeriod validFor) {
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

  public ResourceSpecificationCharacteristic atBaseType(String atBaseType) {
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

  public ResourceSpecificationCharacteristic atSchemaLocation(URI atSchemaLocation) {
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

  public ResourceSpecificationCharacteristic atType(String atType) {
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

  public ResourceSpecificationCharacteristic atValueSchemaLocation(String atValueSchemaLocation) {
    this.atValueSchemaLocation = atValueSchemaLocation;
    return this;
  }

  /**
   * This (optional) field provides a link to the schema describing the value type.
   * @return atValueSchemaLocation
  */
  
  @Schema(name = "@valueSchemaLocation", description = "This (optional) field provides a link to the schema describing the value type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@valueSchemaLocation")
  public String getAtValueSchemaLocation() {
    return atValueSchemaLocation;
  }

  public void setAtValueSchemaLocation(String atValueSchemaLocation) {
    this.atValueSchemaLocation = atValueSchemaLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceSpecificationCharacteristic resourceSpecificationCharacteristic = (ResourceSpecificationCharacteristic) o;
    return Objects.equals(this.id, resourceSpecificationCharacteristic.id) &&
        Objects.equals(this.configurable, resourceSpecificationCharacteristic.configurable) &&
        Objects.equals(this.description, resourceSpecificationCharacteristic.description) &&
        Objects.equals(this.extensible, resourceSpecificationCharacteristic.extensible) &&
        Objects.equals(this.isUnique, resourceSpecificationCharacteristic.isUnique) &&
        Objects.equals(this.maxCardinality, resourceSpecificationCharacteristic.maxCardinality) &&
        Objects.equals(this.minCardinality, resourceSpecificationCharacteristic.minCardinality) &&
        Objects.equals(this.name, resourceSpecificationCharacteristic.name) &&
        Objects.equals(this.regex, resourceSpecificationCharacteristic.regex) &&
        Objects.equals(this.valueType, resourceSpecificationCharacteristic.valueType) &&
        Objects.equals(this.resourceSpecCharRelationship, resourceSpecificationCharacteristic.resourceSpecCharRelationship) &&
        Objects.equals(this.resourceSpecCharacteristicValue, resourceSpecificationCharacteristic.resourceSpecCharacteristicValue) &&
        Objects.equals(this.validFor, resourceSpecificationCharacteristic.validFor) &&
        Objects.equals(this.atBaseType, resourceSpecificationCharacteristic.atBaseType) &&
        Objects.equals(this.atSchemaLocation, resourceSpecificationCharacteristic.atSchemaLocation) &&
        Objects.equals(this.atType, resourceSpecificationCharacteristic.atType) &&
        Objects.equals(this.atValueSchemaLocation, resourceSpecificationCharacteristic.atValueSchemaLocation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, configurable, description, extensible, isUnique, maxCardinality, minCardinality, name, regex, valueType, resourceSpecCharRelationship, resourceSpecCharacteristicValue, validFor, atBaseType, atSchemaLocation, atType, atValueSchemaLocation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceSpecificationCharacteristic {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    configurable: ").append(toIndentedString(configurable)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    extensible: ").append(toIndentedString(extensible)).append("\n");
    sb.append("    isUnique: ").append(toIndentedString(isUnique)).append("\n");
    sb.append("    maxCardinality: ").append(toIndentedString(maxCardinality)).append("\n");
    sb.append("    minCardinality: ").append(toIndentedString(minCardinality)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    regex: ").append(toIndentedString(regex)).append("\n");
    sb.append("    valueType: ").append(toIndentedString(valueType)).append("\n");
    sb.append("    resourceSpecCharRelationship: ").append(toIndentedString(resourceSpecCharRelationship)).append("\n");
    sb.append("    resourceSpecCharacteristicValue: ").append(toIndentedString(resourceSpecCharacteristicValue)).append("\n");
    sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
    sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
    sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
    sb.append("    atValueSchemaLocation: ").append(toIndentedString(atValueSchemaLocation)).append("\n");
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

