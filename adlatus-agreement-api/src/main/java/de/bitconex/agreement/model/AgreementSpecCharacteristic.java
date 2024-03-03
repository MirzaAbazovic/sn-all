package de.bitconex.agreement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A characteristic quality or distinctive feature of an agreement.
 */
@ApiModel(description = "A characteristic quality or distinctive feature of an agreement.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")


public class AgreementSpecCharacteristic   {
  @JsonProperty("configurable")
  private Boolean configurable = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("valueType")
  private String valueType = null;

  @JsonProperty("specCharacteristicValue")
  @Valid
  private List<AgreementSpecCharacteristicValue> specCharacteristicValue = null;

  @JsonProperty("validFor")
  private TimePeriod validFor = null;

  @JsonProperty("@baseType")
  private String baseType = null;

  @JsonProperty("@schemaLocation")
  private String schemaLocation = null;

  @JsonProperty("@type")
  private String type = null;

  public AgreementSpecCharacteristic configurable(Boolean configurable) {
    this.configurable = configurable;
    return this;
  }

  /**
   * If true, the Boolean indicates that the characteristic is configurable
   * @return configurable
  **/
  @ApiModelProperty(value = "If true, the Boolean indicates that the characteristic is configurable")


  public Boolean isConfigurable() {
    return configurable;
  }

  public void setConfigurable(Boolean configurable) {
    this.configurable = configurable;
  }

  public AgreementSpecCharacteristic description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A narrative that explains in detail what the characteristic is
   * @return description
  **/
  @ApiModelProperty(value = "A narrative that explains in detail what the characteristic is")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AgreementSpecCharacteristic name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the characteristic being specified.
   * @return name
  **/
  @ApiModelProperty(value = "Name of the characteristic being specified.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AgreementSpecCharacteristic valueType(String valueType) {
    this.valueType = valueType;
    return this;
  }

  /**
   * A kind of value that the characteristic can take on, such as numeric, text and so forth
   * @return valueType
  **/
  @ApiModelProperty(value = "A kind of value that the characteristic can take on, such as numeric, text and so forth")


  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public AgreementSpecCharacteristic specCharacteristicValue(List<AgreementSpecCharacteristicValue> specCharacteristicValue) {
    this.specCharacteristicValue = specCharacteristicValue;
    return this;
  }

  public AgreementSpecCharacteristic addSpecCharacteristicValueItem(AgreementSpecCharacteristicValue specCharacteristicValueItem) {
    if (this.specCharacteristicValue == null) {
      this.specCharacteristicValue = new ArrayList<AgreementSpecCharacteristicValue>();
    }
    this.specCharacteristicValue.add(specCharacteristicValueItem);
    return this;
  }

  /**
   * Get specCharacteristicValue
   * @return specCharacteristicValue
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<AgreementSpecCharacteristicValue> getSpecCharacteristicValue() {
    return specCharacteristicValue;
  }

  public void setSpecCharacteristicValue(List<AgreementSpecCharacteristicValue> specCharacteristicValue) {
    this.specCharacteristicValue = specCharacteristicValue;
  }

  public AgreementSpecCharacteristic validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * The period for which the specification characteristic is valid
   * @return validFor
  **/
  @ApiModelProperty(value = "The period for which the specification characteristic is valid")

  @Valid

  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public AgreementSpecCharacteristic baseType(String baseType) {
    this.baseType = baseType;
    return this;
  }

  /**
   * When sub-classing, this defines the super-class
   * @return baseType
  **/
  @ApiModelProperty(value = "When sub-classing, this defines the super-class")


  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(String baseType) {
    this.baseType = baseType;
  }

  public AgreementSpecCharacteristic schemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
    return this;
  }

  /**
   * A URI to a JSON-Schema file that defines additional attributes and relationships
   * @return schemaLocation
  **/
  @ApiModelProperty(value = "A URI to a JSON-Schema file that defines additional attributes and relationships")


  public String getSchemaLocation() {
    return schemaLocation;
  }

  public void setSchemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
  }

  public AgreementSpecCharacteristic type(String type) {
    this.type = type;
    return this;
  }

  /**
   * When sub-classing, this defines the sub-class entity name
   * @return type
  **/
  @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AgreementSpecCharacteristic agreementSpecCharacteristic = (AgreementSpecCharacteristic) o;
    return Objects.equals(this.configurable, agreementSpecCharacteristic.configurable) &&
        Objects.equals(this.description, agreementSpecCharacteristic.description) &&
        Objects.equals(this.name, agreementSpecCharacteristic.name) &&
        Objects.equals(this.valueType, agreementSpecCharacteristic.valueType) &&
        Objects.equals(this.specCharacteristicValue, agreementSpecCharacteristic.specCharacteristicValue) &&
        Objects.equals(this.validFor, agreementSpecCharacteristic.validFor) &&
        Objects.equals(this.baseType, agreementSpecCharacteristic.baseType) &&
        Objects.equals(this.schemaLocation, agreementSpecCharacteristic.schemaLocation) &&
        Objects.equals(this.type, agreementSpecCharacteristic.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(configurable, description, name, valueType, specCharacteristicValue, validFor, baseType, schemaLocation, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AgreementSpecCharacteristic {\n");
    
    sb.append("    configurable: ").append(toIndentedString(configurable)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    valueType: ").append(toIndentedString(valueType)).append("\n");
    sb.append("    specCharacteristicValue: ").append(toIndentedString(specCharacteristicValue)).append("\n");
    sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
    sb.append("    baseType: ").append(toIndentedString(baseType)).append("\n");
    sb.append("    schemaLocation: ").append(toIndentedString(schemaLocation)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

