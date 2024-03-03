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
 * Configuration feature characteristic specification.
 */

@Schema(name = "FeatureSpecificationCharacteristic", description = "Configuration feature characteristic specification.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class FeatureSpecificationCharacteristic {

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
  private List<@Valid FeatureSpecificationCharacteristicRelationship> featureSpecCharRelationship;

  @Valid
  private List<@Valid CharacteristicValueSpecification> featureSpecCharacteristicValue;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  private String atValueSchemaLocation;

  /**
   * Default constructor
   * @deprecated Use {@link FeatureSpecificationCharacteristic#FeatureSpecificationCharacteristic(String)}
   */
  @Deprecated
  public FeatureSpecificationCharacteristic() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FeatureSpecificationCharacteristic(String name) {
    this.name = name;
  }

  public FeatureSpecificationCharacteristic id(String id) {
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

  public FeatureSpecificationCharacteristic configurable(Boolean configurable) {
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

  public FeatureSpecificationCharacteristic description(String description) {
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

  public FeatureSpecificationCharacteristic extensible(Boolean extensible) {
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

  public FeatureSpecificationCharacteristic isUnique(Boolean isUnique) {
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

  public FeatureSpecificationCharacteristic maxCardinality(Integer maxCardinality) {
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

  public FeatureSpecificationCharacteristic minCardinality(Integer minCardinality) {
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

  public FeatureSpecificationCharacteristic name(String name) {
    this.name = name;
    return this;
  }

  /**
   * A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.
   * @return name
  */
  @NotNull 
  @Schema(name = "name", description = "A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FeatureSpecificationCharacteristic regex(String regex) {
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

  public FeatureSpecificationCharacteristic valueType(String valueType) {
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

  public FeatureSpecificationCharacteristic featureSpecCharRelationship(List<@Valid FeatureSpecificationCharacteristicRelationship> featureSpecCharRelationship) {
    this.featureSpecCharRelationship = featureSpecCharRelationship;
    return this;
  }

  public FeatureSpecificationCharacteristic addFeatureSpecCharRelationshipItem(FeatureSpecificationCharacteristicRelationship featureSpecCharRelationshipItem) {
    if (this.featureSpecCharRelationship == null) {
      this.featureSpecCharRelationship = new ArrayList<>();
    }
    this.featureSpecCharRelationship.add(featureSpecCharRelationshipItem);
    return this;
  }

  /**
   * An aggregation, migration, substitution, dependency or exclusivity relationship between/among feature characteristics.
   * @return featureSpecCharRelationship
  */
  @Valid 
  @Schema(name = "featureSpecCharRelationship", description = "An aggregation, migration, substitution, dependency or exclusivity relationship between/among feature characteristics.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("featureSpecCharRelationship")
  public List<@Valid FeatureSpecificationCharacteristicRelationship> getFeatureSpecCharRelationship() {
    return featureSpecCharRelationship;
  }

  public void setFeatureSpecCharRelationship(List<@Valid FeatureSpecificationCharacteristicRelationship> featureSpecCharRelationship) {
    this.featureSpecCharRelationship = featureSpecCharRelationship;
  }

  public FeatureSpecificationCharacteristic featureSpecCharacteristicValue(List<@Valid CharacteristicValueSpecification> featureSpecCharacteristicValue) {
    this.featureSpecCharacteristicValue = featureSpecCharacteristicValue;
    return this;
  }

  public FeatureSpecificationCharacteristic addFeatureSpecCharacteristicValueItem(CharacteristicValueSpecification featureSpecCharacteristicValueItem) {
    if (this.featureSpecCharacteristicValue == null) {
      this.featureSpecCharacteristicValue = new ArrayList<>();
    }
    this.featureSpecCharacteristicValue.add(featureSpecCharacteristicValueItem);
    return this;
  }

  /**
   * Used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a FeatureCharacteristic object.
   * @return featureSpecCharacteristicValue
  */
  @Valid 
  @Schema(name = "featureSpecCharacteristicValue", description = "Used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a FeatureCharacteristic object.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("featureSpecCharacteristicValue")
  public List<@Valid CharacteristicValueSpecification> getFeatureSpecCharacteristicValue() {
    return featureSpecCharacteristicValue;
  }

  public void setFeatureSpecCharacteristicValue(List<@Valid CharacteristicValueSpecification> featureSpecCharacteristicValue) {
    this.featureSpecCharacteristicValue = featureSpecCharacteristicValue;
  }

  public FeatureSpecificationCharacteristic validFor(TimePeriod validFor) {
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

  public FeatureSpecificationCharacteristic atBaseType(String atBaseType) {
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

  public FeatureSpecificationCharacteristic atSchemaLocation(URI atSchemaLocation) {
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

  public FeatureSpecificationCharacteristic atType(String atType) {
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

  public FeatureSpecificationCharacteristic atValueSchemaLocation(String atValueSchemaLocation) {
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
    FeatureSpecificationCharacteristic featureSpecificationCharacteristic = (FeatureSpecificationCharacteristic) o;
    return Objects.equals(this.id, featureSpecificationCharacteristic.id) &&
        Objects.equals(this.configurable, featureSpecificationCharacteristic.configurable) &&
        Objects.equals(this.description, featureSpecificationCharacteristic.description) &&
        Objects.equals(this.extensible, featureSpecificationCharacteristic.extensible) &&
        Objects.equals(this.isUnique, featureSpecificationCharacteristic.isUnique) &&
        Objects.equals(this.maxCardinality, featureSpecificationCharacteristic.maxCardinality) &&
        Objects.equals(this.minCardinality, featureSpecificationCharacteristic.minCardinality) &&
        Objects.equals(this.name, featureSpecificationCharacteristic.name) &&
        Objects.equals(this.regex, featureSpecificationCharacteristic.regex) &&
        Objects.equals(this.valueType, featureSpecificationCharacteristic.valueType) &&
        Objects.equals(this.featureSpecCharRelationship, featureSpecificationCharacteristic.featureSpecCharRelationship) &&
        Objects.equals(this.featureSpecCharacteristicValue, featureSpecificationCharacteristic.featureSpecCharacteristicValue) &&
        Objects.equals(this.validFor, featureSpecificationCharacteristic.validFor) &&
        Objects.equals(this.atBaseType, featureSpecificationCharacteristic.atBaseType) &&
        Objects.equals(this.atSchemaLocation, featureSpecificationCharacteristic.atSchemaLocation) &&
        Objects.equals(this.atType, featureSpecificationCharacteristic.atType) &&
        Objects.equals(this.atValueSchemaLocation, featureSpecificationCharacteristic.atValueSchemaLocation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, configurable, description, extensible, isUnique, maxCardinality, minCardinality, name, regex, valueType, featureSpecCharRelationship, featureSpecCharacteristicValue, validFor, atBaseType, atSchemaLocation, atType, atValueSchemaLocation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureSpecificationCharacteristic {\n");
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
    sb.append("    featureSpecCharRelationship: ").append(toIndentedString(featureSpecCharRelationship)).append("\n");
    sb.append("    featureSpecCharacteristicValue: ").append(toIndentedString(featureSpecCharacteristicValue)).append("\n");
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

